import java.util.ArrayList;
import java.util.List;

public class RPTree {
	private List<RPTree> children;
	private List<Col> leafStats;
	private int level;
	private int splitCount;
	private boolean isRoot;
	
	public RPTree() {
		children = new ArrayList<>();
		leafStats = new ArrayList<>();
		isRoot = false;
		level = 0;
		splitCount = 0;
	}

	public List<RPTree> getChildren() {
		return children;
	}

	public void setChildren(List<RPTree> children) {
		this.children = children;
	}

	public List<Col> getLeafStats() {
		return leafStats;
	}

	public void setLeafStats(List<Col> leafStats) {
		this.leafStats = leafStats;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getSplitCount() {
		return splitCount;
	}

	public void setSplitCount(int splitCount) {
		this.splitCount = splitCount;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
}
