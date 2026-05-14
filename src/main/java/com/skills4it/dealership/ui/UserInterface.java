package com.skills4it.dealership.ui;

import com.skills4it.dealership.data.DealershipFileManager;
import com.skills4it.dealership.models.Dealership;
import com.skills4it.dealership.models.Vehicle;
import com.skills4it.dealership.models.enums.VehicleType;
import com.skills4it.dealership.ui.enums.MenuOption;

import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private final Scanner scanner;
    private final DealershipFileManager fileManager;
    private Dealership dealership;

    public UserInterface() {
        this.scanner = new Scanner(System.in);
        this.fileManager = new DealershipFileManager();
    }

    public void display() {
        init();

        MenuOption selectedOption;
        do {
            displayHeader();
            displayMenu();
            int choice = readInt("Choose an option: ");
            selectedOption = MenuOption.fromCode(choice).orElse(null);
            handleMenuChoice(selectedOption);
        } while (selectedOption != MenuOption.QUIT);

        System.out.println("Goodbye!");
    }

    private void init() {
        this.dealership = fileManager.getDealership();
    }
//HELLOE
    private void displayHeader() {
        System.out.println();
        System.out.println("=============================================");
        System.out.println(dealership.getName());
        System.out.println(dealership.getAddress() + " | " + dealership.getPhone());
        System.out.println("=============================================");
    }

    private void displayMenu() {
        for (MenuOption option : MenuOption.values()) {
            System.out.printf("%-3d - %s%n", option.getCode(), option.getLabel());
        }
        System.out.println();
    }

    private void handleMenuChoice(MenuOption option) {
        if (option == null) {
            System.out.println("Invalid option. Please try again.");
            return;
        }

        switch (option) {
            case FIND_BY_PRICE -> processGetByPriceRequest();
            case FIND_BY_MAKE_MODEL -> processGetByMakeModelRequest();
            case FIND_BY_YEAR -> processGetByYearRequest();
            case FIND_BY_COLOR -> processGetByColorRequest();
            case FIND_BY_MILEAGE -> processGetByMileageRequest();
            case FIND_BY_TYPE -> processGetByVehicleTypeRequest();
            case LIST_ALL -> processAllVehiclesRequest();
            case ADD_VEHICLE -> processAddVehicleRequest();
            case REMOVE_VEHICLE -> processRemoveVehicleRequest();
            case QUIT -> { }
        }
    }

    private void processGetByPriceRequest() {
        double minPrice = readDouble("Minimum price: ");
        double maxPrice = readDouble("Maximum price: ");
        displayVehicles(dealership.getVehiclesByPrice(minPrice, maxPrice));
    }

    private void processGetByMakeModelRequest() {
        String make = readString("Make, leave empty for any: ");
        String model = readString("Model, leave empty for any: ");
        displayVehicles(dealership.getVehiclesByMakeModel(make, model));
    }

    private void processGetByYearRequest() {
        int minYear = readInt("Minimum year: ");
        int maxYear = readInt("Maximum year: ");
        displayVehicles(dealership.getVehiclesByYear(minYear, maxYear));
    }

    private void processGetByColorRequest() {
        String color = readString("Color: ");
        displayVehicles(dealership.getVehiclesByColor(color));
    }

    private void processGetByMileageRequest() {
        int minMileage = readInt("Minimum mileage: ");
        int maxMileage = readInt("Maximum mileage: ");
        displayVehicles(dealership.getVehiclesByMileage(minMileage, maxMileage));
    }

    private void processGetByVehicleTypeRequest() {
        VehicleType vehicleType = readVehicleType("Vehicle type (" + VehicleType.getAllowedValuesText() + "): ");
        displayVehicles(dealership.getVehiclesByType(vehicleType));
    }

    private void processAllVehiclesRequest() {
        displayVehicles(dealership.getAllVehicles());
    }

    private void processAddVehicleRequest() {
        System.out.println("Add a new vehicle");

        int vin = readPositiveInt("VIN: ");
        if (dealership.findVehicleByVin(vin).isPresent()) {
            System.out.println("A vehicle with this VIN already exists. Vehicle was not added.");
            return;
        }

        int year = readYear("Year: ");
        String make = readRequiredString("Make: ");
        String model = readRequiredString("Model: ");
        VehicleType vehicleType = readVehicleType("Vehicle type (" + VehicleType.getAllowedValuesText() + "): ");
        String color = readRequiredString("Color: ");
        int odometer = readPositiveInt("Odometer: ");
        double price = readPositiveDouble("Price: ");

        Vehicle vehicle = new Vehicle(vin, year, make, model, vehicleType, color, odometer, price);
        dealership.addVehicle(vehicle);
        fileManager.saveDealership(dealership);

        System.out.println("Vehicle added and inventory saved.");
    }

    private void processRemoveVehicleRequest() {
        int vin = readPositiveInt("Enter VIN of vehicle to remove: ");

        dealership.findVehicleByVin(vin).ifPresentOrElse(vehicle -> {
            System.out.println("Vehicle found:");
            displayVehicles(List.of(vehicle));

            String confirmation = readString("Remove this vehicle? yes/no: ");
            if (confirmation.equalsIgnoreCase("yes") || confirmation.equalsIgnoreCase("y")) {
                dealership.removeVehicleByVin(vin);
                fileManager.saveDealership(dealership);
                System.out.println("Vehicle removed and inventory saved.");
            } else {
                System.out.println("Remove cancelled.");
            }
        }, () -> System.out.println("No vehicle found with VIN " + vin + "."));
    }

    private void displayVehicles(List<Vehicle> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
            pause();
            return;
        }

        System.out.println();
        System.out.printf("%-8s %-6s %-12s %-15s %-8s %-10s %10s %11s%n",
                "VIN", "YEAR", "MAKE", "MODEL", "TYPE", "COLOR", "ODOMETER", "PRICE");
        System.out.println("---------------------------------------------------------------------------------------");

        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle);
        }

        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("Total vehicles: " + vehicles.size());
        pause();
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String readRequiredString(String prompt) {
        while (true) {
            String value = readString(prompt);
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("This field is required. Please try again.");
        }
    }

    private VehicleType readVehicleType(String prompt) {
        while (true) {
            String input = readRequiredString(prompt);
            var vehicleType = VehicleType.fromString(input);

            if (vehicleType.isPresent()) {
                return vehicleType.get();
            }

            System.out.println("Invalid vehicle type. Allowed values: " + VehicleType.getAllowedValuesText());
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            int number = readInt(prompt);
            if (number >= 0) {
                return number;
            }
            System.out.println("Please enter a positive number.");
        }
    }

    private int readYear(String prompt) {
        while (true) {
            int year = readInt(prompt);
            if (year >= 1886 && year <= 2100) {
                return year;
            }
            System.out.println("Please enter a realistic vehicle year between 1886 and 2100.");
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number, for example 1995.00.");
            }
        }
    }

    private double readPositiveDouble(String prompt) {
        while (true) {
            double number = readDouble(prompt);
            if (number >= 0) {
                return number;
            }
            System.out.println("Please enter a positive number.");
        }
    }

    private void pause() {
        System.out.println();
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
