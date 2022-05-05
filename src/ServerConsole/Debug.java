package ServerConsole;

import ServerChat.ServerController;

public class Debug implements Command {

    @Override
    public boolean istBefehl(String pBefehl) {
        if (pBefehl.isEmpty()) {
            hatDebugger.ausgabeAnhalten();
            return true;
        }
        return pBefehl.equalsIgnoreCase("debug");
    }

    @Override
    public boolean run(String pArgumente, ServerController pController, Console pConsole) {
        if (pArgumente.equalsIgnoreCase("on")) {
            hatDebugger.print("Der Dienst wird fortgesetzt.", 1);
            hatDebugger.setzeDebug(true);
        } else if (pArgumente.equalsIgnoreCase("off")){
            hatDebugger.setzeDebug(false);
            hatDebugger.print("Der Dienst wurde eingestellt.", 1);
        } else if (pArgumente.isEmpty()) {
            return true;
        } else {
            hatDebugger.print("Befehl \"debug\": Argumente sind: \"on\", \"off\"", 2);
        }
        return true;
    }
}
