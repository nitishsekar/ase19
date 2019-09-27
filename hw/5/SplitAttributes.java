import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class SplitAttributes {
	private List<Num> xRanges;
	private List<Num> yRanges;
	private List<Sym> ySymRanges;
	private int minSplit;
	private static final float TRIVIAL = 1.025f;
	private static final float COHEN = 0.3f;
	private static final float MIN = 0.5f;
	private static final float DULL = 0.147f;
		
	public SplitAttributes() {
		xRanges = new ArrayList<>();
		yRanges = new ArrayList<>();
		ySymRanges = new ArrayList<>();
		minSplit = 0;
	}

    public PriorityQueue<YZ> getSymSplit(List<String> list) {
//        PriorityQueue<XY> pq = new PriorityQueue<XY>(Integer.MAX_VALUE, new XYComparator());
        PriorityQueue<YZ> pq = new PriorityQueue<YZ>(1, new YZComparator());
        for(String s: list) {
        	String[] strings = s.replaceAll("[\\[\\]]","").split(",");
//            XY xy = new XY(Double.valueOf(strings[0]), Double.valueOf(strings[1]));
            YZ yz = new YZ(Float.valueOf(strings[0].trim()), strings[1].trim());
            pq.add(yz);
        }
        /*
        while (!pq.isEmpty()) {
            YZ yz = pq.poll();
            System.out.println(yz.getY()+","+yz.getZ());
        }
        */
        return pq;
    }
    
    public void getNumSplit(List<String> list) throws IOException {
      PriorityQueue<XY> pq = new PriorityQueue<XY>(new XYComparator());
      minSplit = (int) Math.sqrt(list.size());
      for(String s: list) {
      	String[] strings = s.replaceAll("[\\[\\]]","").split(",");
          XY xy = new XY(Float.valueOf(strings[0]), Float.valueOf(strings[1]));
          pq.add(xy);
      }
      /*
      while (!pq.isEmpty()) {
          XY xy = pq.poll();
          System.out.println(xy.getX()+","+xy.getY());
      }
      */
      Num xNum = new Num();
      Num yNum = new Num();
      while (!pq.isEmpty()) {
          XY xy = pq.poll();
          xNum.updateMeanAndSD(xy.getX());
          yNum.updateMeanAndSD(xy.getY());
      }
      findSplits(xNum, yNum);
    }

    public void findSplits(Num x, Num y) {
    	try {
			Num xR = (Num)x.clone();
			Num yR = (Num)y.clone();
			Num xL = new Num();
			Num yL = new Num();
			
			Num cutXR = new Num();
			Num cutYR = new Num();
			Num cutXL = new Num();
			Num cutYL = new Num();
			
			int n = xR.getCount();
			boolean cut = false;
			for(int i=0; i<n; i++) {
				float val = xR.deleteFirstNum();
				xL.updateMeanAndSD(val);
				if(i >= minSplit) {
					// Check if a cut satisfies all criteria
					
				}
			}
			if(cut) {
				// Recurse if cut exists
				findSplits(cutXL, cutYL);
				findSplits(cutXR, cutYR);
			} else {
				// Else, record the split
				xRanges.add(x);
				yRanges.add(y);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /* O(n^2) */
    private boolean cliffsDeltaNum(List<Float> a, List<Float> b) {
    	int n = 0, gt = 0, lt = 0;
    	for(float i: a) {
    		for(float j: b) {
    			n++;
    			if(i > j) gt++;
    			if(i < j) lt++;
    		}
    	}
    	return Math.abs(gt - lt)/n <= DULL;
    }
    
    static class XYComparator implements Comparator<XY> {
        public int compare(XY s1, XY s2) {
            if (s1.y < s2.y)
                return -1;
            else if (s1.y > s2.y)
                return 1;
            return 0;
        }
    }

    static class YZComparator implements Comparator<YZ> {
        public int compare(YZ s1, YZ s2) {
            return s1.z.compareTo(s2.z);
        }
    }

    static class XY {
        public float x;
        public float y;

        public XY(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
        	return x;
        }
        
        public float getY() {
            return y;
        }
    }

    static class YZ {
        public  float y;
        public String z;

        public YZ(float y, String z) {
            this.y = y;
            this.z = z;
        }

        public String getZ() {
            return z;
        }

        public double getY() {
            return y;
        }
    }
}
