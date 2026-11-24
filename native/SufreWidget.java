package az.sufre.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Home-screen widget: next meal, calories and water with a "+1 glass" button.
public class SufreWidget extends AppWidgetProvider {
    public static final String PREFS = "sufre_widget";
    public static final String ACTION_WATER = "az.sufre.app.WIDGET_WATER";

    static String today() { return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()); }

    @Override
    public void onUpdate(Context c, AppWidgetManager m, int[] ids) {
        for (int id : ids) draw(c, m, id);
    }

    @Override
    public void onReceive(Context c, Intent i) {
        super.onReceive(c, i);
        if (ACTION_WATER.equals(i.getAction())) {
            SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            String t = today();
            int n = t.equals(p.getString("pendingDate", "")) ? p.getInt("pendingWater", 0) : 0;
            p.edit().putInt("pendingWater", n + 1).putString("pendingDate", t).apply();
            refreshAll(c);
        }
    }

    static void draw(Context c, AppWidgetManager m, int id) {
        SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        RemoteViews v = new RemoteViews(c.getPackageName(), R.layout.sufre_widget);
        String t = today();
        boolean fresh = t.equals(p.getString("date", ""));
        v.setTextViewText(R.id.w_title, p.getString("title", "Süfrə"));
        v.setTextViewText(R.id.w_line1, fresh ? p.getString("line1", "") : p.getString("stale", "Süfrə"));
        v.setTextViewText(R.id.w_line2, fresh ? p.getString("line2", "") : "");
        int wa = fresh ? p.getInt("wa", 0) : 0;
        int wb = p.getInt("wb", 8);
        int pend = t.equals(p.getString("pendingDate", "")) ? p.getInt("pendingWater", 0) : 0;
        v.setTextViewText(R.id.w_water, "💧 " + (wa + pend) + " / " + wb);
        v.setTextViewText(R.id.w_plus, p.getString("plus", "+1"));
        Intent open = c.getPackageManager().getLaunchIntentForPackage(c.getPackageName());
        if (open != null) {
            PendingIntent po = PendingIntent.getActivity(c, 0, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            v.setOnClickPendingIntent(R.id.w_root, po);
        }
        Intent w = new Intent(c, SufreWidget.class).setAction(ACTION_WATER);
        PendingIntent pw = PendingIntent.getBroadcast(c, 1, w, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        v.setOnClickPendingIntent(R.id.w_plus, pw);
        m.updateAppWidget(id, v);
    }

    static void refreshAll(Context c) {
        AppWidgetManager m = AppWidgetManager.getInstance(c);
        int[] ids = m.getAppWidgetIds(new ComponentName(c, SufreWidget.class));
        for (int id : ids) draw(c, m, id);
    }
}
