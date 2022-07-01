package Constants;

public interface GLOBAL_CONST {

    String OK = "+OK ";
    String ERR = "-ERR ";

    String VERBINDUNG_HERGESTELLT = "Verbindung hergestellt.";
    String ANGEMELDET = "Erfolgreich als \"%s\" angemeldet.";
    String NAME_EXISTIERT = "Der Name existiert bereits.";
    String BEREITS_VERBUNDEN = "Diese Adresse war bereits verbunden und wurde nun entfernt. Bitte erneut versuchen.";
    String BEFEHL_KEIN = "Der Server konnte keinen Befehl in der Nachricht finden.";
    String NAME_ZEICHEN = "Der Name enthält unzulässige Zeichen. Folgendes Zeichen darf nicht enthalten sein: \"%s\"";
    String NICHT_ANGEMELDET = "Um dies zu tun, ist eine Anmeldung erforderlich.";
    String NACHRICHT_UEBERMITTELT = "Die Nachricht wurde zugestellt.";
    String NACHRICHT_SELBST = "Die Nachricht wurde an den Client zurückgestellt.";
    String NACHRICHT_LEER = "Die Nachricht ist leer.";
    String NACHRICHT_ZEICHEN = "Die Nachricht enthält unzulässige Zeichen. Folgendes Zeichen darf nicht enthalten sein: \"%s\"";
    String NAME_KEIN = "Der Server konnte keinen Namen in der Nachricht finden.";
    String NAME_FEHLT = "Der angegebene Name wurde nicht gefunden. Name: \"%s\"";
    String VERBINDUNG_GETRENNT = "Die Verbindung wurde getrennt.";
    String NUTZER_GEBANNT = "Diese Adresse wurde vom Server gebannt.";
    String FEHLER_FATAL = "Ein fataler Fehler ist aufgetreten. Die Aktion wurde abgebrochen.";

    interface CLIENT_BEFEHLE {
        String OK = "+OK";
        String ERR = "-ERR";
        String NAME = "NAME";
        String NACHRICHT = "NACHRICHT";
        String PRIVATE_NACHRICHT = "NACHRICHTAN";
        String VERBINDUNG_TRENNEN = "TRENNEN";
    }

    interface SERVER_BEFEHLE {
        String AUFLISTEN = "LIST";
        String NACHRICHT = "NACHRICHT";
        String PRIVATE_NACHRICHT = "PRIVATNACHRICHT";
        String ADMIN_ERTEILT = "ADMINERTEILT";
        String ADMIN_ENTZOGEN = "ADMINENTZOGEN";
        String TRENNEN = "TRENNEN";
    }

    String DEBUG_PREFIX = "[Debug] ";
    String CONSOLE_PREFIX = "[Console] ";

    interface DEBUG_NACHRICHTEN {
        String VERBINDUNG = "%s: Verbindung hergestellt.";
        String ANMELDUNG = "%s: Client wurde angemeldet als \"%s\"";
        String LISTE = "Eine neue Liste wurde an alle Clients weitergeleitet.";
        String NACHRICHT = "%s: Sendet Nachricht von \"%s\" an alle verbundenen Clients. Nachricht: \"%s\"";
        String PRIVATE_NACHRICHT = "%s: Sendet private Nachricht von \"%s\" an \"%s\", mit der Adresse \"%s\". Nachricht: \"%s\"";
        String VERBINDUNG_TRENNEN = "%s: Trennt die Verbindung.";
        String VERBINDUNG_GETRENNT = "%s: Verbindung getrennt. Namen freigegeben: \"%s\"";
        String AKTION_ERFOLGREICH = "%s: Die Aktion \"%s\" war erfolgreich. Antwort vom Client: \"%s\"";
        String AKTION_FEHLGESCHLAGEN = "%s: Die Aktion \"%s\" ist fehlgeschlagen. Antwort vom Client: \"%s\"";
    }

    interface CONSOLE_NACHRICHTEN {
        String FEHLER = COLOR.ANSI_RED + "Fehler: " + COLOR.ANSI_RESET;
        String ERFOLG = COLOR.ANSI_GREEN + "Erfolgreich: " + COLOR.ANSI_RESET;
        String INFO = COLOR.ANSI_BLUE + "Info: " + COLOR.ANSI_RESET;
        String GESTARTET = "Konsole gestartet.";
    }
}
