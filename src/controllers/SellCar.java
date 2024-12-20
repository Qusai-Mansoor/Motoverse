package controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import dao.ListingDAO;
import dao.VehicleDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Listing;
import models.User;
import models.Vehicle;
import models.VehicleListingDTO;
import utils.SessionManager;

public class SellCar {

    @FXML
    private TextField inputDescription;

    @FXML
    private TextField inputMake;

    @FXML
    private TextField inputModel;

    @FXML
    private TextField inputPrice;

    @FXML
    private TextField inputYear;

    @FXML
    private Button photoUpload, submitButton;

    @FXML
    private ToggleButton rentingToggle;

    @FXML
    private ToggleButton sellingToggle;

    @FXML private ImageView imageView;
    
    @FXML
    private ToggleGroup type;
    @FXML BorderPane rootPane;
    @FXML private AnchorPane parentPane;
    
    private boolean photoUploaded = false;
    String imagePath;
    private Stage stage;
    private Scene scene;
    
    public void initialize()
    {
   // 	imageView.fitWidthProperty().bind(parentPane.widthProperty());
    //	imageView.fitHeightProperty().bind(parentPane.heightProperty());
    }
    
    
    public void handleSubmit() throws SQLException
    {
    	//Check input of all fields:
    	String Make = inputMake.getText();    
    	String Model = inputModel.getText();
    	int year, price;
    	try {
    	 year = Integer.parseInt(inputYear.getText());
    	 price = Integer.parseInt(inputPrice.getText());
    	}
    	catch(NumberFormatException e)
    	{
			showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
			return;
    	}
    			
    			String description = inputDescription.getText();
    	String type = rentingToggle.isSelected()? "Renting":"Selling";
    	
    	//Check if all fields are filled:
		if (Make.isEmpty() || Model.isEmpty() || year == 0 || price == 0 || description.isEmpty()) {
			showAlert("Error", "Please fill all fields", Alert.AlertType.ERROR);
			return;
		}
    	
     	//Check if the year is valid:
     	if(year < 1900 || year > 2024)
     	{
     		showAlert("Error", "Please enter a valid year", Alert.AlertType.ERROR);
     		return;
     	}
     	
     	//Check if the price is valid:
     	if(price < 0)
     	{
     		showAlert("Error", "Please enter a valid price", Alert.AlertType.ERROR);
     		return;
     	}
     	
		if (!photoUploaded) {
			showAlert("Error", "Please upload a photo", Alert.AlertType.ERROR);
			return;
		}
		
		//If a car with the same make and model already exists, don't create a new Vehicle object:
		VehicleDAO vehicleDAO = new VehicleDAO();
		int vehicleID = vehicleDAO.vehicleExists(Make, Model,year);
		//If vehicle doesn't exist, create a new Vehicle object:
		if(vehicleID == 0)
		{
			
			Vehicle vehicle = new Vehicle(Make, Model, year, price, 0, 0);
			vehicleID = vehicleDAO.addVehicle(vehicle);
			
		}
		User seller = SessionManager.getInstance().getCurrentUser();
		int sellerID = seller.getUserId();
		BigDecimal sellingprice = new BigDecimal(price);
		Listing listing = null;
		if(type.equals("Selling"))
		 listing = new Listing(sellerID, vehicleID, sellingprice, new BigDecimal(0), description, type, imagePath);
		else
          listing = new Listing(sellerID, vehicleID, new BigDecimal(0), sellingprice, description, type, imagePath);
		ListingDAO listingDAO = new ListingDAO();
		listingDAO.insertListing(listing);
		
     	//Show success message:
     	showAlert("Success", "Car added successfully", Alert.AlertType.INFORMATION);
     	
     	//Clear all fields:
     	inputMake.clear();
     	inputModel.clear();
     	inputYear.clear();
     	inputPrice.clear();
     	inputDescription.clear();
     	
    }
    
    public void handlePhotoUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Car Photo");
        
        // Set file extensions for images
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // Open the file chooser dialog
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            imagePath = file.getAbsolutePath(); // Store the absolute path of the file
            showAlert("Success", "Photo uploaded successfully: " + imagePath, Alert.AlertType.INFORMATION);
            photoUploaded = true;
        } else {
            showAlert("Error", "No photo selected", Alert.AlertType.ERROR);
        }
    }

	public void handleBack() throws IOException {
		// TODO Auto-generated method stub
		Parent root = FXMLLoader.load(getClass().getResource("/views/UserDashboard.fxml")); // Load the login.fxml file
        stage = (Stage) rootPane.getScene().getWindow(); // Get the current stage
        scene = new Scene(root); // Set the new scene
        stage.setScene(scene); // Apply the new scene to the stage
        stage.setResizable(true);
        stage.show(); // Show the stage
		
	
	}
	
	  private void showAlert(String title, String message, Alert.AlertType type) {
	        Alert alert = new Alert(type);
	        alert.setTitle(title);
	        alert.setContentText(message);
	        alert.showAndWait();
	    }
    
    
    
    
    
    
}
