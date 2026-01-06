package com.example.unittesting;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LineSegmentAnalyzer analyzer = new LineSegmentAnalyzer();

        System.out.println("Line equation: -3x + 5y - 2 = 0");
        System.out.println("Please enter coordinates of the line segment:");

        try {
            System.out.print("Enter x1: ");
            double x1 = scanner.nextDouble();

            System.out.print("Enter y1: ");
            double y1 = scanner.nextDouble();

            System.out.print("Enter x2: ");
            double x2 = scanner.nextDouble();

            System.out.print("Enter y2: ");
            double y2 = scanner.nextDouble();

            System.out.println("\n--- Analysis Result ---");
            String result = analyzer.analyzePosition(x1, y1, x2, y2);
            System.out.println(result);

        } catch (Exception e) {
            System.out.println("Error: Invalid input. Please enter numeric values.");
        } finally {
            scanner.close();
        }
    }
}