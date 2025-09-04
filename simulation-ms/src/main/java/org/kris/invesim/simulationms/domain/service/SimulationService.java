package org.kris.invesim.simulationms.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kris.invesim.*;
import org.kris.invesim.MarketPreparedCallback;
import org.kris.invesim.simulationms.api.dto.PortfolioPreparedCallback;
import org.kris.invesim.simulationms.api.dto.SimulationRequestDto;
import org.kris.invesim.simulationms.api.dto.SimulationRequestForPortfolioDto;
import org.kris.invesim.simulationms.domain.engine.MonteCarloEngine;
import org.kris.invesim.simulationms.domain.model.SimulationResult;
import org.kris.invesim.simulationms.domain.strategy.InvestmentStrategy;
import org.kris.invesim.simulationms.domain.strategy.StrategyFactory;
import org.kris.invesim.simulationms.infrastructure.client.*;
import org.kris.invesim.simulationms.infrastructure.producer.MarketDataRequestedEventProducer;
import org.kris.invesim.simulationms.infrastructure.producer.SimulationCompletedEventProducer;
import org.kris.invesim.simulationms.infrastructure.producer.SimulationPortfolioRequestedEventProducer;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationService {

    private final MarketDataClient marketDataClient;
    private final PortfolioClient portfolioClient;
    private final NotificationClient notificationClient;

    private final PortfolioAsyncClient portfolioAsyncClient;
    private final MarketAsyncClient marketAsyncClient;

    private final SimulationPortfolioRequestedEventProducer simulationPortfolioRequestedEventProducer;
    private final MarketDataRequestedEventProducer marketDataRequestedProducer;
    private final SimulationCompletedEventProducer simulationCompletedProducer;

    public SimulationResult run(SimulationRequestDto req, Jwt jwt, UUID simulationId) {
        log.info("Simulation [{}] started (HTTP sync, manual).", simulationId);

        if (req.getMarket().getSymbols() == null || req.getMarket().getSymbols().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Brak symboli w market.symbols");
        }
        if (req.getEngine().getSteps() <= 0 || req.getEngine().getSimulations() <= 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "engine.steps i engine.simulations muszą być > 0");
        }

        final String symbol = req.getMarket().getSymbols().getFirst();
        final String bar    = Optional.ofNullable(req.getMarket().getBar()).orElse("1d");

        log.info("Fetch market data for simulation [{}] and symbol [{}].", simulationId, symbol);
        StockDataDto stock = marketDataClient.fetchHistory("Bearer " + jwt.getTokenValue(), symbol);

        InvestmentStrategyType type = req.getStrategy().getType();
        Map<String, Object> params  = Optional.ofNullable(req.getStrategy().getParams()).orElseGet(Map::of);
        InvestmentStrategy strategy = StrategyFactory.create(type, req.getMarket().getSymbols(), params);

        double initialCash = req.getPortfolio().getCash();
        double initialQty  = req.getPortfolio().getPositions() == null ? 0.0 :
                req.getPortfolio().getPositions().stream()
                        .filter(p -> symbol.equalsIgnoreCase(p.getSymbol()))
                        .mapToDouble(PositionDto::getQuantity)
                        .sum();

        int simulations = req.getEngine().getSimulations();
        int steps       = req.getEngine().getSteps();
        long seed       = req.getEngine().getSeed();

        MonteCarloEngine.Result res = new MonteCarloEngine().runSingleAsset(
                stock, strategy, simulations, steps, initialCash, initialQty, symbol, seed, bar
        );

        double[] finals = res.finals;
        Arrays.sort(finals);
        double expected = Arrays.stream(finals).average().orElse(0.0);
        double p5  = percentile(finals, 5);
        double p50 = percentile(finals, 50);
        double p95 = percentile(finals, 95);

        double var95ValueScale  = expected - p5;
        double cvar95ValueScale = cvar(finals, 0.05);

        double invested = req.getPortfolio().getCash();
        double var95Loss  = Math.max(0.0, invested - p5);
        double cvar95Loss = invested - cvar(finals, 0.05);

        double expectedReturnPct = pct(expected, invested);
        double p5ReturnPct       = pct(p5, invested);
        double p50ReturnPct      = pct(p50, invested);
        double p95ReturnPct      = pct(p95, invested);

        SimulationResult result = new SimulationResult(
                simulationId,
                type,
                expected, p5, p50, p95,
                var95ValueScale, cvar95ValueScale,
                res.sample,
                var95Loss, cvar95Loss,
                expectedReturnPct, p5ReturnPct, p50ReturnPct, p95ReturnPct,
                res.navSample
        );

        String bearer = "Bearer " + jwt.getTokenValue();
        SimulationResultDto resp = SimulationResultDto.builder()
                .simulationId(result.getRunId())
                .strategyType(result.getStrategyType())
                .expectedFinalValue(result.getExpectedFinalValue())
                .p5(result.getP5())
                .p50(result.getP50())
                .p95(result.getP95())
                .var95Loss(result.getVar95Loss())
                .cvar95Loss(result.getCvar95Loss())
                .expectedReturnPct(result.getExpectedReturnPct())
                .p5ReturnPct(result.getP5ReturnPct())
                .p50ReturnPct(result.getP50ReturnPct())
                .p95ReturnPct(result.getP95ReturnPct())
                .navSample(result.getNavSample())
                .build();

        notificationClient.sendSimulationCompleted(bearer, resp);
        log.info("Simulation [{}] completed (HTTP sync, manual).", simulationId);
        return result;
    }

    public SimulationResult simulateForExistingPortfolioHttpSync(SimulationRequestForPortfolioDto req, Jwt jwt) {
        UUID simulationId = UUID.randomUUID();

        String bearer = "Bearer " + jwt.getTokenValue();
        log.info("Simulation [{}] started (HTTP sync, portfolio).", simulationId);

        notificationClient.sendSimulationStarted(bearer);
        PortfolioSpec portfolioSpec = portfolioClient.getPortfolioSpec(bearer);

        String symbol = portfolioSpec.getPositions().getFirst().getSymbol();

        SimulationRequestDto reqDto = SimulationRequestDto.builder()
                .engine(req.getEngine())
                .strategy(req.getStrategy())
                .portfolio(portfolioSpec)
                .market(new org.kris.invesim.MarketSpec(List.of(symbol), "1d"))
                .build();

        return run(reqDto, jwt, simulationId);
    }


    public UUID startManualEvent(SimulationRequestDto req, Jwt jwt) {
        UUID simId = UUID.randomUUID();
        String userId = jwt.getClaim("sub");

        String bar = Optional.ofNullable(req.getMarket().getBar()).orElse("1d");
        String symbol = req.getMarket().getSymbols().getFirst();

        log.info("Simulation [{}] started (EVENT, manual). Emitting MarketDataRequested...", simId);

        MarketDataRequestedEvent event = MarketDataRequestedEvent.builder()
                .simulationId(simId)
                .userId(UUID.fromString(userId))
                .email(jwt.getClaim("email"))
                .symbol(symbol)
                .bar(bar)
                .engine(req.getEngine())
                .strategy(req.getStrategy())
                .portfolio(req.getPortfolio())
                .build();

        marketDataRequestedProducer.sendMarketDataRequested(event);
        return simId;
    }


    public UUID startPortfolioEvent(SimulationRequestForPortfolioDto req, Jwt jwt) {
        UUID simId = UUID.randomUUID();
        String userId = jwt.getClaim("sub");

        log.info("Simulation [{}] started (EVENT, portfolio). Emitting SimulationRequested...", simId);


        SimulationPortfolioRequestedDto event = SimulationPortfolioRequestedDto.builder()
                .simulationId(simId)
                .userId(UUID.fromString(userId))
                .email(jwt.getClaim("email"))
                .strategyType(req.getStrategy().getType())
                .engine(req.getEngine())
                .bar("1d")
                .build();

        simulationPortfolioRequestedEventProducer.sendSimulationPortfolioRequestedEvent(event);
        return simId;
    }

    public SimulationResultDto runFromFatEvent(UUID simulationId, MarketDataReadyEvent evt) {
        log.info("Simulation [{}] runFromFatEvent start for symbol={}", simulationId, evt.getSymbol());
        return runCore(
                simulationId,
                evt.getPortfolio(),
                evt.getStock(),
                evt.getEmail(),
                evt.getSymbol(),
                Optional.ofNullable(evt.getBar()).orElse("1d"),
                evt.getEngine(),
                evt.getStrategy()
        );
    }

    private SimulationResultDto runCore(UUID simulationId,
                                        PortfolioSpec portfolio,
                                        StockDataDto stock,
                                        String email,
                                        String symbol,
                                        String bar,
                                        EngineSpec engineSpec,
                                        StrategySpec strategySpec) {

        if (portfolio == null || stock == null || engineSpec == null || strategySpec == null) {
            throw new IllegalArgumentException("Brak wymaganych danych do symulacji (portfolio/stock/engine/strategy).");
        }

        InvestmentStrategyType type = strategySpec.getType();
        Map<String, Object> params  = Optional.ofNullable(strategySpec.getParams()).orElseGet(Map::of);
        InvestmentStrategy strategy = StrategyFactory.create(type, List.of(symbol), params);

        double initialCash = portfolio.getCash();
        double initialQty  = portfolio.getPositions() == null ? 0.0 :
                portfolio.getPositions().stream()
                        .filter(p -> symbol.equalsIgnoreCase(p.getSymbol()))
                        .mapToDouble(PositionDto::getQuantity)
                        .sum();

        int simulations = engineSpec.getSimulations();
        int steps       = engineSpec.getSteps();
        long seed       = engineSpec.getSeed();

        MonteCarloEngine.Result res = new MonteCarloEngine().runSingleAsset(
                stock, strategy, simulations, steps, initialCash, initialQty, symbol, seed, bar
        );

        double[] finals = res.finals;
        Arrays.sort(finals);
        double expected = Arrays.stream(finals).average().orElse(0.0);
        double p5  = percentile(finals, 5);
        double p50 = percentile(finals, 50);
        double p95 = percentile(finals, 95);

        double invested   = portfolio.getCash();
        double var95Loss  = Math.max(0.0, invested - p5);
        double cvarValue  = cvar(finals, 0.05);
        double cvar95Loss = invested - cvarValue;

        double expectedReturnPct = pct(expected, invested);
        double p5ReturnPct       = pct(p5, invested);
        double p50ReturnPct      = pct(p50, invested);
        double p95ReturnPct      = pct(p95, invested);

        return SimulationResultDto.builder()
                .simulationId(simulationId)
                .strategyType(type)
                .email(email)
                .expectedFinalValue(expected)
                .p5(p5)
                .p50(p50)
                .p95(p95)
                .var95Loss(var95Loss)
                .cvar95Loss(cvar95Loss)
                .expectedReturnPct(expectedReturnPct)
                .p5ReturnPct(p5ReturnPct)
                .p50ReturnPct(p50ReturnPct)
                .p95ReturnPct(p95ReturnPct)
                .navSample(res.navSample)
                .build();
    }

    private double percentile(double[] sortedAsc, double p) {
        if (sortedAsc.length == 0) return 0.0;
        double rank = (p / 100.0) * (sortedAsc.length - 1);
        int lo = (int) Math.floor(rank), hi = (int) Math.ceil(rank);
        if (lo == hi) return sortedAsc[lo];
        double w = rank - lo;
        return sortedAsc[lo] * (1 - w) + sortedAsc[hi] * w;
    }

    private double cvar(double[] sortedAsc, double alpha) {
        int k = Math.max(1, (int) Math.floor(alpha * sortedAsc.length));
        double sum = 0;
        for (int i = 0; i < k; i++) sum += sortedAsc[i];
        return sum / k;
    }

    private double pct(double value, double base) {
        if (base == 0.0) return 0.0;
        return (value / base) - 1.0;
    }
}
