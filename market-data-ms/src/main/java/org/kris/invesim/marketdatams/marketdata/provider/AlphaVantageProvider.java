package org.kris.invesim.marketdatams.marketdata.provider;

import lombok.extern.slf4j.Slf4j;
import org.kris.invesim.marketdatams.stock.domain.DailyPrice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class AlphaVantageProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${alphavantage.api-key}")
    private String alphaKey;

    @Value("${alphavantage.url}")
    private String alphaUrl;

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    public Optional<List<DailyPrice>> fetchDailyPrices(String symbol) {
        String url = buildUrl(symbol);
        log.info("Fetching data from AlphaVantage: {}", url);

        Optional<String> response = fetchWithRetry(url, MAX_RETRIES, RETRY_DELAY_MS);
        if (response.isEmpty()) return Optional.empty();

        try {
            return Optional.of(AlphaVantageMapper.mapFromAlphaVantageJson(response.get()));
        } catch (Exception e) {
            log.error("Failed to map AlphaVantage response for {}: {}", symbol, e.getMessage(), e);
            return Optional.empty();
        }
    }

    private String buildUrl(String symbol) {
        return UriComponentsBuilder.fromHttpUrl(alphaUrl)
                .queryParam("function", "TIME_SERIES_DAILY")
                .queryParam("symbol", symbol)
                .queryParam("outputsize", "full")
                .queryParam("apikey", alphaKey)
                .toUriString();
    }

    private Optional<String> fetchWithRetry(String url, int maxAttempts, long delayMillis) {
        int attempt = 0;
        while (attempt < maxAttempts) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                if (response.getBody() != null) {
                    return Optional.of(response.getBody());
                } else {
                    log.warn("Empty response on attempt {}/{}", attempt + 1, maxAttempts);
                }
            } catch (Exception e) {
                log.error("Attempt {}/{} failed: {}", attempt + 1, maxAttempts, e.getMessage());
            }
            attempt++;
            try { Thread.sleep(delayMillis); } catch (InterruptedException ignored) {}
        }
        return Optional.empty();
    }
}
