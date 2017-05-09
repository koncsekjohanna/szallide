package DataSet;

public class Felhasznalo {

	protected String nev;
	protected String email;
	protected String jelszo;
	protected String felhasznalo_nev;
	protected Szerepkorok.Szerepkor szerepkor;

    public Felhasznalo(String nev, String email, String jelszo, String felhasznalo_nev, Szerepkorok.Szerepkor szerepkor) {
        this.nev = nev;
        this.email = email;
        this.jelszo = jelszo;
        this.felhasznalo_nev = felhasznalo_nev;
        this.szerepkor = szerepkor;
    }
    
    public Felhasznalo (String felhasznalo_nev, Szerepkorok.Szerepkor szerepkor){
        this.felhasznalo_nev = felhasznalo_nev;
        this.szerepkor = szerepkor;
    }

    public String getNev() {
        return nev;
    }

    public String getEmail() {
        return email;
    }

    public String getJelszo() {
        return jelszo;
    }

    public String getFelhasznalo_nev() {
        return felhasznalo_nev;
    }

    public Szerepkorok.Szerepkor getSzerepkor() {
        return szerepkor;
    }

    @Override
    public String toString()
    {
        return "Felhasznalo neve: " + felhasznalo_nev
                + "\tFelhasznalo szerepkore: " + szerepkor
                + "\n";
    }
 
}