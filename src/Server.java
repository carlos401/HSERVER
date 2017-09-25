/**
 * UNIVERSIDAD DE COSTA RICA
 * ESCUELA DE CIENCIAS DE LA COMPUTACION E INFORMATICA
 * CI-1310 SISTEMAS OPERATIVOS
 * SEGUNDA TAREA PROGRAMADA, CARLOS DELGADO ROJAS (B52368)
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private List<String> swords; //saves the secret words to be hack
    private int portNumber; //saves always the number of port communication
    private ServerSocket serverSocket; //own serverSocket

    /**
     * The number of passwords hacked
     */
    public static int passHacked; // the number of passwords hacked
    private List<ServerThread> threads; //the list of server threads

    /**
     * The constructor
     * @param portNumber the port where the connection will occur
     */
    public Server(int portNumber) {
        try {
            this.swords = new ArrayList<>(5);
            this.put_words(); //saves the secret words
            this.portNumber = portNumber;
            this.serverSocket = new ServerSocket(portNumber);
            this.passHacked = 0;
            this.threads = new LinkedList<>();
        } catch (Exception e) {
            System.out.println("Could not create the Server");
        }
    }

    /**
     * Put the five secret words in the list
     */
    private void put_words() {
        this.swords.add("Luz");
        this.swords.add("Vida");
        this.swords.add("Amor");
        this.swords.add("Humanismo");
        this.swords.add("Ciencia");
    }

    /**
     * Returns a random word from the list
     * @return a secret word
     */
    private String get_randomWord() {
        Random rand = new Random();
        return this.swords.get(rand.nextInt(this.swords.size()));
    }

    /**
     * Update the list of ServerThreads
     * @return the number of clients already connected
     */
    public int updateClients() {
        Iterator<ServerThread> ite = this.threads.iterator();
        ServerThread sthr; //to access each thread
        while (ite.hasNext()) {
            sthr = ite.next();
            if (sthr.isHacked()) {
                this.threads.remove(sthr);
            }
        }
        return this.threads.size();
    }

    /**
     * Begins the server execution
     * @return false, when the app will be closed
     */
    public boolean start_server() {
        Receiver rec = new Receiver(); //thread that receives requests from clients
        rec.start();
        String commandLine; //to read console line
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("admin >> ");
                commandLine = console.readLine();
                switch (commandLine) { //add more admin commands here
                    case "exit":
                        return false;
                    case "clients":
                        System.out.println("Clients connected: " + this.updateClients());
                        break;
                    case "hacked":
                        System.out.println("Passwords hacked: " + this.passHacked);
                        break;
                    case "man":
                        System.out.println("Use the command [exit] to shutdown the server");
                        System.out.println("Use the command [clients] to print the number of clients connected");
                        System.out.println("Use the command [hacked] to print the number of words hacked");
                        break;
                    default:
                        System.out.println("Notice: Unknown command");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error reading the console. See: " + e.getCause());
                return false;
            }
        }
    }

    /**
     * Prints the welcome message in console
     */
    public void print_welcomeMsg() {
        System.out.println("\nHACKED-SERVER, CREATED BY CARLOS A. DELGADO ROJAS");
        System.out.println("carlos.delgadorojas@ucr.ac.cr");
        System.out.println("Version 1.1");
        System.out.println("\nNotice: The server is ready on port: " + portNumber);
        System.out.println("Notice: Use the command [man] to get help");
    }

    public static void main(String args[]) {
        Server server = new Server(Integer.parseInt(args[0]));
        server.print_welcomeMsg();
        if (!server.start_server()) {
            System.out.println("Notice: Shutting down...");
        }
        System.exit(1);
    }

    class Receiver extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("\nNotice: New client has been connected");
                    System.out.print("admin >> ");
                    ServerThread sth = new ServerThread(clientSocket, get_randomWord());
                    sth.start();
                    threads.add(sth);
                }
            } catch (Exception e) {
                System.out.println("Error in the process. See: " + e.getCause());
            }
        }
    }
}

