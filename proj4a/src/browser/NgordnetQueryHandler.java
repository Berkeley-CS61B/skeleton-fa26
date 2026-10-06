package browser;

import com.google.gson.Gson;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Arrays;
import java.util.List;

public abstract class NgordnetQueryHandler implements Route {
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

    private static browser.NgordnetQuery readQueryMap(QueryParamsMap qm) {
        List<String> words = commaSeparatedStringToList(qm.get("words").value());

        int startYear;
        int endYear;
        int k;

        try {
            startYear = Integer.parseInt(qm.get("startYear").value());
        } catch (RuntimeException e) {
            startYear = DEFAULT_START_YEAR;
        }

        try {
            endYear = Integer.parseInt(qm.get("endYear").value());
        } catch (RuntimeException e) {
            endYear = DEFAULT_END_YEAR;
        }

        try {
            k = Integer.parseInt(qm.get("k").value());
        } catch (RuntimeException e) {
            k = 0;
        }

        return new browser.NgordnetQuery(words, startYear, endYear, k);
    }

    @Override
    public String handle(Request request, Response response) throws Exception {
        QueryParamsMap qm = request.queryMap();
        NgordnetQuery nq = readQueryMap(qm);
        String queryResult = handle(nq);
        return GSON.toJson(queryResult);
    }
}
