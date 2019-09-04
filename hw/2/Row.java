import java.util.ArrayList;
import java.util.List;

public class Row {
	private List<Integer> cells;
	
	public Row() {
		cells = new ArrayList<Integer>();
	}
	
	public void addCell(Integer cell) {
		cells.add(cell);
	}

	public List<Integer> getCells() {
		return cells;
	}
}
