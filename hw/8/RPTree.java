import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class RPTree {
	private List<RPTree> children;
	private List<Col> leafStats;
	private int level;
	private int splitCount;
	private boolean isRoot;
	private List<Row> rows;
	private RPTree envy;
	
	public RPTree getEnvy() {
		return envy;
	}

	public void setEnvy(RPTree envy) {
		this.envy = envy;
	}

	public RPTree() {
		children = new ArrayList<>();
		leafStats = new ArrayList<>();
		isRoot = false;
		level = 0;
		splitCount = 0;
		rows = new ArrayList<>();
	}

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
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
	
	public void printTree(RPTree r) {
		if(!r.isRoot) {
			for(int i=0; i<r.level; i++)
				System.out.print("|. ");
		}
		System.out.println(r.getSplitCount());
		if(r.children.size() == 0) {
			for(int i=0; i<r.level-1; i++)
				System.out.print("|. ");
			for(Col c:r.getLeafStats()) {
				System.out.print(c.getTxt()+" = ");
				if(c.getClass() == Num.class) {
					Num n = (Num) c;
					System.out.print(String.format("%6.2f (%.2f); ",n.getMean(),n.getStdDev()));
				} else if(c.getClass() == Sym.class) {
					Sym s = (Sym) c;
					System.out.print(String.format("%s (%.2f); ",s.getMode(),s.getEntropy()));
				}
			}
			System.out.println(" rows: "+r.getRows().size());
		} else {
			for(RPTree rc:r.getChildren()) {
				printTree(rc);
			}
		}
		if(r.isRoot()) {
			for(Col c:r.getLeafStats()) {
				System.out.print(c.getTxt()+" = ");
				if(c.getClass() == Num.class) {
					Num n = (Num) c;
					System.out.print(String.format("%6.2f (%.2f); ",n.getMean(),n.getStdDev()));
				} else if(c.getClass() == Sym.class) {
					Sym s = (Sym) c;
					System.out.print(String.format("%s (%.2f); ",s.getMode(),s.getEntropy()));
				}
			}
		}
	}

	public void printClusterToFile(Tbl tbl, RPTree leaf, String fileName) {
		PrintWriter writer;
		try {
		    File file = new File(fileName);
		    if (file.exists()) {
		        file.delete();
            }
			writer = new PrintWriter(file);
			StringBuilder sb = new StringBuilder();
			for (Col col : tbl.getCols()) {
				sb.append(col.getTxt());
				sb.append(',');
			}
			sb.append("!class");
			sb.append('\n');
			for (Row row : leaf.rows) {
				for (String string : row.getCells()) {
					if (string.equals("")) {
						sb.append("?");
					}
					else {
						sb.append(string);
					}
					sb.append(',');
				}
				sb.append("class1");
				sb.append('\n');
			}
			if (leaf.envy != null) {
				for (Row row : leaf.envy.rows) {
					for (String string : row.getCells()) {
						if (string.equals("")) {
							sb.append("?");
						}
						else {
							sb.append(string);
						}
						sb.append(',');
					}
					sb.append("class2");
					sb.append('\n');
				}
			}
			writer.write(sb.toString());
			writer.flush();
		}
		catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}
