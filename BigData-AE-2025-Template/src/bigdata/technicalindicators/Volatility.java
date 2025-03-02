package bigdata.technicalindicators;
//

import bigdata.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class Volatility {

    public static double calculate(List<Double> closePrices) {
        if (closePrices.size() < 2) return 0d;

        List<Double> currentClose = closePrices.subList(1, closePrices.size());
        List<Double> previousClose = closePrices.subList(0, closePrices.size() - 1);

        List<Double> volitilities = new ArrayList<Double>(currentClose.size());
        for (int i = 0; i < currentClose.size(); i++) {
            double currentClosePrice = currentClose.get(i);
            double previousClosePrice = previousClose.get(i);

            double vol = (currentClosePrice - previousClosePrice) / previousClosePrice;
            if (vol > 0) vol = Math.log(vol);
            else if (vol < 0) vol = -Math.log(-vol);

            volitilities.add(vol);
        }


        return MathUtils.std(volitilities);


    }

}
//
//public class Volatility {
//
//	public static double calculate(List<Double> closePrices) {
//		if (closePrices.size() < 2) return 0d;
//
//		List<Double> currentClose = closePrices.subList(1, closePrices.size());
//		List<Double> previousClose = closePrices.subList(0, closePrices.size() - 1);
//
//		List<Double> volatilities = new ArrayList<>(currentClose.size());
//
//		for (int i = 0; i < currentClose.size(); i++) {
//			double currentClosePrice = currentClose.get(i);
//			double previousClosePrice = previousClose.get(i);
//
//			// Ensure no division by zero and handle small price movements
//			if (previousClosePrice == 0) {
//				volatilities.add(0d);  // No volatility if previous price is zero
//			} else {
//				double vol = (currentClosePrice - previousClosePrice) / previousClosePrice;
//				// Log transformation only if the value is non-zero
//				if (vol != 0) {
//					vol = Math.log(Math.abs(vol)); // Log of absolute value to avoid errors
//				}
//				volatilities.add(vol);
//			}
//		}
//
//		return MathUtils.std(volatilities);
//	}
//}
//
/// **
// * Calculates the volatility of asset prices based on log returns.
// * The volatility is measured as the standard deviation of log returns.
// */
//public class Volatility {
//
//	/**
//	 * Calculates the volatility as the standard deviation of log returns.
//	 *
//	 * @param closePrices A list of closing prices.
//	 * @return The calculated volatility.
//	 */
//	public static double calculate(List<Double> closePrices) {
//		if (closePrices == null || closePrices.size() < 2) {
//			return 0d;  // Not enough data to calculate volatility
//		}
//
//		List<Double> logReturns = new ArrayList<>(closePrices.size() - 1);
//
//		for (int i = 1; i < closePrices.size(); i++) {
//			double previousClosePrice = closePrices.get(i - 1);
//			double currentClosePrice = closePrices.get(i);
//
//			// Ensure no division by zero and handle small price movements
//			if (previousClosePrice == 0) {
//				logReturns.add(0d);  // No volatility if previous price is zero
//			} else {
//				double priceChange = (currentClosePrice - previousClosePrice) / previousClosePrice;
//				// Apply log transformation of absolute price change to avoid errors
//				if (priceChange != 0) {
//					logReturns.add(Math.log(Math.abs(priceChange)));
//				} else {
//					logReturns.add(0d);  // Handle the case where there is no change
//				}
//			}
//		}
//
//		// Return the standard deviation of the log returns as the volatility
//		return MathUtils.std(logReturns);
//	}
//}


//
///**
// * Calculates the volatility of asset prices based on log returns.
// * The volatility is measured as the standard deviation of log returns.
// */
//public class Volatility {
//
//	private static final double EPSILON = 1e-10; // Small value to avoid log of zero
//
//	/**
//	 * Calculates the volatility as the standard deviation of log returns.
//	 *
//	 * @param closePrices A list of closing prices.
//	 * @return The calculated volatility.
//	 */
//	public static double calculate(List<Double> closePrices) {
//		if (closePrices == null || closePrices.size() < 2) {
////			throw new IllegalArgumentException("Not enough data to calculate volatility.");
//			System.err.println("Not enough data to calculate volatility.");
//			return 0d;
//		}
//        if (closePrices.size() < 251) {
//			System.err.println("Less than 251 closePrices. Available: " + closePrices.size());
//		}
//
//
//		double sumLogReturns = 0d;
//		double sumSquaredLogReturns = 0d;
//		int count = 0;
//
//		for (int i = 1; i < closePrices.size(); i++) {
//			double previousClosePrice = closePrices.get(i - 1);
//			double currentClosePrice = closePrices.get(i);
//
//			// Ensure no division by zero and handle small price movements
//			if (previousClosePrice == 0) {
//				continue;  // Skip if previous close is zero
//			}
//
//			double priceChange = (currentClosePrice - previousClosePrice) / previousClosePrice;
//			double logReturn = (priceChange != 0) ? Math.log(Math.abs(priceChange) + EPSILON) : 0d;
//
//			sumLogReturns += logReturn;
//			sumSquaredLogReturns += logReturn * logReturn;
//			count++;
//		}
//
//		if (count < 2) {
//			return 0d; // Insufficient data after filtering
//		}
//
//		double meanLogReturn = sumLogReturns / count;
//		double variance = (sumSquaredLogReturns / count) - (meanLogReturn * meanLogReturn);
//		return Math.sqrt(variance);
//	}
//}