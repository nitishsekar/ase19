import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestRPTreeProbes {

    public static void main(String[] args) {
        Tbl tbl = new Tbl();
        tbl.read("C:\\Users\\satan\\OneDrive\\Desktop\\xomo10000.csv");
        RPTreeGenerator rpTG = new RPTreeGenerator();
        RPTree node = rpTG.generateRPTree(tbl);
        node.printTree(node);
        List<RPTree> leaves = rpTG.getLeaves(node);
        List<RPTreeProbe> rpTreeProbes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            int randomLeafIndex = (int) (leaves.size()*Math.random());
            RPTree leaf = leaves.get(randomLeafIndex);
            List<Row> leafNodeRows = leaf.getRows();
            int randomChildIndex = (int) (leafNodeRows.size()*Math.random());
            RPTreeProbe rpTreeProbe = new RPTreeProbe(leafNodeRows.get(randomChildIndex));
            List<Col> beforeLeafStats = new ArrayList<>(leaf.getLeafStats());
            rpTreeProbe.setBeforeLeafStats(beforeLeafStats);
            rpTreeProbes.add(rpTreeProbe);
        }
        System.out.println("Hi");
    }
}
