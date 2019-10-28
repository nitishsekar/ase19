import java.util.ArrayList;
import java.util.Random;

public class SplitTestGen {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Float> arr = new ArrayList<>();
		arr.add(58.0f);
		arr.add(132.0f);
		arr.add(247.0f);
		Random r1 = new Random();
		Random r2 = new Random();
		for(int i=0; i<36; i++) {
			System.out.println(r1.nextFloat()*500+", "+(r2.nextFloat()*50)+arr.get(i%3));
		}
	}

}
