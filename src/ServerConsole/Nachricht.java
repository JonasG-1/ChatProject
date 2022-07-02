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
        String instanceName = pConsole.getInstanceName();
        if (!pArgumente.isEmpty()) {
            pController.sendeNachrichtAls(pArgumente, instanceName, instanceName);
            hatDebugger.print("Nachricht gesendet.", 1);
        } else {
            hatDebugger.print("Die Nachricht darf nicht leer sein.", 0);
        }
        return true;
    }
}
