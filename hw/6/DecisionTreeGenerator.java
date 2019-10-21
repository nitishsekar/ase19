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
		//tbl.dump();
		DecisionTree finalDT = recurse(tbl, s, 1);
		finalDT.setLevel(0);
		finalDT.makeRoot();
		finalDT.printTree(finalDT);
	}
	
	private DecisionTree recurse(Tbl tbl, Set<Integer> rows, int level) {
		Sym labelSym = (Sym) tbl.getCols().get(tbl.getCols().size()-1);
		if(tbl.getRowCount() > MIN_ROWS || labelSym.getModeCount() < tbl.getRowCount()) {
			DecisionTree node = new DecisionTree();
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
			//newTbl.dump();
			SplitAttributes sAtt = new SplitAttributes();
			SplitAttributesResponse bestResp = new SplitAttributesResponse();
			try {
				double bestInformationGain = Double.MIN_VALUE;
				String bestFeature = "";
				for (int j = 0; j < newTbl.getCols().size()-1; j++) {
					SplitAttributesResponse splitAttributesResponse = sAtt.identifyFeatureSplit(newTbl.getCols().get(newTbl.getCols().size()-1), newTbl.getCols().get(j));
					Sym label = ((Sym)newTbl.getCols().get(newTbl.getCols().size()-1));
					double featureInformationGain = calcInformationGain(splitAttributesResponse,label);
					// System.out.println(((Col)newTbl.getCols().get(j)).getTxt()+" "+featureInformationGain);
					if (featureInformationGain > bestInformationGain) {
						bestInformationGain = featureInformationGain;
						bestFeature = ((Col)newTbl.getCols().get(j)).getTxt();
						bestResp = splitAttributesResponse;
					}
					sAtt.clear();
				}
				// System.out.println(bestFeature);
				// System.out.println("Label: "+((Sym)newTbl.getCols().get(newTbl.getCols().size()-1)).getEntropy());
				
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
			leafNode.setLeafStats(sym);
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
			//System.out.println(prob+" "+entropy);
			featureEntropy = featureEntropy + prob*entropy;
		}
		return label.getEntropy()-featureEntropy;
	}
}
