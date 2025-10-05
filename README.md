# Big Data Assessed Exercise 2025

This project is a Big Data application that analyzes stock market data to rank investments based on various financial metrics. It leverages Apache Spark to process large datasets efficiently and perform complex calculations in a distributed manner.

## Features

*   **Data Ingestion:** Ingests historical stock prices and asset metadata from CSV and JSON files.
*   **Data Filtering:** Filters out irrelevant or incomplete data to ensure the quality of the analysis.
*   **Financial Metrics Calculation:** Calculates key financial metrics such as:
    *   5-day Return on Investment (ROI)
    *   251-day Volatility
*   **Investment Ranking:** Ranks investments based on a set of criteria, including:
    *   Price-to-Earnings (P/E) ratio
    *   Volatility
    *   Returns
*   **Configurable:** Allows for easy configuration of input/output paths and Spark master through environment variables.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

*   Java 21 or higher
*   Apache Maven 3.6.0 or higher
*   Apache Spark 4.0.0-preview2 or compatible version

### Installation

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/your-username/bigdata.git
    cd bigdata/BigData-AE-2025-Template
    ```

2.  **Build the project:**
    Use Maven to build the project and create the application JAR file:
    ```sh
    mvn clean package
    ```
    This will generate a JAR file in the `target` directory, for example `BigData-AE-2025-Template-0.0.1-SNAPSHOT.jar`.

### Running the application

You can run the application using `spark-submit`. The following command shows an example of how to run the application in local mode:

```sh
spark-submit \
  --class bigdata.app.AssessedExercise \
  --master local[4] \
  target/BigData-AE-2025-Template-0.0.1-SNAPSHOT.jar
```

## Configuration

The application can be configured using the following environment variables:

*   `SPARK_MASTER`: The Spark master URL. Defaults to `local[4]` if not set.
*   `BIGDATA_PRICES`: The path to the CSV file containing stock prices. Defaults to `resources/all_prices-noHead.csv`.
*   `BIGDATA_ASSETS`: The path to the JSON file containing asset metadata. Defaults to `resources/stock_data.json`.
*   `BIGDATA_RESULTS`: The path to the directory where the results will be saved. Defaults to `results/`.

Example of running the application with custom environment variables:

```sh
export SPARK_MASTER="spark://your-spark-master:7077"
export BIGDATA_PRICES="/path/to/your/prices.csv"
export BIGDATA_ASSETS="/path/to/your/assets.json"
export BIGDATA_RESULTS="/path/to/your/results"

spark-submit \
  --class bigdata.app.AssessedExercise \
  --master $SPARK_MASTER \
  target/BigData-AE-2025-Template-0.0.1-SNAPSHOT.jar
```

## Project Structure

```
BigData-AE-2025-Template
├── pom.xml                         # Maven project configuration
├── resources
│   ├── all_prices-noHead.csv       # Default stock prices data
│   └── stock_data.json             # Default asset metadata
├── results
│   └── SPARK.DONE                  # Output file with the ranking results
└── src
    └── main
        └── java
            └── bigdata
                ├── app
                │   └── AssessedExercise.java   # Main application class
                ├── objects                     # Data model classes
                ├── technicalindicators         # Classes for calculating technical indicators
                ├── transformations             # Spark data transformations
                └── util                        # Utility classes
```

## Built With

*   [Apache Spark](https://spark.apache.org/) - The analytics engine for large-scale data processing.
*   [Apache Maven](https://maven.apache.org/) - Dependency Management.