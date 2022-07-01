package ServerChat;

import ServerChat.Clients.Benutzer;
import ServerChat.Clients.Verbindung;
import java.util.ArrayList;
import java.util.List;

public class BenutzerVerwaltung {

    private final List<Benutzer> zBenutzer;
    private final List<Verbindung> zAktiveVerbindungen;
    private final List<Verbindung> zAdminVerbindungen;
    private final List<String> zGebannteIPs;

    public BenutzerVerwaltung() {
        zBenutzer = new ArrayList<>();
        zAktiveVerbindungen = new ArrayList<>();
        zAdminVerbindungen = new ArrayList<>();
        zGebannteIPs = new ArrayList<>();
    }

    public void neueVerbindung(String pAdresse) {
        if (istAdmin(pAdresse)) {
            try {
                Verbindung lVerbindung = gibAdmin(pAdresse);
                zAktiveVerbindungen.add(lVerbindung);
            } catch (NoSuchFieldException lFehler) {
                lFehler.printStackTrace();
            }
        } else {
            zAktiveVerbindungen.add(new Verbindung(pAdresse));
        }
    }

    public void benutzerHinzufuegen(String pAdresse, String pName) {
        if (istVerbunden(pAdresse)) {
            try {
                Verbindung lVerbindung = gibVerbindung(pAdresse);
                Benutzer lBenutzer = new Benutzer(lVerbindung, pName, istAdmin(pAdresse));
                zBenutzer.add(lBenutzer);
            } catch (NoSuchFieldException lFehler) {
                lFehler.printStackTrace();
            }
        }
    }

    public void entferneClient(String pAdresse) {
        if (istVerbunden(pAdresse)) {
            try {
                Verbindung lVerbindung = gibVerbindung(pAdresse);
                if (lVerbindung.istVerknuepft()) {
                    Benutzer lBenutzer = lVerbindung.getBenutzer();
                    lBenutzer.trenne();
                    zBenutzer.remove(lBenutzer);
                }
                zAktiveVerbindungen.remove(lVerbindung);
            } catch (NoSuchFieldException lFehler) {
                lFehler.printStackTrace();
            }
        }
    }

    public boolean istVerbunden(String pAdresse) {
        for (Verbindung lVerbindung : zAktiveVerbindungen) {
            if (lVerbindung.toString().equals(pAdresse)) {
                return true;
            }
        }
        return false;
    }

    public boolean istAngemeldet(String pAdresse) {
        for (Benutzer lBenutzer : zBenutzer) {
            if (lBenutzer.getAdresse().equals(pAdresse)) {
                return true;
            }
        }
        return false;
    }

    public boolean nameExistiert(String pName) {
        for (Benutzer lBenutzer : zBenutzer) {
            if (lBenutzer.getName().equals(pName)) {
                return true;
            }
        }
        return false;
    }

    public Verbindung gibVerbindung(String pAdresse) throws NoSuchFieldException {
        for (Verbindung lVerbindung : zAktiveVerbindungen) {
            if (lVerbindung.toString().equals(pAdresse)) {
                return lVerbindung;
            }
        }
        throw new NoSuchFieldException("Keine aktive Verbindung mit der Adresse gefunden");
    }

    public String gibAdresse(String pName) throws NoSuchFieldException {
        for (Benutzer lBenutzer : zBenutzer) {
            if (lBenutzer.getName().equals(pName)) {
                return lBenutzer.getAdresse();
            }
        }
        throw new NoSuchFieldException("Keinen Benutzer mit dieser Adresse gefunden");
    }

    public String[] gibAdressen() {
        String[] lAdressen = new String[zAktiveVerbindungen.size()];
        for (int i = 0; i < zAktiveVerbindungen.size(); i++) {
            lAdressen[i] = zAktiveVerbindungen.get(i).toString();
        }
        return lAdressen;
    }

    public String gibName(String pAdresse) throws NoSuchFieldException {
        for (Benutzer lBenutzer : zBenutzer) {
            if (lBenutzer.getAdresse().equals(pAdresse)) {
                return lBenutzer.getName();
            }
        }
        throw new NoSuchFieldException("Keinen Benutzer mit diesem Namen gefunden");
    }

    public String[] gibNamen() {
        String[] lNamen = new String[zBenutzer.size()];
        for (int i = 0; i < zBenutzer.size(); i++) {
           lNamen[i] = zBenutzer.get(i).getName();
        }
        return lNamen;
    }

    public ArrayList<String> gibAktionListe(String pAdresse) throws NoSuchFieldException {
        for (Benutzer lBenutzer : zBenutzer) {
            if (lBenutzer.getAdresse().equals(pAdresse)) {
                return lBenutzer.getAktionen();
            }
        }
        throw new NoSuchFieldException("Keinen Benutzer mit dieser Adresse gefunden");
    }

    public void adminHinzufuegen(String pAdresse) {
        if (istVerbunden(pAdresse)) {
            try {
                Verbindung lVerbindung = gibVerbindung(pAdresse);
                zAdminVerbindungen.add(lVerbindung);
            } catch (NoSuchFieldException lFehler) {
                lFehler.printStackTrace();
            }
        } else {
            zAdminVerbindungen.add(new Verbindung(pAdresse));
        }
    }

    public boolean istAdmin(String pAdresse) {
        for (Verbindung lVerbindung : zAdminVerbindungen) {
            if (lVerbindung.toString().equals(pAdresse)) {
                return true;
            }
        }
        return false;
    }

    public void entferneAdmin(String pAdresse) throws NoSuchFieldException {
        Verbindung lVerbindung = gibAdmin(pAdresse);
        zAdminVerbindungen.remove(lVerbindung);
    }

    public String[] gibAdminAdressen() {
        String[] lAdmins = new String[zAdminVerbindungen.size()];
        for (int i = 0; i < zAdminVerbindungen.size(); i++) {
           lAdmins[i] = zAdminVerbindungen.get(i).toString();
        }
        return lAdmins;
    }

    public Verbindung gibAdmin(String pAdresse) throws NoSuchFieldException {
        for (Verbindung lVerbindung : zAdminVerbindungen) {
            if (lVerbindung.toString().equals(pAdresse)) {
                return lVerbindung;
            }
        }
        throw new NoSuchFieldException("Kein Admin mit dieser Adresse gefunden");
    }

    public boolean istGebannt(String pIP) {
        return zGebannteIPs.contains(pIP);
    }

    public void bannHinzufuegen(String pIP) {
        zGebannteIPs.add(pIP);
    }

    public void bannEntfernen(String pIP) {
        zGebannteIPs.remove(pIP);
    }

    public String[] gibAdressenMitIP(String pIP) {
        List<String> lAdressen = new ArrayList<>();
        for (Verbindung lVerbindungen : zAktiveVerbindungen) {
            if (lVerbindungen.getIP().equals(pIP)) {
                lAdressen.add(lVerbindungen.toString());
            }
        }
        String[] lAdressenArray = new String[lAdressen.size()];
        for (int i = 0; i < lAdressen.size(); i++) {
            lAdressenArray[i] = lAdressen.get(i);
        }
        return lAdressenArray;
    }
}
