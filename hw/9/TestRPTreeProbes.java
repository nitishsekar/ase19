import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestRPTreeProbes {

    public static void main(String[] args) {
        Double CONF = 0.95;
        Double SMALL = 0.38;
        Tbl tbl = new Tbl();
        tbl.read("C:\\Users\\satan\\OneDrive\\Desktop\\pom310000.csv");
        RPTreeGenerator rpTG = new RPTreeGenerator();
        Tbl newTbl = new Tbl(tbl);
        RPTree node = rpTG.generateRPTree(newTbl);
        node.printTree(node);
        System.out.println();
        List<RPTree> leaves = rpTG.getLeaves(node);
        List<RPTreeProbe> rpTreeProbes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int randomLeafIndex = (int) (leaves.size() * Math.random());
            RPTree leaf = leaves.get(randomLeafIndex);
            List<Row> leafNodeRows = leaf.getRows();
            int randomChildIndex = (int) (leafNodeRows.size() * Math.random());
            RPTreeProbe rpTreeProbe = new RPTreeProbe(leafNodeRows.get(randomChildIndex));
            List<Col> beforeLeafStats = new ArrayList<>(leaf.getLeafStats());
            rpTreeProbe.setBeforeLeafStats(beforeLeafStats);
            rpTreeProbes.add(rpTreeProbe);
        }
        int ctr = 0, trueCtr = 0;
        int found = 0;

        for (int k = 0; k < 20; k++) {
            newTbl = new Tbl(tbl);
            RPTree newNode = rpTG.generateRPTree(newTbl);
            leaves = rpTG.getLeaves(newNode);
            for (int i = 0; i < 100; i++) {
                RPTreeProbe probe = rpTreeProbes.get(i);
                int id = probe.getRow().getIndex();
                for (RPTree leaf : leaves) {
                    if (leaf.getIndicesSet().contains(id)) {
                        found++;
                        List<Col> after = new ArrayList<>(leaf.getLeafStats());
                        probe.setAfterLeafStats(after);
                        break;
                    }
                }

                boolean same = true;
                List<Col> before = probe.getBeforeLeafStats();
                for (int j = 0; j < before.size(); j++) {
                    Col c = before.get(j);
                    Num n = (Num) c;
                    same = same && n.same(n, (Num) probe.getAfterLeafStats().get(j), CONF, SMALL);
                    if (!same)
                        break;
                }
                if (same) {
                    trueCtr++;
                }
                ctr++;

            }
        }
        Double baseline = (double) trueCtr / ctr;

        System.out.println("Found is " + found);
        System.out.println("Baseline: " + baseline + "\n");
        System.out.print("Running incremental RP tree: ");
        ctr = 0;
        trueCtr = 0;
        found = 0;

        for (int k = 0; k < 20; k++) {
            newTbl = new Tbl(tbl);
            int rc = newTbl.getRowCount();
            List<Integer> vals = new ArrayList<>();
            for (int i = 0; i < rc; i++) {
                vals.add(i);
            }
            Collections.shuffle(vals);
            List<Row> randomRows = new ArrayList<>();
            List<List<String>> randomFile = new ArrayList<>();
//            System.out.println("ROWS: "+newTbl.getRowCount()+" FILE: "+newTbl.getFile().size());
            randomFile.add(newTbl.getFile().get(0));
            for (int i = 0; i < 500; i++) {
                randomRows.add(newTbl.getRows().get(vals.get(i)));
                randomFile.add(newTbl.getFile().get(vals.get(i)+1));
            }
//            System.out.println(randomFile);
            RPTree incNode = new RPTree();
            try {
                Tbl newTbl2 = new Tbl(newTbl, randomRows, randomFile);
                incNode = rpTG.generateRPTree(newTbl2);
                for (int i = 500; i < tbl.getRowCount(); i++) {
                    Row newRow = newTbl.getRows().get(vals.get(i));
                    RPTreeGenerator.Anomalous anomalous = new RPTreeGenerator.Anomalous();
                    anomalous.setAnomalous(false);
                    incNode = rpTG.addAnomalousRows(incNode, newTbl2, newRow, anomalous);
                }
                // node.printTree(node);
            } catch (Exception e) {
                System.out.print("ERROR: ");
                e.printStackTrace();
                //System.out.println(e.getMessage());
            }

            leaves = rpTG.getLeaves(incNode);
            for (int i = 0; i < 100; i++) {
                RPTreeProbe probe = rpTreeProbes.get(i);
                int id = probe.getRow().getIndex();
                for (RPTree leaf : leaves) {
                    if (leaf.getIndicesSet().contains(id)) {
                        found++;
                        List<Col> after = new ArrayList<>(leaf.getLeafStats());
                        probe.setAfterLeafStats(after);
                        boolean same = true;
                        List<Col> before = probe.getBeforeLeafStats();
                        for (int j = 0; j < before.size(); j++) {
                            Col c = before.get(j);
                            Num n = (Num) c;
                            same = same && n.same(n, (Num) probe.getAfterLeafStats().get(j), CONF, SMALL);
                            if (!same)
                                break;
                        }
                        if (same) {
                            trueCtr++;
                        }
                        ctr++;
                        break;
                    }
                }
            }
        }
        Double incScore = (double) trueCtr / ctr;

        System.out.println("Found is " + found);
        System.out.println("Incremental Score: " + incScore + "\n");


    }
}
