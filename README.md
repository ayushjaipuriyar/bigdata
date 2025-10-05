# Big Data Analytics for Investment Ranking

This university coursework project demonstrates a data processing pipeline for analyzing and ranking financial assets using Apache Spark. The application processes historical stock prices and company metadata to identify promising investment opportunities based on key performance indicators like Return on Investment (ROI) and volatility.

## Project Overview

The core of the project is a Spark application that performs the following steps:

1.  **Data Ingestion**: It reads two datasets:
    *   **Asset Metadata**: A JSON file (`stock_data.json`) containing company information such as name, industry, sector, and Price-to-Earnings (P/E) ratio.
    *   **Historical Prices**: A CSV file (`all_prices-noHead.csv`) containing daily stock prices (close price) for various tickers.

2.  **Data Preprocessing**:
    *   Filters out assets with null or zero P/E ratios to ensure data quality.
    *   Removes any price records with null values.
    *   Filters the price data to only include records up to a specified date (`2020-04-01` in the code).

3.  **Feature Calculation**: For each stock ticker, it calculates two key financial metrics:
    *   **5-Day Return on Investment (ROI)**: A measure of the short-term profitability of an asset.
    *   **251-Day Volatility**: A measure of the asset's price fluctuation over the past year (approximately 251 trading days), indicating its risk level.

4.  **Data Integration**: The calculated metrics (returns and volatility) are joined with the asset metadata based on the stock ticker.

5.  **Investment Ranking**: The application filters the assets based on predefined thresholds:
    *   **Volatility Ceiling**: Excludes assets with volatility higher than `4.0`.
    *   **P/E Ratio Threshold**: Excludes assets with a P/E ratio greater than `25.0`.
    *   The remaining assets are then ranked in descending order of their 5-day ROI.

6.  **Output**: The final ranked list of assets, along with their calculated features and metadata, is printed to the console and saved to a `SPARK.DONE` file in the `results/` directory. This file also includes the application's start time, end time, and total execution duration.

## Technologies Used

*   **Apache Spark**: The core technology used for distributed data processing and analysis. Specifically, it uses Spark SQL and Spark Datasets/RDDs.
*   **Java 21**: The programming language used to write the Spark application.
*   **Apache Maven**: Used for project build and dependency management.

## How to Build and Run the Project

### Prerequisites

*   Java Development Kit (JDK) 21
*   Apache Maven 3.6+

### Steps

1.  **Clone the Repository**
    ```bash
    git clone <repository-url>
    cd bigdata/BigData-AE-2025-Template
    ```

2.  **Build the Project**
    Use Maven to compile the source code and package it into a JAR file.
    ```bash
    mvn clean package
    ```
    This command will create a JAR file in the `target/` directory (e.g., `BigData-AE-2025-Template-0.0.1-SNAPSHOT.jar`).

3.  **Run the Spark Application**
    You can submit the application to a local Spark instance using the `spark-submit` script.
    ```bash
    spark-submit \
      --class bigdata.app.AssessedExercise \
      --master local[4] \
      target/BigData-AE-2025-Template-0.0.1-SNAPSHOT.jar
    ```

### Configuration via Environment Variables

The application's behavior can be customized using environment variables, which is useful for running it in different environments without changing the code.

*   `SPARK_MASTER`: Sets the Spark master URL. Defaults to `local[4]` for local execution.
*   `BIGDATA_PRICES`: Specifies the absolute path to the stock prices CSV file. Defaults to `resources/all_prices-noHead.csv`.
*   `BIGDATA_ASSETS`: Specifies the absolute path to the asset metadata JSON file. Defaults to `resources/stock_data.json`.
*   `BIGDATA_RESULTS`: Specifies the directory path where the output file should be saved. Defaults to `results/`.

**Example with custom paths:**
```bash
export BIGDATA_PRICES="/path/to/my/prices.csv"
export BIGDATA_ASSETS="/path/to/my/assets.json"

spark-submit \
  --class bigdata.app.AssessedExercise \
  --master local[4] \
  target/BigData-AE-2025-Template-0.0.1-SNAPSHOT.jar
```

## Project Code Structure

```
.
├── pom.xml                 # Maven build configuration
├── resources/              # Default data files
│   ├── all_prices-noHead.csv
│   └── stock_data.json
├── results/                # Default output directory
└── src/main/java/bigdata/
    ├── app/                # Main application logic
    │   └── AssessedExercise.java
    ├── objects/            # Java objects (POJOs) for data modeling
    ├── technicalindicators/# Logic for calculating financial metrics
    ├── transformations/    # Custom Spark data transformation functions
    └── util/               # Helper and utility functions
```