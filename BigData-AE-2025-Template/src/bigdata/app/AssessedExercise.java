package bigdata.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import bigdata.objects.*;
import bigdata.technicalindicators.Returns;
import bigdata.technicalindicators.Volatility;
import bigdata.util.TimeUtilFunctions;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

import bigdata.transformations.filters.NullPriceFilter;
import bigdata.transformations.maps.PriceReaderMap;
import bigdata.transformations.pairing.AssetMetadataPairing;
//import bigdata.util.TimeUtil;
import bigdata.util.MathUtils;
import scala.Tuple2;

public class AssessedExercise {

	public static void main(String[] args) throws InterruptedException {
		String datasetEndDate = "2020-04-01";
		double volatilityCeiling = 4;
		double peRatioThreshold = 25;

		long startTime = System.currentTimeMillis();

		String sparkMasterDef = System.getenv("SPARK_MASTER");
		if (sparkMasterDef == null) {
			File hadoopDIR = new File("resources/hadoop/");
			System.setProperty("hadoop.home.dir", hadoopDIR.getAbsolutePath());
			sparkMasterDef = "local[4]";
		}

		SparkConf conf = new SparkConf().setMaster(sparkMasterDef).setAppName("BigDataAE");
		SparkSession spark = SparkSession.builder().master("local[4]").config(conf).getOrCreate();

		String pricesFile = System.getenv("BIGDATA_PRICES");
		if (pricesFile == null) pricesFile = "resources/all_prices-noHead.csv";

		String assetsFile = System.getenv("BIGDATA_ASSETS");
		if (assetsFile == null) assetsFile = "resources/stock_data.json";

		Dataset<Row> assetRows = spark.read().option("multiLine", true).json(assetsFile);
		JavaPairRDD<String, AssetMetadata> assetMetadata = assetRows.toJavaRDD().mapToPair(new AssetMetadataPairing());

		Dataset<Row> priceRows = spark.read().csv(pricesFile);
		Dataset<Row> priceRowsNoNull = priceRows.filter(new NullPriceFilter());
		Dataset<StockPrice> prices = priceRowsNoNull.map(new PriceReaderMap(), Encoders.bean(StockPrice.class));

		AssetRanking finalRanking = rankInvestments(spark, assetMetadata, prices, datasetEndDate, volatilityCeiling, peRatioThreshold);

		System.out.println(finalRanking);

//		System.out.println("Holding Spark UI open for 1 minute: http://localhost:4040");
//		Thread.sleep(60000);

		spark.close();

		String out = System.getenv("BIGDATA_RESULTS");
		String resultsDIR = "results/";
		if (out != null) resultsDIR = out;

		long endTime = System.currentTimeMillis();

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(resultsDIR).getAbsolutePath() + "/SPARK.DONE")))) {
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("EEE, dd MMM yyyy HH:mm:ss z")
					.withZone(ZoneId.of("Europe/London"));

			String startTimeStr = formatter.format(Instant.ofEpochSecond(startTime / 1000));
			String endTimeStr = formatter.format(Instant.ofEpochSecond(endTime / 1000));

			writer.write("StartTime:" + startTimeStr + '\n');
			writer.write("EndTime:" + endTimeStr + '\n');
			writer.write("Seconds: " + ((endTime - startTime) / 1000) + '\n');
			writer.write("Seconds: " + ((endTime - startTime) / 1000) + '\n');
			writer.write('\n');
			writer.write(finalRanking.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static AssetRanking rankInvestments(SparkSession spark, JavaPairRDD<String, AssetMetadata> assetMetadata, Dataset<StockPrice> prices, String datasetEndDate, double volatilityCeiling, double peRatioThreshold) {
		Dataset<Row> pr = prices.toDF();
		pr.printSchema();
		TimeUtilFunctions.registerUDFs(spark);

		// Filter prices before the datasetEndDate
		Dataset<Row> filteredPrices = pr
				.withColumn("price", functions.col("closePrice").cast("double"))
				.withColumn("ticker", functions.col("stockTicker"))
				.filter(functions.callUDF("fromDateParts", functions.col("year"), functions.col("month"), functions.col("day")).leq(functions.lit(datasetEndDate)));

		// Calculate Returns (5-day return on investment)
		Dataset<Row> returnsStats = filteredPrices.groupBy("stockTicker")
				.agg(
						functions.collect_list("price").alias("prices")
				).map((MapFunction<Row, Tuple2<String, Double>>) row -> {
					String ticker = row.getString(0);
					List<Double> pricesList = row.getList(1);
					double roi = Returns.calculate(5, pricesList);  // 5 days ROI
					return new Tuple2<>(ticker, roi);
				}, Encoders.tuple(Encoders.STRING(), Encoders.DOUBLE()))
				.toDF("stockTicker", "returns");

		// Calculate Volatility (251-day volatility)
		Dataset<Row> volatilityStats = filteredPrices.groupBy("stockTicker")
				.agg(
						functions.collect_list("price").alias("prices")
				).map((MapFunction<Row, Tuple2<String, Double>>) row -> {
					String ticker = row.getString(0);
					List<Double> pricesList = row.getList(1);
					double vol = Volatility.calculate(pricesList);  // 251 days volatility
					return new Tuple2<>(ticker, vol);
				}, Encoders.tuple(Encoders.STRING(), Encoders.DOUBLE()))
				.toDF("stockTicker", "volatility");

		// Calculate True Range (ATR)
		Dataset<Row> trueRangeStats = filteredPrices.groupBy("stockTicker")
				.agg(
						functions.collect_list(functions.struct(
								"date", "openPrice", "highPrice", "lowPrice", "closePrice", "adjustedClosePrice", "volume", "stockTicker"
						)).alias("prices")
				).map((MapFunction<Row, Tuple2<String, Double>>) row -> {
					String ticker = row.getString(0);
					List<Row> priceRows = row.getList(1);

					// Convert Rows to StockPrice objects
//					System.out.println("Converting " + priceRows.size() + " rows to StockPrice objects" + priceRows.toString());
					List<StockPrice> stockPrices = priceRows.stream()
							.map(StockPrice::new)
							.collect(Collectors.toList());

					// Calculate True Range and its average
					List<Double> trueRanges = MathUtils.trueRange(stockPrices);
					double avgTrueRange = MathUtils.mean(trueRanges);  // Ensure mean() correctly calculates averages

					return new Tuple2<>(ticker, avgTrueRange);
				}, Encoders.tuple(Encoders.STRING(), Encoders.DOUBLE()))
				.toDF("stockTicker", "trueRange");

		// Join asset metadata with price statistics
		Dataset<Row> assetMetadataDS = spark.createDataset(
						assetMetadata.mapToPair(pair -> new Tuple2<>(pair._1, pair._2)).rdd(),
						Encoders.tuple(Encoders.STRING(), Encoders.bean(AssetMetadata.class)))
				.toDF("stockTicker", "metadata");

		// Join Returns, Volatility, and True Range with Asset Metadata
		Dataset<Row> rankedAssets = assetMetadataDS
				.join(returnsStats, "stockTicker")
				.join(volatilityStats, "stockTicker")
				.join(trueRangeStats, "stockTicker")
				.filter(functions.col("metadata.priceEarningRatio").leq(peRatioThreshold))  // Filter by P/E ratio
				.filter(functions.col("volatility").leq(volatilityCeiling))  // Filter by volatility
				.orderBy(functions.desc("returns"));  // Rank by returns (descending)

		// Collect ranked assets
		List<Row> rankedList = rankedAssets.collectAsList();
		AssetRanking finalRanking = new AssetRanking();

		// Iterate over the ranked list and create Asset objects
		for (Row row : rankedList) {
			String ticker = row.getString(0);
			Row metadataRow = row.getStruct(1);
			AssetMetadata metadata = new AssetMetadata(metadataRow);

			// Create an AssetFeatures object
			AssetFeatures features = new AssetFeatures();
			features.setAssetReturn(row.getDouble(3));  // 5-day return
			features.setAssetVolatility(row.getDouble(2));  // 251-day volatility
			features.setTrueRange(row.getDouble(4));  // Average True Range
			features.setPeRatio(metadata.getPriceEarningRatio());  // P/E ratio from metadata

			// Create an Asset object using the metadata and features
			Asset asset = new Asset(ticker, features, metadata.getName(), metadata.getIndustry(), metadata.getSector());

			// Add the created Asset to the final ranking
			finalRanking.addAsset(asset);
		}

		return finalRanking;
	}
}
