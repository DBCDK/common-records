/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class can write values to a MarcRecord.
 */
public class MarcRecordWriter {
    private static final XLogger logger = XLoggerFactory.getXLogger(MarcRecordReader.class);

    private MarcRecord record;

    public MarcRecordWriter(MarcRecord record) {
        this.record = record;
    }

    public MarcRecord getRecord() {
        return record;
    }

    public void addFieldSubfield(String fieldname, String subfieldname, String value) {
        MarcField field = new MarcField(fieldname, "00");
        field.getSubfields().add(new MarcSubField(subfieldname, value));
        record.getFields().add(field);
    }

    public void copyFieldFromRecord(String fieldname, MarcRecord rec) {
        logger.entry(fieldname, rec);
        try {
            List<MarcField> marcFieldsToAdd = new ArrayList<>();
            rec.getFields().forEach((MarcField mf) -> {
                if (mf.getName().equals(fieldname)) {
                    marcFieldsToAdd.add(mf);
                }
            });
            record.getFields().addAll(marcFieldsToAdd);

        } finally {
            logger.exit();
        }
    }

    /***
     * Function to copy a list of fields from a record. Notice the fields will be appended,
     * even if the record being updated already contains the fields
     *
     * @param fieldnames list of strings that denotes which fields from the record param to be added to the record being updated
     * @param rec The record to copy the fields from
     */
    public void copyFieldsFromRecord(List<String> fieldnames, MarcRecord rec) {
        logger.entry(fieldnames, rec);
        try {
            List<MarcField> marcFieldsToAdd = new ArrayList<>();
            fieldnames.forEach((fieldName) -> {
                rec.getFields().forEach((MarcField mf) -> {
                    if (mf.getName().equals(fieldName)) {
                        marcFieldsToAdd.add(mf);
                    }
                });
            });
            record.getFields().addAll(marcFieldsToAdd);

        } finally {
            logger.exit();
        }
    }

    /***
     * This function removes all subfields with given subfield name
     * in fields with the given field name.
     *
     * If this function removes the last subfield of a field, that field is removed as well
     *
     * @param fieldname String name of field to search for
     * @param subfieldname subfield name to remove in  the current record
     */
    public void removeSubfield(String fieldname, String subfieldname) {
        logger.entry(record, fieldname, subfieldname);
        try {
            List<MarcField> marcFieldList = record.getFields();
            for (Iterator<MarcField> mfIter = marcFieldList.listIterator(); mfIter.hasNext(); ) {
                MarcField mf = mfIter.next();
                if (mf.getName().equals(fieldname)) {
                    List<MarcSubField> subfields = mf.getSubfields();
                    for (Iterator<MarcSubField> sfIter = subfields.listIterator(); sfIter.hasNext(); ) {
                        MarcSubField subfield = sfIter.next();
                        if (subfield.getName().equals(subfieldname)) {
                            sfIter.remove();
                        }
                    }
                    // remove field if it has no subfields
                    if (mf.getSubfields().size() == 0) {
                        mfIter.remove();
                    }
                }
            }
        } finally {
            logger.exit();
        }
    }

    /***
     * This function removes all fields with the given field name
     *
     * @param fieldname String name of field to search for
     */
    public void removeField(String fieldname) {
        logger.entry(record, fieldname);
        try {
            List<MarcField> marcFieldList = record.getFields();
            for (Iterator<MarcField> mfIter = marcFieldList.listIterator(); mfIter.hasNext(); ) {
                MarcField mf = mfIter.next();
                if (mf.getName().equals(fieldname)) {
                    mfIter.remove();
                }
            }
        } finally {
            logger.exit();
        }
    }

    /**
     * Function that removes a list of fields from the record
     * @param fieldnames    the list to remove
     */
    public void removeFields(List<String> fieldnames) {
        logger.entry(fieldnames);
        for (String fieldname : fieldnames) {
            removeField(fieldname);
        }
    }

    public void addOrReplaceSubfield(String fieldname, String subfieldname, String value) {
        logger.entry(record, fieldname, subfieldname, value);

        try {
            for (MarcField field : record.getFields()) {
                if (field.getName().equals(fieldname)) {
                    for (MarcSubField subfield : field.getSubfields()) {
                        if (subfield.getName().equals(subfieldname)) {
                            subfield.setValue(value);
                            return;
                        }
                    }

                    field.getSubfields().add(new MarcSubField(subfieldname, value));
                    return;
                }
            }

            MarcField field = new MarcField(fieldname, "00");
            field.getSubfields().add(new MarcSubField(subfieldname, value));

            record.getFields().add(field);
        } finally {
            logger.exit(record);
        }
    }

    public void sort() {
        sort(new SortRecordByName());
    }

    public void sort(Comparator<MarcField> comparator) {
        this.record.getFields().sort(comparator);
    }

    //------------------------------------------------------------------------
    //              Record business values
    //------------------------------------------------------------------------

    /**
     * Marks the record for deletion.
     * <p>
     * That is <code>004r</code> is <code>d</code>
     * </p>
     */
    public void markForDeletion() {
        addOrReplaceSubfield("004", "r", "d");

    }

    /**
     * Updates 001 *d field with current timestamp
     *
     */
    public void setCreationTimestamp() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime dateTime = LocalDateTime.now();

        addOrReplaceSubfield("001", "d", dateTime.format(format));
    }

    /**
     * Updates 001 *c field with current timestamp
     *
     */
    public void setChangedTimestamp() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime dateTime = LocalDateTime.now();

        addOrReplaceSubfield("001", "c", dateTime.format(format));
    }
}
