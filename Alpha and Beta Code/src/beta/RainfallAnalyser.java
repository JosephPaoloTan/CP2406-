package beta;

import java.io.IOException;
import java.util.ArrayList;

/*
 * uses FileLoader class to load a file in another project, format the results and store it in an ArrayList
 */
public class RainfallAnalyser {

    public ArrayList<String[]> LoadFiles(String filePath) throws IOException {
        FileLoader loadedFile = new FileLoader();
        ArrayList<String[]> arrayOfRecords = loadedFile.loadFile(filePath);
        return arrayOfRecords;
    }
}
