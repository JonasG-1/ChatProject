package ServerConsole;

import Constants.GLOBAL_CONST;
import ServerChat.ChatServer;
import ServerChat.ServerController;

public class Nachricht implements Command {

    @Override
    public boolean istBefehl(String pBefehl) {
        return pBefehl.equalsIgnoreCase("msg");
    }

    @Override
    public boolean run(String pArgumente, ServerController pController, Console pConsole) {
        ChatServer lServer = pController.gibServer();
        if (!pArgumente.isEmpty()) {
            String lBefehl = GLOBAL_CONST.SERVER_BEFEHLE.NACHRICHT + " Konsole ";
            lServer.sendToAll(lBefehl + pArgumente);
            hatDebug.print("Nachricht gesendet.", 1);
        } else {
            hatDebug.print("Die Nachricht darf nicht leer sein.", 0);
        }
        return true;
    }
}
