import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class TestDominate {

	public static void main(String[] args) {
		Tbl tbl = new Tbl();
		tbl.read("C:\\Users\\satan\\OneDrive\\Desktop\\auto.csv");
		System.out.println("Goals are rows "+tbl.getMy().getGoals()+"\nMinimize: "+tbl.getMy().getW());
		int rc = tbl.getRowCount();
		List<Integer> vals = new ArrayList<>();
		for(int i = 0; i<rc; i++) {
			vals.add(i);
		}
		Collections.shuffle(vals);
		List<Row> randomRows = new ArrayList<>();
		for(int i = 0; i<100; i++) {
			randomRows.add(tbl.getRows().get(vals.get(i)));
		}
		My my = tbl.getMy();
		List<Col> leafStats = new ArrayList<>();
		for(Integer i:my.getGoals()) {
			Col c = tbl.getCols().get(i-1);
			leafStats.add(c);
		}
		List<Integer> domCount = new ArrayList<>();
		for(int i=0; i<100; i++) {
			int count = 0;
			Row ri = randomRows.get(i);
			for(int j=0; j<100; j++) {
				Row rj = randomRows.get(j);
				if(i != j) {
					float dom = ri.dominates(ri, rj, leafStats);
					if(dom < 0) {
						count++;
					}
				}
			}
			domCount.add(count);
		}
		
		PriorityQueue<XY> pq = new PriorityQueue<XY>(new XYComparator());
		int k = 0;
        for(Row r: randomRows) {
        	XY xy = new XY(domCount.get(k), r);
            pq.add(xy);
            k++;
        }
        for(int i=0; i<100; i++) {
        	XY xy = pq.poll();
        	Row r = xy.getRow();
        	randomRows.set(i, r);
        	domCount.set(i, xy.getDom());
        }
        System.out.println("Top 4 rows:");
        for(int i=0; i<4; i++) {
        	System.out.println(randomRows.get(i).getCells());
        }
        System.out.println("Bottom 4 rows:");
        for(int i=96; i<100; i++) {
        	System.out.println(randomRows.get(i).getCells());
        }
        
       
	}
	
	static class XY {
        public int dom;
        public Row row;

        public XY(int dom, Row row) {
            this.dom = dom;
            this.row = row;
        }

        public int getDom() {
        	return dom;
        }
        
        public Row getRow() {
            return row;
        }
    }
	
	// Comparator for descending order
	static class XYComparator implements Comparator<XY> {
        public int compare(XY s1, XY s2) {
            if (s1.dom < s2.dom)
                return 1;
            else if (s1.dom > s2.dom)
                return -1;
            return 0;
        }
    }


}
