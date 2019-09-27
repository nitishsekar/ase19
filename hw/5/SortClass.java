import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class SortClass {

    public static void main(String[] args) {
//        PriorityQueue<XY> pq = new PriorityQueue<XY>(Integer.MAX_VALUE, new XYComparator());
        PriorityQueue<YZ> pq = new PriorityQueue<YZ>(1, new YZComparator());
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        while (s.length()!=0) {
            String[] strings = s.toString().replaceAll("[\\[\\]]","").split(",");
//            XY xy = new XY(Double.valueOf(strings[0]), Double.valueOf(strings[1]));
            YZ yz = new YZ(Double.valueOf(strings[0].trim()), strings[1].trim());
            pq.add(yz);
            s = in.nextLine();
        }
        in.close();
        while (!pq.isEmpty()) {
            YZ yz = pq.poll();
            System.out.println(yz.getY()+","+yz.getZ());
        }
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
        public  double x;
        public double y;

        public XY(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getY() {
            return y;
        }
    }

    static class YZ {
        public  double y;
        public String z;

        public YZ(double y, String z) {
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
