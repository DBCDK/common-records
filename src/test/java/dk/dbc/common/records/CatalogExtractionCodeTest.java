/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class CatalogExtractionCodeTest {

    @Test
    public void testhasLastProductionDate() throws Exception {
        assertThat(CatalogExtractionCode.hasLastProductionDate(""), equalTo(false));
        assertThat(CatalogExtractionCode.hasLastProductionDate("XXX20161"), equalTo(false));
        assertThat(CatalogExtractionCode.hasLastProductionDate("XXX2016001"), equalTo(false));
        assertThat(CatalogExtractionCode.hasLastProductionDate("DBIXXXXXX"), equalTo(false));

        assertThat(CatalogExtractionCode.hasLastProductionDate("DBI201652"), equalTo(true));
        assertThat(CatalogExtractionCode.hasLastProductionDate("DBI999999"), equalTo(true));
    }

    @Test
    public void testhasFutureLastProductionDate() throws Exception {
        assertThat(CatalogExtractionCode.hasFutureLastProductionDate("DBI999999"), equalTo(true));
        assertThat(CatalogExtractionCode.hasFutureLastProductionDate("DBI201502"), equalTo(false));
        assertThat(CatalogExtractionCode.hasFutureLastProductionDate("DBI211602"), equalTo(true));
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

}
