import java.util.ArrayList;
import java.util.List;

public class RPTreeProbe {
    private Row row;
    private List<Col> beforeLeafStats;
    private List<Col> afterLeafStats;

    public RPTreeProbe(Row row) {
        this.row = new Row(row);
        this.beforeLeafStats = new ArrayList<>();
        this.afterLeafStats = new ArrayList<>();
    }

    public Row getRow() {
        return row;
    }

    public List<Col> getBeforeLeafStats() {
        return beforeLeafStats;
    }

    public void setBeforeLeafStats(List<Col> beforeLeafStats) {
        this.beforeLeafStats = beforeLeafStats;
    }

    public List<Col> getAfterLeafStats() {
        return afterLeafStats;
    }

    public void setAfterLeafStats(List<Col> afterLeafStats) {
        this.afterLeafStats = afterLeafStats;
    }


}


