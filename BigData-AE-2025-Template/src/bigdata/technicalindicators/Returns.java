package bigdata.technicalindicators;
//

import java.util.List;
//

/// **
// * Returns the percentage change in price over the period considered
// * @author richardm
// *
// */
public class Returns {

    public static double calculate(int numDays, List<Double> closePrices) {
        if (closePrices.size() < numDays + 1) return 0d;

        Double previousPrice = closePrices.get(closePrices.size() - (numDays + 1));
        Double currentPrice = closePrices.getLast();

        Double priceChange = currentPrice - previousPrice;
        return priceChange / previousPrice;

    }
}
/// /public class Returns {

/// /    public static Double calculate(int numDays, List<Double> closePrices) {
/// /        if (closePrices == null || closePrices.size() < numDays) {
/// /            System.err.println("Insufficient data for calculating returns. numDays: " + numDays + ", available prices: " + (closePrices == null ? 0 : closePrices.size()));
/// /            return 0d;
/// /        }
/// /        try {
/// /            double initialPrice = closePrices.getFirst();
/// /            double finalPrice = closePrices.get(numDays - 1);
/// /            return (finalPrice - initialPrice) / initialPrice;
/// /        } catch (Exception e) {
/// /            System.err.println("Error calculating returns: " + e.getMessage());
/// /            return 0d;
/// /        }
/// /    }
/// /}
//
//
//
//
//
//
//
//public class Returns {
//
//    /**
//     * Calculates the Return on Investment (ROI) over the specified period.
//     * ROI is calculated as the percentage change between the initial and final prices over the given number of days.
//     *
//     * @param numDays      The number of days to calculate ROI over.
//     * @param closePrices  The list of closing prices, sorted in chronological order (oldest to most recent).
//     * @return             The ROI as a percentage. If insufficient data is provided, returns 0%.
//     */
//    public static double calculate(int numDays, List<Double> closePrices) {
//        // Check if closePrices list is null or does not have enough data points
//        if (closePrices == null || closePrices.size() < numDays) {
//            System.err.println("Insufficient data for " + numDays + " days.");
//            return 0d; // Return 0 if there's not enough data
//        }
//
//        try {
//            // Calculate the initial price (price from 'numDays' ago)
//            double initialPrice = closePrices.get(closePrices.size() - numDays); // Getting the price 'numDays' ago
//
//            // Get the final (most recent) closing price
//            double finalPrice = closePrices.getLast();
//
//            // Calculate the Return on Investment (ROI) as a percentage
//
//            // Return the ROI as a percentage
//            return ((finalPrice - initialPrice) / initialPrice) * 100;
//        } catch (IndexOutOfBoundsException e) {
//            // Catch potential errors when accessing elements in the list
//            System.err.println("Error accessing data for calculation: " + e.getMessage());
//            return 0d; // Return 0 if an exception occurs during calculation
//        } catch (Exception e) {
//            // Catch any other unexpected errors and log them
//            System.err.println("Unexpected error while calculating ROI: " + e.getMessage());
//            return 0d; // Return 0 in case of unexpected errors
//        }
//    }
//}