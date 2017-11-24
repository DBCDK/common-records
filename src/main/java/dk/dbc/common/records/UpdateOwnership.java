/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UpdateOwnership {
    private static final XLogger logger = XLoggerFactory.getXLogger(UpdateOwnership.class);

    /**
     * Merges ownership in field 996 from record and currentRecord.
     * The result is written to field 996 in record.
     * <p>
     * There are three scenarios to take into account:
     * - The owner (996 *a) on both records are the same ->
     * copy 996 from current to new record
     * - Owners are different, and existing record has no *o field ->
     * set 996 *a = new owner
     * add 996 *o = current owner
     * add 996 *m = current owner
     * - Owners are different and existing record has an *o field ->
     * set 996 *a = new owner
     * add 996 *m = current owner unless already added
     * set 996 *o = original owner
     * <p>
     * Some info about 996:
     * *a is current owner
     * *o is the original owner (should never be changed)
     * *m (repeatable) is the previous owners - m is unordered
     *
     * @param newRecord     The record to merge and write the result to.
     * @param currentRecord currentRecord The current record to merge with.
     * @return MarcRecord The record with the merged result in record.
     */
    public static MarcRecord mergeRecord(MarcRecord newRecord, MarcRecord currentRecord) {
        logger.entry(newRecord, currentRecord);

        try {
            if (newRecord == null || currentRecord == null) {
                return newRecord;
            }

            MarcRecordReader currentRecordReader = new MarcRecordReader(currentRecord);

            MarcRecordReader newRecordReader = new MarcRecordReader(newRecord);
            MarcRecordWriter newRecordWriter = new MarcRecordWriter(newRecord);

            String currentOwner = currentRecordReader.getValue("996", "a");
            String newOwner = newRecordReader.getValue("996", "a");

            logger.info("currentOwner: {}", currentOwner);
            logger.info("newOwner: {}", newOwner);

            newRecordWriter.removeField("996");

            // A 996 field will most likely exist on the new record but we check anyway.
            // Field 996 is assumed to always exist on the current record as that field is mandatory.
            // If the new and current owners are the same, simply copy the 996 field from current record.
            if (newOwner == null || newOwner.equals(currentOwner)) {
                newRecord.getFields().add(new MarcField(currentRecordReader.getField("996")));
            } else {
                MarcField ownerField = new MarcField("996", "00");

                // Handle 996 *a
                ownerField.getSubfields().add(new MarcSubField("a", newOwner));

                // Handle 996 *o
                if (currentRecordReader.hasSubfield("996", "o")) {
                    String originalOwner = currentRecordReader.getValue("996", "o");

                    ownerField.getSubfields().add(new MarcSubField("o", originalOwner));
                } else {
                    ownerField.getSubfields().add(new MarcSubField("o", currentOwner));
                }

                // Handle 996 *m
                List<String> previousOwners = createListOfPreviousOwners(currentRecordReader);

                for (String previousOwner : previousOwners) {
                    ownerField.getSubfields().add(new MarcSubField("m", previousOwner));
                }

                newRecord.getFields().add(ownerField);
            }

            newRecordWriter.sort();

            return newRecord;
        } finally {
            logger.exit(newRecord);
        }
    }

    private static List<String> createListOfPreviousOwners(MarcRecordReader reader) {
        List<String> owners;

        if (reader.hasSubfield("996", "m")) {
            owners = reader.getValues("996", "m");
        } else {
            owners = new ArrayList<>();
        }

        String currentOwner = reader.getValue("996", "a");

        // Current owner should be added to the list of previous owners unless already listed
        if (owners.indexOf(currentOwner) == -1) {
            owners.add(currentOwner);
        }

        return owners;
    }

}