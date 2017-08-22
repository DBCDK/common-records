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
 * This class can read values from a MarcField.
 */
public class MarcFieldReader {
    private static final XLogger logger = XLoggerFactory.getXLogger(MarcFieldReader.class);

    private MarcField field;

    public MarcFieldReader(MarcField field) {
        this.field = field;
    }

    /**
     * Returns the first occurrence of the value of a subfield.
     *
     * @param subfieldName Name of the subfield.
     * @return The value of the subfield if found, <code>null</code> otherwise.
     */
    public String getValue(String subfieldName) {
        logger.entry(subfieldName);

        String result = null;
        try {
            for (MarcSubField sf : this.field.getSubfields()) {
                if (sf.getName().equals(subfieldName)) {
                    return result = sf.getValue();
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
     * @param subfieldName Name of the subfield.
     * @return The values as a List that was found. An empty list is returned
     * if no subfields matches the arguments.
     */
    List<String> getValues(String subfieldName) {
        logger.entry(subfieldName);

        List<String> result = new ArrayList<>();
        try {
            for (MarcSubField sf : this.field.getSubfields()) {
                if (sf.getName().equals(subfieldName)) {
                    result.add(sf.getValue());
                }
            }

            return result;
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Checks if a subfield in the field contains a value.
     * <p>
     * The value of a subfield is matched with <code>equals</code>, so it will have to
     * match exactly.
     * </p>
     *
     * @param subfieldName Name of the subfield.
     * @param value   The value to check for.
     * @return <code>true</code> if a subfield with name <code>subfieldName</code> contains the value
     * <code>value</code>. <code>false</code> otherwise.
     */
    boolean hasValue(String subfieldName, String value) {
        logger.entry(subfieldName, value);

        Boolean result = null;
        try {
            result = false;
            for (MarcSubField sf : this.field.getSubfields()) {
                if (sf.getName().equals(subfieldName) && sf.getValue().equals(value)) {
                    return result = true;
                }
            }

            return result;
        } finally {
            logger.exit(result);
        }
    }

    public boolean hasSubfield(String subfieldName) {
        logger.entry(subfieldName);

        Boolean result = null;
        try {
            result = false;
            for (MarcSubField sf : this.field.getSubfields()) {
                if (sf.getName().equals(subfieldName)) {
                    return result = true;
                }
            }

            return result;
        } finally {
            logger.exit(result);
        }
    }

}
