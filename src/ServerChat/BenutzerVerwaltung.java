package ServerChat;

import linearestrukturen.List;

import java.util.HashMap;

public class BenutzerVerwaltung {

    private final HashMap<String, String> zNamenZuIP;
    private final HashMap<String, String> zIPZuNamen;
    private final HashMap<String, List<String>> zAktionen;
    private final List<String> zNamenListe;
    private final List<String> zAdressen;
    private final List<String> zAdmins;
    private final List<String> zGebannt;

    public BenutzerVerwaltung() {
        zNamenZuIP = new HashMap<>();
        zIPZuNamen = new HashMap<>();
        zAktionen = new HashMap<>();
        zNamenListe = new List<>();
        zAdressen = new List<>();
        zAdmins = new List<>();
        zGebannt = new List<>();
    }

    public void neueVerbindung(String pAdresse) {
        zAdressen.append(pAdresse);
    }

    public void verbindungHinzufuegen(String pAdresse, String pName) {
        zNamenZuIP.put(pName, pAdresse);
        zIPZuNamen.put(pAdresse, pName);
        zAktionen.put(pAdresse, new List<>());
        zNamenListe.append(pName);
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
            String lName = zIPZuNamen.get(pAdresse);
            zNamenZuIP.remove(lName);
            zIPZuNamen.remove(pAdresse);
            zAktionen.remove(pAdresse);
            zNamenListe.toFirst();
            while (zNamenListe.hasAccess()) {
                if (zNamenListe.getContent().equals(lName)) {
                    zNamenListe.remove();
                }
                zNamenListe.next();
            }
        }
    }

    public boolean istAngemeldet(String pAdresse) {
        return zIPZuNamen.containsKey(pAdresse);
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
        return zNamenZuIP.containsKey(pName);
    }

    public String gibVerbindung(String pName) {
        return zNamenZuIP.get(pName);
    }

    public String gibName(String pAdresse) {
        return zIPZuNamen.get(pAdresse);
    }

    public List<String> gibAktionListe(String pAdresse) {
        return zAktionen.get(pAdresse);
    }

    public List<String> gibNamenListe() {
        return zNamenListe;
    }

    public List<String> gibAdressenListe() {
        return zAdressen;
    }

    public void adminHinzufuegen(String pAdresse) {
        zAdmins.append(pAdresse);
    }

    public boolean istAdmin(String pAdresse, boolean pEntfernen) {
        zAdmins.toFirst();
        while (zAdmins.hasAccess()) {
            if (zAdmins.getContent().equals(pAdresse)) {
                if (pEntfernen) {
                    zAdmins.remove();
                }
                return true;
            }
        }
        return false;
    }

    public String[] gibAdmins() {
        if (zAdmins.length() == 0) {
            return null;
        }
        zAdmins.toFirst();
        String[] lAdmins = new String[zAdmins.length()];
        for (int i = 0; i < lAdmins.length; i++) {
            lAdmins[i] = zAdmins.getContent();
            zAdmins.next();
        }
        return lAdmins;
    }

    public boolean istGebannt(String pIP) {
        zGebannt.toFirst();
        while (zGebannt.hasAccess()) {
            if (zGebannt.getContent().equals(pIP)) {
                return true;
            }
            zGebannt.next();
        }
        return false;
    }

    public void bannHinzufuegen(String pIP) {
        zGebannt.append(pIP);
    }

    public void bannEntfernen(String pIP) {
        boolean lGebannt = istGebannt(pIP);
        if (lGebannt) {
            zGebannt.remove();
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
