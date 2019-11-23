import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class GlobalLearning {
    public void printRankedRowsToFile(Tbl tbl,String fileName) {
        System.out.println("Goals are rows "+tbl.getMy().getGoals()+"\nMinimize: "+tbl.getMy().getW());
        int rc = tbl.getRowCount();
        My my = tbl.getMy();
        List<Col> leafStats = new ArrayList<>();
        for(Integer i:my.getGoals()) {
            Col c = tbl.getCols().get(i-1);
            leafStats.add(c);
        }
        List<Integer> domCount = new ArrayList<>();
        for(int i=0; i<rc; i++) {
            int count = 0;
            Row ri = tbl.getRows().get(i);
            for(int j=0; j<rc; j++) {
                Row rj = tbl.getRows().get(j);
                if(i != j) {
                    float dom = ri.dominates(ri, rj, leafStats);
                    if(dom < 0) {
                        count++;
                    }
                }
            }
            domCount.add(count);
        }
        List<Row> rankedRows = new ArrayList<>();
        PriorityQueue<TestDominate.XY> pq = new PriorityQueue<TestDominate.XY>(new TestDominate.XYComparator());
        int k = 0;
        for(Row r: tbl.getRows()) {
            TestDominate.XY xy = new TestDominate.XY(domCount.get(k), r);
            pq.add(xy);
            k++;
        }
        for(int i=0; i<rc; i++) {
            TestDominate.XY xy = pq.poll();
            Row r = xy.getRow();
            rankedRows.add(i, r);
            domCount.set(i, xy.getDom());
        }
        List<String> headers = new ArrayList<String>();
        for (Col col : tbl.getCols()) {
            headers.add(col.getTxt());
        }

        PrintWriter writer;
        System.out.println(headers);
        System.out.println("Top 20% rows:");
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
            for(int i=0; i<(int)(0.2*(rankedRows.size())); i++) {
                System.out.println(rankedRows.get(i).getCells());
                for (String string : rankedRows.get(i).getCells()) {
                    if (string.equals("")) {
                        sb.append("?");
                    }
                    else {
                        sb.append(string);
                    }
                    sb.append(',');
                }
                sb.append("best");
                sb.append('\n');
            }
            System.out.println("Bottom 80% rows:");
            for(int i=(int)(0.2*(rankedRows.size())); i<rankedRows.size(); i++) {
                System.out.println(rankedRows.get(i).getCells());
                for (String string : rankedRows.get(i).getCells()) {
                    if (string.equals("")) {
                        sb.append("?");
                    }
                    else {
                        sb.append(string);
                    }
                    sb.append(',');
                }
                sb.append("rest");
                sb.append('\n');
            }
            writer.write(sb.toString());
            writer.flush();
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
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
    static class XYComparator implements Comparator<TestDominate.XY> {
        public int compare(TestDominate.XY s1, TestDominate.XY s2) {
            if (s1.dom < s2.dom)
                return 1;
            else if (s1.dom > s2.dom)
                return -1;
            return 0;
        }
    }
}
