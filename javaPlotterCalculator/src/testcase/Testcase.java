package testcase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Testcase {

	public static void main(String[] args) throws IOException, Exception {
		String fn = "testcases.txt";
		BufferedReader br = new BufferedReader(new FileReader(fn));
		String s;
		String q[] = new String[3];
		int i, j, k, r[] = { 0, 0 }, line;
		int li[] = new int[3];
		Case t = new Case();
		i = 0;
		line = 0;
		while ((s = br.readLine()) != null) {
			line++;
			if (s.isEmpty()) {
				continue;
			}
			q[i] = s;
			li[i] = line;
			if (i == 2) {
				t.set(q, li);
				r[t.test() ? 1 : 0]++;
				i = 0;
			} else {
				i++;
			}
		}
		br.close();

		j = r[0] + r[1];
		k = (int) (Math.log10(j) + 1);
		for (i = 1; i >= 0; i--) {
			System.out.printf("%-5s %" + k + "d/%d=%5.1f%%\n", i != 0 ? "ok" : "error", r[i], j, r[i] * 100. / j);
		}
	}

}
