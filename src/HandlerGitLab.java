import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HandlerGitLab implements HttpHandler {

    private AuthGitLab authGitLab;

    public HandlerGitLab(AuthGitLab authGitLab) {
        this.authGitLab = authGitLab;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Utilisateur en ligne");
        OutputStream response = exchange.getResponseBody();
        String message = " En ligne ";
        exchange. sendResponseHeaders(200, message.length());
        response.write(message.getBytes(StandardCharsets.UTF_8));
        response.flush();

        HashMap<String, String> params = parseQueryParams(exchange.getRequestURI().toString());
        System.out.println(params);
        if (params.containsKey("code")){
            authGitLab.sendAuthCode(params.get("code"));
        }
    }

    public HashMap<String, String> parseQueryParams(String uri) {
        HashMap<String, String> queryParams = new HashMap<>();
        if (uri.contains("?")) {
            String paramString = uri.split("\\?")[1];
            String[] params = paramString.split("&");
            for (String param : params) {
                if (param.contains("=")) {
                    String[] splitedParam = param.split("=");
                    queryParams.put(splitedParam[0], splitedParam[1]);
                }
            }
        }
        return queryParams;
    }

}
