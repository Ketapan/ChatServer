package Prozess;

import Gui.ServerGraphicalUserInterface;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ChatServerThread extends Thread {
    private ChatServer server = null;
    private Socket socket = null;
    private int ID = -1;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;
    private String username = "";
    private MessageProzess messageProcessing = new MessageProzess();

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
//                String messageAsString = "";
                byte[] messageBytes = null;
                if (length > 0) {
                    messageBytes = new byte[length];
                    System.out.println(Arrays.toString(messageBytes));
                    streamIn.readFully(messageBytes, 0, length);
                    messageProcessing.messageHandling(messageBytes, getUsername(), getID());
//                    messageAsString = new String(messageBytes);
                }
//                if (messageAsString.startsWith("-pm")) {
//                    String privatMessageTo;
//                    String input = messageAsString;
//                    privatMessageTo = input.substring(3, input.indexOf(":"));
//                    System.out.println(privatMessageTo);
//                    input = input.substring(input.indexOf(":"), input.length());
//                    server.privatMessages(privatMessageTo, input, username);
//                } else {
//                    server.handle(ID, messageAsString, username);
//                }

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
