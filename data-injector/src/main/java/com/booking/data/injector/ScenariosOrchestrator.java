package com.booking.data.injector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ScenariosOrchestrator {

	private final int runTimesPerScenario = 50;
	private final int clientsPerScenario = 150;

	private final RestTemplate restTemplate = new RestTemplate();
	private final SecureRandom secureRandom = new SecureRandom();
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final List<Future<?>> fs = new CopyOnWriteArrayList<>();
	private final CountDownLatch finishedFiring = new CountDownLatch(1);

	private final Scenario[] scenarios;

	private final ExecutorService workersPool;

	public ScenariosOrchestrator() {
		scenarios = new Scenario[] {
				new CreateBookingClient(restTemplate, secureRandom, objectMapper),
				new DeleteBookingClient(restTemplate, secureRandom, objectMapper),
				new EditBookingClient(restTemplate, secureRandom, objectMapper)
		};

		workersPool = Executors.newFixedThreadPool(scenarios.length * clientsPerScenario);

		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
	}

	public void shutdown() {
		workersPool.shutdown();
	}

	public void run() {

		for (Scenario scenario : scenarios) {

			List<Runnable> tasks = new ArrayList<>(clientsPerScenario);
			for (int i = 1; i <= clientsPerScenario; i++) {

				Runnable task = () -> {
					for (int k = 1; k <= runTimesPerScenario; k++) {
						scenario.execute();
					}
				};

				tasks.add(task);
			}

			for (Runnable task : tasks) {
				Future<?> f = workersPool.submit(task);
				fs.add(f);
			}

		}

		finishedFiring.countDown();

	}

	public List<Future<?>> getFs() {
		return fs;
	}

	public CountDownLatch getFinishedFiring() {
		return finishedFiring;
	}
}
