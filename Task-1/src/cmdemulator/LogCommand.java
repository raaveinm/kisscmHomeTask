package cmdemulator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogCommand implements Command {

    private final String logFilePath;

    public LogCommand(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 2) {
            return "Usage: Artificer <command> <output>";
        }

        String command = args[0];
        String output = args[1];

        try (FileWriter file = new FileWriter(logFilePath, true)) {
            JSONObject logEntry = new JSONObject();
            logEntry.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            logEntry.put("command", command);
            logEntry.put("output", output);

            JSONArray logArray = new JSONArray();
            if (Files.exists(Paths.get(logFilePath))) {
                String existingLog = new String(Files.readAllBytes(Paths.get(logFilePath)));
                if (!existingLog.isEmpty()) {
                    logArray = new JSONArray(existingLog);
                }
            }

            logArray.put(logEntry);
            file.write(logArray.toString(2)); // Use toString(2) for pretty printing
            file.flush();

            return "Command and output logged successfully.";
        } catch (IOException e) {
            return "Error logging command: " + e.getMessage();
        }
    }
}