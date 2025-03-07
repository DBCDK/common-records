package dk.dbc.common.records;

import dk.dbc.marc.binding.DataField;
import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.binding.SubField;
import dk.dbc.marc.reader.DanMarc2LineFormatReader;
import dk.dbc.marc.reader.MarcReaderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ExpandCommonMarcRecordTest {
    private static final String AUT_RAW_22810804 = "authority/raw-22810804.marc";
    private static final String AUT_RAW_126350333 = "authority/raw-126350333.marc";
    private static final String AUT_RAW_126850298 = "authority/raw-126850298.marc";
    private static final String AUT_RAW_22642448 = "authority/raw-22642448.marc";
    private static final String AUT_RAW_26081718 = "authority/raw-26081718.marc";
    private static final String AUT_RAW_26443784 = "authority/raw-26443784.marc";
    private static final String AUT_RAW_27568602 = "authority/raw-27568602.marc";
    private static final String AUT_RAW_47625475 = "authority/raw-47625475.marc";
    private static final String AUT_RAW_48141943 = "authority/raw-48141943.marc";
    private static final String AUT_RAW_48776108 = "authority/raw-48776108.marc";
    private static final String AUT_RAW_48802362 = "authority/raw-48802362.marc";
    private static final String AUT_RAW_48867472 = "authority/raw-48867472.marc";
    private static final String AUT_RAW_52846943 = "authority/raw-52846943.marc";
    private static final String AUT_RAW_53025757 = "authority/raw-53025757.marc";
    private static final String AUT_RAW_53161510 = "authority/raw-53161510.marc";
    private static final String AUT_RAW_53180485 = "authority/raw-53180485.marc";
    private static final String AUT_RAW_53213642 = "authority/raw-53213642.marc";
    private static final String AUT_RAW_53214592 = "authority/raw-53214592.marc";
    private static final String AUT_RAW_53214827 = "authority/raw-53214827.marc";
    private static final String AUT_RAW_53333338 = "authority/raw-53333338.marc";
    private static final String AUT_RAW_53356478 = "authority/raw-53356478.marc";
    private static final String AUT_RAW_53551173 = "authority/raw-53551173.marc";
    private static final String AUT_RAW_90004158 = "authority/raw-90004158.marc";
    private static final String AUT_RAW_130955754 = "authority/raw-130955754.marc";
    private static final String AUT_RAW_61777431 = "authority/raw-61777431.marc";
    private static final String AUT_RAW_47042143 = "authority/raw-47042143.marc";

    private static final String AUT_EXPANDED_22810804 = "authority/expanded-22810804.marc";
    private static final String AUT_EXPANDED_126350333 = "authority/expanded-126350333.marc";
    private static final String AUT_EXPANDED_126850298 = "authority/expanded-126850298.marc";
    private static final String AUT_EXPANDED_22642448 = "authority/expanded-22642448.marc";
    private static final String AUT_EXPANDED_26081718 = "authority/expanded-26081718.marc";
    private static final String AUT_EXPANDED_26443784 = "authority/expanded-26443784.marc";
    private static final String AUT_EXPANDED_27568602 = "authority/expanded-27568602.marc";
    private static final String AUT_EXPANDED_47625475 = "authority/expanded-47625475.marc";
    private static final String AUT_EXPANDED_48141943 = "authority/expanded-48141943.marc";
    private static final String AUT_EXPANDED_48776108 = "authority/expanded-48776108.marc";
    private static final String AUT_EXPANDED_48802362 = "authority/expanded-48802362.marc";
    private static final String AUT_EXPANDED_48867472 = "authority/expanded-48867472.marc";
    private static final String AUT_EXPANDED_52846943 = "authority/expanded-52846943.marc";
    private static final String AUT_EXPANDED_53025757 = "authority/expanded-53025757.marc";
    private static final String AUT_EXPANDED_53161510 = "authority/expanded-53161510.marc";
    private static final String AUT_EXPANDED_53180485 = "authority/expanded-53180485.marc";
    private static final String AUT_EXPANDED_53213642 = "authority/expanded-53213642.marc";
    private static final String AUT_EXPANDED_53214592 = "authority/expanded-53214592.marc";
    private static final String AUT_EXPANDED_53214827 = "authority/expanded-53214827.marc";
    private static final String AUT_EXPANDED_53333338 = "authority/expanded-53333338.marc";
    private static final String AUT_EXPANDED_53356478 = "authority/expanded-53356478.marc";
    private static final String AUT_EXPANDED_53551173 = "authority/expanded-53551173.marc";
    private static final String AUT_EXPANDED_90004158 = "authority/expanded-90004158.marc";
    private static final String AUT_EXPANDED_130955754 = "authority/expanded-130955754.marc";
    private static final String AUT_EXPANDED_61777431 = "authority/expanded-61777431.marc";
    private static final String AUT_EXPANDED_47042143 = "authority/expanded-47042143.marc";

    private static final String AUTHORITY_68305926 = "authority/authority-68305926.marc";
    private static final String AUTHORITY_19024687 = "authority/authority-19024687.marc";
    private static final String AUTHORITY_19024709 = "authority/authority-19024709.marc";
    private static final String AUTHORITY_19043800 = "authority/authority-19043800.marc";
    private static final String AUTHORITY_19064689 = "authority/authority-19064689.marc";
    private static final String AUTHORITY_19104869 = "authority/authority-19104869.marc";
    private static final String AUTHORITY_19130452 = "authority/authority-19130452.marc";
    private static final String AUTHORITY_47220882 = "authority/authority-47220882.marc";
    private static final String AUTHORITY_47919142 = "authority/authority-47919142.marc";
    private static final String AUTHORITY_48280129 = "authority/authority-48280129.marc";
    private static final String AUTHORITY_48327826 = "authority/authority-48327826.marc";
    private static final String AUTHORITY_48623964 = "authority/authority-48623964.marc";
    private static final String AUTHORITY_48623972 = "authority/authority-48623972.marc";
    private static final String AUTHORITY_48623999 = "authority/authority-48623999.marc";
    private static final String AUTHORITY_48872123 = "authority/authority-48872123.marc";
    private static final String AUTHORITY_48872158 = "authority/authority-48872158.marc";
    private static final String AUTHORITY_48872174 = "authority/authority-48872174.marc";
    private static final String AUTHORITY_48872212 = "authority/authority-48872212.marc";
    private static final String AUTHORITY_48872239 = "authority/authority-48872239.marc";
    private static final String AUTHORITY_48872247 = "authority/authority-48872247.marc";
    private static final String AUTHORITY_48872328 = "authority/authority-48872328.marc";
    private static final String AUTHORITY_48872336 = "authority/authority-48872336.marc";
    private static final String AUTHORITY_48873073 = "authority/authority-48873073.marc";
    private static final String AUTHORITY_68046335 = "authority/authority-68046335.marc";
    private static final String AUTHORITY_68058953 = "authority/authority-68058953.marc";
    private static final String AUTHORITY_68098203 = "authority/authority-68098203.marc";
    private static final String AUTHORITY_68139864 = "authority/authority-68139864.marc";
    private static final String AUTHORITY_68219027 = "authority/authority-68219027.marc";
    private static final String AUTHORITY_68313686 = "authority/authority-68313686.marc";
    private static final String AUTHORITY_68354153 = "authority/authority-68354153.marc";
    private static final String AUTHORITY_68432359 = "authority/authority-68432359.marc";
    private static final String AUTHORITY_68472806 = "authority/authority-68472806.marc";
    private static final String AUTHORITY_68560985 = "authority/authority-68560985.marc";
    private static final String AUTHORITY_68562554 = "authority/authority-68562554.marc";
    private static final String AUTHORITY_68570492 = "authority/authority-68570492.marc";
    private static final String AUTHORITY_68584566 = "authority/authority-68584566.marc";
    private static final String AUTHORITY_68585627 = "authority/authority-68585627.marc";
    private static final String AUTHORITY_68611490 = "authority/authority-68611490.marc";
    private static final String AUTHORITY_68619858 = "authority/authority-68619858.marc";
    private static final String AUTHORITY_68630258 = "authority/authority-68630258.marc";
    private static final String AUTHORITY_68679265 = "authority/authority-68679265.marc";
    private static final String AUTHORITY_68712742 = "authority/authority-68712742.marc";
    private static final String AUTHORITY_68750679 = "authority/authority-68750679.marc";
    private static final String AUTHORITY_68759498 = "authority/authority-68759498.marc";
    private static final String AUTHORITY_68799406 = "authority/authority-68799406.marc";
    private static final String AUTHORITY_68801451 = "authority/authority-68801451.marc";
    private static final String AUTHORITY_68839734 = "authority/authority-68839734.marc";
    private static final String AUTHORITY_68895650 = "authority/authority-68895650.marc";
    private static final String AUTHORITY_68897785 = "authority/authority-68897785.marc";
    private static final String AUTHORITY_68900719 = "authority/authority-68900719.marc";
    private static final String AUTHORITY_68942667 = "authority/authority-68942667.marc";
    private static final String AUTHORITY_68955076 = "authority/authority-68955076.marc";
    private static final String AUTHORITY_69094139 = "authority/authority-69094139.marc";
    private static final String AUTHORITY_69242162 = "authority/authority-69242162.marc";
    private static final String AUTHORITY_69294685 = "authority/authority-69294685.marc";
    private static final String AUTHORITY_69328776 = "authority/authority-69328776.marc";
    private static final String AUTHORITY_69518060 = "authority/authority-69518060.marc";
    private static final String AUTHORITY_69029868 = "authority/authority-69029868.marc";
    private static final String AUTHORITY_222222222 = "authority/authority-222222222.marc";
    private static final String AUTHORITY_444444444 = "authority/authority-444444444.marc";
    private static final String AUTHORITY_133990054 = "authority/authority-133990054.marc";
    private static final String EXPANDED_133990054 = "authority/expanded-133990054.marc";
    private static final String AUTHORITY_133990119 = "authority/authority-133990119.marc";
    private static final String AUTHORITY_138073416 = "authority/authority-138073416.marc";

    private static final String COMMON_SINGLE_RECORD_RESOURCE = "authority/common_enrichment.marc";

    private static MarcRecord loadMarcRecord(String filename) throws MarcReaderException, IOException {
        final ClassLoader classLoader = ExpandCommonMarcRecordTest.class.getClassLoader();
        final File file = new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());
        final InputStream is = new FileInputStream(file);

        final DanMarc2LineFormatReader lineFormatReader = new DanMarc2LineFormatReader(is, StandardCharsets.UTF_8);

        return lineFormatReader.read();
    }

    @Test
    void expandCommonRecordOk_52846943() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_52846943);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_52846943);
        MarcRecord auth1 = loadMarcRecord(AUTHORITY_19024709);
        MarcRecord auth2 = loadMarcRecord(AUTHORITY_19024687);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("52846943", raw);
        collection.put("19024709", auth1);
        collection.put("19024687", auth2);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "52846943"), is(expanded));
    }

    @Test
    void expandCommonRecordOk_53025757() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_53025757);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_53025757);
        MarcRecord auth1 = loadMarcRecord(AUTHORITY_68432359);
        MarcRecord auth2 = loadMarcRecord(AUTHORITY_69328776);
        MarcRecord auth3 = loadMarcRecord(AUTHORITY_19043800);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("53025757", raw);
        collection.put("68432359", auth1);
        collection.put("69328776", auth2);
        collection.put("19043800", auth3);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "53025757"), is(expanded));
    }

    @Test
    void expandCommonRecordOk_53161510() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_53161510);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_53161510);
        MarcRecord auth1 = loadMarcRecord(AUTHORITY_69094139);
        MarcRecord auth2 = loadMarcRecord(AUTHORITY_68098203);
        MarcRecord auth3 = loadMarcRecord(AUTHORITY_19064689);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("53161510", raw);
        collection.put("69094139", auth1);
        collection.put("68098203", auth2);
        collection.put("19064689", auth3);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "53161510"), is(expanded));
    }

    @Test
    void expandCommonRecordOk_53180485() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_53180485);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_53180485);
        MarcRecord auth1 = loadMarcRecord(AUTHORITY_68839734);
        MarcRecord auth2 = loadMarcRecord(AUTHORITY_68584566);
        MarcRecord auth3 = loadMarcRecord(AUTHORITY_68900719);
        MarcRecord auth4 = loadMarcRecord(AUTHORITY_68560985);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("53180485", raw);
        collection.put("68839734", auth1);
        collection.put("68584566", auth2);
        collection.put("68900719", auth3);
        collection.put("68560985", auth4);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "53180485"), is(expanded));
    }

    @Test
    void expandCommonRecordOk_53213642() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_53213642);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_53213642);
        MarcRecord auth1 = loadMarcRecord(AUTHORITY_68895650);
        MarcRecord auth2 = loadMarcRecord(AUTHORITY_19130452);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("53213642", raw);
        collection.put("68895650", auth1);
        collection.put("19130452", auth2);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "53213642"), is(expanded));
    }

    @Test
    void expandCommonRecordOk_53214592() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_53214592);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_53214592);
        MarcRecord auth1 = loadMarcRecord(AUTHORITY_68354153);
        MarcRecord auth3 = loadMarcRecord(AUTHORITY_68472806);
        MarcRecord auth4 = loadMarcRecord(AUTHORITY_68585627);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("53214592", raw);
        collection.put("68354153", auth1);
        collection.put("68472806", auth3);
        collection.put("68585627", auth4);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "53214592"), is(expanded));
    }

    @Test
    void expandCommonRecordOk_53214827() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_53214827);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_53214827);
        MarcRecord auth1 = loadMarcRecord(AUTHORITY_68570492);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("53214827", raw);
        collection.put("68570492", auth1);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "53214827"), is(expanded));
    }

    @Test
    void expandCommonRecordUniverse133_433() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_47042143);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_47042143);
        MarcRecord auth1 = loadMarcRecord(AUTHORITY_138073416);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("47042143", raw);
        collection.put("138073416", auth1);
        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "47042143"), is(expanded));
    }

    @Test
    void expandCommonRecordUniverse() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_61777431);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_61777431);
        MarcRecord auth1 = loadMarcRecord(AUTHORITY_69029868);
        MarcRecord auth2 = loadMarcRecord(AUTHORITY_133990054);
        MarcRecord auth3 = loadMarcRecord(AUTHORITY_133990119);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("61777431", raw);
        collection.put("69029868", auth1);
        collection.put("133990054", auth2);
        collection.put("133990119", auth3);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "61777431"), is(expanded));
    }

    @Test
    void expandAuthSeriesAndUniverse() throws Exception {
        MarcRecord auth1 = loadMarcRecord(AUTHORITY_133990054);
        MarcRecord auth2 = loadMarcRecord(AUTHORITY_222222222);
        MarcRecord auth3 = loadMarcRecord(AUTHORITY_444444444);
        MarcRecord expanded = loadMarcRecord(EXPANDED_133990054);
        Map<String, MarcRecord> collection = Map.of("133990054", auth1, "222222222", auth2, "444444444", auth3);
        MarcRecord record = ExpandCommonMarcRecord.expandMarcRecord(collection, "133990054");
        assertThat(record, is(expanded));
    }

    @Test
    void expandCommonRecordOk_90004158() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_90004158);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_90004158);
        MarcRecord auth1 = loadMarcRecord(AUTHORITY_68712742);
        MarcRecord auth2 = loadMarcRecord(AUTHORITY_69294685);
        MarcRecord auth3 = loadMarcRecord(AUTHORITY_48872247);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("90004158", raw);
        collection.put("68712742", auth1);
        collection.put("69294685", auth2);
        collection.put("48872247", auth3);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "90004158"), is(expanded));
    }

    @Test
    void noCommonRecord() {
        Map<String, MarcRecord> collection = new HashMap<>();

        Assertions.assertThrows(MarcRecordExpandException.class, () -> ExpandCommonMarcRecord.expandMarcRecord(collection, ""));
    }

    @Test
    void missingAuthorityRecords() throws Exception {
        MarcRecord record = loadMarcRecord(AUT_RAW_90004158);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("90004158", record);

        Assertions.assertThrows(MarcRecordExpandException.class, () -> ExpandCommonMarcRecord.expandMarcRecord(collection, "90004158"));
    }

    @Test
    void expandCommonRecordWithoutAuthorityFields() throws Exception {
        MarcRecord record = loadMarcRecord(COMMON_SINGLE_RECORD_RESOURCE);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("20611529", record);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "20611529"), is(record));
    }

    @Test
    void expandCommonRecordWithTwoReferencesToSameAuthorityRecord() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_26443784);
        MarcRecord authority = loadMarcRecord(AUTHORITY_68313686);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_26443784);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("26443784", raw);
        collection.put("68313686", authority);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "26443784"), is(expanded));
    }

    @Test
    void expandLittolkRecordWithDoubleA() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_126350333);

        MarcRecord authority1 = loadMarcRecord(AUTHORITY_68058953);
        MarcRecord authority2 = loadMarcRecord(AUTHORITY_68560985);
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_126350333);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("126350333", raw);

        collection.put("68058953", authority1);
        collection.put("68560985", authority2);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "126350333"), is(expanded));
    }

    @Test
    void expandLittolkRecordWithExistingReferenceEarlierName() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_126850298);

        MarcRecord authority1 = loadMarcRecord(AUTHORITY_68046335);
        MarcRecord authority2 = loadMarcRecord(AUTHORITY_48872247);
        MarcRecord authority3 = loadMarcRecord(AUTHORITY_68619858);
        MarcRecord authority4 = loadMarcRecord(AUTHORITY_68679265);

        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_126850298);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("126850298", raw);

        collection.put("68046335", authority1);
        collection.put("48872247", authority2);
        collection.put("68619858", authority3);
        collection.put("68679265", authority4);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "126850298"), is(expanded));
    }

    @Test
    void expandLittolkRecordWithExistingReferenceLaterName() throws Exception {
        MarcRecord raw = loadMarcRecord(AUT_RAW_22810804);

        MarcRecord authority1 = loadMarcRecord(AUTHORITY_68305926);

        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_22810804);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("22810804", raw);

        collection.put("68305926", authority1);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "22810804"), is(expanded));
    }

    @Test
    void textAutMus1() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_22642448);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("22642448", loadMarcRecord(AUT_RAW_22642448));
        collection.put("48872212", loadMarcRecord(AUTHORITY_48872212));
        collection.put("48872239", loadMarcRecord(AUTHORITY_48872239));
        collection.put("48872123", loadMarcRecord(AUTHORITY_48872123));
        collection.put("48872336", loadMarcRecord(AUTHORITY_48872336));
        collection.put("48872247", loadMarcRecord(AUTHORITY_48872247));

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "22642448"), is(expanded));
    }

    @Test
    void textAutMus2() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_26081718);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("26081718", loadMarcRecord(AUT_RAW_26081718));

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "26081718"), is(expanded));
    }

    @Test
    void textAutMus3() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_27568602);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("27568602", loadMarcRecord(AUT_RAW_27568602));
        collection.put("48872212", loadMarcRecord(AUTHORITY_48872212));
        collection.put("48872158", loadMarcRecord(AUTHORITY_48872158));
        collection.put("68750679", loadMarcRecord(AUTHORITY_68750679));
        collection.put("68759498", loadMarcRecord(AUTHORITY_68759498));
        collection.put("48872247", loadMarcRecord(AUTHORITY_48872247));

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "27568602"), is(expanded));
    }

    @Test
    void textAutMus4() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_47625475);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("47625475", loadMarcRecord(AUT_RAW_47625475));
        collection.put("48872336", loadMarcRecord(AUTHORITY_48872336));

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "47625475"), is(expanded));
    }

    @Test
    void textAutMus5() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_48141943);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("48141943", loadMarcRecord(AUT_RAW_48141943));
        collection.put("48623964", loadMarcRecord(AUTHORITY_48623964));
        collection.put("48280129", loadMarcRecord(AUTHORITY_48280129));
        collection.put("48623972", loadMarcRecord(AUTHORITY_48623972));
        collection.put("48623999", loadMarcRecord(AUTHORITY_48623999));


        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "48141943"), is(expanded));
    }

    @Test
    void textAutMus6() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_48802362);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("48802362", loadMarcRecord(AUT_RAW_48802362));
        collection.put("48872174", loadMarcRecord(AUTHORITY_48872174));

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "48802362"), is(expanded));
    }

    @Test
    void textAutMus7() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_48867472);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("48867472", loadMarcRecord(AUT_RAW_48867472));
        collection.put("47220882", loadMarcRecord(AUTHORITY_47220882));

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "48867472"), is(expanded));
    }

    @Test
    void textAutMus8() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_53333338);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("53333338", loadMarcRecord(AUT_RAW_53333338));
        collection.put("47919142", loadMarcRecord(AUTHORITY_47919142));

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "53333338"), is(expanded));
    }

    @Test
    void textAutMus9() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_53356478);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("53356478", loadMarcRecord(AUT_RAW_53356478));
        collection.put("69518060", loadMarcRecord(AUTHORITY_69518060));
        collection.put("48327826", loadMarcRecord(AUTHORITY_48327826));

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "53356478"), is(expanded));
    }

    @Test
    void textAutMus10() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_53551173);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("53551173", loadMarcRecord(AUT_RAW_53551173));
        collection.put("48872174", loadMarcRecord(AUTHORITY_48872174));
        collection.put("48872123", loadMarcRecord(AUTHORITY_48872123));
        collection.put("48872328", loadMarcRecord(AUTHORITY_48872328));
        collection.put("48872158", loadMarcRecord(AUTHORITY_48872158));

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "53551173"), is(expanded));
    }

    @Test
    void textAutMus11() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_48776108);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("48776108", loadMarcRecord(AUT_RAW_48776108));
        collection.put("48873073", loadMarcRecord(AUTHORITY_48873073));

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "48776108"), is(expanded));
    }

    @Test
    void missingPartOfAuthorName_A() throws Exception {
        final MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_53214592);
        final MarcRecordWriter writer = new MarcRecordWriter(expanded);
        writer.addOrReplaceSubField("900", 'w', "Michael");

        for (DataField field : expanded.getFields(DataField.class, MarcRecord.hasTag("700"))) {
            final Optional<SubField> subField = field.getSubField(DataField.hasSubFieldCode('h'));
            if (subField.isPresent() && "Michael".equals(subField.get().getData())) {
                field.removeSubField('a');
            }
        }

        final MarcRecord auth1 = loadMarcRecord(AUTHORITY_68354153);
        final MarcRecord auth2 = loadMarcRecord(AUTHORITY_68472806);
        final MarcRecord auth3 = loadMarcRecord(AUTHORITY_68585627);
        final DataField dataField100 = auth2.getField(DataField.class, MarcRecord.hasTag("100")).orElseThrow();
        dataField100.removeSubField('a');

        final Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("53214592", loadMarcRecord(AUT_RAW_53214592));
        collection.put("68354153", auth1);
        collection.put("68472806", auth2);
        collection.put("68585627", auth3);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "53214592"), is(expanded));
    }

    @Test
    void missingPartOfAuthorName_H() throws Exception {
        final MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_53214592);

        final MarcRecordWriter writer = new MarcRecordWriter(expanded);
        writer.addOrReplaceSubField("900", 'w', "Hviid Jacobsen");

        for (DataField field : expanded.getFields(DataField.class, MarcRecord.hasTag("700"))) {
            final Optional<SubField> subField = field.getSubField(DataField.hasSubFieldCode('a'));
            if (subField.isPresent() && "Hviid Jacobsen".equals(subField.get().getData())) {
                field.removeSubField('h');
            }
        }

        final MarcRecord auth1 = loadMarcRecord(AUTHORITY_68354153);
        final MarcRecord auth2 = loadMarcRecord(AUTHORITY_68472806);
        final MarcRecord auth3 = loadMarcRecord(AUTHORITY_68585627);
        final DataField dataField100 = auth2.getField(DataField.class, MarcRecord.hasTag("100")).orElseThrow();
        dataField100.removeSubField('h');

        final Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("53214592", loadMarcRecord(AUT_RAW_53214592));
        collection.put("68354153", auth1);
        collection.put("68472806", auth2);
        collection.put("68585627", auth3);

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "53214592"), is(expanded));
    }

    @Test
    void testSortingAa() throws Exception {
        MarcRecord expanded = loadMarcRecord(AUT_EXPANDED_130955754);

        Map<String, MarcRecord> collection = new HashMap<>();
        collection.put("130955754", loadMarcRecord(AUT_RAW_130955754));
        collection.put("19104869", loadMarcRecord(AUTHORITY_19104869));
        collection.put("68139864", loadMarcRecord(AUTHORITY_68139864));
        collection.put("68219027", loadMarcRecord(AUTHORITY_68219027));
        collection.put("68562554", loadMarcRecord(AUTHORITY_68562554));
        collection.put("68611490", loadMarcRecord(AUTHORITY_68611490));
        collection.put("68630258", loadMarcRecord(AUTHORITY_68630258));
        collection.put("68799406", loadMarcRecord(AUTHORITY_68799406));
        collection.put("68801451", loadMarcRecord(AUTHORITY_68801451));
        collection.put("68897785", loadMarcRecord(AUTHORITY_68897785));
        collection.put("68942667", loadMarcRecord(AUTHORITY_68942667));
        collection.put("68955076", loadMarcRecord(AUTHORITY_68955076));
        collection.put("69242162", loadMarcRecord(AUTHORITY_69242162));

        assertThat(ExpandCommonMarcRecord.expandMarcRecord(collection, "130955754"), is(expanded));
    }

}
