package com.booking.consumer.error;

/**
 *
 * Note: this error can happen for example:
 *
 * 	[1] we submit a create booking message from producer
 * 	[2] consumer has not processed it yet, so no record in the database
 * 	[3] we submit an edit booking message from producer
 * 	[4] so consumer tries to find the record, but has not create one yet due to [2]
 *
 */
public class RecordNotExistsException extends RuntimeException {

	public RecordNotExistsException(String id, String entityType) {
		super("id --> " + id + ", entity-type --> " + entityType);
	}

}
