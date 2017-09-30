package Listener;

import Prozess.ChatServer;

public class StartServerActionListener {

    public void startServer()
    {
        Prozess.ChatServer sc = new ChatServer();
        sc.startServer(5555);
    }
}
