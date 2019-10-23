import java.util.ArrayList;
import java.util.List;

public class Row {
	private List<String> cells;
	
	public Row() {
		cells = new ArrayList<String>();
	}
	
	public Row(Row r) {
		this.cells = new ArrayList<>();
		for(String s:r.cells) {
			this.cells.add(s);
		}
	}
	
	public void addCell(Integer cell) {
		cells.add(cell.toString());
	}
	
	public void addCell(Float cell) {
		cells.add(cell.toString());
	}

	public void addCell(String cell) {
		cells.add(cell);
	}
	
	public List<String> getCells() {
		return cells;
	}
}
