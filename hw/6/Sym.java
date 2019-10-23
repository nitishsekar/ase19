import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sym extends Col{
    List<String> words;
    Map<String, Integer> colMap;
    Integer totalCount;
    String mode;
    Integer modeCount;
    private static final float M = 0.001f;
    double entropyList;
    
    public Sym() {
    	this.words = new ArrayList<>();
    	this.colMap = new HashMap<>();
    	this.totalCount = 0;
    	this.modeCount = Integer.MIN_VALUE;
    }
    
    public Sym(Sym sym) {
    	super(sym);
    	this.words = new ArrayList<>();
    	for(String s:sym.words) this.words.add(s);
    	this.colMap = new HashMap<>();
    	for(String s:sym.colMap.keySet()) this.colMap.put(s, sym.colMap.get(s));
    	this.totalCount = sym.totalCount;
    	this.mode = sym.mode;
    	this.modeCount = sym.modeCount;
    	this.entropyList = sym.entropyList;
    }

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
        	int newTotal = 1;
            colMap.put(colVal, newTotal);
            totalCount++;
            words.add(colVal);
            if(newTotal > modeCount) {
            	modeCount = newTotal;
            	mode = colVal;
            }
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
            if (entry.getValue() == 0) continue;
            double pi = ((double)entry.getValue()/totalCount);
            entropy = entropy + (-pi)*(Math.log(pi)/Math.log(2));
        }
        return entropy;
    }

    public float symLike(String val, float prior) {
        int freq = 0;
        //System.out.println(colMap.keySet()+" val: "+val);
        if(colMap.containsKey(val)) {
	        freq = colMap.get(val);
	        //System.out.println("Prior: "+prior+", Freq. "+freq+", total: "+totalCount);
	        return (float) (freq + M*prior)/(totalCount + M);
        } else {
        	return (float) (0 + M*prior)/(totalCount + M);
        }
    }

    public String deleteFirstSym() throws IOException {
        String arg = words.get(0);
        words.remove(0);
        totalCount--;
        for (Map.Entry<String,Integer> entry : colMap.entrySet()) {
            if (entry.getKey().equals(arg)) {
                entry.setValue(entry.getValue()-1);
            }
        }
        if (arg.equals(mode)) setMode();
        entropyList = entropyCalc();
        return arg;
    }

    public String deleteIthSym(int i) throws IOException {
        String arg = words.get(i);
        words.remove(i);
        totalCount--;
        for (Map.Entry<String,Integer> entry : colMap.entrySet()) {
            if (entry.getKey().equals(arg)) {
                entry.setValue(entry.getValue()-1);
            }
        }
        if (arg.equals(mode)) setMode();
        entropyList = entropyCalc();
        return arg;
    }
    
    public void setMode() {
        int maxValue = Integer.MIN_VALUE;
        for (Map.Entry<String,Integer> entry : colMap.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                modeCount = entry.getValue();
                mode = entry.getKey();
            }
        }
    }
    
    public double getEntropy() {
    	return entropyCalc();
    }

    public List<String> getValList() {
        return words;
    }

    public Integer getCount() {
        return totalCount;
    }
}