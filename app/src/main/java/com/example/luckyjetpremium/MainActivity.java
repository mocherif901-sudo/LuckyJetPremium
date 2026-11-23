package com.example.luckyjetpremium;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView clockView, countdownView, statusView, targetView;
    private Button estimateButton;
    private String selectedGame = "LUCKY JET";                                private long targetMillis = 0L;
    private final ArrayDeque<String> history = new ArrayDeque<>();
    private LinearLayout historyList;

    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            long now = System.currentTimeMillis();
            clockView.setText("HEURE DE CÔTE D’IVOIRE  •  " + format(now, "HH:mm:ss"));
            if (targetMillis > 0L) {
                long remaining = Math.max(0L, targetMillis - now);
                long seconds = remaining / 1000L;
                countdownView.setText(String.format(Locale.FRANCE, "%02d:%02d", seconds / 60, seconds % 60));
                targetView.setText("Heure cible : " + format(targetMillis, "HH:mm:ss") + " (GMT)");
                if (remaining == 0L) {
                    targetMillis = 0L;
                    statusView.setText("Fenêtre atteinte. Le multiplicateur réel reste inconnu.");
                    addHistory(format(now, "HH:mm") + " — aucune prédiction fiable disponible");
                    estimateButton.setText("LANCER UNE NOUVELLE ESTIMATION");
                }
            }
            handler.postDelayed(this, 1000L);
        }
    };

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(0xFF071426);
        getWindow().setNavigationBarColor(0xFF071426);
        buildUi();
        handler.post(ticker);
    }

    private void buildUi() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(22), dp(22), dp(22), dp(18));
        page.setBackgroundColor(0xFF071426);

        TextView brand = text("LUCKY JET", 29, 0xFFFFD36A, true);
        brand.setGravity(Gravity.CENTER);
        page.addView(brand, matchWrap());

        TextView sub = text("PREMIUM  •  ESTIMATEUR INDICATIF", 11, 0xFFB7C4D6, true);
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = matchWrap(); subLp.bottomMargin = dp(24);
        page.addView(sub, subLp);                                                                                                                                                                               
LinearLayout gameSelector = new LinearLayout(this);
gameSelector.setOrientation(LinearLayout.HORIZONTAL);

Button luckyButton = new Button(this);
luckyButton.setText("LUCKY JET");

Button aviatorButton = new Button(this);
aviatorButton.setText("AVIATOR");

luckyButton.setOnClickListener(v -> {
    selectedGame = "LUCKY JET";
    luckyButton.setText("✓ LUCKY JET");
    aviatorButton.setText("AVIATOR");
});

aviatorButton.setOnClickListener(v -> {
    selectedGame = "AVIATOR";
    aviatorButton.setText("✓ AVIATOR");
    luckyButton.setText("LUCKY JET");
});

gameSelector.addView(luckyButton,
    new LinearLayout.LayoutParams(0, dp(55), 1));

gameSelector.addView(aviatorButton,
    new LinearLayout.LayoutParams(0, dp(55), 1));

page.addView(gameSelector, matchWrap());
        clockView = text("", 13, 0xFFE7EDF5, true);
        clockView.setGravity(Gravity.CENTER);
        page.addView(clockView, matchWrap());

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER);
        panel.setPadding(dp(18), dp(24), dp(18), dp(24));
        panel.setBackground(roundBg(0xFF10243D, 0xFFFFC85A, 2, 18));
        LinearLayout.LayoutParams panelLp = matchWrap();
        panelLp.topMargin = dp(22); panelLp.bottomMargin = dp(18);
        page.addView(panel, panelLp);

        TextView label = text("COMPTE À REBOURS", 12, 0xFFB7C4D6, true);
        panel.addView(label);
        countdownView = text("02:00", 54, 0xFFFFD36A, true);
        countdownView.setGravity(Gravity.CENTER);
        panel.addView(countdownView);
        targetView = text("Heure cible : en attente", 12, 0xFFE7EDF5, false);
        panel.addView(targetView);

        estimateButton = new Button(this);
        estimateButton.setText("ESTIMER À +2 MINUTES");
        estimateButton.setTextColor(0xFF071426);
        estimateButton.setTextSize(14);
        estimateButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        estimateButton.setAllCaps(false);
        estimateButton.setBackground(roundBg(0xFFFFD36A, 0xFFFFD36A, 0, 14));
        LinearLayout.LayoutParams btnLp = matchWrap();
        btnLp.height = dp(56); btnLp.bottomMargin = dp(16);
        page.addView(estimateButton, btnLp);

        statusView = text("Appuie pour lancer un compte à rebours de 2 minutes. Cette application ne peut pas connaître le prochain multiplicateur.", 14, 0xFFE7EDF5, false);
        statusView.setGravity(Gravity.CENTER);
        statusView.setPadding(dp(8), dp(8), dp(8), dp(18));
        page.addView(statusView, matchWrap());

        TextView histTitle = text("HISTORIQUE DES 6 DERNIÈRES SESSIONS", 12, 0xFFFFD36A, true);
        LinearLayout.LayoutParams hLp = matchWrap(); hLp.topMargin = dp(10); hLp.bottomMargin = dp(8);
        page.addView(histTitle, hLp);

        historyList = new LinearLayout(this);
        historyList.setOrientation(LinearLayout.VERTICAL);
        historyList.setPadding(dp(12), dp(10), dp(12), dp(10));
        historyList.setBackground(roundBg(0xFF0D1D31, 0xFF243A54, 1, 12));
        page.addView(historyList, matchWrap());
        addHistory("Aucune session pour le moment");

        TextView disclaimer = text("IMPORTANT : Lucky Jet est un jeu de hasard. Les estimations ne garantissent aucun résultat et ne doivent pas servir de base à une mise.", 11, 0xFF9EADBF, false);
        disclaimer.setPadding(dp(2), dp(18), dp(2), 0);
        page.addView(disclaimer, matchWrap());

        estimateButton.setOnClickListener(v -> {
            targetMillis = System.currentTimeMillis() + 120_000L;
            statusView.setText("Compte à rebours lancé. À l’échéance, l’application rappellera que le résultat réel est inconnu.");
            estimateButton.setText("COMPTE À REBOURS EN COURS");
        });

        setContentView(page);
    }

    private void addHistory(String item) {
        if (history.size() >= 6) history.removeFirst();
        history.addLast(item);
        historyList.removeAllViews();
        for (String entry : history) {
            TextView row = text("•  " + entry, 12, 0xFFE7EDF5, false);
            row.setPadding(0, dp(5), 0, dp(5));
            historyList.addView(row, matchWrap());
        }
    }

    private String format(long millis, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.FRANCE);
        sdf.setTimeZone(TimeZone.getTimeZone("Africa/Abidjan"));
        return sdf.format(new Date(millis));
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(value); tv.setTextSize(sp); tv.setTextColor(color);
        if (bold) tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return tv;
    }

    private GradientDrawable roundBg(int color, int stroke, int strokeWidth, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius));
        if (strokeWidth > 0) d.setStroke(dp(strokeWidth), stroke);
        return d;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override protected void onDestroy() {
        handler.removeCallbacks(ticker);
        super.onDestroy();
    }
}
