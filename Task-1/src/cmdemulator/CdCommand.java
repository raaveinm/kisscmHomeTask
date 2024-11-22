package cmdemulator;

public class CdCommand implements Command {
    private final FileSystem fileSystem;
    public CdCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            fileSystem.setCurrentDirectory("/");
            return "";
        }

        String targetDirectory = args[0];
        if (targetDirectory.equals("..")) {
            if (!fileSystem.getCurrentDirectory().equals("/")) {
                int lastSlashIndex = fileSystem.getCurrentDirectory().lastIndexOf("/");
                if (lastSlashIndex > 0) {
                    fileSystem.setCurrentDirectory(fileSystem.getCurrentDirectory().substring(0, lastSlashIndex));
                } else {fileSystem.setCurrentDirectory("/");}
            }
        }
        else if (targetDirectory.startsWith("/")) {fileSystem.setCurrentDirectory(targetDirectory);}
        else {fileSystem.setCurrentDirectory(fileSystem.getCurrentDirectory() + "/" + targetDirectory);}
        return "";
    }
}