import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Handler implements HttpHandler {

    private Auth auth;

    public Handler(Auth auth){
        this.auth = auth;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("User connected !!");
        OutputStream response = exchange.getResponseBody();
        String message = " Vous etes connecte ";
        exchange.sendResponseHeaders(200,message.length());
        response.write(message.getBytes(StandardCharsets.UTF_8));
        response.flush();

        HashMap<String, String> params = parseQueryParams(exchange.getRequestURI().toString());
        System.out.println(params);
        if (params.containsKey("code")){
            auth.sendAuthCode(params.get("code"));
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