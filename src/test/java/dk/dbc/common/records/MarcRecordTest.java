/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.common.records;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class MarcRecordTest {
    @Test
    public void testCopyConstructor() throws Exception {
        MarcField field = new MarcField("245", "00");
        field.getSubfields().add(new MarcSubField("a", "k"));
        field.getSubfields().add(new MarcSubField("x", "IDO200724"));

        MarcRecord r1 = new MarcRecord(Collections.singletonList(field));
        MarcRecord r2 = new MarcRecord(r1);

        assertThat(r2, notNullValue());
        assertThat(r1, equalTo(r2));
    }
}
