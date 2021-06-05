package com.example.projekt;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Layout;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class GameKeyboard {
    public final static String TAG = "game keyboard";
    Context context;
    LinearLayout[] keyButtonsLayouts;
    Map<Character, Button> keyButtons;
    Consumer<Character> lambda;

    public GameKeyboard(Context context, LinearLayout[] layouts, Consumer<Character> lambda) {
        this.context = context;
        keyButtonsLayouts = layouts;
        this.lambda = lambda;
    }

    public void prepareKeyButtons() {
        keyButtons = new HashMap<>();
        Character[][] allCharacters = {{'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'},
                {'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L'},
                {'Z', 'X', 'C', 'V', 'B', 'N', 'M'}};

        for (int i = 0; i < allCharacters.length; i++) {
            LinearLayout lo = keyButtonsLayouts[i];
            Character[] characters = allCharacters[i];

            for (int j = 0; j< characters.length; j++) {
                Character c = characters[j];
                Button button = new Button(context);
                button.setText(new String(new StringBuilder().append(c)));
                button.setTextSize(24);
                button.setPadding(0, 0, 0, 0);
                button.setBackgroundColor(Color.TRANSPARENT);
                button.setTypeface(ResourcesCompat.getFont(context, R.font.fonty));
                button.setTextColor(Color.rgb(0, 0, 0));
                button.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        lambda.accept(c);
                    }
                });

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        70,
                        100);
                lp.gravity = LinearLayout.TEXT_ALIGNMENT_CENTER;

//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lo.addView(button, lp);

                keyButtons.put(Character.toLowerCase(c), button);
            }
        }
    }

    public void updateButtons(Map<Character, Boolean> charsPressed) {
        for (Map.Entry<Character, Boolean> p : charsPressed.entrySet()) {
            Button button = keyButtons.get(Character.toLowerCase(p.getKey()));
            if (button == null) return;
            button.setClickable(false);
            if (p.getValue()) {
                button.setTextColor(Color.rgb(0, 128, 0));
            } else {
                button.setTextColor(Color.rgb(128, 0, 0));
            }
        }
    }

    public void resetButtons() {
        Log.d(TAG, "Resetting buttons");
        for (Map.Entry<Character, Button> p : keyButtons.entrySet()) {
            Button button = p.getValue();
            button.setClickable(true);
            button.setTextColor(Color.rgb(0, 0, 0));
        }
    }

}
