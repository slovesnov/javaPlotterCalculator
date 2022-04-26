package testcase;

public class Data {
	String compile;
	String variables[];
	double values[];
	
	void set(String s,int line) throws Exception {
		int p=s.indexOf('"');
		if(p==-1) {
			throw new Exception();
		}
		p++;
		//assert(s.length()>p);		
		int p1=s.indexOf('"',p);
		if(p1==-1) {
			throw new Exception();
		}
		compile=s.substring(p, p1);
//		System.out.println(p+"#"+p1);
//		System.out.println(compile+"#"+s+Thread.currentThread().getStackTrace()[1]);

		variables=new String[0];
		values=new double[0];

		p=s.indexOf('"',p1+1);
		if(p==-1){
			return;
		}
		p++;
		if(s.length()<=p) {
			throw new Exception();
		}
		p1=s.indexOf('"',p);
		variables=s.substring(p, p1).split("\\s+");

		p=s.indexOf('"',p1+1);
		if(p==-1) {
			throw new Exception();
		}
		p++;
		if(s.length()<=p) {
			throw new Exception();
		}
		p1=s.indexOf('"',p);
		String a[]=s.substring(p, p1).split("\\s+");
		values=new double[a.length];
		p=0;
		for(String v:a){
			values[p]=Double.parseDouble(v);
			p++;
		}
		
	}
	
	public String toString() {
		int i=0;
		String s="compile="+compile;
		if(variables.length!=0){
			s+=" variables="+String.join(" ",variables)+" values=";
			for(double a:values){
				if(i!=0){
					s+=' ';
				}
				s+=a;
				i++;
			}
		}
		return s;
		
	}

}
