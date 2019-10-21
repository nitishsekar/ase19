import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DecisionTreeGenerator {
	public void createDecisionTree(Tbl tbl) {
		Set<Integer> s = new HashSet<>();
		
		recurse(tbl, s);
	}
	
	private void recurse(Tbl tbl, Set<Integer> rows) {
		Tbl newTbl = new Tbl(tbl);
		int i = 0, adj = 0;
		int count = newTbl.getRowCount();
		while(i < count) {
			// Delete rows from the table if index not in Set rows
			if(rows.contains(i)) {
				try {
					newTbl.deleteRow(i-adj);
					adj++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			i++;
		}
		//newTbl.dump();
		SplitAttributes sAtt = new SplitAttributes();
		try {
			sAtt.identifyFeatureSplit(newTbl.getCols().get(newTbl.getCols().size()-1), newTbl.getCols().get(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
