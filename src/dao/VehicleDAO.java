package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Vehicle;
import models.VehicleListingDTO;

public class VehicleDAO {

    

    public VehicleDAO() throws SQLException {
        // Establishing database connection
       // this.connection = utils.DatabaseConnection.getConnection();
    }

    public static List<VehicleListingDTO> getAvailableVehicles(String listingType,String make, Integer year, Double minPrice, Double maxPrice) {
    	String query ;
    	if(listingType == "Selling")
	    	{ 
    		query = """
	            SELECT v.vehicle_id, v.make, v.model, v.year, v.price,
	                   l.description, u.name AS seller_name, v.image_path, v.average_rating
	            FROM Vehicles v
	            JOIN Listings l ON v.vehicle_id = l.vehicle_id
	            JOIN Users u ON l.seller_id = u.user_id
	            WHERE v.status = 'Available' 
	              AND l.listing_type = 'Selling'
	              AND (v.make = ? OR ? IS NULL) -- Filter by make
	              AND (v.year <= ? OR ? IS NULL) -- Filter by year
	              AND (v.price BETWEEN ? AND ?) -- Filter by price range
	        """;
	    	}
    	else
    	{
    		query = """
    	            SELECT v.vehicle_id, v.make, v.model, v.year, l.rental_price AS price,
    	                   l.description, u.name AS seller_name, v.image_path, v.average_rating
    	            FROM Vehicles v
    	            JOIN Listings l ON v.vehicle_id = l.vehicle_id
    	            JOIN Users u ON l.seller_id = u.user_id
    	            WHERE v.status = 'Available' 
    	              AND l.listing_type = 'Renting'
    	              AND (v.make = ? OR ? IS NULL) -- Filter by make
    	              AND (v.year <= ? OR ? IS NULL) -- Filter by year
    	              AND (l.rental_price BETWEEN ? AND ?) -- Filter by price range
    	        """;
    		
    	}

        List<VehicleListingDTO> vehicleListings = new ArrayList<>();
        try (Connection conn = utils.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
        	
            stmt.setString(1, make);
            stmt.setString(2, make); // Allow null filtering
            stmt.setObject(3, year);
            stmt.setObject(4, year); // Allow null filtering
            stmt.setDouble(5, minPrice);
            stmt.setDouble(6, maxPrice);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                VehicleListingDTO dto = new VehicleListingDTO(
                    rs.getInt("vehicle_id"),
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getDouble("price"),
                    rs.getString("description"),   // From Listings table
                    rs.getString("seller_name")   // From Users table
                );
                dto.setListingType(listingType);
                dto.setImagePath(rs.getString("image_path"));
                dto.setAverageRating(rs.getDouble("average_rating"));
                
                vehicleListings.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicleListings;
    }
    
    
    
    public int vehicleExists(String make,String model)
    {
    	String query = """
                SELECT v.vehicle_id
                FROM Vehicles v
                WHERE v.make = ? AND v.model = ?
            """;
    	
    	try (Connection conn = utils.DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
        	
            stmt.setString(1, make);
            stmt.setString(2, model);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("vehicle_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            }
    	        return 0;
    }
    
    public void addVehicle(Vehicle vehicle)
    {
    	String query = """
                INSERT INTO Vehicles (make, model, year, price, rental_price, average_rating, status)
                VALUES (?, ?, ?, ?, ?, ?, 'Available')
            """;
    	
    	try (Connection conn = utils.DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
        	
            stmt.setString(1, vehicle.getMake());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setDouble(4, vehicle.getPrice());
            stmt.setDouble(5, vehicle.getRentalPrice());
            stmt.setDouble(6, vehicle.getAverageRating());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            }
    	
    }
    
    
    
    
    
    
    
    
    

}