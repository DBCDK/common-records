/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a marc record.
 */
public class MarcRecord {
    private static final XLogger logger = XLoggerFactory.getXLogger(MarcRecord.class);

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

    public List<MarcField> getFields() {
        return fields;
    }

    public void setFields(List<MarcField> fields) {
        this.fields = fields;
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
        int hash = 3;
        hash = 79 * hash + (this.fields != null ? this.fields.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final MarcRecord other = (MarcRecord) obj;
        return this.fields == other.fields || (this.fields != null && this.fields.equals(other.fields));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (MarcField rf : this.fields) {
            result.append(rf.toString()).append("\n");
        }

        return result.toString();
    }

}
