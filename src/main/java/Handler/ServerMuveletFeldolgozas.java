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
import java.util.ArrayList;

/**
 *
 * @author Johika
 */
public class ServerMuveletFeldolgozas {

    public static Message FeladatVegrehajtas(Message message) {
        Message valasz = new Message(new Head(), new Body());

        switch (message.getHead().getTipus()) {

            case Bejelentkezes:
                valasz = bejelentkezes(message);
                break;

            case SzabadSzobakListazasa:
                valasz = szabadSzobakListazasa(message);
                break;

            case FoglalasokKezelese:
                valasz = foglalasokKezelese(message);
                break;

            case Fizetes:
                valasz = fizetes(message);
                break;

            case Kilepes:
                valasz.getHead().setTipus(message.getHead().getTipus());
                break;

            default:
                valasz.getHead().setTipus(null);
                break;
        }

        return valasz;
    }

    private static Message bejelentkezes(Message message) {
        ArrayList<Object> bejelentkezesiAdatok = (ArrayList<Object>) message.getBody().getData();
        DataSet.Szerepkorok.Szerepkor szerepkor = DatabaseManager.bejelentkezes((String) bejelentkezesiAdatok.get(0), (String) bejelentkezesiAdatok.get(1));
        message.setBody(new Body(szerepkor));
        message.setHead(new Head(message.getHead().getTipus(), ProtokollUzenetek.Feladatok.FeladatBefejezes));
        return message;
    }

    private static Message szabadSzobakListazasa(Message message) {
        ArrayList<Szoba> szabadSzobak = DatabaseManager.szabadSzobakListazasa((ArrayList<String>) message.getBody().getData());
        message.setBody(new Body(szabadSzobak));
        message.setHead(new Head(message.getHead().getTipus(), ProtokollUzenetek.Feladatok.FeladatBefejezes));
        return message;
    }

    private static Message foglalasokKezelese(Message message) {
        switch (message.getHead().getFeladat()) {
            case FoglalasLetrehozasa:
                ArrayList<Object> letrehozasElemek = (ArrayList<Object>) message.getBody().getData();
                boolean sikeresFoglalas = DatabaseManager.szobaLefoglalasa((String) letrehozasElemek.get(0), (int) letrehozasElemek.get(1), (ArrayList<String>) letrehozasElemek.get(2));
                message.setBody(new Body(sikeresFoglalas));
                message.setHead(new Head(message.getHead().getTipus(), ProtokollUzenetek.Feladatok.FoglalasLetrehozasaBefejezes));
                return message;

            case FoglalasMegtekintese:
                ArrayList<Foglalas> foglalasok = DatabaseManager.foglalasokMegtekintese((String) message.getBody().getData());
                message.setBody(new Body(foglalasok));
                message.setHead(new Head(message.getHead().getTipus(), ProtokollUzenetek.Feladatok.FoglalasMegtekinteseBefejezes));
                return message;

            case FoglalasModositasa: //Későbbi iteráció
                //TODO
                return null;

            case FoglalasTorlese:
                boolean sikeresTorles = DatabaseManager.foglalasTorlese((int) message.getBody().getData());
                message.setBody(new Body(sikeresTorles));
                message.setHead(new Head(message.getHead().getTipus(), ProtokollUzenetek.Feladatok.FoglalasTorleseBefejezes));
                return message;

            default: //ide soha nem fog beugrani
                return null;
        }
    }

    private static Message fizetes(Message message) {
        switch (message.getHead().getFeladat()) {
            case FizetendoElemekMegtekintese:
                ArrayList<Foglalas> fizetendoElemek = DatabaseManager.fizetendoMegtekintes((String) message.getBody().getData());
                message.setBody(new Body(fizetendoElemek));
                message.setHead(new Head(message.getHead().getTipus(), ProtokollUzenetek.Feladatok.FizetendoElemekMegtekinteseBefejezes));
                return message;

            case ElemFizetese:
                boolean sikeresFizetes = DatabaseManager.elemFizetese((int) message.getBody().getData());
                message.setBody(new Body(sikeresFizetes));
                message.setHead(new Head(message.getHead().getTipus(), ProtokollUzenetek.Feladatok.ElemFizeteseBefejezes));
                return message;

            default: //ide soha nem fog beugrani
                return null;
        }
    }

}
