package testcase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException, Exception {
		String fn = "testcases.txt";
		BufferedReader br = new BufferedReader(new FileReader(fn));
		String s;
		String q[]=new String[3];
		int i, j, k, r[] = { 0, 0 },line;
		int li[]=new int[3];
		Case t=new Case();
		i = 0;
		line = 1;
		while ((s = br.readLine()) != null) {
			if (s.isEmpty()) {
				line++;
				continue;
			}
			q[i % 3] = s;
			li[i%3]=line;
			if (i % 3 == 2) {
//				System.out.println(q[0]+" "+Thread.currentThread().getStackTrace()[1]);
				t.set(q, li);
				r[t.test()?1:0]++;
			}
			i++;
			line++;
		}
		br.close();

		j = r[0] + r[1];
		k = (int) (Math.log10(j) + 1);
		for (i = 1; i >= 0; i--) {
			System.out.printf("%-5s %"+k+"d/%d=%5.1f%%\n", i != 0 ? "ok" : "error", r[i], j,
					r[i] * 100. / j);
		}
	}


}
