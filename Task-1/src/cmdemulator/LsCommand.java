package cmdemulator;

public class LsCommand implements Command {
    private final FileSystem fileSystem;

    public LsCommand(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String execute(String[] args) {
        StringBuilder output = new StringBuilder();
        for (String file : fileSystem.getFiles()) {
            output.append(file).append("\n");
        }
        return output.toString();
    }
}