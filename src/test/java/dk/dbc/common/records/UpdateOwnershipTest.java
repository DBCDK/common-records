/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

public class UpdateOwnershipTest {

    private MarcRecord getCurrentRecordWithOwner(Boolean includeSelf) {
        MarcRecord record = new MarcRecord();

        MarcField field996 = new MarcField("996", "00");

        List<MarcSubField> subfields = new ArrayList<>();
        subfields.add(new MarcSubField("a", "789456"));
        subfields.add(new MarcSubField("o", "ORIGINAL"));
        subfields.add(new MarcSubField("m", "ABC"));

        if (includeSelf)
            subfields.add(new MarcSubField("m", "789456"));

        field996.setSubfields(subfields);
        record.getFields().add(field996);

        return record;
    }

    @Test
    public void testNullRecordBoth() {
        assertThat(UpdateOwnership.mergeRecord(null, null), nullValue());
    }

    @Test
    public void testNullRecord() {
        MarcRecord currentRecord = new MarcRecord();

        assertThat(UpdateOwnership.mergeRecord(null, currentRecord), nullValue());
    }

    @Test
    public void testNullCurrentRecord() {
        MarcRecord record = new MarcRecord();

        assertThat(UpdateOwnership.mergeRecord(record, null), equalTo(record));
    }

    @Test
    public void testEmptyRecord() {
        MarcRecord record = new MarcRecord();
        MarcRecord currentRecord = getCurrentRecordWithOwner(true);

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), equalTo(record));
    }

    @Test
    public void testMergeOwnersSameOwner() {
        MarcField owner = new MarcField("996", "00");
        owner.getSubfields().add(new MarcSubField("a", "789456"));

        MarcRecord record = new MarcRecord();
        record.getFields().add(owner);

        MarcRecord currentRecord = getCurrentRecordWithOwner(true);

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), equalTo(currentRecord));
    }

    @Test
    public void testMergeOwners() {
        MarcRecord record = getCurrentRecordWithOwner(true);
        MarcRecord currentRecord = getCurrentRecordWithOwner(true);

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), equalTo(currentRecord));
    }

    @Test
    public void testMergeOwners_DifferentOwners_PreviousOwnersIncludeCurrent() {
        MarcField owner = new MarcField("996", "00");
        owner.getSubfields().add(new MarcSubField("a", "777777"));
        MarcRecord record = new MarcRecord();
        record.getFields().add(owner);

        MarcRecord currentRecord = getCurrentRecordWithOwner(true);

        MarcRecord expected = getCurrentRecordWithOwner(true);
        MarcRecordWriter expectedWriter = new MarcRecordWriter(expected);
        expectedWriter.addOrReplaceSubfield("996", "a", "777777");

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), equalTo(expected));
    }

    @Test
    public void testMergeOwners_DifferentOwners_PreviousOwnersDontIncludeCurrent() {
        MarcField owner = new MarcField("996", "00");
        owner.getSubfields().add(new MarcSubField("a", "777777"));
        MarcRecord record = new MarcRecord();
        record.getFields().add(owner);

        MarcRecord currentRecord = getCurrentRecordWithOwner(false);

        MarcRecord expected = getCurrentRecordWithOwner(true);
        MarcRecordWriter expectedWriter = new MarcRecordWriter(expected);
        expectedWriter.addOrReplaceSubfield("996", "a", "777777");

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), equalTo(expected));
    }

    @Test
    public void testMergeOwners_RETToDBC() {
        MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "RET"))));

        MarcRecord record = new MarcRecord();
        record.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "DBC"))));

        MarcRecord expected = new MarcRecord();
        expected.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "DBC"))));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), equalTo(expected));
    }

    @Test
    public void testMergeOwners_DBCToRET() {
        MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "DBC"))));

        MarcRecord record = new MarcRecord();
        record.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "RET"))));

        MarcRecord expected = new MarcRecord();
        expected.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "RET"))));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), equalTo(expected));
    }

    @Test
    public void testMergeOwners_NewNon7xOwner() {
        MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "888888"))));

        MarcRecord record = new MarcRecord();
        record.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "777777"))));

        MarcRecord expected = new MarcRecord();
        expected.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "777777"))));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), equalTo(expected));
    }

    @Test
    public void testMergeOwners_RETTo7xOwner() {
        MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "RET"))));

        MarcRecord record = new MarcRecord();
        record.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "777777"))));

        MarcRecord expected = new MarcRecord();
        expected.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "777777"))));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), equalTo(expected));
    }

    @Test
    public void testMergeOwners_7xToDBC() {
        MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "710100"))));

        MarcRecord record = new MarcRecord();
        record.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "DBC"))));

        MarcRecord expected = new MarcRecord();
        expected.getFields().add(new MarcField("996", "00", Arrays.asList(new MarcSubField("a", "DBC"),
                new MarcSubField("o", "710100"),
                new MarcSubField("m", "710100"))));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), equalTo(expected));
    }

}
