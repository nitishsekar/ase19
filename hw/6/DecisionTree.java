import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DecisionTree {
	private List<DecisionTree> children;
	private String feature;
	private Num stats;
	private Sym symStats;
	private Sym symLeafStats;
	private Num numLeafStats;
	private boolean isRoot;
	private int level;
	private boolean flag;

	public DecisionTree(String labelType) {
		if (labelType == "Sym") {
			children = new ArrayList<>();
			stats = new Num();
			isRoot = false;
			symLeafStats = new Sym();
			level = 0;
			feature = new String();
			flag = false;
			symStats = new Sym();
		}
		if (labelType == "Num") {
			children = new ArrayList<>();
			stats = new Num();
			isRoot = false;
			numLeafStats = new Num();
			level = 0;
			feature = new String();
			flag = false;
			symStats = new Sym();
		}
	}

	public void makeRoot() {
		isRoot = true;
	}

	public List<DecisionTree> getChildren() {
		return children;
	}

	public void setChildren(List<DecisionTree> children) {
		this.children = children;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public Num getStats() {
		return stats;
	}

	public void setStats(Num stats) {
		this.stats = stats;
	}

	public Sym getSymLeafStats() {
		return symLeafStats;
	}

	public void setSymLeafStats(Sym symLeafStats) {
		this.symLeafStats = symLeafStats;
	}

	public Sym getSymStats() {
		return symStats;
	}

	public void setSymStats(Sym symStats) {
		this.symStats = symStats;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public void printTree(DecisionTree d, String labelType) {
		if(!d.isRoot) {
			for(int i=0; i<d.level-1; i++)
				System.out.print("| ");
			if (!d.isFlag()) {
				System.out.print(d.feature+" = "+d.stats.getLow()+" .. "+d.stats.getHi());
			}
			else {
				System.out.print(d.feature+" = "+d.symStats.getEntropy()+" .. "+d.symStats.getModeCount());
			}
		}
		if(d.children.size() > 0) {
			System.out.println();
			for(DecisionTree dt:d.children) {
				printTree(dt,labelType);
			}
		} else {
			if (labelType == "Sym") {
				System.out.println(" : "+d.symLeafStats.getMode()+" ("+d.symLeafStats.getModeCount()+")");
			}
			if (labelType == "Num") {
				System.out.println(" : "+d.numLeafStats.getStdDev()+" ("+d.numLeafStats.getCount()+")");
			}
		}
	}

	public DecisionTree pruneTree(DecisionTree dt) {
		if(dt.getChildren().size() > 0) {
			boolean canPrune1 = true;
			boolean canPrune2 = false;
			String prevMode = "";
			for(int i=0; i<dt.getChildren().size(); i++) {
				DecisionTree d = dt.getChildren().get(i);
				if(d.getChildren().size() > 0) {
					d = pruneTree(d);
					if(d.getChildren().size() > 0) {
						canPrune1 = false;
						prevMode = "";
					} else {
						if(d.getSymLeafStats().getMode().equals(prevMode))
							canPrune2 = true;
						prevMode = d.getSymLeafStats().getMode();
					}
					dt.getChildren().remove(i);
					dt.getChildren().add(i, d);
				}
			}

			if(canPrune1 || canPrune2) {
				int ctr = 0;
				while(ctr < dt.getChildren().size()-1) {
					//System.out.println(ctr);
					DecisionTree d1 = dt.getChildren().get(ctr);
					DecisionTree d2 = dt.getChildren().get(ctr+1);
					if(d1.getChildren().size() == 0 && d2.getChildren().size() == 0) {
						if(d1.getSymLeafStats().getMode().equals(d2.getSymLeafStats().getMode())) {
							Num d2n = d2.getStats();
							Sym d2s = d2.getSymLeafStats();
							Num d1n = d1.getStats();
							for(float f:d2n.getValList()) {
								try {
									d1n.updateMeanAndSD(f);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							Sym d1s = d1.getSymLeafStats();
							for(String s:d2s.getValList()) {
								d1s.addSymbol(s);
							}
							d1.setSymLeafStats(d1s);
							d1.setStats(d1n);
							dt.getChildren().remove(ctr);
							dt.getChildren().add(ctr, d1);
							dt.getChildren().remove(ctr+1);
						} else {
							ctr++;
						}
					} else {
						ctr++;
					}
				}
				if(dt.getChildren().size() == 1) {
					DecisionTree dChild = dt.getChildren().get(0);
					dt.getChildren().remove(0);
					dt.setSymLeafStats(dChild.symLeafStats);
				}
				return dt;
			} else {
				return dt;
			}
		}
		return dt;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Num getNumLeafStats() {
		return numLeafStats;
	}

	public void setNumLeafStats(Num numLeafStats) {
		this.numLeafStats = numLeafStats;
	}


}