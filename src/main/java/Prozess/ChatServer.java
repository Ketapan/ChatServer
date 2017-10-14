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

    public synchronized void handle(int ID, String input, String username) {
        //Hier werden die Nachrichten verarbeitet
        if (input.equals("-bye") || input.equals("-exit") || input.equals("-quit")) {
            clients[findClientbyID(ID)].send("/bye");
            remove(ID);
            for (int i = 0; i < clientCount; i++) {
                clients[i].send("Client: " + username + " disconnected.");
            }
        }
        else {
            sendToAllClients(username + ": " + input);
        }
    }

    public synchronized void privatMessages(String privateMessageTo, String input, String username){
        ServerGraphicalUserInterface.publicGUI.appendTextMessages(username + ": " + input);
        if(input.startsWith(":-pic")){
            String temp = input;
            temp = temp.substring(5, temp.length());
//            BufferedImage bImageFromConvert = base64StringToImg(temp);
//            Toolkit toolkit = Toolkit.getDefaultToolkit();
//            Image img = toolkit.createImage(bImageFromConvert.getSource());
//            //Erzeuge die GUI
//            JFrame frame = new JFrame("Screenshot");
//            frame.getContentPane().add(new PicturePanel(img));
//            frame.setSize(800, 400);
//            frame.setLocationRelativeTo(null);
//            frame.setVisible(true);
//            temp = "";
//            temp = imgToBase64String(bImageFromConvert);
            temp = "/pic" + temp;
            clients[findClientbyName(privateMessageTo)].send(temp);
        } else if(input.startsWith("-msg")){
            
        }
        else {
            clients[findClientbyName(privateMessageTo)].send(username + input);
        }
    }

    public void refreshAllOnlineLists(){
        sendToAllClients("/refreshList");
        for(int i = 0; i < ServerGraphicalUserInterface.publicGUI.userListModel.getSize(); i++){
            sendToAllClients("/addwho" + ServerGraphicalUserInterface.publicGUI.userListModel.getElementAt(i));
        }
    }

    public void sendToAllClients(String input){
        for(int i = 0; i < clientCount; i++){
            clients[i].send(input);
        }
        ServerGraphicalUserInterface.publicGUI.appendTextMessages(input);
    }

    private void makeScreenshot(int ID) {
        try {
            byte[] imageInByte;

            Robot awt_robot = new Robot();
            BufferedImage screenshot = awt_robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

            String temp = imgToBase64String(screenshot);

            clients[findClientbyID(ID)].send("/pic" + temp);

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static String imgToBase64String(final RenderedImage img)
    {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try
        {
            ImageIO.write(img, "PNG", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        }
        catch (final IOException ioe)
        {
            throw new UncheckedIOException(ioe);
        }
    }

    public static BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public synchronized void remove(int ID) {
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
            sendToAllClients(username + " ging offline");
            try {
                toTerminate.close();
            } catch (IOException ioe) {
                ServerGraphicalUserInterface.publicGUI.appendTextMessages("Error closing thread: " + ioe);
            }
            toTerminate.stop();
        }
    }

    private void addThread(Socket socket) {
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
                sendToAllClients(username + " kam online.");

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
