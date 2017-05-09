/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Handler;

import DataSet.Szoba;
import Protokoll.Body;
import Protokoll.Head;
import Protokoll.Message;
import Protokoll.ProtokollUzenetek;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTimeComparator;

/**
 *
 * @author Neferet
 */
public class RecepciosMenu {

    Message message = new Message(new Head(), new Body());
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String standardInput;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    boolean vissza = false;
    
    public RecepciosMenu(Message message) {
        this.message = message;
    }
    
    public Message run() throws IOException {
        switch (message.getHead().getTipus()){
            case Bejelentkezes:
                return Fomenu();
            case SzabadSzobakListazasa:
                return SzabadSzobakListazasa_S();
            case FoglalasokKezelese:
                System.out.print(FoglalasokKezeleseMenu);
                break;
            case BejegyzesKezeles: 
                System.out.print(BejegyzesKezeleseMenu);
                break;
            default:
                System.out.print(RecepciosMenu);
                break;
        }
        return null;      
    }
    
    public Message Fomenu() throws IOException
    {
        System.out.println(RecepciosMenu);
        message.getHead().setFeladat(null);
        standardInput = br.readLine();
        switch (standardInput)
        {
            case "1":
                message.getHead().setTipus(ProtokollUzenetek.Tipusok.SzabadSzobakListazasa);
                message = SzabadSzobakListazasa();
                break;
            case "2":
                message.getHead().setTipus(ProtokollUzenetek.Tipusok.FoglalasokKezelese);
                message = FoglalasokKezelese();
                break;
            case "3":
                message.getHead().setTipus(ProtokollUzenetek.Tipusok.BejegyzesKezeles);
                message = BejegyzesKezeles();
                break;
            case "4":
                message.getHead().setTipus(ProtokollUzenetek.Tipusok.Kilepes);
                message = Kilepes();
                break;
            default:
                System.out.println("Ervenytelen menu elem!");
                message = Fomenu();
                break;
        }
        return message;
    } 
    
    static public String RecepciosMenu
            = "1 - Szabad szobak listazasa\n"
            + "2 - Foglalasok kezelese\n"
            + "3 - Bejegyzes kezelese\n"
            + "4 - Kilepes\n"
            + "Parancs: ";
    
    static public String BejegyzesKezeleseMenu
            = "1 - Bejegyzes irasa\n"
            + "2 - Bejegyzes megtekintese\n"
            + "3 - Vissza\n"
            + "Parancs: ";
    
    static public String FoglalasokKezeleseMenu
            = "1 - Foglalas letrehozasa\n"
            + "2 - Foglalas modositasa\n"
            + "3 - Foglalas torlese\n"
            + "4 - Foglalas megtekintese\n"
            + "5 - Vissza\n"
            + "Parancs: ";
    
    
    public static String RecepciosMenu() {
        return  "1 - Szabad szobak listazasa\n"
            +   "2 - Foglalasok kezelese\n"
            +   "3 - Hiba/ takarítás bejelentése\n" //Új bejegyzés felvitele
            +   "4 - Kilépés\n"
            +   "Parancs: ";
        
    }
    
    public static String FoglalasKezeles(){
    return  "1 - Foglalások megtekintése\n"
            +   "2 - Foglalás felvitele\n"
            +   "3 - Visszalépés\n"
            +   "Parancs: ";    
    }
    
    public static String BejgyezesKezeles(){
    return  "1 - Bejegyzés felvitele\n"
            +   "2 - Visszalépés\n"
            +   "Parancs: ";    
    }
    
    public Message SzabadSzobakListazasa() {
        String kezdoDatumStr = null, vegDatumStr = null;
        Date kezdoDatum = null, vegDatum = null;
        try
        {
            System.out.print("Adja meg a foglalas kezdodatumat (pelda: 2017-04-12): ");
            kezdoDatumStr = br.readLine();
            kezdoDatum = df.parse(kezdoDatumStr);
            System.out.print("Adja meg a foglalas vegdatumat (pelda: 2017-04-12): ");
            vegDatumStr = br.readLine();
            vegDatum = df.parse(vegDatumStr);

            if (DateTimeComparator.getDateOnlyInstance().compare(kezdoDatum, vegDatum) == 1)
            {
                throw new Exception("A foglalas kezdodatuma nem lehet kesobbi, mint a vegdatum");
            }
        }
        catch (ParseException exc)
        {
            System.err.println("!!! Ervenytelen datum !!!");
            message = SzabadSzobakListazasa();
        }
        catch (Exception ex)
        {
           // Logger.getLogger(ClientMuveletFeldolgozas.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("!!! Hiba a datumok megadasanak modjaban !!!");
            message = SzabadSzobakListazasa();
        }

        ArrayList<String> kezdoEsVeg = new ArrayList<>();
        kezdoEsVeg.add(kezdoDatumStr);
        kezdoEsVeg.add(vegDatumStr);
        message.getHead().setFeladat(null);
        message.getBody().setData(kezdoEsVeg);

        return message;
    }
    
    public Message foglalasokMegtekintese(Message m) {
        System.out.println(">> Foglalások megtekintése");
        m.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasMegtekintese);
        return m;
    }
    
    public Message foglalasFelvitele(Message m) {
        System.out.println(">> Új foglalás <<");
        m.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasLetrehozasa);
        return m;
    }
    
    public Message hibaTakaritasBejelentese(Message m) {
        System.out.println(">> Új bejegyzés felvitele <<");
        m.getHead().setFeladat(ProtokollUzenetek.Feladatok.BejegyzesLetrehozasa);
        return m;
    }
    
    public Message Kilepes() {
        message.getHead().setTipus(ProtokollUzenetek.Tipusok.Kilepes);
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FeladatBefejezes);
        return message;
    }
    
    public Message SzabadSzobakListazasa_S()
    {
        if (message.getBody().getData() == null)
        {
            System.out.println("A megadott idotartomanyban nincs szabad szoba!");
        }
        else
        {
            ArrayList<Szoba> szobak = (ArrayList<Szoba>) message.getBody().getData();
            for (int i = 0; i < szobak.size(); i++)
            {
                System.out.println(szobak.get(i));
            }
        }

        message.getHead().setTipus(ProtokollUzenetek.Tipusok.Bejelentkezes);
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FeladatBefejezes);
        return message;
    }
    
    public Message FoglalasokKezelese() throws IOException
    {
        System.out.println("--------------------------");
        System.out.println(FoglalasokKezeleseMenu);

        standardInput = br.readLine();
        switch (standardInput)
        {
            case "1":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasLetrehozasa);
                message = FoglalasLetrehozasa();
                break;
            case "2":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasModositasa);
                message = FoglalasModositasa();
                break;
            case "3":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasTorlese);
                message = FoglalasTorlese();
                break;
            case "4":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasMegtekintese);
                message = FoglalasMegtekintese();
                break;
            case "5":
                return Fomenu();
            default:
                System.out.println("--------------------------");
                System.out.println("* Ervenytelen menu elem! *");
                message = FoglalasokKezelese();
                break;
        }
        return message;
    }
    
    public Message FoglalasLetrehozasa()
    {

        return message;
    }
    
    public Message FoglalasModositasa()
    {

        return message;
    }

    public Message FoglalasTorlese()
    {

        return message;
    }
    public Message FoglalasMegtekintese()
    {

        return message;
    }
    
    public Message BejegyzesKezeles() throws IOException
    {
        System.out.println("--------------------------");
        System.out.println(BejegyzesKezeleseMenu);

        standardInput = br.readLine();
        switch (standardInput)
        {
            case "1":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.BejegyzesLetrehozasa);
                message = BejegyzesIrasa();
            case "2":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.BejegyzesMegtekintese);
                message = BejegyzesMegtekintese();
            case "3":
                return Fomenu();
            default:
                System.out.println("--------------------------");
                System.out.println("* Ervenytelen menu elem! *");
                message = BejegyzesKezeles();
                break;
        }

        return message;
    }
    
    public Message BejegyzesIrasa()
    {

        return message;
    }

    public Message BejegyzesMegtekintese()
    {

        return message;
    }

    
}

