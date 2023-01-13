package beta;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * JAVAFX program that formats a CSV files data about rainfall, stores it to memory
 * and then displays it on a barchart
 */
public class RainfallVisualiser extends Application {

    HashMap<String, ArrayList<String[]>> rainfallDataInMemory = new HashMap<>();   // HashMap that saves rainfall data to memory

    /*
     * This is the main application, handles the GUI scene (barchart, combobox, filechooser, etc), handles storing
     * the data to memory and displaying the barchart, handles interactions with the GUI
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Rainfall Data Visualizer");  // sets title

        CategoryAxis xAxis = new CategoryAxis();    // sets x axis
        xAxis.setLabel("Years & Months");

        NumberAxis yAxis = new NumberAxis();    // sets y axis
        yAxis.setLabel("Monthly Rainfall Amount (mm)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis); // creates bar chart

        TextField recordNameTextField = new TextField();    // creates an input field for entering the name of a CSV file
        Text recordNameTitle = new Text("Enter the name of your record:");  // creates text above the input field
        recordNameTitle.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));

        ComboBox<String> comboBox = new ComboBox<>(); // creates drop down menu for selecting files already in memory
        comboBox.setMinWidth(150);

        FileChooser fileChooser = new FileChooser();    // class for choosing a file
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv")); // restricts file choosing to CSV files only
        Button fileButton = new Button("Select File");  // button to create event for choosing a file
        // sets the action for when button is pressed, grabs file and stores to HashMap, visualizes graph
        fileButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            String textFieldString = recordNameTextField.getText(); // gets the user input text as a string
            if (textFieldString.trim().equals("")) {   // checks user input for valid name (has characters)
                recordNameTitle.setText("Please enter a valid name");
                recordNameTitle.setFill(Color.RED);
            } else {
                barChart.getData().clear(); // clears barchart to prevent different graphs stacking
                try {
                    String selectedFilePath = selectedFile.getAbsolutePath();   // gets the absolute path of a file to allowing using CSV files anywhere
                    RainfallAnalyser analyser = new RainfallAnalyser(); // new RainfallAnalyser class
                    ArrayList<String[]> processedFile = new ArrayList<>(); // ArrayList for where processed data will be stored
                    try {
                        processedFile = new ArrayList<>(analyser.LoadFiles(selectedFilePath));  // loads the file chosen in FileChooser to processedFile
                        comboBox.getItems().add(textFieldString);   // adds user input as a drop-down menu option
                    } catch (IOException | NumberFormatException ex) {  // catches corrupted file or invalid file (desktop shortcuts)
                        System.out.println("Invalid or Corrupted File: " + ex);
                    }
                    rainfallDataInMemory.put(textFieldString, processedFile);   // stores data to HashMap with user input as key processedFile as the value

                    ArrayList<String[]> data = new ArrayList<>(rainfallDataInMemory.get(textFieldString));  // gets data from memory based on the user input key name

                    ArrayList<XYChart.Series<String, Number>> seriesList = new ArrayList<>(visualizeGraph(data));   // loads the values from hashmap and converts it to barchart data

                    for (XYChart.Series<String, Number> seriesItem : seriesList) {  // loops through the barchart data and adds it to barchart
                        barChart.getData().add(seriesItem);
                    }
                    recordNameTitle.setText("Enter the name of your record:");
                    recordNameTitle.setFill(Color.BLACK);
                } catch (NullPointerException np) {     // Catches error when non CSV file is loaded
                    System.out.println("Null Pointer Error, choose a CSV file: "+ np);
                }
            }
        });
        // action for when item from drop-down menu is chosen, clears barchart then loads
        // values from HashMap using the drop-down option as the key
        comboBox.setOnAction((event) -> {
            barChart.getData().clear();

            ArrayList<String[]> data = new ArrayList<>(rainfallDataInMemory.get(comboBox.getValue()));

            ArrayList<XYChart.Series<String, Number>> seriesList = new ArrayList<>(visualizeGraph(data));

            for (XYChart.Series<String, Number> seriesItem : seriesList) {
                barChart.getData().add(seriesItem);
            }
        });

        VBox vboxBarChart = new VBox(barChart); // turns barChart into a vertical box

        VBox sceneTop = new VBox(comboBox); // turns comboBox into a vertical box
        sceneTop.setAlignment(Pos.CENTER);

        // adds rest of the scene to a vertical box
        VBox sceneRightSide = new VBox();
        sceneRightSide.getChildren().addAll(recordNameTitle, recordNameTextField, fileButton);
        sceneRightSide.setAlignment(Pos.CENTER_LEFT);
        sceneRightSide.setPadding(new Insets(5, 15, 5, 5));

        BorderPane border = new BorderPane();   // new borderPane class (for the layout of GUI)

        // layout placement for objects in the scene
        border.setCenter(vboxBarChart);
        border.setRight(sceneRightSide);
        border.setTop(sceneTop);

        Scene scene = new Scene(border);    // creates the GUI scene with bar graph and button in it

        primaryStage.setScene(scene);   // sets the scene as the primary stage
        primaryStage.setHeight(450);    // GUI height
        primaryStage.setWidth(1320);    // GUI width
        primaryStage.setResizable(false);   // makes GUI size unchangeable
        
        primaryStage.show();
    }

    /*
     * Visualizes the graph, gets the values from memory in the HashMap then loops through it creating a list
     * of the barChart data as a series
     */
    public ArrayList<XYChart.Series<String, Number>> visualizeGraph(ArrayList<String[]> data) {
        ArrayList<XYChart.Series<String, Number>> seriesList = new ArrayList<>();   // list of series
        // loops through all the records in memory
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
        return seriesList;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
