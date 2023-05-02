package dk.dbc.common.records;

import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.reader.MarcReaderException;
import dk.dbc.marc.reader.MarcXchangeV1Reader;
import dk.dbc.marc.writer.MarcXchangeV1Writer;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class RecordContentTransformer {

    public static MarcRecord decodeRecord(byte[] content) throws MarcReaderException {
        final ByteArrayInputStream buf = new ByteArrayInputStream(content);
        final MarcXchangeV1Reader reader = new MarcXchangeV1Reader(buf, StandardCharsets.UTF_8);

        return reader.read();
    }

    public static byte[] encodeRecord(MarcRecord marcRecord) {
        MarcXchangeV1Writer marcXchangeV1Writer = new MarcXchangeV1Writer();
        return marcXchangeV1Writer.write(marcRecord, StandardCharsets.UTF_8);
    }
}
