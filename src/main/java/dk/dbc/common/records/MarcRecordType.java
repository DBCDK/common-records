package dk.dbc.common.records;

import javax.xml.bind.annotation.XmlEnumValue;

public enum MarcRecordType {
    BIBLIOGRAPHIC("Bibliographic"),
    AUTHORITY("Authority"),
    HOLDINGS("Holdings"),
    CLASSIFICATION("Classification"),
    COMMUNITY("Community"),
    UNDEFINED("Undefined");

    private String value;

    public String getText() {
        return this.value;
    }

    private MarcRecordType(String value) {
        this.value = value;
    }

    public static MarcRecordType fromString(String text) {
        for (MarcRecordType type : MarcRecordType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
