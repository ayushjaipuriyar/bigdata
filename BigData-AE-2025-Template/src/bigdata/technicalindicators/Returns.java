package bigdata.technicalindicators;

import java.util.List;

/**
 * Returns the percentage change in price over the period considered
 * @author richardm
 *
 */
public class Returns {

//	public static double calculate(int numDays, List<Double> closePrices) {
//		if (closePrices.size() < numDays + 1) return 0d;
//
//        Double previousPrice = closePrices.get(closePrices.size() - (numDays + 1));
//        Double currentPrice = closePrices.get(closePrices.size() - 1);
//
//        Double priceChange = currentPrice - previousPrice;
//        return priceChange / previousPrice;
//
//	}
    public static Double calculate(int numDays, List<Double> closePrices) {
        if (closePrices == null || closePrices.size() < numDays) {
            System.err.println("Insufficient data for calculating returns. numDays: " + numDays + ", available prices: " + (closePrices == null ? 0 : closePrices.size()));
            return 0d;
        }
        try {
            double initialPrice = closePrices.getFirst();
            double finalPrice = closePrices.get(numDays - 1);
            return (finalPrice - initialPrice) / initialPrice;
        } catch (Exception e) {
            System.err.println("Error calculating returns: " + e.getMessage());
            return 0d;
        }
    }

//
//}
//public class Returns {
//
//    public static Double calculate(int numDays, List<Double> closePrices) {
//        // Ensure we have enough data for the specified number of days
//        if (closePrices.size() < numDays + 1) {
//            System.err.println("Not enough data for the specified number of days.");
//            return null; // Returning null for clarity
//        }
//
//        // Get the price 'numDays' ago and the latest price
//        Double previousPrice = closePrices.get(closePrices.size() - (numDays + 1));
//        Double currentPrice = closePrices.get(closePrices.size() - 1);
//
//        // Calculate price change and return as a percentage
//        Double priceChange = currentPrice - previousPrice;
//        return (priceChange / previousPrice) * 100; // Returning percentage change
//    }
}
