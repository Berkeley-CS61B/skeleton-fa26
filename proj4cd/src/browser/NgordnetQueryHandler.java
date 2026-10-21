package browser;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.util.Arrays;
import java.util.List;

public abstract class NgordnetQueryHandler implements Handler {
    public abstract String handle(browser.NgordnetQuery q);
    private static final Gson GSON = new Gson();

    public static final int DEFAULT_START_YEAR = 1900;
    public static final int DEFAULT_END_YEAR = 2020;

    private static List<String> commaSeparatedStringToList(String s) {
        String[] requestedWords = s.split(",");
        for (int i = 0; i < requestedWords.length; i += 1) {
            requestedWords[i] = requestedWords[i].trim();
        }
        return Arrays.asList(requestedWords);
    }

    private static browser.NgordnetQuery readQueryMap(Context context) {
        List<String> words = commaSeparatedStringToList(context.queryParam("words"));

        int startYear;
        int endYear;
        int k;

        try {
            startYear = Integer.parseInt(context.queryParam("startYear"));
        } catch (RuntimeException e) {
            startYear = DEFAULT_START_YEAR;
        }

        try {
            endYear = Integer.parseInt(context.queryParam("endYear"));
        } catch (RuntimeException e) {
            endYear = DEFAULT_END_YEAR;
        }

        try {
            k = Integer.parseInt(context.queryParam("k"));
        } catch (RuntimeException e) {
            k = 0;
        }

        return new browser.NgordnetQuery(words, startYear, endYear, k);
    }

    @Override
    public void handle(Context context) throws Exception {
        NgordnetQuery nq = readQueryMap(context);
        String queryResult = handle(nq);
        context.result(GSON.toJson(queryResult));
    }
}
