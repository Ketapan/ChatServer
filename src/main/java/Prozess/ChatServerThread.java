package Prozess;

import Gui.ServerGraphicalUserInterface;

import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread {
    private ChatServer server = null;
    private Socket socket = null;
    private int ID = -1;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;
    private String username = "";

    public ChatServerThread(ChatServer _server, Socket _socket, String name) {
        super();
        server = _server;
        socket = _socket;
        ID = socket.getPort();
        username = name;
    }

    public void send(String msg) {
        //Konvertiert den String in bytes um und schickt die bytes an den client / an alle clients
        try {
            byte[] message = msg.getBytes();
            streamOut.writeInt(message.length);
            streamOut.write(message);
            streamOut.flush();
        } catch (IOException ioe) {
            ServerGraphicalUserInterface.publicGUI.appendTextMessages(ID + " ERROR sending: " + ioe.getMessage());
            server.remove(ID);
            stop();
        }
    }

    public void send(String msg, String username)
    {
        //Eventuell hier das senden der Privaten Nachrichten regeln
    }

    public void sendBytes(byte[] message){
        /*
            Hier werden die Bytes direkt versendet ohne Konvertierung
            Grund:
            -> Zum versenden des Screenshots da dieser direkt in bytes gewandelt wird
            Problem:
            -> So entstehen zwei Kanäle zum Senden von "nachrichten"
         */
        try{
            streamOut.writeInt(message.length);
            streamOut.write(message);
            streamOut.flush();
        } catch (IOException e)
        {
            ServerGraphicalUserInterface.publicGUI.appendTextMessages(ID + " ERROR sending: " + e.getMessage());
            server.remove(ID);
            stop();
        }
    }

    public int getID() {
        return ID;
    }

    public String getUsername() { return username; }

    public void run() {
        //Hier werden die bytes von den clients empfangen und in einen string konvertiert
        ServerGraphicalUserInterface.publicGUI.appendTextMessages("Server Thread " + ID + " running.");
        while (true) {
            try {
                int length = streamIn.readInt();
                String messageAsString = "";
                if(length > 0)
                {
                    byte[] messageBytes = new byte[length];
                    streamIn.readFully(messageBytes, 0, length);
                    messageAsString = new String(messageBytes);
                }
                server.handle(ID, messageAsString, username);
            } catch (IOException ioe) {
                ServerGraphicalUserInterface.publicGUI.appendTextMessages(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                stop();
                interrupt();
            }
        }
    }

    public void open() throws IOException {
        streamIn = new DataInputStream(new
                BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new
                BufferedOutputStream(socket.getOutputStream()));
    }

    public void close() throws IOException {
        if (socket != null) socket.close();
        if (streamIn != null) streamIn.close();
        if (streamOut != null) streamOut.close();
    }
}
