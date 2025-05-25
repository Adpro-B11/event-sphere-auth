package id.ac.ui.cs.advprog.eventsphereauth.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final Counter userRegistrationCounter;
    private final Counter userLoginCounter;
    private final Counter userLoginFailuresCounter;
    private final Counter jwtTokenGenerationCounter;
    private final Timer authenticationTimer;
    private final Timer databaseOperationTimer;
    private final Counter adminOperationsCounter;
    private final Counter balanceOperationsCounter;

    public void incrementUserRegistrations() {
        userRegistrationCounter.increment();
    }

    public void incrementUserLogins() {
        userLoginCounter.increment();
    }

    public void incrementLoginFailures() {
        userLoginFailuresCounter.increment();
    }

    public void incrementJwtTokenGeneration() {
        jwtTokenGenerationCounter.increment();
    }

    public void incrementAdminOperations() {
        adminOperationsCounter.increment();
    }

    public void incrementBalanceOperations() {
        balanceOperationsCounter.increment();
    }

    public <T> T timeAuthentication(Supplier<T> operation) throws Exception {
        return authenticationTimer.recordCallable(operation::get);
    }

    public <T> T timeDatabaseOperation(Supplier<T> operation) throws Exception {
        return databaseOperationTimer.recordCallable(operation::get);
    }

    public void recordAuthenticationTime(Duration duration) {
        authenticationTimer.record(duration);
    }

    public void recordDatabaseOperationTime(Duration duration) {
        databaseOperationTimer.record(duration);
    }
}                                       