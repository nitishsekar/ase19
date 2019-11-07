import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TestGlobalLearning {

    public static void main(String args[]) {
        Tbl originalTbl = new Tbl();
        originalTbl.read("C:\\Users\\satan\\OneDrive\\Desktop\\auto.csv");
        String fileNameDataSet = "globalLearningDataSet.csv";
        GlobalLearning globalLearning = new GlobalLearning();
        globalLearning.printRankedRowsToFile(originalTbl,fileNameDataSet);
        Tbl rankedTable = new Tbl();
        String labelType = rankedTable.read(fileNameDataSet);
        DecisionTreeGenerator dt = new DecisionTreeGenerator();
        System.out.println("Decision Tree for "+fileNameDataSet);
        String fileNameDT = "resultantGlobalLearningDT.md";
        PrintWriter writer;
        try {
            File file = new File(fileNameDT);
            if (file.exists()) {
                file.delete();
            }
            writer = new PrintWriter(file);
            StringBuilder sb = new StringBuilder();
            if (labelType.contains("Sym")) {
                dt.createDecisionTree(rankedTable,"Sym",sb);
            }
            if (labelType.contains("Num")) {
                dt.createDecisionTree(rankedTable,"Num",sb);
            }
            writer.write(sb.toString());
            writer.flush();
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }
}
