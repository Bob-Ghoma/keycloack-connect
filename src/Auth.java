import com.sun.net.httpserver.HttpServer;
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

public class Auth {
    private String clientId = "application-java";
    private String clientSecret = "3d0c0329-a993-4bd5-a902-a947a79092b5";
    private String redirectUri = "http://localhost:8888";

    public void openAuthorizationPage(){
        String url = "https://auth.dunarr.com/auth/realms/hoc/protocol/openid-connect/auth?client_id=%s&response_type=code&redirect_uri=%s";
        String finalUrl = String.format(url, clientId, redirectUri);
        try {
            Desktop.getDesktop().browse(new URI(finalUrl));
        } catch (Exception e) {
            System.out.println("Merci d'ouvir ce lien " + finalUrl);
        }
    }

    public void runHttpServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8888), 0);
            server.createContext("/", new Handler(this));
            server.start();
        } catch (IOException e) {
            System.out.println("Oops, there was an error !!");
        }
    }

    public void sendAuthCode(String code){
        String url = "https://auth.dunarr.com/auth/realms/hoc/protocol/openid-connect/token";
        String parameter = "client_id=%s&client_secret=%s&grant_type=authorization_code&redirect_uri=%s&code=%s";
        String finalParameter = String.format(parameter, clientId, clientSecret, redirectUri, code);
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(finalParameter))
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
        }catch (URISyntaxException | IOException | InterruptedException e){
            System.out.println(e);
        }
    }
}
