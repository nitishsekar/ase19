import java.util.ArrayList;
import java.util.List;

public class TestDT {

	public static void main(String[] args) {
		
		Tbl tbl = new Tbl();
		
		// For Regression Tree
		String labelType = tbl.read("C:\\Users\\satan\\OneDrive\\Desktop\\auto.csv");
		
		// Uncomment for Decision Tree
		// String labelType = tbl.read("diabetes.csv");
		
		DecisionTreeGenerator dt = new DecisionTreeGenerator();
		if (labelType.contains("Sym")) {
			dt.createDecisionTree(tbl,"Sym");
		}
		if (labelType.contains("Num")) {
			dt.createDecisionTree(tbl,"Num");
		}
	}

}
