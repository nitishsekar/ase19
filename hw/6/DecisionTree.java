import java.util.ArrayList;
import java.util.List;

public class DecisionTree {
	List<DecisionTree> children;
	String feature;
	Num stats;
	Sym leafStats;
	boolean isRoot;
	int level;

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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
}
