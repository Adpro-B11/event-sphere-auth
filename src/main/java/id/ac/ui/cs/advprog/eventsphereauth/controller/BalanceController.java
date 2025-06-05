package id.ac.ui.cs.advprog.eventsphereauth.controller;

import id.ac.ui.cs.advprog.eventsphereauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final UserService userService;

    @PostMapping("/{id}/add")
    public ResponseEntity<Void> addBalance(
            @PathVariable UUID id,
            @RequestBody Map<String, Double> payload
    ) {
        Double amt = payload.get("amount");
        BigDecimal amount = BigDecimal.valueOf(amt);

        userService.addBalance(id.toString(), amount);
        userService.getBalance(id.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deduct")
    public ResponseEntity<Void> deductBalance(
            @PathVariable UUID id,
            @RequestBody Map<String, Double> payload
    ) {
        Double amt = payload.get("amount");
        BigDecimal amount = BigDecimal.valueOf(amt);

        userService.deductBalance(id.toString(), amount);
        userService.getBalance(id.toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID id) {
        BigDecimal balance = userService.getBalance(id.toString());
        return ResponseEntity.ok(balance);
    }
}
