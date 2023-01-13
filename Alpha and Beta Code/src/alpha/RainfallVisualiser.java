package alpha;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;

import java.util.ArrayList;

import java.io.IOException;



public class RainfallVisualiser extends Application {

    private static final String INPUTFILEPATH = "resources/newMountSheridanStationCNS.csv";

    @Override
    public void start(Stage primaryStage) {

        ArrayList<String[]> data = new ArrayList<>(loadRainfallData(INPUTFILEPATH)); // loads rainfall data
        String inputFileName = INPUTFILEPATH.substring(INPUTFILEPATH.lastIndexOf("New") + 3); // gets file name
        System.out.println(inputFileName + " has been successfully loaded");

        primaryStage.setTitle("Rainfall in " + inputFileName);  // sets title

        CategoryAxis xAxis = new CategoryAxis();    // sets x axis
        xAxis.setLabel("Years & Months");   //labels x axis

        NumberAxis yAxis = new NumberAxis();    // sets y axis
        yAxis.setLabel("Monthly Rainfall Amount (mm)"); // labels y axis

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis); // creates bar chart

        ArrayList<XYChart.Series<String, Number>> seriesList = new ArrayList<>();   // list of series
        // loops through all the records in file
        // separates data into months, adds to a series then adds to series list
        for (int m = 1; m < 13; m++) {
            XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();

            String month = String.valueOf(m);   // turns the month being looped into a string
            dataSeries.setName(month);  // sets the series name
            // loops through all the records and collects data for a specific month
            for (String[] monthlyData : data) {
                int monthInt = Integer.parseInt(monthlyData[1]);

                if (monthInt == m) {    // checks whether record fits certain month
                    double rainfallDataDouble = Double.parseDouble(monthlyData[2]); // turns rainfall data into double

                    dataSeries.getData().add(new XYChart.Data<>(monthlyData[0], rainfallDataDouble)); // adds to series if fits with month
                }
            }
            seriesList.add(dataSeries); // adds series to series list
        }
        // loops through series list then adds to bar chart
        for (XYChart.Series<String, Number> seriesItem : seriesList) {
            barChart.getData().add(seriesItem);
        }

        VBox vbox = new VBox(barChart); // creates the space for bar graph

        Scene scene = new Scene(vbox);  // creates the GUI scene with bar graph in it

        primaryStage.setScene(scene);   // sets the scene as the primary stage
        primaryStage.setHeight(450);    // set GUI height
        primaryStage.setWidth(1320);    // set GUI width
        primaryStage.setResizable(false);   // makes GUI size unchangeable

        primaryStage.show();
    }

    /*
     * Loads a processed CSV file
     * Puts relevant data into an Object ArrayList
     */
    public ArrayList<String[]> loadRainfallData(String filePath) {

        int dataRecordSize = 3; // size of each record in ArrayList
        ArrayList<String[]> data = new ArrayList<>();

        try {
            Reader in = new FileReader(filePath);                       // loads processed CSV file
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

            for (CSVRecord record : records) {      // loops through each data set
                String[] dataRecord = new String[dataRecordSize];    // creates an array to enter into ArrayList as a value
                for (int i = 0; i < dataRecord.length; i++) {
                    dataRecord[i] = record.get(i);  // adds relevant data to array
                }
                data.add(dataRecord);   // adds array to ArrayList
            }
        } catch(IOException e){   // catches IOException when the file path given is invalid
            System.out.println("File path error: " + e);
        }
        return data;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
