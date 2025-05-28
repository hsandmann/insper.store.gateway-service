package store.gateway.security;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouterValidator {

    private List<String> openApiEndpoints = List.of(

        // authorization and authentication
        "POST /auth/register",
        "POST /auth/login",

        // observability
        "GET /auth/actuator/prometheus",
        "GET /account/actuator/prometheus",
        "GET /product/actuator/prometheus",
        "GET /order/actuator/prometheus"
        
    );

    public Predicate<ServerHttpRequest> isSecured =
        request -> openApiEndpoints
            .stream()
            .noneMatch(uri -> {
                String[] parts = uri.replaceAll("[^a-zA-Z0-9// *]", "").split(" ");
                final String method = parts[0];
                final String path = parts[1];
                final boolean deep = path.endsWith("/**");
                return ("ANY".equalsIgnoreCase(method) || request.getMethod().toString().equalsIgnoreCase(method))
                    && (request.getURI().getPath().equals(path) || (deep && request.getURI().getPath().startsWith(path.replace("/**", ""))));
            });

}
