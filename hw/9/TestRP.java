import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestRP {

	public static void main(String[] args) {
		Tbl tbl = new Tbl();
		//tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 7\\xomo10000.csv");
		//tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 7\\pom310000.csv");
		tbl.read("C:\\Users\\satan\\OneDrive\\Desktop\\xomo10000.csv");
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
		}
		PrintWriter writer;
		String fileNameDT = "Trees.md";
		try {
			File file = new File(fileNameDT);
			if (file.exists()) {
				file.delete();
			}
			writer = new PrintWriter(file);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < leaves.size(); i++) {
				int index = i+1;
				String fileNameDataSet = "resultantDataSet_"+index+".csv";
				RPTree leaf = leaves.get(i);
				leaf.printClusterToFile(tbl,leaf,fileNameDataSet);
				Tbl table = new Tbl();
				String labelType = table.read(fileNameDataSet);
				DecisionTreeGenerator dt = new DecisionTreeGenerator();
				System.out.println("Decision Tree for "+fileNameDataSet);
//				String fileNameDT = "resultantDT_"+index+".md";
//				PrintWriter writer;
				sb.append("Decision Tree for "+fileNameDataSet);
				if (labelType.contains("Sym")) {
					dt.createDecisionTree(table,"Sym",sb);
				}
				if (labelType.contains("Num")) {
					dt.createDecisionTree(table,"Num",sb);
				}
				sb.append('\n');
			}
			writer.write(sb.toString());
			writer.flush();
		}
		catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}

		System.out.println();
	}

}
