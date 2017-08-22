/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class MarcFieldTest {
    @Test
    public void testCopyConstructor() throws Exception {
        MarcField f1 = new MarcField("245", "00");
        f1.getSubfields().add(new MarcSubField("a", "k"));
        f1.getSubfields().add(new MarcSubField("x", "IDO200724"));

        MarcField f2 = new MarcField(f1);

        assertThat(f2, notNullValue());
        assertThat(f1, equalTo(f2));
    }
}
