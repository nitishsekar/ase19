import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DecisionTreeGenerator {
	public static final int MIN_ROWS = 4;
	public void createDecisionTree(Tbl tbl) {
		Set<Integer> s = new HashSet<>();
		DecisionTree finalDT = recurse(tbl, s, 1);
		finalDT.setLevel(0);
		finalDT.makeRoot();
		finalDT.printTree(finalDT);
		finalDT = finalDT.pruneTree(finalDT);
		finalDT.printTree(finalDT);
	}
	
	private DecisionTree recurse(Tbl tbl, Set<Integer> rows, int level) {
		Tbl newTbl = new Tbl(tbl);
		int i = 0, adj = 0;
		int count = newTbl.getRowCount();
		while(i < count) {
			// Delete rows from the table if index not in Set rows
			if(!rows.isEmpty()) {
				if(!rows.contains(i)) {
					try {
						newTbl.deleteRow(i-adj);
						adj++;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			i++;
		}
		Sym labelSym = (Sym) newTbl.getCols().get(newTbl.getCols().size()-1);
		
		if(newTbl.getRowCount() > MIN_ROWS && labelSym.getModeCount() < newTbl.getRowCount()) {
			DecisionTree node = new DecisionTree();
			SplitAttributes sAtt = new SplitAttributes();
			SplitAttributesResponse bestResp = new SplitAttributesResponse();
			try {
				double bestInformationGain = 0;
				String bestFeature = "";
				for (int j = 0; j < newTbl.getCols().size()-1; j++) {
					SplitAttributesResponse splitAttributesResponse = sAtt.identifyFeatureSplit(newTbl.getCols().get(newTbl.getCols().size()-1), newTbl.getCols().get(j));
					Sym label = ((Sym)newTbl.getCols().get(newTbl.getCols().size()-1));
					double featureInformationGain = calcInformationGain(splitAttributesResponse,label);

					if (featureInformationGain > bestInformationGain) {
						bestInformationGain = featureInformationGain;
						bestFeature = ((Col)newTbl.getCols().get(j)).getTxt();
						bestResp = splitAttributesResponse;
					}
					sAtt.clear();
				}
				
				List<DecisionTree> children = new ArrayList<>();
				List<Col> featureRanges = bestResp.getFeatureRanges();
				for(int k=0; k<featureRanges.size(); k++) {
					Num c = (Num) featureRanges.get(k);
					List<Integer> indices = bestResp.getIndicesLists().get(k);
					Set<Integer> inds = new HashSet<>();
					for(int j:indices) {
						inds.add(j);
					}
					DecisionTree child = recurse(newTbl,inds,level+1);
					child.setFeature(bestFeature);
					child.setStats(c);
					children.add(child);
					
				}
				node.setChildren(children);
				node.setLevel(level-1);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return node;
		} else {
			DecisionTree leafNode = new DecisionTree();
			Sym sym = (Sym) tbl.getCols().get(tbl.getCols().size()-1);
			List<DecisionTree> children = new ArrayList<>();
			leafNode.setChildren(children);
			leafNode.setLeafStats(new Sym(sym));
			leafNode.setLevel(level-1);
			return leafNode;
		}
	}

	public double calcInformationGain(SplitAttributesResponse splitAttributesResponse, Sym label) {
		List<Col> labelRanges = splitAttributesResponse.getLabelRanges();
		double featureEntropy = 0.0;
		for (Col col : labelRanges) {
			double prob = (double) ((Sym) col).getCount()/label.getCount();
			double entropy = (double) ((Sym) col).getEntropy();
			featureEntropy = featureEntropy + prob*entropy;
		}
		return label.getEntropy()-featureEntropy;
	}
}
