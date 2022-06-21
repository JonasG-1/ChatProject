package ServerChat;

import linearestrukturen.List;

import java.util.HashMap;

public class BenutzerVerwaltung {

    private final HashMap<String, String> zNamenZuAdressen;
    private final HashMap<String, String> zAdressenZuNamen;
    private final HashMap<String, List<String>> zAdressenZuAktionen;
    private final List<String> zNamen;
    private final List<String> zAdressen;
    private final List<String> zAdminAdressen;
    private final List<String> zGebannteAdressen;

    public BenutzerVerwaltung() {
        zNamenZuAdressen = new HashMap<>();
        zAdressenZuNamen = new HashMap<>();
        zAdressenZuAktionen = new HashMap<>();
        zNamen = new List<>();
        zAdressen = new List<>();
        zAdminAdressen = new List<>();
        zGebannteAdressen = new List<>();
    }

    public void neueVerbindung(String pAdresse) {
        zAdressen.append(pAdresse);
    }

    public void verbindungHinzufuegen(String pAdresse, String pName) {
        zNamenZuAdressen.put(pName, pAdresse);
        zAdressenZuNamen.put(pAdresse, pName);
        zAdressenZuAktionen.put(pAdresse, new List<>());
        zNamen.append(pName);
    }

    public void verbindungEntfernen(String pAdresse) {
        if (istVerbunden(pAdresse)) {
            zAdressen.toFirst();
            while (zAdressen.hasAccess()) {
                if (zAdressen.getContent().equals(pAdresse)) {
                    zAdressen.remove();
                }
                zAdressen.next();
            }
        }
        if (istAngemeldet(pAdresse)) {
            String lName = zAdressenZuNamen.get(pAdresse);
            zNamenZuAdressen.remove(lName);
            zAdressenZuNamen.remove(pAdresse);
            zAdressenZuAktionen.remove(pAdresse);
            zNamen.toFirst();
            while (zNamen.hasAccess()) {
                if (zNamen.getContent().equals(lName)) {
                    zNamen.remove();
                }
                zNamen.next();
            }
        }
    }

    public boolean istAngemeldet(String pAdresse) {
        return zAdressenZuNamen.containsKey(pAdresse);
    }

    public boolean istVerbunden(String pAdresse) {
        zAdressen.toFirst();
        while (zAdressen.hasAccess()) {
            if (zAdressen.getContent().equals(pAdresse)) {
                return true;
            }
            zAdressen.next();
        }
        return false;
    }

    public boolean nameExistiert(String pName) {
        return zNamenZuAdressen.containsKey(pName);
    }

    public String gibVerbindung(String pName) {
        return zNamenZuAdressen.get(pName);
    }

    public String gibName(String pAdresse) {
        return zAdressenZuNamen.get(pAdresse);
    }

    public List<String> gibAktionListe(String pAdresse) {
        return zAdressenZuAktionen.get(pAdresse);
    }

    public List<String> gibNamenListe() {
        return zNamen;
    }

    public List<String> gibAdressenListe() {
        return zAdressen;
    }

    public void adminHinzufuegen(String pAdresse) {
        zAdminAdressen.append(pAdresse);
    }

    public boolean istAdmin(String pAdresse, boolean pEntfernen) {
        zAdminAdressen.toFirst();
        while (zAdminAdressen.hasAccess()) {
            if (zAdminAdressen.getContent().equals(pAdresse)) {
                if (pEntfernen) {
                    zAdminAdressen.remove();
                }
                return true;
            }
        }
        return false;
    }

    public String[] gibAdmins() {
        if (zAdminAdressen.length() == 0) {
            return null;
        }
        zAdminAdressen.toFirst();
        String[] lAdmins = new String[zAdminAdressen.length()];
        for (int i = 0; i < lAdmins.length; i++) {
            lAdmins[i] = zAdminAdressen.getContent();
            zAdminAdressen.next();
        }
        return lAdmins;
    }

    public boolean istGebannt(String pIP) {
        zGebannteAdressen.toFirst();
        while (zGebannteAdressen.hasAccess()) {
            if (zGebannteAdressen.getContent().equals(pIP)) {
                return true;
            }
            zGebannteAdressen.next();
        }
        return false;
    }

    public void bannHinzufuegen(String pIP) {
        zGebannteAdressen.append(pIP);
    }

    public void bannEntfernen(String pIP) {
        boolean lGebannt = istGebannt(pIP);
        if (lGebannt) {
            zGebannteAdressen.remove();
        }
    }

    public List<String> gibAdressenMitIP(String pIP) {
        List<String> lListe = new List<>();
        zAdressen.toFirst();
        while (zAdressen.hasAccess()) {
            if (zAdressen.getContent().startsWith(pIP)) {
                lListe.append(zAdressen.getContent());
            }
            zAdressen.next();
        }
        return lListe;
    }
}
