/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records.utils;

import dk.dbc.common.records.MarcRecord;

import java.util.Base64;

public class LogUtils {

    public static String base64Encode(MarcRecord record) {
        if (record == null) {
            return "null";
        } else {
            String recordString = record.toString();

            byte[] encodedBytes = Base64.getEncoder().encode(recordString.getBytes());

            // This format is a compromise between human readable format and script readable format
            return "BASE64 " + new String(encodedBytes);
        }
    }

}
