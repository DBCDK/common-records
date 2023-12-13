package dk.dbc.common.records;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public class FieldRules {

    public static final Set<String> IMMUTABLE_DEFAULT = new HashSet<>(Arrays.asList("010", "020", "990", "991", "996"));
    public static final String VALID_REGEX_DANMARC2 = "\\d{3}";

    public static final Map<String, Set<String>> OVERWRITE_DEFAULT = Map.ofEntries(
            entry("001", new HashSet<>(List.of("001"))),
            entry("004", new HashSet<>(List.of("004"))),
            entry("005", new HashSet<>(List.of("005"))),
            entry("006", new HashSet<>(List.of("006"))),
            entry("008", new HashSet<>(Arrays.asList("008", "009", "038", "039", "100", "110", "239", "245", "652", "654"))),
            entry("009", new HashSet<>(Arrays.asList("008", "009", "038", "039", "100", "110", "239", "245", "652", "654"))),
            entry("013", new HashSet<>(List.of("013"))),
            entry("014", new HashSet<>(List.of("014"))),
            entry("017", new HashSet<>(List.of("017"))),
            entry("035", new HashSet<>(List.of("035"))),
            entry("036", new HashSet<>(List.of("036"))),
            entry("038", new HashSet<>(Arrays.asList("008", "009", "038", "039", "100", "110", "239", "245", "652", "654"))),
            entry("039", new HashSet<>(Arrays.asList("008", "009", "038", "039", "100", "110", "239", "245", "652", "654"))),
            entry("100", new HashSet<>(Arrays.asList("008", "009", "038", "039", "100", "110", "239", "245", "652", "654"))),
            entry("110", new HashSet<>(Arrays.asList("008", "009", "038", "039", "100", "110", "239", "245", "652", "654"))),
            entry("239", new HashSet<>(Arrays.asList("008", "009", "038", "039", "100", "110", "239", "245", "652", "654"))),
            entry("240", new HashSet<>(List.of("240"))),
            entry("243", new HashSet<>(List.of("243"))),
            entry("245", new HashSet<>(Arrays.asList("008", "009", "038", "039", "100", "110", "239", "245", "652", "654"))),
            entry("300", new HashSet<>(List.of("300"))),
            entry("652", new HashSet<>(Arrays.asList("008", "009", "038", "039", "100", "110", "239", "245", "652", "654"))),
            entry("654", new HashSet<>(Arrays.asList("008", "009", "038", "039", "100", "110", "239", "245", "652", "654")))
    );

    private final Pattern validRegex;
    private final Set<String> invalid;
    private final Set<String> immutable;
    private final Set<String> remove;
    private final Map<String, Set<String>> overwriteCollections;

    /**
     * Default setup
     */
    public FieldRules() {
        this.invalid = new HashSet<>();
        this.immutable = IMMUTABLE_DEFAULT;
        this.remove = new HashSet<>();
        this.overwriteCollections = OVERWRITE_DEFAULT;
        this.validRegex = Pattern.compile(VALID_REGEX_DANMARC2, Pattern.MULTILINE);
    }

    /**
     * @param immutable  fields that can't be modified
     * @param overwrite  fields that are replacing (groups (of tags separated by
     *                   space) separated by ;)
     * @param invalid    fields that should always be removed
     * @param validRegex regex that tag must match to be considered valid
     */
    public FieldRules(Set<String> immutable, Map<String, Set<String>> overwrite, Set<String> invalid, String validRegex) {
        this.invalid = invalid;
        this.immutable = immutable;
        this.remove = new HashSet<>();
        this.overwriteCollections = overwrite;
        this.validRegex = Pattern.compile(validRegex, Pattern.MULTILINE);
    }

    /**
     * class for ruleset for an individual marcx merge
     * <p>
     * needs to have called
     * {@link #registerLocalField(java.lang.String) registerLocalField} for
     * every field to know if a
     * {@link #removeField(java.lang.String) removeField} should return true or
     * false
     */
    public class RuleSet {

        private final Set<String> immutable = new HashSet<>();
        private final Set<String> remove = new HashSet<>();

        private RuleSet(Set<String> immutable, Set<String> remove) {
            this.immutable.addAll(immutable);
            this.remove.addAll(remove);
        }

        /**
         * Register the presence of a local field, and all the fields in its
         * collection
         *
         * @param field Name of the field
         */
        public void registerLocalField(String field) {
            if (overwriteCollections.containsKey(field)) {
                remove.addAll(overwriteCollections.get(field));
            }
        }

        /**
         * The presence of this field is not wanted
         *
         * @param field            Name of the field
         * @param includeAllFields Do not restrict to validRegex
         * @return boolean
         */
        public boolean invalidField(String field, boolean includeAllFields) {
            return !includeAllFields && !validRegex.matcher(field).matches() || invalid.contains(field);
        }

        /**
         * Remove this field from the common data
         *
         * @param field Name of the field to remove, e.g. 065
         * @return boolean
         */
        public boolean removeField(String field) {
            return remove.contains(field);
        }

        /**
         * This field cannot be overwritten by a local value
         *
         * @param field Name of the field to keep, e.g. 065
         * @return boolean
         */
        public boolean immutableField(String field) {
            return immutable.contains(field);
        }

        @Override
        public String toString() {
            return "RuleSet{" +
                    "immutable=" + immutable +
                    ", remove=" + remove +
                    '}';
        }
    }

    /**
     * @return Ruleset
     */
    public RuleSet newRuleSet() {
        return new RuleSet(immutable, remove);
    }


}
