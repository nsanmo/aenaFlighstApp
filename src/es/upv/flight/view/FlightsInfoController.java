package es.upv.flight.view;

import java.time.LocalDate;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import application.data.Data;
import application.model.Flight;
import application.model.AENA_Airport;
import application.model.Airport;
import application.model.AirportFlights;
import application.model.FlightsHistory;


public class FlightsInfoController{
	 
	// BarChart shows the number of flights per Airport in a chosen date
	@FXML
	private BarChart<String, Integer> barChart;
	@FXML
	private CategoryAxis xAxis;
	// PieChart shows the number of flights per Date
	@FXML
	private PieChart pieChart;
	ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
	// PieChart shows the delay distribution
	@FXML
	private PieChart pieChartDelay;
	ObservableList<PieChart.Data> pieChartDataDelay = FXCollections.observableArrayList();

	// ComboBox to choose the date
	@FXML
	private ComboBox<LocalDate> comboBoxDate;
	@FXML
	private ComboBox<Airport> comboBox2;
	// CheckBox 
	@FXML
	private CheckBox domesticCheckBox;
	boolean domesticSelected;
	@FXML
	private CheckBox internationalCheckBox;
	boolean internationalSelected;
	
	@FXML
	private CheckBox domesticCheckBox2;
	boolean domesticSelected2;
	@FXML
	private CheckBox internationalCheckBox2;
	boolean internationalSelected2;
	
	@FXML
	private CheckBox arrivalCheckBox;
	boolean arrivalSelected;
	@FXML
	private CheckBox departureCheckBox;
	boolean departureSelected;
	
	private LocalDate dateSelected;
	private Data data;
	
	private List<Airport> airports;
	private ObservableList<String> airportNames = FXCollections.observableArrayList();
	private ObservableList<Airport> airportObs = FXCollections.observableArrayList();
	private ObservableList<LocalDate> dates;
	private XYChart.Series<String, Integer> series ;
	private Airport airportSelected;
	
		
	public FlightsInfoController() {
        domesticSelected = true;
        domesticSelected2 = true;
        internationalSelected = true;
        internationalSelected2 = true;
        departureSelected = true;
        arrivalSelected = true;
        airportSelected = AENA_Airport.getAirportByCode("VLC");
    }
	
	/**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
	@FXML
	private void initialize() {
		
		
		data = Data.getInstance();
	    airports = data.getAirportList();
	    dates = FXCollections.observableArrayList(data.getDates());
	    airportObs = FXCollections.observableArrayList(airports);
  
	    for (Airport a : airports) { 
	    	airportNames.add(a.getCode());
	    	//System.out.println(a.getName());
	    }

	    xAxis.setCategories(airportNames);
	    
	    // Handle DomesticCheckBox event.
	 	domesticCheckBox.setOnAction((event) -> {
	 		domesticSelected = domesticCheckBox.isSelected();
	 		setFlightsData();
	 		//System.out.println("domestic = " + domesticSelected);
	 	});
	 	
	 	// Handle internationalCheckBox event.
	 	internationalCheckBox.setOnAction((event) -> {
	 	 		internationalSelected = internationalCheckBox.isSelected();
	 	 		setFlightsData();
	 	 });

	    // Handle DomesticCheckBox2 event.
	 	domesticCheckBox2.setOnAction((event) -> {
	 		domesticSelected2 = domesticCheckBox2.isSelected();
	 		setFlightsDataVLC();
	 	});
	 	
	 	// Handle internationalCheckBox event.
	 	internationalCheckBox2.setOnAction((event) -> {
	 	 		internationalSelected2 = internationalCheckBox2.isSelected();
	 	 		setFlightsDataVLC();
	 	 });
	 	
	 	// Handle arrivalCheckBox event.
	 	arrivalCheckBox.setOnAction((event) -> {
	 		arrivalSelected = arrivalCheckBox.isSelected();
	 		pieChartDelay.setTitle("Global Arrivals Delay");
	 		setDelayDistribution();
	 	});
	 	 	
	 	 // Handle internationalCheckBox event.
	 	 departureCheckBox.setOnAction((event) -> {
	 	 	departureSelected = departureCheckBox.isSelected();
	 	 	pieChartDelay.setTitle("Global Departures Delay");
	 	 	setDelayDistribution();
	 	 }); 	
	 	
	 	comboBoxDate.setPromptText(data.getFromDate().toString());
	    comboBoxDate.setItems(dates);
	 	 
	 	 // Handle comboBoxDate event.
		comboBoxDate.valueProperty().addListener(new ChangeListener<LocalDate>() {
        public void changed(ObservableValue ov, LocalDate t, LocalDate t1) {
        	dateSelected = t1;
        	setFlightsData();                
            }    
        });
		
		comboBox2.setPromptText("VLC");
		comboBox2.setItems(airportObs);
		
		comboBox2.setOnAction((event) -> {
			Airport airportSelected = comboBox2.getSelectionModel().getSelectedItem();
			pieChart.setTitle("Flight per date in "+ airportSelected.getCode());
			setFlightsDataVLC();
		});
		
		dateSelected = data.getFromDate();
		setFlightsData();
		setFlightsDataVLC();
		setDelayDistribution();
	}
		
	public void setFlightsData() {

		// count the number of flights that departs at an airport in a given date
		int flightCounter[] = new int[airports.size()];
		int j=0;    	
		if (dateSelected==null) dateSelected =data.getFromDate();
		
		for (Airport air: airports) {
			AirportFlights af = data.getAirportFlights(air, dateSelected);
			if (domesticSelected && (!internationalSelected)) {				// only domestic flights
				if (af != null) flightCounter[j++] = af.getNumNationalFlights();
				else flightCounter[j++] = 0;
			} else if ((!domesticSelected) && internationalSelected) {				// only international flights
				if (af != null) flightCounter[j++] = af.getNumInternationalFlights();
				else flightCounter[j++] = 0;
			} else if (domesticSelected && internationalSelected) {				// all flights
				if (af != null) flightCounter[j++] = af.getNumFlights();
				else flightCounter[j++] = 0;
			} else flightCounter[j++] = 0;
		} 
			//System.out.println(airports.size() + " Aeropuerto " + air + " tiene " + flightCounter[j-1]);
			
		barChart.getData().clear(); 
		series = new XYChart.Series<>();
		for (int i = 0; i < flightCounter.length; i++) {
			series.getData().add(new XYChart.Data<>(airportNames.get(i), flightCounter[i]));
	    }
	    barChart.getData().add(series); 
	}
	
	
	public void setFlightsDataVLC() {

		// count the number of flights that departs at an airport in a given date
		
		Airport vlc = AENA_Airport.getAirportByCode("VLC");
		pieChartData.clear();
		
		
		for (LocalDate ld: data.getDates()) {
			AirportFlights af = data.getAirportFlights(vlc, ld);
		
			if (domesticSelected2 && (!internationalSelected2)) {				// only domestic flights
				if (af != null) pieChartData.add(new PieChart.Data(ld.toString(),af.getNumNationalFlights())); 
				else pieChartData.add(new PieChart.Data(ld.toString(),0));
			} else if ((!domesticSelected2) && internationalSelected2) {				// only international flights
				if (af != null) pieChartData.add(new PieChart.Data(ld.toString(),af.getNumInternationalFlights())); 
				else pieChartData.add(new PieChart.Data(ld.toString(),0));
			} else if (domesticSelected2 && internationalSelected2) {				// all flights
				if (af != null) pieChartData.add(new PieChart.Data(ld.toString(),af.getNumFlights())); 
				else pieChartData.add(new PieChart.Data(ld.toString(),0));
			} else pieChartData.add(new PieChart.Data(ld.toString(),0));
		}
		pieChart.setData(pieChartData);
	}
		
	public void setDelayDistribution() {
		
		int [] deldist = new int[3];
		pieChartDataDelay.clear();
		
		if (dateSelected==null) dateSelected =data.getFromDate();
		 
		for (Airport a: airports) {

			AirportFlights af = data.getAirportFlights(a, dateSelected);
			if(af != null){
			
			FlightsHistory arrivals = af.getArrivals();
			FlightsHistory departures = af.getDepartures();
			
			if (arrivalSelected && (!departureSelected)) {
	            for (Flight vuelo: arrivals.getFlights().values()) {
	                if (vuelo.getDelay() <= 10) deldist[0]++;
	                else if ((vuelo.getDelay() > 10) && (vuelo.getDelay() <= 30)) deldist[1]++;
	                else deldist[2]++;
	            }
	        } else if ((!arrivalSelected) && (departureSelected)) {
	            for (Flight vuelo: arrivals.getFlights().values()) {
	                if (vuelo.getDelay() <= 10) deldist[0]++;
	                else if ((vuelo.getDelay() > 10) && (vuelo.getDelay() <= 30)) deldist[1]++;
	                else deldist[2]++;
	            }    
	        } else if  ((arrivalSelected) && (departureSelected)) {
	            for (Flight vuelo: arrivals.getFlights().values()) {
	                if (vuelo.getDelay() <= 10) deldist[0]++;
	                else if ((vuelo.getDelay() > 10) && (vuelo.getDelay() <= 30)) deldist[1]++;
	                else deldist[2]++;
	            }    
	            for (Flight vuelo: departures.getFlights().values()) {
	                if (vuelo.getDelay() <= 10) deldist[0]++;
	                else if ((vuelo.getDelay() > 10) && (vuelo.getDelay() <= 30)) deldist[1]++;
	                else deldist[2]++;
	            }
	        }    
			}
		}
		pieChartDataDelay.add(new PieChart.Data("< 10 min", deldist[0]));
		pieChartDataDelay.add(new PieChart.Data("10 < min < 30", deldist[1]));
		pieChartDataDelay.add(new PieChart.Data("> 30 min", deldist[2]));

		pieChartDelay.setData(pieChartDataDelay);
	}
		
				
	
	//pieChart.setTitle("XXXXXXXX");
       
	    
}
