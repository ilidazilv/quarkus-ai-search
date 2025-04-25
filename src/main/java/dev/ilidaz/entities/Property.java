package dev.ilidaz.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "property_ai")
public class Property extends PanacheEntityBase {
    @Id
    @Column(name = "id")
    public String id;
    @Column(name = "title")
    public String title;
    @Column(name = "description")
    public String description;
    @Column(name = "single_line")
    public String singleLine;

    public static List<Property> searchByTitleLike(String title) {
        return find("title like ?1", "%" + title + "%").list();
    }

    public static Property fromCsvLine(String line) {
        Property property = new Property();

        try {
            // Add error tolerance to the parser
            CSVFormat format = CSVFormat.newFormat(';');

            // Try to fix unclosed quotes by appending a quote if needed
            if (countOccurrences(line, '"') % 2 != 0) {
                line = line + "\"";
            }

            CSVParser parser = CSVParser.parse(line, format);

            // Get the first record
            for (CSVRecord record : parser) {
                if (record.size() > 0) property.setId(record.get(0));
                if (record.size() > 1) property.setTitle(record.get(1));
                if (record.size() > 2) property.setDescription(record.get(2));
                if (record.size() > 3) property.setSingleLine(record.get(3));
                break; // Only process the first record
            }

        } catch (IOException e) {
            // Fallback to a simpler split approach if parsing fails
            String[] values = line.split(",");
            if (values.length > 0) property.setId(cleanValue(values[0]));
            if (values.length > 1) property.setTitle(cleanValue(values[1]));
            if (values.length > 2) property.setDescription(cleanValue(values[2]));
            if (values.length > 3) property.setSingleLine(cleanValue(values[3]));

            System.err.println("Error parsing CSV line, used fallback: " + e.getMessage());
        }

        return property;
    }

    private static String cleanValue(String value) {
        if (value == null) return "";
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}