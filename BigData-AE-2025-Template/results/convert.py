import csv
import re

# Input and output file paths
input_file = "SPARK.DONE"  # Change this to your actual file name
output_file = "output.csv"

# Regex patterns to match data fields
rank_pattern = re.compile(r"Rank (\d+): (.+?) \((.+?)\)")
field_pattern = re.compile(
    r"(?: *)-(?: *)(Industry|Sector|Returns|Volatility|P\/E Ratio): (.+)"
)

# Prepare to store parsed data
data = []

# Read and parse the input file
with open(input_file, "r") as file:
    current_entry = None
    for line in file:
        line = line.strip()
        rank_match = rank_pattern.match(line)
        field_match = field_pattern.match(line)
        if rank_match:
            if current_entry:
                data.append(current_entry)
            rank, name, ticker = rank_match.groups()
            current_entry = {
                "Rank": rank,
                "Name": name,
                "Ticker": ticker,
                "Industry": "",
                "Sector": "",
                "Returns": "",
                "Volatility": "",
                "P/E Ratio": "",
            }
        elif field_match and current_entry:
            field, value = field_match.groups()
            if field in current_entry:
                current_entry[field] = value

    if current_entry:  # Save the last entry
        data.append(current_entry)

# Define CSV headers
headers = [
    "Rank",
    "Name",
    "Ticker",
    "Industry",
    "Sector",
    "Returns",
    "Volatility",
    "P/E Ratio",
]

# Write to CSV
with open(output_file, "w", newline="") as csvfile:
    writer = csv.DictWriter(csvfile, fieldnames=headers)
    writer.writeheader()
    writer.writerows(data)

print(f"CSV file '{output_file}' has been created.")
