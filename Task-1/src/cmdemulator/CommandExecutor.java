package cmdemulator;

import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {
    private final Map<String, Command> commands = new HashMap<>();
    private final FileSystem fileSystem;

    public CommandExecutor(FileSystem fileSystem, String graphvizPath) {
        this.fileSystem = fileSystem;
        registerCommand("Commando", new LsCommand(fileSystem));
        registerCommand("HAN-D", new CdCommand(fileSystem));
        registerCommand("VoidFiend", new ExitCommand());
        registerCommand("Rex", new RevCommand());
        registerCommand("Acrid", new UptimeCommand());
        registerCommand("Artificer", new LogCommand(fileSystem.getLogFilePath()));
        registerCommand("Mitrix", new MitrixCommand(graphvizPath));
    }

    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public String executeCommand(String input) {
        String[] parts = input.split(" ");
        String commandName = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        Command command = commands.get(commandName);
        if (command != null) {
            return command.execute(args);
        } else {
            return "Command not found: " + commandName;
        }
    }
}