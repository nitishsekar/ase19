import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sym extends Col{
    List<String> words = new ArrayList<String>();
    Map<String, Integer> colMap = new HashMap<String, Integer>();
    Integer totalCount = 0;
    String mode;
    Integer modeCount = Integer.MIN_VALUE;

    public void addSymbol(String colVal) {
        if (colMap.containsKey(colVal)) {
        	int newTotal = colMap.get(colVal)+1;
            colMap.replace(colVal, newTotal);
            totalCount++;
            words.add(colVal);
            if(newTotal > modeCount) {
            	modeCount = newTotal;
            	mode = colVal;
            }
        }
        else {
            colMap.put(colVal, 1);
            totalCount++;
            words.add(colVal);
        }
        
    }

    public List<String> getWords() {
		return words;
	}

	public Map<String, Integer> getColMap() {
		return colMap;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public String getMode() {
		return mode;
	}

	public Integer getModeCount() {
		return modeCount;
	}

	public double entropyCalc() {
        double entropy = 0.0;
        for (Map.Entry<String,Integer> entry : colMap.entrySet()) {
            if (entry.getValue() > modeCount) {
                modeCount = entry.getValue();
                mode = entry.getKey();
            }
            double pi = ((double)entry.getValue()/totalCount);
            entropy = entropy + (-pi)*(Math.log(pi)/Math.log(2));
        }
        return entropy;
    }
}
