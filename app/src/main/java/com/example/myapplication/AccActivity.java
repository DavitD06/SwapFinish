package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AccActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ImageView mImageView;
    private PopupWindow mPopupWindow;
    private int clickCount = 0;
    private static final int MAX_CLICK_COUNT = 3;
    private String username;
    private String phone;

    private void onImageView20Clicked() {
        Intent intent = new Intent(this, SwapActivity.class);
        startActivity(intent);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc);
        mImageView = findViewById(R.id.imageView);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        phone = sharedPreferences.getString("phone", "");

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        TextView phoneTextView = findViewById(R.id.phoneTextView);

        usernameTextView.setText(username);
        phoneTextView.setText(phone);

        View mPopupView = LayoutInflater.from(this).inflate(R.layout.popup_window, null);

        mPopupWindow = new PopupWindow(mPopupView, ViewGroup.LayoutParams.MATCH_PARENT,
                200, true);

        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setOutsideTouchable(true);
        ImageView imageView18 = mPopupView.findViewById(R.id.imageView18);
        imageView18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageView20Clicked();
            }
        });

        Button Button = findViewById(R.id.button);
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Удаление сохраненного пароля
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("password");
                editor.apply();
                // Выход из аккаунта
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(AccActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.showAtLocation(mImageView, Gravity.BOTTOM, 0, 0);
                ObjectAnimator animator = ObjectAnimator.ofFloat(mImageView, "translationY", 0f, 100f);
                animator.setDuration(1000);
                animator.start();
            }
        });

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(mImageView, "translationY", mImageView.getTranslationY(), 0f);
                animator.setDuration(1000);
                animator.start();
            }
        });

        mPopupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPopupWindow.dismiss();
                return true;
            }
        });

        ImageView imageView = findViewById(R.id.image);
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "translationY", 0f, -20f, 0f);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }
}
