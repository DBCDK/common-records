/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class AgencyNumberTest {
    @Test
    public void testIntegerNumbers() {
        AgencyNumber instance = new AgencyNumber(100);
        assertThat(instance.getAgencyId(), equalTo(100));
        assertThat(instance.toString(), equalTo("000100"));

        instance.setAgencyId(716800);
        assertThat(instance.getAgencyId(), equalTo(716800));
        assertThat(instance.toString(), equalTo("716800"));
    }

    @Test
    public void testStringNumbers() {
        AgencyNumber instance = new AgencyNumber("100");
        assertThat(instance.getAgencyId(), equalTo(100));
        assertThat(instance.toString(), equalTo("000100"));

        instance.setAgencyId("716800");
        assertThat(instance.getAgencyId(), equalTo(716800));
        assertThat(instance.toString(), equalTo("716800"));
    }

    @Test
    public void testShortNumber() {
        AgencyNumber instance = new AgencyNumber(100);
        assertThat(instance.toString(), equalTo("000100"));
    }
}
