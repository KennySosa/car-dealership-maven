package com.skills4it.dealership.Helper;

import com.skills4it.dealership.models.enums.VehicleType;

import java.util.Scanner;

public class Helper {

    private static final Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static String readRequiredString(String prompt) {
        while (true) {
            String value = readString(prompt);
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("This field is required. Please try again.");
        }
    }

    public static VehicleType readVehicleType(String prompt) {
        while (true) {
            String input = readRequiredString(prompt);
            var vehicleType = VehicleType.fromString(input);

            if (vehicleType.isPresent()) {
                return vehicleType.get();
            }

            System.out.println("Invalid vehicle type. Allowed values: " + VehicleType.getAllowedValuesText());
        }
    }

    public static int readInt(String prompt) {
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

    public static int readPositiveInt(String prompt) {
        while (true) {
            int number = readInt(prompt);
            if (number >= 0) {
                return number;
            }
            System.out.println("Please enter a positive number.");
        }
    }

    public static int readYear(String prompt) {
        while (true) {
            int year = readInt(prompt);
            if (year >= 1886 && year <= 2100) {
                return year;
            }
            System.out.println("Please enter a realistic vehicle year between 1886 and 2100.");
        }
    }

    public static double readDouble(String prompt) {
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

    public static double readPositiveDouble(String prompt) {
        while (true) {
            double number = readDouble(prompt);
            if (number >= 0) {
                return number;
            }
            System.out.println("Please enter a positive number.");
        }
    }

    public static void pause(String prompt) {
        System.out.println();
        System.out.print(prompt);
        scanner.nextLine();
    }
}