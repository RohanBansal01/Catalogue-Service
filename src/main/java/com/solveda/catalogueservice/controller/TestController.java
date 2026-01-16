package com.solveda.catalogueservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The {@code TestController} class provides a simple endpoint
 * to verify that the Catalogue Service API is running and accessible.
 * <p>
 * This controller is typically used for health checks or connectivity tests.
 * It exposes a basic {@code /api/ping} endpoint that returns a "pong" response.
 * </p>
 *
 * <p><b>Example Request:</b></p>
 * <pre>
 * GET /api/ping
 * </pre>
 *
 * <p><b>Example Response:</b></p>
 * <pre>
 * pong
 * </pre>
 * 
 */
@RestController
@RequestMapping("/api")
public class TestController {

    /**
     * Responds with a simple "pong" message to confirm that the service is active.
     *
     * @return a {@code String} containing the message {@code "pong"}.
     */
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
