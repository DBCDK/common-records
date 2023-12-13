package dk.dbc.common.records;

import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.reader.MarcReaderException;
import dk.dbc.marc.reader.MarcXchangeV1Reader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DanMARC2MergerTest {

    public static Collection<String[]> filenames() {
        return Arrays.asList(new String[][]{ // Constructor arguments
                {"append", "", "", "", ".*", "false"}, // Append
                {"immutable", "245", "", "", ".*", "false"}, // Don't overwrite protected field
                {"overwrite", "", "245", "", ".*", "false"}, // Overwrite of single fields
                {"overwrite_multi_to_single", "", "006;300", "", ".*", "false"},
                {"overwrite_multi", "", "245", "", ".*", "false"}, // Overwrite of repeated field
                {"overwrite_group", "", "245 239", "", ".*", "false"}, // Overwrite of repeated field
                {"remove", "", "", "245", ".*", "false"}, // Remove field
                {"invalid", "", "", "", "\\d{3}", "false"}, // Pattern validation
                {"final", "", "", "", "\\d{3}", "true"} // Pattern validation
        });
    }

    @ParameterizedTest
    @MethodSource("filenames")
    void testMerge(String base, String immutableString, String overwriteString, String invalidString, String valid_regex, String isFinalString)
            throws MarcReaderException, IOException {
        final FieldRules fieldRulesIntermediate = new FieldRules(
                collectionInit(immutableString),
                overwriteCollectionsInit(overwriteString), collectionInit(invalidString), valid_regex);
        final boolean isFinal = Boolean.parseBoolean(isFinalString);

        final MarcRecord common = loadMarcRecord("dk.dbc.marcxmerger/" + base + "/common.xml");
        final MarcRecord local = loadMarcRecord("dk.dbc.marcxmerger/" +base + "/local.xml");
        final MarcRecord result = loadMarcRecord("dk.dbc.marcxmerger/" +base + "/result.xml");
        final DanMARC2Merger marcxMerger = new DanMARC2Merger(fieldRulesIntermediate, "custom");
        final MarcRecord merge = marcxMerger.merge(common, local, isFinal);

        assertThat(merge, is(result));
    }

    @Test
    void testMergeDefaultRules() throws Exception {
        final MarcRecord common = loadMarcRecord("dk.dbc.marcxmerger/defaultRules/common.xml");
        final MarcRecord local = loadMarcRecord("dk.dbc.marcxmerger/defaultRules/local.xml");
        final MarcRecord result = loadMarcRecord("dk.dbc.marcxmerger/defaultRules/result.xml");
        final DanMARC2Merger marcxMerger = new DanMARC2Merger();
        final MarcRecord merge = marcxMerger.merge(common, local, true);

        assertThat(merge, is(result));
    }

    private static Map<String, Set<String>> overwriteCollectionsInit(String init) {
        Map<String, Set<String>> map = new HashMap<>();
        String[] groups = init.split(";");
        for (String group : groups) {
            Set<String> set = new HashSet<>();
            String[] tags = group.split(" ");
            for (String tag : tags) {
                set.add(tag);
                if (map.containsKey(tag)) {
                    throw new IllegalArgumentException("Error initializing overwriteCollections, field: " + tag + " is repeated");
                }
                map.put(tag, set);
            }
        }
        return map;
    }

    private static Set<String> collectionInit(String init) {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, init.split(";"));
        return set;
    }

    private static MarcRecord loadMarcRecord(String filename) throws MarcReaderException, IOException {
        final ClassLoader classLoader = DanMARC2MergerTest.class.getClassLoader();
        final File file = new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());
        final InputStream is = new FileInputStream(file);

        final MarcXchangeV1Reader reader = new MarcXchangeV1Reader(is, StandardCharsets.UTF_8);

        return reader.read();
    }
}
