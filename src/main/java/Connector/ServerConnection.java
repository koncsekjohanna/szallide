package Connector;

import Protokoll.Body;
import Protokoll.Head;
import Protokoll.Message;
import Protokoll.ProtokollUzenetek;
import static Handler.ServerMuveletFeldolgozas.FeladatVegrehajtas;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szallodanyilvantartorendszer.szallide.Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection implements Runnable {

    DataOutputStream os;
    Socket clientSocket;
    int id;
    Server server;
    static int connectionCounter = 0;
    DataInputStream input;
    boolean clientConnected;

    public ServerConnection(Socket clientSocket, int id, DataInputStream streamIn, DataOutputStream os, Server server) {
        this.clientSocket = clientSocket;
        this.id = id;
        this.server = server;
        this.input = streamIn;
        this.os = os;
        clientConnected = true;
        connectionCounter++;
        System.out.println("Number of connections: " + connectionCounter);
        System.out.println("Connection with  " + id + ". client: " + clientSocket + " established");
    }

    @Override
    public void run() {
        // itt kell  majd használni a kapott üzenetet a kliens felől és 
        // Switch - case szerkezetben szépen a megfelelő függvényeket meghívogatni
        String line;
        ObjectMapper mapper = new ObjectMapper();
        try {
            os.writeUTF(mapper.writeValueAsString(new Message(new Head(ProtokollUzenetek.Tipusok.Csatlakozas, ProtokollUzenetek.Feladatok.FeladatBefejezes), new Body())));
            os.flush(); //csatlakozás visszaigazolás szerver felé
            while (clientConnected) {
                // uzenet elkapasa a kliens felol
                line = input.readUTF();
                System.out.println("Message from " + id + ". client is: " + line);
                Message valaszUzenet;

                Message message = mapper.readValue(line, Message.class);

                // uzenet feldolgozasa
                if (message.getHead().getTipus().equals("5")) {
                    connectionCounter--;
                    System.out.println("Number of connections: " + connectionCounter);
                    clientConnected = false;
                } else {
                    valaszUzenet = FeladatVegrehajtas(message);
                    // valasz a kliensnek
                    os.writeUTF(mapper.writeValueAsString(valaszUzenet));
                    os.flush();
                    System.out.println("Server message to client " + id + ".: " + mapper.writeValueAsString(valaszUzenet));
                }
            }

            
            input.close();
            os.close();
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("***Connection closed for client " + id + "***");
        }
    }

}
