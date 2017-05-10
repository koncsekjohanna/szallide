package Handler;

import Connector.DatabaseConnection;
import DataSet.Szerepkorok;
import DataSet.Foglalas;
import DataSet.Szoba;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.Days;
import org.joda.time.LocalDate;

/**
 *
 * @author Johika
 */
public class DatabaseManager {

    private static PreparedStatement prepStatement;
    private static ResultSet resultset;

    //felhasznaloi szerepkorok
    private static final String ADMIN = "admin";
    private static final String VENDEG = "vendeg";
    private static final String RECEPCIOS = "recepcios";
    private static final String TAKARITO = "takarito";
    private static final String KARBANTARTO = "karbantarto";
    private static final String VEZETO = "vezeto";

    //Bejelentjezes típus
    public static Szerepkorok.Szerepkor bejelentkezes(String felhasznaloNev, String jelszo) {
        try {
            prepStatement = DatabaseConnection.getConnection().prepareStatement("select * from User where Felhasznalonev=(?) and Jelszo=(?)");
            prepStatement.setString(1, felhasznaloNev);
            prepStatement.setString(2, jelszo);
            resultset = prepStatement.executeQuery();
            while (resultset.next()) {
                switch (resultset.getString("Szerepkor")) {
                    case ADMIN:
                        return Szerepkorok.Szerepkor.admin;
                    case VENDEG:
                        return Szerepkorok.Szerepkor.vendeg;
                    case RECEPCIOS:
                        return Szerepkorok.Szerepkor.recepcios;
                    case TAKARITO:
                        return Szerepkorok.Szerepkor.takarito;
                    case KARBANTARTO:
                        return Szerepkorok.Szerepkor.karbantarto;
                    case VEZETO:
                        return Szerepkorok.Szerepkor.vezeto;
                }
            }
            return null;//ha nem jó a login adat
        } catch (SQLException ex) {
            return null;//SQL error
        } finally {
            closeQuery();
        }
    }

    //SzabadSzobakListazasa típus
    public static ArrayList<Szoba> szabadSzobakListazasa(ArrayList<String> intervallum) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date intervallum0 = df.parse(intervallum.get(0));
            Date intervallum1 = df.parse(intervallum.get(1));
//            java.sql.Date intervallum0 = java.sql.Date.valueOf(intervallum.get(0));
//            java.sql.Date intervallum1 = java.sql.Date.valueOf(intervallum.get(1));
            ArrayList<Szoba> szabadSzobak = new ArrayList<>();
            prepStatement = DatabaseConnection.getConnection().prepareStatement("select * from Foglalas inner join Szoba on Foglalas.Szobaszam = Szoba.Szobaszam");
            resultset = prepStatement.executeQuery();
            while (resultset.next()) {
                if (intervallum1.before(resultset.getDate("Foglalas_kezdete")) || intervallum0.after(resultset.getDate("Foglalas_vege"))) {
                    szabadSzobak.add(new Szoba(resultset.getInt("Szobaszam"), resultset.getString("Felszereltseg"), resultset.getInt("Ferohelyek"), resultset.getInt("Alapar")));
                }
            }
            prepStatement = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM Szoba sz LEFT JOIN Foglalas f ON sz.Szobaszam = f.Szobaszam WHERE f.Szobaszam IS NULL");//szobák, amire egyáltalán nincs foglalás
            resultset = prepStatement.executeQuery();
            while (resultset.next()) {
                szabadSzobak.add(new Szoba(resultset.getInt("Szobaszam"), resultset.getString("Felszereltseg"), resultset.getInt("Ferohelyek"), resultset.getInt("Alapar")));
            }
            if (szabadSzobak.isEmpty()) {
                return null;
            }
            return szabadSzobak;
        } catch (SQLException ex) {
            return null;//SQL error
        } catch (ParseException ex) {
            return null;//should never happen
        } finally {
            closeQuery();
        }
    }

    //FoglalasokKezelese típus
    //TODO: Még hátralevő feladatok FoglalasModositasa,
    public static boolean foglalasTorlese(int foglalasID) {
        try {
            prepStatement = DatabaseConnection.getConnection().prepareStatement("DELETE FROM Foglalas WHERE FoglalasID=(?)");
            prepStatement.setInt(1, foglalasID);
            prepStatement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;//SQL error
        } finally {
            closeQuery();
        }
    }

    public static boolean szobaLefoglalasa(String felhasznaloNev, int szobaszam, ArrayList<String> intervallum) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            df.setTimeZone(TimeZone.getDefault());
            Date intervallum0 = df.parse(intervallum.get(0));
            Date intervallum1 = df.parse(intervallum.get(1));
 //           java.sql.Date intervallum0 = java.sql.Date.valueOf(intervallum.get(0));
//            java.sql.Date intervallum1 = java.sql.Date.valueOf(intervallum.get(1));
            ArrayList<Szoba> szabadSzobak = new ArrayList<>();
            prepStatement = DatabaseConnection.getConnection().prepareStatement("select * from Foglalas inner join Szoba on Foglalas.Szobaszam = Szoba.Szobaszam");
            resultset = prepStatement.executeQuery();
            while (resultset.next()) {
                if (intervallum1.before(resultset.getDate("Foglalas_kezdete")) || intervallum0.after(resultset.getDate("Foglalas_vege"))) {
                    szabadSzobak.add(new Szoba(resultset.getInt("Szobaszam"), resultset.getString("Felszereltseg"), resultset.getInt("Ferohelyek"), resultset.getInt("Alapar")));
                }
            }
            prepStatement = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM Szoba sz LEFT JOIN Foglalas f ON sz.Szobaszam = f.Szobaszam WHERE f.Szobaszam IS NULL");//szobák, amire egyáltalán nincs foglalás
            resultset = prepStatement.executeQuery();
            while (resultset.next()) {
                szabadSzobak.add(new Szoba(resultset.getInt("Szobaszam"), resultset.getString("Felszereltseg"), resultset.getInt("Ferohelyek"), resultset.getInt("Alapar")));
            }
            if (szabadSzobak.isEmpty()) {
                return false;
            }
            for (Szoba szoba : szabadSzobak) {
                if (szoba.getSzobaszam() == szobaszam) {
                    prepStatement = DatabaseConnection.getConnection().prepareStatement("SELECT Alapar FROM Szoba WHERE Szobaszam=(?)");
                    prepStatement.setInt(1, szobaszam);
                    resultset = prepStatement.executeQuery();
                    int alapar = 0;
                    while (resultset.next()) {
                        alapar = resultset.getInt("Alapar");
                    }
                    prepStatement = DatabaseConnection.getConnection().prepareStatement("insert into Foglalas (Foglalo, Szobaszam, Foglalas_kezdete, Foglalas_vege, Kifizette, Vegosszeg) VALUES (?,?,?,?,?,?)");
                    prepStatement.setString(1, felhasznaloNev);
                    prepStatement.setInt(2, szobaszam);
                    prepStatement.setDate(3, java.sql.Date.valueOf(intervallum.get(0)));
                    prepStatement.setDate(4, java.sql.Date.valueOf(intervallum.get(1)));
                    prepStatement.setBoolean(5, false);
                    String[] date0 = intervallum.get(0).split("-");
                    String[] date1 = intervallum.get(1).split("-");
                    LocalDate localDate0 = new LocalDate(Integer.valueOf(date0[0]), Integer.valueOf(date0[1]), Integer.valueOf(date0[2]));
                    LocalDate localDate1 = new LocalDate(Integer.valueOf(date1[0]), Integer.valueOf(date1[1]), Integer.valueOf(date1[2]));
                    int napokSzama = Days.daysBetween(localDate0, localDate1).getDays();
                    int teljesAr = napokSzama * alapar;
                    prepStatement.setInt(6, teljesAr);
                    prepStatement.executeUpdate();
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            return false;//SQL error
        } catch (ParseException ex) {
            return false;//should never happen
        } finally {
            closeQuery();
        }
    }

    public static ArrayList<Foglalas> foglalasokMegtekintese(String felhasznaloNev) {
        try {
            ArrayList<Foglalas> foglalasok = new ArrayList<>();
            prepStatement = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM Foglalas WHERE Foglalo=(?)");
            prepStatement.setString(1, felhasznaloNev);
            resultset = prepStatement.executeQuery();
            while (resultset.next()) {
                String foglalasKezdete = resultset.getString("Foglalas_kezdete");
                String foglalasVege = resultset.getString("Foglalas_vege");
                ArrayList<String> intervallumTMP = new ArrayList<>();
                intervallumTMP.add(foglalasKezdete);
                intervallumTMP.add(foglalasVege);
                foglalasok.add(new Foglalas(resultset.getString("Foglalo"), resultset.getInt("Szobaszam"), intervallumTMP, resultset.getBoolean("Kifizette"), resultset.getInt("Vegosszeg"), resultset.getInt("FoglalasID")));
            }
            if (foglalasok.isEmpty()) {
                return null;
            }
            return foglalasok;
        } catch (SQLException ex) {
            return null;//SQL error
        } finally {
            closeQuery();
        }
    }

    //Fizetes típus
    public static boolean elemFizetese(int foglalasID) {
        
        try {
            prepStatement = DatabaseConnection.getConnection().prepareStatement("update Foglalas set Kifizette=true where FoglalasID=(?)");
            prepStatement.setInt(1, foglalasID);
            prepStatement.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;//SQL error
        } finally {
            closeQuery();
        }
    }

    static ArrayList<Foglalas> fizetendoMegtekintes(String felhasznaloNev) {
        try {
            ArrayList<Foglalas> fizetendo = new ArrayList<>();
            prepStatement = DatabaseConnection.getConnection().prepareStatement("select * from Foglalas where Foglalo=(?) and Kifizette=\"0\"");
            prepStatement.setString(1, felhasznaloNev);
            resultset = prepStatement.executeQuery();
            while (resultset.next()) {
                String foglalasKezdete = resultset.getString("Foglalas_kezdete");
                String foglalasVege = resultset.getString("Foglalas_vege");
                ArrayList<String> intervallum = new ArrayList<>();
                intervallum.add(foglalasKezdete);
                intervallum.add(foglalasVege);
                fizetendo.add(new Foglalas(resultset.getString("Foglalo"), resultset.getInt("Szobaszam"), intervallum, resultset.getBoolean("Kifizette"), resultset.getInt("Vegosszeg"), resultset.getInt("FoglalasID")));
            }
            if (fizetendo.isEmpty()) {
                return null;
            }
            return fizetendo;
        } catch (SQLException ex) {
            return null;//SQL error
        } finally {
            closeQuery();
        }
    }

    private static void closeQuery() {
        try {
            if (resultset != null) {
                resultset.close();
            }
            if (prepStatement != null) {
                prepStatement.close();
            }
        } catch (SQLException ignore) {
        }
    }

    @Override
    @SuppressWarnings("FinalizeDeclaration")
    protected void finalize() throws Throwable {
        try {
            DatabaseConnection.close();
        } finally {
            super.finalize();
        }
    }

}
