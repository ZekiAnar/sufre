package az.sufre.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.RemoteViews;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

// Home-screen widget: the next meal with its picture and an "Eaten" button, calories, and water with "+1 glass".
// Taps made while the app is closed are kept here and picked up by the app (SufreWidgetPlugin.takePending).
public class SufreWidget extends AppWidgetProvider {
    public static final String PREFS = "sufre_widget";
    public static final String ACTION_WATER = "az.sufre.app.WIDGET_WATER";
    public static final String ACTION_EAT = "az.sufre.app.WIDGET_EAT";

    static String today() { return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()); }

    @Override
    public void onUpdate(Context c, AppWidgetManager m, int[] ids) {
        for (int id : ids) draw(c, m, id);
    }

    @Override
    public void onReceive(Context c, Intent i) {
        super.onReceive(c, i);
        SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String t = today();
        if (ACTION_WATER.equals(i.getAction())) {
            int n = t.equals(p.getString("pendingDate", "")) ? p.getInt("pendingWater", 0) : 0;
            p.edit().putInt("pendingWater", n + 1).putString("pendingDate", t).apply();
            refreshAll(c);
        } else if (ACTION_EAT.equals(i.getAction())) {
            if (!t.equals(p.getString("date", ""))) return;
            try {
                JSONArray meals = new JSONArray(p.getString("meals", "[]"));
                int mi = p.getInt("mi", 0);
                if (mi >= meals.length()) return;
                JSONObject cur = meals.getJSONObject(mi);
                JSONArray pend = new JSONArray(p.getString("pendingEaten", "[]"));
                JSONObject e = new JSONObject();
                e.put("d", t); e.put("x", cur.optString("x")); e.put("mid", p.getString("mid", ""));
                pend.put(e);
                p.edit().putString("pendingEaten", pend.toString()).putInt("mi", mi + 1)
                    .putInt("ekWidget", p.getInt("ekWidget", 0) + cur.optInt("k", 0)).apply();
            } catch (Exception ex) { /* keep the widget as it was */ }
            refreshAll(c);
        }
    }

    static void draw(Context c, AppWidgetManager m, int id) {
        SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        RemoteViews v = new RemoteViews(c.getPackageName(), R.layout.sufre_widget);
        String t = today();
        boolean fresh = t.equals(p.getString("date", ""));
        v.setTextViewText(R.id.w_title, p.getString("title", "Süfrə"));
        String line1 = p.getString("stale", "Süfrə"), name = "", line2 = "";
        boolean hasMeal = false;
        Bitmap bmp = null;
        if (fresh) {
            try {
                JSONArray meals = new JSONArray(p.getString("meals", "[]"));
                int mi = p.getInt("mi", 0);
                if (mi < meals.length()) {
                    JSONObject cur = meals.getJSONObject(mi);
                    line1 = cur.optString("line"); name = cur.optString("name"); hasMeal = true;
                    File f = new File(c.getFilesDir(), "widget_meal_" + mi + ".png");
                    if (f.exists()) bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                } else {
                    line1 = p.getString("done", "");
                }
            } catch (Exception ex) { line1 = p.getString("done", ""); }
            int ek = p.getInt("ek", 0) + p.getInt("ekWidget", 0);
            line2 = ek + " / " + p.getInt("tk", 0) + " " + p.getString("kcal", "kcal") + p.getString("extra", "");
        }
        v.setTextViewText(R.id.w_line1, line1);
        v.setTextViewText(R.id.w_name, name);
        v.setViewVisibility(R.id.w_name, hasMeal ? View.VISIBLE : View.GONE);
        v.setTextViewText(R.id.w_line2, line2);
        if (bmp != null) { v.setImageViewBitmap(R.id.w_img, bmp); v.setViewVisibility(R.id.w_img, View.VISIBLE); }
        else v.setViewVisibility(R.id.w_img, View.GONE);
        int wa = fresh ? p.getInt("wa", 0) : 0;
        int wb = p.getInt("wb", 8);
        int pend = t.equals(p.getString("pendingDate", "")) ? p.getInt("pendingWater", 0) : 0;
        v.setTextViewText(R.id.w_water, "💧 " + (wa + pend) + " / " + wb);
        v.setTextViewText(R.id.w_plus, p.getString("plus", "+1"));
        v.setTextViewText(R.id.w_eat, p.getString("eat", "✓"));
        v.setViewVisibility(R.id.w_eat, hasMeal ? View.VISIBLE : View.GONE);
        Intent open = c.getPackageManager().getLaunchIntentForPackage(c.getPackageName());
        if (open != null) {
            PendingIntent po = PendingIntent.getActivity(c, 0, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            v.setOnClickPendingIntent(R.id.w_root, po);
        }
        Intent w = new Intent(c, SufreWidget.class).setAction(ACTION_WATER);
        v.setOnClickPendingIntent(R.id.w_plus, PendingIntent.getBroadcast(c, 1, w, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        Intent e = new Intent(c, SufreWidget.class).setAction(ACTION_EAT);
        v.setOnClickPendingIntent(R.id.w_eat, PendingIntent.getBroadcast(c, 2, e, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        m.updateAppWidget(id, v);
    }

    static void refreshAll(Context c) {
        AppWidgetManager m = AppWidgetManager.getInstance(c);
        int[] ids = m.getAppWidgetIds(new ComponentName(c, SufreWidget.class));
        for (int id : ids) draw(c, m, id);
    }
}
