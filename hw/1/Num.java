import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Num extends Col {

    public List<Integer> integerList;
    public List<Double> meanList;
    public List<Double> sdList;
    public Integer prevSum;
    public Integer count;
        
    public Num() {
    	integerList = new ArrayList<>();
        meanList = new ArrayList<>();
        sdList = new ArrayList<>();
        prevSum = 0;
        count = 0;
    }

    public List<Integer> generateList() {
        Random random = new Random();
        List<Integer> integerList = new ArrayList<>();

        return integerList;
    }

    public double generateMean(double prevSum, int count) {
        return (prevSum/count);
    }

    public double generateSD(List<Integer> integerList, double mean, int count) {
        float variance = 0;
        for (int i = 0; i < count; i++) {
            variance += Math.pow(integerList.get(i)-mean, 2);
        }
        return Math.sqrt(variance)/count;
    }

    public void updateMeanAndSD(Integer arg) throws IOException {
        integerList.add(arg);
        count++;
        prevSum += arg;
        if(count % 10 == 0){
            double mean = generateMean(prevSum, count);
            meanList.add(mean);
            sdList.add(generateSD(integerList, mean, count));
            System.out.println("Count is "+count+"\nMean: "+meanList.get((count/10)-1)+"; SD: "+sdList.get((count/10)-1));
        }

    }
    public void deleteNum() throws IOException {
        Integer arg = integerList.get(integerList.size()-1);
        integerList.remove(integerList.size()-1);
        prevSum-=arg;
        count--;
        if(count%10 == 0){
            double mean = generateMean(prevSum, count);
            System.out.println("Count is "+count+"\nMean: "+mean+"; SD: "+generateSD(integerList, mean, count));
            if(meanList.get((count/10)-1).equals(mean)) {
                System.out.println("Mean matches at count "+count);
            }

            if(sdList.get((count/10)-1).equals(generateSD(integerList, mean, count))) {
                System.out.println("SD matches at count "+count);
                System.out.println();
            }
        }
    }
}
