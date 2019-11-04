import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestRP {

	public static void main(String[] args) {
		Tbl tbl = new Tbl();
		//tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 7\\xomo10000.csv");
		//tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 7\\pom310000.csv");
		tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 8\\auto.csv");
		RPTreeGenerator rpTG = new RPTreeGenerator();
		RPTree node = rpTG.generateRPTree(tbl);
		node.printTree(node);
		List<RPTree> leaves = rpTG.getLeaves(node);
		//System.out.println("\n"+leaves.size());
		System.out.println();
		System.out.println();
		
		leaves = rpTG.getEnviousClusters(leaves);
		for(int k = 0; k<leaves.size(); k++) {
			RPTree leaf0 = leaves.get(k);
			RPTree leafEnvy = leaf0.getEnvy();
			System.out.print("Leaf "+k+" envy stats: ");
			if(leafEnvy != null) {
				for(Col c:leafEnvy.getLeafStats()) {
					if(c.getClass() == Num.class) {
						Num n = (Num) c;
						System.out.print(n.getMean()+" ");
						
					}
				}
			} else {
				System.out.print("Best Cluster: ");
				for(Col c:leaf0.getLeafStats()) {
					if(c.getClass() == Num.class) {
						Num n = (Num) c;
						System.out.print(n.getMean()+" ");
						
					}
				}
			}
			System.out.println();
		}
	}

}
