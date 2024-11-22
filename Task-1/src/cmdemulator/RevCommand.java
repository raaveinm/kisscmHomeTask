package cmdemulator;

public class RevCommand implements Command {
    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            return "Usage: Rex <string>";
        }

        String inputString = args[0];
        return new StringBuilder(inputString).reverse().toString();
    }
}