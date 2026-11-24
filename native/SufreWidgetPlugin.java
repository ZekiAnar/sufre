package az.sufre.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.File;
import java.io.FileOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;

// Bridge from the web app to the home-screen widget.
@CapacitorPlugin(name = "SufreWidget")
public class SufreWidgetPlugin extends Plugin {
    @PluginMethod
    public void update(PluginCall call) {
        Context c = getContext();
        SharedPreferences p = c.getSharedPreferences(SufreWidget.PREFS, Context.MODE_PRIVATE);
        String mealsIn = call.getString("meals", "[]");
        JSONArray out = new JSONArray();
        try {
            JSONArray meals = new JSONArray(mealsIn);
            for (int i = 0; i < meals.length() && i < 6; i++) {
                JSONObject o = meals.getJSONObject(i);
                String img = o.optString("img", "");
                File f = new File(c.getFilesDir(), "widget_meal_" + i + ".png");
                if (img.length() > 0) {
                    try (FileOutputStream fo = new FileOutputStream(f)) { fo.write(Base64.decode(img, Base64.DEFAULT)); }
                } else if (f.exists()) f.delete();
                o.remove("img");
                out.put(o);
            }
        } catch (Exception ex) { /* no meals */ }
        p.edit()
            .putString("date", call.getString("date", ""))
            .putString("mid", call.getString("mid", ""))
            .putString("title", call.getString("title", "Süfrə"))
            .putString("stale", call.getString("stale", "Süfrə"))
            .putString("done", call.getString("done", ""))
            .putString("plus", call.getString("plus", "+1"))
            .putString("eat", call.getString("eat", "✓"))
            .putString("kcal", call.getString("kcal", "kcal"))
            .putString("extra", call.getString("extra", ""))
            .putString("meals", out.toString())
            .putInt("mi", 0)
            .putInt("ekWidget", 0)
            .putInt("ek", call.getInt("ek", 0))
            .putInt("tk", call.getInt("tk", 0))
            .putInt("wa", call.getInt("wa", 0))
            .putInt("wb", call.getInt("wb", 8))
            .apply();
        SufreWidget.refreshAll(c);
        call.resolve();
    }

    @PluginMethod
    public void takePending(PluginCall call) {
        SharedPreferences p = getContext().getSharedPreferences(SufreWidget.PREFS, Context.MODE_PRIVATE);
        JSObject r = new JSObject();
        r.put("water", p.getInt("pendingWater", 0));
        r.put("date", p.getString("pendingDate", ""));
        r.put("eaten", p.getString("pendingEaten", "[]"));
        p.edit().putInt("pendingWater", 0).putString("pendingEaten", "[]").apply();
        call.resolve(r);
    }
}
