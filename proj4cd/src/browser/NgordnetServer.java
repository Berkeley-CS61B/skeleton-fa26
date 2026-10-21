package browser;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by hug.
 */
public class NgordnetServer {
    private final Map<String, NgordnetQueryHandler> handlers = new LinkedHashMap<>();

    public void register(String url, NgordnetQueryHandler nqh) {
        handlers.put("/" + url, nqh);
    }

    public void startUp() {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("static", Location.EXTERNAL);

            /* Allow for all origin requests (since this is not an authenticated server, we do not
             * care about CSRF).  */
            config.routes.before(context -> {
                context.header("Access-Control-Allow-Origin", "*");
                context.header("Access-Control-Request-Method", "*");
                context.header("Access-Control-Allow-Headers", "*");
            });

            handlers.forEach((url, handler) -> config.routes.get(url, handler));
        });
        app.start(4567);
    }
}
