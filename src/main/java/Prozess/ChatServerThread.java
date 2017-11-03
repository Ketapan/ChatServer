package Prozess;

import Gui.ServerGraphicalUserInterface;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

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

    public void sendByte(byte[] msgBytes) throws IOException {
        try {
            streamOut.writeInt(msgBytes.length);
            streamOut.write(msgBytes);
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
                UnzipMessage unzipMSG = new UnzipMessage();
                int length = streamIn.readInt();
                String messageTo;
                String message;
                ArrayList messageList;
                byte[] messageBytes;

                if(length > 0)
                {
                    messageBytes = new byte[length];
                    streamIn.readFully(messageBytes, 0, length);
                    messageList = unzipMSG.unzip(messageBytes);
                    messageTo = messageList.get(0).toString();
                    message = messageList.get(1).toString();

                    server.handleMessages(messageTo, messageBytes);
                    ServerGraphicalUserInterface.publicGUI.appendTextMessages(message);
                }

            } catch (IOException ioe) {
                ServerGraphicalUserInterface.publicGUI.appendTextMessages(ID + " ERROR reading: " + ioe.getMessage());
                try {
                    server.remove(ID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
