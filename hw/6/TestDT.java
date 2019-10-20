
public class TestDT {

	public static void main(String[] args) {
		Tbl tbl = new Tbl();
		tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 6\\diabetesTest.csv");
		tbl.dump();
		System.out.println("==============================================");
		DecisionTreeGenerator dt = new DecisionTreeGenerator();
		dt.createDecisionTree(tbl);
	}

}
