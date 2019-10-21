import java.util.ArrayList;
import java.util.List;

public class TestDT {

	public static void main(String[] args) {
		
		Tbl tbl = new Tbl();
		tbl.read("C:\\Users\\nitis\\Desktop\\CSC 591 - ASE\\HW 6\\diabetes.csv");
		//tbl.dump();
		DecisionTreeGenerator dt = new DecisionTreeGenerator();
		dt.createDecisionTree(tbl);
		
		/*
		try {
			Num n1 = new Num();
			for(int i=0; i<15; i++) {
				n1.updateMeanAndSD((float) i);
			}
			Num n2 = new Num();
			for(int i=16; i<30; i++) {
				n2.updateMeanAndSD((float) i);
			}
			Sym s1 = new Sym();
			for(int j=0; j<10; j++)
			s1.addSymbol("a");
			Sym s2 = new Sym();
			for(int j=0; j<10; j++)
			s2.addSymbol("b");
			
			DecisionTree dc1 = new DecisionTree();
			dc1.setStats(n1);
			dc1.setLeafStats(s1);
			dc1.setLevel(2);
			dc1.setFeature("test1");
			DecisionTree dc2 = new DecisionTree();
			dc2.setStats(n2);
			dc2.setLeafStats(s2);
			dc2.setLevel(2);
			dc2.setFeature("test1");
			List<DecisionTree> l = new ArrayList<>();
			l.add(dc1);
			l.add(dc2);
			DecisionTree dcc = new DecisionTree();
			dcc.setChildren(l);
			dcc.setLevel(1);
			dcc.setFeature("test2");
			Num n4 = new Num();
			for(int i=15; i<30; i++) {
				n4.updateMeanAndSD((float) i);
			}
			dcc.setStats(n4);
			
			Num n3 = new Num();
			for(int i=0; i<15; i++) {
				n3.updateMeanAndSD((float) i);
			}
			Sym s3 = new Sym();
			for(int j=0; j<10; j++)
			s3.addSymbol("k");
			
			DecisionTree dcc1 = new DecisionTree();
			dcc1.setStats(n3);
			dcc1.setLeafStats(s3);
			dcc1.setLevel(1);
			dcc1.setFeature("test2");
			List<DecisionTree> l1 = new ArrayList<>();
			l1.add(dcc1);
			l1.add(dcc);
			DecisionTree root = new DecisionTree();
			root.setChildren(l1);
			root.setLevel(0);
			root.makeRoot();
			root.printTree(root);
			
			
			
		} catch(Exception e) {
			
		}
		*/
	}

}
