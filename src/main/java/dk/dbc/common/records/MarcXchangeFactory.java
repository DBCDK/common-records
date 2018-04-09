/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import dk.dbc.common.records.marcxchange.ControlFieldType;
import dk.dbc.common.records.marcxchange.DataFieldType;
import dk.dbc.common.records.marcxchange.LeaderFieldType;
import dk.dbc.common.records.marcxchange.RecordType;
import dk.dbc.common.records.marcxchange.SubfieldatafieldType;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Factory class to construct MarcXchange objects.
 */
public class MarcXchangeFactory {
    private static final XLogger logger = XLoggerFactory.getXLogger(MarcXchangeFactory.class);

    public static RecordType createMarcXchangeFromMarc(MarcRecord marcRecord) {
        logger.info("createMarcXchangeFromMarc");

        RecordType recordType = new RecordType();
        DataFieldType dataFieldType;
        ControlFieldType controlFieldType;

        if (marcRecord != null && marcRecord.getFields() != null) {
            logger.info("Input record:");
            logger.info(marcRecord.toString());

            LeaderFieldType leaderFieldType = new LeaderFieldType();
            if (marcRecord.getLeader() == null) {
                leaderFieldType.setValue(MarcStatic.MARC_X_CHANGE_LEADER);
            } else {
                leaderFieldType.setValue(marcRecord.getLeader());
            }
            recordType.setLeader(leaderFieldType);
            logger.info("Leader: {}", leaderFieldType);
            recordType.setType(marcRecord.getType());
            logger.info("Type: {}", marcRecord.getType());

            if (marcRecord.getControlFields() != null) {
                for (MarcControlField controlField : marcRecord.getControlFields()) {
                    controlFieldType = createMarcXchangeControlFieldFromMarcField(controlField);
                    recordType.getControlfield().add(controlFieldType);
                    logger.info("Adding control field: {}", controlFieldType);
                }
            }

            for (MarcField marcField : marcRecord.getFields()) {
                dataFieldType = createMarcXchangeFieldFromMarcField(marcField);
                recordType.getDatafield().add(dataFieldType);
                logger.info("Adding field: {}", dataFieldType);
            }
        }
        return recordType;
    }

    private static DataFieldType createMarcXchangeFieldFromMarcField(MarcField marcField) {
        DataFieldType dataFieldType = new DataFieldType();
        if (marcField != null) {
            fillIndicatorFieldsFromString(marcField.getIndicator(), dataFieldType);
            dataFieldType.setTag(marcField.getName());
            SubfieldatafieldType subfieldatafieldType;
            if (marcField.getSubfields() != null && !marcField.getSubfields().isEmpty()) {
                for (MarcSubField marcSubField : marcField.getSubfields()) {
                    subfieldatafieldType = createMarcXchangeSubfieldFromMarcSubfield(marcSubField);
                    dataFieldType.getSubfield().add(subfieldatafieldType);
                }
            }
        }
        return dataFieldType;
    }

    private static SubfieldatafieldType createMarcXchangeSubfieldFromMarcSubfield(MarcSubField marcSubField) {
        SubfieldatafieldType subfieldatafieldType = new SubfieldatafieldType();
        if (marcSubField != null) {
            subfieldatafieldType.setCode(marcSubField.getName());
            subfieldatafieldType.setValue(marcSubField.getValue());
        } else {
            subfieldatafieldType.setCode("");
        }
        return subfieldatafieldType;
    }

    private static ControlFieldType createMarcXchangeControlFieldFromMarcField(MarcControlField controlField) {
        ControlFieldType controlFieldType = new ControlFieldType();

        controlFieldType.setTag(controlField.getName());
        controlFieldType.setValue(controlField.getValue());

        return controlFieldType;
    }

    private static DataFieldType fillIndicatorFieldsFromString(String indicator, DataFieldType dataFieldType) {
        if (indicator != null && !indicator.isEmpty()) {
            String tmpIndicator = indicator;
            if (tmpIndicator.length() > 0) {
                dataFieldType.setInd1(tmpIndicator.substring(0, 1));
                tmpIndicator = tmpIndicator.substring(1);
            }
            if (!tmpIndicator.isEmpty() && tmpIndicator.length() > 0) {
                dataFieldType.setInd2(tmpIndicator.substring(0, 1));
                tmpIndicator = tmpIndicator.substring(1);
            }
            if (!tmpIndicator.isEmpty() && tmpIndicator.length() > 0) {
                dataFieldType.setInd3(tmpIndicator.substring(0, 1));
                tmpIndicator = tmpIndicator.substring(1);
            }
            if (!tmpIndicator.isEmpty() && tmpIndicator.length() > 0) {
                dataFieldType.setInd4(tmpIndicator.substring(0, 1));
                tmpIndicator = tmpIndicator.substring(1);
            }
            if (!tmpIndicator.isEmpty() && tmpIndicator.length() > 0) {
                dataFieldType.setInd5(tmpIndicator.substring(0, 1));
                tmpIndicator = tmpIndicator.substring(1);
            }
            if (!tmpIndicator.isEmpty() && tmpIndicator.length() > 0) {
                dataFieldType.setInd6(tmpIndicator.substring(0, 1));
                tmpIndicator = tmpIndicator.substring(1);
            }
            if (!tmpIndicator.isEmpty() && tmpIndicator.length() > 0) {
                dataFieldType.setInd7(tmpIndicator.substring(0, 1));
                tmpIndicator = tmpIndicator.substring(1);
            }
            if (!tmpIndicator.isEmpty() && tmpIndicator.length() > 0) {
                dataFieldType.setInd8(tmpIndicator.substring(0, 1));
                tmpIndicator = tmpIndicator.substring(1);
            }
            if (!tmpIndicator.isEmpty() && tmpIndicator.length() > 0) {
                dataFieldType.setInd9(tmpIndicator.substring(0, 1));
            }
        }
        return dataFieldType;
    }
}
