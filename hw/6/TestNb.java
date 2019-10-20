
public class TestNb {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Nb nb  = new Nb();
		nb.readFileAndClassify("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 4\\weathernon.csv");
		nb.displayResults();
		System.out.println();
		
		Nb nb2 = new Nb();
		nb2.readFileAndClassify("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 4\\diabetes.csv");
		nb2.displayResults();
		
	}

}
