package io.github.cepr0.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.listener.RetryListenerSupport;

import java.util.Optional;

@Slf4j
@EnableRetry
@SpringBootApplication
public class Application {

	private final RetryService service;

	public Application(RetryService service) {
		this.service = service;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onReady() {
		printResult(service.toUpperCase("text"));
		printResult(service.toUpperCase(null));
	}

	private void printResult(Optional<String> result) {
		result.ifPresentOrElse(
				value -> System.out.println("Result: " + value),
				() -> System.out.println("Result is empty")
		);
	}

	@Configuration
	public static class RetryConfig {

		/**
		 * Retry Listener bean must be instantiated in a separate configuration, otherwise {@link Retryable} does not work
		 */
		@Bean
		public RetryListener listener() {
			return new RetryListenerSupport() {
				@Override
				public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
					log.info("[i] onError: {}", throwable.toString());
				}
			};
		}
	}
}
