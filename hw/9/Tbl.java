import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tbl {
	private int oid;
	private List<Row> rows;
	/* Note: Num is a subclass of Col. Hence, Col is not used to initilize cols. */
	private List<Col> cols;
	private List<List<String>> file;
	private Set<Integer> ignoreCol;
	private Set<Integer> symCols;
	private My my;
	
	
	public Tbl() {
		rows = new ArrayList<Row>();
		cols = new ArrayList<Col>();
		file = new ArrayList<List<String>>();
		ignoreCol = new HashSet<>();
		symCols = new HashSet<>();
		my = new My();
	}
	
	public Tbl(Tbl tbl) {
		this.rows = new ArrayList<>();
		for(int i=0; i<tbl.rows.size(); i++) {
			this.rows.add(new Row(tbl.rows.get(i)));
		}
    	this.cols = new ArrayList<>();
    	for(int i=0; i<tbl.cols.size(); i++) {
    		Col c = tbl.cols.get(i);
    		if(c.getClass() == Num.class) {
    			this.cols.add(new Num((Num) c));
    		} else if(c.getClass() == Sym.class) {
    			this.cols.add(new Sym((Sym) c));
    		} else {
    			this.cols.add(new Col(c));
    		}
		}
    	this.file = tbl.file;
		this.ignoreCol = tbl.ignoreCol;
		this.symCols = tbl.symCols;
		this.my = tbl.my;
	}

	public Tbl(Tbl tbl, List<Row> rows, List<List<String>> file) throws IOException {
//		System.out.println("hello");
		this.rows = new ArrayList<>();
		this.file = new ArrayList<>();
		for(int i=0; i<file.size(); i++) {
			this.file.add(file.get(i));
		}
		this.ignoreCol = tbl.ignoreCol;
		this.symCols = tbl.symCols;
		this.my = tbl.my;
//		clearCols(tbl.getCols());
		this.cols = new ArrayList<>();
		for(int i=0; i<tbl.cols.size(); i++) {
			Col c = tbl.cols.get(i);
			if(c.getClass() == Num.class) {
				Num n = new Num((Num) c);
				n.clearAllVals();
				this.cols.add(n);
			} else if(c.getClass() == Sym.class) {
				Sym s = new Sym((Sym)c);
				((Sym) c).clearAllVals();
				this.cols.add(c);
			} else {
				this.cols.add(new Col(c));
			}
		}

//		for (List<String> lst : file) {
//			addRow(lst);
//		}
		for (int i = 1; i < file.size(); i++) {
		    addRow(file.get(i));
        }
//		System.out.println("hi");
	}

	public List<List<String>> getFile() {
		return file;
	}

	public void setFile(List<List<String>> file) {
		this.file = file;
	}

	public void clearCols(List<Col> cols) throws IOException {
		for (Col col : cols) {
			if (col.getClass() == Num.class) {
				col = (Num) col;
				int size = ((Num) col).getValList().size();
				for (int i = 0; i < size; i++) {
					((Num) col).deleteIthNum(0);
//					System.out.println("Deleted "+i);
				}
//				System.out.println("hi");
			}
			if (col.getClass() == Sym.class) {
				col = (Sym) col;
				int size = ((Sym)col).getValList().size();
				for (int i = 0; i < size; i++) {
					((Sym) col).deleteIthSym(0);
				}
			}
		}
	}
	
	public void setCols(List<String> row) {
		int colCount = 0;
		int colN = 0;
		for(String s: row) {
			if(!s.contains("?")) {
				if(s.contains("<") || s.contains(">") || s.contains("$")) {
					/* Adding a Num if <>$ present */
					Num col = new Num();
					colN++;
					col.setTxt(s);
					col.setPos(colCount);
					cols.add(col);
					my.addToList("nums", colCount+1);
				} else {
					/* Adding a Sym otherwise */
					Sym col = new Sym();
					colN++;
					col.setTxt(s);
					col.setPos(colCount);
					cols.add(col);
					my.addToList("syms", colCount+1);
					symCols.add(colCount);
				}
				if(s.contains("<") || s.contains(">") || s.contains("!")) {
					/* Adding goal indices */
					my.addToList("goals", colCount+1);
					if(s.contains("<")) {
						/* Adding negative weight indices */
						my.addToList("w", colCount+1);
					}
				} else {
					/* Adding xs indices */
					my.addToList("xs", colCount+1);
					if(s.contains("$")) {
						my.addToList("xnums", colCount+1);
					} else {
						my.addToList("xsyms", colCount+1);
					}
				}
			} else {
				ignoreCol.add(colCount);
				cols.add(new Col());
			}
			colCount++;
		}
	}
	
	public void addRow(List<String> row) {
		int colCount = 0;
		Row newRow = new Row();
		if(row != null) {
			for(int j=0; j<row.size(); j++) {
				if(!ignoreCol.contains(j)) {
					String s = row.get(j);
					if("?".equals(s)) {
						newRow.addCell("");
						/* Adding 0 when ? is encountered. Is this right? */
						Col col = cols.get(colCount);
							if(col.getClass() == Num.class) {
							try {
								Num num = (Num) col;
								num.updateMeanAndSD(0.0f);
								cols.set(colCount, num);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} else {
						if(symCols.contains(colCount)) {
							newRow.addCell(s);
							Sym sym = (Sym) cols.get(colCount);
							sym.addSymbol(s);
							cols.set(colCount, sym);
						} else {
							try {
								Integer val = Integer.parseInt(s);
								newRow.addCell(val);
								Num col = (Num) cols.get(colCount);
								try {
									col.updateMeanAndSD((float)val);
									cols.set(colCount, col);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} catch(NumberFormatException e) {
								Float val = Float.parseFloat(s);
								newRow.addCell(val);
								Num col = (Num) cols.get(colCount);
								try {
									col.updateMeanAndSD(val);
									cols.set(colCount, col);
								} catch (IOException e2) {
									e2.printStackTrace();
								}
							}
							
						}
					}
				}
				colCount++;
			}
		}
		rows.add(newRow);
	}
	
	public void deleteRow(int j) throws IOException {
		rows.remove(j);
		int size = cols.size();
		for(int i=0; i<size; i++) {
			Col c = cols.get(i);
			if(c.getClass() == Num.class) {
				((Num) c).deleteIthNum(j);
			} else if(c.getClass() == Sym.class) {
				((Sym) c).deleteIthSym(j);
			}
			cols.remove(i);
			cols.add(i, c);
		}
	}
	
	public Set<Integer> getIgnoreCol() {
		return ignoreCol;
	}
	
	public Integer getRowCount() {
		return rows.size();
	}

	public List<Col> getCols() {
		return cols;
	}
	
	public String getClassMode() {
		/* Returns mode if the last Column is a class (Sym). Else, null. */
		Col col = cols.get(cols.size()-1);
		if(col.getClass() == Sym.class) {
			Sym sym = (Sym) col;
			return sym.getMode();
		} else {
			return null;
		}
	}
	
	public String read(String fileName) {
		BufferedReader csvReader;
		try {
//			System.out.println("Reading file: "+fileName);
			csvReader = new BufferedReader(new FileReader(fileName));
			String line;
			int rowCount = 0, colCount = 0;
			try {
				while ((line = csvReader.readLine()) != null) {
					rowCount++;
					if(!line.isEmpty()) {
						String[] data = line.split(",");
						if(rowCount == 1) {
							colCount = data.length; 
						}
						if(data.length == colCount) {
							List<String> fileRow = new ArrayList<String>();
							for(String s: data) {
								if(!s.contains("#")) {
									/* Removing comments */
									fileRow.add(s.trim());
								} else {
									fileRow.add(s.trim().split(" ")[0]);
								}
							}
							file.add(fileRow);
						} else {
							file.add(null);
						}
					}
				}
				csvReader.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		parseFile();
		return cols.get(cols.size()-1).getClass().toString();
	}
	
	public void dump() {
		System.out.println("t.cols");
		for(int i=1; i<=cols.size(); i++) {
			Col col = cols.get(i-1);
			if(col.getClass() == Num.class) {
				Num num = (Num) col;	
				System.out.println("| "+i);//Change this to getPos maybe.
				System.out.println("| | add: Num");
				System.out.println("| | col: "+num.getPos());
				System.out.println("| | hi: "+num.getHi());
				System.out.println("| | low: "+num.getLow());
				System.out.println(String.format("| | mean: %.4f",num.getMean()));
				System.out.println("| | n: "+num.getCount());
				System.out.println(String.format("| | sd: %.4f",num.getStdDev()));
				System.out.println("| | txt: "+num.getTxt());
			} else if(col.getClass() == Sym.class) {
				Sym sym = (Sym) col;	
				System.out.println("| "+i);//Change this to getPos maybe.
				System.out.println("| | add: Sym");
				Map<String, Integer> cnt = sym.getColMap();
				System.out.println("| | cnt");
				for(String k: cnt.keySet()) {
					System.out.println("| | | "+k+": "+cnt.get(k));
				}
				System.out.println("| | col: "+sym.getPos());
				System.out.println("| | mode: "+sym.getMode());
				System.out.println("| | most: "+sym.getModeCount());
				System.out.println("| | n: "+sym.getTotalCount());
				System.out.println(String.format("| | entropy: %.2f",sym.entropyCalc()));
				System.out.println("| | txt: "+sym.getTxt());
			} else {
				System.out.println("| "+i+" Empty");
			}
		}
		System.out.println("t.my");
		List<Integer> myList;
		myList = my.getGoals();
		System.out.println("| goals");
		for(int j=0; j<myList.size(); j++) {
			System.out.println("| | "+myList.get(j));
		}
		myList = my.getNums();
		System.out.println("| nums");
		for(int j=0; j<myList.size(); j++) {
			System.out.println("| | "+myList.get(j));
		}
		myList = my.getSyms();
		System.out.println("| syms");
		for(int j=0; j<myList.size(); j++) {
			System.out.println("| | "+myList.get(j));
		}
		myList = my.getW();
		System.out.println("| w");
		for(int j=0; j<myList.size(); j++) {
			System.out.println("| | "+myList.get(j)+" : -1");
		}
		myList = my.getXnums();
		System.out.println("| xnums");
		for(int j=0; j<myList.size(); j++) {
			System.out.println("| | "+myList.get(j));
		}
		myList = my.getXs();
		System.out.println("| xs");
		for(int j=0; j<myList.size(); j++) {
			System.out.println("| | "+myList.get(j));
		}
		myList = my.getXsyms();
		System.out.println("| xsyms");
		for(int j=0; j<myList.size(); j++) {
			System.out.println("| | "+myList.get(j));
		}
		
		System.out.println("t.rows");
		for(int i=1; i<=rows.size(); i++) {
			System.out.println("| "+i);
			System.out.println("| | cells");
			List<String> list = rows.get(i-1).getCells();
			for(int j=0; j<list.size(); j++) {
				System.out.println("| | | "+(j+1)+": "+list.get(j));
			}
		}
		
	}
	
	private void parseFile() {
		for(int i=0; i<file.size(); i++) {
			if(i == 0) {
				int colCount = 0;
				int colN = 0;
				List<String> row = file.get(i);
				for(String s: row) {
					if(!s.contains("?")) {
						if(s.contains("<") || s.contains(">") || s.contains("$")) {
							/* Adding a Num if <>$ present */
							Num col = new Num();
							colN++;
							col.setTxt(s);
							col.setPos(colCount);
							if(s.contains("<")) {
								col.setWeight(-1);
							}
							cols.add(col);
							my.addToList("nums", colCount+1);
						} else {
							/* Adding a Sym otherwise */
							Sym col = new Sym();
							colN++;
							col.setTxt(s);
							col.setPos(colCount);
							cols.add(col);
							my.addToList("syms", colCount+1);
							symCols.add(colCount);
						}
						if(s.contains("<") || s.contains(">") || s.contains("!")) {
							/* Adding goal indices */
							my.addToList("goals", colCount+1);
							if(s.contains("<")) {
								/* Adding negative weight indices */
								my.addToList("w", colCount+1);
							}
						} else {
							/* Adding xs indices */
							my.addToList("xs", colCount+1);
							if(s.contains("$")) {
								my.addToList("xnums", colCount+1);
							} else {
								my.addToList("xsyms", colCount+1);
							}
						}
					} else {
						ignoreCol.add(colCount);
						cols.add(new Col());
					}
					colCount++;
				}
			} else {
				int colCount = 0;
				Row newRow = new Row();
				List<String> row = file.get(i);
				if(row != null) {
					for(int j=0; j<row.size(); j++) {
						if(!ignoreCol.contains(j)) {
							String s = row.get(j);
							if("?".equals(s)) {
								newRow.addCell("");
								/* Adding null when ? is encountered. Is this right? */
								Col col = cols.get(colCount);
									if(col.getClass() == Num.class) {
									try {
										Num num = (Num) col;
										num.updateMeanAndSD(null);
										cols.set(colCount, num);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							} else {
								if(symCols.contains(colCount)) {
									newRow.addCell(s);
									Sym sym = (Sym) cols.get(colCount);
									sym.addSymbol(s);
									cols.set(colCount, sym);
								} else {
									Float val = Float.parseFloat(s);
									newRow.addCell(val);
									Num col = (Num) cols.get(colCount);
									try {
										//System.out.println(s+" "+val);
										col.updateMeanAndSD((float)val);
										cols.set(colCount, col);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						} else {
							newRow.addCell("");
						}
						colCount++;
					}
				}
				rows.add(newRow);
			}
		}
		//printRows();
	}
	
	public My getMy() {
		return my;
	}

	public List<Row> getRows() {
		return rows;
	}

	private void printRows() {
		System.out.print("[");
		for(int i=0; i<cols.size(); i++) {
			System.out.print("'"+cols.get(i).getTxt()+"'");
			if(i != cols.size()-1)
				System.out.print(", ");
		}
		System.out.println("]");
		for(Row i: rows) {
			List<String> cells = i.getCells();
			if(cells.size() == 0) {
				System.out.println("E> Skipping this line. Invalid number of cells provided.");
			} else {
				System.out.print("[");
				for(int j=0; j<cells.size(); j++) {
					
					System.out.print("'"+(cells.get(j) == null ? "?" : cells.get(j))+"'");
					if(j != cells.size()-1)
						System.out.print(", ");
				}
				System.out.println("]");
			}
		}
		System.out.println("\n");
	}
}
