package dk.dbc.common.records;

import dk.dbc.marc.binding.DataField;
import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.binding.SubField;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

class MarcRecordReaderTest {

    @Test
    void testGetValue() {
        MarcRecord record = new MarcRecord();

        DataField f1 = new DataField("245", "00")
                .addSubField(new SubField('a', "v1"))
                .addSubField(new SubField('x', "x1_1"))
                .addSubField(new SubField('x', "x1_2"));
        record.getFields().add(f1);

        DataField f2 = new DataField("245", "00")
                .addSubField(new SubField('a', "v2"))
                .addSubField(new SubField('x', "x2_1"))
                .addSubField(new SubField('x', "x2_2"))
                .addSubField(new SubField('z', "z2_1"));
        record.getFields().add(f2);

        MarcRecordReader instance = new MarcRecordReader(record);

        assertThat(instance.getValue("001", 'z'), nullValue());
        assertThat(instance.getValue("245", 'z'), is("z2_1"));
        assertThat(instance.getValue("245", 'x'), is("x1_1"));
    }

    @Test
    void testGetValues() {
        MarcRecord record = new MarcRecord();

        DataField f1 = new DataField("245", "00");
        f1.getSubFields().add(new SubField('a', "v1"));
        f1.getSubFields().add(new SubField('x', "x1_1"));
        f1.getSubFields().add(new SubField('x', "x1_2"));
        record.getFields().add(f1);

        DataField f2 = new DataField("245", "00");
        f2.getSubFields().add(new SubField('a', "v2"));
        f2.getSubFields().add(new SubField('x', "x2_1"));
        f2.getSubFields().add(new SubField('x', "x2_2"));
        record.getFields().add(f2);

        List<String> expected;
        MarcRecordReader instance = new MarcRecordReader(record);

        expected = new ArrayList<>();
        assertThat(instance.getValues("001", 'z'), equalTo(expected));
        assertThat(instance.getValues("245", 'z'), equalTo(expected));

        expected = Arrays.asList("x1_1", "x1_2", "x2_1", "x2_2");
        assertThat(instance.getValues("245", 'x'), equalTo(expected));
    }

    @Test
    void testHasField() {
        MarcRecord record = new MarcRecord();

        DataField f1 = new DataField("245", "00");
        f1.getSubFields().add(new SubField('a', "v1"));
        record.getFields().add(f1);

        MarcRecordReader instance = new MarcRecordReader(record);
        assertThat(instance.hasField("245"), is(true));
        assertThat(instance.hasField("110"), is(false));
    }

    @Test
    void testHasValue() {
        MarcRecord record = new MarcRecord();

        DataField f1 = new DataField("245", "00");
        f1.getSubFields().add(new SubField('a', "v1"));
        f1.getSubFields().add(new SubField('x', "x1_1"));
        f1.getSubFields().add(new SubField('x', "x1_2"));
        record.getFields().add(f1);

        DataField f2 = new DataField("245", "00");
        f2.getSubFields().add(new SubField('a', "v2"));
        f2.getSubFields().add(new SubField('x', "x2_1"));
        f2.getSubFields().add(new SubField('x', "x2_2"));
        record.getFields().add(f2);

        MarcRecordReader instance = new MarcRecordReader(record);
        //DataFieldReader firstFieldReader = new DataFieldReader(f1);

        assertThat(instance.hasValue("001", 'z', "xxx"), is(false));
        assertThat(instance.hasValue("245", 'z', "xxx"), is(false));
        assertThat(instance.hasValue("245", 'x', "x1_1"), is(true));
        assertThat(instance.hasValue("245", 'x', "x1_2"), is(true));
    }

    @Test
    void testRecordId() {
        MarcRecord record = new MarcRecord();

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecordReader instance = new MarcRecordReader(record);

        writer.addOrReplaceSubField("001", 'b', "xxx");
        assertThat(instance.getRecordId(), nullValue());
        writer.addOrReplaceSubField("001", 'a', "xxx");
        assertThat(instance.getRecordId(), is("xxx"));
    }

    @Test
    void testCentralAliasId() {
        MarcRecord record = new MarcRecord();

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecordReader instance = new MarcRecordReader(record);

        writer.addFieldSubfield("002", 'b', "yyy");
        assertThat(instance.getCentralAliasIds().isEmpty(), is(true));
        writer.addFieldSubfield("002", 'a', "xxx");
        writer.addFieldSubfield("002", 'a', "zzz");
        assertThat(instance.getCentralAliasIds(), equalTo(Arrays.asList("xxx", "zzz")));
    }

    @Test
    void testDecentralAliasIdEmpty() {
        MarcRecord record = new MarcRecord();

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecordReader instance = new MarcRecordReader(record);

        writer.addFieldSubfield("002", 'b', "yyy");
        assertThat(instance.getDecentralAliasIds().isEmpty(), is(true));

        writer.addFieldSubfield("002", 'c', "xxx");
        assertThat(instance.getDecentralAliasIds().isEmpty(), is(true));
    }

    @Test
    void testDecentralAliasIdFound() {
        MarcRecord record = new MarcRecord();

        MarcRecordReader instance = new MarcRecordReader(record);

        DataField field = new DataField("002", "00");
        field.getSubFields().add(new SubField('b', "yyy"));
        field.getSubFields().add(new SubField('c', "xxx"));
        record.getFields().add(field);

        HashMap<Character, String> expectedHash = new HashMap<>();
        expectedHash.put('b', "yyy");
        expectedHash.put('c', "xxx");
        List<HashMap<Character, String>> expectedList = new ArrayList<>();
        expectedList.add(expectedHash);

        assertThat(instance.getDecentralAliasIds(), is(expectedList));
    }

    @Test
    void testIsDBCRecord() {
        MarcRecord record = new MarcRecord();

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecordReader instance = new MarcRecordReader(record);

        // NOTE: s10*b must be at start since s10*a is added or replaced
        writer.addOrReplaceSubField("996", 'b', "xxx");
        assertThat(instance.isDBCRecord(), is(false));
        writer.addOrReplaceSubField("996", 'a', "810010");
        assertThat(instance.isDBCRecord(), is(false));
        writer.addOrReplaceSubField("996", 'a', "DBC");
        assertThat(instance.isDBCRecord(), is(true));
        writer.addOrReplaceSubField("996", 'a', "RET");
        assertThat(instance.isDBCRecord(), is(true));
    }

    @Test
    void testAgencyId() {
        MarcRecord record = new MarcRecord();

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecordReader instance = new MarcRecordReader(record);

        writer.addOrReplaceSubField("001", 'a', "xxx");
        assertThat(instance.getAgencyId(), nullValue());
        writer.addOrReplaceSubField("001", 'b', "xxx");
        assertThat(instance.getAgencyId(), is("xxx"));
        writer.addOrReplaceSubField("001", 'b', "127");
        assertThat(instance.getAgencyIdAsInt(), is(127));
    }

    @Test
    void testMarkedForDeletion() {
        MarcRecord record = new MarcRecord();

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecordReader instance = new MarcRecordReader(record);

        assertThat(instance.markedForDeletion(), is(false));

        writer.addOrReplaceSubField("004", 'r', "d");
        assertThat(instance.markedForDeletion(), is(true));

        writer.addOrReplaceSubField("004", 'r', "q");
        assertThat(instance.markedForDeletion(), is(false));
    }

    @Test
    void testParentId() {
        MarcRecord record = new MarcRecord();

        MarcRecordWriter writer = new MarcRecordWriter(record);
        MarcRecordReader instance = new MarcRecordReader(record);

        writer.addOrReplaceSubField("014", 'a', "xxx");
        assertThat(instance.getParentRecordId(), equalTo(instance.getValue("014", 'a')));
    }

    @Test
    void testGetField() {
        DataField commentField1 = new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('s', "Julemand")));
        DataField commentField2 = new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('s', "Grønland")));

        MarcRecord record = new MarcRecord();
        record.getFields().add(commentField1);
        record.getFields().add(commentField2);

        MarcRecordReader instance = new MarcRecordReader(record);

        assertThat(instance.getField("666"), equalTo(commentField1));
    }

    @Test
    void testGetFieldAll() {
        DataField commentField1 = new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('s', "Julemand")));
        DataField commentField2 = new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('s', "Grønland")));

        MarcRecord record = new MarcRecord();
        record.getFields().add(commentField1);
        record.getFields().add(commentField2);

        MarcRecordReader instance = new MarcRecordReader(record);

        assertThat(instance.getFieldAll("666"), equalTo(Arrays.asList(commentField1, commentField2)));
    }

    @Test
    void testMatchValueTrueSingle() {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870970"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('a', "et emneord"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('c', "Julemand"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('d', ""))));

        MarcRecordReader instance = new MarcRecordReader(record);

        assertThat(instance.matchValue("666", 'c', "(Julemand)"), equalTo(true));
    }

    @Test
    void testMatchValueFalseHasSubfield() {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870970"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('c', "et emneord"))));

        MarcRecordReader instance = new MarcRecordReader(record);

        assertThat(instance.matchValue("666", 'c', "(Julemand)"), equalTo(false));
    }

    @Test
    void testMatchValueFalseDoesntHaveSubfield() {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870970"))));

        MarcRecordReader instance = new MarcRecordReader(record);

        assertThat(instance.matchValue("666", 'c', "(Julemand)"), equalTo(false));
    }

    @Test
    void testMatchValueTrueMultiField() {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870970"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('c', "Julemand"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('c', "Julemand"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('d', ""))));

        MarcRecordReader instance = new MarcRecordReader(record);

        assertThat(instance.matchValue("666", 'c', "(Julemand)"), equalTo(true));
    }

    @Test
    void testMatchValueTrueMultiSubfield() {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870970"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('c', "Julemand"), new SubField('c', "Julemand"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('0', ""), new SubField('d', ""))));

        MarcRecordReader instance = new MarcRecordReader(record);

        assertThat(instance.matchValue("666", 'c', "(Julemand)"), equalTo(true));
    }


    @Test
    void testGetParentAgencyId_1() {
        MarcRecord record = new MarcRecord();

        MarcRecordReader instance = new MarcRecordReader(record);

        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870971"))));
        record.getFields().add(new DataField("014", "00").addAllSubFields(Arrays.asList(new SubField('a', "87654321"), new SubField('x', "Julemand"))));
        assertThat(instance.getParentAgencyId(), is("870971"));
    }

    @Test
    void testGetParentAgencyId_2() {
        MarcRecord record = new MarcRecord();

        MarcRecordReader instance = new MarcRecordReader(record);

        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870971"))));
        record.getFields().add(new DataField("014", "00").addAllSubFields(Arrays.asList(new SubField('A', "87654321"), new SubField('a', "87654321"))));
        assertThat(instance.getParentAgencyId(), is("870971"));
        assertThat(instance.getParentRecordId(), is("87654321"));
    }

    @Test
    void testGetParentAgencyId_3() {
        MarcRecord record = new MarcRecord();

        MarcRecordReader instance = new MarcRecordReader(record);

        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870971"))));
        record.getFields().add(new DataField("014", "00").addAllSubFields(Arrays.asList(new SubField('a', "87654321"), new SubField('x', "ANM"))));
        assertThat(instance.getParentAgencyId(), is("870970"));
        assertThat(instance.getParentRecordId(), is("87654321"));
    }

    @Test
    void testGetParentAgencyId_DEB() {
        MarcRecord record = new MarcRecord();

        MarcRecordReader instance = new MarcRecordReader(record);

        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870971"))));
        record.getFields().add(new DataField("014", "00").addAllSubFields(Arrays.asList(new SubField('a', "87654321"), new SubField('x', "DEB"))));
        assertThat(instance.getParentAgencyId(), is("870971"));
        assertThat(instance.getParentRecordId(), is("87654321"));
    }

    @Test
    void testGetParentAgencyId_016_WithAgency() {
        MarcRecord record = new MarcRecord();

        MarcRecordReader instance = new MarcRecordReader(record);

        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870974"))));
        record.getFields().add(new DataField("016", "00").addAllSubFields(Arrays.asList(new SubField('a', "87654321"), new SubField('5', "123456"))));
        assertThat(instance.getParentAgencyId(), is("123456"));
        assertThat(instance.getParentRecordId(), is("87654321"));
    }

    @Test
    void testGetParentAgencyId_018_WithAgency() {
        MarcRecord record = new MarcRecord();

        MarcRecordReader instance = new MarcRecordReader(record);

        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870974"))));
        record.getFields().add(new DataField("018", "00").addAllSubFields(Arrays.asList(new SubField('a', "87654321"), new SubField('5', "123456"))));
        assertThat(instance.getParentAgencyId(), is("123456"));
        assertThat(instance.getParentRecordId(), is("87654321"));
    }

    @Test
    void testGetParentAgencyId_VP_016_WithAgency() {
        MarcRecord record = new MarcRecord();

        MarcRecordReader instance = new MarcRecordReader(record);

        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870975"))));
        record.getFields().add(new DataField("016", "00").addAllSubFields(Arrays.asList(new SubField('a', "87654321"), new SubField('5', "123456"))));
        assertThat(instance.getParentAgencyId(), is("123456"));
        assertThat(instance.getParentRecordId(), is("87654321"));
    }

    @Test
    void testGetParentAgencyId_VP_016_WithoutAgency() {
        MarcRecord record = new MarcRecord();

        MarcRecordReader instance = new MarcRecordReader(record);

        record.getFields().add(new DataField("001", "00").addAllSubFields(Arrays.asList(new SubField('a', "12345678"), new SubField('b', "870975"))));
        record.getFields().add(new DataField("016", "00").addAllSubFields(List.of(new SubField('a', "87654321"))));
        assertThat(instance.getParentAgencyId(), is("870975"));
        assertThat(instance.getParentRecordId(), is("87654321"));
    }

    @Test
    void testGetSubfieldValueMatchers_MultipleMatchesInSameField() {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("666", "00").addAllSubFields(Arrays.asList(new SubField('u', "For 3-4 år"), new SubField('u', "For 5-8 år"))));

        MarcRecordReader instance = new MarcRecordReader(record);

        String pattern = "^(For|for) ([0-9]+)-([0-9]+) (år)";
        Pattern p = Pattern.compile(pattern);
        List<Matcher> matchers = instance.getSubfieldValueMatchers("666", 'u', p);
        assertThat(matchers.size(), is(2));
    }

    @Test
    void testGetSubfieldValueMatchers_MultipleMatchesInMultipleFields() {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("666", "00").addAllSubFields(List.of(new SubField('u', "For 3-4 år"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(List.of(new SubField('u', "For 5-8 år"))));

        MarcRecordReader instance = new MarcRecordReader(record);

        String pattern = "^(For|for) ([0-9]+)-([0-9]+) (år)";
        Pattern p = Pattern.compile(pattern);
        List<Matcher> matchers = instance.getSubfieldValueMatchers("666", 'u', p);
        assertThat(matchers.size(), is(2));
    }

    @Test
    void testGetSubfieldValueMatchers_SingleMatchInMultipleFields() {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("666", "00").addAllSubFields(List.of(new SubField('u', "For 3-4 år"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(List.of(new SubField('u', "For 3 år"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(List.of(new SubField('u', "For 4 år"))));

        MarcRecordReader instance = new MarcRecordReader(record);

        String pattern = "^(For|for) ([0-9]+)-([0-9]+) (år)";
        Pattern p = Pattern.compile(pattern);
        List<Matcher> matchers = instance.getSubfieldValueMatchers("666", 'u', p);
        assertThat(matchers.size(), is(1));
    }

    @Test
    void testGetSubfieldValueMatchers_NoMatch() {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("666", "00").addAllSubFields(List.of(new SubField('u', "For 3 år"))));
        record.getFields().add(new DataField("666", "00").addAllSubFields(List.of(new SubField('u', "For 4 år"))));

        MarcRecordReader instance = new MarcRecordReader(record);

        String pattern = "^(For|for) ([0-9]+)-([0-9]+) (år)";
        Pattern p = Pattern.compile(pattern);
        List<Matcher> matchers = instance.getSubfieldValueMatchers("666", 'u', p);
        assertThat(matchers.size(), is(0));
    }
}
