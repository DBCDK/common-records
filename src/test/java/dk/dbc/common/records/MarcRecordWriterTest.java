package dk.dbc.common.records;

import dk.dbc.marc.binding.DataField;
import dk.dbc.marc.binding.MarcRecord;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

class MarcRecordWriterTest {

    private MarcRecord getBasicRecord() {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("001", "00"));
        record.getFields().add(new DataField("004", "00"));

        return record;
    }

    @Test
    void testSetChangedTimeStamp() {
        MarcRecord record = getBasicRecord();
        MarcRecordWriter writer = new MarcRecordWriter(record);
        writer.setChangedTimestamp();
        MarcRecordReader reader = new MarcRecordReader(record);
        String datestr = reader.getValue("001", 'c');
        assertThat(datestr.length(), is(14));
        assertThat(datestr.matches("[0-9]*"), is(true));
    }

    @Test
    void testSetCreationTimeStamp() {
        MarcRecord record = getBasicRecord();
        MarcRecordWriter writer = new MarcRecordWriter(record);
        writer.setCreationTimestamp();
        MarcRecordReader reader = new MarcRecordReader(record);
        String datestr = reader.getValue("001", 'd');
        assertThat(datestr.length(), is(8));
        assertThat(datestr.matches("[0-9]*"), is(true));
    }

    @Test
    void testRemoveSubfield() {
        MarcRecord record = getBasicRecord();
        MarcRecordWriter writer = new MarcRecordWriter(record);
        writer.addOrReplaceSubField("004", 'b', "xxx");
        MarcRecord record2 = new MarcRecord(record);
        writer.addOrReplaceSubField("004", 'a', "xxx");

        writer.removeSubfield("004", 'a');
        assertThat(record, equalTo(record2));
    }

    @Test
    void testCopyFieldFromRecord() {
        MarcRecord record = getBasicRecord();
        MarcRecord result = getBasicRecord();
        record.getFields().add(new DataField("666", "00"));

        MarcRecordWriter writer = new MarcRecordWriter(result);
        writer.copyFieldFromRecord("666", record);
        assertThat(record, equalTo(result));
    }

    @Test
    void testCopyFieldsFromRecord() {
        MarcRecord record = getBasicRecord();
        MarcRecord result = getBasicRecord();
        record.getFields().add(new DataField("666", "00"));
        record.getFields().add(new DataField("777", "00"));
        record.getFields().add(new DataField("888", "00"));

        MarcRecordWriter writer = new MarcRecordWriter(result);
        writer.copyFieldsFromRecord(Arrays.asList("666", "777", "888"), record);
        assertThat(record, equalTo(result));
    }

    @Test
    void testRemoveFieldsFromRecord() {
        MarcRecord record = getBasicRecord();
        record.getFields().add(new DataField("666", "00"));
        record.getFields().add(new DataField("777", "00"));
        record.getFields().add(new DataField("888", "00"));

        MarcRecordWriter writer = new MarcRecordWriter(record);
        writer.addFieldSubfield("888", 'b', "text");
        MarcRecord expected = getBasicRecord();

        List<String> removers = Arrays.asList("666", "777", "888");
        writer.removeFields(removers);

        assertThat(record, equalTo(expected));

    }

    @Test
    void testAddOrReplaceSubfield() {
        MarcRecord record = new MarcRecord();

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecordReader reader = new MarcRecordReader(record);

        writer.addOrReplaceSubField("001", 'a', "xxx");
        assertThat(reader.getValues("001", 'a'), equalTo(Collections.singletonList("xxx")));

        writer.addOrReplaceSubField("001", 'b', "yy");
        assertThat(reader.getValues("001", 'b'), equalTo(Collections.singletonList("yy")));

        writer.addOrReplaceSubField("001", 'a', "zzz");
        assertThat(reader.getValues("001", 'a'), equalTo(Collections.singletonList("zzz")));
    }

    @Test
    void testMarkForDeletion() {
        MarcRecord record = new MarcRecord();

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecordReader reader = new MarcRecordReader(record);

        writer.markForDeletion();
        assertThat(reader.markedForDeletion(), equalTo(true));
    }

    @Test
    void testRemoveFieldNone() throws Exception {
        MarcRecord record = getBasicRecord();

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecord expected = getBasicRecord();

        writer.removeField("666");

        assertThat(record, equalTo(expected));
    }

    @Test
    void testRemoveFieldOne() throws Exception {
        MarcRecord record = getBasicRecord();
        record.getFields().add(new DataField("666", "00"));

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecord expected = getBasicRecord();

        writer.removeField("666");

        assertThat(record, equalTo(expected));
    }

    @Test
    void testRemoveFieldTwo() throws Exception {
        MarcRecord record = getBasicRecord();
        record.getFields().add(new DataField("666", "00"));
        record.getFields().add(new DataField("666", "00"));

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecord expected = getBasicRecord();

        writer.removeField("666");

        assertThat(record, equalTo(expected));
    }
}
