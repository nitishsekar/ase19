import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DecisionTree {
	private List<DecisionTree> children;
	private String feature;
	private Num stats;
	private Sym leafStats;
	private boolean isRoot;
	private int level;

	public DecisionTree() {
		children = new ArrayList<>();
		stats = new Num();
		isRoot = false;
		leafStats = new Sym();
		level = 0;
		feature = new String();
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

	public Sym getLeafStats() {
		return leafStats;
	}

	public void setLeafStats(Sym leafStats) {
		this.leafStats = leafStats;
	}
	
	public void printTree(DecisionTree d) {
		if(!d.isRoot) {
			for(int i=0; i<d.level-1; i++)
				System.out.print("| ");
			System.out.print(d.feature+" = "+d.stats.getLow()+" .. "+d.stats.getHi());
		}
		if(d.children.size() > 0) {
			System.out.println();
			for(DecisionTree dt:d.children) {
				printTree(dt);
			}
		} else {
			System.out.println(" : "+d.leafStats.getMode()+" ("+d.leafStats.getModeCount()+")");
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
						if(d.getLeafStats().getMode().equals(prevMode))
							canPrune2 = true;
						prevMode = d.getLeafStats().getMode();
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
						if(d1.getLeafStats().getMode().equals(d2.getLeafStats().getMode())) {
							Num d2n = d2.getStats();
							Sym d2s = d2.getLeafStats();
							Num d1n = d1.getStats();
							for(float f:d2n.getValList()) {
								try {
									d1n.updateMeanAndSD(f);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							Sym d1s = d1.getLeafStats();
							for(String s:d2s.getValList()) {
								d1s.addSymbol(s);
							}
							d1.setLeafStats(d1s);
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
					dt.setLeafStats(dChild.leafStats);
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
	
}
