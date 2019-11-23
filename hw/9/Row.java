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
	
	public Float dominates(Row i, Row j, List<Col> goals) {
		Float z = 0.00001f;
		float s1 = z, s2 = z;
		int n = goals.size();
		for(int k=0; k<n; k++) {
			Col goal = goals.get(k);
			if(goal.getClass() == Num.class) {
				Num num = (Num) goal;
				Float a = Float.parseFloat(i.getCells().get(num.getPos()));
				Float b = Float.parseFloat(j.getCells().get(num.getPos()));
				a = num.norm(a, num);
				b = num.norm(b, num);
				//System.out.println(num.getWeight());
				//System.out.println(a+" "+b);
				s1 -= Math.pow(10, (num.getWeight()*((a-b)/n)));
				s2 -= Math.pow(10, (num.getWeight()*((b-a)/n)));
			}
			//System.out.println(s1+" "+s2);
		}
		return (s1/n - s2/n); // i dominates j if this value is < 0
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
