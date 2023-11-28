package dk.dbc.common.records;


import dk.dbc.marc.binding.DataField;
import dk.dbc.marc.binding.Field;
import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.binding.SubField;
import dk.dbc.marc.reader.MarcReaderException;
import dk.dbc.marc.reader.MarcXchangeV1Reader;
import dk.dbc.marc.writer.MarcXchangeV1Writer;
import dk.dbc.util.Stopwatch;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static dk.dbc.marc.binding.DataField.hasSubFieldCode;
import static dk.dbc.marc.binding.MarcRecord.hasTag;

public class ExpandCommonMarcRecord {
    private static final XLogger logger = XLoggerFactory.getXLogger(ExpandCommonMarcRecord.class);
    public static final List<String> AUTHORITY_FIELD_LIST = Arrays.asList("100", "110", "233", "234", "600", "610", "664", "665", "666", "700", "710", "770", "780", "845", "846");
    private static final MarcXchangeV1Writer marcRecordWriter = new MarcXchangeV1Writer();
    private static final Charset charset = StandardCharsets.UTF_8;

    private static MarcRecord contentToMarcRecord(byte[] content) throws MarcReaderException {
        final InputStream inputStream = new ByteArrayInputStream(content);
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        final MarcXchangeV1Reader reader = new MarcXchangeV1Reader(bufferedInputStream, charset);

        return reader.read();
    }

    /**
     * This function performs authority expansion on a rawrepo Record.
     *
     * @param content          The record content which should be expanded
     * @param authorityContent List of authority record content to be used for expanding
     * @param keepAutFields    If true the  *5 and *6 fields remains in the output record
     * @throws MarcReaderException When expansion fails (usually due to missing authority record)
     */
    public static byte[] expandRecord(byte[] content, Map<String, byte[]> authorityContent, boolean keepAutFields) throws MarcReaderException, MarcRecordExpandException {
        final Stopwatch stopWatch = new Stopwatch();
        final MarcRecord commonMarcRecord = contentToMarcRecord(content);
        logger.debug("Stopwatch - {} took {} ms", "RecordContentTransformer.decodeRecord(common)", stopWatch.getElapsedTime(TimeUnit.MILLISECONDS));
        stopWatch.reset();

        final Map<String, MarcRecord> authorityMarcRecords = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : authorityContent.entrySet()) {
            authorityMarcRecords.put(entry.getKey(), contentToMarcRecord(entry.getValue()));
            logger.debug("Stopwatch - {} took {} ms", "RecordContentTransformer.decodeRecord(loop)", stopWatch.getElapsedTime(TimeUnit.MILLISECONDS));
            stopWatch.reset();
        }

        final MarcRecord expandedMarcRecord = doExpand(commonMarcRecord, authorityMarcRecords, keepAutFields);
        logger.debug("Stopwatch - {} took {} ms", "doExpand", stopWatch.getElapsedTime(TimeUnit.MILLISECONDS));
        stopWatch.reset();

        logger.debug("Stopwatch - {} took {} ms", "sortFields", stopWatch.getElapsedTime(TimeUnit.MILLISECONDS));
        stopWatch.reset();

        logger.debug("Stopwatch - {} took {} ms", "RecordContentTransformer.encodeRecord", stopWatch.getElapsedTime(TimeUnit.MILLISECONDS));
        stopWatch.reset();
        return marcRecordWriter.write(expandedMarcRecord, charset);
    }

    /**
     * The function takes a set of  records and return a common marc record expanded with authority fields (if any)
     *
     * @param records map containing a common record and x amount of authority records
     * @return a single common record expanded with authority data
     * @throws MarcRecordExpandException if the collection doesn't contain the necessary records
     */
    public static MarcRecord expandMarcRecord(Map<String, MarcRecord> records, String recordId, boolean keepAutFields) throws MarcRecordExpandException {
        final Stopwatch stopWatch = new Stopwatch();
        MarcRecord commonRecord = null;
        final Map<String, MarcRecord> authorityRecords = new HashMap<>();

        // Key is the recordId and value is the record. AgencyId have to be found in the record
        for (Map.Entry<String, MarcRecord> entry : records.entrySet()) {
            final MarcRecord marcRecord = entry.getValue();
            final String foundRecordId = marcRecord.getSubFieldValue("001", 'a').orElseThrow();
            final String foundAgencyId = marcRecord.getSubFieldValue("001", 'b').orElseThrow();
            logger.debug("Found record in expand collection: {}:{}", foundRecordId, foundAgencyId);
            if (recordId.equals(foundRecordId)) {
                commonRecord = entry.getValue();
            } else if ("870979".equals(foundAgencyId)) {
                authorityRecords.put(foundRecordId, entry.getValue());
            }
        }

        logger.debug("Stopwatch - {} took {} ms", "expandMarcRecord", stopWatch.getElapsedTime(TimeUnit.MILLISECONDS));

        if (commonRecord == null) {
            throw new MarcRecordExpandException("The record collection doesn't contain a common record");
        }

        return doExpand(commonRecord, authorityRecords, keepAutFields);
    }

    /**
     * The function takes a set of  records and return a common marc record expanded with authority fields (if any)
     *
     * @param records  The collection of records
     * @param recordId The id of the record to expand
     * @return a single common record expanded with authority data
     * @throws MarcRecordExpandException if the collection doesn't contain the necessary records
     */
    public static MarcRecord expandMarcRecord(Map<String, MarcRecord> records, String recordId) throws MarcRecordExpandException {
        return expandMarcRecord(records, recordId, false);
    }

    private static MarcRecord doExpand(MarcRecord commonRecord, Map<String, MarcRecord> authorityRecords, boolean keepAutFields) throws MarcRecordExpandException {
        final MarcRecord expandedRecord = new MarcRecord()
                .setLeader(commonRecord.getLeader());
        /*
         * Okay, here are (some) of the rules for expanding with auth records:
         * Fields that can contain AUT are: 100, 110, 600, 610, 700, 710, 770, 780, 845 or 846
         * AUT reference are located in *5 and *6
         *
         * A field points to AUT data if:
         * Field name is either 100, 110, 600, 610, 700, 710, 770, 780, 845 or 846
         * And contains subfields *5 and *6
         *
         * Rules for expanding are:
         * Remove *5 and *6 if keepAutFields are false
         * Add all subfields from AUT record field 100 or 110 at the same location as *5
         * For fields 100, 600, 700, 770:
         * If AUT record contains field 400 or 500 then add that field as well to the expanded record but as field 900
         * For fields 110, 610, 710, 780:
         * If AUT record contains field 410 or 510 then add that field as well to the expanded record but as field 910
         */

        // Record doesn't have any authority record references, so just return the same record
        if (!hasAutFields(commonRecord)) {
            return commonRecord;
        }

        final int authNumerator = findMaxAuthNumerator(commonRecord.getFields(DataField.class));
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("100")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("110")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("233")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("234")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("600")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("610")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("664")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("665")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("666")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("700")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("710")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("770")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("780")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("845")), expandedRecord, authorityRecords, keepAutFields, authNumerator);
        handleRepeatableField(commonRecord.getFields(DataField.class, hasTag("846")), expandedRecord, authorityRecords, keepAutFields, authNumerator);

        for (DataField dataField : commonRecord.getFields(DataField.class)) {
            if (!AUTHORITY_FIELD_LIST.contains(dataField.getTag())) {
                expandedRecord.getFields().add(new DataField(dataField));
            }
        }

        sortFields(expandedRecord);

        return expandedRecord;
    }

    private static int findMaxAuthNumerator(List<DataField> dataFields) {
        int authNumerator = 1001;
        for (DataField dataField : dataFields) {
            if (dataField.hasSubField(hasSubFieldCode('å'))) {
                try {
                    final int numerator = Integer.parseInt(dataField.getSubField(hasSubFieldCode('å')).orElseThrow().getData());
                    if (numerator > authNumerator) {
                        authNumerator = numerator + 1;
                    }
                } catch (NumberFormatException ex) {
                    final String message = String.format("Ugyldig værdi i delfelt %s *å. Forventede et tal men fik '%s' - ignorerer", dataField.getTag(), dataField.getSubField(hasSubFieldCode('å')).get().getData());
                    logger.debug(message);
                }
            }
        }

        return authNumerator;
    }

    private static void handleRepeatableField(List<DataField> dataFields, MarcRecord expandedRecord, Map<String, MarcRecord> authorityRecords, boolean keepAutFields, int authNumerator) throws MarcRecordExpandException {
        for (DataField dataField : dataFields) {
            if (dataField.hasSubField(hasSubFieldCode('5')) && dataField.hasSubField(hasSubFieldCode('6'))) {
                final String authRecordId = dataField.getSubField(hasSubFieldCode('6')).orElseThrow().getData();
                final MarcRecord authRecord = authorityRecords.get(authRecordId);

                if (authRecord == null) {
                    final String message = String.format("Autoritetsposten '%s' blev ikke fundet i forbindelse med ekspandering af fællesskabsposten", authRecordId);
                    logger.error(message);
                    throw new MarcRecordExpandException(message);
                }

                final DataField expandedField = new DataField(dataField);
                String authAuthorFieldName = "";

                int mode = 0;
                switch (dataField.getTag()) {
                    case "100":
                        mode = 1;
                        authAuthorFieldName = "100";
                        break;
                    case "600":
                    case "700":
                    case "770":
                        mode = 2;
                        authAuthorFieldName = "100";
                        break;
                    case "110":
                        mode = 1;
                        authAuthorFieldName = "110";
                        break;
                    case "610":
                    case "710":
                    case "780":
                        mode = 2;
                        authAuthorFieldName = "110";
                        break;
                    case "845":
                        mode = 3;
                    case "233":
                        authAuthorFieldName = "133";
                        break;
                    case "846":
                        mode = 4;
                    case "234":
                        authAuthorFieldName = "134";
                        break;
                }
                if (!authRecord.hasField(hasTag(authAuthorFieldName))) {
                    return;
                }
                final DataField authAuthorField = new DataField((DataField) authRecord.getField(hasTag(authAuthorFieldName)).orElseThrow());

                addMainField(expandedField, authAuthorField, keepAutFields);

                String fieldReference = dataField.getTag();
                if (mode == 1 || mode == 2) {
                    // x00 and 770 puts 400 and 500 in 900 fields and x10 and 780 puts 410 and 510 in 910 fields - this is so fun
                    boolean hasAdditionalFields;
                    List<String> mayNeedFourFiveHundred = Arrays.asList("00", "70");
                    if (mayNeedFourFiveHundred.contains(dataField.getTag().substring(1))) {
                        hasAdditionalFields = authRecord.hasField(hasTag("400").or(hasTag("500")));
                    } else {
                        hasAdditionalFields = authRecord.hasField(hasTag("410").or(hasTag("510")));
                    }
                    if (mode == 2 && hasAdditionalFields) {
                        // The field is repeatable, so we add a numerator value to the *z content
                        if (!dataField.hasSubField(hasSubFieldCode('å'))) {
                            expandedField.getSubFields().add(0, new SubField('å', Integer.toString(authNumerator)));
                            fieldReference += "/" + authNumerator;
                            authNumerator++;
                        } else {
                            fieldReference += "/" + dataField.getSubField(hasSubFieldCode('å')).orElseThrow().getData();
                        }
                    }
                    if (mayNeedFourFiveHundred.contains(dataField.getTag().substring(1))) {
                        if (hasAdditionalFields) {
                            addAdditionalFields("900", expandedRecord, authRecord.getFields(DataField.class, hasTag("400")), authAuthorField, fieldReference);
                            addAdditionalFields("900", expandedRecord, authRecord.getFields(DataField.class, hasTag("500")), authAuthorField, fieldReference);
                        }
                    } else {
                        if (hasAdditionalFields) {
                            addAdditionalFields("910", expandedRecord, authRecord.getFields(DataField.class, hasTag("410")), authAuthorField, fieldReference);
                            addAdditionalFields("910", expandedRecord, authRecord.getFields(DataField.class, hasTag("510")), authAuthorField, fieldReference);
                        }
                    }
                } else {
                    // The universe/series fields is repeatable, so we add a numerator value to the *z content
                    if (!dataField.hasSubField(hasSubFieldCode('å'))) {
                        expandedField.getSubFields().add(0, new SubField('å', Integer.toString(authNumerator)));
                        fieldReference += "/" + authNumerator;
                        authNumerator++;
                    } else {
                        fieldReference += "/" + dataField.getSubField(hasSubFieldCode('å')).orElseThrow().getData();
                    }
                    if (mode == 3) {
                        addAdditionalFields("945", expandedRecord, authRecord.getFields(DataField.class, hasTag("433")), authAuthorField, fieldReference);
                    } else {
                        addAdditionalFields("945", expandedRecord, authRecord.getFields(DataField.class, hasTag("434")), authAuthorField, fieldReference);
                    }
                }
                expandedRecord.getFields().add(new DataField(expandedField));
            } else {
                expandedRecord.getFields().add(new DataField(dataField));
            }
        }
    }

    private static void addMainField(Field<DataField> field, Field<DataField> authField, boolean keepAutFields) {
        // Find the index of where the AUT reference subfields are in the field
        // We need to add the AUT content at that location
        int authSubfieldIndex = 0;
        final DataField dataField = (DataField) field;
        for (int i = 0; i < dataField.getSubFields().size(); i++) {
            if (dataField.getSubFields().get(i).getCode() == '5') {
                authSubfieldIndex = i;
                break;
            }
        }

        if (keepAutFields) {
            // If we are keeping *5 and *6 then move the aut data 2 fields "back"
            authSubfieldIndex += 2;
        } else {
            dataField.removeSubField('5');
            dataField.removeSubField('6');
        }
        dataField.setInd1('0');
        dataField.setInd2('0');

        if ("845".equals(dataField.getTag()) || "846".equals(dataField.getTag())) {
            final DataField authDataField = (DataField) authField;
            for (SubField authSubfield : authDataField.getSubFields()) {
                if ('a' == authSubfield.getCode()) {
                    dataField.getSubFields().add(authSubfieldIndex++, new SubField(authSubfield));
                }
            }
        } else {
            final DataField authDataField = (DataField) authField;
            for (SubField authSubfield : authDataField.getSubFields()) {
                dataField.getSubFields().add(authSubfieldIndex++, new SubField(authSubfield));
            }
        }
    }

    private static void addAdditionalFields(String fieldName, MarcRecord marcRecord, List<DataField> authFields, DataField authAuthorField, String fieldReference) {
        final boolean universeFields = "945".equals(fieldName); // I don't like this, but for the moment only universe/series put things in 945
        for (DataField authDataField : authFields) {
            final DataField additionalField = new DataField(fieldName, "00");
            String subfieldwValue = null;
            for (SubField authSubfield : authDataField.getSubFields()) {
                if ('w' == authSubfield.getCode()) {
                    if (!universeFields) {
                        subfieldwValue = authSubfield.getData();
                    }
                } else {
                    if (universeFields) {
                        if ('a' == authSubfield.getCode()) {
                            // there will at least be a subfield 8 which isn't wanted - only subfield a should be copied
                            additionalField.getSubFields().add(new SubField(authSubfield));
                        }
                    } else {
                        additionalField.getSubFields().add(new SubField(authSubfield));
                    }
                }
            }

            if (!universeFields) {
                if (subfieldwValue != null) {
                    if ("tidligere navn".equals(subfieldwValue)) {
                        additionalField.getSubFields().add(new SubField('x', "se også under det senere navn"));
                    } else if ("senere navn".equals(subfieldwValue)) {
                        additionalField.getSubFields().add(new SubField('x', "se også under det tidligere navn"));
                    } else {
                        additionalField.getSubFields().add(new SubField('x', subfieldwValue));
                    }
                } else {
                    if (Arrays.asList("500", "510").contains(authDataField.getTag())) {
                        additionalField.getSubFields().add(new SubField('x', "se også"));
                    } else {
                        additionalField.getSubFields().add(new SubField('x', "se"));
                    }
                }
            }

            final StringBuilder sb = new StringBuilder();

            /*
             * Generelt om ekspansion af felt 410 til 910:
             * For ekspansion af felt 410 i A-posten (som henvisning til 610, 710 og 780) gælder:
             * Indhold fra felt 110 - alle delfelterne undtagen eijk - skal skrives i B-postens felt 910 *w i den rækkefølge de optræder i A-posten. Efter hvert delfelt skal skrives et punktum.
             * Indhold fra delfelterne e, i, j og k skal skrives i en blød parentes med : mellem. Der skal være blanktegn på begge sider af semikolon. Se eksempler.
             * Kommer et delfelt c efter et af delfelterne e, i, j eller k skal der være et punktum efter den bløde parentes afsluttes. Se eksempel.
             */
            if (!universeFields) {
                if ("110".equals(authAuthorField.getTag())) {
                    final List<Character> parenthesesSubFieldNames = Arrays.asList('e', 'i', 'j', 'k');

                    char previousSubFieldName = '\0';

                    for (SubField subField : authAuthorField.getSubFields()) {
                        if (parenthesesSubFieldNames.contains(subField.getCode())) {
                            if (parenthesesSubFieldNames.contains(previousSubFieldName)) {
                                // Continue parentheses
                                sb.append(" : ");
                                sb.append(subField.getData());
                            } else {
                                // New parentheses
                                sb.append(" (");
                                sb.append(subField.getData());
                            }
                        } else {
                            if (parenthesesSubFieldNames.contains(previousSubFieldName)) {
                                // End parentheses
                                sb.append("). ");
                                sb.append(subField.getData());
                            } else {
                                if ('\u0000' != previousSubFieldName) {
                                    sb.append(". ");
                                }
                                sb.append(subField.getData());
                            }
                        }
                        previousSubFieldName = subField.getCode();
                    }
                    if (parenthesesSubFieldNames.contains(previousSubFieldName)) {
                        sb.append(")");
                    }
                } else {
                    final boolean hasAuthField100A = authAuthorField.hasSubField(hasSubFieldCode('a'));
                    final boolean hasAuthField100H = authAuthorField.hasSubField(hasSubFieldCode('h'));

                    if (hasAuthField100A && hasAuthField100H) {
                        sb.append(authAuthorField.getSubField(hasSubFieldCode('a')).orElseThrow().getData());
                        sb.append(", ");
                        sb.append(authAuthorField.getSubField(hasSubFieldCode('h')).orElseThrow().getData());
                    } else if (hasAuthField100A) {
                        sb.append(authAuthorField.getSubField(hasSubFieldCode('a')).orElseThrow().getData());
                    } else if (hasAuthField100H) {
                        sb.append(authAuthorField.getSubField(hasSubFieldCode('h')).orElseThrow().getData());
                    }

                    if (authAuthorField.hasSubField(hasSubFieldCode('c'))) {
                        sb.append(" (");
                        sb.append(authAuthorField.getSubField(hasSubFieldCode('c')).orElseThrow().getData());
                        sb.append(")");
                    }
                }
                additionalField.getSubFields().add(new SubField('w', sb.toString()));

            }
            additionalField.getSubFields().add(new SubField('z', fieldReference));
            marcRecord.getFields().add(additionalField);
        }
    }

    private static void sortFields(MarcRecord marcRecord) {
        // First sort by field name then sort by subfield å
        marcRecord.getFields().sort((m1, m2) -> {
            if (m1.getTag().equals(m2.getTag())) {
                return getAaIntegerValue((DataField) m1) - getAaIntegerValue((DataField) m2);
            }

            return m1.getTag().compareTo(m2.getTag());
        });
    }

    private static int getAaIntegerValue(DataField dataField) {
        if (dataField.hasSubField(hasSubFieldCode('å'))) {
            try {
                return Integer.parseInt(dataField.getSubField(hasSubFieldCode('å')).orElseThrow().getData());
            } catch (NumberFormatException e) {
                // The subfield has a value that can't be converted to an integer, so we just put the field at top
                return 0;
            }
        } else {
            return 0;
        }
    }

    private static boolean hasAutFields(MarcRecord marcRecord) {
        for (DataField dataField : marcRecord.getFields(DataField.class)) {
            if (dataField.hasSubField(hasSubFieldCode('5').or(hasSubFieldCode('6')))) {
                return true;
            }
        }

        return false;
    }

}
