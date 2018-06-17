//package chatapplication;

import com.sun.deploy.util.SessionState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;
  //  Date date = new Date();
  //  SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");



    private ChatServer(int port) {
        this.port = port;
    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private synchronized void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                Socket socket = serverSocket.accept();
                // server waits until a client opens a Socket with the same address and port number

                // Socket socket = serverSocket.accept();
                ClientThread r = new ClientThread(socket, uniqueId++);
              //  System.out.println("After connection : "+ uniqueId);
                Thread t = new Thread(r);
                clients.add(r);
             //   System.out.println("Client's size: " + clients.size());
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void broadcast(String message)
    {
        for(int i = 0; i < clients.size(); i++)
        {
            clients.get(i).writeMessage(message);
        }

    }


    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static synchronized void main(String[] args) {
        ChatServer server = new ChatServer(1500);
        server.start();
    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private synchronized boolean writeMessage(String msg) {

            //msg = cm.getMessage();

            if(socket.isConnected())
            {
                try {
                    System.out.print(username+": "+ msg);
                    sOutput.writeObject(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            else
            {
                return false;
            }

        }

        private synchronized void remove(int id)
        {
            clients.remove(clients.get(id));
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            boolean run = true;
            while (run)
            {
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(cm.getType() == 1)
                {
                    run = false;
                    try {
                        socket.close();
                        sOutput.close();
                        sInput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                  //  System.out.print(username);
                   // System.out.println(cm.getMessage());
                    System.out.println(clients.size());
                    broadcast(cm.getMessage());
                   // writeMessage(cm.getMessage());
                }
                // Send message back to the client
                /*try {

                   // sOutput.writeObject(username+": "+cm.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            }
        }
    }
}
