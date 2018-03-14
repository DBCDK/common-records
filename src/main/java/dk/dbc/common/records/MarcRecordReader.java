/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class can read values from a MarcRecord.
 */
public class MarcRecordReader {
    private static final XLogger logger = XLoggerFactory.getXLogger(MarcRecordReader.class);

    private MarcRecord record;

    public MarcRecordReader(MarcRecord record) {
        this.record = record;
    }

    /**
     * Returns the first occurrence of the value of a field and subfield.
     * <p>
     *
     * @param fieldName    The name of the field.
     * @param subfieldName Name of the subfield.
     * @return The value of the subfield if found, <code>null</code> otherwise.
     */
    public String getValue(String fieldName, String subfieldName) {
        logger.entry(fieldName, subfieldName);
        String result = null;
        List<MarcField> fields;
        try {
            fields = getFieldStream(fieldName);

            for (MarcField field : fields) {
                if (field.getName().equals(fieldName)) {
                    result = new MarcFieldReader(field).getValue(subfieldName);
                    if (result != null) {
                        return result;
                    }
                }
            }

            return result;
        } finally {
            logger.exit(result);
        }
    }

    public Boolean hasField(String fieldName) {
        List<MarcField> fields = getFieldStream(fieldName);

        return fields.size() > 0;
    }

    public MarcField getField(String fieldName) {
        MarcField result = null;

        List<MarcField> fields = getFieldStream(fieldName);

        if (fields != null && fields.size() > 0) {
            result = fields.get(0);
        }

        return result;
    }

    public List<MarcField> getFieldAll(String fieldName) {
        return getFieldStream(fieldName);
    }

    /**
     * Private helper function for iterating over the fields in the record and only returning the matching fields
     *
     * @param fieldName Name of the fields with want to retrieve
     * @return List of fields with the fieldName
     */
    private List<MarcField> getFieldStream(String fieldName) {
        return record.getFields().stream()
                .filter(f -> fieldName.equals(f.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Return true if the field and subfield exist on the record.
     * Otherwise returns false
     *
     * @param fieldName    The name of the field.
     * @param subfieldName Name of the subfield.
     * @return True if fieldName and subfieldName exists at least once in the record
     */
    public Boolean hasSubfield(String fieldName, String subfieldName) {
        logger.entry(fieldName, subfieldName);
        Boolean result = false;
        try {
            for (MarcField field : getFieldStream(fieldName)) {
                for (MarcSubField subfield : field.getSubfields()) {
                    if (subfield.getName().equals(subfieldName)) {
                        result = true;
                    }
                }
            }
            return result;
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Returns the content of all subfields that match a subfield name.
     *
     * @param fieldName    The name of the field.
     * @param subfieldName Name of the subfield.
     * @return The values as a List that was found. An empty list is returned
     * if no field or subfield matches the arguments.
     */
    public List<String> getValues(String fieldName, String subfieldName) {
        logger.entry(fieldName, subfieldName);
        List<String> result = new ArrayList<>();
        try {
            for (MarcField field : getFieldStream(fieldName)) {
                result.addAll(new MarcFieldReader(field).getValues(subfieldName));
            }
            return result;
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Checks if a subfield in the record contains a value.
     * <p>
     * The value of a subfield is matched with <code>equals</code>, so it will have to
     * match exactly.
     * </p>
     *
     * @param fieldName    The name of the field.
     * @param subfieldName Name of the subfield.
     * @param value        The value to check for.
     * @return <code>true</code> if a subfield with name <code>subfieldName</code> in a field
     * with <code>fieldName</code> contains the value <code>value</code>. <code>false</code>
     * otherwise.
     */
    public Boolean hasValue(String fieldName, String subfieldName, String value) {
        logger.entry(fieldName, subfieldName);

        try {
            for (MarcField field : getFieldStream(fieldName)) {
                if (new MarcFieldReader(field).hasValue(subfieldName, value)) {
                    return true;
                }
            }

            return false;
        } finally {
            logger.exit();
        }
    }

    public Boolean matchValue(String fieldName, String subfieldName, String value) {
        logger.entry(fieldName, subfieldName);

        try {
            for (MarcField field : getFieldStream(fieldName)) {
                for (MarcSubField subfield : field.getSubfields()) {
                    if (subfield.getName().equals(subfieldName) && subfield.getValue().matches(value)) {
                        return true;
                    }
                }
            }

            return false;
        } finally {
            logger.exit();
        }
    }

    /**
     * This function looks for field and subfield with the given names, and if subfield is found then pattern matching
     * is perform. All matching subfields are returned as a list - if no matches then empty list is returned
     *
     * @param fieldName    The field name
     * @param subfieldName The subfield name
     * @param p            Pattern
     * @return List of matchers (empty if no matches)
     */
    public List<Matcher> getSubfieldValueMatchers(String fieldName, String subfieldName, Pattern p) {
        logger.entry(fieldName, subfieldName, p);
        final List<Matcher> result = new ArrayList<>();

        try {
            for (MarcField field : getFieldStream(fieldName)) {
                for (MarcSubField subfield : field.getSubfields()) {
                    if (subfield.getName().equals(subfieldName)) {
                        Matcher m = p.matcher(subfield.getValue());
                        if (m.find()) {
                            result.add(m);
                        }
                    }
                }
            }

            return result;
        } finally {
            logger.exit();
        }
    }

    /**
     * Returns id of an alias record.
     * <p>
     * The id in <code>002a</code>.
     * </p>
     *
     * @return If an alias id is found it is returned, <code>null</code> otherwise.
     */
    public List<String> getCentralAliasIds() {
        logger.entry();
        List<String> result = null;
        try {
            return result = getValues("002", "a");
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Return a list of pairs of 002 b and c fields
     * <p>
     * Used for getting all sets of 002 b and c values from the current record
     * </p>
     *
     * @return list of pairs of 002 b and c values
     */
    public List<HashMap<String, String>> getDecentralAliasIds() {
        logger.entry();
        List<HashMap<String, String>> result = new ArrayList<>();
        try {
            for (MarcField field : getFieldStream("002")) {
                String bValue = null;
                String cValue = null;

                for (MarcSubField subfield : field.getSubfields()) {
                    if (subfield.getName().equals("b")) {
                        bValue = subfield.getValue();
                    }

                    if (subfield.getName().equals("c")) {
                        cValue = subfield.getValue();
                    }

                    if (bValue != null && cValue != null) {
                        HashMap<String, String> bcValues = new HashMap<>();
                        bcValues.put("b", bValue);
                        bcValues.put("c", cValue);

                        result.add(bcValues);
                    }
                }
            }
            return result;
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Returns true if the record are owned by DBC otherwise false
     *
     * @return If content of 996*a is DBC or RET, true is returned, false otherwise.
     */
    public boolean isDBCRecord() {
        logger.entry();
        String result = null;
        try {
            result = getValue("996", "a");
            if (result != null) {
                if (result.equals("DBC") || result.equals("RET")) {
                    return true;
                }
            }
            return false;
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Returns id of this record.
     * <p>
     * The id in <code>001a</code>.
     * </p>
     *
     * @return If an id is found it is returned, <code>null</code> otherwise.
     */
    public String getRecordId() {
        logger.entry();

        String result = null;
        try {
            return result = getValue("001", "a");
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Returns agency id of this record.
     * <p>
     * The id in <code>001b</code>.
     * </p>
     *
     * @return If a id is found it is returned, <code>null</code> otherwise.
     */
    public String getAgencyId() {
        logger.entry();
        String result = null;
        try {
            return result = getValue("001", "b");
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Returns agency id of this record.
     * <p>
     * The id in <code>001b</code>.
     * </p>
     *
     * @return If an id is found it is returned, <code>null</code> otherwise.
     */
    @Deprecated
    public Integer getAgencyIdAsInteger() {
        logger.entry();
        int result = 0;
        try {
            String id = getAgencyId();
            return result = Integer.valueOf(id, 10);
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Returns agency id of this record.
     * <p>
     * The id in <code>001b</code>.
     * </p>
     *
     * @return The value of 001 *b
     */
    public int getAgencyIdAsInt() {
        logger.entry();
        int result = 0;
        try {
            String id = getAgencyId();
            if (id != null) {
                result = Integer.valueOf(id, 10);
            } else {
                result = 0;
            }

            return result;
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Checks if this record is marked for deletion.
     * <p>
     * A deletion mark satisfies: 004r = d
     * </p>
     */
    public boolean markedForDeletion() {
        logger.entry();
        Boolean result = null;
        try {
            return result = "d".equals(getValue("004", "r"));
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Returns id of a parent record, that a record points to.
     * <p>
     * The parent id to located in <code>014a</code>.
     * If there is a field 014 either without a subfield x or if the content of subfield x is ANM
     * then the record is part of a volume/section/head structure.
     * </p>
     *
     * @return If a parent id is found it is returned, <code>null</code>
     * otherwise.
     */
    public String getParentRecordId() {
        logger.entry();
        String result = null;
        try {
            String field014x;
            field014x = getValue("014", "x");
            if (field014x == null || "ANM".equals(field014x)) {
                result = getValue("014", "a");
            }
            return result;
        } finally {
            logger.exit(result);
        }
    }

    /**
     * If 014 *x = ANM then 014 *a record has agency 870970. If 014 *x is either missing or has another value than ANM,
     * then the record in 014 *a has the same agency as this record (001 *b)
     * <p>
     * Note: ANM = Anmeldelse = Review
     * Note: An article of type review always point to a common record
     *
     * @return The agencyId of the 014 *a record
     */
    public String getParentAgencyId() {
        logger.entry();
        String result = null;
        try {
            String field014x;
            field014x = getValue("014", "x");
            if (field014x != null && "ANM".equals(field014x)) {
                result = "870970";
            } else {
                result = getAgencyId();
            }

            return result;
        } finally {
            logger.exit(result);
        }
    }

    @Deprecated
    public Integer getParentAgencyIdAsInteger() {
        logger.entry();
        Integer result = null;
        try {
            String id = getParentAgencyId();
            return result = Integer.valueOf(id, 10);
        } finally {
            logger.exit(result);
        }
    }

    public int getParentAgencyIdAsInt() {
        logger.entry();
        Integer result = null;
        try {
            String id = getParentAgencyId();
            if (id != null) {
                result = Integer.valueOf(id, 10);
            } else {
                result = 0;
            }

            return result;
        } finally {
            logger.exit(result);
        }
    }
}
