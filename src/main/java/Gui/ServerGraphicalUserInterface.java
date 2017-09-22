package Gui;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Simple.ChatServer;
import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;

/**
 * Created by Darth Vader on 22.09.2017.
 */
public class ServerGraphicalUserInterface {
    public static ServerGraphicalUserInterface publicGUI;
    public JPanel panel;
    private JButton Start;
    private JButton stopServerButton;
    private JTextArea textArea2;
    private JList list1;

    public ServerGraphicalUserInterface() {

        Start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Simple.ChatServer sc = new ChatServer();
                sc.startServer(5555);
            }
        });

        stopServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Simple.ChatServer sc = new ChatServer();
                sc.stop();
            }
        });
    }

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
        JFrame frame = new JFrame("Client");
        frame.setPreferredSize(new Dimension(700,450));
        frame.setContentPane(publicGUI.panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(frame.getSize());
        frame.setMinimumSize(frame.getSize());

        //Eigenschaften der Componenten festlegen
        publicGUI.textArea2.setEditable(false);
        publicGUI.textArea2.setWrapStyleWord(true);
        publicGUI.textArea2.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) publicGUI.textArea2.getCaret(); //Auto Scroll von dem Update Log
        caret.setUpdatePolicy(ALWAYS_UPDATE);

        frame.setVisible(true);
    }

    public void appendTextMessages(String message) {
        publicGUI.textArea2.append(message + "\n");
    }
    public void appendUsers(String message){
        //publicGUI.list1.add(message);
    }
}
