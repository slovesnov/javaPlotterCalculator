package testcase;

public class Action {
	enum ActionEnum{
		STATIC_COMPILE,
		COMPILE_CALCULATE
	};
	
	ActionEnum action;
	void set(String s,int line) throws Exception {
		action=ActionEnum.valueOf(s.toUpperCase());
	}
	
	public String toString(){
		return action.toString();		
	}
	
}
