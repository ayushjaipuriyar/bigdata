package bigdata.objects;

import java.io.Serializable;

public class AssetFeatures implements Serializable {

	private static final long serialVersionUID = -5189657923034324108L;

	double assetReturn;
	double assetVolatility;
	double peRatio;
	double trueRange; // Added trueRange

	public AssetFeatures() {}

	public double getAssetReturn() {
		return assetReturn;
	}

	public void setAssetReturn(double assetReturn) {
		this.assetReturn = assetReturn;
	}

	public double getAssetVolatility() {
		return assetVolatility;
	}

	public void setAssetVolatility(double assetVolatility) {
		this.assetVolatility = assetVolatility;
	}

	public double getPeRatio() {
		return peRatio;
	}

	public void setPeRatio(double peRatio) {
		this.peRatio = peRatio;
	}

	public double getTrueRange() {
		return trueRange;
	}

	public void setTrueRange(double trueRange) {
		this.trueRange = trueRange;
	}

	@Override
	public String toString() {
		return String.format("AssetFeatures [assetReturn=%.2f, assetVolatility=%.2f, peRatio=%.2f, trueRange=%.2f]",
				assetReturn, assetVolatility, peRatio, trueRange);
	}
}
