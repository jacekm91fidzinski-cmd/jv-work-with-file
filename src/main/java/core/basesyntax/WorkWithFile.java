package core.basesyntax;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class WorkWithFile {
    private static final String SUPPLY = "supply";
    private static final String BUY = "buy";
    private static final String RESULT = "result";
    private static final String DELIMITER = ",";
    private static final int EXPECTED_PARTS = 2;

    public void getStatistic(String fromFileName, String toFileName) {
        List<String> lines = readLines(fromFileName);
        long[] sums = processLines(lines);
        String report = buildReport(sums[0], sums[1]);
        writeReport(toFileName, report);
    }

    private List<String> readLines(String fileName) {

        Path path = Path.of(fileName);
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return reader.lines().toList();
        } catch (IOException e) {
            throw new RuntimeException("Can't read data from the file " + fileName, e);
        }
    }

    /**
     * Process CSV lines and return array {supplySum, buySum}
     */
    private long[] processLines(List<String> lines) {
        long supplySum = 0L;
        long buySum = 0L;

        for (String rawLine : lines) {
            if (rawLine == null) {
                continue;
            }

            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(DELIMITER);
            if (parts.length != EXPECTED_PARTS) {
                throw new RuntimeException(
                        "Invalid record (wrong number of fields): \"" + rawLine + "\"");
            }

            String operation = parts[0].trim();
            String amountStr = parts[1].trim();

            int amount;
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid number in record: \"" + rawLine + "\"");
            }

            if (SUPPLY.equals(operation)) {
                supplySum += amount;
            } else if (BUY.equals(operation)) {
                buySum += amount;
            } else {
                throw new RuntimeException("Unknown operation in record: \"" + rawLine + "\"");
            }
        }
        return new long[] {supplySum, buySum};
    }

    private String buildReport(long supplySum, long buySum) {
        long result = supplySum - buySum;
        return SUPPLY + DELIMITER + supplySum + System.lineSeparator()
                + BUY + DELIMITER + buySum + System.lineSeparator()
                + RESULT + DELIMITER + result + System.lineSeparator();
    }

    private void writeReport(String fileName, String content) {
        Path path = Path.of(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Can't write data to the file " + fileName, e);
        }
    }

}
