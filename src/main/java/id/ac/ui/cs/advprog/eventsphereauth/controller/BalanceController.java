package id.ac.ui.cs.advprog.eventsphereauth.controller;

import id.ac.ui.cs.advprog.eventsphereauth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final UserService userService;

    @PostMapping("/{id}/add")
    public ResponseEntity<Void> addBalance(
            @PathVariable UUID id,
            @RequestBody Map<String, Double> payload   // ← ubah ke Double
    ) {
        Double amt = payload.get("amount");
        BigDecimal amount = BigDecimal.valueOf(amt);
        log.info("⏩ addBalance called for userId={} amount={}", id, amount);

        userService.addBalance(id.toString(), amount);

        BigDecimal newBal = userService.getBalance(id.toString());
        log.info("✅ addBalance succeeded for userId={} newBalance={}", id, newBal);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deduct")
    public ResponseEntity<Void> deductBalance(
            @PathVariable UUID id,
            @RequestBody Map<String, Double> payload   // ← ubah ke Double
    ) {
        Double amt = payload.get("amount");
        BigDecimal amount = BigDecimal.valueOf(amt);
        log.info("⏩ deductBalance called for userId={} amount={}", id, amount);

        userService.deductBalance(id.toString(), amount);

        BigDecimal newBal = userService.getBalance(id.toString());
        log.info("✅ deductBalance succeeded for userId={} newBalance={}", id, newBal);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable UUID id) {
        log.info("⏩ getBalance called for userId={}", id);

        BigDecimal balance = userService.getBalance(id.toString());

        log.info("✅ getBalance returned {} for userId={}", balance, id);
        return ResponseEntity.ok(balance);
    }
}
