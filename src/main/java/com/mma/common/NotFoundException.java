package com.mma.common;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -35664811196546088L;

	public NotFoundException(String message) {
		super(message);
	}

}
