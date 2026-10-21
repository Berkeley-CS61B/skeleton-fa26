package lab9main;

import lab9algorithms.Lab9BFS;
import lab9algorithms.Lab9DFS;
import lab9algorithms.Lab9Dijkstras;
import lab9lib.Lab9Graph;

import io.javalin.Javalin;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

/** Supporting code for Main: draws a Lab9Graph in your web browser and shows
 *  what the classes in lab9algorithms compute on it. You don't need to read
 *  or change this file.
 *
 *  Usage: GraphVisualizerInternals.show(g);
 *
 *  The layout is force directed and is computed here in Java before the
 *  page is served, so the browser just shows the finished picture. Edges
 *  with larger weights are drawn longer, so for a graph of road distances,
 *  far apart cities end up far apart on the screen (roughly).
 *
 *  The page has two dropdowns: the start node (marked S), and which of the
 *  four orders to compute: DFS preorder, DFS postorder, BFS order, or the
 *  order in which Dijkstra's settles nodes (by distance). "Compute" runs
 *  the chosen one from the chosen start and puts the result in a text box;
 *  "Visualize" then steps through whatever order is in the text box, so you
 *  can also type in an order of your own to check. For Dijkstra's, Compute
 *  also draws the shortest paths tree in blue, labels every reachable node
 *  with its distance, and lets you click a node to see the path to it. */
public class GraphVisualizerInternals {
    static final int WIDTH = 1000;
    static final int HEIGHT = 700;
    private static final int MARGIN = 60;
    private static final int ITERATIONS = 800;
    static final String DIJKSTRAS = "Lab9Dijkstras order";

    /** The orderings the page offers, in the order they appear in the dropdown. */
    private static final Map<String, BiFunction<Lab9Graph, String, List<String>>> ORDERINGS = new LinkedHashMap<>();
    static {
        ORDERINGS.put("Lab9DFS preOrder", (g, start) -> new Lab9DFS(g, start).preOrder());
        ORDERINGS.put("Lab9DFS postOrder", (g, start) -> new Lab9DFS(g, start).postOrder());
        ORDERINGS.put("Lab9BFS order", (g, start) -> new Lab9BFS(g, start).order());
        ORDERINGS.put(DIJKSTRAS, GraphVisualizerInternals::dijkstrasOrder);
    }

    /** The nodes reachable from start in the order Dijkstra's algorithm
     *  settles them: by distance from start, with ties broken by position in
     *  g.nodes(). Computed from the distances reported by Lab9Dijkstras. */
    static List<String> dijkstrasOrder(Lab9Graph g, String start) {
        Lab9Dijkstras d = new Lab9Dijkstras(g, start);
        List<String> order = new ArrayList<>();
        for (String v : g.nodes()) {
            if (d.hasPathTo(v)) {
                order.add(v);
            }
        }
        order.sort((a, b) -> Integer.compare(d.distTo(a), d.distTo(b)));  // List.sort is stable
        return order;
    }

    /** Starts a web server on port 4567 showing g and opens a browser tab. */
    public static void show(Lab9Graph g) {
        show(g, 4567);
    }

    /** Starts a web server on the given port showing g and opens a browser tab. */
    public static void show(Lab9Graph g, int port) {
        Map<String, double[]> pos = layout(g);
        String page = render(g, pos);
        Javalin app = Javalin.create(config -> {
            config.routes.get("/", ctx -> ctx.html(page));
            config.routes.get("/order/{name}", ctx -> ctx.contentType("application/json")
                    .result(orderJson(g, ctx.pathParam("name"), ctx.queryParam("start"))));
            config.routes.get("/spt/{source}", ctx -> ctx.contentType("application/json")
                    .result(sptJson(g, ctx.pathParam("source"))));
        });
        app.start(port);
        String url = "http://localhost:" + port + "/";
        IO.println("Visualizer is being served at " + url + " (stop the program to shut it down)");
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception e) {
            IO.println("Could not open a browser automatically; open the URL above yourself.");
        }
    }

    /** Runs the named ordering (see ORDERINGS) on g from start (or from the
     *  first node if start is null) and reports the result as JSON:
     *  {"order": [...]} or {"error": "..."}. */
    static String orderJson(Lab9Graph g, String name, String start) {
        BiFunction<Lab9Graph, String, List<String>> f = ORDERINGS.get(name);
        if (f == null) {
            return "{\"error\": " + json("no ordering named " + name) + "}";
        }
        List<String> order;
        try {
            if (start == null || start.isEmpty()) {
                start = g.nodes().isEmpty() ? "" : g.nodes().get(0);
            }
            order = f.apply(g, start);
        } catch (RuntimeException e) {
            return "{\"error\": " + json(name + " threw " + e) + "}";
        }
        if (order == null) {
            return "{\"error\": " + json(name + " returned null (not implemented yet?)") + "}";
        }
        if (order.isEmpty()) {
            return "{\"error\": " + json(name + " returned no nodes, not even " + start + " (not implemented yet?)") + "}";
        }
        StringBuilder sb = new StringBuilder("{\"order\": [");
        for (int i = 0; i < order.size(); i++) {
            sb.append(i > 0 ? ", " : "").append(json(String.valueOf(order.get(i))));
        }
        return sb.append("]}").toString();
    }

    /** Runs Lab9Dijkstras from source and reports, for every reachable node,
     *  its distance and shortest path: {"dists": {...}, "paths": {...}}, or
     *  {"error": "..."} if the student code fails. */
    static String sptJson(Lab9Graph g, String source) {
        if (!g.nodes().contains(source)) {
            return "{\"error\": " + json("no node named " + source) + "}";
        }
        StringBuilder dists = new StringBuilder();
        StringBuilder paths = new StringBuilder();
        try {
            Lab9Dijkstras d = new Lab9Dijkstras(g, source);
            for (String v : g.nodes()) {
                if (!d.hasPathTo(v)) {
                    continue;
                }
                List<String> path = d.shortestPathTo(v);
                if (path == null) {
                    return "{\"error\": " + json("shortestPathTo(" + v + ") returned null (not implemented yet?)") + "}";
                }
                dists.append(dists.length() > 0 ? ", " : "").append(json(v)).append(": ").append(d.distTo(v));
                paths.append(paths.length() > 0 ? ", " : "").append(json(v)).append(": [");
                for (int i = 0; i < path.size(); i++) {
                    paths.append(i > 0 ? ", " : "").append(json(String.valueOf(path.get(i))));
                }
                paths.append("]");
            }
        } catch (RuntimeException e) {
            return "{\"error\": " + json("Lab9Dijkstras threw " + e) + "}";
        }
        if (dists.length() == 0) {
            return "{\"error\": " + json("hasPathTo returned false for every node, even the source (not implemented yet?)") + "}";
        }
        return "{\"dists\": {" + dists + "}, \"paths\": {" + paths + "}}";
    }

    private static String json(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }

    /** Fruchterman-Reingold force directed layout. Returns x, y for each node.
     *  The simulation runs in unbounded space with a weak pull toward the
     *  center; the result is then rescaled to fit the picture. */
    static Map<String, double[]> layout(Lab9Graph g) {
        List<String> nodes = g.nodes();
        int n = nodes.size();
        Map<String, double[]> pos = new HashMap<>();
        Random rand = new Random(61);   // fixed seed so the same graph always looks the same
        for (String v : nodes) {
            pos.put(v, new double[] {rand.nextDouble() * WIDTH, rand.nextDouble() * HEIGHT});
        }
        if (n <= 1) {
            return fit(pos);
        }

        double maxWeight = 1;
        for (String u : nodes) {
            for (String v : g.neighbors(u)) {
                maxWeight = Math.max(maxWeight, g.edgeWeight(u, v));
            }
        }
        double k = 0.6 * Math.sqrt((double) WIDTH * HEIGHT / n);  // ideal distance between nodes
        double temperature = WIDTH / 8.0;

        for (int iter = 0; iter < ITERATIONS; iter++) {
            Map<String, double[]> disp = new HashMap<>();
            for (String v : nodes) {
                disp.put(v, new double[2]);
            }
            // every pair of nodes repels
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    String u = nodes.get(i);
                    String v = nodes.get(j);
                    double[] d = delta(pos.get(u), pos.get(v));
                    double dist = Math.max(0.01, Math.hypot(d[0], d[1]));
                    double force = k * k / dist;
                    disp.get(u)[0] += d[0] / dist * force;
                    disp.get(u)[1] += d[1] / dist * force;
                    disp.get(v)[0] -= d[0] / dist * force;
                    disp.get(v)[1] -= d[1] / dist * force;
                }
            }
            // every edge attracts, with heavier edges wanting to be longer
            for (String u : nodes) {
                for (String v : g.neighbors(u)) {
                    if (u.compareTo(v) >= 0) {
                        continue;  // each undirected edge once
                    }
                    double ideal = k * (0.8 + 1.2 * g.edgeWeight(u, v) / maxWeight);
                    double[] d = delta(pos.get(u), pos.get(v));
                    double dist = Math.max(0.01, Math.hypot(d[0], d[1]));
                    double force = dist * dist / ideal;
                    disp.get(u)[0] -= d[0] / dist * force;
                    disp.get(u)[1] -= d[1] / dist * force;
                    disp.get(v)[0] += d[0] / dist * force;
                    disp.get(v)[1] += d[1] / dist * force;
                }
            }
            // a weak pull toward the center keeps disconnected pieces from drifting away
            for (String v : nodes) {
                double[] p = pos.get(v);
                disp.get(v)[0] -= (p[0] - WIDTH / 2.0) * 0.02;
                disp.get(v)[1] -= (p[1] - HEIGHT / 2.0) * 0.02;
            }
            // move each node, but no farther than the current temperature
            for (String v : nodes) {
                double[] d = disp.get(v);
                double len = Math.max(0.01, Math.hypot(d[0], d[1]));
                double step = Math.min(len, temperature);
                double[] p = pos.get(v);
                p[0] += d[0] / len * step;
                p[1] += d[1] / len * step;
            }
            temperature = Math.max(2.0, temperature * 0.985);
        }
        return fit(pos);
    }

    /** Rescales the positions so the drawing fills the picture. */
    private static Map<String, double[]> fit(Map<String, double[]> pos) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (double[] p : pos.values()) {
            minX = Math.min(minX, p[0]);
            maxX = Math.max(maxX, p[0]);
            minY = Math.min(minY, p[1]);
            maxY = Math.max(maxY, p[1]);
        }
        // Each axis is scaled on its own so the drawing fills the picture.
        double scaleX = (WIDTH - 2 * MARGIN) / Math.max(1, maxX - minX);
        double scaleY = (HEIGHT - 2 * MARGIN) / Math.max(1, maxY - minY);
        for (double[] p : pos.values()) {
            p[0] = MARGIN + (p[0] - minX) * scaleX;
            p[1] = MARGIN + (p[1] - minY) * scaleY;
        }
        return pos;
    }

    private static double[] delta(double[] a, double[] b) {
        return new double[] {a[0] - b[0], a[1] - b[1]};
    }

    /** The SVG contents for g: edges (with weights), then nodes. Each edge
     *  line carries data-u/data-v and each node group carries data-name so
     *  the script can find them. Each node has a badge text (.k) used for
     *  step numbers, a distance label (.d) used for Dijkstra's, and a hidden
     *  "S" badge that shows when the node has the class "start". */
    static String svgBody(Lab9Graph g, Map<String, double[]> pos) {
        StringBuilder svg = new StringBuilder();
        for (String u : g.nodes()) {
            for (String v : g.neighbors(u)) {
                if (u.compareTo(v) >= 0) {
                    continue;
                }
                double[] a = pos.get(u);
                double[] b = pos.get(v);
                svg.append(String.format("<line class='edge' data-u='%s' data-v='%s' x1='%.1f' y1='%.1f' x2='%.1f' y2='%.1f'/>\n",
                        escape(u), escape(v), a[0], a[1], b[0], b[1]));
                svg.append(String.format("<text class='w' x='%.1f' y='%.1f'>%d</text>\n",
                        (a[0] + b[0]) / 2, (a[1] + b[1]) / 2 - 4, g.edgeWeight(u, v)));
            }
        }
        for (String v : g.nodes()) {
            double[] p = pos.get(v);
            svg.append(String.format("<g class='node' data-name='%s'><circle cx='%.1f' cy='%.1f' r='18'/>"
                    + "<text class='k' x='%.1f' y='%.1f'></text>"
                    + "<text class='n' x='%.1f' y='%.1f'>%s</text>"
                    + "<text class='d' x='%.1f' y='%.1f'></text>"
                    + "<circle class='sb' cx='%.1f' cy='%.1f' r='9'/><text class='st' x='%.1f' y='%.1f'>S</text></g>\n",
                    escape(v), p[0], p[1], p[0], p[1] + 5, p[0], p[1] + 32, escape(v), p[0], p[1] + 46,
                    p[0] + 15, p[1] - 15, p[0] + 15, p[1] - 11));
        }
        return svg.toString();
    }

    /** <option> elements for every node of g, with selected chosen. */
    static String nodeOptions(Lab9Graph g, String selected) {
        StringBuilder sb = new StringBuilder();
        for (String v : g.nodes()) {
            sb.append("<option").append(v.equals(selected) ? " selected" : "").append(">").append(escape(v)).append("</option>");
        }
        return sb.toString();
    }

    /** Builds the HTML page: the controls, the SVG drawing, and the script. */
    static String render(Lab9Graph g, Map<String, double[]> pos) {
        String svg = svgBody(g, pos);
        String first = g.nodes().isEmpty() ? "" : g.nodes().get(0);
        StringBuilder choices = new StringBuilder();
        for (String name : ORDERINGS.keySet()) {
            choices.append("<option>").append(name).append("</option>");
        }
        return """
                <!DOCTYPE html>
                <html><head><meta charset="utf-8"><title>Lab 9</title>
                <style>
                  body { margin: 0; background: #fafafa; font-family: sans-serif; }
                  #controls { max-width: %dpx; margin: 12px auto 0; display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
                  #controls button { font-size: 14px; padding: 6px 12px; cursor: pointer; }
                  #controls select { font-size: 14px; padding: 4px; }
                  #order { flex: 1; min-width: 320px; font-size: 14px; padding: 6px; }
                  #status { max-width: %dpx; margin: 4px auto 0; font-size: 14px; min-height: 18px; }
                  #status.err { color: #b03030; }
                  svg { display: block; margin: 0 auto; }
                  line { stroke: #888; stroke-width: 2; }
                  line.tree { stroke: #1d4f8c; stroke-width: 4; }
                  line.path { stroke: #e94e4e; stroke-width: 6; }
                  circle { fill: #4a90e2; stroke: #1d4f8c; stroke-width: 2; }
                  .node.visited circle { fill: #f5a623; stroke: #b36b00; }
                  .node.current circle { fill: #e94e4e; stroke: #8c1d1d; }
                  .node.unreachable circle { fill: #ccc; stroke: #999; }
                  .node.target circle { fill: #e94e4e; stroke: #8c1d1d; }
                  svg.spt .node { cursor: pointer; }
                  text { text-anchor: middle; }
                  .n { font-size: 15px; font-weight: bold; fill: #222; }
                  .k { font-size: 14px; font-weight: bold; fill: #fff; pointer-events: none; }
                  .d { font-size: 12px; fill: #1d4f8c; }
                  .w { font-size: 13px; fill: #b03030; paint-order: stroke; stroke: #fafafa; stroke-width: 4px; }
                  .sb, .st { display: none; }
                  .node.start .sb { display: block; fill: #f5a623; stroke: #b36b00; stroke-width: 2; }
                  .node.start .st { display: block; font-size: 12px; font-weight: bold; fill: #222; pointer-events: none; }
                </style></head>
                <body>
                <div id="controls">
                  <label>Start: <select id="start">%s</select></label>
                  <label>Order: <select id="which">%s</select></label>
                  <button id="compute">Compute</button>
                  <input id="order" type="text" placeholder="click Compute, or type an order: A, B, C">
                  <button id="viz">Visualize</button>
                  <button id="reset">Reset</button>
                </div>
                <div id="status"></div>
                <svg id="svg" width="%d" height="%d">
                %s</svg>
                <script>
                  const DIJKSTRAS = '%s';
                  const status = document.getElementById('status');
                  const box = document.getElementById('order');
                  const startSel = document.getElementById('start');
                  const whichSel = document.getElementById('which');
                  const svg = document.getElementById('svg');
                  const nodes = {};
                  document.querySelectorAll('.node').forEach(n => nodes[n.dataset.name] = n);
                  const edges = {};
                  document.querySelectorAll('line.edge').forEach(e => { edges[e.dataset.u + '|' + e.dataset.v] = e; edges[e.dataset.v + '|' + e.dataset.u] = e; });
                  let timer = null;
                  let spt = null;   // {dists, paths} after a Dijkstra's compute, else null
                  function say(msg, err) { status.textContent = msg; status.className = err ? 'err' : ''; }
                  function markStart() {
                    for (const n of Object.values(nodes)) { n.classList.remove('start'); }
                    if (nodes[startSel.value]) nodes[startSel.value].classList.add('start');
                  }
                  markStart();
                  function clearPath() {
                    document.querySelectorAll('line.path').forEach(e => e.classList.remove('path'));
                    document.querySelectorAll('.node.target').forEach(n => n.classList.remove('target'));
                  }
                  function clearSteps() {
                    if (timer) { clearInterval(timer); timer = null; }
                    for (const n of Object.values(nodes)) { n.classList.remove('visited', 'current'); n.querySelector('.k').textContent = ''; }
                  }
                  function reset() {
                    clearSteps(); clearPath();
                    spt = null; svg.classList.remove('spt');
                    document.querySelectorAll('line.tree').forEach(e => e.classList.remove('tree'));
                    for (const n of Object.values(nodes)) { n.classList.remove('unreachable'); n.querySelector('.d').textContent = ''; }
                    say('', false);
                  }
                  startSel.onchange = () => { reset(); box.value = ''; markStart(); };
                  whichSel.onchange = () => { reset(); box.value = ''; };
                  document.getElementById('reset').onclick = reset;
                  document.getElementById('compute').onclick = async () => {
                    reset();
                    const start = startSel.value, which = whichSel.value;
                    const r = await (await fetch('/order/' + encodeURIComponent(which) + '?start=' + encodeURIComponent(start))).json();
                    if (r.error) { box.value = ''; say(r.error, true); return; }
                    box.value = r.order.join(', ');
                    if (which !== DIJKSTRAS) return;
                    const t = await (await fetch('/spt/' + encodeURIComponent(start))).json();
                    if (t.error) { say(t.error, true); return; }
                    spt = t; svg.classList.add('spt');
                    for (const [name, n] of Object.entries(nodes)) {
                      if (name in t.dists) { n.querySelector('.d').textContent = t.dists[name]; } else { n.classList.add('unreachable'); }
                    }
                    for (const path of Object.values(t.paths)) {
                      for (let i = 1; i < path.length; i++) { const e = edges[path[i - 1] + '|' + path[i]]; if (e) e.classList.add('tree'); }
                    }
                    const n = Object.keys(t.dists).length, total = Object.keys(nodes).length;
                    say('Shortest paths tree from ' + start + ' in blue: ' + n + ' of ' + total + ' nodes reachable. Click a node to see the path to it.', false);
                  };
                  document.getElementById('viz').onclick = () => {
                    clearSteps(); clearPath();
                    const order = box.value.split(',').map(s => s.trim()).filter(s => s.length > 0);
                    const unknown = order.filter(s => !nodes[s]);
                    if (unknown.length > 0) { say('not in the graph: ' + unknown.join(', '), true); return; }
                    let i = 0;
                    timer = setInterval(() => {
                      if (i > 0) { nodes[order[i - 1]].classList.replace('current', 'visited'); }
                      if (i >= order.length) { clearInterval(timer); timer = null; return; }
                      const n = nodes[order[i]];
                      n.classList.add('current');
                      n.querySelector('.k').textContent = i + 1;
                      i++;
                    }, 500);
                  };
                  for (const [name, n] of Object.entries(nodes)) {
                    n.onclick = () => {
                      if (!spt) return;
                      clearPath();
                      const path = spt.paths[name];
                      if (!path) { say(name + ' is not reachable from ' + startSel.value + '.', true); return; }
                      for (let i = 1; i < path.length; i++) { const e = edges[path[i - 1] + '|' + path[i]]; if (e) e.classList.add('path'); }
                      n.classList.add('target');
                      say(path.join(' \\u2192 ') + '   (distance ' + spt.dists[name] + ')', false);
                    };
                  }
                </script>
                </body></html>
                """.formatted(WIDTH, WIDTH, nodeOptions(g, first), choices, WIDTH, HEIGHT, svg, DIJKSTRAS);
    }

    static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
