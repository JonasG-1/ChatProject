package ClientChat;

import sum.ereignis.*;
import sum.komponenten.*;

public class Oberflaeche extends EBAnwendung {
    // Objekte
    Etikett hatEtikettHeader;
    Etikett hatEtikettLogin;
    Etikett hatEtikettIP;
    Etikett hatEtikettPort;
    Etikett hatEtikettName;
    Etikett hatEtikettStatus;
    Etikett hatEtikettNutzer;
    Etikett hatEtikettChat;
    Etikett hatEtikettPrivat;
    Etikett hatEtikettChatStatus;
    Knopf hatKnopfAktion;
    Knopf hatKnopfSenden;
    Knopf hatKnopfBeenden;
    Textfeld hatTextfeldIP;
    Textfeld hatTextfeldPort;
    Textfeld hatTextfeldName;
    Textfeld hatTextfeldChat;
    Textfeld hatTextfeldPrivat;
    Zeilenbereich hatZeilenbereichNutzer;
    Zeilenbereich hatZeilenbereichChat;
    Schalter hatSchalterPrivat;
    ClientController kenntController;
    private int zStatus = 0;

    public Oberflaeche(ClientController pController) {
        // Initialisierung der Oberklasse
        super(1067, 900);
        kenntController = pController;
        hatEtikettHeader = new Etikett(10, 10, 1040, 50, "SimpleChat FSG");
        hatEtikettHeader.setzeAusrichtung(1);
        hatEtikettHeader.setzeSchriftgroesse(16);
        hatEtikettLogin = new Etikett(10, 80, 300, 40, "Login");
        hatEtikettLogin.setzeAusrichtung(1);
        hatEtikettLogin.setzeSchriftgroesse(12);
        hatEtikettIP = new Etikett(10, 130, 110, 30, "Server-IP");
        hatEtikettIP.setzeAusrichtung(1);
        hatEtikettPort = new Etikett(10, 170, 110, 30, "Port");
        hatEtikettPort.setzeAusrichtung(1);
        hatEtikettName = new Etikett(10, 150, 110, 40, "Name");
        hatEtikettName.setzeAusrichtung(1);
        hatEtikettStatus = new Etikett(10, 210, 300, 30, "");
        hatEtikettStatus.setzeAusrichtung(1);
        hatEtikettNutzer = new Etikett(10, 320, 300, 40, "Verbundene Nutzer");
        hatEtikettNutzer.setzeAusrichtung(1);
        hatEtikettNutzer.setzeSchriftgroesse(12);
        hatEtikettChat = new Etikett(370, 80, 680, 40, "Chat");
        hatEtikettChat.setzeAusrichtung(1);
        hatEtikettChat.setzeSchriftgroesse(12);
        hatEtikettPrivat = new Etikett(540, 750, 120, 30, "Name");
        hatEtikettPrivat.setzeAusrichtung(2);
        hatEtikettPrivat.setzeSchriftgroesse(10);
        hatKnopfAktion = new Knopf(100, 250, 120, 40, "Verbinden");
        hatKnopfAktion.setzeBearbeiterGeklickt("hatKnopfAktionGeklickt");
        hatKnopfSenden = new Knopf(960, 720, 90, 50, "Senden");
        hatKnopfSenden.setzeBearbeiterGeklickt("hatKnopfSendenGeklickt");
        hatKnopfBeenden = new Knopf(483.5, 810, 100, 50, "Beenden");
        hatKnopfBeenden.setzeBearbeiterGeklickt("hatKnopfBeendenGeklickt");
        hatTextfeldIP = new Textfeld(130, 130, 180, 30, "localhost");
        hatTextfeldIP.setzeBearbeiterEingabeBestaetigt("hatTextfeldIPEingabeBestaetigt");
        hatTextfeldPort = new Textfeld(130, 170, 180, 30, "8888");
        hatTextfeldPort.setzeBearbeiterEingabeBestaetigt("hatTextfeldPortEingabeBestaetigt");
        hatTextfeldName = new Textfeld(130, 150, 180, 40, "");
        hatTextfeldName.setzeBearbeiterEingabeBestaetigt("hatTextfeldNameEingabeBestaetigt");
        hatTextfeldChat = new Textfeld(370, 710, 580, 30, "");
        hatTextfeldChat.setzeBearbeiterEingabeBestaetigt("hatTextfeldChatEingabeBestaetigt");
        hatTextfeldPrivat = new Textfeld(680, 750, 270, 30, "");
        hatTextfeldPrivat.setzeBearbeiterEingabeBestaetigt("hatTextfeldPrivatEingabeBestaetigt");
        hatZeilenbereichNutzer = new Zeilenbereich(10, 370, 300, 410, "Bitte mit einem Server verbinden.");
        hatZeilenbereichChat = new Zeilenbereich(370, 130, 680, 530, "");
        hatEtikettChatStatus = new Etikett(370, 660, 680, 40, "");
        hatEtikettChatStatus.setzeAusrichtung(1);
        hatSchalterPrivat = new Schalter(370, 750, 160, 30, "Private Nachricht");
        hatSchalterPrivat.setzeBearbeiterGeklickt("hatSchalterPrivatGeklickt");

        hatZeilenbereichNutzer.deaktiviere();
        hatEtikettName.verstecke();
        hatTextfeldName.verstecke();
        hatTextfeldChat.deaktiviere();
        hatSchalterPrivat.deaktiviere();
        hatTextfeldPrivat.deaktiviere();
        hatKnopfSenden.deaktiviere();
        hatZeilenbereichChat.deaktiviere();
    }

    public void hatKnopfAktionGeklickt() {
        if (zStatus == 0) {
            kenntController.verbinden();
        } else if (zStatus == 1) {
            kenntController.anmelden();
        } else if (zStatus == 2) {
            kenntController.trennen(false);
        }
    }

    public void hatKnopfSendenGeklickt() {
        kenntController.sendeNachricht(hatTextfeldChat.inhaltAlsText());
        hatTextfeldChat.setzeInhalt("");
    }

    public void hatKnopfBeendenGeklickt() {
        kenntController.beenden();
    }

    public void hatTextfeldIPEingabeBestaetigt() {
        hatKnopfAktionGeklickt();
    }

    public void hatTextfeldPortEingabeBestaetigt() {
        hatKnopfAktionGeklickt();
    }

    public void hatTextfeldNameEingabeBestaetigt() {
        hatKnopfAktionGeklickt();
    }

    public void hatTextfeldChatEingabeBestaetigt() {
        hatKnopfSendenGeklickt();
    }

    public void hatTextfeldPrivatEingabeBestaetigt() {
        hatSchalterPrivatGeklickt();
    }

    public void hatSchalterPrivatGeklickt() {
        
    }

    public String gibIP() {
        return hatTextfeldIP.inhaltAlsText();
    }

    public String gibPort() {
        return hatTextfeldPort.inhaltAlsText();
    }

    public String gibName() {
        return hatTextfeldName.inhaltAlsText();
    }

    public String gibChatEingabe() {
        return hatTextfeldChat.inhaltAlsText();
    }

    public boolean gibPrivat() {
        return hatSchalterPrivat.angeschaltet();
    }

    public String gibPrivatNamen() {
        return hatTextfeldPrivat.inhaltAlsText();
    }

    public void setzeStatus(String pStatus) {
        hatEtikettStatus.setzeInhalt(pStatus);
    }

    public void setzeChatStatus(String pStatus) {
        hatEtikettChatStatus.setzeInhalt(pStatus);
    }

    public void setzeNutzer(String pListe) {
        hatZeilenbereichNutzer.setzeInhalt(pListe);
    }

    public void setzeNutzerZahl(int pZahl) {
        hatEtikettNutzer.setzeInhalt(String.format("Verbundene Nutzer (%d):", pZahl));
    }

    public void haengeChatAn(String pNachricht) {
        hatZeilenbereichChat.haengeAn(pNachricht);
    }

    public void setzeChatZurueck() {
        hatZeilenbereichChat.loescheAlles();
    }

    public void statusVerbunden() {
        zStatus = 1;
        hatEtikettIP.verstecke();
        hatEtikettPort.verstecke();
        hatTextfeldIP.verstecke();
        hatTextfeldPort.verstecke();
        hatEtikettName.zeige();
        hatTextfeldName.zeige();
        hatKnopfAktion.setzeInhalt("Anmelden");
        hatEtikettStatus.setzeInhalt("Verbunden");
    }

    public void statusAngemeldet() {
        zStatus = 2;
        hatTextfeldName.deaktiviere();
        hatKnopfAktion.setzeInhalt("Trennen");
        hatEtikettStatus.setzeInhalt("Angemeldet");
        hatTextfeldChat.aktiviere();
        hatSchalterPrivat.aktiviere();
        hatTextfeldPrivat.aktiviere();
        hatZeilenbereichChat.aktiviere();
        hatKnopfSenden.aktiviere();
    }

    public void statusReset() {
        zStatus = 0;
        hatTextfeldName.aktiviere();
        hatEtikettIP.zeige();
        hatEtikettPort.zeige();
        hatTextfeldIP.zeige();
        hatTextfeldPort.zeige();
        hatEtikettName.verstecke();
        hatTextfeldName.verstecke();
        hatKnopfAktion.setzeInhalt("Verbinden");
        hatEtikettStatus.setzeInhalt("Getrennt");
        hatTextfeldChat.deaktiviere();
        hatSchalterPrivat.deaktiviere();
        hatTextfeldPrivat.deaktiviere();
        hatZeilenbereichChat.deaktiviere();
        hatKnopfSenden.deaktiviere();
    }

    public int gibStatus() {
        return zStatus;
    }

    public void ende() {
        this.beenden();
    }
    
    
}