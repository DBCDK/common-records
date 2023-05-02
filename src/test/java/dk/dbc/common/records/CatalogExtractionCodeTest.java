package dk.dbc.common.records;

import dk.dbc.marc.binding.DataField;
import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.binding.SubField;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CatalogExtractionCodeTest {

    @Test
    void testhasLastProductionDate() {
        assertThat(CatalogExtractionCode.hasPublishingDate(""), is(false));
        assertThat(CatalogExtractionCode.hasPublishingDate("XXX20161"), is(false));
        assertThat(CatalogExtractionCode.hasPublishingDate("XXX2016001"), is(false));
        assertThat(CatalogExtractionCode.hasPublishingDate("DBIXXXXXX"), is(false));

        assertThat(CatalogExtractionCode.hasPublishingDate("DBI201652"), is(true));
        assertThat(CatalogExtractionCode.hasPublishingDate("DBI999999"), is(true));
    }

    @Test
    void testhasFutureLastProductionDate() {
        assertThat(CatalogExtractionCode.hasFuturePublishingDate("DBI999999"), is(true));
        assertThat(CatalogExtractionCode.hasFuturePublishingDate("DBI201502"), is(false));
        assertThat(CatalogExtractionCode.hasFuturePublishingDate("DBI211602"), is(true));
    }

    @Test
    void testIsRecordInProductionEmptyRecord() {
        MarcRecord record = new MarcRecord();

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(false));
    }

    @Test
    void testIsRecordInProductionNo032() {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new DataField("004", "00"));

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(false));
    }

    private MarcRecord generateMarcRecordWith032(char subfieldName, String value) {
        SubField subfield = new SubField(subfieldName, value);

        DataField field = new DataField("032", "00");
        field.getSubFields().add(subfield);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        return record;
    }

    @Test
    void testIsRecordInProductionNo032ax() {
        MarcRecord record = generateMarcRecordWith032('k', "xxx");

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(false));
    }

    @Test
    void testIsRecordInProduction032aWrongFormat() {
        MarcRecord record = generateMarcRecordWith032('a', "xxx");

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(false));
    }

    @Test
    void testIsRecordInProduction032aWrongProductionCode() {
        MarcRecord record = generateMarcRecordWith032('a', "XXX201504");

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(false));
    }

    @Test
    void testIsRecordInProduction032aityDateIsBeforeCurrentDate() {
        MarcRecord record = generateMarcRecordWith032('a', "DBI201504");

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(false));
    }

    @Test
    void testIsRecordInProduction032aityDateIsBeforeCurrentDateCustomCatalogCodes() {
        MarcRecord record = generateMarcRecordWith032('a', "DBF201504");

        assertThat(CatalogExtractionCode.isUnderProduction(record,
                        Arrays.asList("DLF", "DBI", "DMF", "DMO", "DPF", "BKM", "GBF", "GMO", "GPF", "FPF", "DBR", "UTI")),
                is(false));
    }

    @Test
    void testIsRecordInProduction032aityDateIsAfterCurrentDate() {
        MarcRecord record = generateMarcRecordWith032('a', "DBI211604");

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(true));
    }

    @Test
    void testIsRecordInProduction032xWrongFormat() {
        MarcRecord record = generateMarcRecordWith032('x', "xxx");

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(false));
    }

    @Test
    void testIsRecordInProduction032xWrongProductionCode() {
        MarcRecord record = generateMarcRecordWith032('x', "XXX201504");

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(false));
    }

    @Test
    void testIsRecordInProduction032xityDateIsBeforeCurrentDate() {
        MarcRecord record = generateMarcRecordWith032('x', "DBI201504");

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(false));
    }

    @Test
    void testIsRecordInProduction032xityDateIsAfterCurrentDate() {
        MarcRecord record = generateMarcRecordWith032('x', "DBI211604");

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(true));
    }

    @Test
    void testIsRecordInProduction032xTemporaryDateOnly() {
        MarcRecord record = generateMarcRecordWith032('x', "DBI999999");

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(true));
    }

    @Test
    void testIsRecordInProduction032aTemporaryDateOnly() {
        MarcRecord record = generateMarcRecordWith032('a', "DBI999999");

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(true));
    }

    @Test
    void testIsRecordInProduction032aityDateIsBeforeCurrentDateAnd032xityDateIsAfterCurrentDate() {
        SubField subfieldA = new SubField('a', "DBI191304");
        SubField subfieldX = new SubField('x', "DBI211604");

        DataField field = new DataField("032", "00");
        field.getSubFields().add(subfieldA);
        field.getSubFields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(false));
    }

    @Test
    void testIsRecordInProduction032aTemporaryDateAnd032xityDateIsAfterCurrentDate() {
        SubField subfieldA = new SubField('a', "DBI999999");
        SubField subfieldX = new SubField('x', "DBI211604");

        DataField field = new DataField("032", "00");
        field.getSubFields().add(subfieldA);
        field.getSubFields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(true));
    }

    @Test
    void testIsRecordInProduction032aityDateIsBeforeCurrentDateAnd032xTemporaryDate() {
        SubField subfieldA = new SubField('a', "DBI191304");
        SubField subfieldX = new SubField('x', "DBI999999");

        DataField field = new DataField("032", "00");
        field.getSubFields().add(subfieldA);
        field.getSubFields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isUnderProduction(record), is(false));
    }

    @Test
    void testIsPublishedNoField032() {
        MarcRecord record = new MarcRecord();

        assertThat(CatalogExtractionCode.isPublished(record), is(false));
    }


    @Test
    void testIsPublishedHasProductionCodeInThePast() {
        SubField subfieldA = new SubField('a', "DBI191304");
        SubField subfieldX = new SubField('x', "DBI999999");

        DataField field = new DataField("032", "00");
        field.getSubFields().add(subfieldA);
        field.getSubFields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isPublished(record), is(true));
    }

    @Test
    void testIsPublishedHasProductionCodeInThePastCustomCatalogueCodes() {
        SubField subfieldA = new SubField('a', "DBI191304");
        SubField subfieldX = new SubField('x', "DBI999999");

        DataField field = new DataField("032", "00");
        field.getSubFields().add(subfieldA);
        field.getSubFields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isPublished(record, Arrays.asList("DBF", "DLF", "DMF", "DMO", "DPF", "BKM", "GBF", "GMO", "GPF", "FPF", "DBR", "UTI")), is(false));
    }

    @Test
    void verifySubfieldAndContent() {

        assertThat(CatalogExtractionCode.verifySubfieldAndContent('&', "text"), is(false));
        assertThat(CatalogExtractionCode.verifySubfieldAndContent('a', "short"), is(false));
        assertThat(CatalogExtractionCode.verifySubfieldAndContent('a', "0123456789"), is(false));
        assertThat(CatalogExtractionCode.verifySubfieldAndContent('a', "123456789"), is(true));
        assertThat(CatalogExtractionCode.verifySubfieldAndContent('a', "OVE456789"), is(false));
        assertThat(CatalogExtractionCode.verifySubfieldAndContent('a', "BKM456789"), is(true));
    }

    @Test
    void testIsPublishedIgnoreCatalogCodes() {
        SubField subfieldA = new SubField('a', "XYZ191304");
        SubField subfieldX = new SubField('x', "ÅÅÅ999999");
        SubField subfieldAmp = new SubField('&', "715700");
        SubField subfieldOve = new SubField('a', "OVE202121");

        DataField field = new DataField("032", "00");
        field.getSubFields().add(subfieldA);
        field.getSubFields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isPublishedIgnoreCatalogCodes(record), is(true));

        DataField fieldAmp = new DataField("032", "00");
        fieldAmp.getSubFields().add(subfieldAmp);
        MarcRecord recordAmp = new MarcRecord();
        recordAmp.getFields().add(fieldAmp);
        assertThat(CatalogExtractionCode.isPublishedIgnoreCatalogCodes(recordAmp), is(false));
        fieldAmp.getSubFields().add(subfieldOve);
        assertThat(CatalogExtractionCode.isPublishedIgnoreCatalogCodes(recordAmp), is(false));
        fieldAmp.getSubFields().add(subfieldA);
        assertThat(CatalogExtractionCode.isPublishedIgnoreCatalogCodes(recordAmp), is(true));

    }

    @Test
    void testIsPublishedHasProductionCodeInTheFuture() {
        SubField subfieldA = new SubField('a', "DBI291304");
        SubField subfieldX = new SubField('x', "DBI999999");

        DataField field = new DataField("032", "00");
        field.getSubFields().add(subfieldA);
        field.getSubFields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isPublished(record), is(false));
    }

    @Test
    void testIsPublishedHasACCCodeInThePast() {
        SubField subfieldA = new SubField('a', "ACC201839");
        SubField subfieldX = new SubField('x', "DBI999999");

        DataField field = new DataField("032", "00");
        field.getSubFields().add(subfieldA);
        field.getSubFields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isPublished(record), is(false));
    }

}
