package com.booking.common.infra.error;

public class InfrastructureException extends RuntimeException {

	public InfrastructureException(Throwable error) {
		super(error);
	}

	public InfrastructureException() {
	}

}
