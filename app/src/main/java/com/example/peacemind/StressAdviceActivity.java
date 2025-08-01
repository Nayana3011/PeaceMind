package com.example.peacemind;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class StressAdviceActivity extends AppCompatActivity {

    private TextView adviceTitle, adviceContent, extraTips;
    private ImageView emojiIcon;
    private CardView cardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stress_advice);

        adviceTitle = findViewById(R.id.adviceTitle);
        adviceContent = findViewById(R.id.adviceContent);
        extraTips = findViewById(R.id.extraTips);
        emojiIcon = findViewById(R.id.emojiIcon);
        cardContainer = findViewById(R.id.cardContainer);

        // Get emotion and status from intent
        String emotion = getIntent().getStringExtra("emotion");
        String status = getIntent().getStringExtra("status");

        if ("stress_detected".equals(status)) {
            adviceTitle.setText("           Stress Detected - " + capitalize(emotion));
            emojiIcon.setImageResource(R.drawable.ic_stress); // Ensure this drawable exists
            adviceContent.setText(getAdviceForEmotion(emotion));
            extraTips.setText(getGeneralTips());
        } else {
            adviceTitle.setText("      You Seem Calm  " );
            emojiIcon.setImageResource(R.drawable.ic_calm); // Ensure this drawable exists
            adviceContent.setText("Keep it up! You're doing great.");
            extraTips.setText(getPositiveTips());
        }

        // Optional: Automatically go to MainActivity after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(StressAdviceActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }, 5000);
    }

    private String getAdviceForEmotion(String emotion) {
        if (emotion == null) return "Take a deep breath and relax.";

        switch (emotion.toLowerCase()) {
            case "angry":
                return "Tips for Managing Anger:\n\n" +
                        "• Take deep breaths\n" +
                        "• Walk away for a moment\n" +
                        "• Use physical activity to cool down\n" +
                        "• Speak calmly and clearly";

            case "sad":
                return "Tips for Handling Sadness:\n\n" +
                        "• Talk to someone you trust\n" +
                        "• Practice self-care routines\n" +
                        "• Journal your feelings\n" +
                        "• Engage in hobbies and music";

            case "fearful":
            case "scared":
                return "Tips for Overcoming Fear:\n\n" +
                        "• Acknowledge your fear\n" +
                        "• Challenge irrational thoughts\n" +
                        "• Visualize peaceful scenes\n" +
                        "• Practice slow breathing";

            case "disgusted":
                return "Tips for Coping with Disgust:\n\n" +
                        "• Reframe negative thoughts\n" +
                        "• Focus on beauty and positivity\n" +
                        "• Connect with someone you trust";

            default:
                return "General Tips for Stress:\n\n" +
                        "• Take short breaks often\n" +
                        "• Stretch and breathe deeply\n" +
                        "• Get enough rest\n" +
                        "• Stay socially connected";
        }
    }

    private String getPositiveTips() {
        return "    Tips to Stay Positive:\n" +
                "• Smile more and share kindness\n" +
                "• Practice daily gratitude\n" +
                "• Meditate or do yoga\n" +
                "• Keep a journal of happy moments";
    }

    private String getGeneralTips() {
        return "       General Self-Care Tips:\n" +
                "• Drink water and eat well\n" +
                "• Step into nature for a walk\n" +
                "• Avoid overthinking and social media\n" +
                "• Watch calming videos or listen to music";
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
