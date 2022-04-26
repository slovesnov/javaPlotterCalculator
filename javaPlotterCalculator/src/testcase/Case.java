package testcase;

import estimator.ExpressionEstimator;

public class Case {
	Data data=new Data();
	Action action=new Action();
	Result result=new Result();
	ExpressionEstimator e=new ExpressionEstimator();
	
	void set(String s[],int lines[]) throws Exception {
		data.set(s[0],lines[0]);
		action.set(s[1],lines[1]);
		result.set(s[2],lines[2]);
	}
	
	boolean test() throws Exception {
		Result r=new Result();
		double v;
//		System.out.println(data+" "+action+" "+Thread.currentThread().getStackTrace()[1]);
		if(action.action==Action.ActionEnum.STATIC_COMPILE){
			try {
//				System.out.println(data.compile+"#");
				v = ExpressionEstimator.calculate(data.compile);
				r.set(Result.ErrorCode.OK, v);
			} catch (Exception ex) {
				r.set(Result.ErrorCode.COMPILE_ERROR);
			}
		}
		else if(action.action==Action.ActionEnum.COMPILE_CALCULATE){
			try {
				e.compile(data.compile,data.variables);
				try {
					v = e.calculate(data.values);
					r.set(Result.ErrorCode.OK, v);
				} catch (Exception ex1 ) {
					r.set(Result.ErrorCode.CALCULATE_ERROR);
				}
			} catch (Exception ex2) {
				r.set(Result.ErrorCode.COMPILE_ERROR);
			}
		}
		else{
			throw new Exception();
		}

		boolean b=r.equals(result);
		if(!b){
			//r what we got here,result is data from file
			System.out.println(data+" "+r+" file "+result);
		}
		return b;
	}

}
