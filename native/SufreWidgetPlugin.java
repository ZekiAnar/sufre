package az.sufre.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

// Bridge from the web app to the home-screen widget.
@CapacitorPlugin(name = "SufreWidget")
public class SufreWidgetPlugin extends Plugin {
    @PluginMethod
    public void update(PluginCall call) {
        Context c = getContext();
        SharedPreferences p = c.getSharedPreferences(SufreWidget.PREFS, Context.MODE_PRIVATE);
        p.edit()
            .putString("date", call.getString("date", ""))
            .putString("title", call.getString("title", "Süfrə"))
            .putString("line1", call.getString("line1", ""))
            .putString("line2", call.getString("line2", ""))
            .putString("stale", call.getString("stale", "Süfrə"))
            .putString("plus", call.getString("plus", "+1"))
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
        p.edit().putInt("pendingWater", 0).apply();
        call.resolve(r);
    }
}
