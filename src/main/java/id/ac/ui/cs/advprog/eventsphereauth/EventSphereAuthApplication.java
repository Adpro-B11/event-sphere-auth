package id.ac.ui.cs.advprog.eventsphereauth;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventSphereAuthApplication {

    public static void main(String[] args) {
        // ── Load .env ────────────────────────────────────────────────
        Dotenv dotenv = Dotenv.load();

        // 2) JWT
        System.setProperty("jwt.secret-key", dotenv.get("JWT_SECRET_KEY"));
        System.setProperty("jwt.expiration", dotenv.get("JWT_EXPIRATION"));
        // 3) Admin user
        System.setProperty("app.admin.email",    dotenv.get("APP_ADMIN_EMAIL"));
        System.setProperty("app.admin.password", dotenv.get("APP_ADMIN_PASSWORD"));
        // ────────────────────────────────────────────────────────────────

        SpringApplication.run(EventSphereAuthApplication.class, args);
    }
}
