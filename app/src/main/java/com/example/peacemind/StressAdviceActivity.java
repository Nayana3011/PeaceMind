package com.example.peacemind;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StressAdviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stress_advice);

        TextView adviceTitle = findViewById(R.id.adviceTitle);
        TextView adviceContent = findViewById(R.id.adviceContent);

        // Get emotion and status from intent
        String emotion = getIntent().getStringExtra("emotion");
        String status = getIntent().getStringExtra("status");

        if ("stress_detected".equals(status)) {
            adviceTitle.setText("😰 Stress Detected - " + emotion);
            adviceContent.setText(getAdviceForEmotion(emotion));
        } else {
            adviceTitle.setText("🙂 You Seem Calm - " + emotion);
            adviceContent.setText("Keep it up! You're doing great.\n\n" +
                    "✨ Tips to stay positive:\n" +
                    "• Keep smiling and stay social\n" +
                    "• Engage in activities you enjoy\n" +
                    "• Practice gratitude daily\n" +
                    "• Stay physically active\n" +
                    "• Keep a positive mindset");
        }
    }

    private String getAdviceForEmotion(String emotion) {
        if (emotion == null) return "Take a deep breath and relax.";

        switch (emotion.toLowerCase()) {
            case "angry":
                return "Tips for Managing Anger:\n\n" +
                        "• Take deep breaths\n" +
                        "• Walk away for a moment\n" +
                        "• Use physical activity to cool down\n" +
                        "• Speak calmly";

            case "sad":
                return "Tips for Handling Sadness:\n\n" +
                        "• Talk to someone\n" +
                        "• Practice self-care\n" +
                        "• Listen to music or journal\n" +
                        "• Engage in hobbies";

            case "fearful":
            case "scared":
                return "Tips for Overcoming Fear:\n\n" +
                        "• Identify your fear\n" +
                        "• Challenge irrational thoughts\n" +
                        "• Practice relaxation\n" +
                        "• Visualize success";

            case "disgusted":
                return "Tips for Coping with Disgust:\n\n" +
                        "• Reframe your thoughts\n" +
                        "• Focus on the positive\n" +
                        "• Talk to someone you trust";

            default:
                return "General Tips for Stress:\n\n" +
                        "• Take breaks\n" +
                        "• Stretch and breathe deeply\n" +
                        "• Get enough sleep\n" +
                        "• Connect with others";
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}