package com.solveda.catalogueservice.model;

import java.util.Arrays;

/**
 * Represents supported currencies in the Catalogue Service.
 * <p>
 * This enum defines a fixed set of currency values used for product pricing
 * and related monetary operations.
 * <p>
 * Each currency is represented using its ISO 4217 currency code
 * (e.g., INR, USD, EUR, GBP).
 *
 * <h3>Purpose</h3>
 * <ul>
 *   <li>Prevents invalid currency values in the application</li>
 *   <li>Ensures consistent currency handling across APIs and persistence</li>
 *   <li>Provides a safe conversion utility from raw string inputs</li>
 * </ul>
 *
 * <h3>Example Usage</h3>
 * <pre>{@code
 * Currency currency = Currency.fromCode("usd");  // returns Currency.USD
 * System.out.println(currency.getCode());        // prints "USD"
 * }</pre>
 */
public enum Currency {

    /** Indian Rupee (ISO 4217 Code: INR). */
    INR("INR"),

    /** United States Dollar (ISO 4217 Code: USD). */
    USD("USD"),

    /** Euro (ISO 4217 Code: EUR). */
    EUR("EUR"),

    /** British Pound Sterling (ISO 4217 Code: GBP). */
    GBP("GBP");

    /**
     * ISO 4217 currency code representation.
     */
    private final String code;

    /**
     * Creates a currency constant with the given ISO currency code.
     *
     * @param code ISO 4217 currency code
     */
    Currency(String code) {
        this.code = code;
    }

    /**
     * Returns the ISO currency code.
     *
     * @return currency code as a string (example: "INR", "USD")
     */
    public String getCode() {
        return code;
    }

    /**
     * Converts a string currency code into a {@link Currency} enum constant.
     * <p>
     * This method is case-insensitive and trims spaces.
     *
     * @param code currency code to convert (example: "USD", "INR")
     * @return matching {@link Currency} enum value
     *
     * @throws IllegalArgumentException if code is null, empty, or invalid
     */
    public static Currency fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be null or empty");
        }

        return Arrays.stream(values())
                .filter(currency -> currency.code.equalsIgnoreCase(code.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid currency code: " + code));
    }
}
