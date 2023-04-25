package dk.dbc.common.records.utils;

import dk.dbc.marc.binding.MarcRecord;

import java.util.Base64;

public class LogUtils {

    private LogUtils() {

    }

    public static String base64Encode(MarcRecord marcRecord) {
        if (marcRecord == null) {
            return "null";
        } else {
            String recordString = marcRecord.toString();

            byte[] encodedBytes = Base64.getEncoder().encode(recordString.getBytes());

            // This format is a compromise between human-readable format and script readable format
            return "BASE64 " + new String(encodedBytes);
        }
    }

}
