package Gui.Messages;

import Gui.ServerGraphicalUserInterface;
import Prozess.ChatServer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;


public class HandleMessages {

    public void messageHandling(byte[] messageBytes, String username, int ID) {
        /**
         * als Parameter: die Message als Byte, den Username, die ID
         *
         * Type Möglichkeiten:
         * - pm
         * - exit
         *
         * else:
         * - nachricht an alle
         */
        ArrayList<String> messageList;
        String messageTo;
        String message;
        String type;
        ChatServer server = new ChatServer();

        try {
            messageList = unzip(messageBytes);
            if(messageList.size() == 3){
                messageTo = messageList.get(0);
                message = messageList.get(1);
                type = messageList.get(2);

                //verschiedene methoden aufrufen jenach type der message
                //oder immer die gleiche methode aufrufen unnd mit einer switch-case anweisung dann weitere methoden aufrufen

                if(type.equalsIgnoreCase("pm")){
                    server.privatMessages(messageTo, message);
                } else if(type.equalsIgnoreCase("exit"))
                {
                    server.closeClientConnection(ID, username);
                }
                else {
                    server.sendToAllClients(message);
                }
            } else {
                ServerGraphicalUserInterface.publicGUI.appendTextMessages("Fehler beim einlesen der Nachricht.");
            }
        } catch (IOException e) {
            ServerGraphicalUserInterface.publicGUI.appendTextMessages(e.getLocalizedMessage());
        }
    }

    private ArrayList<String> unzip(byte[] zipBytes) throws IOException
    {
        ArrayList<String> ergebnisListe = new ArrayList<>();

        if(zipBytes != null){
            InputStream input = new ByteArrayInputStream(zipBytes);
            byte[] daten = new byte[2048];
            ZipInputStream zip = new ZipInputStream(input);
            int length;

            while((zip.getNextEntry()) != null){
                length = zip.read(daten);
                byte[] unzipByte = new byte[length];
                System.arraycopy(daten, 0, unzipByte, 0, length);
                ergebnisListe.add(new String(unzipByte));
            }

            zip.close();
            input.close();
        } else{
            ServerGraphicalUserInterface.publicGUI.appendTextMessages("Fehler beim Entpacken der Nachricht (byte == null)");
        }

        return ergebnisListe;
    }
}
