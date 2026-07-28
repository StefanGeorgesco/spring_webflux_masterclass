package fr.stefangeorgesco.spring_webflux_masterclass.sec05.filter;

import org.springframework.web.server.ServerWebExchange;

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

    public static Category getCategory(ServerWebExchange exchange) {
        String token = getToken(exchange);
        if (Objects.isNull(token)) {
            return null;
        }
        return TOKEN_CATEGORY_MAP.get(token);
    }

    private static String getToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
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
