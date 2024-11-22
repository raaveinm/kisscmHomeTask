package cmdemulator;

public class ExitCommand implements Command {
    @Override
    public String execute(String[] args) {
        System.exit(0);
        return ""; // This line won't be reached
    }
}