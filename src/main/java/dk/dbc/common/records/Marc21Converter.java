package dk.dbc.common.records;


import dk.dbc.common.records.marc21.CollectionType;
import dk.dbc.common.records.marc21.ControlFieldType;
import dk.dbc.common.records.marc21.DataFieldType;
import dk.dbc.common.records.marc21.ObjectFactory;
import dk.dbc.common.records.marc21.RecordType;
import dk.dbc.common.records.marc21.RecordTypeType;
import dk.dbc.common.records.marc21.SubfieldatafieldType;
import org.w3c.dom.Document;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Marc21Converter {

    public static MarcRecord convertFromMarc21(String xml) {
        // Try to unmarshal a collection
        CollectionType collection = JAXB.unmarshal(new StreamSource(new StringReader(xml)), CollectionType.class);
        if (!collection.getRecord().isEmpty()) {
            return convertFromRecordType(collection.getRecord().get(0));
        }
        // Try to unmarshal a single record
        RecordType record = JAXB.unmarshal(new StreamSource(new StringReader(xml)), RecordType.class);
        return convertFromRecordType(record);
    }

    /**
     * @param xml Some marcxchange xml.
     * @return A MarcRecord. If no records are found, when we returns an
     * empty MarcRecord.
     * @brief Constructs a MarcRecord from a Source that contains an
     * marcxchange xml document.
     */
    public static MarcRecord createFromMarc21(Source xml) {
        // Try to unmarshal a collection
        CollectionType collection = JAXB.unmarshal(xml, CollectionType.class);
        if (!collection.getRecord().isEmpty()) {
            return convertFromRecordType(collection.getRecord().get(0));
        }

        // Try to unmarshal a single record
        RecordType record = JAXB.unmarshal(xml, RecordType.class);
        return convertFromRecordType(record);
    }

    /**
     * @param record Marcxchange record.
     * @return A MarcRecord.
     * @brief Constructs a MarcRecord from a RecordType that contains a
     * record from a marcxchange xml.
     */
    public static MarcRecord createFromMarc21e(RecordType record) {
        return convertFromRecordType(record);
    }

    public static Document convertToMarc21AsDocument(MarcRecord record) throws JAXBException, ParserConfigurationException {
        RecordType marcXhangeType = Marc21Factory.createMarc21FromMarc(record);
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<RecordType> jAXBElement = objectFactory.createRecord(marcXhangeType);

        JAXBContext jc = JAXBContext.newInstance(RecordType.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.loc.gov/MARC21/slim");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        marshaller.marshal(jAXBElement, document);
        return document;
    }

    /**
     * @brief Constructs a MarcRecord from a RecordType.
     */
    private static MarcRecord convertFromRecordType(RecordType rt) {
        MarcRecord record = new MarcRecord();

        record.setLeader(rt.getLeader().getValue());

        if (rt.getType() != null) {
            record.setType(rt.getType().value());
        }

        List<MarcField> fields = new ArrayList<>();
        for (DataFieldType df : rt.getDatafield()) {
            fields.add(convertFromDataFieldType(df));
        }
        record.getFields().addAll(fields);

        List<MarcControlField> marcControlFieldList = new ArrayList<>();
        for (ControlFieldType cft : rt.getControlfield()) {
            marcControlFieldList.add(convertFromControlFieldType(cft));
        }
        record.setControlFields(marcControlFieldList);

        return record;
    }

    /**
     * @brief Constructs a MarcField from a DataFieldType.
     * <p/>
     * The indicator of the field is generated from attributes ind1 - ind9
     * of DataFieldType.
     */
    private static MarcField convertFromDataFieldType(DataFieldType df) {
        MarcField mf = new MarcField(df.getTag(), "");
        String indicator = "";
        if (df.getInd1() != null) {
            indicator += df.getInd1();
        }
        if (df.getInd2() != null) {
            indicator += df.getInd2();
        }
        mf.setIndicator(indicator);

        ArrayList<MarcSubField> sfl = new ArrayList<>();
        for (SubfieldatafieldType sf : df.getSubfield()) {
            sfl.add(convertFromSubfieldDataFieldType(sf, df));
        }
        mf.setSubfields(sfl);
        return mf;
    }

    private static MarcControlField convertFromControlFieldType(ControlFieldType controlFieldType) {
        MarcControlField controlField = new MarcControlField(controlFieldType.getTag(), controlFieldType.getValue());

        return controlField;
    }


    /**
     * @brief Constructs a MarcSubField from a SubfieldatafieldType.
     */
    private static MarcSubField convertFromSubfieldDataFieldType(SubfieldatafieldType sf, DataFieldType df) throws IllegalArgumentException {
        String name = sf.getCode();
        String val = sf.getValue();
        if (name.length() > 1) {
            throw new IllegalArgumentException("Subfield name cannot exceed one char. Field [" + df.getTag() + "], subfield name [" + name + "], subfield value [" + val + "]");
        }
        return new MarcSubField(name.trim(), val.trim());
    }

}
