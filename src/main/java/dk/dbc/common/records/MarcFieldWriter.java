/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;


import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.Iterator;
import java.util.List;

public class MarcFieldWriter {
    private static final XLogger logger = XLoggerFactory.getXLogger(MarcRecordReader.class);
    MarcField field;

    public MarcFieldWriter(MarcField marcField) {
        this.field = marcField;
    }

    public MarcField getField() {
        return field;
    }

    public void addOrReplaceSubfield(String subfieldname, String value) {
        logger.entry(subfieldname, value);

        try {
            for (MarcSubField subfield : field.getSubfields()) {
                if (subfield.getName().equals(subfieldname)) {
                    subfield.setValue(value);
                    return;
                }
            }

            field.getSubfields().add(new MarcSubField(subfieldname, value));
        } finally {
            logger.exit();
        }
    }

    public void removeSubfield(String subfieldName) {
        logger.entry(subfieldName);
        try {
            List<MarcSubField> subfieldList = field.getSubfields();
            subfieldList.removeIf(msf -> msf.getName().equals(subfieldName));
        } finally {
            logger.exit();
        }
    }

}
