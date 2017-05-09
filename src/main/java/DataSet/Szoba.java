package DataSet;

public class Szoba
{

    @Override
    public String toString()
    {
        return "\n*** A szoba adatai ***\n"
                + "\n\tSzobaszam:\t" + this.szobaszam
                + "\n\tFerohelyek:\t" + this.ferohelyek
                + "\n\tFelszereltseg:\t" + this.felszereltseg
                + "\n\n\t\tAra:\t" + this.alapar
                + "\n********************\n";
    }

    private final int szobaszam;
    private String felszereltseg;
    private int ferohelyek;
    private int alapar;

    public Szoba(int szobaszam, String felszereltseg, int ferohelyek, int alapar)
    {
        this.szobaszam = szobaszam;
        this.felszereltseg = felszereltseg;
        this.ferohelyek = ferohelyek;
        this.alapar = alapar;
    }

    public int getSzobaszam()
    {
        return szobaszam;
    }

    public String getFelszereltseg()
    {
        return felszereltseg;
    }

    public int getFerohelyek()
    {
        return ferohelyek;
    }

    public int getAlapar()
    {
        return alapar;
    }

    public void setFelszereltseg(String felszereltseg)
    {
        this.felszereltseg = felszereltseg;
    }

    public void setFerohelyek(int ferohelyek)
    {
        this.ferohelyek = ferohelyek;
    }

    public void setAlapar(int alapar)
    {
        this.alapar = alapar;
    }

}
