package io.github.cepr0.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Component
public class RetryService {

	@Retryable(
			value = IllegalArgumentException.class,
			maxAttempts = 2,
			listeners = "listener"
	)
	public Optional<String> toUpperCase(String text) {
		if (text == null) {
			throw new IllegalArgumentException("Text must not be null");
		}

		if (StringUtils.hasText(text)) {
			return Optional.of(text.toUpperCase());
		} else {
			return Optional.empty();
		}
	}

	@Recover
	public Optional<String> toUpperCase(IllegalArgumentException e, String text) {
		log.warn("[w] Recovered due to {}", e.toString());
		return Optional.empty();
	}
}
