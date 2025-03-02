//package bigdata.objects;
//
//import java.io.Serializable;
//import java.util.Arrays;
//
//public class AssetRanking implements Serializable{
//
//	private static final long serialVersionUID = 4427588018088441273L;
//
//	Asset[] assetRanking = null;
//
//	public AssetRanking() {
//		assetRanking = new Asset[5];
//	}
//
//	public AssetRanking(Asset[] assetRanking) {
//		super();
//		this.assetRanking = assetRanking;
//	}
//
//	public Asset[] getAssetRanking() {
//		return assetRanking;
//	}
//
//	public void setAssetRanking(Asset[] assetRanking) {
//		this.assetRanking = assetRanking;
//	}
//
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		for (int i=0; i<assetRanking.length; i++) {
//			builder.append("Rank ");
//			builder.append(i+1);
//			builder.append(": ");
//
//			Asset a = assetRanking[i];
//
//			if (a==null) {
//				builder.append("NULL\n");
//				builder.append("\n");
//				continue;
//			}
//
//			builder.append(a.getName());
//			builder.append(" (");
//			builder.append(a.getTicker());
//			builder.append(")");
//			builder.append("\n");
//			builder.append("  - Industry: ");
//			builder.append(a.getIndustry());
//			builder.append("\n");
//			builder.append("  - Sector: ");
//			builder.append(a.getSector());
//			builder.append("\n");
//			builder.append("  - Returns: ");
//			builder.append(a.getFeatures().getAssetReturn());
//			builder.append("\n");
//			builder.append("  - Volatility: ");
//			builder.append(a.getFeatures().getAssetVolatility());
//			builder.append("\n");
//			builder.append("  - P/E Ratio: ");
//			builder.append(String.format("%.2f", a.getFeatures().getPeRatio()));
//			builder.append("\n");
//			builder.append("\n");
//		}
//
//		return builder.toString();
//	}
//
//	public void addAsset(Asset asset) {
//		// Resize array if needed or shift elements to make space
//		for (int i = 0; i < assetRanking.length; i++) {
//			if (assetRanking[i] == null) {
//				assetRanking[i] = asset;
//				return;
//			}
//		}
//		// If array is full, you could expand it, e.g., double its size:
////		assetRanking = Arrays.copyOf(assetRanking, assetRanking.length * 2);
////		assetRanking[assetRanking.length / 2] = asset; // Add asset at the new space
//	}
//}


package bigdata.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AssetRanking implements Serializable {

	private static final long serialVersionUID = 4427588018088441273L;

	private List<Asset> assetRanking = new ArrayList<>();

	private final int size = 5;

	public AssetRanking() {}

	public AssetRanking(List<Asset> assetRanking) {
		this.assetRanking = new ArrayList<>(assetRanking);
	}

	public List<Asset> getAssetRanking() {
		return assetRanking;
	}

	public void setAssetRanking(List<Asset> assetRanking) {
		this.assetRanking = new ArrayList<>(assetRanking);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < assetRanking.size(); i++) {
			builder.append("Rank ").append(i + 1).append(": ");
			Asset a = assetRanking.get(i);
			if (a == null) {
				builder.append("NULL\n\n");
				continue;
			}
			builder.append(a.getName()).append(" (").append(a.getTicker()).append(")\n")
					.append("  - Industry: ").append(a.getIndustry()).append("\n")
					.append("  - Sector: ").append(a.getSector()).append("\n")
					.append("  - Returns: ").append(a.getFeatures().getAssetReturn()).append("\n")
					.append("  - Volatility: ").append(a.getFeatures().getAssetVolatility()).append("\n")
					.append("  - P/E Ratio: ").append(String.format("%.2f", a.getFeatures().getPeRatio())).append("\n\n");
		}
		return builder.toString();
	}

	public void addAsset(Asset asset) {
		if (assetRanking.size() < this.size) {
			assetRanking.add(asset);
		}
	}
}
