package ServerConsole;

import ServerChat.ServerController;

public class Stop implements Command {

    public boolean istBefehl(String pBefehl) {
        return pBefehl.equalsIgnoreCase("stop");
    }

    @Override
    public boolean run(String pArgumente, ServerController pController, Console pConsole) {
        if (!pArgumente.isEmpty()) {
            hatDebugger.print("Dieser Befehl benötigt keine weiteren Argumente.", 2);
            return true;
        }
        hatDebugger.print("Der Server wird heruntergefahren, bitte warten...", 2);
        pConsole.lLaeuft = false;
        return true;
    }
}
