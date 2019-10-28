import java.util.Random;

public class TestRP {

	public static void main(String[] args) {
		Tbl tbl = new Tbl();
		//tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 7\\xomo10000.csv");
		tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 7\\pom310000.csv");
		RPTreeGenerator rpTG = new RPTreeGenerator();
		RPTree node = rpTG.generateRPTree(tbl);
		node.printTree(node);
	}

}
