package systems.rajshah.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("HeathEndPointMain")
public class HealthEndpoint implements HealthIndicator{
	@Override
	@GetMapping("/health")
	public Health health() {
		return Health.up().build();
	}
}
