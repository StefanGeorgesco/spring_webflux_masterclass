package fr.stefangeorgesco.spring_webflux_masterclass.sec07.dto;

public record CalculatorResponse(int first, int second, String operation, double result) {
}
