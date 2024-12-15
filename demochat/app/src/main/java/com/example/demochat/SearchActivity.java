package com.example.demochat;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demochat.adapter.SearchUserRecyclerAdapter;
import com.example.demochat.model.UserModel;
import com.example.demochat.utils.AndroidUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchUserRecyclerAdapter adapter;
    private SearchView searchView;
    private ImageButton backSearch;
    private FirebaseFirestore db;
    private TextView noResultsMessage;
    private List<UserModel> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.search_recycler_view);
        searchView = findViewById(R.id.search_view);
        noResultsMessage = findViewById(R.id.no_results_message);
        backSearch = findViewById(R.id.back_search);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AndroidUtil.setItemDecorationHoriAndVerti(recyclerView, SearchActivity.this);

        backSearch.setOnClickListener(view -> {
            AndroidUtil.hideKeyboard(SearchActivity.this);
            getOnBackPressedDispatcher().onBackPressed();
        });
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        searchView.requestFocus();
        // Load users from Firestore
        loadUsersFromFirestore();

        // Configure SearchView to filter users
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setSearchQuery(newText);
                adapter.getFilter().filter(newText); // Call filter to perform filtering
                checkNoResults(); // Check if there are no results after filtering
                return false;
            }
        });
    }

    private void loadUsersFromFirestore() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        usersList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("userName");
                            String phone = document.getString("phoneNumber");
                            usersList.add(new UserModel(id, phone, name, "", null));
                        }

                        adapter = new SearchUserRecyclerAdapter(SearchActivity.this, usersList);
                        recyclerView.setAdapter(adapter);

                        // Add empty result listener
                        adapter.setOnEmptyResultListener(isEmpty -> {
                            if (isEmpty) {
                                noResultsMessage.setVisibility(View.VISIBLE);
                            } else {
                                noResultsMessage.setVisibility(View.GONE);
                            }
                        });

                        checkNoResults();
                    } else {
                        Toast.makeText(SearchActivity.this, R.string.failed_to_load_users, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void checkNoResults() {
        if (adapter.getItemCount() == 0) {
            // Nếu không có mục nào trong RecyclerView
            noResultsMessage.setVisibility(View.VISIBLE); // Hiển thị thông báo "Không có kết quả"
        } else {
            // Nếu có ít nhất một mục trong RecyclerView
            noResultsMessage.setVisibility(View.GONE); // Ẩn thông báo
        }
    }
}
