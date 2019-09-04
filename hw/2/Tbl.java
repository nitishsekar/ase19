import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tbl {
	private int oid;
	private List<Row> rows;
	/* Note: Num is a subclass of Col. Hence, Col is not used to initilize cols. */
	private List<Num> cols;
	private List<List<String>> file;
	private Set<Integer> ignoreCol;
	
	
	public Tbl() {
		rows = new ArrayList<Row>();
		cols = new ArrayList<Num>();
		file = new ArrayList<List<String>>();
		ignoreCol = new HashSet<>();
	}
	
	public void read(String fileName) {
		System.out.println("PART 1");
		BufferedReader csvReader;
		try {
			System.out.println("Reading file: "+fileName);
			csvReader = new BufferedReader(new FileReader(fileName));
			String line;
			int rowCount = 0, colCount = 0;
			try {
				System.out.println("Converting the file into a list of lists.\nNOTE: Handling one special case (row not the same size as line 1) now to make code simpler");
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
								fileRow.add(s.trim());
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
		for(List<String> i: file) {
			System.out.println(i);
		}
		System.out.println("null represents invalid rows.\n");
		parseFile();
	}
	
	public void dump() {
		System.out.println("t.cols");
		for(int i=1; i<=cols.size(); i++) {
			Num num = cols.get(i-1);
			System.out.println("| "+i);
			System.out.println("| | col: "+num.getPos());
			System.out.println("| | hi: "+num.getHi());
			System.out.println("| | low: "+num.getLow());
			System.out.println("| | mean: "+num.getMean());
			System.out.println("| | n: "+num.getCount());
			System.out.println("| | sd: "+num.getStdDev());
			System.out.println("| | txt: "+num.getTxt());
		}
		System.out.println("t.rows");
		for(int i=1; i<=rows.size(); i++) {
			System.out.println("| "+i);
			System.out.println("| | cells");
			List<Integer> list = rows.get(i-1).getCells();
			for(int j=0; j<list.size(); j++) {
				System.out.println("| | | "+(j+1)+": "+list.get(j));
			}
		}
	}
	
	private void parseFile() {
		System.out.println("PART 2");
		for(int i=0; i<file.size(); i++) {
			if(i == 0) {
				int colCount = 0;
				int colN = 0;
				List<String> row = file.get(i);
				for(String s: row) {
					if(!s.contains("?")) {
						Num col = new Num();
						colN++;
						col.setTxt(s);
						col.setPos(colN);
						cols.add(col);
					} else {
						ignoreCol.add(colCount);
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
								newRow.addCell(null);
								/* Adding 0 when ? is encountered. Is this right? */
								Num col = cols.get(colCount);
								try {
									col.updateMeanAndSD(0);
									cols.set(colCount, col);
								} catch (IOException e) {
									e.printStackTrace();
								}
							} else {
								Integer val = Integer.parseInt(s);
								newRow.addCell(val);
								Num col = cols.get(colCount);
								try {
									col.updateMeanAndSD(val);
									cols.set(colCount, col);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							colCount++;
						}
					}
				}
				rows.add(newRow);
			}
		}
		printRows();
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
			List<Integer> cells = i.getCells();
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
