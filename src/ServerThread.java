/**
 * UNIVERSIDAD DE COSTA RICA
 * ESCUELA DE CIENCIAS DE LA COMPUTACION E INFORMATICA
 * CI-1310 SISTEMAS OPERATIVOS
 * SEGUNDA TAREA PROGRAMADA, CARLOS DELGADO ROJAS (B52368)
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerThread extends Thread {

    private Socket clientSocket; //
    private String sword; //
    private PrintWriter out; //
    private BufferedReader in; //
    private boolean hacked; //

    /**
     * Constructor
     *
     * @param clientSocket the client socket
     * @param sword        the secret word
     */
    public ServerThread(Socket clientSocket, String sword) {
        try {
            this.clientSocket = clientSocket;
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.sword = sword;
            this.hacked = false;
        } catch (Exception e) {
            System.out.println("Error. See: " + e.getCause());
        }
    }

    /**
     * Returns true if the secret word has been hacked
     *
     * @return true if the word has been hacked
     */
    public boolean isHacked() {
        return hacked;
    }

    @Override
    public void run() {
        try {
            String inputLine;
            // Initiate conversation with client
            out.println(getHash(this.sword));
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals(this.sword)) {
                    out.println("Congratulations! The password has been hacked");
                    Server.passHacked++;
                    this.hacked = true;
                    break;
                } else {
                    out.println("Oh no! The password is incorrect");
                }
            }
        } catch (Exception e) {
            System.out.println("The connection has been lose");
            this.hacked = true;
        }
    }

    /**
     * Returns the hash associated with an argument
     *
     * @param s the string to be hash
     * @return the hash associated with the argument
     * @throws NoSuchAlgorithmException
     */
    public static String getHash(String s) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(s.getBytes(), 0, s.length());
        return new BigInteger(1, m.digest()).toString(16);
    }
}

