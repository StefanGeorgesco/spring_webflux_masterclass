package fr.stefangeorgesco.spring_webflux_masterclass.sec06.validator;

import fr.stefangeorgesco.spring_webflux_masterclass.sec06.dto.CustomerDto;
import fr.stefangeorgesco.spring_webflux_masterclass.sec06.exception.ApplicationExceptions;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class RequestValidator {

    private static final String EMAIL_REGEX =
            "[a-zA-Z0-9]+([._%+\\-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([.\\-][a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}$";

    private RequestValidator() {
    }

    public static UnaryOperator<Mono<CustomerDto>> validate() {
        return mono -> mono
                .filter(hasName())
                .switchIfEmpty(ApplicationExceptions.missingName())
                .filter(hasValidEmail())
                .switchIfEmpty(ApplicationExceptions.missingValidEmail());
    }

    private static Predicate<CustomerDto> hasName() {
        return customerDto -> Objects.nonNull(customerDto.name()) && !customerDto.name().isBlank();
    }

    private static Predicate<CustomerDto> hasValidEmail() {
        return customerDto -> {
            String customerEmail = customerDto.email();
            return Objects.nonNull(customerEmail) &&
                    Pattern.compile(EMAIL_REGEX)
                            .matcher(customerEmail)
                            .matches();
        };
    }
}
