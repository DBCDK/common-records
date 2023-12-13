package dk.dbc.common.records;

import java.util.Arrays;

public class MarcXMimeTypeMerger {

    public static boolean canMerge(String originalMimeType, String enrichmentMimeType) {
        return Arrays.asList(MarcXChangeMimeType.MARCXCHANGE,
                MarcXChangeMimeType.ARTICLE,
                MarcXChangeMimeType.AUTHORITY,
                MarcXChangeMimeType.LITANALYSIS,
                MarcXChangeMimeType.MATVURD,
                MarcXChangeMimeType.HOSTPUB,
                MarcXChangeMimeType.SIMPLE).contains(originalMimeType) && MarcXChangeMimeType.ENRICHMENT.equals(enrichmentMimeType);
    }

    public static String mergedMimeType(String originalMimeType, String enrichmentMimeType) {
        if (Arrays.asList(MarcXChangeMimeType.MARCXCHANGE,
                MarcXChangeMimeType.ARTICLE,
                MarcXChangeMimeType.AUTHORITY,
                MarcXChangeMimeType.LITANALYSIS,
                MarcXChangeMimeType.MATVURD,
                MarcXChangeMimeType.HOSTPUB,
                MarcXChangeMimeType.SIMPLE).contains(originalMimeType) && MarcXChangeMimeType.ENRICHMENT.equals(enrichmentMimeType)) {
            return originalMimeType;
        } else {
            throw new IllegalStateException("Cannot figure out mimetype of: " + originalMimeType + "&" + enrichmentMimeType);
        }
    }
}
