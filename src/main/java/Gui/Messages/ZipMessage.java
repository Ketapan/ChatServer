package Gui.Messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipMessage {

    public byte[] zipAndSendBytes(byte[] byteMessageTo, byte[] byteMessage, byte[] byteType) throws IOException
    {
        byte[] byteSend = null;
        if(byteMessageTo != null && byteMessage != null && byteType != null){
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream( output );

            zip.setMethod( ZipOutputStream.DEFLATED );

            ZipEntry entry = new ZipEntry("messageTO");
            ZipEntry entry2 = new ZipEntry("message");
            ZipEntry entry3 = new ZipEntry("type");

            zip.putNextEntry( entry );
            zip.write(byteMessageTo);
            zip.closeEntry();

            zip.putNextEntry(entry2);
            zip.write(byteMessage);
            zip.closeEntry();

            zip.putNextEntry(entry3);
            zip.write(byteType);
            zip.closeEntry();

            byteSend = output.toByteArray();

            zip.close();
            output.close();
        }

        return byteSend;
    }
}
