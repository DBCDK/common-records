package dk.dbc.common.records;

import dk.dbc.marc.binding.DataField;
import dk.dbc.marc.binding.Field;
import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.binding.SubField;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * This class can write values to a MarcRecord.
 */
public class MarcRecordWriter {
    private final MarcRecord marcRecord;

    public MarcRecordWriter(MarcRecord marcRecord) {
        this.marcRecord = marcRecord;
    }

    public MarcRecord getMarcRecord() {
        return marcRecord;
    }

    public void addFieldSubfield(String fieldname, char subfieldname, String value) {
        final DataField field = new DataField(fieldname, "00");
        field.getSubFields().add(new SubField(subfieldname, value));
        marcRecord.getFields().add(field);
    }

    public void copyFieldFromRecord(String fieldname, MarcRecord rec) {
        final List<DataField> marcFieldsToAdd = new ArrayList<>();
        rec.getFields(DataField.class).forEach((DataField mf) -> {
            if (mf.getTag().equals(fieldname)) {
                marcFieldsToAdd.add(mf);
            }
        });
        marcRecord.getFields().addAll(marcFieldsToAdd);
    }

    /***
     * Function to copy a list of fields from a record. Notice the fields will be appended,
     * even if the record being updated already contains the fields
     *
     * @param fieldnames list of strings that denotes which fields from the record param to be added to the record being updated
     * @param rec The record to copy the fields from
     */
    public void copyFieldsFromRecord(List<String> fieldnames, MarcRecord rec) {
        final List<DataField> marcFieldsToAdd = new ArrayList<>();
        fieldnames.forEach(fieldName -> rec.getFields(DataField.class).forEach((DataField mf) -> {
            if (mf.getTag().equals(fieldName)) {
                marcFieldsToAdd.add(mf);
            }
        }));
        marcRecord.getFields().addAll(marcFieldsToAdd);
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
    public void removeSubfield(String fieldname, char subfieldname) {
        final List<DataField> marcFieldList = marcRecord.getFields(DataField.class);
        for (Iterator<DataField> mfIter = marcFieldList.listIterator(); mfIter.hasNext(); ) {
            DataField mf = mfIter.next();
            if (mf.getTag().equals(fieldname)) {
                List<SubField> subfields = mf.getSubFields();
                subfields.removeIf(subfield -> subfieldname == subfield.getCode());
                // remove field if it has no subfields
                if (mf.getSubFields().isEmpty()) {
                    mfIter.remove();
                }
            }
        }
    }

    /***
     * This function removes all fields with the given field name
     *
     * @param fieldname String name of field to search for
     */
    public void removeField(String fieldname) {
        marcRecord.removeField(fieldname);
    }

    /**
     * Function that removes a list of fields from the record
     *
     * @param fieldnames the list to remove
     */
    public void removeFields(List<String> fieldnames) {
        for (String fieldname : fieldnames) {
            removeField(fieldname);
        }
    }

    public void addOrReplaceSubField(String tag, char code, String value) {
        final Optional<DataField> dataField = marcRecord.getField(DataField.class, MarcRecord.hasTag(tag));

        if (dataField.isPresent()) {
            dataField.get().addOrReplaceFirstSubField(new SubField(code, value));
        } else {
            marcRecord.addField(new DataField(tag, "0")
                    .addSubField(new SubField(code, value)));
        }
    }

    public void sort() {
        sort(new SortFieldByTag());
    }

    public void sort(Comparator<Field> comparator) {
        this.marcRecord.getFields().sort(comparator);
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
        addOrReplaceSubField("004", 'r', "d");
    }

    /**
     * Updates 001 *d field with current timestamp
     */
    public void setCreationTimestamp() {
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        final LocalDateTime dateTime = LocalDateTime.now();

        addOrReplaceSubField("001", 'd', dateTime.format(format));
    }

    /**
     * Updates 001 *c field with current timestamp
     */
    public void setChangedTimestamp() {
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        final LocalDateTime dateTime = LocalDateTime.now();

        addOrReplaceSubField("001", 'c', dateTime.format(format));
    }
}
