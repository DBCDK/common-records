/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a marc record.
 */
public class MarcRecord {
    private static final XLogger logger = XLoggerFactory.getXLogger(MarcRecord.class);

    private String leader;
    private MarcRecordType type;
    private List<MarcControlField> controlFields;
    private List<MarcField> fields;

    /**
     * Constructs a marc record.
     *
     * <p>The fields attribute is initialized to null.
     */
    public MarcRecord() {
        this(new ArrayList<>());
    }

    /**
     * Constructs a marc record.
     *
     * @param fields List of fields.
     */
    public MarcRecord(List<MarcField> fields) {
        this.fields = fields;
    }

    /**
     * Copy constructor.
     *
     * <p>Each field is deep copied to the new instance.
     */
    public MarcRecord(MarcRecord other) {
        this.fields = new ArrayList<>();
        for (MarcField field : other.fields) {
            this.fields.add(new MarcField(field));
        }
    }

    public MarcRecord(String leader, MarcRecordType type) {
        this.leader = leader;
        this.type = type;
        this.controlFields = new ArrayList<>();
        this.fields = new ArrayList<>();
    }

    public List<MarcField> getFields() {
        return fields;
    }

    public void setFields(List<MarcField> fields) {
        this.fields = fields;
    }

    public List<MarcControlField> getControlFields() {
        return controlFields;
    }

    public void setControlFields(List<MarcControlField> controlFields) {
        this.controlFields = controlFields;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public MarcRecordType getType() {
        return type;
    }

    public void setType(MarcRecordType type) {
        this.type = type;
    }

    public boolean isEmpty() {
        logger.entry();

        Boolean result = null;
        try {
            return result = fields == null || fields.isEmpty();
        } finally {
            logger.exit(result);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(leader, type, controlFields, fields);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarcRecord that = (MarcRecord) o;
        return Objects.equals(leader, that.leader) &&
                type == that.type &&
                Objects.equals(controlFields, that.controlFields) &&
                Objects.equals(fields, that.fields);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (leader != null && !leader.isEmpty()) {
            result.append(leader);
            result.append("\n");
            result.append("------------------------------\n");
        }

        if (controlFields != null) {
            for (MarcControlField marcControlField : this.controlFields) {
                result.append(marcControlField.toString());
                result.append("\n");
            }
            result.append("------------------------------\n");
        }

        for (MarcField marcField : this.fields) {
            result.append(marcField.toString());
            result.append("\n");
        }

        return result.toString();
    }

}
