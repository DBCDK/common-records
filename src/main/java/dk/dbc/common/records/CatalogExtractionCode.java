/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This class operates on the 032 field
 */
public class CatalogExtractionCode {
    private static final XLogger logger = XLoggerFactory.getXLogger(CatalogExtractionCode.class);
    public static final List<String> listOfCatalogCodes = Arrays.asList("DBF", "DLF", "DBI", "DMF", "DMO", "DPF", "BKM", "GBF", "GMO", "GPF", "FPF", "DBR", "UTI");
    private static final String temporaryDate = "999999";

    /**
     * This function checks whether the given record is under production
     * <p>
     * NOTE: Production means the catalogization is underway and the record is not yet publicly available
     * <p>
     * So if there is a production date, and that production date is in the future then this function returns true
     * If there is no production date or if that date is in the past false is returned
     * The date 999999 is a temporary date because the exact date is not yet known. 999999 is treated as a future production date
     * If there is at least one production release date in the past then the record is no longer under production (return false)
     *
     * @param record The input record
     * @return <code>true</code> if publishing date is in the future, otherwise <code>false</code>
     */
    public static boolean isUnderProduction(MarcRecord record) {
        return isUnderProduction(record, listOfCatalogCodes);
    }

    public static boolean isUnderProduction(MarcRecord record, List<String> listOfCatalogCodes) {
        logger.entry(record);

        try {
            boolean hasExtractionDateInTheFuture = false;
            final MarcRecordReader reader = new MarcRecordReader(record);
            final MarcField field032 = reader.getField("032");

            if (field032 != null) {
                logger.info("Found 032 field: {}", field032);
                // 032 contains both *a and *x fields but for this calculation they are treated the same way
                for (MarcSubField subfield : field032.getSubfields()) {
                    final String value = subfield.getValue();
                    logger.info("Checking {} for production date", value);
                    if (hasPublishingDate(value, listOfCatalogCodes)) {
                        if (hasFuturePublishingDate(value)) {
                            logger.info("Found future extraction date");
                            hasExtractionDateInTheFuture = true;
                        } else {
                            logger.info("Extraction date in the past was found so returning false");
                            return false;
                        }
                    }
                }
            }

            // If we get to this point there has not been found an extraction date in the past
            if (hasExtractionDateInTheFuture) {
                logger.info("Extraction date was found in the future but not in the past - therefor this record is under production");
                return true;
            } else {
                logger.info("Neither past nor future extraction date was found - record is not under production");
                return false;
            }
        } finally {
            logger.exit();
        }
    }

    /**
     * This function checks whether the given record has been published
     * A record is publish if it has a publishing date in the past
     *
     * @param record The input record
     * @return <code>true</code> if the record has a publishing date is in the past, otherwise <code>false</code>
     */
    public static boolean isPublished(MarcRecord record) {
        return isPublished(record, listOfCatalogCodes);
    }

    public static boolean isPublished(MarcRecord record, List<String> listOfCatalogCodes) {
        logger.entry(record);

        try {
            final MarcRecordReader reader = new MarcRecordReader(record);
            final MarcField field032 = reader.getField("032");

            if (field032 != null) {
                logger.info("Found 032 field: {}", field032);
                // 032 contains both *a and *x fields but for this calculation they are treated the same way
                for (MarcSubField subfield : field032.getSubfields()) {
                    final String value = subfield.getValue();
                    logger.info("Checking {} for production date", value);
                    if (hasPublishingDate(value, listOfCatalogCodes)) {
                        if (!hasFuturePublishingDate(value)) {
                            // Since the publishing date is not in the future it must be in the past
                            logger.info("Extraction date in the past was found so returning true");
                            return true;
                        }
                    }
                }
            }

            return false;
        } finally {
            logger.exit();
        }
    }

    /**
     * This function takes the value of a subfield and determines whether that value should be treated as an
     * extraction date.
     *
     * @param value Value of the subfield
     * @return <code>true</code> if:
     * - The input is exactly 9 chars long
     * - Starts with catalog code that matches a limited set of catalog codes
     * - Ends with either 999999 or date format
     * <p>
     * Otherwise <code>false</code> is returned
     */
    static boolean hasPublishingDate(String value) {
        return hasPublishingDate(value, listOfCatalogCodes);
    }

    static boolean hasPublishingDate(String value, List<String> listOfCatalogCodes) {
        logger.entry(value);

        boolean result = false;
        final String patternDate = "^(\\d){6}"; // Matches 6 numbers

        try {
            if (value.length() == 9) {
                final String catalogCode = value.substring(0, 3);

                if (listOfCatalogCodes.contains(catalogCode)) {
                    final String extractionDate = value.substring(3, 9);

                    result = extractionDate.equals(temporaryDate) || extractionDate.matches(patternDate);
                }
            }

            return result;
        } finally {
            logger.exit(result);
        }
    }

    /**
     * Given a subfield value that is assumed to contain an extraction date plus a date this function extracts the
     * date from the subfield value and compares it to the input date.
     *
     * @param value Subfield value
     * @return <code>true</code> if the value contains a date later that the date input - otherwise <code>false</code>
     */
    static boolean hasFuturePublishingDate(String value) {
        logger.entry(value);
        boolean result = false;
        final LocalDate date = LocalDate.now();

        try {
            final String dateStr = value.substring(3, 9);

            if (dateStr.equals(temporaryDate)) {
                result = true;
            } else {
                final int year = Integer.parseInt(value.substring(3, 7));
                final int weekNo = Integer.parseInt(value.substring(7, 9));

                final Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.WEEK_OF_YEAR, weekNo - 1);
                cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

                final Instant instant = cal.toInstant();
                final ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
                final LocalDate dateProduction = zonedDateTime.toLocalDate();

                result = !date.until(dateProduction).isNegative();
            }

            return result;
        } finally {
            logger.exit(result);
        }
    }

}
