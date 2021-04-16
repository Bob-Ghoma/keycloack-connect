import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthGitLab {
    private String clientId = "c4e14a03723b6d6ca7687abaae82ca570fc3ce22af516fe26c2afab06e3ee0e1";
    private String redirectUri = "http://localhost:8889";
    private String clientSecret = "d8b6431eccf3a4b260673db7044d860e7377ac1a809146183d7c6b0407e9eaff";
    private HttpServer server;
    private HttpClient client;


    public void openAuthorizationPage(){
        String url = "https://gitlab.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code&state=STATE&scope=read_user";
        String parameterUrl = String.format(url, clientId, redirectUri );
        try {
            Desktop.getDesktop().browse(new URI(parameterUrl));
        }catch (Exception e){
            System.out.println(" Cliquer sur le lien !" + parameterUrl);
        }
    }

    public void runHttpServer(){
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 8889), 0);
            server.createContext("/", new HandlerGitLab(this));
            server.start();
        } catch (IOException e) {
            System.out.println("Oops, there was an error !!");
        }
    }

    public void sendAuthCode(String code){
        server.stop(0);
        String url = "https://gitlab.com/oauth/token";
        String parameters = "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s";
        String finalParameters = String.format(parameters, clientId, clientSecret, code, redirectUri);

        try {
            client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(finalParameters))
                    .uri(new URI(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String content = response.body();
            System.out.println(content);
            JSONObject data = (JSONObject) JSONValue.parse(content);
            String accessToken = (String) data.get("access_token");
            System.out.println(accessToken);
            callApi(accessToken);

        }catch (URISyntaxException | IOException | InterruptedException e){
          e.printStackTrace();
        }

    }

    public void callApi(String accessToken){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("https://gitlab.com/oauth/token/info"))
                    .header("Accept", "application/json")
                    .header("Authorization", String.format("Bearer %s" + accessToken))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject data = (JSONObject) JSONValue.parse(response.body());
            System.out.println(data);
            showProjects(accessToken);

        } catch (URISyntaxException | InterruptedException | IOException exception){
            exception.printStackTrace();
        }
    }

    public void showProjects(String accessToken){
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("https://gitlab.com/api/v4/projects?owned=true"))
                    .header("Accept", "application/json")
                    .header("Authorization", String.format("Bearer %s" + accessToken))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray data = (JSONArray) JSONValue.parse(response.body());
           // System.out.println(data);
            for (Object name : data) {
                JSONObject nameFile = (JSONObject) name;
                System.out.println(nameFile.get("name"));
            }

        }catch (URISyntaxException | IOException | InterruptedException e){
            System.out.println("ok");
        }
    }

}
