/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Handler;

import DataSet.Foglalas;
import DataSet.Szoba;
import Protokoll.Body;
import Protokoll.Head;
import Protokoll.Message;
import Protokoll.ProtokollUzenetek;
import com.szallodanyilvantartorendszer.szallide.Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTimeComparator;

public class VendegMenu {

    Message message = new Message(new Head(), new Body());
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String standardInput;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
    boolean vissza = false;

    static public String Menu
            = "1 - Szabad szobak listazasa\n"
            + "2 - Foglalasok kezelese\n"
            + "3 - Fizetes\n"
            + "4 - Kilepes\n"
            + "Parancs: ";
    static public String FoglalasokKezeleseMenu
            = "1 - Foglalas letrehozasa\n"
            + "2 - Foglalas megtekintese\n"
            + "3 - Foglalas torlese\n"
            + "4 - Vissza\n"
            + "Parancs: ";
    static public String FoglalasLetrehozasaMenu
            = "1 - Foglalas erre az intervallumra\n"
            + "2 - Visszalepes\n"
            + "Parancs: ";
    static public String FizetesMenu
            = "1 - Fizetendo elemek megtekintese\n"
            + "2 - Elemek fizetese\n"
            + "3 - Vissza\n"
            + "Parancs: ";

    public VendegMenu(Message message) {
        this.message = message;
    }

    // A szerver oldalról érkező üzenetek feldolgozása típus szerint
    public Message Run() throws IOException {
        switch (message.getHead().getTipus()) {
            case Bejelentkezes:
                return Fomenu();
            case Fizetes:
                return fizetesUzenetSzervernek(message);
            case FoglalasokKezelese:
                return foglalasUzenetSzervernek(message);
            case SzabadSzobakListazasa:
                return SzabadSzobakListazasa_S();
            default:
                System.out.print(Menu);
                break;
        }
        return message;
    }

// +++ Főmenü +++
    public Message Fomenu() throws IOException {
        System.out.print(Menu);

        message.getHead().setFeladat(null);
        standardInput = br.readLine();
        switch (standardInput) {
            case "1":
                message.getHead().setTipus(ProtokollUzenetek.Tipusok.SzabadSzobakListazasa);
                message = SzabadSzobakListazasa();
                break;
            case "2":
                message.getHead().setTipus(ProtokollUzenetek.Tipusok.FoglalasokKezelese);
                message = FoglalasokKezelese();
                break;         
            case "3":
                message.getHead().setTipus(ProtokollUzenetek.Tipusok.Fizetes);
                message = Fizetes();
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

        if (vissza) {
            vissza = false;
            message = Fomenu();
        }

        return message;
    }
// --- Főmenü ---
Message foglalasUzenetSzervernek(Message message) {
        switch (message.getHead().getFeladat()) {
            case FoglalasLetrehozasaBefejezes:
                message = FoglalasLetrehozasa_S();
                break;
            case FoglalasMegtekinteseBefejezes:
                message = FoglalasokMegtekintese_S();
                break;
            case FoglalasTorleseBefejezes:
                message = FoglalasTorlese_S();
                break;
        }
        return message;
    }

    Message fizetesUzenetSzervernek(Message message) {
        switch (message.getHead().getFeladat()) {
            case FizetendoElemekMegtekinteseBefejezes:
                message = FizetendoElemekMegtekintese_S();
                break;
            case ElemFizeteseBefejezes:
                message = ElemFizetese_S();
                break;
        }
        return message;
    }
// +++ Szabad szobák listázása +++
    public Message SzabadSzobakListazasa() {
        String kezdoDatumStr = null, vegDatumStr = null;
        
        Date kezdoDatum = null, vegDatum = null;
        try {
            System.out.print("Adja meg a megtekinteni kivan idoszak kezdodatumat (pelda: 2017-04-12): ");
            kezdoDatumStr = br.readLine();
            kezdoDatum=df.parse(kezdoDatumStr);
            System.out.print("Adja meg a megtekinteni kivan idoszak vegdatumat (pelda: 2017-04-12): ");
            vegDatumStr = br.readLine();
            vegDatum=df.parse(vegDatumStr);
            //df.setTimeZone(TimeZone.getTimeZone("CEST"));
            if (DateTimeComparator.getDateOnlyInstance().compare(kezdoDatum, vegDatum) == 1) {
                throw new Exception("A megtekinteni kivan idoszak kezdodatuma nem lehet kesobbi, mint a vegdatum");
            }
        } catch (ParseException exc) {
            System.err.println("!!! Ervenytelen datum !!!");
            message = SzabadSzobakListazasa();
        } catch (Exception ex) {
            //  Logger.getLogger(ClientMuveletFeldolgozas.class.getName()).log(Level.SEVERE, null, ex);
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

    public Message SzabadSzobakListazasa_S() {
        if (message.getBody().getData() == null) {
            System.out.println("A megadott idotartomanyban nincs szabad szoba!");
        } else {
            ArrayList<Szoba> szobak = (ArrayList<Szoba>) message.getBody().getData();
            for (int i = 0; i < szobak.size(); i++) {
                System.out.println(szobak.get(i));
            }
        }

        message.getHead().setTipus(ProtokollUzenetek.Tipusok.Bejelentkezes);
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FeladatBefejezes);
        return message;
    }
// --- Szabad szobák listázása ---

// +++ Foglalások kezelése +++
    public Message FoglalasokKezelese() throws IOException {
        System.out.println("--------------------------");
        System.out.println(FoglalasokKezeleseMenu);

        standardInput = br.readLine();
        switch (standardInput) {
            case "1":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasLetrehozasa);
                message = FoglalasLetrehozasa();
                break;
            case "2":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasMegtekintese);
                message = FoglalasMegtekintese();
                break;
            case "3":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasTorlese);
                message = FoglalasTorlese();
                break;
            case "4":
                return Fomenu();
            default:
                System.out.println("--------------------------");
                System.out.println("* Ervenytelen menu elem! *");
                message = FoglalasokKezelese();
                break;
        }

        return message;
    }

    public Message FoglalasLetrehozasa() {
        System.out.print(">> Foglalas letrehozasa <<\n"
                + "Adja meg a foglalas kezdodatumat (pelda: 2017-04-12): ");
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasLetrehozasa);
        String kezdoDatumStr = null;
        String vegDatumStr = null;
        int szobaszam = 0;
        Date kezdoDatum = null, vegDatum = null;
        try {
            kezdoDatumStr = br.readLine();
            kezdoDatum = df.parse(kezdoDatumStr);
            System.out.print("Adja meg a foglalas vegdatumat (pelda: 2017-04-12): ");
            vegDatumStr = br.readLine();
            vegDatum = df.parse(vegDatumStr);
            System.out.println("Adja meg  lefoglalni kivant szoba szamat!");
            szobaszam = Integer.parseInt(br.readLine());
            if (vegDatum.before(kezdoDatum)) {
                throw new Exception("A foglalas kezdodatuma nem lehet nagyobb, mint a vegdatum");
            }
        } catch (ParseException exc) {
            System.out.println("!!! Ervenytelen formatum !!!");
            message = FoglalasLetrehozasa();
        } catch (Exception ex) {
            //  Logger.getLogger(ClientMuveletFeldolgozas.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("!!! A kezdodatum nem lehet nagyobb, mint a vegdatum !!!");
            message = FoglalasLetrehozasa();
        }

        ArrayList<String> intervallum = new ArrayList<>();
        intervallum.add(kezdoDatumStr);
        intervallum.add(vegDatumStr);

        ArrayList<Object> foglalasLetrehozasa = new ArrayList<>();
        foglalasLetrehozasa.add(Client.getACTUAL_USER().getFelhasznalo_nev());
        foglalasLetrehozasa.add(szobaszam);
        foglalasLetrehozasa.add(intervallum);

        message.getBody().setData(foglalasLetrehozasa);

        return message;
    }

    private Message FoglalasLetrehozasa_S() {

        if ((boolean) message.getBody().getData() == false) {
            System.out.println("Sikertelen foglalas!!!\nA szoba sajnos foglalt az adott időpontban!\nKérjük próbájon meg egy másik szobát lefoglalni.\nKöszönjük!");
        } else {
            System.out.println("Sikeres foglalas! ");
        }
        message.getHead().setTipus(ProtokollUzenetek.Tipusok.Bejelentkezes);
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FeladatBefejezes);
        return message;
    }

    public Message FoglalasTorlese() {
        System.out.println(">> Foglalasok torlese <<");
        System.out.println("Adja meg a torolni kivant foglalas azonositojat! ");
        int foglalasID = 0;
        try {
            foglalasID = Integer.parseInt(br.readLine());
        } catch (IOException ex) {
            System.out.println("Hibas adat");
            message = FoglalasTorlese();
            //Logger.getLogger(VendegMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasTorlese);
        message.getBody().setData(foglalasID);
        return message;
    }

    private Message FoglalasTorlese_S() {
        if ((boolean) message.getBody().getData() == false) {
            System.out.println("Sikertelen torles!!!");
        } else {

            System.out.println("Sikeres torles! ");

        }

        message.getHead().setTipus(ProtokollUzenetek.Tipusok.Bejelentkezes);
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FeladatBefejezes);
        return message;
    }

    public Message FoglalasMegtekintese() {
        System.out.print(">> Foglalasok listazasa <<\n");
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FoglalasMegtekintese);
        message.getBody().setData(Client.getACTUAL_USER().getFelhasznalo_nev());
        return message;
    }

    private Message FoglalasokMegtekintese_S() {
        if (message.getBody().getData() == null) {
            System.out.println("Hiba!");
        } else {
            ArrayList<Szoba> szobak = (ArrayList<Szoba>) message.getBody().getData();
            for (int i = 0; i < szobak.size(); i++) {
                System.out.println(szobak.get(i));
            }
        }

        message.getHead().setTipus(ProtokollUzenetek.Tipusok.Bejelentkezes);
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FeladatBefejezes);
        return message;
    }

// +++ Fizetés +++
    public Message Fizetes() throws IOException {
        System.out.println("--------------------------");
        System.out.println(FizetesMenu);

        standardInput = br.readLine();
        switch (standardInput) {
            case "1":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FizetendoElemekMegtekintese);
                message = FizetendoElemekMegtekintese();
                break;
            case "2":
                message.getHead().setFeladat(ProtokollUzenetek.Feladatok.ElemFizetese);
                message = ElemFizetese();
                break;
            case "3":
                return Fomenu();
            default:
                System.out.println("--------------------------");
                System.out.println("* Ervenytelen menu elem! *");
                message = FoglalasokKezelese();
                break;
        }

        return message;
    }
// --- Fizetés ---

    public Message Kilepes() {
        message.getHead().setTipus(ProtokollUzenetek.Tipusok.Kilepes);
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FeladatBefejezes);
        System.out.println("Sikeres kijelentkezés.");
        return message;
       
    }

    private Message FizetendoElemekMegtekintese() {
        System.out.print(">> Fizetendo elemek listazasa <<\n");
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FizetendoElemekMegtekintese);
        message.getBody().setData(Client.getACTUAL_USER().getFelhasznalo_nev());
        return message;
    }

    private Message ElemFizetese() throws IOException {
        System.out.println(">> Elem fizetese <<");
        System.out.println("Adja meg a fizetni kivant foglalas azonositojat! ");
        int foglalasID = 0;
        try {
            foglalasID = Integer.parseInt(br.readLine());
        } catch (NumberFormatException ex) {
            System.out.println("Nem megfelelo azonosito");
            message = ElemFizetese();
            //Logger.getLogger(VendegMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.ElemFizetese);
        message.getBody().setData(foglalasID);
        return message;
    }

    private Message FizetendoElemekMegtekintese_S() {
        if (message.getBody().getData() == null) {
            System.out.println("Hiba!");
        } else {
            ArrayList<Foglalas> foglalasok = (ArrayList<Foglalas>) message.getBody().getData();
            for (int i = 0; i < foglalasok.size(); i++) {
                System.out.println(foglalasok.get(i));
            }
        }

        message.getHead().setTipus(ProtokollUzenetek.Tipusok.Bejelentkezes);
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FeladatBefejezes);
        return message;
    }

    private Message ElemFizetese_S() {
        if ((boolean) message.getBody().getData() == false) {
            System.out.println("Sikertelen fizetes!!!");
        } else {

            System.out.println("Sikeres fizetes! ");

        }

        message.getHead().setTipus(ProtokollUzenetek.Tipusok.Bejelentkezes);
        message.getHead().setFeladat(ProtokollUzenetek.Feladatok.FeladatBefejezes);
        return message;
    }

}
