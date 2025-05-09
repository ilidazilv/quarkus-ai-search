package dev.ilidaz.services;

import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for extracting and replacing JSON objects in text
 */
@ApplicationScoped
public class JsonTextProcessor {

    private static final Pattern JSON_PATTERN = Pattern.compile("\\{[^{}]*\\}");

    // Regular expression for matching standard UUID format
    private static final Pattern UUID_PATTERN = Pattern.compile(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
    );

    // Regular expression for UUID without dashes
    private static final Pattern UUID_NO_DASH_PATTERN = Pattern.compile(
            "[0-9a-fA-F]{32}"
    );

    /**
     * Find all UUIDs in a string
     *
     * @param input The string to search
     * @return List of found UUID strings
     */
    public static List<UUID> findUuidsInString(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }

        List<UUID> results = new ArrayList<>();

        // Find standard UUIDs with dashes
        Matcher matcher = UUID_PATTERN.matcher(input);
        while (matcher.find()) {
            String found = matcher.group();
            try {
                // Validate that it's a real UUID by parsing it
                results.add(UUID.fromString(found));
            } catch (IllegalArgumentException e) {
                // Skip invalid UUIDs
            }
        }

        // Optionally find UUIDs without dashes (uncomment if needed)
        /*
        matcher = UUID_NO_DASH_PATTERN.matcher(input);
        while (matcher.find()) {
            String found = matcher.group();
            try {
                // Try to convert to standard UUID format and validate
                String withDashes = found.replaceFirst(
                        "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{12})",
                        "$1-$2-$3-$4-$5"
                );
                UUID.fromString(withDashes);
                results.add(found);
            } catch (IllegalArgumentException e) {
                // Skip invalid UUIDs
            }
        }
        */

        return results;
    }

    /**
     * Replaces only the first JSON object in text using a custom formatter function
     *
     * @param text Text containing JSON objects
     * @param formatter Function that takes a JsonObject and returns a replacement string
     * @return Text with first JSON object replaced according to formatter function
     */
    public String replaceFirstJsonWithCustomFormat(String text, Function<JsonObject, String> formatter) {
        Matcher matcher = JSON_PATTERN.matcher(text);

        if (matcher.find()) {
            String jsonStr = matcher.group();
            try {
                JsonObject jsonObject = new JsonObject(jsonStr);
                String replacement = formatter.apply(jsonObject);
                return text.substring(0, matcher.start()) +
                        replacement +
                        text.substring(matcher.end());
            } catch (Exception e) {
                Log.warn("Failed to process JSON: " + jsonStr, e);
                return text.substring(0, matcher.start()) +
                        "[Invalid JSON]" +
                        text.substring(matcher.end());
            }
        }

        return text; // No JSON found, return original text
    }

    /**
     * Finds the first JSON object in text and returns it
     *
     * @param text Text containing JSON objects
     * @return Optional containing the first parsed JsonObject, or empty if none found
     */
    public Optional<JsonObject> findFirstJsonObject(String text) {
        Matcher matcher = JSON_PATTERN.matcher(text);

        if (matcher.find()) {
            String jsonStr = matcher.group();
            try {
                JsonObject jsonObject = new JsonObject(jsonStr);
                return Optional.of(jsonObject);
            } catch (Exception e) {
                Log.warn("Failed to parse JSON: " + jsonStr, e);
            }
        }

        return Optional.empty();
    }

    /**
     * Replaces only the first JSON object in text with a static value
     *
     * @param text Text containing JSON objects
     * @param replacementValue Static value to replace the first JSON object with
     * @return Text with first JSON object replaced
     */
    public String replaceFirstJsonWithStaticValue(String text, String replacementValue) {
        Matcher matcher = JSON_PATTERN.matcher(text);
        if (matcher.find()) {
            return text.substring(0, matcher.start()) +
                    replacementValue +
                    text.substring(matcher.end());
        }
        return text; // No JSON found, return original text
    }

    /**
     * Extracts JSON objects from text
     *
     * @param text Text containing JSON objects
     * @return List of parsed JsonObject instances
     */
    public List<JsonObject> extractJsonObjects(String text) {
        List<JsonObject> result = new ArrayList<>();
        Matcher matcher = JSON_PATTERN.matcher(text);

        while (matcher.find()) {
            String jsonStr = matcher.group();
            try {
                JsonObject jsonObject = new JsonObject(jsonStr);
                result.add(jsonObject);
            } catch (Exception e) {
                Log.warn("Failed to parse JSON: " + jsonStr, e);
            }
        }

        return result;
    }

    /**
     * Replaces JSON objects in text with a static value
     *
     * @param text Text containing JSON objects
     * @param replacementValue Static value to replace JSON objects with
     * @return Text with JSON objects replaced
     */
    public String replaceJsonWithStaticValue(String text, String replacementValue) {
        return JSON_PATTERN.matcher(text).replaceAll(replacementValue);
    }

    /**
     * Replaces JSON objects in text using a custom formatter function
     *
     * @param text Text containing JSON objects
     * @param formatter Function that takes a JsonObject and returns a replacement string
     * @return Text with JSON objects replaced according to formatter function
     */
    public String replaceJsonWithCustomFormat(String text, Function<JsonObject, String> formatter) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = JSON_PATTERN.matcher(text);

        while (matcher.find()) {
            String jsonStr = matcher.group();
            try {
                JsonObject jsonObject = new JsonObject(jsonStr);
                String replacement = formatter.apply(jsonObject);
                // Quote replacement string to avoid issues with special regex chars
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            } catch (Exception e) {
                Log.warn("Failed to process JSON: " + jsonStr, e);
                matcher.appendReplacement(result, "[Invalid JSON]");
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }
}