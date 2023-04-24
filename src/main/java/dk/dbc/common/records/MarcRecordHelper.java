package dk.dbc.common.records;

import dk.dbc.marc.binding.DataField;
import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.binding.SubField;

import java.util.List;
import java.util.Optional;

public class MarcRecordHelper {

    public static void addOrReplaceSubField(MarcRecord marcRecord, String tag, char code, String value) {
        final List<DataField> dataFields = marcRecord.getFields(DataField.class, MarcRecord.hasTag(tag));

        // Field wasn't found so add it
        if(dataFields.isEmpty()) {
            marcRecord.getFields().add(new DataField(tag, "00")
                    .addSubField(new SubField(code, value)));
            return;
        }

        for (DataField dataField : dataFields) {
            // Try to update existing value
            final Optional<SubField> subField = dataField.getSubField(DataField.hasSubFieldCode(code));
            if (subField.isPresent()) {
                subField.get().setData(value);
                return;
            }

        }

        // No field has the subfield, so add subfield
        dataFields.get(0).getSubFields().add(new SubField(code, value));
    }

}
