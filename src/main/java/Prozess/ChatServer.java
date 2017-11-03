package Prozess;

import Gui.ServerGraphicalUserInterface;
import com.sun.security.ntlm.Server;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

public class ChatServer implements Runnable {
    //Server Variablen
    private ChatServerThread clients[] = new ChatServerThread[10];
    private ServerSocket server = null;
    private Thread thread = null;
    private int clientCount = 0;

    //TODO: Bytes senden (check)
    //TODO: Updater
    //TODO: Screenshot senden (GUI)
    //TODO: Privat nachrichten (GUI)
    //TODO: Prozess beenden (Task Liste)
    //TODO: Messagebox senden (GUI)
    //TODO: Programme starten
    //TODO: Keine Doppeltnamen

    public void startServer (int port)
    {
        //Startet Server und den Listener
        try {
            ServerGraphicalUserInterface.publicGUI.appendTextMessages("Binding to port " + port + ", please wait ...");
            server = new ServerSocket(port);
            ServerGraphicalUserInterface.publicGUI.appendTextMessages("Server started: " + server);
            System.out.println();
            start();
        } catch (IOException ioe) {
            ServerGraphicalUserInterface.publicGUI.appendTextMessages("Can not bind to port " + port + ": " + ioe.getMessage());
        }
    }

    public void run() {
        //Solange der Server da ist wartet er auf neue clients
        while (thread != null) {
            try {
                ServerGraphicalUserInterface.publicGUI.appendTextMessages("Waiting for a client ...");
                addThread(server.accept());
            } catch (IOException ioe) {
                ServerGraphicalUserInterface.publicGUI.appendTextMessages("Server accept error: " + ioe);
                stop();
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }

    private int findClientbyID(int ID) {
        //Geht die aktiven clients durch und sucht sie anhand der ID, ID wird vom Server vergeben
        for (int i = 0; i < clientCount; i++)
            if (clients[i].getID() == ID)
                return i;
        return -1;
    }

    private int findClientbyName(String username) {
        //Sucht die aktiven clients nach dem Benutzernamen
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].getUsername().equals(username)) {
                return i;
            }
        }
        return -1;
    }


    public void handleMessages(String messageTo, byte[] zipMessage){
        if(messageTo.equals("alle")){
            for(int i = 0; i < clientCount; i++){
                clients[i].sendByte(zipMessage);
            }
        } else {
            clients[findClientbyName(messageTo)].sendByte(zipMessage);
        }
    }

    public void refreshAllOnlineLists() throws IOException {
        sendToAllClients("/refreshList", "refreshList");
        for(int i = 0; i < ServerGraphicalUserInterface.publicGUI.userListModel.getSize(); i++){
            sendToAllClients(ServerGraphicalUserInterface.publicGUI.userListModel.getElementAt(i).toString(), "addwho");
        }
    }

    public void sendToAllClients(String input, String type) throws IOException {
        byte[] zipByteMessage = null;
        ZipMessage zipMSG = new ZipMessage();
        for(int i = 0; i < clientCount; i++){
            zipByteMessage = zipMSG.zip("alle".getBytes(), input.getBytes(), type.getBytes());
            clients[i].sendByte(zipByteMessage);
        }
        ServerGraphicalUserInterface.publicGUI.appendTextMessages(input);
    }

    public synchronized void remove(int ID) throws IOException {
        //Hier wird die ID aus der Liste entfernt
        int pos = findClientbyID(ID);

        String username = clients[pos].getUsername();
        ServerGraphicalUserInterface.publicGUI.userListModel.removeElement(username);

        if (pos >= 0) {
            ChatServerThread toTerminate = clients[pos];
            ServerGraphicalUserInterface.publicGUI.appendTextMessages("Removing client thread " + ID + " at " + pos);
            if (pos < clientCount - 1)
                for (int i = pos + 1; i < clientCount; i++){
                    clients[i - 1] = clients[i];
                }
            clientCount--;
            refreshAllOnlineLists();
            sendToAllClients(username + " ging offline", "msg");
            try {
                toTerminate.close();
            } catch (IOException ioe) {
                ServerGraphicalUserInterface.publicGUI.appendTextMessages("Error closing thread: " + ioe);
            }
            toTerminate.stop();
        }
    }

    private void addThread(Socket socket) throws IOException {
        //Benutzer wird hinzugefügt
        if (clientCount < clients.length) {
            ServerGraphicalUserInterface.publicGUI.appendTextMessages("Client acepted: " + socket);
            DataInputStream input = null;
            String username = "";
            int ID;

            try {
                input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                int length = input.readInt();
                if (length > 0) {
                    byte[] message = new byte[length];
                    input.readFully(message, 0, message.length);
                    username = new String(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(!isUsernameForgive(username)){
                clients[clientCount] = new ChatServerThread(this, socket, username);

                try {
                    clients[clientCount].open();
                    clients[clientCount].start();
                    clientCount++;
                } catch (IOException ioe) {
                    ServerGraphicalUserInterface.publicGUI.appendTextMessages("Error opening thread: " + ioe);
                }

                ServerGraphicalUserInterface.publicGUI.userListModel.addElement(username);
                refreshAllOnlineLists();
                sendToAllClients(username + " kam online.", "msg");

            } else {
                clients[clientCount] = new ChatServerThread(this, socket, "BEREITSVERGEBEN");
                try {
                    clients[clientCount].open();
                    clients[clientCount].start();
                    clientCount++;
                } catch (IOException ioe) {
                    ServerGraphicalUserInterface.publicGUI.appendTextMessages("Error opening thread: " + ioe);
                }
                ID = clients[findClientbyName("BEREITSVERGEBEN")].getID();
                clients[findClientbyName("BEREITSVERGEBEN")].send("/vergeben");
                remove(ID);
            }
        } else {
            ServerGraphicalUserInterface.publicGUI.appendTextMessages("Client refused: maximum " + clients.length + " reached.");
        }
    }

    private boolean isUsernameForgive(String username){
        boolean isForgive = false;

        String user;
        int z = 0;
        for(int i = 0; i < ServerGraphicalUserInterface.publicGUI.userListModel.getSize(); i++){
            user = String.valueOf(ServerGraphicalUserInterface.publicGUI.userListModel.getElementAt(i));
            if(user.equalsIgnoreCase(username)){
                z++;
            }
        }

        if(z > 0){
            isForgive = true;
        }

        return isForgive;
    }
}
