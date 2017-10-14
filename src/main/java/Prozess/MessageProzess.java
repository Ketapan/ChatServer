package Prozess;

import Gui.ServerGraphicalUserInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class MessageProzess {

    public void messageHandling(byte[] messageBytes, String username, int ID) {
        ArrayList<String> messageList = new ArrayList<>();
        String messageTo = "";
        String message = "";
        String type = "";

        try {
            messageList = unzip(messageBytes);
            if(messageList.size() == 3){
                messageTo = messageList.get(0);
                message = messageList.get(1);
                type = messageList.get(2);

                //verschiedene methoden aufrufen jenach type der message
                //oder immer die gleiche methode aufrufen unnd mit einer switch-case anweisung dann weitere methoden aufrufen
            }
        } catch (IOException e) {
            ServerGraphicalUserInterface.publicGUI.appendTextMessages(e.getLocalizedMessage());
        }
    }


    private byte[] zip(byte[] messageTo, byte[] message, byte[] type) throws IOException
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

    private ArrayList<String> unzip (byte[] pDaten) throws IOException
    {
        InputStream input = new ByteArrayInputStream(pDaten);
        byte[] daten = new byte[2048];
        ZipInputStream zip = new ZipInputStream(input);
        int length;
        ArrayList<String> ergebnisListe = new ArrayList<>();

        while((zip.getNextEntry()) != null){
            length = zip.read(daten);
            byte[] unzipByte = new byte[length];
            System.arraycopy(daten, 0, unzipByte, 0, length);
            ergebnisListe.add(new String(unzipByte));
        }

        zip.close();
        input.close();

        return ergebnisListe;
    }
}
