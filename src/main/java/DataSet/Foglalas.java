package DataSet;

import java.util.ArrayList;

public class Foglalas {

	private final String felhasznaloNev;
	private final int szobaszam;
        private ArrayList<String> foglalasIdeje;
        private int foglalasID;
        private boolean kifizette;
        private int vegosszeg;

    public Foglalas(String felhasznaloNev, int szobaszam, ArrayList<String> foglalasIdeje, boolean kifizette, int vegosszeg, int foglalasID) {
        this.felhasznaloNev = felhasznaloNev;
        this.szobaszam = szobaszam;
        this.foglalasIdeje = foglalasIdeje;
        this.foglalasID = foglalasID;
        this.kifizette = kifizette;
        this.vegosszeg = vegosszeg;
    }

    public Foglalas(String felhasznaloNev, int szobaszam, ArrayList<String> foglalasIdeje, boolean kifizette, int vegosszeg) {
        this.felhasznaloNev = felhasznaloNev;
        this.szobaszam = szobaszam;
        this.foglalasIdeje = foglalasIdeje;
        this.kifizette = kifizette;
        this.vegosszeg = vegosszeg;
    }

    public String getFelhasznalo() {
        return felhasznaloNev;
    }

    public int getSzobaszam() {
        return szobaszam;
    }

    public ArrayList<String> getFoglalasIdeje() {
        return foglalasIdeje;
    }

    public int getFoglalasID() {
        return foglalasID;
    }

    public void setFoglalasIdeje(ArrayList<String> foglalasIdeje) {
        this.foglalasIdeje = foglalasIdeje;
    }

    public String getFelhasznaloNev() {
        return felhasznaloNev;
    }

    public boolean isKifizette() {
        return kifizette;
    }

    public int getVegosszeg() {
        return vegosszeg;
    }
        
}