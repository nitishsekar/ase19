
public class TestZeroR {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ZeroR zeroR = new ZeroR();
		zeroR.readFileAndClassify("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 4\\weathernon.csv");
		zeroR.displayResults();
		System.out.println();
		ZeroR zeroR2 = new ZeroR();
		zeroR2.readFileAndClassify("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 4\\diabetes.csv");
		zeroR2.displayResults();
		
	}

}
