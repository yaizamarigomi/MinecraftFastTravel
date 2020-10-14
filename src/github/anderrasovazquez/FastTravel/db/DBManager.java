package github.anderrasovazquez.FastTravel.db;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.*;


public class DBManager {

    private Connection connection;
    private final String db_url = "jdbc:sqlite:FastTravel.db";

    public DBManager() {
        try {
            connection = DriverManager.getConnection(db_url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS travel_data (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "player text NOT NULL," +
                    "location_name text NOT NULL," +
                    "x REAL NOT NULL," +
                    "y REAL NOT NULL," +
                    "z REAL NOT NULL" +
                    ");");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if(connection != null) connection.close();
            }
            catch(SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public boolean add(String player, String location_name, double x, double y, double z) {
        try {
            connection = DriverManager.getConnection(db_url);
            String sql = "insert into travel_data (player, location_name, x, y, z) values(?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, location_name);
            preparedStatement.setDouble(3, x);
            preparedStatement.setDouble(4, y);
            preparedStatement.setDouble(5, z);
            return preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if(connection != null) connection.close();
            }
            catch(SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return false;
    }

    public boolean rm(String player, int id) {
        try {
            connection = DriverManager.getConnection(db_url);
            String sql = "delete from travel_data where player=? and id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            preparedStatement.setInt(2, id);
            return (preparedStatement.executeUpdate() > 0);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if(connection != null) connection.close();
            }
            catch(SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        return false;
    }

    public String list(String player) {
        try {
            connection = DriverManager.getConnection(db_url);
            String sql = "SELECT id, location_name, x, y, z from travel_data where player=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, player);
            ResultSet result = preparedStatement.executeQuery();

            JsonObject list = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            while(result.next())
            {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", result.getInt("id"));
                jsonObject.addProperty("location_name", result.getString("location_name"));
                jsonObject.addProperty("x", result.getDouble("x"));
                jsonObject.addProperty("y", result.getDouble("y"));
                jsonObject.addProperty("z", result.getDouble("z"));
                jsonArray.add(jsonObject);
            }
            list.add("list", jsonArray);
            return list.toString();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                if(connection != null) connection.close();
            }
            catch(SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }
}

