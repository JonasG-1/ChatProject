package ServerChat.Clients;

import java.util.ArrayList;
import java.util.List;

public class Benutzer {

     private final String zName;
     private final Verbindung zVerbindung;
     private boolean zAdmin;
     private final ArrayList<String> zAktionen;

     public Benutzer(Verbindung pVerbindung, String pName, boolean pAdmin) {
          zVerbindung = pVerbindung;
          zVerbindung.verknuepfeBenutzer(this);
          zName = pName;
          zAdmin = pAdmin;
          zAktionen = new ArrayList<>();
     }

     public String getAdresse() {
          return zVerbindung.toString();
     }

     public Verbindung getVerbindung() {
          return zVerbindung;
     }

     public String getName() {
          return zName;
     }

     public boolean istAdmin() {
          return zAdmin;
     }

     public void setzeAdmin(boolean pAdmin) {
          zAdmin = pAdmin;
     }

     public ArrayList<String> getAktionen() {
          return zAktionen;
     }

     public void trenne() {
          zVerbindung.entferneBenutzer();
     }
}
