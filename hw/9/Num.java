import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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
    	super();
    	valList = new ArrayList<>();
        meanList = new ArrayList<>();
        sdList = new ArrayList<>();
        prevSum = 0.0f;
        count = 0;
        hi = -Float.MAX_VALUE;
        low = Float.MAX_VALUE;
    }
    
    public Num(Num num) {
    	super(num);
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
		if(arg == null) {
        	arg = 0.0f;
        }
        valList.add(arg);
        count++;
        prevSum += arg;
        if(arg > hi) {
        	hi = arg;
        }
        if(arg < low) {
        	low = arg;
        }
        
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

    public void clearAllVals() {
    	this.valList.clear();
    	this.meanList.clear();
    	this.sdList.clear();
    	this.prevSum = 0.0f;
		this.count = 0;
		this.hi = -Float.MAX_VALUE;
		this.low = Float.MAX_VALUE;
		this.mean = 0.0;
		this.stdDev = 0.0;
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
        	//System.out.println("DEBUG SD: list count: "+integerList.size()+", count: "+count+" mean:"+mean+"\n\t"+integerList);
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
	
	// Distance function for Nums
	public Float dist(Num n, String a, String b) {
		Float x = 0.0f, y = 0.0f;
		if("".equals(a)) {
			if("".equals(b))
				return 1.0f;
			y = norm(Float.parseFloat(b),n);
			x = y > 0.5f ? 0.0f : 1.0f;
    	} else {
    		x = norm(Float.parseFloat(a),n);
    		if("".equals(b))
    			y = x > 0.5f ? 0.0f : 1.0f;
    		else
    			y = norm(Float.parseFloat(b),n);
    	}
		return Math.abs(y-x);
    }
	
	public Float norm(Float x, Num n) {
		return (float) ((x-n.getLow())/(n.getHi() - n.getLow() + Math.pow(10, -32)));
	}
	
	/* Similarity tests - For "Same" score */
	public boolean same(Num num1, Num num2, Double conf, Double small) {
		return tTestSame(num1, num2, conf) || hedges(num1, num2, small);
	}
	
	public boolean tTestSame(Num num1, Num num2, Double conf) {
		Double nom = Math.abs(num1.getMean() - num2.getMean());
		Double s1 = num1.getStdDev();
		Double s2 = num2.getStdDev();
		Double denom;
		if(s1 > 0 && s2 > 0) {
			denom = Math.sqrt((s1/num1.getCount()) + (s2/num2.getCount()));
		} else {
			denom = 1.0;
		}
		int df = Math.min(num1.getCount() - 1, num2.getCount() - 1);
		return criticalValue(df, conf) >= nom/denom;
	}
	 
	public Double criticalValue(int df, Double conf) {
		int[] xsArr = new int[]{ 1,2,5,10,15,20,25,30,60,100 }; 
		List<Integer> xs = new ArrayList<>();
		for(int i=0; i<10; i++) {
			xs.add(xsArr[i]);
		}
		HashMap<Double, List<Double>> cvMap = new HashMap<>();
		double[] ysArr1 = new double[]{ 3.078, 1.886, 1.476, 1.372, 1.341, 1.325, 1.316, 1.31,  1.296, 1.29 }; 
		List<Double> ys1 = new ArrayList<>();
		for(int i=0; i<10; i++) {
			ys1.add(ysArr1[i]);
		}
		double[] ysArr2 = new double[]{ 6.314, 2.92,  2.015, 1.812, 1.753, 1.725, 1.708, 1.697, 1.671, 1.66 }; 
		List<Double> ys2 = new ArrayList<>();
		for(int i=0; i<10; i++) {
			ys2.add(ysArr2[i]);
		}
		double[] ysArr3 = new double[]{ 31.821, 6.965, 3.365, 2.764, 2.602, 2.528, 2.485, 2.457, 2.39,  2.364 }; 
		List<Double> ys3 = new ArrayList<>();
		for(int i=0; i<10; i++) {
			ys3.add(ysArr3[i]);
		}
		cvMap.put(0.9,ys1);
		cvMap.put(0.95,ys2);
		cvMap.put(0.99,ys3);
		return interpolate(df, xs, cvMap.get(conf));
	}
	
	public Double interpolate(int df, List<Integer> xs, List<Double> cvVals) {
		if(df <= xs.get(0)) 
			return cvVals.get(0);
		if(df >= xs.get(xs.size()-1)) 
			return cvVals.get(xs.size()-1);
		int x0 = xs.get(0);
		Double y0 = cvVals.get(0);
		int x1 = x0; Double y1 = y0;
		for(int i = 1; i < xs.size(); i++) {
			x1 = xs.get(i);
			y1 = cvVals.get(i);
			if((df < x0) || (df > xs.get(xs.size()-1)) || ((x0 <= df) && (df < x1))) {
				break;
			}
			x0 = x1;
			y0 = y1;
		}
		int gap = (df-x0)/(x1-x0);
		return y0 + gap*(y1-y0);
	}
	
	public boolean hedges(Num num1, Num num2, Double small) {
		
		/*
	    Hedges effect size test.
	    Returns true if the "i" and "j" difference is only a small effect.
	    "i" and "j" are   objects reporing mean (i.mu), standard deviation (i.s) 
	    and size (i.n) of two  population of numbers.
	    */
		Double num   = (num1.getCount() - 1)*Math.pow(num1.getStdDev(),2) + (num2.getCount() - 1)*Math.pow(num2.getStdDev(),2);
	    int denom = (num1.getCount() - 1) + (num2.getCount() - 1);
	    Double sp = Math.sqrt(num/denom);
	    Double delta = Math.abs(num1.getMean() - num2.getMean()) / sp;  
	    Double c = 1 - 3.0 / (4*((num1.getCount() - 1) + (num1.getCount() - 1)) - 1);
	    return delta * c < small;
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
