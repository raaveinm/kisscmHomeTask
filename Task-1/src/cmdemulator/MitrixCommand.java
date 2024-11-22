package cmdemulator;

import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.MutableGraph;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static guru.nidi.graphviz.model.Factory.*;

public class MitrixCommand implements Command {

    private final String graphvizPath;

    public MitrixCommand(String graphvizPath) {
        this.graphvizPath = graphvizPath;
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 3) {
            return "Usage: Mitrix <package_name> <max_depth> <repository_url>";
        }

        String packageName = args[0];
        int maxDepth = Integer.parseInt(args[1]);
        String repositoryUrl = args[2];

        try {
            Graphviz.useEngine(new GraphvizCmdLineEngine(graphvizPath));

            MutableGraph graph = mutGraph("dependencies").setDirected(true);
            Set<String> visited = new HashSet<>();

            buildDependencyGraph(graph, packageName, repositoryUrl, maxDepth, 0, visited);

            Graphviz.fromGraph(graph).render(Format.PNG).toFile(new File("dependencies.png"));
            return "Dependency graph saved to dependencies.png";
        } catch (IOException | InterruptedException e) {
            return "Error generating dependency graph: " + e.getMessage();
        }
    }

    private static final String DEFAULT_URL = "https://search.maven.org/solrsearch/select?q=g:%s+AND+a:%s&rows=20&wt=json";

    private void buildDependencyGraph(MutableGraph graph, String packageName, String repositoryUrl, int maxDepth, int currentDepth, Set<String> visited) throws IOException, InterruptedException {
        if (currentDepth >= maxDepth || visited.contains(packageName)) {
            return;
        }

        visited.add(packageName);
        String[] parts = packageName.split(":");
        String groupId = parts[0];
        String artifactId = parts[1];

        String url = String.format(repositoryUrl.isEmpty() || repositoryUrl == null ? DEFAULT_URL : repositoryUrl, groupId, artifactId);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();
        int dependenciesStartIndex = responseBody.indexOf("\"dependencies\":[") + 15;
        int dependenciesEndIndex = responseBody.indexOf("]", dependenciesStartIndex);

        if (dependenciesStartIndex == 14 || dependenciesEndIndex == -1) {
            return; /* No dependencies found or bad JSON format*/
        }

        String dependenciesString = responseBody.substring(dependenciesStartIndex, dependenciesEndIndex);
        String[] dependencies = dependenciesString.split(",");

        for (String dependency : dependencies) {
            String depPackageName = dependency.replaceAll("[{\"}]", "").trim();

            if (!depPackageName.isEmpty()) {
                graph.add(mutNode(packageName).addLink(mutNode(depPackageName)));
                buildDependencyGraph(graph, depPackageName, repositoryUrl, maxDepth, currentDepth + 1, visited);
            }
        }
    }
}