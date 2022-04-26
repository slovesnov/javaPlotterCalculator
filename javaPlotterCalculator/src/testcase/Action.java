package testcase;

public class Action {
	enum ActionEnum{
		STATIC_COMPILE,
		COMPILE_CALCULATE
	};
	
	ActionEnum action;
	void set(String s,int line) throws Exception {
		if(s.equals("scompile")){
			action=ActionEnum.STATIC_COMPILE;
		}
		else if(s.equals("compile_calculate")){
			action=ActionEnum.COMPILE_CALCULATE;
		}
		else{
			throw new Exception("invalid string "+s+" at line"+line);
		}		
	}
	
	public String toString(){
		return action.toString();		
	}
	
}
