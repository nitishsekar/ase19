import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
			double bestInformationGain = Double.MIN_VALUE;
			String bestFeature;
			for (int j = 0; j < newTbl.getCols().size()-1; j++) {
				SplitAttributesResponse splitAttributesResponse = sAtt.identifyFeatureSplit(newTbl.getCols().get(newTbl.getCols().size()-1), newTbl.getCols().get(j));
				Sym label = ((Sym)newTbl.getCols().get(newTbl.getCols().size()-1));
				double featureInformationGain = calcInformationGain(splitAttributesResponse,label);
				if (featureInformationGain > bestInformationGain) {
					bestInformationGain = featureInformationGain;
					bestFeature = ((Col)newTbl.getCols().get(j)).getTxt();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public double calcInformationGain(SplitAttributesResponse splitAttributesResponse, Sym label) {
		List<Col> labelRanges = splitAttributesResponse.getLabelRanges();
		double featureEntropy = 0.0;
		for (Col col : labelRanges) {
			double colEntropy = 0.0;
			for (Map.Entry<String,Integer> entry : ((Sym)col).colMap.entrySet()) {
				if (entry.getValue() == 0) continue;
				double pi = ((double)entry.getValue()/((Sym)col).getCount());
				colEntropy = colEntropy + (-pi)*(Math.log(pi)/Math.log(2));
			}
			featureEntropy = featureEntropy + colEntropy*(((Sym)col).getCount()/label.getCount());
		}
		return label.getEntropy()-featureEntropy;
	}
}
