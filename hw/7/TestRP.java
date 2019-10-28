import java.util.Random;

public class TestRP {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Tbl tbl = new Tbl();
		//tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 7\\test.csv");
		tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 7\\xomo10000.csv");
		//tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 7\\pom310000.csv");
		
		//tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 6\\diabetes.csv");
		//tbl.dump();
		RPTreeGenerator rpTG = new RPTreeGenerator();
		rpTG.generateRPTree(tbl);
	}

}
