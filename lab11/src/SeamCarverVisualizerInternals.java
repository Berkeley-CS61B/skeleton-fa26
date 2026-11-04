import edu.princeton.cs.algs4.Picture;
import io.javalin.Javalin;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import javax.imageio.ImageIO;

/** Supporting code for SeamCarverVisualizer: a small web server that holds a
 *  SeamCarver and a page that talks to it. You don't need to read this file.
 *
 *  The page asks the server to do one step at a time (POST /step?dir=vertical
 *  or horizontal). The server calls your findVerticalSeam/findHorizontalSeam,
 *  then removeVerticalSeam/removeHorizontalSeam, and replies with the seam it
 *  just removed and the new picture. The page draws the seam in red over the
 *  old picture, pauses, and then shows the new picture. */
public class SeamCarverVisualizerInternals {
    /** When the page is asked to load an image bigger than this in either
     *  dimension, it offers to scale it down so its largest dimension is this
     *  ("reduced scale") before carving, since every step on a full-size
     *  photo is slow. */
    static final int REDUCED_MAX_DIMENSION = 500;
    static final int DEFAULT_PORT = 4567;

    private static final Object LOCK = new Object();
    private static Picture original;
    private static SeamCarver carver;

    /** Starts the server on the default port, preloaded with the image at
     *  startingImage (if that file exists), and opens a browser tab. */
    public static void show(String startingImage) {
        show(startingImage, DEFAULT_PORT);
    }

    public static void show(String startingImage, int port) {
        if (startingImage != null && new File(startingImage).isFile()) {
            load(new Picture(startingImage));
        }
        Javalin app = Javalin.create(config -> {
            config.http.maxRequestSize = 100L * 1024 * 1024;
            config.routes.get("/", ctx -> ctx.html(PAGE));
            config.routes.get("/state", ctx -> ctx.contentType("application/json").result(stateJson()));
            config.routes.post("/upload", ctx -> ctx.contentType("application/json")
                    .result(upload(ctx.bodyAsBytes(), "reduced".equals(ctx.queryParam("scale")))));
            config.routes.post("/reset", ctx -> ctx.contentType("application/json").result(reset()));
            config.routes.post("/step", ctx -> ctx.contentType("application/json")
                    .result(step(ctx.queryParam("dir"))));
            config.routes.get("/energy", ctx -> ctx.contentType("application/json").result(energyJson()));
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

    // ------------------------------------------------------------------
    // Request handlers. Each returns a JSON string.
    // ------------------------------------------------------------------

    private static void load(Picture p) {
        synchronized (LOCK) {
            original = p;
            carver = new SeamCarver(p);
        }
    }

    /** Loads the image in bytes. If reduced is true, scales it down so that
     *  its largest dimension is REDUCED_MAX_DIMENSION. */
    static String upload(byte[] bytes, boolean reduced) {
        BufferedImage img;
        try {
            img = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            img = null;
        }
        if (img == null) {
            return errorJson("That file doesn't look like an image Java can read (try PNG or JPEG).");
        }
        if (reduced) {
            img = shrinkToFit(img, REDUCED_MAX_DIMENSION);
        }
        Picture p = new Picture(img.getWidth(), img.getHeight());
        for (int x = 0; x < img.getWidth(); x += 1) {
            for (int y = 0; y < img.getHeight(); y += 1) {
                p.setARGB(x, y, img.getRGB(x, y) | 0xFF000000);
            }
        }
        try {
            load(p);
        } catch (RuntimeException e) {
            return errorJson("Constructing your SeamCarver threw " + describe(e));
        }
        return stateJson();
    }

    static String reset() {
        synchronized (LOCK) {
            if (original == null) {
                return errorJson("No image loaded yet.");
            }
            try {
                carver = new SeamCarver(original);
            } catch (RuntimeException e) {
                return errorJson("Constructing your SeamCarver threw " + describe(e));
            }
        }
        return stateJson();
    }

    /** Finds and removes one seam in the given direction ("vertical" or
     *  "horizontal"). Replies with the seam, so the page can draw it, and the
     *  picture after removal. */
    static String step(String dir) {
        boolean vertical = !"horizontal".equals(dir);
        synchronized (LOCK) {
            if (carver == null) {
                return errorJson("No image loaded yet.");
            }
            if (vertical && carver.width() <= 1 || !vertical && carver.height() <= 1) {
                return errorJson("The picture can't get any smaller in that direction.");
            }
            int[] seam;
            try {
                seam = vertical ? carver.findVerticalSeam() : carver.findHorizontalSeam();
            } catch (RuntimeException e) {
                return errorJson((vertical ? "findVerticalSeam" : "findHorizontalSeam")
                        + " threw " + describe(e));
            }
            if (seam == null) {
                return errorJson((vertical ? "findVerticalSeam" : "findHorizontalSeam")
                        + " returned null, which means computeM or findSeamFromM still returns null. "
                        + "Implement them, then stop and re-run.");
            }
            try {
                if (vertical) {
                    carver.removeVerticalSeam(seam);
                } else {
                    carver.removeHorizontalSeam(seam);
                }
            } catch (RuntimeException e) {
                return errorJson((vertical ? "removeVerticalSeam" : "removeHorizontalSeam")
                        + " threw " + describe(e) + ". The seam was " + seamJson(seam));
            }
            return "{\"seam\": " + seamJson(seam) + ", " + pictureFields(carver.picture()) + "}";
        }
    }

    static String stateJson() {
        synchronized (LOCK) {
            if (carver == null) {
                return "{\"empty\": true}";
            }
            return "{" + pictureFields(carver.picture()) + "}";
        }
    }

    /** The energies of the current picture as a grayscale image. */
    static String energyJson() {
        synchronized (LOCK) {
            if (carver == null) {
                return errorJson("No image loaded yet.");
            }
            try {
                double[][] e = SCUtility.toEnergyMatrix(carver);
                for (double[] column : e) {   // sqrt so that mid-energy pixels are visible too
                    for (int y = 0; y < column.length; y += 1) {
                        column[y] = Math.sqrt(column[y]);
                    }
                }
                return "{" + pictureFields(SCUtility.doubleToPicture(e)) + "}";
            } catch (RuntimeException e) {
                return errorJson("energy threw " + describe(e));
            }
        }
    }

    // ------------------------------------------------------------------
    // Helpers.
    // ------------------------------------------------------------------

    private static String pictureFields(Picture p) {
        return "\"width\": " + p.width() + ", \"height\": " + p.height()
                + ", \"image\": \"" + toDataUrl(p) + "\"";
    }

    private static String seamJson(int[] seam) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < seam.length; i += 1) {
            sb.append(i > 0 ? "," : "").append(seam[i]);
        }
        return sb.append("]").toString();
    }

    private static String errorJson(String message) {
        return "{\"error\": " + json(message) + "}";
    }

    private static String describe(Throwable e) {
        String s = e.getClass().getSimpleName();
        if (e.getMessage() != null) {
            s += ": " + e.getMessage();
        }
        StackTraceElement[] trace = e.getStackTrace();
        if (trace.length > 0) {
            s += " (at " + trace[0] + ")";
        }
        return s;
    }

    private static String json(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            if (c == '"' || c == '\\') {
                sb.append('\\').append(c);
            } else if (c < 0x20) {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.append('"').toString();
    }

    /** Encodes p as a PNG data URL. */
    private static String toDataUrl(Picture p) {
        BufferedImage img = new BufferedImage(p.width(), p.height(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < p.width(); x += 1) {
            for (int y = 0; y < p.height(); y += 1) {
                img.setRGB(x, y, p.getARGB(x, y));
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
    }

    private static BufferedImage shrinkToFit(BufferedImage img, int maxDimension) {
        int w = img.getWidth();
        int h = img.getHeight();
        if (w <= maxDimension && h <= maxDimension) {
            return img;
        }
        double scale = Math.min((double) maxDimension / w, (double) maxDimension / h);
        int nw = Math.max(1, (int) Math.round(w * scale));
        int nh = Math.max(1, (int) Math.round(h * scale));
        BufferedImage small = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = small.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, nw, nh, null);
        g.dispose();
        return small;
    }

    // ------------------------------------------------------------------
    // The page.
    // ------------------------------------------------------------------

    private static final String PAGE = """
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Seam Carver</title>
<style>
  body { font-family: system-ui, sans-serif; margin: 0; background: #1e1e24; color: #eee; }
  header { padding: 12px 20px; background: #2a2a33; display: flex; flex-wrap: wrap; gap: 14px 22px; align-items: center; }
  header h1 { font-size: 18px; margin: 0 16px 0 0; font-weight: 600; }
  label { font-size: 14px; display: inline-flex; align-items: center; gap: 6px; }
  input[type=number] { width: 70px; font-size: 14px; padding: 3px 4px; }
  button { font-size: 14px; padding: 5px 14px; border-radius: 6px; border: 1px solid #666; background: #3a3a46; color: #eee; cursor: pointer; }
  button:hover { background: #4a4a58; }
  button.go { background: #2d7a2d; border-color: #3c3; font-weight: 600; min-width: 70px; }
  button.go.stop { background: #8a2a2a; border-color: #d44; }
  button:disabled { opacity: 0.4; cursor: default; }
  #status { font-size: 14px; color: #bbb; padding: 8px 20px; min-height: 18px; }
  #status.error { color: #ff8a8a; }
  #drop { margin: 10px 20px 20px; min-height: 300px; border: 2px dashed #555; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-direction: column; gap: 10px; }
  #drop.over { border-color: #6c6; background: #26332a; }
  #drop p { color: #999; margin: 0; }
  canvas { display: block; image-rendering: auto; }
  #sizes { font-size: 14px; color: #ccc; }
  #modal { position: fixed; inset: 0; background: rgba(0,0,0,0.6); display: none; align-items: center; justify-content: center; }
  #modal.open { display: flex; }
  #modal .box { background: #2a2a33; border: 1px solid #555; border-radius: 10px; padding: 22px 26px; max-width: 460px; }
  #modal p { margin: 0 0 16px; line-height: 1.4; }
  #modal .buttons { display: flex; gap: 10px; flex-wrap: wrap; }
</style>
</head>
<body>
<header>
  <h1>Seam Carver</h1>
  <button id="uploadBtn">Upload image</button>
  <input type="file" id="file" accept="image/*" style="display:none">
  <span id="sizes">No image loaded</span>
  <label>Target width <input type="number" id="tw" min="1"></label>
  <label>Target height <input type="number" id="th" min="1"></label>
  <label>Delay (ms) <input type="number" id="delay" min="0" value="60"></label>
  <button id="go" class="go">GO</button>
  <button id="reset">Reset</button>
  <label><input type="checkbox" id="energy"> Show energies</label>
</header>
<div id="status"></div>
<div id="modal">
  <div class="box">
    <p id="modalText"></p>
    <div class="buttons">
      <button id="scaleReduced" class="go">Reduced scale (fast)</button>
      <button id="scaleFull">Full scale (slow)</button>
      <button id="scaleCancel">Cancel</button>
    </div>
  </div>
</div>
<div id="drop">
  <p id="hint">Drag a photo here, or click Upload image.</p>
  <canvas id="c" width="0" height="0"></canvas>
</div>
<script>
const $ = id => document.getElementById(id);
const canvas = $('c'), ctx = canvas.getContext('2d');
const MAX_W = Math.max(400, window.innerWidth - 60), MAX_H = Math.max(300, window.innerHeight - 160);
let cur = null;        // {img: Image, w, h}
let scale = 1;         // display scale, fixed when an image is loaded
let running = false;
let startW = 0, startH = 0;

function setStatus(msg, isError) {
  $('status').textContent = msg || '';
  $('status').className = isError ? 'error' : '';
}
function loadImage(src) {
  return new Promise((resolve, reject) => {
    const im = new Image();
    im.onload = () => resolve(im);
    im.onerror = reject;
    im.src = src;
  });
}
function draw(im) {
  const w = cur.w, h = cur.h;
  canvas.width = Math.max(1, Math.round(w * scale));
  canvas.height = Math.max(1, Math.round(h * scale));
  ctx.drawImage(im || cur.img, 0, 0, canvas.width, canvas.height);
  const pct = Math.round(scale * 100);
  $('sizes').textContent = w + ' \\u00d7 ' + h + ' pixels' + (pct < 100 ? ' (shown at ' + pct + '%%)' : '');
}
function drawSeam(seam, vertical) {
  ctx.save();
  ctx.lineWidth = Math.max(2, 1.5 * scale);
  ctx.lineJoin = 'round';
  ctx.shadowColor = 'rgba(255, 40, 40, 0.9)';
  ctx.shadowBlur = 6;
  ctx.strokeStyle = '#ff2020';
  ctx.beginPath();
  for (let i = 0; i < seam.length; i++) {
    const x = (vertical ? seam[i] : i) + 0.5, y = (vertical ? i : seam[i]) + 0.5;
    if (i === 0) ctx.moveTo(x * scale, y * scale); else ctx.lineTo(x * scale, y * scale);
  }
  ctx.stroke();
  ctx.restore();
}
async function showState(state, resetTargets) {
  if (state.error) { setStatus(state.error, true); return; }
  if (state.empty) { return; }
  const im = await loadImage(state.image);
  const fresh = !cur || resetTargets;
  cur = {img: im, w: state.width, h: state.height};
  if (fresh) {
    scale = Math.min(1, MAX_W / cur.w, MAX_H / cur.h);
    startW = cur.w; startH = cur.h;
    $('tw').value = Math.max(1, Math.round(cur.w * 0.75));
    $('th').value = Math.max(1, Math.round(cur.h * 0.75));
  }
  $('hint').style.display = 'none';
  draw();
}
async function post(url) {
  const r = await fetch(url, {method: 'POST'});
  return r.json();
}
const REDUCED_MAX = %d;
function imageSize(file) {
  return new Promise(resolve => {
    const url = URL.createObjectURL(file);
    const im = new Image();
    im.onload = () => { URL.revokeObjectURL(url); resolve({w: im.naturalWidth, h: im.naturalHeight}); };
    im.onerror = () => { URL.revokeObjectURL(url); resolve(null); };
    im.src = url;
  });
}
function askScale(size) {
  return new Promise(resolve => {
    const r = Math.max(1, Math.round(size.w * Math.min(1, REDUCED_MAX / Math.max(size.w, size.h))));
    const rh = Math.max(1, Math.round(size.h * Math.min(1, REDUCED_MAX / Math.max(size.w, size.h))));
    $('modalText').textContent = 'This image is ' + size.w + ' \u00d7 ' + size.h + ' pixels. Carving it at full size means every '
      + 'seam takes a while. Reduced scale shrinks it to ' + r + ' \u00d7 ' + rh + ' first.';
    $('modal').classList.add('open');
    const done = answer => { $('modal').classList.remove('open'); resolve(answer); };
    $('scaleReduced').onclick = () => done('reduced');
    $('scaleFull').onclick = () => done('full');
    $('scaleCancel').onclick = () => done(null);
  });
}
async function upload(file) {
  if (!file || running) return;
  const size = await imageSize(file);
  let scale = 'full';
  if (size && Math.max(size.w, size.h) > REDUCED_MAX) {
    scale = await askScale(size);
    if (!scale) return;
  }
  setStatus('Loading ' + file.name + '...');
  const r = await fetch('/upload?scale=' + scale, {method: 'POST', body: file});
  const state = await r.json();
  await showState(state, true);
  if (!state.error) {
    setStatus(scale === 'reduced' ? 'Scaled down from ' + size.w + ' \u00d7 ' + size.h + ' for speed.' : '');
  }
}
$('uploadBtn').onclick = () => $('file').click();
$('file').onchange = e => { const f = e.target.files[0]; e.target.value = ''; upload(f); };
const drop = $('drop');
['dragenter', 'dragover'].forEach(ev => drop.addEventListener(ev, e => { e.preventDefault(); drop.classList.add('over'); }));
['dragleave', 'drop'].forEach(ev => drop.addEventListener(ev, e => { e.preventDefault(); drop.classList.remove('over'); }));
drop.addEventListener('drop', e => upload(e.dataTransfer.files[0]));
document.body.addEventListener('dragover', e => e.preventDefault());
document.body.addEventListener('drop', e => e.preventDefault());

$('reset').onclick = async () => {
  running = false;
  const state = await post('/reset');
  await showState(state, false);
  if (!state.error) setStatus('');
};
$('energy').onchange = async () => {
  if (!cur) return;
  if ($('energy').checked) {
    const r = await (await fetch('/energy')).json();
    if (r.error) { setStatus(r.error, true); $('energy').checked = false; return; }
    draw(await loadImage(r.image));
  } else {
    draw();
  }
};
const sleep = ms => new Promise(r => setTimeout(r, ms));
$('go').onclick = async () => {
  if (running) { running = false; return; }
  if (!cur) { setStatus('Load an image first.', true); return; }
  const tw = parseInt($('tw').value), th = parseInt($('th').value);
  if (!(tw >= 1 && tw <= cur.w && th >= 1 && th <= cur.h)) {
    setStatus('Targets must be between 1 and the current size (' + cur.w + ' \\u00d7 ' + cur.h + ').', true);
    return;
  }
  $('energy').checked = false;
  running = true;
  $('go').textContent = 'Stop';
  $('go').classList.add('stop');
  setStatus('');
  const totalDx = cur.w - tw, totalDy = cur.h - th;
  let removed = 0;
  while (running && (cur.w > tw || cur.h > th)) {
    const dx = cur.w - tw, dy = cur.h - th;
    let vertical;
    if (dx === 0) vertical = false;
    else if (dy === 0) vertical = true;
    else vertical = dx / totalDx >= dy / totalDy;   // whichever direction is further behind
    const r = await post('/step?dir=' + (vertical ? 'vertical' : 'horizontal'));
    if (r.error) { setStatus(r.error, true); break; }
    draw();
    drawSeam(r.seam, vertical);
    const delay = Math.max(0, parseInt($('delay').value) || 0);
    if (delay > 0) await sleep(delay);
    const im = await loadImage(r.image);
    cur = {img: im, w: r.width, h: r.height};
    draw();
    removed++;
    setStatus('Removed ' + removed + ' seam' + (removed === 1 ? '' : 's') + '.');
  }
  running = false;
  $('go').textContent = 'GO';
  $('go').classList.remove('stop');
};
fetch('/state').then(r => r.json()).then(s => showState(s, true));
</script>
</body>
</html>
""".formatted(REDUCED_MAX_DIMENSION);
}
