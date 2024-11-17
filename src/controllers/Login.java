package controllers;

import java.io.IOException;

import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Login {

    @FXML
    private AnchorPane rootPane; // The parent AnchorPane
    private Stage stage;
    private Scene scene;

    @FXML
    private AnchorPane OpeningBack;
    @FXML
    private Label loginLabel; // The "LOG IN" Label
    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;
    
    

    @FXML
    public void initialize() {
        // Bind font size dynamically to the height of the root pane
//        rootPane.heightProperty().addListener((obs, oldHeight, newHeight) -> {
//            double fontSize = newHeight.doubleValue() * 0.05; // Adjust multiplier as needed
//            loginLabel.setStyle("-fx-font-size: " + fontSize + "px;");
//        });
    }
    
    public void switchToSignUp(ActionEvent event) throws IOException
    {
    	 Parent root = FXMLLoader.load(getClass().getResource("/views/SignUp.fxml")); // Load the login.fxml file
         stage = (Stage) rootPane.getScene().getWindow(); // Get the current stage
         scene = new Scene(root); // Set the new scene
         stage.setScene(scene); // Apply the new scene to the stage
         stage.setResizable(true);
         stage.show(); // Show the stage
    
    }
    
	public void handleLogIn(ActionEvent event) throws IOException {
		// Get user input
		String name = nameField.getText();
		String password = passwordField.getText();

		// Validate input
		if (name.isEmpty() || password.isEmpty()) {
			// Show an error message
			showAlert("Error", "All fields are required!", Alert.AlertType.ERROR);
			return;
		}

		// Check if the user exists in the database
		 UserDAO userDAO = new UserDAO();
		 boolean exists = userDAO.checkUser(name, password);
		
		// If the user exists, switch to the main screen
		if (exists) {
			Parent root = FXMLLoader.load(getClass().getResource("/views/UserDashboard.fxml")); // Load the main.fxml file
			stage = (Stage) rootPane.getScene().getWindow(); // Get the current stage
			scene = new Scene(root); // Set the new scene
			stage.setScene(scene); // Apply the new scene to the stage
			stage.setResizable(true);
			stage.show(); // Show the stage
		} else {
			// If the user does not exist, show an error message
			//Show alert:
			showAlert("Error", "User does not exist. Please try again.", Alert.AlertType.ERROR);
			
		}
		
	}
	
	 private void showAlert(String title, String message, Alert.AlertType type) {
	        Alert alert = new Alert(type);
	        alert.setTitle(title);
	        alert.setContentText(message);
	        alert.showAndWait();
	    }

	
	
	
    
    
}
