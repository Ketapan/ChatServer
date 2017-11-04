package Gui;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import Listener.StartServerActionListener;
import Listener.StopServerActionListener;
import Prozess.ChatServer;
import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;

/**
 * Created by Darth Vader on 22.09.2017.
 */
public class ServerGraphicalUserInterface {
    public static ServerGraphicalUserInterface publicGUI;

    public JPanel panel;
    public JButton Start;
    public JButton stopServerButton;
    public JTextArea textArea_sendMessages;
    public JList userList;
    public DefaultListModel userListModel = new DefaultListModel();


    public static void main(String[] args)
    {
        ServerGraphicalUserInterface mainGUI = new ServerGraphicalUserInterface();
        mainGUI.guiLoad();
    }

    public void guiLoad()
    {
        //Try-Block = Windows Design
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Erzeuge die GUI
        publicGUI = new ServerGraphicalUserInterface();
        JFrame frame = new JFrame("Server");
        frame.setPreferredSize(new Dimension(700,450));
        frame.setContentPane(publicGUI.panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(frame.getSize());
        frame.setMinimumSize(frame.getSize());
        //Eigenschaften der Componenten festlegen

        publicGUI.textArea_sendMessages.setEditable(false);
        publicGUI.textArea_sendMessages.setWrapStyleWord(true);
        publicGUI.textArea_sendMessages.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) publicGUI.textArea_sendMessages.getCaret(); //Auto Scroll von dem Update Log
        caret.setUpdatePolicy(ALWAYS_UPDATE);

        publicGUI.userList.setModel(publicGUI.userListModel);
        publicGUI.userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        publicGUI.userList.setLayoutOrientation(JList.VERTICAL);
        publicGUI.userList.setVisibleRowCount(-1);

        frame.setVisible(true);


    }

    public void appendTextMessages(String message) {
        publicGUI.textArea_sendMessages.append(message + "\n");
    }

    //Listener
    public ServerGraphicalUserInterface() {

        Start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StartServerActionListener ssal = new StartServerActionListener();
                ssal.startServer();
            }
        });

        stopServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StopServerActionListener stopServerListener = new StopServerActionListener();
                /*
                    TODO: Server stoppen muss auf dem gleichen Objekt pasieren wie das starten
                            Gleichzeitig sollte auch die Verbindung zu jedem client sauber getrennt werden
                            -> /bye an alle clients senden mit noch einer nachricht davor irgendwwie Server stoppt jetzt oder so
                 */
            }
        });
    }
}
