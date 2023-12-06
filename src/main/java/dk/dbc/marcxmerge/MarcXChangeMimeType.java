package dk.dbc.marcxmerge;

/**
 * @author DBC {@literal <dbc.dk>}
 */
public class MarcXChangeMimeType {

    public static final String MARCXCHANGE = "text/marcxchange";
    public static final String ARTICLE = "text/article+marcxchange";
    public static final String AUTHORITY = "text/authority+marcxchange";
    public static final String LITANALYSIS = "text/litanalysis+marcxchange";
    public static final String MATVURD = "text/matvurd+marcxchange";
    public static final String HOSTPUB = "text/hostpub+marcxchange";
    public static final String SIMPLE = "text/simple+marcxchange";

    public static final String ENRICHMENT = "text/enrichment+marcxchange";
    public static final String UNKNOWN = "unknown/unknown";

    public static boolean isMarcXChange(String mimetype) {
        if (mimetype == null) {
            return false;
        }
        switch (mimetype) {
            case MARCXCHANGE:
            case ARTICLE:
            case AUTHORITY:
            case LITANALYSIS:
            case MATVURD:
            case HOSTPUB:
            case SIMPLE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isEnrichment(String mimetype) {
        if (mimetype == null) {
            return false;
        }
        return ENRICHMENT.equals(mimetype);
    }

    public static boolean isArticle(String mimetype) {
        if (mimetype == null) {
            return false;
        }
        return ARTICLE.equals(mimetype);
    }

    public static boolean isAuthority(String mimetype) {
        if (mimetype == null) {
            return false;
        }
        return AUTHORITY.equals(mimetype);
    }

    public static boolean isLitAnalysis(String mimetype) {
        if (mimetype == null) {
            return false;
        }
        return LITANALYSIS.equals(mimetype);
    }

    public static boolean isMatVurd(String mimetype) {
        if (mimetype == null) {
            return false;
        }
        return MATVURD.equals(mimetype);
    }

    public static boolean isHostPub(String mimetype) {
        if (mimetype == null) {
            return false;
        }
        return HOSTPUB.equals(mimetype);
    }

    public static boolean isSimple(String mimetype) {
        if (mimetype == null) {
            return false;
        }
        return SIMPLE.equals(mimetype);
    }
}
