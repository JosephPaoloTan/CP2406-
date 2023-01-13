package alpha;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/*
 * File loader class
 * loads files and prints to a csv
 */
@SuppressWarnings("deprecation")    // removes deprecation lines through code
public class FileLoader {

    /*
     * loads CSV file from given path
     * removes header from CSV file
     */
    public FileLoader(String filePath, String newFilePath) {
        try {
            Reader in = new FileReader(filePath);
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader("Product Code", "Bureau Station Number", "Year", "Month", "Day", "Rainfall")
                    .withSkipHeaderRecord().parse(in);  // skips the header line when looping through the csv file
                                                        // "Product Code" and "Bureau Station Number" Aren't used, simply included to give correct values to headers
            printNewCSV(records, newFilePath);
        } catch (IOException e) {   // catches IOException when the file path given is invalid
            System.out.println("File path error: " + e); // print out argument and cursor stays on same line
        }
    }

    /*
     * loops through the CSV file
     * Adds monthly totals together, finds the min and max daily rainfall for each month
     * prints useful data to a new CSV file
     */
    private void printNewCSV(Iterable<CSVRecord> file, String newFilePath) {
        double rainfallAmount = 0.0, min = Double.POSITIVE_INFINITY, max = 0.0;
        double recordRainfallAmountDouble = 0.0;
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(newFilePath));    // creates the new CSV file
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

            for (CSVRecord record : file) {      // loops through each data set
                String recordYear = record.get("Year");                 // gets the usable data from CSV file
                String recordMonth = record.get("Month");
                int recordDay = Integer.parseInt(record.get("Day"));    // turns day into an INT value
                String recordRainfallAmount = record.get("Rainfall");

                if (recordRainfallAmount.equals("")) {  // stops empty string error, if a rainfall amount is null
                    recordRainfallAmount = "0.0";       // changes the value to 0 instead
                }

                recordRainfallAmountDouble = Double.parseDouble(recordRainfallAmount);  // changes the rainfall amount
                                                                                        // grabbed from CSV to double
                if (recordRainfallAmountDouble > max) {
                    max = recordRainfallAmountDouble;
                }                                               // finds the min and max rainfall amount for each month
                if (recordRainfallAmountDouble > 0.0 && recordRainfallAmountDouble < min) {
                    min = recordRainfallAmountDouble;
                }

                rainfallAmount += recordRainfallAmountDouble;
                // if the day is the first of the month, print totals to CSV file and reset values
                if (recordDay == 1) {
                    String rainfallAmountString = String.format("%.1f", rainfallAmount);

                    if (min == Double.POSITIVE_INFINITY) {   // if min value doesn't change then set to 0
                        min = 0.0;
                    }

                    csvPrinter.printRecord(Arrays.asList(recordYear, recordMonth, rainfallAmountString, max, min));    // prints the usable data to a CSV file
                    csvPrinter.flush(); // flush the printer, stops any printing errors

                    rainfallAmount = 0.0; min = Double.POSITIVE_INFINITY; max = 0.0;    // resets the values
                }
            }
        } catch (IOException e) {   // catches invalid file error
            System.out.println("Error printing to CSV file: " + e);
        }
    }
}







