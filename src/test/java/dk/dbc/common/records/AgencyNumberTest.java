package dk.dbc.common.records;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class AgencyNumberTest {
    @Test
    void testIntegerNumbers() {
        AgencyNumber instance = new AgencyNumber(100);
        assertThat(instance.getAgencyId(), is(100));
        assertThat(instance.toString(), is("000100"));

        instance.setAgencyId(716800);
        assertThat(instance.getAgencyId(), is(716800));
        assertThat(instance.toString(), is("716800"));
    }

    @Test
    void testStringNumbers() {
        AgencyNumber instance = new AgencyNumber("100");
        assertThat(instance.getAgencyId(), is(100));
        assertThat(instance.toString(), is("000100"));

        instance.setAgencyId("716800");
        assertThat(instance.getAgencyId(), is(716800));
        assertThat(instance.toString(), is("716800"));
    }

    @Test
    void testShortNumber() {
        AgencyNumber instance = new AgencyNumber(100);
        assertThat(instance.toString(), is("000100"));
    }
}
