package testcase;

public class Result {

	enum ErrorCode {
		OK, COMPILE_ERROR, CALCULATE_ERROR
	};

	ErrorCode errorCode;
	double value;

	void set(ErrorCode code) {
		set(code, 0);
	}

	void set(ErrorCode code, double v) {
		errorCode = code;
		value = v;
	}

	void set(String s, int line) {
		try {
			errorCode = ErrorCode.valueOf(s.toUpperCase());
		} catch (IllegalArgumentException ex) {
			errorCode = ErrorCode.OK;
			value = s.equals("nan") ? Double.NaN : Double.parseDouble(s);
		}
	}

	public boolean equals(Result a) {
		if (errorCode == ErrorCode.OK && a.errorCode == ErrorCode.OK) {
			return Math.abs(value - a.value) < 9e-16 || Double.isNaN(value) && Double.isNaN(a.value);
		} else {
			return a.errorCode == errorCode;
		}
	}

	public String toString() {
		if (errorCode == ErrorCode.OK) {
			return value + "";
		} else {
			return errorCode + "";
		}
	}

}
