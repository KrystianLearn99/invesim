package org.kris.invesim.portfolioms.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kris.invesim.PortfolioSpec;
import org.kris.invesim.portfolioms.portfolio.service.PortfolioService;
import org.kris.invesim.portfolioms.portfolio.service.ValuationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final ValuationService valuationService;

    @PostMapping
    public ResponseEntity<IdResponse> create(@Valid @RequestBody CreatePortfolioRequest req,
                                             @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        UUID id = portfolioService.createPortfolio(userId, req.initialCash());
        return ResponseEntity.ok(new IdResponse(id));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@Valid @RequestBody CashRequest req,
                                        @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        portfolioService.deposit(userId, req.amount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@Valid @RequestBody CashRequest req,
                                         @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        portfolioService.withdraw(userId, req.amount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/buy")
    public ResponseEntity<Void> buy(@Valid @RequestBody TradeRequest req,
                                    @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        portfolioService.buy(userId, req.symbol(), req.quantity(), req.price());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sell")
    public ResponseEntity<Void> sell(@Valid @RequestBody TradeRequest req,
                                     @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        portfolioService.sell(userId, req.symbol(), req.quantity(), req.price());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/valuation")
    public ResponseEntity<?> valuation(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(valuationService.nav(userId));
    }

    @GetMapping
    public ResponseEntity<PortfolioSpec> getPortfolioSpec(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(portfolioService.getPortfolioSpec(userId));
    }
}
