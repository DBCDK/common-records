package dk.dbc.common.records;

public class MarcConverter {

    public static MarcRecord convertFromXML(String xml) throws Exception {
        if(xml.contains("MARC21/slim")) {
            return Marc21Converter.convertFromMarc21(xml);
        } else if (xml.contains("marcxchange-v1")) {
            return MarcXConverter.convertFromMarcXChange(xml);
        } else {
            throw new Exception("Unknown XML type");
        }
    }

}
