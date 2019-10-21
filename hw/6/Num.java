import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Num extends Col implements Cloneable {

    private List<Float> valList;
    private List<Double> meanList;
    private List<Double> sdList;
    private Float prevSum;
    private Integer count;
    private Double mean;
    private Double stdDev;
    private Float hi;
    private Float low;
    
    public Num() {
    	valList = new ArrayList<>();
        meanList = new ArrayList<>();
        sdList = new ArrayList<>();
        prevSum = 0.0f;
        count = 0;
        hi = Float.MIN_VALUE;
        low = Float.MAX_VALUE;
    }
    
    public Num(Num num) {
    	this.valList = new ArrayList<>();
    	for(Float f:num.valList) this.valList.add(f);
    	this.meanList = new ArrayList<>();
    	for(Double f:num.meanList) this.meanList.add(f);
    	this.meanList = num.meanList;
    	this.sdList = new ArrayList<>();
    	for(Double f:num.sdList) this.sdList.add(f);
    	this.prevSum = num.prevSum;
    	this.count = num.count;
    	this.mean = num.mean;
    	this.stdDev = num.stdDev;
    	this.hi = num.hi;
    	this.low = num.low;
    }
    
    public Object clone() throws
	    CloneNotSupportedException 
	{ 
    	return super.clone(); 
	} 
    
    public List<Float> getValList() {
		return valList;
	}

	public void updateMeanAndSD(Float arg) throws IOException {
        valList.add(arg);
        count++;
        prevSum += arg;
        if(arg > hi) hi = arg;
        if(arg < low) low = arg;
        mean = generateMean(prevSum, count);
        stdDev = generateSD(valList, mean, count);
        //System.out.println("ValList: "+valList+", mean: "+mean+", count: "+count);
        //System.out.println("SD: "+stdDev);
        if(count % 10 == 0){
            meanList.add(mean);
            sdList.add(stdDev);
        }

    }
    public Integer getCount() {
		return count;
	}

	public Double getMean() {
		return mean;
	}

	public Double getStdDev() {
		return stdDev;
	}

	public Float getHi() {
		return hi;
	}

	public Float getLow() {
		return low;
	}

	public void deleteNum() throws IOException {
        Float arg = valList.get(valList.size()-1);
        valList.remove(valList.size()-1);
        prevSum-=arg;
        count--;
        double meanV = generateMean(prevSum, count);
        mean = meanV;
        double sd = generateSD(valList, mean, count);
        stdDev = sd;
        if(arg == low) setLow();
        if(arg == hi) setHi();
    }
	
	public float deleteFirstNum() throws IOException {
        Float arg = valList.get(0);
        valList.remove(0);
        prevSum-=arg;
        count--;
        double meanV = generateMean(prevSum, count);
        mean = meanV;
        double sd = generateSD(valList, mean, count);
        stdDev = sd;
        if(low.equals(arg)) {
        	//System.out.println("DEBUG: arg "+arg+" low "+low);
        	setLow();
        }
        else if(hi.equals(arg)) setHi();
        return arg;
    }
	
	public float deleteIthNum(int i) throws IOException {
		Float arg = valList.get(i);
        valList.remove(i);
        prevSum-=arg;
        count--;
        double meanV = generateMean(prevSum, count);
        mean = meanV;
        double sd = generateSD(valList, mean, count);
        stdDev = sd;
        if(low.equals(arg)) {
        	//System.out.println("DEBUG: arg "+arg+" low "+low);
        	setLow();
        }
        else if(hi.equals(arg)) setHi();
        return arg;
    }
    
    private List<Integer> generateList() {
        Random random = new Random();
        List<Integer> integerList = new ArrayList<>();
        return integerList;
    }

    private double generateMean(double prevSum, int count) {
        return (prevSum/count);
    }

	private double generateSD(List<Float> integerList, double mean, int count) {
        float variance = 0;
//        /System.out.println("DEBUG SD: list count: "+integerList.size()+", count: "+count);
        if(count > 1) {
	        for (int i = 0; i < integerList.size(); i++) {
	            variance += Math.pow(integerList.get(i)-mean, 2);
	        }
	        return Math.sqrt(variance/(count-1));
	    } else {
	    	return 0;
	    }
    }
	
	 public float numLike(float val) {
		//System.out.println("Mean is "+mean+" and SD is "+stdDev); 
        float numerator = (float) Math.pow(2.7128, (-Math.pow(val - mean, 2)/(2*Math.pow(stdDev, 2) + 0.0001)));
        //System.out.println(numerator);
        float denominator = (float) Math.pow(3.14159*2*Math.pow(stdDev, 2), 0.5);
        //System.out.println(denominator);
        return (float) (numerator/(denominator + Math.pow(10, -64)));
    }
	 
	private void setLow() {
		float min = Float.MAX_VALUE;
		for(float f: valList) {
			if(f < min)
				min = f;
		}
		low = min;
	}
	
	private void setHi() {
		float max = Float.MIN_VALUE;
		for(float f: valList) {
			if(f > max)
				max = f;
		}
		hi = max;
	}

}
