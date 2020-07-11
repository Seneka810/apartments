package com.company;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private static Connection conn;
    public static void main(String[] args) {
        DbProperties properties = new DbProperties();

        Scanner scanner = new Scanner(System.in);
        try {
            conn = DriverManager.getConnection(properties.getUrl(),
                    properties.getUser(),
                    properties.getPassword());

            initDB();

            while (true) {
                System.out.println("If you want to add apartment press 1: ");
                System.out.println("If you want to view apartments press 2: ");
                String s = scanner.nextLine();
                switch (s) {
                    case "1": addApartment(scanner);
                        break;
                    case "2" : changeApartments(scanner);
                        break;
                    default: return;
                }
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initDB() throws SQLException {
        try (Statement statement = conn.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS Apartment");
            statement.execute("CREATE TABLE Apartment (" +
                    "id              INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "district        VARCHAR(50) NOT NULL," +
                    "address         VARCHAR(50) NOT NULL," +
                    "area            INT NOT NULL," +
                    "number_of_rooms INT NOT NULL," +
                    "price           DECIMAL NOT NULL)");
        }
    }

    private static void addApartment(Scanner scanner) throws SQLException {
        System.out.print("Enter apartment's district: ");
        String district = scanner.nextLine();
        System.out.print("Enter apartment's address: ");
        String address = scanner.nextLine();
        System.out.print("Enter apartment's area: ");
        String area = scanner.nextLine();
        System.out.print("Enter number of rooms: ");
        String numberOfRooms = scanner.nextLine();
        double price = getPrice(Integer.parseInt(area), Integer.parseInt(numberOfRooms));

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Apartment(district, address, area, number_of_rooms, price) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, district);
            ps.setString(2, address);
            ps.setInt(3, Integer.parseInt(area));
            ps.setInt(4, Integer.parseInt(numberOfRooms));
            ps.setDouble(5, price);

            ps.executeUpdate();
        }
    }

    private static void changeApartments(Scanner scanner) throws SQLException {
        System.out.println("To see all information press Enter: ");
        System.out.println("To see districts press 1: ");
        System.out.println("To see address press 2: ");
        System.out.println("To see area press 3: ");
        System.out.println("To see number of rooms press 4: ");
        System.out.println("To see price press 5: ");
        String s = scanner.nextLine();

        switch (s) {
            case "1": viewApartments("SELECT district FROM Apartment");
            break;
            case "2": viewApartments("SELECT address FROM Apartment");
            break;
            case "3": viewApartments("SELECT area FROM Apartment");
            break;
            case "4": viewApartments("SELECT number_of_rooms FROM Apartment");
            break;
            case "5": viewApartments("SELECT price FROM Apartment");
            break;
            default: viewApartments("SELECT * FROM Apartment");
        }
    }

    private static void viewApartments(String query) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();

                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(md.getColumnName(i) + "\t");
                }

                System.out.println();

                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t\t");
                    }

                    System.out.println();
                }
            }
        }
    }

    private static double getPrice(int area, int numberOfRooms) {
        double price;
        if(area < 40 || numberOfRooms == 1) {
            price = 20 + Math.random()*30;
        } else if(area > 40 && area < 70 || numberOfRooms == 2) {
            price = 30 + Math.random()*50;
        } else {
            price = 50 + Math.random()*100;
        }

        return price;
    }
}
