
package com.example.luckyjetpremium;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends Activity {

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private SharedPreferences prefs;

    private TextView clockView;
    private TextView countdownView;
    private TextView statusView;
    private TextView targetView;
    private TextView gameLabel;
    private Button estimateButton;
    private ProgressBar loadingBar;
    private LinearLayout historyList;

    private String selectedGame = "LUCKY JET";
    private long targetMillis = 0L;

    private final ArrayDeque<String> history =
            new ArrayDeque<>();

    private final int NAVY = Color.rgb(7, 20, 38);
    private final int PANEL = Color.rgb(17, 39, 62);
    private final int GOLD = Color.rgb(255, 211, 106);
    private final int WHITE = Color.rgb(239, 245, 255);
    private final int MUTED = Color.rgb(165, 180, 198);

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();

            clockView.setText(
                    "HEURE DE CÔTE D'IVOIRE  •  "
                            + format(now, "HH:mm:ss")
            );

            if (targetMillis > 0L) {
                long remaining =
                        Math.max(0L, targetMillis - now);

                long seconds = (remaining + 999L) / 1000L;

                countdownView.setText(
                        String.format(
                                Locale.FRANCE,
                                "%02d:%02d",
                                seconds / 60,
                                seconds % 60
                        )
                );

                targetView.setText(
                        "Heure cible : "
                                + format(targetMillis, "HH:mm:ss")
                                + " (GMT)"
                );

                if (remaining == 0L) {
                    targetMillis = 0L;
                    prefs.edit().remove("target").apply();

                    statusView.setText(
                            "Session terminée. Le résultat réel reste inconnu."
                    );

                    estimateButton.setText(
                            "LANCER UNE NOUVELLE SESSION"
                    );

                    addHistory(
                            format(now, "HH:mm:ss")
                                    + " • "
                                    + selectedGame
                                    + " • Résultat inconnu"
                    );
                }
            } else {
                countdownView.setText("02:00");
                targetView.setText("Heure cible : en attente");
            }

            handler.postDelayed(this, 250L);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);

        prefs = getSharedPreferences(
                "LuckyJetPremium",
                MODE_PRIVATE
        );

        loadHistory();

        targetMillis = prefs.getLong("target", 0L);

        buildUi();

        handler.post(ticker);
    }

    private void buildUi() {

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(NAVY);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(22), dp(20), dp(22), dp(24));

        scroll.addView(page);

        TextView brand = text(
                "LUCKY JET",
                30,
                GOLD,
                true
        );
        brand.setGravity(Gravity.CENTER);
        page.addView(brand, matchWrap());

        TextView subtitle = text(
                "PREMIUM  •  ESTIMATEUR INDICATIF",
                13,
                MUTED,
                true
        );
        subtitle.setGravity(Gravity.CENTER);
        page.addView(subtitle, matchWrap());

        addSpace(page, 28);

        clockView = text("", 15, WHITE, true);
        clockView.setGravity(Gravity.CENTER);
        page.addView(clockView, matchWrap());

        addSpace(page, 25);

        LinearLayout gameSelector = new LinearLayout(this);
        gameSelector.setOrientation(LinearLayout.HORIZONTAL);

        Button lucky = makeButton("✓ LUCKY JET");
        Button aviator = makeButton("AVIATOR");

        lucky.setOnClickListener(v -> {
            selectedGame = "LUCKY JET";
            gameLabel.setText("Jeu sélectionné : LUCKY JET");
            lucky.setText("✓ LUCKY JET");
            aviator.setText("AVIATOR");
        });

        aviator.setOnClickListener(v -> {
            selectedGame = "AVIATOR";
            gameLabel.setText("Jeu sélectionné : AVIATOR");
            aviator.setText("✓ AVIATOR");
            lucky.setText("LUCKY JET");
        });

        gameSelector.addView(
                lucky,
                new LinearLayout.LayoutParams(
                        0, dp(48), 1
                )
        );

        gameSelector.addView(
                aviator,
                new LinearLayout.LayoutParams(
                        0, dp(48), 1
                )
        );

        page.addView(gameSelector);
        addSpace(page, 12);

        gameLabel = text(
                "Jeu sélectionné : LUCKY JET",
                13,
                MUTED,
                false
        );
        gameLabel.setGravity(Gravity.CENTER);
        page.addView(gameLabel, matchWrap());

        addSpace(page, 20);

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER);
        panel.setPadding(
                dp(20), dp(22), dp(20), dp(22)
        );

        panel.setBackground(roundBg(
                PANEL, GOLD, 2, 22
        ));

        page.addView(panel, matchWrap());

        TextView label = text(
                "COMPTE À REBOURS",
                14,
                MUTED,
                true
        );
        panel.addView(label, matchWrap());

        addSpace(panel, 12);

        countdownView = text(
                "02:00",
                58,
                GOLD,
                true
        );
        countdownView.setGravity(Gravity.CENTER);
        panel.addView(countdownView, matchWrap());

        addSpace(panel, 8);

        targetView = text(
                "Heure cible : en attente",
                14,
                WHITE,
                false
        );
        panel.addView(targetView, matchWrap());

        addSpace(page, 18);

        loadingBar = new ProgressBar(this);
        loadingBar.setIndeterminate(true);
        loadingBar.setVisibility(View.GONE);
        page.addView(loadingBar, matchWrap());

        estimateButton = makeButton(
                "ESTIMER À +2 MINUTES"
        );

        estimateButton.setOnClickListener(v -> {
            targetMillis =
                    System.currentTimeMillis() + 120000L;

            prefs.edit()
                    .putLong("target", targetMillis)
                    .apply();

            statusView.setText(
                    "Session lancée. Aucun multiplicateur futur n'est connu."
            );

            estimateButton.setText(
                    "COMPTE À REBOURS EN COURS"
            );

            loadingBar.setVisibility(View.VISIBLE);

            handler.postDelayed(
                    () -> loadingBar.setVisibility(View.GONE),
                    800L
            );
        });

        page.addView(estimateButton, matchWrap());

        addSpace(page, 14);

        statusView = text(
                "Appuie pour lancer une session de 2 minutes.",
                15,
                WHITE,
                false
        );
        statusView.setGravity(Gravity.CENTER);
        page.addView(statusView, matchWrap());

        addSpace(page, 28);

        TextView historyTitle = text(
                "HISTORIQUE DES 6 DERNIÈRES SESSIONS",
                14,
                GOLD,
                true
        );
        page.addView(historyTitle, matchWrap());

        addSpace(page, 10);

        historyList = new LinearLayout(this);
        historyList.setOrientation(LinearLayout.VERTICAL);
        historyList.setPadding(
                dp(12), dp(10), dp(12), dp(10)
        );
        historyList.setBackground(
                roundBg(PANEL, 0, GOLD, 16)
        );

        page.addView(historyList, matchWrap());

        refreshHistory();

        addSpace(page, 20);

        TextView disclaimer = text(
                "IMPORTANT : Lucky Jet et Aviator sont des jeux de hasard. "
                + "Cette application ne prédit pas les multiplicateurs. "
                + "Les estimations sont indicatives et ne garantissent aucun gain. "
                + "Ne mise pas de l'argent pour tester cette application.",
                12,
                MUTED,
                false
        );

        disclaimer.setPadding(
                dp(2), dp(4), dp(2), dp(4)
        );

        page.addView(disclaimer, matchWrap());

        setContentView(scroll);
    }

    private void addHistory(String item) {

        if (history.size() >= 6) {
            history.removeLast();
        }

        history.addFirst(item);

        saveHistory();
        refreshHistory();
    }

    private void refreshHistory() {

        if (historyList == null) return;

        historyList.removeAllViews();

        if (history.isEmpty()) {
            TextView empty = text(
                    "• Aucune session pour le moment",
                    14,
                    WHITE,
                    false
            );
            historyList.addView(empty, matchWrap());
            return;
        }

        for (String entry : history) {
            TextView row = text(
                    "• " + entry,
                    13,
                    WHITE,
                    false
            );

            row.setPadding(
                    0, dp(6), 0, dp(6)
            );

            historyList.addView(row, matchWrap());
        }
    }

    private void saveHistory() {

        StringBuilder data = new StringBuilder();

        for (String item : history) {
            if (data.length() > 0) {
                data.append("\n");
            }
            data.append(item);
        }

        prefs.edit()
                .putString("history", data.toString())
                .apply();
    }

    private void loadHistory() {

        String data = prefs.getString("history", "");

        if (data.isEmpty()) return;

        String[] entries = data.split("\n");

        for (String entry : entries) {
            if (!entry.isEmpty() && history.size() < 6) {
                history.addLast(entry);
            }
        }
    }

    private String format(long millis, String pattern) {

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        pattern,
                        Locale.FRANCE
                );

        sdf.setTimeZone(
                TimeZone.getTimeZone("Africa/Abidjan")
        );

        return sdf.format(new Date(millis));
    }

    private TextView text(
            String value,
            int size,
            int color,
            boolean bold
    ) {

        TextView tv = new TextView(this);

        tv.setText(value);
        tv.setTextSize(size);
        tv.setTextColor(color);

        if (bold) {
            tv.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }

        return tv;
    }

    private Button makeButton(String value) {

        Button button = new Button(this);

        button.setText(value);
        button.setTextColor(NAVY);
        button.setTextSize(14);
        button.setAllCaps(false);
        button.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        button.setBackground(
                roundBg(GOLD, 0, GOLD, 16)
        );

        return button;
    }

    private GradientDrawable roundBg(
            int color,
            int strokeColor,
            int unused,
            int radius
    ) {

        GradientDrawable d = new GradientDrawable();

        d.setColor(color);
        d.setCornerRadius(dp(radius));

        if (strokeColor != 0) {
            d.setStroke(dp(2), strokeColor);
        }

        return d;
    }

    private void addSpace(
            LinearLayout layout,
            int height
    ) {
        View space = new View(this);

        layout.addView(
                space,
                new LinearLayout.LayoutParams(
                        1, dp(height)
                )
        );
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics().density
        );
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(ticker);
        super.onDestroy();
    }
}
package com.example.luckyjetpremium;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends Activity {

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private SharedPreferences prefs;

    private TextView clockView;
    private TextView countdownView;
    private TextView statusView;
    private TextView targetView;
    private TextView gameLabel;
    private Button estimateButton;
    private ProgressBar loadingBar;
    private LinearLayout historyList;

    private String selectedGame = "LUCKY JET";
    private long targetMillis = 0L;

    private final ArrayDeque<String> history =
            new ArrayDeque<>();

    private final int NAVY = Color.rgb(7, 20, 38);
    private final int PANEL = Color.rgb(17, 39, 62);
    private final int GOLD = Color.rgb(255, 211, 106);
    private final int WHITE = Color.rgb(239, 245, 255);
    private final int MUTED = Color.rgb(165, 180, 198);

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();

            clockView.setText(
                    "HEURE DE CÔTE D'IVOIRE  •  "
                            + format(now, "HH:mm:ss")
            );

            if (targetMillis > 0L) {
                long remaining =
                        Math.max(0L, targetMillis - now);

                long seconds = (remaining + 999L) / 1000L;

                countdownView.setText(
                        String.format(
                                Locale.FRANCE,
                                "%02d:%02d",
                                seconds / 60,
                                seconds % 60
                        )
                );

                targetView.setText(
                        "Heure cible : "
                                + format(targetMillis, "HH:mm:ss")
                                + " (GMT)"
                );

                if (remaining == 0L) {
                    targetMillis = 0L;
                    prefs.edit().remove("target").apply();

                    statusView.setText(
                            "Session terminée. Le résultat réel reste inconnu."
                    );

                    estimateButton.setText(
                            "LANCER UNE NOUVELLE SESSION"
                    );

                    addHistory(
                            format(now, "HH:mm:ss")
                                    + " • "
                                    + selectedGame
                                    + " • Résultat inconnu"
                    );
                }
            } else {
                countdownView.setText("02:00");
                targetView.setText("Heure cible : en attente");
            }

            handler.postDelayed(this, 250L);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);

        prefs = getSharedPreferences(
                "LuckyJetPremium",
                MODE_PRIVATE
        );

        loadHistory();

        targetMillis = prefs.getLong("target", 0L);

        buildUi();

        handler.post(ticker);
    }

    private void buildUi() {

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(NAVY);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(22), dp(20), dp(22), dp(24));

        scroll.addView(page);

        TextView brand = text(
                "LUCKY JET",
                30,
                GOLD,
                true
        );
        brand.setGravity(Gravity.CENTER);
        page.addView(brand, matchWrap());

        TextView subtitle = text(
                "PREMIUM  •  ESTIMATEUR INDICATIF",
                13,
                MUTED,
                true
        );
        subtitle.setGravity(Gravity.CENTER);
        page.addView(subtitle, matchWrap());

        addSpace(page, 28);

        clockView = text("", 15, WHITE, true);
        clockView.setGravity(Gravity.CENTER);
        page.addView(clockView, matchWrap());

        addSpace(page, 25);

        LinearLayout gameSelector = new LinearLayout(this);
        gameSelector.setOrientation(LinearLayout.HORIZONTAL);

        Button lucky = makeButton("✓ LUCKY JET");
        Button aviator = makeButton("AVIATOR");

        lucky.setOnClickListener(v -> {
            selectedGame = "LUCKY JET";
            gameLabel.setText("Jeu sélectionné : LUCKY JET");
            lucky.setText("✓ LUCKY JET");
            aviator.setText("AVIATOR");
        });

        aviator.setOnClickListener(v -> {
            selectedGame = "AVIATOR";
            gameLabel.setText("Jeu sélectionné : AVIATOR");
            aviator.setText("✓ AVIATOR");
            lucky.setText("LUCKY JET");
        });

        gameSelector.addView(
                lucky,
                new LinearLayout.LayoutParams(
                        0, dp(48), 1
                )
        );

        gameSelector.addView(
                aviator,
                new LinearLayout.LayoutParams(
                        0, dp(48), 1
                )
        );

        page.addView(gameSelector);
        addSpace(page, 12);

        gameLabel = text(
                "Jeu sélectionné : LUCKY JET",
                13,
                MUTED,
                false
        );
        gameLabel.setGravity(Gravity.CENTER);
        page.addView(gameLabel, matchWrap());

        addSpace(page, 20);

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER);
        panel.setPadding(
                dp(20), dp(22), dp(20), dp(22)
        );

        panel.setBackground(roundBg(
                PANEL, GOLD, 2, 22
        ));

        page.addView(panel, matchWrap());

        TextView label = text(
                "COMPTE À REBOURS",
                14,
                MUTED,
                true
        );
        panel.addView(label, matchWrap());

        addSpace(panel, 12);

        countdownView = text(
                "02:00",
                58,
                GOLD,
                true
        );
        countdownView.setGravity(Gravity.CENTER);
        panel.addView(countdownView, matchWrap());

        addSpace(panel, 8);

        targetView = text(
                "Heure cible : en attente",
                14,
                WHITE,
                false
        );
        panel.addView(targetView, matchWrap());

        addSpace(page, 18);

        loadingBar = new ProgressBar(this);
        loadingBar.setIndeterminate(true);
        loadingBar.setVisibility(View.GONE);
        page.addView(loadingBar, matchWrap());

        estimateButton = makeButton(
                "ESTIMER À +2 MINUTES"
        );

        estimateButton.setOnClickListener(v -> {
            targetMillis =
                    System.currentTimeMillis() + 120000L;

            prefs.edit()
                    .putLong("target", targetMillis)
                    .apply();

            statusView.setText(
                    "Session lancée. Aucun multiplicateur futur n'est connu."
            );

            estimateButton.setText(
                    "COMPTE À REBOURS EN COURS"
            );

            loadingBar.setVisibility(View.VISIBLE);

            handler.postDelayed(
                    () -> loadingBar.setVisibility(View.GONE),
                    800L
            );
        });

        page.addView(estimateButton, matchWrap());

        addSpace(page, 14);

        statusView = text(
                "Appuie pour lancer une session de 2 minutes.",
                15,
                WHITE,
                false
        );
        statusView.setGravity(Gravity.CENTER);
        page.addView(statusView, matchWrap());

        addSpace(page, 28);

        TextView historyTitle = text(
                "HISTORIQUE DES 6 DERNIÈRES SESSIONS",
                14,
                GOLD,
                true
        );
        page.addView(historyTitle, matchWrap());

        addSpace(page, 10);

        historyList = new LinearLayout(this);
        historyList.setOrientation(LinearLayout.VERTICAL);
        historyList.setPadding(
                dp(12), dp(10), dp(12), dp(10)
        );
        historyList.setBackground(
                roundBg(PANEL, 0, GOLD, 16)
        );

        page.addView(historyList, matchWrap());

        refreshHistory();

        addSpace(page, 20);

        TextView disclaimer = text(
                "IMPORTANT : Lucky Jet et Aviator sont des jeux de hasard. "
                + "Cette application ne prédit pas les multiplicateurs. "
                + "Les estimations sont indicatives et ne garantissent aucun gain. "
                + "Ne mise pas de l'argent pour tester cette application.",
                12,
                MUTED,
                false
        );

        disclaimer.setPadding(
                dp(2), dp(4), dp(2), dp(4)
        );

        page.addView(disclaimer, matchWrap());

        setContentView(scroll);
    }

    private void addHistory(String item) {

        if (history.size() >= 6) {
            history.removeLast();
        }

        history.addFirst(item);

        saveHistory();
        refreshHistory();
    }

    private void refreshHistory() {

        if (historyList == null) return;

        historyList.removeAllViews();

        if (history.isEmpty()) {
            TextView empty = text(
                    "• Aucune session pour le moment",
                    14,
                    WHITE,
                    false
            );
            historyList.addView(empty, matchWrap());
            return;
        }

        for (String entry : history) {
            TextView row = text(
                    "• " + entry,
                    13,
                    WHITE,
                    false
            );

            row.setPadding(
                    0, dp(6), 0, dp(6)
            );

            historyList.addView(row, matchWrap());
        }
    }

    private void saveHistory() {

        StringBuilder data = new StringBuilder();

        for (String item : history) {
            if (data.length() > 0) {
                data.append("\n");
            }
            data.append(item);
        }

        prefs.edit()
                .putString("history", data.toString())
                .apply();
    }

    private void loadHistory() {

        String data = prefs.getString("history", "");

        if (data.isEmpty()) return;

        String[] entries = data.split("\n");

        for (String entry : entries) {
            if (!entry.isEmpty() && history.size() < 6) {
                history.addLast(entry);
            }
        }
    }

    private String format(long millis, String pattern) {

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        pattern,
                        Locale.FRANCE
                );

        sdf.setTimeZone(
                TimeZone.getTimeZone("Africa/Abidjan")
        );

        return sdf.format(new Date(millis));
    }

    private TextView text(
            String value,
            int size,
            int color,
            boolean bold
    ) {

        TextView tv = new TextView(this);

        tv.setText(value);
        tv.setTextSize(size);
        tv.setTextColor(color);

        if (bold) {
            tv.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }

        return tv;
    }

    private Button makeButton(String value) {

        Button button = new Button(this);

        button.setText(value);
        button.setTextColor(NAVY);
        button.setTextSize(14);
        button.setAllCaps(false);
        button.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        button.setBackground(
                roundBg(GOLD, 0, GOLD, 16)
        );

        return button;
    }

    private GradientDrawable roundBg(
            int color,
            int strokeColor,
            int unused,
            int radius
    ) {

        GradientDrawable d = new GradientDrawable();

        d.setColor(color);
        d.setCornerRadius(dp(radius));

        if (strokeColor != 0) {
            d.setStroke(dp(2), strokeColor);
        }

        return d;
    }

    private void addSpace(
            LinearLayout layout,
            int height
    ) {
        View space = new View(this);

        layout.addView(
                space,
                new LinearLayout.LayoutParams(
                        1, dp(height)
                )
        );
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics().density
        );
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(ticker);
        super.onDestroy();
    }
}
