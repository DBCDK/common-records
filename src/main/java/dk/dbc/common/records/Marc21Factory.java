package dk.dbc.common.records;

import dk.dbc.common.records.marc21.ControlFieldType;
import dk.dbc.common.records.marc21.DataFieldType;
import dk.dbc.common.records.marc21.LeaderFieldType;
import dk.dbc.common.records.marc21.RecordType;
import dk.dbc.common.records.marc21.RecordTypeType;
import dk.dbc.common.records.marc21.SubfieldatafieldType;

public class Marc21Factory {
    public static RecordType createMarc21FromMarc(MarcRecord marcRecord) {
        RecordType recordType = new RecordType();
        DataFieldType dataFieldType;
        ControlFieldType controlFieldType;

        if (marcRecord != null) {
            LeaderFieldType leaderFieldType = new LeaderFieldType();
            leaderFieldType.setValue(marcRecord.getLeader());
            recordType.setLeader(leaderFieldType);

            RecordTypeType recordTypeType = RecordTypeType.fromValue(marcRecord.getType());
            recordType.setType(recordTypeType);
            if (marcRecord.getFields() != null) {
                for (MarcField marcField : marcRecord.getFields()) {
                    dataFieldType = createMarc21FieldFromMarcField(marcField);
                    recordType.getDatafield().add(dataFieldType);
                }
            }

            if (marcRecord.getControlFields() != null) {
                for (MarcControlField controlField : marcRecord.getControlFields()) {
                    controlFieldType = createMarc21ControlFieldFromMarcField(controlField);
                    recordType.getControlfield().add(controlFieldType);
                }
            }
        }

        return recordType;
    }

    private static DataFieldType createMarc21FieldFromMarcField(MarcField marcField) {
        DataFieldType dataFieldType = new DataFieldType();
        if (marcField != null) {
            fillIndicatorFieldsFromString(marcField.getIndicator(), dataFieldType);
            dataFieldType.setTag(marcField.getName());
            SubfieldatafieldType subfieldatafieldType;
            if (marcField.getSubfields() != null && !marcField.getSubfields().isEmpty()) {
                for (MarcSubField marcSubField : marcField.getSubfields()) {
                    subfieldatafieldType = createMarc21SubfieldFromMarcSubfield(marcSubField);
                    dataFieldType.getSubfield().add(subfieldatafieldType);
                }
            }
        }
        return dataFieldType;
    }

    private static SubfieldatafieldType createMarc21SubfieldFromMarcSubfield(MarcSubField marcSubField) {
        SubfieldatafieldType subfieldatafieldType = new SubfieldatafieldType();
        if (marcSubField != null) {
            subfieldatafieldType.setCode(marcSubField.getName());
            subfieldatafieldType.setValue(marcSubField.getValue());
        } else {
            subfieldatafieldType.setCode("");
        }
        return subfieldatafieldType;
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
        }
        return dataFieldType;
    }

    private static ControlFieldType createMarc21ControlFieldFromMarcField(MarcControlField controlField) {
        ControlFieldType controlFieldType = new ControlFieldType();

        controlFieldType.setTag(controlField.getName());
        controlFieldType.setValue(controlField.getValue());

        return controlFieldType;
    }
}
