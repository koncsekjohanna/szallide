package Protokoll;

public class ProtokollUzenetek {
    
    public static enum Tipusok {
        Csatlakozas, Bejelentkezes,
        SzabadSzobakListazasa,
        FoglalasokKezelese,
        BejegyzesKezeles,
        Fizetes, Kilepes
    }
    
    public static enum Feladatok {
        FoglalasLetrehozasa, FoglalasMegtekintese, FoglalasModositasa,
        FoglalasTorlese,
        BejegyzesLetrehozasa, BejegyzesMegtekintese, BejegyzesModositasa,
        BejegyzesTorlese,
        FizetendoElemekMegtekintese, ElemFizetese,
        FeladatBefejezes,
        FoglalasLetrehozasaBefejezes, FoglalasMegtekinteseBefejezes, FoglalasModositasaBefejezes,
        FoglalasTorleseBefejezes,
        BejegyzesLetrehozasaBefejezes, BejegyzesMegtekinteseBefejezes, BejegyzesModositasaBefejezes,
        BejegyzesTorleseBefejezes,
        FizetendoElemekMegtekinteseBefejezes, ElemFizeteseBefejezes,
    }
    
}
