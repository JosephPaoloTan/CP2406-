package alpha;

import org.apache.commons.csv.CSVRecord;
/*
 * uses FileLoader class to load file and print to a new CSV file
 */
public class RainfallAnalyser {

    private static final String FILEPATH = "resources/IDCJAC0009_031205_1800_Data.csv"; //setting the filepath

    public static void main(String[] args) {
        String newFilePath = createNewFilePath(FILEPATH);
        FileLoader fileLoader = new FileLoader(FILEPATH, newFilePath);  // loads the file and prints to a new CSV file
    }
    /*
     * finds the file name from the file path
     */
    public static String createNewFilePath(String filePath) {
        String inputFileName = filePath.substring(filePath.lastIndexOf('/') + 1); // cuts string at / giving file name
        return "resources/new" + inputFileName;
    }
}
