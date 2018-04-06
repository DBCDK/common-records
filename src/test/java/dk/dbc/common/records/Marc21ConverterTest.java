package dk.dbc.common.records;

import dk.dbc.common.records.utils.IOUtils;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class Marc21ConverterTest {

    @Test
    public void testCreateFromMarc21_Single_marc21_Record() throws Exception {
        InputStream in = getClass().getResourceAsStream("marc21_record.xml");
        MarcRecord rec = Marc21Converter.convertFromMarc21(IOUtils.readAll(in, "UTF-8"));

        assertEquals("005", rec.getControlFields().get(0).getName());
        assertEquals("20170929085245.0", rec.getControlFields().get(0).getValue());
        assertEquals("007", rec.getControlFields().get(1).getName());
        assertEquals("ta", rec.getControlFields().get(1).getValue());
        assertEquals("008", rec.getControlFields().get(2).getName());
        assertEquals("150709s1965 no#|||||||||||000|u|nob|d", rec.getControlFields().get(2).getValue());
        assertEquals("001", rec.getControlFields().get(3).getName());
        assertEquals("990000480974702201", rec.getControlFields().get(3).getValue());
    }
}
