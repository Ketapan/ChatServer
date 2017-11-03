package Prozess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipMessage {

    public byte[] zip(byte[] messageTo, byte[] message, byte[] type) throws IOException
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream( output );

        zip.setMethod( ZipOutputStream.DEFLATED );

        ZipEntry entry = new ZipEntry("messageTO");
        ZipEntry entry2 = new ZipEntry("message");
        ZipEntry entry3 = new ZipEntry("type");

        zip.putNextEntry( entry );
        zip.write(messageTo);
        zip.closeEntry();

        zip.putNextEntry(entry2);
        zip.write(message);
        zip.closeEntry();

        zip.putNextEntry(entry3);
        zip.write(type);
        zip.closeEntry();

        byte[] bytes = output.toByteArray();

        zip.close();
        output.close();

        return bytes;
    }
}
