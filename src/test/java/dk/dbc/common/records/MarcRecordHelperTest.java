package dk.dbc.common.records;

import dk.dbc.marc.binding.MarcRecord;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MarcRecordHelperTest {

    @Test
    void testAddOrReplaceSubfield() {
        MarcRecord marcRecord = new MarcRecord();


        MarcRecordHelper.addOrReplaceSubField(marcRecord, "001", 'a', "xxx");
        assertThat(marcRecord.getSubFieldValues("001", 'a'), is(Collections.singletonList("xxx")));

        MarcRecordHelper.addOrReplaceSubField(marcRecord, "001", 'b', "yy");
        assertThat(marcRecord.getSubFieldValues("001", 'b'), is(Collections.singletonList("yy")));

        MarcRecordHelper.addOrReplaceSubField(marcRecord, "001", 'a', "zzz");
        assertThat(marcRecord.getSubFieldValues("001", 'a'), is(Collections.singletonList("zzz")));
    }
}
