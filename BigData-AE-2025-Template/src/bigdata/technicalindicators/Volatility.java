package bigdata.technicalindicators;
//
import java.util.ArrayList;
import java.util.List;
//
import bigdata.util.MathUtils;
//
//public class Volatility {
//
//	public static double calculate(List<Double> closePrices) {
//		if (closePrices.size() < 2) return 0d;
//
//        List<Double> currentClose = closePrices.subList(1, closePrices.size());
//        List<Double> previousClose = closePrices.subList(0, closePrices.size() - 1);
//
//        List<Double> volitilities = new ArrayList<Double>(currentClose.size());
//        for (int i=0; i<currentClose.size(); i++) {
//        	double currentClosePrice = currentClose.get(i);
//        	double previousClosePrice = previousClose.get(i);
//
//        	double vol = (currentClosePrice - previousClosePrice) / previousClosePrice;
//        	if (vol>0) vol = Math.log(vol);
//        	else if (vol<0) vol = -Math.log(-vol);
//
//        	volitilities.add(vol);
//        }
//
//
//        return MathUtils.std(volitilities);
//
//
//	}
//
//}
//
public class Volatility {

	public static double calculate(List<Double> closePrices) {
		if (closePrices.size() < 2) return 0d;

		List<Double> currentClose = closePrices.subList(1, closePrices.size());
		List<Double> previousClose = closePrices.subList(0, closePrices.size() - 1);

		List<Double> volatilities = new ArrayList<>(currentClose.size());

		for (int i = 0; i < currentClose.size(); i++) {
			double currentClosePrice = currentClose.get(i);
			double previousClosePrice = previousClose.get(i);

			// Ensure no division by zero and handle small price movements
			if (previousClosePrice == 0) {
				volatilities.add(0d);  // No volatility if previous price is zero
			} else {
				double vol = (currentClosePrice - previousClosePrice) / previousClosePrice;
				// Log transformation only if the value is non-zero
				if (vol != 0) {
					vol = Math.log(Math.abs(vol)); // Log of absolute value to avoid errors
				}
				volatilities.add(vol);
			}
		}

		return MathUtils.std(volatilities);
	}
}
