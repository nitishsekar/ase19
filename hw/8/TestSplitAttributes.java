import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class TestSplitAttributes {

	public static void main(String[] args) {
		BufferedReader csvReader;
		List<String> list = new ArrayList<String>();
		String fileName = "C:\\\\Users\\\\nitis\\\\Desktop\\\\CSC 591 - ASE\\\\HW 6\\\\test.diab.csv";
		// Uncomment the next line and comment the line above to test Sym 
		// String fileName = "C:\\\\Users\\\\nitis\\\\Desktop\\\\CSC 591 - ASE\\\\HW 5\\\\Symtest.txt";
		
		try {
			System.out.println("Reading file: "+fileName+"\n");
			csvReader = new BufferedReader(new FileReader(fileName));
			String line;
			
			try {
				while ((line = csvReader.readLine()) != null) {
					list.add(line);
				}
				csvReader.close();
				SplitAttributes sc = new SplitAttributes();
				sc.identifySplit(list,"Num");
				// Uncomment the next line and comment the line above to test Sym 
				// sc.identifySplit(list,"Sym");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
	}

}
