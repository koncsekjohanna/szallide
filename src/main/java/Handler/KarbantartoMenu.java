/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Handler;

import Protokoll.Body;
import Protokoll.Head;
import Protokoll.Message;
import Protokoll.ProtokollUzenetek;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author Barbus
 */
public class KarbantartoMenu {

    Message message = new Message(new Head(), new Body());
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String standardInput;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    boolean vissza = false;

    public KarbantartoMenu(Message message){
        this.message=message;
    }
    
    public Message run() throws IOException{
        switch (message.getHead().getTipus()){
            case Bejelentkezes:
                return FoKarbantartoMenu();
            default:
                System.out.print(KarbantartoMenu);
                break;
        }
        return message;
    }
    
    static public String KarbantartoMenu 
            = "1 - Karbantartandó szobák lekérdezése  \n" //(Bejegyzések listázása)
            + "2 - Bejegyzés módosítása \n"  
            + "3 - Karbantartás elvégzése \n" //(Bejegyzések törlése)
            + "4 - Kilepes \n"
            + "Parancs: ";  
      
    public Message FoKarbantartoMenu() throws IOException{
        System.out.print(KarbantartoMenu);
        
        standardInput = br.readLine();
        switch(standardInput){
            case "1":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.BejegyzesMegtekintese);
                message = BejegyzesMegtekintese();
            case "2":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.BejegyzesModositasa);
                message = BejegyzesModositas();    
            case "3":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.BejegyzesTorlese);
                message = BejegyzesTorlese();
            case "4":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FeladatBefejezes);
                message = FeladatBefejezes();
        }
        return message;
    }
    
    public Message BejegyzesMegtekintese(){
        return message;
    }
   
    public Message BejegyzesModositas(){
        return message;
    } 
    
    public Message BejegyzesTorlese() {
        return message;
    }

    public Message FeladatBefejezes() {
      message.getHead().setTipus(ProtokollUzenetek.Tipusok.Kilepes);
      return message;
    }  
}
