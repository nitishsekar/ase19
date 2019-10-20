import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DecisionTreeGenerator {
	public void createDecisionTree(Tbl tbl) {
		Set<Integer> s = new HashSet<>();
		s.add(5);
		s.add(4);
		s.add(2);
		recurse(tbl, s);
	}
	
	private void recurse(Tbl tbl, Set<Integer> rows) {
		Tbl newTbl = new Tbl(tbl);
		int i = 0, adj = 0;
		int count = newTbl.getRowCount();
		while(i < count) {
			// Delete rows from the table if index not in Set rows
			if(!rows.contains(i)) {
				try {
					newTbl.deleteRow(i-adj);
					adj++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			i++;
		}
		newTbl.dump();
	}
}
