package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SwapActivity extends AppCompatActivity{
    FloatingActionButton fab;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    List<DataClass> filteredList; // Добавлен список для отфильтрованных данных
    MyAdapter adapter;
    SearchView searchView;

    private void onImageView22Clicked() {
        Intent intent = new Intent(this, AccActivity.class);
        startActivity(intent);
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap);
        View mImageView = findViewById(R.id.image);


        View mPopupView = LayoutInflater.from(this).inflate(R.layout.popup_window, null);

        PopupWindow mPopupWindow = new PopupWindow(mPopupView, ViewGroup.LayoutParams.MATCH_PARENT,
                200, true);

        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setOutsideTouchable(true);
        ImageView categorySearch = findViewById(R.id.categorySearch);
        categorySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryDialog();
            }
        });

        ImageView imageView22 = mPopupView.findViewById(R.id.imageView22);
        imageView22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageView22Clicked();
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
        // В методе onCreate()





        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(SwapActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(SwapActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        dataList = new ArrayList<>();
        filteredList = new ArrayList<>(); // Инициализация списка отфильтрованных данных

        adapter = new MyAdapter(SwapActivity.this, dataList);
        adapter.setOnItemClickListener( this);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials");
        dialog.show();
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    assert dataClass != null;
                    dataClass.setKey(itemSnapshot.getKey());
                    dataClass.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    dataList.add(dataClass);
                }
                filterData(); // Фильтрация данных при каждом обновлении
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(); // Фильтрация данных при изменении текста в SearchView
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SwapActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        ImageView imageView = findViewById(R.id.image);
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "translationY", 0f, -20f, 0f);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    private void showCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SwapActivity.this);
        builder.setTitle("Выберите категорию");
        String[] categories = getResources().getStringArray(R.array.categories);

        builder.setItems(categories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                String selectedCategory = categories[position];
                searchView.setQuery(selectedCategory, true);
            }
        });

        builder.show();
    }

    private void filterData() {
        String query = searchView.getQuery().toString().toLowerCase();

        if (TextUtils.isEmpty(query)) {
            filteredList.clear();
            filteredList.addAll(dataList);
        } else {
            filteredList.clear();
            for (DataClass data : dataList) {
                if (data.getDataTitle().toLowerCase().contains(query) || data.getCategory().toLowerCase().contains(query)) {
                    filteredList.add(data);
                }
            }
        }

        adapter.searchDataList((ArrayList<DataClass>) filteredList);
    }


}
