import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestAnomalousRowAdd {

    public static void main(String[] args) {
        Tbl tbl = new Tbl();
        tbl.read("C:\\Users\\satan\\OneDrive\\Desktop\\xomo10000.csv");
        int rc = tbl.getRowCount();
        List<Integer> vals = new ArrayList<>();
        for(int i = 0; i<rc; i++) {
            vals.add(i);
        }
        Collections.shuffle(vals);
        List<Row> randomRows = new ArrayList<>();
        List<List<String>> randomFile = new ArrayList<>();
        randomFile.add(tbl.getFile().get(0));
        for(int i = 0; i<500; i++) {
            randomRows.add(tbl.getRows().get(vals.get(i)));
            randomFile.add(tbl.getFile().get(vals.get(i)+1));
        }
        try {
            Tbl newTbl = new Tbl(tbl,randomRows,randomFile);
            RPTreeGenerator rpTG = new RPTreeGenerator();
            RPTree node = rpTG.generateRPTree(newTbl);
            node.printTree(node);
            System.out.println("\n");
            for(int i = 500; i<5000; i++) {
                Row newRow = tbl.getRows().get(vals.get(i));
                RPTreeGenerator.Anomalous anomalous = new RPTreeGenerator.Anomalous();
                anomalous.setAnomalous(false);
                node = rpTG.addAnomalousRows(node, newTbl, newRow, anomalous);
            }
            node.printTree(node);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }





    }


}
