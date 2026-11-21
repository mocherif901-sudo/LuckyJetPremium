
package com.example.luckyjetpremium;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends Activity {

    private final Handler handler = new Handler(Looper.getMainLooper());

    private TextView clockView, countdownView, statusView;
    private TextView analysisView, historyList;
    private EditText multiplierInput;

    private Button startButton, stopButton;

    private long targetMillis = 0L;
    private final ArrayDeque<String> history = new ArrayDeque<>();

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();

            clockView.setText(
                "HEURE DE CÔTE D'IVOIRE • " + format(now, "HH:mm:ss")
            );

            if (targetMillis > 0L) {
                long remaining = Math.max(0L, targetMillis - now);

                long seconds = remaining / 1000L;

                countdownView.setText(String.format(
                    Locale.FRANCE,
                    "%02d:%02d",
                    seconds / 60,
                    seconds % 60
                ));

                if (remaining == 0L) {
                    targetMillis = 0L;
                    statusView.setText(
                        "Analyse terminée. Le prochain résultat réel reste inconnu."
                    );
                    countdownView.setText("00:00");
                }
            }

            handler.postDelayed(this, 1000L);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(0xFF16002E);
        getWindow().setNavigationBarColor(0xFF16002E);

        buildUi();
        handler.post(ticker);
    }

    private void buildUi() {

        ScrollView scroll = new ScrollView(this);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(20), dp(20), dp(25));
        page.setBackgroundColor(0xFF100021);

        scroll.addView(page);

        TextView brand = text(
            "LUCKY JET",
            30,
            0xFFFFD36A,
            true
        );
        brand.setGravity(Gravity.CENTER);
        page.addView(brand, matchWrap());

        TextView subtitle = text(
            "PREMIUM PREDICTOR • ANALYSE STATISTIQUE",
            12,
            0xFF00DFFF,
            true
        );
        subtitle.setGravity(Gravity.CENTER);

        page.addView(subtitle, matchWrap());

        clockView = text("", 14, 0xFFFFFFFF, true);
        clockView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams clockLp = matchWrap();
        clockLp.topMargin = dp(20);
        clockLp.bottomMargin = dp(20);

        page.addView(clockView, clockLp);

        // SAISIE DES MULTIPLICATEURS

        addTitle(page, "1. SAISIS TES MULTIPLICATEURS");

        TextView instructions = text(
            "Exemple : 1.25  2.40  5.10  1.80",
            13,
            0xFFB8C8DA,
            false
        );

        page.addView(instructions, matchWrap());

        multiplierInput = new EditText(this);
        multiplierInput.setSingleLine(false);
        multiplierInput.setMinLines(2);
        multiplierInput.setTextSize(17);
        multiplierInput.setTextColor(0xFFFFFFFF);
        multiplierInput.setHintTextColor(0xFF8795AA);
        multiplierInput.setHint("1.25  2.40  5.10");
        multiplierInput.setPadding(
            dp(14), dp(12), dp(14), dp(12)
        );
        multiplierInput.setBackground(
            roundBg(0xFF25113D, 0xFF00DFFF, 2, 12)
        );

        LinearLayout.LayoutParams inputLp = matchWrap();
        inputLp.topMargin = dp(10);
        inputLp.bottomMargin = dp(15);

        page.addView(multiplierInput, inputLp);

        // BOUTON START

        startButton = makeButton(
            "START • ANALYSER",
            0xFF00C853,
            0xFFFFFFFF
        );

        page.addView(startButton, buttonParams());

        // BOUTON STOP

        stopButton = makeButton(
            "STOP • ARRÊTER",
            0xFFD50000,
            0xFFFFFFFF
        );

        LinearLayout.LayoutParams stopLp = buttonParams();
        stopLp.topMargin = dp(10);

        page.addView(stopButton, stopLp);

        // RÉSULTAT

        addTitle(page, "2. RÉSULTAT DE L'ANALYSE");

        LinearLayout resultPanel = new LinearLayout(this);
        resultPanel.setOrientation(LinearLayout.VERTICAL);
        resultPanel.setPadding(dp(16), dp(18), dp(16), dp(18));
        resultPanel.setBackground(
            roundBg(0xFF25113D, 0xFF9B4DFF, 2, 16)
        );

        analysisView = text(
            "En attente des multiplicateurs...",
            16,
            0xFFFFFFFF,
            true
        );

        analysisView.setGravity(Gravity.CENTER);

        resultPanel.addView(analysisView, matchWrap());

        LinearLayout.LayoutParams resultLp = matchWrap();
        resultLp.topMargin = dp(10);

        page.addView(resultPanel, resultLp);

        // COMPTE À REBOURS

        addTitle(page, "3. COMPTE À REBOURS");

        countdownView = text(
            "02:00",
            45,
            0xFFFFD36A,
            true
        );

        countdownView.setGravity(Gravity.CENTER);

        page.addView(countdownView, matchWrap());

        // STATUT

        statusView = text(
            "Saisis tes multiplicateurs puis appuie sur START.",
            14,
            0xFFB8C8DA,
            false
        );

        statusView.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams statusLp = matchWrap();
        statusLp.topMargin = dp(15);

        page.addView(statusView, statusLp);

        // HISTORIQUE

        addTitle(page, "4. HISTORIQUE DES 6 ANALYSES");

        historyList = text(
            "Aucune analyse pour le moment.",
            13,
            0xFFFFFFFF,
            false
        );

        historyList.setPadding(dp(12), dp(12), dp(12), dp(12));
        historyList.setBackground(
            roundBg(0xFF25113D, 0xFF51406B, 1, 12)
        );

        page.addView(historyList, matchWrap());

        TextView disclaimer = text(
            "IMPORTANT : les statistiques décrivent uniquement les "
            + "multiplicateurs saisis. Elles ne prédisent pas le prochain "
            + "résultat et ne garantissent aucun gain.",
            11,
            0xFF9EADBF,
            false
        );

        LinearLayout.LayoutParams disclaimerLp = matchWrap();
        disclaimerLp.topMargin = dp(20);

        page.addView(disclaimer, disclaimerLp);

        startButton.setOnClickListener(v -> startAnalysis());

        stopButton.setOnClickListener(v -> stopAnalysis());

        setContentView(scroll);
    }

    private void startAnalysis() {

        String input = multiplierInput.getText().toString().trim();

        if (input.isEmpty()) {
            statusView.setText(
                "Erreur : saisis d'abord tes multiplicateurs."
            );
            return;
        }

        String[] values = input.split("[,;\\s]+");

        ArrayList<Double> numbers = new ArrayList<>();

        try {
            for (String value : values) {

                double number = Double.parseDouble(value);

                if (Double.isNaN(number)
                        || Double.isInfinite(number)
                        || number < 1.0) {

                    statusView.setText(
                        "Valeur incorrecte. Utilise des multiplicateurs "
                        + "supérieurs ou égaux à 1.00."
                    );
                    return;
                }

                numbers.add(number);
            }

        } catch (NumberFormatException e) {

            statusView.setText(
                "Erreur de saisie. Exemple : 1.25 2.40 5.10"
            );
            return;
        }

        if (numbers.isEmpty()) {
            return;
        }

        // CALCULS STATISTIQUES

        double sum = 0;
        double minimum = Collections.min(numbers);
        double maximum = Collections.max(numbers);

        for (double number : numbers) {
            sum += number;
        }

        double average = sum / numbers.size();

        ArrayList<Double> sorted = new ArrayList<>(numbers);
        Collections.sort(sorted);

        int middle = sorted.size() / 2;

        double median;

        if (sorted.size() % 2 == 0) {
            median = (
                sorted.get(middle - 1) + sorted.get(middle)
            ) / 2.0;
        } else {
            median = sorted.get(middle);
        }

        String result =
            "Nombre de valeurs : " + numbers.size()
            + "\n\nMoyenne observée : " + fmt(average) + "x"
            + "\nMédiane : " + fmt(median) + "x"
            + "\nMinimum : " + fmt(minimum) + "x"
            + "\nMaximum : " + fmt(maximum) + "x"
            + "\n\nIndicateur statistique : " + fmt(average) + "x"
            + "\n\nCe chiffre est une moyenne passée, "
            + "pas une prédiction du prochain tour.";

        analysisView.setText(result);

        statusView.setText(
            "Analyse terminée : " + numbers.size()
            + " multiplicateur(s) étudié(s)."
        );

        addHistory(
            format(System.currentTimeMillis(), "HH:mm")
            + " • " + numbers.size()
            + " valeurs • Moyenne " + fmt(average) + "x"
        );

        // COMPTE À REBOURS

        targetMillis = System.currentTimeMillis() + 120000L;

        countdownView.setText("02:00");

        statusView.setText(
            "Analyse effectuée. Compte à rebours de 2 minutes lancé."
        );
    }

    private void stopAnalysis() {

        targetMillis = 0L;

        countdownView.setText("STOP");

        statusView.setText(
            "Robot arrêté. Les résultats saisis restent disponibles."
        );
    }

    private void addHistory(String item) {

        if (history.size() >= 6) {
            history.removeFirst();
        }

        history.addLast(item);

        StringBuilder content = new StringBuilder();

        for (String entry : history) {
            content.append("•  ")
                   .append(entry)
                   .append("\n\n");
        }

        historyList.setText(content.toString());
    }

    private void addTitle(LinearLayout page, String title) {

        TextView label = text(
            title,
            14,
            0xFFFFD36A,
            true
        );

        LinearLayout.LayoutParams lp = matchWrap();
        lp.topMargin = dp(22);
        lp.bottomMargin = dp(9);

        page.addView(label, lp);
    }

    private Button makeButton(
            String label,
            int background,
            int foreground) {

        Button button = new Button(this);

        button.setText(label);
        button.setTextColor(foreground);
        button.setTextSize(15);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(false);
        button.setBackground(
            roundBg(background, background, 0, 15)
        );

        return button;
    }

    private LinearLayout.LayoutParams buttonParams() {

        LinearLayout.LayoutParams lp = matchWrap();
        lp.height = dp(55);

        return lp;
    }

    private String fmt(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private String format(long millis, String pattern) {

        SimpleDateFormat sdf = new SimpleDateFormat(
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
            boolean bold) {

        TextView tv = new TextView(this);

        tv.setText(value);
        tv.setTextSize(size);
        tv.setTextColor(color);

        if (bold) {
            tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }

        return tv;
    }

    private GradientDrawable roundBg(
            int color,
            int stroke,
            int strokeWidth,
            int radius) {

        GradientDrawable drawable = new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));

        if (strokeWidth > 0) {
            drawable.setStroke(dp(strokeWidth), stroke);
        }

        return drawable;
    }

    private LinearLayout.LayoutParams matchWrap() {

        return new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private int dp(int value) {

        return (int) (
            value * getResources().getDisplayMetrics().density + 0.5f
        );
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacks(ticker);

        super.onDestroy();
    }
}
