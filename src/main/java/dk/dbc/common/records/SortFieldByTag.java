package dk.dbc.common.records;

import dk.dbc.marc.binding.Field;

import java.util.Comparator;

/**
 * Class to sort the fields in a MarcRecord.
 * <p>
 * The fields are sorted by name/tag.
 * </p>
 */
public class SortFieldByTag implements Comparator<Field> {

    @Override
    public int compare(Field o1, Field o2) {
        return o1.getTag().compareTo(o2.getTag());
    }
}
