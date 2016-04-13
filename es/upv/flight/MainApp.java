package es.upv.flight;

import java.io.IOException;
import es.upv.flight.view.FlightsInfoController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {

    	primaryStage.setTitle("Flights");
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/FlightsInfo.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.show();
          
        } catch (IOException e) { e.printStackTrace();}
    }
      
    public static void main(String[] args) {
        launch(args);
    }
}