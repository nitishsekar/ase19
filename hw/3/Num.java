import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Num extends Col {

    private List<Integer> integerList;
    private List<Double> meanList;
    private List<Double> sdList;
    private Integer prevSum;
    private Integer count;
    private Double mean;
    private Double stdDev;
    private Integer hi;
    private Integer low;
    
    public Num() {
    	integerList = new ArrayList<>();
        meanList = new ArrayList<>();
        sdList = new ArrayList<>();
        prevSum = 0;
        count = 0;
        hi = Integer.MIN_VALUE;
        low = Integer.MAX_VALUE;
    }
    
    public void updateMeanAndSD(Integer arg) throws IOException {
        integerList.add(arg);
        count++;
        prevSum += arg;
        if(arg > hi) hi = arg;
        if(arg < low) low = arg;
        mean = generateMean(prevSum, count);
        stdDev = generateSD(integerList, mean, count);
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

	public Integer getHi() {
		return hi;
	}

	public Integer getLow() {
		return low;
	}

	public void deleteNum() throws IOException {
        Integer arg = integerList.get(integerList.size()-1);
        integerList.remove(integerList.size()-1);
        prevSum-=arg;
        count--;
        if(count%10 == 0){
            double mean = generateMean(prevSum, count);
            if(meanList.get((count/10)-1).equals(mean)) {
               // System.out.println("Mean matches at count "+count);
            }
        }
    }
    
    private List<Integer> generateList() {
        Random random = new Random();
        List<Integer> integerList = new ArrayList<>();
        return integerList;
    }

    private double generateMean(double prevSum, int count) {
        return (prevSum/count);
    }

	private double generateSD(List<Integer> integerList, double mean, int count) {
        float variance = 0;
        for (int i = 0; i < count; i++) {
            variance += Math.pow(integerList.get(i)-mean, 2);
        }
        return Math.sqrt(variance/(count-1));
    }

}
