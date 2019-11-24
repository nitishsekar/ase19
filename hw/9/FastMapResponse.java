import java.util.HashSet;
import java.util.Set;

public class FastMapResponse {
    private Row pivot1;
    private Row pivot2;
    private Float distance;
    private Set<Integer> indices;
    private Float medianCosineDistance;

    public FastMapResponse() {
        this.pivot1 = new Row();
        this.pivot2 = new Row();
        this.distance = -1.0f;
        this.indices = new HashSet<>();
        this.medianCosineDistance = 0.0f;
    }

    public FastMapResponse(Row pivot1, Row pivot2, Float distance) {
        this.pivot1 = pivot1;
        this.pivot2 = pivot2;
        this.distance = distance;
    }

    public Row getPivot1() {
        return pivot1;
    }

    public Row getPivot2() {
        return pivot2;
    }

    public float getDistance() {
        return distance;
    }

    public Set<Integer> getIndices() {
        return indices;
    }

    public void setIndices(Set<Integer> indices) {
        this.indices = indices;
    }

    public Float getMedianCosineDistance() {
        return medianCosineDistance;
    }

    public void setMedianCosineDistance(Float medianCosineDistance) {
        this.medianCosineDistance = medianCosineDistance;
    }
}
