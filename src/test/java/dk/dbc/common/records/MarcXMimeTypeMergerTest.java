package dk.dbc.common.records;

import dk.dbc.common.records.MarcXChangeMimeType;
import dk.dbc.common.records.MarcXMimeTypeMerger;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarcXMimeTypeMergerTest {

    @Test
    void testCanMerge() {
        assertTrue(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.MARCXCHANGE, MarcXChangeMimeType.ENRICHMENT));
        assertTrue(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.ARTICLE, MarcXChangeMimeType.ENRICHMENT));
        assertTrue(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.MATVURD, MarcXChangeMimeType.ENRICHMENT));
        assertTrue(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.LITANALYSIS, MarcXChangeMimeType.ENRICHMENT));
        assertTrue(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.HOSTPUB, MarcXChangeMimeType.ENRICHMENT));
        assertTrue(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.AUTHORITY, MarcXChangeMimeType.ENRICHMENT));
        assertTrue(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.SIMPLE, MarcXChangeMimeType.ENRICHMENT));

        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.MARCXCHANGE, MarcXChangeMimeType.MARCXCHANGE));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.ARTICLE, MarcXChangeMimeType.ARTICLE));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.MATVURD, MarcXChangeMimeType.MATVURD));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.LITANALYSIS, MarcXChangeMimeType.LITANALYSIS));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.HOSTPUB, MarcXChangeMimeType.HOSTPUB));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.AUTHORITY, MarcXChangeMimeType.AUTHORITY));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.SIMPLE, MarcXChangeMimeType.SIMPLE));

        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.ENRICHMENT, MarcXChangeMimeType.ENRICHMENT));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.ENRICHMENT, MarcXChangeMimeType.MARCXCHANGE));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.ENRICHMENT, MarcXChangeMimeType.ARTICLE));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.ENRICHMENT, MarcXChangeMimeType.MATVURD));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.ENRICHMENT, MarcXChangeMimeType.LITANALYSIS));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.ENRICHMENT, MarcXChangeMimeType.HOSTPUB));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.ENRICHMENT, MarcXChangeMimeType.AUTHORITY));
        assertFalse(MarcXMimeTypeMerger.canMerge(MarcXChangeMimeType.ENRICHMENT, MarcXChangeMimeType.SIMPLE));
    }

    @Test
    void testMergedMimetype() {
        assertThat(MarcXMimeTypeMerger.mergedMimeType(MarcXChangeMimeType.MARCXCHANGE, MarcXChangeMimeType.ENRICHMENT), is(MarcXChangeMimeType.MARCXCHANGE));
        assertThat(MarcXMimeTypeMerger.mergedMimeType(MarcXChangeMimeType.ARTICLE, MarcXChangeMimeType.ENRICHMENT), is(MarcXChangeMimeType.ARTICLE));
        assertThat(MarcXMimeTypeMerger.mergedMimeType(MarcXChangeMimeType.AUTHORITY, MarcXChangeMimeType.ENRICHMENT), is(MarcXChangeMimeType.AUTHORITY));
        assertThat(MarcXMimeTypeMerger.mergedMimeType(MarcXChangeMimeType.LITANALYSIS, MarcXChangeMimeType.ENRICHMENT), is(MarcXChangeMimeType.LITANALYSIS));
        assertThat(MarcXMimeTypeMerger.mergedMimeType(MarcXChangeMimeType.MATVURD, MarcXChangeMimeType.ENRICHMENT), is(MarcXChangeMimeType.MATVURD));
    }
}
