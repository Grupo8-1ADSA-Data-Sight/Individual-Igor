package util;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
// METODO SLACK PRECISA SER CHAMADO EM ALGUM LUGAR
public class Slack {
    public static void criarConexaoSlack() throws IOException, InterruptedException {
        JSONObject json = new JSONObject();

        json.put("text", "");

        Slack.sendMessage(json);

    }

    private static HttpClient client = HttpClient.newHttpClient();
    private static final String URL = "https://hooks.slack.com/services/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";

    public static void sendMessage(JSONObject content) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(URL))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(String.format("Status: %s", response.statusCode()));
        System.out.println(String.format("Response: %s", response.body()));
    }
}
