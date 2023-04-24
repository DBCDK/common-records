package dk.dbc.common.records;

import dk.dbc.marc.binding.DataField;
import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.binding.SubField;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class UpdateOwnershipTest {

    private MarcRecord getCurrentRecordWithOwner(boolean includeSelf) {
        final MarcRecord record = new MarcRecord();

        final DataField field996 = new DataField("996", "00");

        final List<SubField> subfields = new ArrayList<>();
        subfields.add(new SubField('a', "789456"));
        subfields.add(new SubField('o', "ORIGINAL"));
        subfields.add(new SubField('m', "ABC"));

        if (includeSelf) {
            subfields.add(new SubField('m', "789456"));
        }

        field996.getSubFields().clear();
        field996.getSubFields().addAll(subfields);
        record.getFields().add(field996);

        return record;
    }

    @Test
    void testNullRecordBoth() {
        assertThat(UpdateOwnership.mergeRecord(null, null), nullValue());
    }

    @Test
    void testNullRecord() {
        final MarcRecord currentRecord = new MarcRecord();

        assertThat(UpdateOwnership.mergeRecord(null, currentRecord), nullValue());
    }

    @Test
    void testNullCurrentRecord() {
        final MarcRecord record = new MarcRecord();

        assertThat(UpdateOwnership.mergeRecord(record, null), is(record));
    }

    @Test
    void testEmptyRecord() {
        final MarcRecord record = new MarcRecord();
        final MarcRecord currentRecord = getCurrentRecordWithOwner(true);

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(record));
    }

    @Test
    void testMergeOwnersSameOwner() {
        final DataField owner = new DataField("996", "00")
                .addSubField(new SubField('a', "789456"));
        final MarcRecord record = new MarcRecord();
        record.getFields().add(owner);

        final MarcRecord currentRecord = getCurrentRecordWithOwner(true);

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(currentRecord));
    }

    @Test
    void testMergeOwners() {
        final MarcRecord record = getCurrentRecordWithOwner(true);
        final MarcRecord currentRecord = getCurrentRecordWithOwner(true);

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(currentRecord));
    }

    @Test
    void testMergeOwners_DifferentOwners_PreviousOwnersIncludeCurrent() {
        final DataField owner = new DataField("996", "00");
        owner.getSubFields().add(new SubField('a', "777777"));
        final MarcRecord record = new MarcRecord();
        record.getFields().add(owner);

        final MarcRecord currentRecord = getCurrentRecordWithOwner(true);

        final MarcRecord expected = getCurrentRecordWithOwner(true);
        final DataField expectedField996 = (DataField) expected.getField(MarcRecord.hasTag("996")).get();
        expectedField996.addOrReplaceFirstSubField(new SubField('a', "777777"));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(expected));
    }

    @Test
    void testMergeOwners_DifferentOwners_PreviousOwnersDontIncludeCurrent() {
        final DataField owner = new DataField("996", "00");
        owner.getSubFields().add(new SubField('a', "777777"));
        final MarcRecord record = new MarcRecord();
        record.getFields().add(owner);

        final MarcRecord currentRecord = getCurrentRecordWithOwner(false);

        final MarcRecord expected = getCurrentRecordWithOwner(true);
        final DataField expectedField996 = expected.getFields(DataField.class, MarcRecord.hasTag("996")).get(0);
        expectedField996.addOrReplaceFirstSubField(new SubField('a', "777777"));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(expected));
    }

    @Test
    void testMergeOwners_RETToDBC() {
        final MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "RET")));

        final MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "DBC")));

        final MarcRecord expected = new MarcRecord();
        expected.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "DBC")));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(expected));
    }

    @Test
    void testMergeOwners_DBCToRET() {
        final MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "DBC")));

        final MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "RET")));

        final MarcRecord expected = new MarcRecord();
        expected.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "RET")));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(expected));
    }

    @Test
    void testMergeOwners_NewNon7xOwner() {
        final MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "888888")));

        final MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "777777")));

        final MarcRecord expected = new MarcRecord();
        expected.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "777777")));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(expected));
    }

    @Test
    void testMergeOwners_RETTo7xOwner() {
        final MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "RET")));

        final MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "777777")));

        final MarcRecord expected = new MarcRecord();
        expected.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "777777")));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(expected));
    }

    @Test
    void testMergeOwners_7xToDBC() {
        final MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "710100")));

        final MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "DBC")));

        final MarcRecord expected = new MarcRecord();
        expected.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "DBC"))
                .addSubField(new SubField('o', "710100")));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(expected));
    }

    @Test
    void testMergeOwners_7xPreviousOwner() {
        final MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "710100"))
                .addSubField(new SubField('o', "720200")));

        final MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "730300")));

        final MarcRecord expected = new MarcRecord();
        expected.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "730300"))
                .addSubField(new SubField('o', "720200"))
                .addSubField(new SubField('m', "710100")));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(expected));
    }

    @Test
    void testMergeOwners_7xPreviousOwners() {
        final MarcRecord currentRecord = new MarcRecord();
        currentRecord.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "710100"))
                .addSubField(new SubField('o', "720200"))
                .addSubField(new SubField('m', "740400")));

        final MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "730300")));

        final MarcRecord expected = new MarcRecord();
        expected.getFields().add(new DataField("996", "00")
                .addSubField(new SubField('a', "730300"))
                .addSubField(new SubField('o', "720200"))
                .addSubField(new SubField('m', "740400"))
                .addSubField(new SubField('m', "710100")));

        assertThat(UpdateOwnership.mergeRecord(record, currentRecord), is(expected));
    }

}
