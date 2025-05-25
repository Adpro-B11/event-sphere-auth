package id.ac.ui.cs.advprog.eventsphereauth.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MonitoringConfig {

    private final MeterRegistry meterRegistry;

    @Bean
    public Counter userRegistrationCounter() {
        return Counter.builder("auth.user.registrations")
                .description("Number of user registrations")
                .tag("service", "auth")
                .register(meterRegistry);
    }

    @Bean
    public Counter userLoginCounter() {
        return Counter.builder("auth.user.logins")
                .description("Number of user logins")
                .tag("service", "auth")
                .register(meterRegistry);
    }

    @Bean
    public Counter userLoginFailuresCounter() {
        return Counter.builder("auth.user.login.failures")
                .description("Number of failed login attempts")
                .tag("service", "auth")
                .register(meterRegistry);
    }

    @Bean
    public Counter jwtTokenGenerationCounter() {
        return Counter.builder("auth.jwt.tokens.generated")
                .description("Number of JWT tokens generated")
                .tag("service", "auth")
                .register(meterRegistry);
    }

    @Bean
    public Timer authenticationTimer() {
        return Timer.builder("auth.authentication.duration")
                .description("Authentication request duration")
                .tag("service", "auth")
                .register(meterRegistry);
    }

    @Bean
    public Timer databaseOperationTimer() {
        return Timer.builder("auth.database.operation.duration")
                .description("Database operation duration")
                .tag("service", "auth")
                .register(meterRegistry);
    }

    @Bean
    public Counter adminOperationsCounter() {
        return Counter.builder("auth.admin.operations")
                .description("Number of admin operations")
                .tag("service", "auth")
                .register(meterRegistry);
    }

    @Bean
    public Counter balanceOperationsCounter() {
        return Counter.builder("auth.balance.operations")
                .description("Number of balance operations")
                .tag("service", "auth")
                .register(meterRegistry);
    }
}