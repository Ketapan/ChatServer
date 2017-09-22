package Simple;

import Gui.ServerGraphicalUserInterface;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatServer implements Runnable {
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
            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
            start();
        } catch (IOException ioe) {
            System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());
        }
    }

    public void run() {
        //Solange der Server da ist wartet er auf neue clients
        while (thread != null) {
            try {
                System.out.println("Waiting for a client ...");
                addThread(server.accept());
            } catch (IOException ioe) {
                System.out.println("Server accept error: " + ioe);
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
        ServerGraphicalUserInterface.publicGUI.appendTextMessages(input);
        //Hier werden die Nachrichten verarbeitet
        if (input.equals("-bye")) {
            clients[findClientbyID(ID)].send("/bye");
            remove(ID);
            System.out.println("Client: " + ID + " disconnected.");
        } else if (input.equalsIgnoreCase("-who")) {
            int temp = clientCount;
            clients[findClientbyID(ID)].send("Users online: " + String.valueOf(clientCount));
            for (int i = 0; i < temp; i++) {
                clients[findClientbyID(ID)].send("- " + clients[i].getUsername());
            }
        } else if(input.equalsIgnoreCase("-pic")) {
            clients[findClientbyID(ID)].send("/pic");
            makeScreenshot(ID);
        }
        else if(input.startsWith("-pm:"))
        {
            String privateMessageTo;
            input = input.substring(input.indexOf(":"), input.length());
            privateMessageTo = input;
            privateMessageTo = privateMessageTo.substring(1, privateMessageTo.lastIndexOf(":"));
            input = input.substring(2, input.length());
            clients[findClientbyName(privateMessageTo.substring(1, privateMessageTo.length()))].send(input);
        }
        else {

            for (int i = 0; i < clientCount; i++) {
                clients[i].send(username + ": " + input);
            }
            System.out.println(username + ": " + input);
        }
    }

    private void makeScreenshot(int ID) {
        //macht einen Screenshot und schickt ihn an alle clients, macht immoment nur screenshot von server "perspektive"
        //clients sollen aber auch screenshots machen und diese versenden können
        //screenshot soll beim client dann auf der Form erscheinen und nicht hart auf die platte geschrieben werden
        try {
            byte[] imageInByte;

            Robot awt_robot = new Robot();
            BufferedImage screenshot = awt_robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
//            ImageIO.write(screenshot, "PNG", new File("C:\\Users\\aaron\\Desktop\\Entire_Screen.png"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenshot, "PNG", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();

            clients[findClientbyID(ID)].sendBytes(imageInByte);

        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void remove(int ID) {
        //Hier wird die ID aus der Liste entfernt
        int pos = findClientbyID(ID);
        if (pos >= 0) {
            ChatServerThread toTerminate = clients[pos];
            System.out.println("Removing client thread " + ID + " at " + pos);
            if (pos < clientCount - 1)
                for (int i = pos + 1; i < clientCount; i++)
                    clients[i - 1] = clients[i];
            clientCount--;
            try {
                toTerminate.close();
            } catch (IOException ioe) {
                System.out.println("Error closing thread: " + ioe);
            }
            toTerminate.stop();
        }
    }

    private void addThread(Socket socket) {
        //Benutzer wird hinzugefügt
        if (clientCount < clients.length) {
            System.out.println("Client accepted: " + socket);
            DataInputStream input = null;
            String username = "";
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
            clients[clientCount] = new ChatServerThread(this, socket, username);
            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            } catch (IOException ioe) {
                System.out.println("Error opening thread: " + ioe);
            }
        } else
            System.out.println("Client refused: maximum " + clients.length + " reached.");
    }

    public static void main(String args[]) {
        ChatServer server = new ChatServer();
        server.startServer(5555);
    }
}
