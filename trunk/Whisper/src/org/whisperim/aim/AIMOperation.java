package org.whisperim.aim;

public class AIMOperation {
	
	public final static int SIGNIN = 1;
	public final static int SIGNOUT = 2;
	public final static int SEND_MESSAGE = 3;
	
	Integer operation;
	Object[] arguments;
	
	public Integer getOperation() {
		return operation;
	}
	public void setOperation(Integer operation) {
		this.operation = operation;
	}
	public Object[] getArguments() {
		return arguments;
	}
	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}
	
	private AIMOperation() {
		super();
	}
	
	public static AIMOperation createSignIn(String handle, String password) {
		AIMOperation op = new AIMOperation();
		op.operation = SIGNIN;
		Object[] args = { handle, password }; 
		op.arguments = args;
		return op;
	}

	public static AIMOperation createSignOut() {
		AIMOperation op = new AIMOperation();
		op.operation = SIGNOUT;
		op.arguments = new Object[0];
		return op;
	}
	
	public static AIMOperation createSendMessage(String handle, String message) {
		AIMOperation op = new AIMOperation();
		op.operation = SEND_MESSAGE;
		Object[] args = { handle, message }; 
		op.arguments = args;
		return op;
	}


}

