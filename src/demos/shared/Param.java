package demos.shared;

public class Param<T> {

	int paramId;
	String paramName;
	T paramValue;
	
	public Param(int paramId, String paramName, T paramValue ) {
		this.paramId = paramId;
		this.paramName = paramName;
		this.paramValue = paramValue;
	}

	public int getParamId() {
		return paramId;
	}

	public String getParamName() {
		return paramName;
	}

	public T getParamValue() {
		return paramValue;
	}

	public void setParamId(int paramId) {
		this.paramId = paramId;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public void setParamValue(T paramValue) {
		this.paramValue = paramValue;
	}
	
	
}
