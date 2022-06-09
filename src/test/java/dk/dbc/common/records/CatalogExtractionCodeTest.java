/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CatalogExtractionCodeTest {

    @Test
    public void testhasLastProductionDate() throws Exception {
        assertThat(CatalogExtractionCode.hasPublishingDate(""), equalTo(false));
        assertThat(CatalogExtractionCode.hasPublishingDate("XXX20161"), equalTo(false));
        assertThat(CatalogExtractionCode.hasPublishingDate("XXX2016001"), equalTo(false));
        assertThat(CatalogExtractionCode.hasPublishingDate("DBIXXXXXX"), equalTo(false));

        assertThat(CatalogExtractionCode.hasPublishingDate("DBI201652"), equalTo(true));
        assertThat(CatalogExtractionCode.hasPublishingDate("DBI999999"), equalTo(true));
    }

    @Test
    public void testhasFutureLastProductionDate() throws Exception {
        assertThat(CatalogExtractionCode.hasFuturePublishingDate("DBI999999"), equalTo(true));
        assertThat(CatalogExtractionCode.hasFuturePublishingDate("DBI201502"), equalTo(false));
        assertThat(CatalogExtractionCode.hasFuturePublishingDate("DBI211602"), equalTo(true));
    }

    @Test
    public void testIsRecordInProductionEmptyRecord() throws Exception {
        MarcRecord record = new MarcRecord();

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(false));
    }

    @Test
    public void testIsRecordInProductionNo032() throws Exception {
        MarcRecord record = new MarcRecord();
        record.getFields().add(new MarcField("004", "00"));

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(false));
    }

    private MarcRecord generateMarcRecordWith032(String subfieldName, String value) {
        MarcSubField subfield = new MarcSubField(subfieldName, value);

        MarcField field = new MarcField("032", "00");
        field.getSubfields().add(subfield);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        return record;
    }

    @Test
    public void testIsRecordInProductionNo032ax() throws Exception {
        MarcRecord record = generateMarcRecordWith032("k", "xxx");

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(false));
    }

    @Test
    public void testIsRecordInProduction032aWrongFormat() throws Exception {
        MarcRecord record = generateMarcRecordWith032("a", "xxx");

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(false));
    }

    @Test
    public void testIsRecordInProduction032aWrongProductionCode() throws Exception {
        MarcRecord record = generateMarcRecordWith032("a", "XXX201504");

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(false));
    }

    @Test
    public void testIsRecordInProduction032aPublicityDateIsBeforeCurrentDate() throws Exception {
        MarcRecord record = generateMarcRecordWith032("a", "DBI201504");

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(false));
    }

    @Test
    public void testIsRecordInProduction032aPublicityDateIsBeforeCurrentDateCustomCatalogCodes() throws Exception {
        MarcRecord record = generateMarcRecordWith032("a", "DBF201504");

        assertThat(CatalogExtractionCode.isUnderProduction(record,
                Arrays.asList("DLF", "DBI", "DMF", "DMO", "DPF", "BKM", "GBF", "GMO", "GPF", "FPF", "DBR", "UTI")),
                equalTo(false));
    }

    @Test
    public void testIsRecordInProduction032aPublicityDateIsAfterCurrentDate() throws Exception {
        MarcRecord record = generateMarcRecordWith032("a", "DBI211604");

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(true));
    }

    @Test
    public void testIsRecordInProduction032xWrongFormat() throws Exception {
        MarcRecord record = generateMarcRecordWith032("x", "xxx");

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(false));
    }

    @Test
    public void testIsRecordInProduction032xWrongProductionCode() throws Exception {
        MarcRecord record = generateMarcRecordWith032("x", "XXX201504");

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(false));
    }

    @Test
    public void testIsRecordInProduction032xPublicityDateIsBeforeCurrentDate() throws Exception {
        MarcRecord record = generateMarcRecordWith032("x", "DBI201504");

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(false));
    }

    @Test
    public void testIsRecordInProduction032xPublicityDateIsAfterCurrentDate() throws Exception {
        MarcRecord record = generateMarcRecordWith032("x", "DBI211604");

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(true));
    }

    @Test
    public void testIsRecordInProduction032xTemporaryDateOnly() throws Exception {
        MarcRecord record = generateMarcRecordWith032("x", "DBI999999");

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(true));
    }

    @Test
    public void testIsRecordInProduction032aTemporaryDateOnly() throws Exception {
        MarcRecord record = generateMarcRecordWith032("a", "DBI999999");

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(true));
    }

    @Test
    public void testIsRecordInProduction032aPublicityDateIsBeforeCurrentDateAnd032xPublicityDateIsAfterCurrentDate() throws Exception {
        MarcSubField subfieldA = new MarcSubField("a", "DBI191304");
        MarcSubField subfieldX = new MarcSubField("x", "DBI211604");

        MarcField field = new MarcField("032", "00");
        field.getSubfields().add(subfieldA);
        field.getSubfields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(false));
    }

    @Test
    public void testIsRecordInProduction032aTemporyDateAnd032xPublicityDateIsAfterCurrentDate() throws Exception {
        MarcSubField subfieldA = new MarcSubField("a", "DBI999999");
        MarcSubField subfieldX = new MarcSubField("x", "DBI211604");

        MarcField field = new MarcField("032", "00");
        field.getSubfields().add(subfieldA);
        field.getSubfields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(true));
    }

    @Test
    public void testIsRecordInProduction032aPublicityDateIsBeforeCurrentDateAnd032xTemporyDate() throws Exception {
        MarcSubField subfieldA = new MarcSubField("a", "DBI191304");
        MarcSubField subfieldX = new MarcSubField("x", "DBI999999");

        MarcField field = new MarcField("032", "00");
        field.getSubfields().add(subfieldA);
        field.getSubfields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isUnderProduction(record), equalTo(false));
    }

    @Test
    public void testIsPublishedNoField032() throws Exception {
        MarcRecord record = new MarcRecord();

        assertThat(CatalogExtractionCode.isPublished(record), equalTo(false));
    }


    @Test
    public void testIsPublishedHasProductionCodeInThePast() throws Exception {
        MarcSubField subfieldA = new MarcSubField("a", "DBI191304");
        MarcSubField subfieldX = new MarcSubField("x", "DBI999999");

        MarcField field = new MarcField("032", "00");
        field.getSubfields().add(subfieldA);
        field.getSubfields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isPublished(record), equalTo(true));
    }

    @Test
    public void testIsPublishedHasProductionCodeInThePastCustomCatalogueCodes() throws Exception {
        MarcSubField subfieldA = new MarcSubField("a", "DBI191304");
        MarcSubField subfieldX = new MarcSubField("x", "DBI999999");

        MarcField field = new MarcField("032", "00");
        field.getSubfields().add(subfieldA);
        field.getSubfields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isPublished(record, Arrays.asList("DBF", "DLF", "DMF", "DMO", "DPF", "BKM", "GBF", "GMO", "GPF", "FPF", "DBR", "UTI")), equalTo(false));
    }

    @Test
    public void testIsPublishedIgnoreCatalogCodes() {
        MarcSubField subfieldA = new MarcSubField("a", "XYZ191304");
        MarcSubField subfieldX = new MarcSubField("x", "ÅÅÅ999999");

        MarcField field = new MarcField("032", "00");
        field.getSubfields().add(subfieldA);
        field.getSubfields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isPublishedIgnoreCatalogCodes(record), equalTo(true));
    }

    @Test
    public void testIsPublishedHasProductionCodeInTheFuture() throws Exception {
        MarcSubField subfieldA = new MarcSubField("a", "DBI291304");
        MarcSubField subfieldX = new MarcSubField("x", "DBI999999");

        MarcField field = new MarcField("032", "00");
        field.getSubfields().add(subfieldA);
        field.getSubfields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isPublished(record), equalTo(false));
    }

    @Test
    public void testIsPublishedHasACCCodeInThePast() throws Exception {
        MarcSubField subfieldA = new MarcSubField("a", "ACC201839");
        MarcSubField subfieldX = new MarcSubField("x", "DBI999999");

        MarcField field = new MarcField("032", "00");
        field.getSubfields().add(subfieldA);
        field.getSubfields().add(subfieldX);

        MarcRecord record = new MarcRecord();
        record.getFields().add(field);

        assertThat(CatalogExtractionCode.isPublished(record), equalTo(false));
    }

}
