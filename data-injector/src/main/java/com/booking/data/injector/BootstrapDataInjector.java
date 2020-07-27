package com.booking.data.injector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class BootstrapDataInjector {

	private static final Logger LOG = LoggerFactory.getLogger(BootstrapDataInjector.class);


	public static void main(String[] args) throws Exception {

		ScenariosOrchestrator scenariosOrchestrator = new ScenariosOrchestrator();
		scenariosOrchestrator.run();


		scenariosOrchestrator.getFinishedFiring().await();

		scenariosOrchestrator.getFs().forEach(f -> {
			try {
				f.get();
			} catch (InterruptedException e) {
				LOG.error("error occurred", e);
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				LOG.error("error occurred", e);
				throw new RuntimeException(e);
			}
		});

		scenariosOrchestrator.shutdown();

	}

}
