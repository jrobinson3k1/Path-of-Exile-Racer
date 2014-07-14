package com.jasonrobinson.racer.enumeration;

public enum ErrorCode {

	RESOURCE_NOT_FOUND(1),
	INVALID_QUERY(2),
	RATE_LIMIT_EXCEEDED(3),
	INTERNAL_ERROR(4),
	UNEXPECTED_CONTENT_TYPE(5),
	FORBIDDEN(6);

	private int code;

	ErrorCode(int code) {

		this.code = code;
	}

	public int getCode() {

		return code;
	}

	public static ErrorCode getForCode(int code) {

		ErrorCode[] errorCodes = values();
		for (ErrorCode errorCode : errorCodes) {
			if (errorCode.getCode() == code) {
				return errorCode;
			}
		}

		return null;
	}
}
