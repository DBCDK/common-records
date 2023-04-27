package dk.dbc.common.records;

import dk.dbc.marc.binding.DataField;
import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.binding.SubField;

import java.util.Optional;

public class MarcRecordHelper {

    public static void addOrReplaceSubField(MarcRecord marcRecord, String tag, char code, String value) {
        final Optional<DataField> dataField = marcRecord.getField(DataField.class, MarcRecord.hasTag(tag));

        if (dataField.isPresent()) {
            dataField.get().addOrReplaceFirstSubField(new SubField(code, value));
        } else {
            marcRecord.addField(new DataField(tag, "0")
                    .addSubField(new SubField(code, value)));
        }
    }

}
