import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SplitAttributes {
	private List<Col> xRanges;
	private List<Col> yRanges;
	
	// Used when one of the Cols is a Sym (For DT)
	private List<Col> ySymRanges;
	private int minSplit;
	private static final float TRIVIAL = 1.025f;
	private static final float COHEN = 0.3f;
	private static final float MIN = 0.5f;
	private static final float DULL = 0.147f;
	private static final float M = 10^(-64);
	private List<Integer> indices;
	private List<List<Integer>> indexRanges;
	private float epsilon;

	public SplitAttributes() {
		xRanges = new ArrayList<>();
		yRanges = new ArrayList<>();
		ySymRanges = new ArrayList<>();
		indices = new ArrayList<>();
		indexRanges = new ArrayList<>();
		minSplit = 0;
		epsilon = 0;
	}
	
	public void clear() {
		xRanges = new ArrayList<>();
		yRanges = new ArrayList<>();
		ySymRanges = new ArrayList<>();
		indices = new ArrayList<>();
		indexRanges = new ArrayList<>();
		minSplit = 0;
		epsilon = 0;
	}

	public void identifySplit(List<String> list, String yis) throws IOException {
        if ("Num".equals(yis)) getNumSplit(list);
        if ("Sym".equals(yis)) getSymSplit(list);
    }

    public void getSymSplit(List<String> list) throws IOException {
        PriorityQueue<YZ> pq = new PriorityQueue<YZ>(new YZComparator());
        minSplit = (int) Math.sqrt(list.size());
        for(String s: list) {
        	String[] strings = s.replaceAll("[\\[\\]]","").split(",");
            YZ yz = new YZ(Float.valueOf(strings[0].trim()), strings[1].trim().replaceAll("[\\']", ""));
            pq.add(yz);
        }
        Num xNum = new Num();
        Sym ySym = new Sym();
        while (!pq.isEmpty()) {
            YZ yz = pq.poll();
            xNum.updateMeanAndSD(yz.getY());
            ySym.addSymbol(yz.getZ());
        }
        epsilon = (float) (COHEN*ySym.getEntropy());
        findSymSplits(xNum, ySym);
        printSymSplits();
    }
    
    public void getNumSplit(List<String> list) throws IOException {
      PriorityQueue<XY> pq = new PriorityQueue<XY>(new XYComparator());
      minSplit = (int) Math.sqrt(list.size());
      for(String s: list) {
      	String[] strings = s.replaceAll("[\\[\\]]","").split(",");
          XY xy = new XY(Float.valueOf(strings[0]), Float.valueOf(strings[1]));
          pq.add(xy);
      }
      Num xNum = new Num();
      Num yNum = new Num();
      while (!pq.isEmpty()) {
          XY xy = pq.poll();
          xNum.updateMeanAndSD(xy.getX());
          yNum.updateMeanAndSD(xy.getY());
      }
      epsilon = (float) (COHEN*yNum.getStdDev());
      findNumSplits(xNum, yNum);
      printSplits();
    }

    public void findNumSplits(Num x, Num y) {
    	try {
			Num xR = new Num(x);
			Num yR = new Num(y);
			Num xL = new Num();
			Num yL = new Num();
			
			Num cutXR = new Num();
			Num cutYR = new Num();
			Num cutXL = new Num();
			Num cutYL = new Num();
			
			Double best = y.getStdDev();
			int n = yR.getCount();
			
			boolean cut = false;
			int cutLoc = -1;
			List<Float> list = y.getValList();
			float start = list.get(0);
			float stop = list.get(list.size()-1);
			for(int i=0; i<n; i++) {
				if(n-i-1 >= minSplit) {
					float val = xR.deleteFirstNum();
					xL.updateMeanAndSD(val);
					float yVal = yR.deleteFirstNum();
					yL.updateMeanAndSD(yVal);
					if(i > minSplit-1) {
						if(yVal == yR.getValList().get(0)) continue;
						// Check if a cut satisfies all criteria
						if(Math.abs(yL.getMean() - yR.getMean()) >= epsilon) {
							if((yR.getValList().get(0)-start >= epsilon))
								if(stop-yVal >= epsilon){
									Double expect = expectedValue(yL, yR);
									if(expect*TRIVIAL < best) {
										//System.out.println("DEBUG: Best:"+best+", new Best:"+expect+", Cut:"+i);
										best = expect;
										cut = true;
										cutLoc = i;
										cutXL = new Num(xL);
										cutXR = new Num(xR);
										cutYL = new Num(yL);
										cutYR = new Num(yR);
										
									}
								}
						}
					}
				}
			}
			if(cut) {
				// Recurse if cut exists
				findNumSplits(cutXL, cutYL);
				findNumSplits(cutXR, cutYR);
			} else {
				// Else, record the split
				xRanges.add(x);
				yRanges.add(y);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void findSymSplits(Num x, Sym y) {
    	try {
			Num xR = new Num(x);
			Sym yR = new Sym(y);
			Num xL = new Num();
			Sym yL = new Sym();

			Num cutXR = new Num();
			Sym cutYR = new Sym();
			Num cutXL = new Num();
			Sym cutYL = new Sym();
			
			Double best = y.getEntropy();
			int n = yR.getTotalCount();
			boolean cut = false;
			int cutLoc = -1;
			List<String> list = y.getWords();
			String start = list.get(0);
			String stop = list.get(list.size()-1);
			for(int i=0; i<n; i++) {
				if(n-i-1 >= minSplit) {
					float val = xR.deleteFirstNum();
					xL.updateMeanAndSD(val);
					String yVal = yR.deleteFirstSym();
					yL.addSymbol(yVal);
					if(i > minSplit-1) {
						if (yR.totalCount != 0) {
							if(yVal.equals(yR.getWords().get(0))) continue;
							// Check if a cut satisfies all criteria
							if(Math.abs(getASCII(yL.getMode()) - getASCII(yR.getMode())) >= epsilon) {
								if((getASCII(yR.getValList().get(0)) - getASCII(start) >= epsilon) &&
										(getASCII(stop) - getASCII(yVal) >= epsilon)){
									Double expect = expectedValueSym(yL, yR);
									if(expect*TRIVIAL < best) {
										best = expect;
										cut = true;
										cutLoc = i;
										cutXL = new Num(xL);
										cutXR = new Num(xR);
										cutYL = new Sym(yL);
										cutYR = new Sym(yR);

									}
								}
							}
						}
					}
				}
			}
			if(cut) {
				// Recurse if cut exists
				findSymSplits(cutXL, cutYL);
				findSymSplits(cutXR, cutYR);
			} else {
				// Else, record the split
				xRanges.add(x);
				ySymRanges.add(y);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
   
    
    public void printSplits() {
    	for(int i=0; i<xRanges.size(); i++) {
    		Num x = (Num) xRanges.get(i);
    		Num y = (Num) yRanges.get(i);
    		System.out.println(String.format(" %d x.n %5d | x.lo %10.5f  x.hi %10.5f | y.lo %10.5f  y.hi %10.5f  y.sd %10.5f", 
    				i+1, 
    				x.getCount(), 
    				x.getLow(), 
    				x.getHi(), 
    				y.getLow(), 
    				y.getHi(),
    				y.getStdDev()));
    		}
    }

	public void printSymSplits() {
		for(int i=0; i<xRanges.size(); i++) {
			Num x = (Num) xRanges.get(i);
			Sym y = (Sym) ySymRanges.get(i);
			System.out.println(String.format(" %d x.n %5d | x.lo %10.5f  x.hi %10.5f  sd %10.5f | y.mode %s  y.ent %10.5f",
					i+1,
					x.getCount(),
					x.getLow(),
					x.getHi(),
					x.getStdDev(),
					y.getMode(),
					y.getEntropy()));
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
    	System.out.println("DEBUG: GT "+gt+" LT "+lt+" Cliffs Delta "+Math.abs(gt - lt)/(a.size()*b.size()));
    	return Math.abs(gt - lt)/(a.size()*b.size()) <= DULL;
    }
    
    private Double expectedValue(Num l, Num r) {
    	int n = l.getCount() + r.getCount();
    	//return (((l.getCount()/n*l.getStdDev()) + (r.getCount()/n*r.getStdDev())));
    	return ((float)l.getCount()/n*l.getStdDev() + (float)r.getCount()/n*r.getStdDev());
    }

	private Double expectedValueSym(Sym l, Sym r) {
		int n = l.getCount() + r.getCount();
		return ((l.getCount()/n * (l.getEntropy()+M)) + (r.getCount()/n * (r.getEntropy()+M)));
	}

	public int getASCII(String string) {
		return (int)(string.charAt(0));
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

        public float getY() {
            return y;
        }
    }


}
