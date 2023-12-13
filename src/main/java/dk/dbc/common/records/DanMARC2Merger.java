package dk.dbc.common.records;

import dk.dbc.marc.binding.DataField;
import dk.dbc.marc.binding.Leader;
import dk.dbc.marc.binding.MarcRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class DanMARC2Merger {
    private static final String DEFAULT_NAME = "default";
    private final FieldRules fieldRulesIntermediate;
    private final String name;

    /**
     * Default constructor, sets up FieldRules according to std rules
     */
    public DanMARC2Merger() {
        this.fieldRulesIntermediate = new FieldRules();
        this.name = DEFAULT_NAME;
    }


    /**
     * Constructor for custom FieldRules
     *
     * @param fieldRulesIntermediate ruleset for merging records
     */
    public DanMARC2Merger(FieldRules fieldRulesIntermediate, String name) {
        this.fieldRulesIntermediate = fieldRulesIntermediate;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Merge two MarcRecord objects according to the rules defined in the
     * constructor
     *
     * @param common           the base of the result
     * @param local            the additional data
     * @param includeAllFields should all fields be included
     * @return a merged MarcRecord
     */
    public MarcRecord merge(MarcRecord common, MarcRecord local, boolean includeAllFields) {
        final FieldRules.RuleSet ruleSet = fieldRulesIntermediate.newRuleSet();
        final MarcRecord result = new MarcRecord()
                .setLeader(new Leader().setData(common.getLeader().getData()))
                .setType(common.getType())
                .setFormat(common.getFormat());
        final List<DataField> localDataFields = local.getFields(DataField.class);
        final List<DataField> commonDataFields = common.getFields(DataField.class);

        removeRegisterAndImportLocalFields(localDataFields, ruleSet, includeAllFields);
        removeAndImportCommonFields(commonDataFields, ruleSet);

        final List<DataField> dataFields = mergeCommonAndLocalIntoTarget(localDataFields, commonDataFields);

        result.getFields().addAll(dataFields);

        return result;
    }

    /**
     * removes unwanted nodes from localFields
     * <p>
     * removes nodes from localDom
     * <p>
     * imports nodes into targetDom
     * <p>
     * registers tags in ruleSet, selecting which to remove from commonFields
     *
     * @param localDataFields list of DataFields from the local record
     * @param ruleSet the rule set to use
     * @param includeAllFields should all fields be included
     */
    private static void removeRegisterAndImportLocalFields(List<DataField> localDataFields, FieldRules.RuleSet ruleSet, boolean includeAllFields) {
        for (ListIterator<DataField> it = localDataFields.listIterator(); it.hasNext(); ) {
            final DataField df = it.next();
            final String tag = df.getTag();
            if (ruleSet.immutableField(tag) || ruleSet.invalidField(tag, includeAllFields)) {
                it.remove();
            } else {
                it.set(df);
                ruleSet.registerLocalField(tag);
            }
        }
    }

    /**
     * removes nodes from commonFields according to ruleSet
     * <p>
     * imports nodes into targetDom
     * <p>
     * removes nodes from commonDom
     *
     * @param commonDataFields list of DataFields from the common record
     * @param ruleSet the rule set to use
     */
    private static void removeAndImportCommonFields(List<DataField> commonDataFields, FieldRules.RuleSet ruleSet) {
        for (ListIterator<DataField> it = commonDataFields.listIterator(); it.hasNext(); ) {
            final DataField df = it.next();
            final String tag = df.getTag();
            if (ruleSet.invalidField(tag, false) || ruleSet.removeField(tag)) {
                it.remove();
            } else {
                it.set(df);
            }
        }
    }

    private static List<DataField> mergeCommonAndLocalIntoTarget(List<DataField> localDataFields, List<DataField> commonDataFields) {
        final List<DataField> result = new ArrayList<>();
        ListIterator<DataField> localIterator = localDataFields.listIterator();
        ListIterator<DataField> commonIterator = commonDataFields.listIterator();

        while (commonIterator.hasNext() && localIterator.hasNext()) {
            final DataField localDataField = localIterator.next();
            final String localTag = localDataField.getTag();
            final DataField commonDataField = commonIterator.next();
            final String commonTag = commonDataField.getTag();

            if (commonTag.compareTo(localTag) <= 0) {
                localIterator.previous();
                result.add(commonDataField);
            } else {
                commonIterator.previous();
                result.add(localDataField);
            }
        }
        while (commonIterator.hasNext()) {
            final DataField commonDataField = commonIterator.next();
            result.add(commonDataField);
        }
        while (localIterator.hasNext()) {
            final DataField localDataField = localIterator.next();
            result.add(localDataField);
        }

        return result;
    }

}
