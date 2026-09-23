import edu.princeton.cs.algs4.In;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// You do not need to modify or submit this file.

public class ParseUtils {

    // Star utils

    /**
     * Read a CSV with header: name,distance_ly,mass_Msun,m_V
     * Returns a list of Star objects. Robust to blank lines and a few formats:
     *  - mass values like "0.45-0.48" (stored as the average)
     *  - unicode minus signs
     */
    public static List<Star> readCsv(In in) {
        List<Star> stars = new ArrayList<>();
        if (in.isEmpty()) {
            return stars;
        }

        // Skip header if present
        String first = in.readLine();
        if (first == null) {
            return stars;
        }

        boolean headerLooksRight = first.toLowerCase().startsWith("name,");
        if (!headerLooksRight) {
            // First line is data; parse it and proceed
            Star s = parseLine(first);
            if (s != null) {
                stars.add(s);
            }
        }

        while (!in.isEmpty()) {
            String line = in.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            Star s = parseLine(line);
            if (s != null) {
                stars.add(s);
            }
        }
        return stars;
    }

    private static Star parseLine(String line) {
        // Simple CSV split (no quoted commas expected for this dataset)
        String[] parts = line.split(",", -1);
        if (parts.length < 4) {
            return null;
        }

        String name = parts[0].trim();
        Double distance = tryParseDouble(parts[1]);
        Double mass = tryParseMass(parts[2]); // allows ranges like "0.39-0.42"
        Double mV = tryParseDouble(parts[3]);

        if (name.isEmpty() || distance == null || mV == null) {
            return null;
        }
        return new Star(name, distance, mass, mV);
    }

    private static Double tryParseDouble(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.isEmpty()) {
            return null;
        }
        // normalize unicode minus
        s = s.replace('\u2212', '-');
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private static Double tryParseMass(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.isEmpty()) {
            return null;
        }
        s = s.replace('\u2212', '-'); // unicode minus

        // Handle ranges like "0.45-0.48" or with en/em dash
        String[] dashSplits = s.split("\\s*[-\u2013\u2014]\\s*"); // -, – (en), — (em)
        if (dashSplits.length == 2) {
            Double a = tryParseDouble(dashSplits[0]);
            Double b = tryParseDouble(dashSplits[1]);
            if (a != null && b != null) {
                return (a + b) / 2.0;
            }
        }

        // Fall back to plain parse
        return tryParseDouble(s);
    }

    // Wikipedia utils

    public static final int TIMEOUT = 15000;

    /** Fetches the full HTML for the given URL. Returns the HTML string, or "" on error. */
    public static String fetchHtml(String url) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(TIMEOUT))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(TIMEOUT))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            System.err.println("Failed to fetch page: " + e.getMessage());
            return "";
        }
    }

    /** Convenience overload if you already have the HTML and want to extract words from it. */
    private static String[] wordsFromHtml(String html) {
        if (html == null || html.isEmpty()) {
            return new String[0];
        }

        // 1. Isolate main Wikipedia article text block (#mw-content-text / .mw-parser-output or #bodyContent)
        String content = html;
        Pattern parserOutputPattern = Pattern.compile("(?s)<div[^>]*class=[\"'][^\"']*mw-parser-output[^\"']*[\"'][^>]*>(.*)");
        Matcher matcher = parserOutputPattern.matcher(html);

        if (matcher.find()) {
            content = matcher.group(1);
        } else {
            Pattern bodyContentPattern = Pattern.compile("(?s)<div[^>]*id=[\"']bodyContent[\"'][^>]*>(.*)");
            Matcher bodyMatcher = bodyContentPattern.matcher(html);
            if (bodyMatcher.find()) {
                content = bodyMatcher.group(1);
            }
        }

        // 2. Remove script and style tags
        content = content.replaceAll("(?s)<script[^>]*>.*?</script>", " ")
                .replaceAll("(?s)<style[^>]*>.*?</style>", " ");

        // 3. Remove tables (infoboxes, navboxes), gallery/thumb divs, reflists, TOC, edit links
        content = content.replaceAll("(?s)<table[^>]*>.*?</table>", " ")
                .replaceAll("(?s)<figure[^>]*>.*?</figure>", " ")
                .replaceAll("(?s)<div[^>]*class=[\"'][^\"']*(?:thumb|gallery|toc|reflist|mw-references-wrap)[^\"']*[\"'][^>]*>.*?</div>", " ")
                .replaceAll("(?s)<span[^>]*class=[\"'][^\"']*mw-editsection[^\"']*[\"'][^>]*>.*?</span>", " ");

        // 4. Strip remaining HTML tags
        String text = content.replaceAll("<[^>]+>", " ");

        // 5. Clean up non-breaking spaces, entities, and whitespace
        text = text.replace("&nbsp;", " ")
                .replace('\u00A0', ' ')
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"");

        text = text.replaceAll("\\s+", " ").trim();
        if (text.isEmpty()) {
            return new String[0];
        }

        return text.split("\\s+");
    }

    public static String[] fetchWords(String url) {
        String html = fetchHtml(url);
        return wordsFromHtml(html);
    }

    public static void main(String[] args) {
        String url = "https://en.wikipedia.org/wiki/Cat";

        String[] words = fetchWords(url);
        if (words.length >= 3) {
            System.out.println("The first 3 words are: " + words[0] + ", "  + words[1] + ", " + words[2]);
        } else {
            System.out.println("Fetched " + words.length + " words.");
        }
    }
}
