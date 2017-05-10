/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.szallodanyilvantartorendszer.szallide;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import DataSet.Szerepkorok;
import DataSet.Felhasznalo;
import Handler.MenuManager;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import Protokoll.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.TimeZone;


/**
 *
 * @author Johika
 */
public class Client
{
    
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static Felhasznalo ACTUAL_USER = null;
    Felhasznalo user = null;

    public static Felhasznalo getACTUAL_USER() {
        return ACTUAL_USER;
    }

    public static void setACTUAL_USER(Felhasznalo ACTUAL_USER) {
        Client.ACTUAL_USER = ACTUAL_USER;
    }
    
    

    public Message Bejelentkezes () throws IOException{
       // df.setTimeZone(TimeZone.getDefault());
        String felhasznalonev, jelszo;
        System.out.println("\n*********************************\n\t*");
        System.out.print("* Felhasznalonev: ");
        felhasznalonev = br.readLine();
        System.out.print("\t*\n* Jelszo: ");
        jelszo = br.readLine();
        System.out.println("\t*\n*********************************");
        
        ArrayList<String> felhasznaloiAdatok = new ArrayList<>();
        felhasznaloiAdatok.add(felhasznalonev.trim());
        felhasznaloiAdatok.add(jelszo.trim());
        Message bejelentkezes = new Message(
                new Head(ProtokollUzenetek.Tipusok.Bejelentkezes, null),
                new Body(felhasznaloiAdatok)
        );
        
        return bejelentkezes;
    }

    //ClientMuveletFeldolgozas processor = new ClientMuveletFeldolgozas();

    private void ClientRun()
    {
        DataOutputStream outputstr = null;  //tartalom kiiratásához
        Socket clientSocket = null; //végpont létérehozása
        DataInputStream input = null; //beolvasáshoz
        ObjectMapper mapper = new ObjectMapper();

        try
        {
            clientSocket = new Socket("localhost", 1112); //új végpont létrehozása
            outputstr = new DataOutputStream(clientSocket.getOutputStream()); //tartalom csatorna
            input = new DataInputStream(clientSocket.getInputStream());
        }
        catch (UnknownHostException e)
        {
            System.out.println("***Localhost error***");
        }
        catch (IOException e)
        {
            System.out.println("***Cannot connect***");
        }

        if (clientSocket == null || outputstr == null || input == null)
        { //ha nincs végpont vagy tartalom vagy bemenet
            System.out.println("***Error occured***");
            return;
        }

        String szerverValasz;
        Message sm = new Message(new Head(), new Body());
        try
        {
            // *** Bejelentkeztetés *** //
            while (user == null)
            {
                // szerver oldalról érkező ACK fogadás a kapcsolódás nyugtázására
                szerverValasz = input.readUTF();
                sm = mapper.readValue(szerverValasz, Message.class);

                // Ha megkaptam az ACK-ot, nem csinálok semmit, engedem futni a programot. Ha nem, nincs 
                // kapcsolat, szóval hibát dobok
                if (sm.getHead().getTipus() != ProtokollUzenetek.Tipusok.Csatlakozas)
                {
                    throw new Exception("!!! Nem lehet a szerverhez csatlakozni !!!");
                }

                // Összeszedem a bejelentkezési adatokat, majd elküldöm a szerver felé
               // Message cm = Bejelentkezes(new Message(new Head(), new Body()));
               Message cm = Bejelentkezes();
                String jsonString = mapper.writeValueAsString(cm);
                outputstr.writeUTF(jsonString); //kiiratás
                outputstr.flush();

                // A bejelentkezési adatokra kapok a szerver felől egy Felhasznala objektumot, 
                // ha a bejelentkezési adatokkal lézetik felhasználó, vagy null-t, ha nincs.
                // Előbbi esetben a program fut tovább, utóbbiban addig próbálkozok, amíg nem tudok bejelentkezni
                szerverValasz = input.readUTF();
                sm = mapper.readValue(szerverValasz, Message.class);

                ArrayList<String> userNamePassword = (ArrayList<String>) cm.getBody().getData();
                Szerepkorok.Szerepkor szerepkor = Szerepkorok.Szerepkor.valueOf((String) sm.getBody().getData());
                user = new Felhasznalo(userNamePassword.get(0), szerepkor);
                System.out.println(user);
            }

            // *** A bejelentkeztetett felhasználó beállításra kerül a bejelentkezés idejére
            // a menük és folyamatok kezelésének céljából ***
            setACTUAL_USER(user);
            MenuManager menuManager = new MenuManager(user.getSzerepkor());
            Message clientMessage = new Message(new Head(), new Body());
            // *** A bejelentkezett felhasználó műveleteinek kezelése *** //
            while (true)
            {

                clientMessage = menuManager.Handle(sm);

                // kifelé
                if (clientMessage.getHead().getTipus() == ProtokollUzenetek.Tipusok.Kilepes)
                {
                    break;
                }

                if (!(clientMessage.getHead().getTipus() == ProtokollUzenetek.Tipusok.Bejelentkezes && clientMessage.getHead().getFeladat() == ProtokollUzenetek.Feladatok.FeladatBefejezes))
                {
                    String jsonString = mapper.writeValueAsString(clientMessage);
                    outputstr.writeUTF(jsonString); //kiiratás
                    outputstr.flush();

                    // szerver felőli válasz
                    szerverValasz = input.readUTF();
                    sm = mapper.readValue(szerverValasz, Message.class);
                }
            }

            outputstr.close();
            input.close();
            clientSocket.close();
        }
        catch (UnknownHostException e)
        {
            System.out.println("Unknown host: " + e);
        }
        catch (IOException e)
        {
            System.out.println("IOException:  " + e);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public static void main(String[] args)
    {
        new Client().ClientRun();
    }
}
