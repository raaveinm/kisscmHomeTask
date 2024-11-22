package cmdemulator;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileSystem {
    private String currentDirectory = "/";
    private final List<String> files = new ArrayList<>();
    private final String tarFilePath; // Path to the tar archive
    private final String logFilePath; // Path to the JSON log file

    public FileSystem(String tarFilePath, String logFilePath) {
        this.tarFilePath = tarFilePath;
        this.logFilePath = logFilePath;
        loadFileSystemFromTar();
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public List<String> getFiles() {
        return files;
    }

    private void loadFileSystemFromTar() {
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new FileInputStream(tarFilePath))) {
            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    files.add(entry.getName() + "/");
                } else {
                    files.add(entry.getName());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading file system from TAR: " + e.getMessage());
        }
    }

    public void logCommand(String command, String output) {
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
            file.write(logArray.toString(2));
            file.flush();
        } catch (IOException e) {
            System.err.println("Error logging command: " + e.getMessage());
        }
    }
    public String getLogFilePath() {
        return logFilePath;
    }
}