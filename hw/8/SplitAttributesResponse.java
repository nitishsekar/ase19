import java.util.ArrayList;
import java.util.List;

public class SplitAttributesResponse {
	private List<Col> featureRanges;
	private List<Col> labelRanges;
	private List<List<Integer>> indicesLists;
	
	public SplitAttributesResponse(List<Col> featureRanges, List<Col> labelRanges, List<List<Integer>> indicesLists) {
		this.featureRanges = featureRanges;
		this.labelRanges = labelRanges;
		this.indicesLists = indicesLists;
	}
	
	public SplitAttributesResponse() {
		this.featureRanges = new ArrayList<>();
		this.labelRanges = new ArrayList<>();
		this.indicesLists = new ArrayList<>();
	}
	
	public List<Col> getFeatureRanges() {
		return featureRanges;
	}
	public void setFeatureRanges(List<Col> featureRanges) {
		this.featureRanges = featureRanges;
	}
	public List<Col> getLabelRanges() {
		return labelRanges;
	}
	public void setLabelRanges(List<Col> labelRanges) {
		this.labelRanges = labelRanges;
	}
	public List<List<Integer>> getIndicesLists() {
		return indicesLists;
	}
	public void setIndicesLists(List<List<Integer>> indicesLists) {
		this.indicesLists = indicesLists;
	}
}
