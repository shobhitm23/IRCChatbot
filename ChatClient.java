//package chatapplication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;
    static Scanner sc;

    private final String server;
    private final String username;
    private final int port;
    private static String msg = "x";

    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");


    private ChatClient(String server, int port, String username) {
        this.server = server;
        this.port = port;
        this.username = username;
        sc = new Scanner(System.in);

    }

    /*
     * This starts the Chat Client
     */
    private synchronized boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create your input and output streams
        try {

            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }



        // This thread will listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            sOutput.writeObject(sdf.format(date)+" "+username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /*
     * This method is used to send a ChatMessage Objects to the server
     */
    private synchronized void sendMessage(ChatMessage msg) {

        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    private synchronized void close() throws IOException {
        socket.close();
    }
    public static void main(String[] args) {
        // Get proper arguments and override defaults
        while (true) {
        String username = "Anonymous";
        int portNumber = 1500;
        String serverAddress = "localhost";

        /*switch (args.length)
        {
            case 1:
                 username = args[0];
                break;

            case 2:
                username = args[0];
                portNumber = Integer.parseInt(args[1]);
                break;

            case 3:
                username = args[0];
                portNumber = Integer.parseInt(args[1]);
                serverAddress = args[2];
        }*/


        // Create your client and start it
        ChatClient client = new ChatClient(serverAddress, portNumber, username);

            client.start();

            // Send an empty message to the server

            msg = sc.nextLine();
            client.sendMessage(new ChatMessage(0, msg));
        }
    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

        public void run() {

            try {
                /*while(true) {
                    Socket socket = serverSocket.accept();
                    // server waits until a client opens a Socket with the same address and port number
                }*/

                while (true & !ChatClient.msg.equalsIgnoreCase("/logout"))
                {
                    String msg =(String) sInput.readObject();
                    System.out.println(sdf.format(date) +" " + username + ": "+ msg);
                }

            }
            catch (IOException|ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}
