/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szallodanyilvantartorendszer.szallide;


import Connector.ServerConnection;
import Connector.DatabaseConnection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Johika
 */
public class Server {

    int port;
    ServerSocket serverSock;    //szerveroldali végpont
    Socket clientSocket;        //kliens oldali végpont
    int numOfConnections;
    DataInputStream streamIn;
    DataOutputStream streamOu;

    public Server(int port) {
        numOfConnections = 0;
        this.port = port;
    }

    public static void main(String args[]) {
        DatabaseConnection.connect();
        new Server(1112).startServer(); //server objektum készítése, alapértelemezett porttal
        DatabaseConnection.close();
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void startServer() {
        try {
            serverSock = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("***Server is running***");

        while (true) {
            try {
                clientSocket = serverSock.accept(); //kapcsolat elfogadása
                streamIn = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                streamOu = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                ServerConnection oneconnection = new ServerConnection(clientSocket, numOfConnections, streamIn, streamOu, this);
                numOfConnections++;
                new Thread(oneconnection).start();  //új szál
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
