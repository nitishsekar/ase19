import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DecisionTreeGenerator {
	public static final int MIN_ROWS = 9;
	public void createDecisionTree(Tbl tbl, String labelType, StringBuilder sb) {
		Set<Integer> s = new HashSet<>();
		DecisionTree finalDT = recurse(tbl, s, 1, labelType);
		finalDT.setLevel(0);
		finalDT.makeRoot();
		if (labelType == "Sym") {
			finalDT = finalDT.pruneTree(finalDT);
			finalDT.printTree(finalDT,labelType, 0, sb);
		} else {
			finalDT.printTree(finalDT,labelType, 0, sb);
			
		}
	}

	private DecisionTree recurse(Tbl tbl, Set<Integer> rows, int level, String labelType) {
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
		if (labelType == "Sym") {
			Sym labelSym = (Sym) newTbl.getCols().get(newTbl.getCols().size()-1);

			if(newTbl.getRowCount() > MIN_ROWS && labelSym.getModeCount() < newTbl.getRowCount()) {
				DecisionTree node = new DecisionTree(labelType);
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
						List<Integer> indices = bestResp.getIndicesLists().get(k);
						Set<Integer> inds = new HashSet<>();
						for(int j:indices) {
							inds.add(j);
						}
						DecisionTree child = recurse(newTbl,inds,level+1,labelType);
						child.setFeature(bestFeature);
						if (featureRanges.get(k).getClass() == Num.class) {
							Num c = (Num) featureRanges.get(k);
							child.setStats(c);
						}
						if (featureRanges.get(k).getClass() == Sym.class) {
							Sym c = (Sym) featureRanges.get(k);
							child.setFlag(true);
							child.setSymStats(c);
						}
						children.add(child);

					}
					node.setChildren(children);
					node.setLevel(level-1);

				} catch (IOException e) {
					e.printStackTrace();
				}
				return node;
			} else {
				DecisionTree leafNode = new DecisionTree(labelType);
				Sym sym = (Sym) newTbl.getCols().get(newTbl.getCols().size()-1);
				List<DecisionTree> children = new ArrayList<>();
				leafNode.setChildren(children);
				leafNode.setSymLeafStats(new Sym(sym));
				leafNode.setLevel(level-1);
				return leafNode;
			}
		}
		if (labelType == "Num") {
			Num labelSym = (Num) newTbl.getCols().get(newTbl.getCols().size()-1);

			if(newTbl.getRowCount() > MIN_ROWS) {
				DecisionTree node = new DecisionTree(labelType);
				SplitAttributes sAtt = new SplitAttributes();
				SplitAttributesResponse bestResp = new SplitAttributesResponse();
				try {
					double bestSDR = 0;
					String bestFeature = "";
					double ratio = 0.0;
					for (int j = 0; j < newTbl.getCols().size()-1; j++) {
						SplitAttributesResponse splitAttributesResponse = sAtt.identifyFeatureSplit(newTbl.getCols().get(newTbl.getCols().size()-1), newTbl.getCols().get(j));
						Num label = ((Num)newTbl.getCols().get(newTbl.getCols().size()-1));
						double featureSDR = calcSDReduction(splitAttributesResponse,label);
//						System.out.println("Best "+bestSDR+" Feature sdr"+featureSDR+" Ratio "+featureSDR/label.getStdDev());
						if (featureSDR > bestSDR) {
							bestSDR = featureSDR;
							bestFeature = ((Col)newTbl.getCols().get(j)).getTxt();
							bestResp = splitAttributesResponse;
							ratio = bestSDR/label.getStdDev();
						}
						sAtt.clear();
					}
					if (ratio < 0.2) {
						DecisionTree leafNode = new DecisionTree(labelType);
						Num num = (Num) newTbl.getCols().get(newTbl.getCols().size()-1);
						List<DecisionTree> children = new ArrayList<>();
						leafNode.setChildren(children);
						leafNode.setNumLeafStats(new Num(num));
						leafNode.setLevel(level-1);
						return leafNode;
					}
					
					List<DecisionTree> children = new ArrayList<>();
					List<Col> featureRanges = bestResp.getFeatureRanges();
					for(int k=0; k<featureRanges.size(); k++) {
						List<Integer> indices = bestResp.getIndicesLists().get(k);
						Set<Integer> inds = new HashSet<>();
						for(int j:indices) {
							inds.add(j);
						}
						DecisionTree child = recurse(newTbl,inds,level+1,labelType);
						child.setFeature(bestFeature);
						if (featureRanges.get(k).getClass() == Num.class) {
							Num c = (Num) featureRanges.get(k);
							child.setStats(c);
						}
						if (featureRanges.get(k).getClass() == Sym.class) {
							Sym c = (Sym) featureRanges.get(k);
							child.setFlag(true);
							child.setSymStats(c);
						}
						children.add(child);

					}
					node.setChildren(children);
					node.setLevel(level-1);

				} catch (IOException e) {
					e.printStackTrace();
				}
				return node;
			} else {
				DecisionTree leafNode = new DecisionTree(labelType);
				Num num = (Num) newTbl.getCols().get(newTbl.getCols().size()-1);
				List<DecisionTree> children = new ArrayList<>();
				leafNode.setChildren(children);
				leafNode.setNumLeafStats(new Num(num));
				leafNode.setLevel(level-1);
				return leafNode;
			}
		}
		return null;
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

	public double calcSDReduction(SplitAttributesResponse splitAttributesResponse, Num label) {
		List<Col> labelRanges = splitAttributesResponse.getLabelRanges();
		double weightedSD = 0.0;
		for (Col col : labelRanges) {
			double prob = (double) ((Num) col).getCount()/label.getCount();
			double rangeSD = ((Num) col).getStdDev();
//			System.out.println("Prob: "+prob+" RangeSD: "+rangeSD);
			weightedSD = weightedSD + prob*rangeSD;
		}
//		System.out.println("Label sd: "+label.getStdDev()+" weighted sd: "+weightedSD);
		return label.getStdDev()-weightedSD;
	}
}