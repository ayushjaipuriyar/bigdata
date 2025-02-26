package bigdata.util;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF3;
import org.apache.spark.sql.api.java.UDF1;
import java.time.Instant;
import java.sql.Timestamp;
import org.apache.spark.sql.types.DataTypes;

public class TimeUtilFunctions {

    public static UDF1<String, Timestamp> fromDateUDF() {
        return (String dateString) -> {
            Instant instant = TimeUtil.fromDate(dateString);
            return Timestamp.from(instant);  // Convert Instant to Timestamp
        };
    }
    public static UDF3<Short, Short, Short, Timestamp> fromDatePartsUDF() {
        return (Short year, Short month, Short day) -> {
            Instant instant = TimeUtil.fromDate(year, month, day);
            return Timestamp.from(instant);
        };
    }
    public static void registerUDFs(SparkSession spark) {
        spark.udf().register("fromDate", fromDateUDF(), DataTypes.TimestampType);
        spark.udf().register("fromDateParts", fromDatePartsUDF(), DataTypes.TimestampType);
    }

}
