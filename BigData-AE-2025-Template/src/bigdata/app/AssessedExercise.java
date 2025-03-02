package bigdata.app;

import bigdata.objects.*;
import bigdata.technicalindicators.Returns;
import bigdata.technicalindicators.Volatility;
import bigdata.transformations.filters.NullPriceFilter;
import bigdata.transformations.maps.PriceReaderMap;
import bigdata.transformations.pairing.AssetMetadataPairing;
import bigdata.util.TimeUtilFunctions;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import scala.Tuple2;
import scala.Tuple3;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
        spark.sparkContext().setLogLevel("ERROR");
        spark.conf().set("spark.sql.session.timeZone", "Europe/London");

        String pricesFile = System.getenv("BIGDATA_PRICES");
        if (pricesFile == null) pricesFile = "resources/all_prices-noHead.csv";

        String assetsFile = System.getenv("BIGDATA_ASSETS");
        if (assetsFile == null) assetsFile = "resources/stock_data.json";

        Dataset<Row> assetRows = spark.read().option("multiLine", true).json(assetsFile);
//		JavaPairRDD<String, AssetMetadata> assetMetadata = assetRows.toJavaRDD().mapToPair(new AssetMetadataPairing());
        JavaPairRDD<String, AssetMetadata> assetMetadata = assetRows.toJavaRDD()
                .filter(row -> {
                    Double peRatio = row.getAs("price_earning_ratio");  // Access the P/E ratio directly
                    return peRatio != null && peRatio > 0;  // Filter out null or zero P/E ratios
                })
                .mapToPair(new AssetMetadataPairing());

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
//		pr.printSchema();
        TimeUtilFunctions.registerUDFs(spark);


        // Filter prices before the datasetEndDate
        Dataset<Row> filteredPrices = pr
                .withColumn("price", functions.col("closePrice").cast("double"))
                .withColumn("ticker", functions.col("stockTicker"))
                .filter(functions.callUDF("fromDateParts", functions.col("year"), functions.col("month"), functions.col("day")).leq(functions.lit(datasetEndDate)))
                .orderBy(functions.col("year").desc(), functions.col("month").desc(), functions.col("day").desc());

        Dataset<Row> calculatedStats = filteredPrices.groupBy("stockTicker")
                .agg(
                        functions.collect_list("price").alias("prices")
                )
                .map(new CalculateReturnsAndVolatility(),
                        Encoders.tuple(Encoders.STRING(), Encoders.DOUBLE(), Encoders.DOUBLE()))
                .toDF("stockTicker", "returns", "volatility");

        // Join asset metadata with price statistics
        Dataset<Row> assetMetadataDS = spark.createDataset(
                        assetMetadata.mapToPair(pair -> new Tuple2<>(pair._1, pair._2)).rdd(),
                        Encoders.tuple(Encoders.STRING(), Encoders.bean(AssetMetadata.class)))
                .toDF("stockTicker", "metadata");

        // Join Returns, Volatility, and True Range with Asset Metadata
        Dataset<Row> rankedAssets = assetMetadataDS
                .join(calculatedStats, "stockTicker")
                .filter(functions.col("metadata.priceEarningRatio").leq(peRatioThreshold))  // Filter by P/E ratio
                .filter(functions.col("volatility").leq(volatilityCeiling))  // Filter by volatility
                .orderBy(functions.desc("returns"));  // Rank by returns (descending)
//        rankedAssets.show(10, false);

        // Collect ranked assets
        List<Row> rankedList = rankedAssets.collectAsList();
        AssetRanking finalRanking = new AssetRanking();

        // Iterate over the ranked list and create Asset objects
        for (Row row : rankedList) {
            String ticker = row.getAs("stockTicker");  // Get stock ticker by field name
            Row metadataRow = row.getAs("metadata");   // Get metadata struct by field name

            // Convert Row to AssetMetadata
            AssetMetadata metadata = new AssetMetadata(metadataRow, "priceEarningRatio");

            // Create an AssetFeatures object
            AssetFeatures features = new AssetFeatures();
            features.setAssetReturn(row.getAs("returns"));       // 5-day return by name
            features.setAssetVolatility(row.getAs("volatility")); // 251-day volatility by name
            features.setPeRatio(metadata.getPriceEarningRatio()); // From metadata

            // Create an Asset object using the metadata and features
            Asset asset = new Asset(ticker, features, metadata.getName(), metadata.getIndustry(), metadata.getSector());
            // Add the created Asset to the final ranking
            finalRanking.addAsset(asset);
        }

        return finalRanking;
    }

    public static class CalculateReturnsAndVolatility implements MapFunction<Row, Tuple3<String, Double, Double>> {
        @Override
        public Tuple3<String, Double, Double> call(Row row) {
            String ticker = row.getAs("stockTicker");
            List<Double> pricesList = row.getList(row.fieldIndex("prices"));

            // Calculate 5-day ROI
            double roi = Returns.calculate(5, pricesList);

            // Calculate volatility using the last 251 days' prices
            int numDays = Math.min(pricesList.size(), 251);
            List<Double> recentPrices = pricesList.subList(pricesList.size() - numDays, pricesList.size());
            double vol = Volatility.calculate(recentPrices);

            return new Tuple3<>(ticker, roi, vol);
        }
    }
}