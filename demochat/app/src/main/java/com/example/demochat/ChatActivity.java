package com.example.demochat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demochat.adapter.ChatRecyclerAdapter;
import com.example.demochat.model.ChatMessageModel;
import com.example.demochat.model.ChatRoomModel;
import com.example.demochat.model.UserModel;
import com.example.demochat.utils.AndroidUtil;
import com.example.demochat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    private UserModel otherUser;
    private EditText edtMessage;
    private ImageButton btnBack, btnSendMessage;
    private TextView tvUserName;
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private RecyclerView messageRecyclerView;
    private ChatRoomModel chatRoomModel;
    private String chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        // Initialize data from Intent
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        init();
        setupListeners();

        tvUserName.setText(otherUser.getUserName());
        getOrCreateChatRoomModel();
        setUpChatRecyclerView();

        edtMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                AndroidUtil.hideKeyboard(ChatActivity.this);  // Ẩn bàn phím khi EditText mất focus
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            AndroidUtil.hideKeyboard(ChatActivity.this);
            getOnBackPressedDispatcher().onBackPressed();
        });

        btnSendMessage.setOnClickListener(v -> {
            String message = edtMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessageToUser(message);
            } else {
                AndroidUtil.showToats(ChatActivity.this, "Tin nhắn không được để trống!");
            }
        });
    }

    private void setUpChatRecyclerView() {
        Query query = FirebaseUtil.getChatRoomMessageCollectionReference(chatRoomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options =
                new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                        .setQuery(query, ChatMessageModel.class)
                        .build();

        chatRecyclerAdapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this);
        manager.setReverseLayout(true);
        messageRecyclerView.setLayoutManager(manager);
        messageRecyclerView.setAdapter(chatRecyclerAdapter);
        chatRecyclerAdapter.startListening();
        chatRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                messageRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessageToUser(String message) {
        edtMessage.setText("");
        ChatMessageModel chatMessageModel = new ChatMessageModel(
                message,
                FirebaseUtil.currentUserId(),
                "", // Image URL (if any)
                Timestamp.now()
        );

        FirebaseUtil.getChatRoomMessageCollectionReference(chatRoomId)
                .add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
                        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
                        chatRoomModel.setLastMessage(message);
                        FirebaseUtil.getChatRoomDocumentReference(chatRoomId).set(chatRoomModel);
                    } else {
                        AndroidUtil.showToats(ChatActivity.this, "Gửi tin nhắn thất bại. Vui lòng thử lại.");
                    }
                });
    }

    private void getOrCreateChatRoomModel() {
        FirebaseUtil.getChatRoomDocumentReference(chatRoomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if (chatRoomModel == null) {
                    chatRoomModel = new ChatRoomModel("",
                            Timestamp.now(),
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            chatRoomId
                    );
                    FirebaseUtil.getChatRoomDocumentReference(chatRoomId).set(chatRoomModel);
                }
            } else {
                AndroidUtil.showToats(this, "Không thể tải phòng chat. Vui lòng kiểm tra kết nối mạng.");
            }
        });
    }

    private void init() {
        edtMessage = findViewById(R.id.edt_message);
        btnBack = findViewById(R.id.btn_back);
        btnSendMessage = findViewById(R.id.btn_send_message);
        tvUserName = findViewById(R.id.tv_user_name);
        messageRecyclerView = findViewById(R.id.message_recycler_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (chatRecyclerAdapter != null) {
            chatRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (chatRecyclerAdapter != null) {
            chatRecyclerAdapter.stopListening();
        }
    }
}