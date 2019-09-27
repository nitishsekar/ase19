import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TestNum {

    public static void main(String args[]) throws IOException {
        Num num = new Num();
        /*
        Random random = new Random();
        File file = new File("out.txt");
        if (!file.exists()) {
            file.createNewFile();
        }

        for (int i = 0; i < 100; i++) {
            num.updateMeanAndSD((float)random.nextInt(1000));
        }
        System.out.println("\nDeleting numbers from 100 to 10:\n");
        for (int i = 0; i < 90; i++) {
            num.deleteFirstNum();
        }
        System.out.println(num.getCount());
        
        */
        num.updateMeanAndSD(2.0f);
        num.updateMeanAndSD(4.0f);
        System.out.println(num.getMean());
        try {
			Num n2 = (Num)num.clone();
			n2.updateMeanAndSD(9.0f);
	        System.out.println(num.getMean());
	        System.out.println(n2.getMean());
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
