import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class RPTreeGenerator {
	private static final Integer P = 2;
	private static final Integer N = 10;
	private Integer minSplit;
	private Integer rootCount;
	private List<RPTree> leaves;
	private Tbl centroidTbl;
	private Float magicAlpha = 0.5f;
	
	public RPTree generateRPTree(Tbl tbl) {
		minSplit = (int) Math.round(Math.sqrt(tbl.getRowCount()));
		return generateRPTreeGeneric(tbl);
	}
	
	private RPTree generateRPTreeForCluster(Tbl tbl, Integer min) {
		minSplit = min;
		return generateRPTreeGeneric(tbl);
	}
	
	private RPTree generateRPTreeGeneric(Tbl tbl) {
		Tbl newTbl = new Tbl(tbl);
		RPTree node = recurse(newTbl, 0);
		node.setRoot(true);
		My my = tbl.getMy();
		List<Col> leafStats = new ArrayList<>();
		for(Integer i:my.getGoals()) {
			Col c = tbl.getCols().get(i-1);
			leafStats.add(c);
		}
		node.setLeafStats(leafStats);
		return node;
	}
	
	public List<RPTree> getLeaves(RPTree r) {
		leaves = new ArrayList<>();
		recordLeaves(r);
		return leaves;
	}
	
	public List<RPTree> getEnviousClusters(List<RPTree> leaves) {
		this.leaves = leaves;
		recordLeafCentroids();
//		centroidTbl.dump();
		List<Row> leafCentroids = centroidTbl.getRows();
		List<Col> centCols = centroidTbl.getCols();
		float leastEnvy = Float.MAX_VALUE;
		int leastIndex = -1;
		float leastDomCount = Integer.MAX_VALUE;
		for(int i=0; i<leaves.size(); i++) {
			float envy = 0.0f;
			List<Float> centDoms = new ArrayList<>();
			List<Float> distVals = new ArrayList<>();
			float totalDom = 0.0f;
			float maxDom = -Float.MAX_VALUE;
			RPTree envyLeaf = new RPTree();
			RPTree leaf = leaves.get(i);
			for(int j=0; j<leaves.size(); j++) {
				if(j != i) {
					Row cent1 = leafCentroids.get(i);
					Row cent2 = leafCentroids.get(j);
					
					Float dist = rowDistance(cent1, cent2, leaf.getLeafStats());
					dist = (float) (dist / Math.sqrt(centCols.size()));
					Float dom = cent1.dominates(cent1, cent2, centCols);
					if (dom > maxDom) {
						maxDom = dom;
					}
					distVals.add(dist);
					centDoms.add(dom);
					totalDom += dom;
				} else {
					distVals.add(-1.0f);
					centDoms.add(0.0f);
				}
			}
			for(int j=0; j<leaves.size(); j++) {
				if(j != i) {
					Row cent2 = leafCentroids.get(j);
					float envyVal = envy(distVals.get(j), centDoms.get(j)/totalDom);
//					System.out.print(envyVal+" ");
//					System.out.println("Dist: "+distVals.get(j)+" Dom: "+centDoms.get(j)/totalDom+" Envy: "+envyVal);
					if(envyVal > envy) {
						envy = envyVal;
						envyLeaf = leaves.get(j);
					}
				}

			}

			if (maxDom < 0.0f) {
				leastIndex = i;
				System.out.println("Best cluster is "+i);
			}
			RPTree currLeaf = leaves.get(i);
			currLeaf.setEnvy(envyLeaf);
			leaves.set(i, currLeaf);
		}
		RPTree leastLeaf = leaves.get(leastIndex);
		leastLeaf.setEnvy(null);
		leaves.set(leastIndex, leastLeaf);
		return leaves;
	}
	
	private void recordLeafCentroids() {
		centroidTbl = new Tbl();
		List<Col> cols = leaves.get(0).getLeafStats();
		List<String> colNames = new ArrayList<>();
		for(int i=0; i<cols.size(); i++) {
			Col c = cols.get(i);
			colNames.add(c.getTxt());
		}
		centroidTbl.setCols(colNames);
		for(RPTree rp:leaves) {
			List<String> row = new ArrayList<>();
			for(Col c:rp.getLeafStats()) {
				if(c.getClass() == Num.class) {
					Num n = (Num) c;
					row.add(n.getMean().toString());
				} else if(c.getClass() == Sym.class) {
					Sym s = (Sym) c;
					row.add(s.getMode());
				}
			}
			centroidTbl.addRow(row);
		}
	}
	
	private Float envy(Float dist, Float dom) {
		return ((1-dist) - dom);
	}
	
	private void recordLeaves(RPTree r) {
		if(r.getChildren().size() == 0) {
			leaves.add(r);
		} else {
			for(RPTree rc:r.getChildren()) {
				recordLeaves(rc);
			}
		}
	}
	
	private RPTree recurse(Tbl tbl, int level) {
		RPTree rpNode = new RPTree();
		if(tbl.getRowCount() < 2*minSplit) {
			List<RPTree> children = new ArrayList<>();
			My my = tbl.getMy();
			List<Col> leafStats = new ArrayList<>();
			for(Integer i:my.getGoals()) {
				Col c = tbl.getCols().get(i-1);
				leafStats.add(c);
			}
			rpNode.setChildren(children);
			rpNode.setLeafStats(leafStats);
			rpNode.setLevel(level);
			rpNode.setSplitCount(tbl.getRowCount());
			Tbl leafTbl = new Tbl(tbl);
			rpNode.setRows(leafTbl.getRows());
			return rpNode;
		} else {
			FastMapResponse resp = getBestPivots(tbl);
			//System.out.println(resp.indices);
			Set<Integer> indices = resp.getIndices();
			Tbl newTbl = new Tbl(tbl);
			int i = 0, adj = 0;
			int count = tbl.getRowCount();
			while(i < count) {
				// Delete rows from the table if index not in Set rows
				if(!indices.isEmpty()) {
					if(!indices.contains(i)) {
						try {
							tbl.deleteRow(i-adj);
							adj++;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				i++;
			}
			i = 0; adj = 0;
			count = newTbl.getRowCount();
			while(i < count) {
				// Delete rows from the table if index not in Set rows
				if(!indices.isEmpty()) {
					if(indices.contains(i)) {
						try {
							newTbl.deleteRow(i-adj);
							adj++;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				i++;
			}
			int splitCount = tbl.getRowCount()+newTbl.getRowCount();
			RPTree child1 = recurse(tbl,level+1);
			RPTree child2 = recurse(newTbl,level+1);
			List<RPTree> children = new ArrayList<>();
			children.add(child1);
			children.add(child2);
			rpNode.setChildren(children);
			rpNode.setLevel(level);
			rpNode.setSplitCount(splitCount);
			rpNode.setFastMapResponse(resp);
			return rpNode;
		}
	}
	
	private FastMapResponse getBestPivots(Tbl tbl) {
		Integer delta = tbl.getRowCount();
		Integer split1 = 0, split2 = 0;
		FastMapResponse bestResp = new FastMapResponse();
		Set<Integer> bestIndices = new HashSet<>();
		Float bestMedian = 0.0f;
		for(int j = 0; j<N; j++) {
			FastMapResponse resp = fastMap(tbl);
			/*
			System.out.println("Pivot 1:\n\t"+resp.getPivot1().getCells()
					+"\nPivot 2:\n\t"+resp.getPivot2().getCells()+"\nDist: "
					+resp.getDistance());
			*/
			int i = 0;
			List<IndDistPair> distList = new ArrayList<>();
			List<Row> rows = tbl.getRows();
			List<Col> cols = tbl.getCols();
			for(Row r:rows) {
				//System.out.println(initRand+", "+i);
				float dist = cosDistance(resp.getPivot1(), resp.getPivot2(), r, resp.getDistance(), cols);
				//System.out.println(dist);
				IndDistPair ip = new IndDistPair(i, dist);
				distList.add(ip);
				i++;
			}
			Collections.sort(distList, new IndDistPairComparator());
			int size = distList.size();
			Float median;
			if(size%2 == 1) {
				median = distList.get((size+1)/2 - 1).getDistance();
			} else {
				Float val1 = distList.get((size)/2 - 1).getDistance();
				Float val2 = distList.get((size+2)/2 - 1).getDistance();
				median = (val1 + val2)/2;
			}
			Set<Integer> indices = new HashSet<>();
			i = 0;
			for(IndDistPair ip:distList) {
				if(ip.getDistance() < median) {
					indices.add(ip.getIndex());
				}
			}
			Integer newDelta = Math.abs(indices.size()-(size-indices.size()));
			if(newDelta < delta) {
				delta = newDelta;
				split1 = indices.size();
				split2 = size-indices.size();
				bestResp = resp;
				bestIndices = indices;
				bestMedian = median;
			}
		}
		//System.out.println("Best Pivot:\nDistance: "+bestResp.getDistance()+" Total: "+tbl.getRowCount()+" Split1: "+split1+" Split2: "+split2);
		bestResp.setIndices(bestIndices);
		bestResp.setMedianCosineDistance(bestMedian);
		return bestResp;
	}
	
	private FastMapResponse fastMap(Tbl tbl) {
		//tbl.dump();
		List<Row> rows = tbl.getRows();
		Random rand = new Random();
		boolean validIndex = false;
		Row initRow;
		int initRand;
		do {
			initRand = Math.round(rand.nextFloat()*(rows.size()-1));
			initRow = rows.get(initRand);
			if(initRow.getCells().size() > 0)
				validIndex = true;
		} while(!validIndex);
		//System.out.println(initRand+" "+initRow.getCells());
		List<Col> cols = tbl.getCols();
		int i = 0;
		List<IndDistPair> distList = new ArrayList<>();
		for(Row r:rows) {
			//System.out.println(initRand+", "+i);
			float dist = rowDistance(initRow, r, cols);
			//System.out.println(dist);
			IndDistPair ip = new IndDistPair(i, dist);
			distList.add(ip);
			i++;
		}
		Collections.sort(distList, new IndDistPairComparator());
		int _90thIndex = Math.round(distList.size()*0.9f);
		Row pivot1 = rows.get(distList.get(_90thIndex).getIndex());
		
		i = 0;
		distList.clear();
		for(Row r:rows) {
			//System.out.println(initRand+", "+i);
			float dist = rowDistance(pivot1, r, cols);
			//System.out.println(dist);
			IndDistPair ip = new IndDistPair(i, dist);
			distList.add(ip);
			i++;
		}
		Collections.sort(distList, new IndDistPairComparator());
		_90thIndex = Math.round(distList.size()*0.9f);
		Row pivot2 = rows.get(distList.get(_90thIndex).getIndex());
		return new FastMapResponse(pivot1, 
								   pivot2, 
								   distList.get(_90thIndex)
								   		   .getDistance());
	}
	
	private Float cosDistance(Row x, Row y, Row z, Float distance, List<Col> cols) {
		Float numerator = (float) Math.pow(rowDistance(x, z, cols), 2) + (float) Math.pow(distance, 2) - (float) Math.pow(rowDistance(y, z, cols), 2);
		Float denominator = 2*distance;
		return numerator/denominator;
	}
	
	private Float rowDistance(Row i, Row j, List<Col> cols) {
		Float d = 0.0f, n = 0.0f;
		Float d0 = 0.0f;
		List<String> iCells = i.getCells();
		List<String> jCells = j.getCells();
		if(iCells.size() == 0 || jCells.size() == 0) {
			return 0.0f;
		}		
		int k=0;
		for(Col c:cols) {
			if(c.getClass() != Col.class) {
				n += 1;
				if(c.getClass() == Num.class) {
					Num num = (Num) c;
					d0 = num.dist(num, iCells.get(k), jCells.get(k));
				} else if(c.getClass() == Sym.class) {
					Sym sym = (Sym) c;
					d0 = sym.dist(sym, iCells.get(k), jCells.get(k));
				}
				d += (float) Math.pow(d0, P);
				k++;
			}
		}
		Float numerator = (float) Math.pow(d, (float) 1/P);
		Float denominator = (float) Math.pow(n, (float) 1/P);
		return (float) numerator/denominator;
	}
	
	static class IndDistPair {
        public int index;
        public float distance;

        public IndDistPair(int x, float y) {
            this.index = x;
            this.distance = y;
        }

        public int getIndex() {
        	return index;
        }
        
        public float getDistance() {
            return distance;
        }
    }
	
//	static class FastMapResponse {
//        private Row pivot1;
//        private Row pivot2;
//        private Float distance;
//        private Set<Integer> indices;
//		private Float medianCosineDistance;
//
//        public FastMapResponse() {
//        	this.pivot1 = new Row();
//        	this.pivot2 = new Row();
//        	this.distance = -1.0f;
//        	this.indices = new HashSet<>();
//        	this.medianCosineDistance = medianCosineDistance;
//        }
//
//        public FastMapResponse(Row pivot1, Row pivot2, Float distance) {
//            this.pivot1 = pivot1;
//            this.pivot2 = pivot2;
//            this.distance = distance;
//        }
//
//        public Row getPivot1() {
//        	return pivot1;
//        }
//
//        public Row getPivot2() {
//        	return pivot2;
//        }
//
//        public float getDistance() {
//            return distance;
//        }
//
//        public Set<Integer> getIndices() {
//        	return indices;
//        }
//
//        public void setIndices(Set<Integer> indices) {
//        	this.indices = indices;
//        }
//
//		public Float getMedianCosineDistance() {
//			return medianCosineDistance;
//		}
//
//		public void setMedianCosineDistance(Float medianCosineDistance) {
//			this.medianCosineDistance = medianCosineDistance;
//		}
//    }
	
	
	
	static class IndDistPairComparator implements Comparator<IndDistPair> {
	    public int compare(IndDistPair p1, IndDistPair p2) {
            if (p1.distance < p2.distance)
                return -1;
            else if (p1.distance > p2.distance)
                return 1;
            return 0;
        }
	}

	public Float newRowDistance(RPTree rpTree, Tbl tbl, Row row) {
		float x = 0.0f;
		FastMapResponse fastMapResponse = rpTree.getFastMapResponse();
		x = cosDistance(fastMapResponse.getPivot1(), fastMapResponse.getPivot2(), row, fastMapResponse.getDistance(), tbl.getCols());
		return x;
	}

	public Anomalous getAnomaly(RPTree rpTree, float x) {
		Anomalous anomalous = new Anomalous();
		Float far = 0.0f;
		boolean isAnomalous;
		String direction;
		FastMapResponse fastMapResponse = rpTree.getFastMapResponse();
		float s = fastMapResponse.getMedianCosineDistance();
		if (x < s) {
			direction = "left";
		}
		else {
			direction = "right";
		}
		if (s < 0.5) {
			far = s*magicAlpha;
			isAnomalous = x < far;
		}
		else {
			far = s+((1-s)*magicAlpha);
			isAnomalous = x > far;
		}
//		System.out.println("DEBUG: far= "+far+", x="+x+", Anomaly? = "+isAnomalous);
		anomalous.setFar(far);
		anomalous.setAnomalous(isAnomalous);
		anomalous.setDirection(direction);
		return anomalous;
	}

	static class Anomalous {
		private float far;
		private boolean isAnomalous;
		private String direction;

		public float getFar() {
			return far;
		}

		public void setFar(float far) {
			this.far = far;
		}

		public boolean getAnomalous() {
			return isAnomalous;
		}

		public void setAnomalous(boolean anomalous) {
			isAnomalous = anomalous;
		}

		public String getDirection() {
			return direction;
		}

		public void setDirection(String direction) {
			this.direction = direction;
		}
	}

	public RPTree addAnomalousRows(RPTree rpTree, Tbl tbl, Row newRow, Anomalous anomalous) {
		if(rpTree.getLevel() == 0) {
			rootCount = rpTree.getSplitCount();
		}
		if (rpTree.getChildren().size() == 0) {
			if(anomalous.getAnomalous()) {
//				System.out.println("DEBUG: Adding Anomalous row");
				//add row to cluster
				List<Row> rows = rpTree.getRows();
				rows.add(newRow);
				rpTree.setRows(rows);
				rpTree.setSplitCount(rows.size());
				My my = tbl.getMy();
				List<Col> leafStats = new ArrayList<>();
				for (Integer i : my.getGoals()) {
					Col c = tbl.getCols().get(i - 1);
					leafStats.add(c);
				}
				rpTree.setLeafStats(leafStats);
				int oldLevel = rpTree.getLevel();
				//System.out.println("OLD LEVEL: "+oldLevel);
				minSplit = (int) Math.round(Math.sqrt((rootCount + 1)));
				if(rows.size() > 2*minSplit) {
					String fileNameDataSet = "tempDataSet.csv";
					createTempDataSet(tbl, rpTree, newRow, fileNameDataSet);
					Tbl newTable = new Tbl();
					newTable.read(fileNameDataSet);
					RPTree newTree = generateRPTreeForCluster(newTable, minSplit);
					newTree = updateLevels(newTree, oldLevel);
					newTree.setRoot(false);
					//newTree.printTree(newTree);
					rpTree = newTree;
					//System.out.println("DEBUG: Modified cluster size "+rpTree.getSplitCount()+" children "+rpTree.getChildren().size());
				}
			}
		}
		else {
			int oldLevel = rpTree.getLevel();
			float x = newRowDistance(rpTree,tbl,newRow);
			boolean prevAnomaly = anomalous.getAnomalous();
			anomalous = getAnomaly(rpTree,x);
			anomalous.setAnomalous(anomalous.getAnomalous()||prevAnomaly);
			if (anomalous.getDirection() == "left") {
				List<RPTree> children = rpTree.getChildren();
				children.set(0, addAnomalousRows(children.get(0),tbl,newRow, anomalous));
				rpTree.setChildren(children);
			}
			if (anomalous.getDirection() == "right") {
				List<RPTree> children = rpTree.getChildren();
				children.set(1, addAnomalousRows(children.get(1),tbl,newRow, anomalous));
				rpTree.setChildren(children);
			}
			List<RPTree> children = rpTree.getChildren();
			int size = 0;
			for(RPTree r:children) {
				size += r.getSplitCount();
			}
			rpTree.setSplitCount(size);
			rpTree = updateLevels(rpTree, oldLevel);
		}
		return rpTree;
	}

	public void createTempDataSet(Tbl tbl, RPTree leaf, Row newRow, String fileName) {
		PrintWriter writer;
		try {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			writer = new PrintWriter(file);
			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < tbl.getCols().size(); i++) {
				if (i == tbl.getCols().size()-1) {
					sb.append(tbl.getCols().get(i).getTxt());
				}
				else {
					sb.append(tbl.getCols().get(i).getTxt());
					sb.append(',');
				}
			}
			sb.append('\n');
			List<Row> rows = leaf.getRows();
			rows.add(newRow);

			for (Row row : rows) {
				for (int j = 0; j < row.getCells().size(); j++) {
					if (j == row.getCells().size()-1) {
						if (row.getCells().get(j).equals("")) {
							sb.append("?");
						}
						else {
							sb.append(row.getCells().get(j));
						}
					}
					else {
						if (row.getCells().get(j).equals("")) {
							sb.append("?");
						}
						else {
							sb.append(row.getCells().get(j));
						}
						sb.append(',');
					}
				}
				sb.append('\n');
			}
			writer.write(sb.toString());
			writer.flush();
		}
		catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private RPTree updateLevels(RPTree rpTree, Integer level) {
		rpTree.setLevel(level);
		List<RPTree> children = rpTree.getChildren();
		if(children.size() > 0) {
			for(int i=0; i<rpTree.getChildren().size(); i++) {
				children.set(i, updateLevels(children.get(i), level+1));
			}
			rpTree.setChildren(children);
		}
		return rpTree;
	}

}
