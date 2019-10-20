
public class AbcdAldaTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Abcd abcd = new Abcd();
		abcd.classify("yes", "no");
		for(int i=0; i<10; i++) abcd.classify("yes", "yes");
		for(int i=0; i<2; i++) abcd.classify("no", "yes");
		for(int i=0; i<2; i++) abcd.classify("no", "no");
		
		abcd.abcdReport();
	}

}
