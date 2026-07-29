package fr.stefangeorgesco.spring_webflux_masterclass.sec06.filter;

import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebFilterUtil {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Pattern AUTHORIZATION_PATTERN = Pattern.compile("^Bearer (\\S+)$");

    private static final Map<String, Category> TOKEN_CATEGORY_MAP = Map.of(
            "secret123", Category.STANDARD,
            "secret456", Category.PRIME
    );

    private WebFilterUtil() {
    }

    public static Category getCategory(ServerRequest request) {
        String token = getToken(request);
        if (Objects.isNull(token)) {
            return null;
        }
        return TOKEN_CATEGORY_MAP.get(token);
    }

    public static Mono<ServerResponse> prime(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return next.handle(request);
    }

    public static Mono<ServerResponse> standard(ServerRequest request, HandlerFunction<ServerResponse> next) {
        boolean isGetMethod = HttpMethod.GET.equals(request.method());
        if (isGetMethod) {
            return next.handle(request);
        }
        return ServerResponse.status(403).build();
    }

    private static String getToken(ServerRequest request) {
        String authHeader = request.headers().firstHeader(AUTHORIZATION_HEADER);
        if (Objects.isNull(authHeader)) {
            return null;
        }
        Matcher matcher = AUTHORIZATION_PATTERN.matcher(authHeader);
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }
}
