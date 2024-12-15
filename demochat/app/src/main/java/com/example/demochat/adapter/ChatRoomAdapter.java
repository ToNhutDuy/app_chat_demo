package com.example.demochat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demochat.ChatActivity;
import com.example.demochat.R;
import com.example.demochat.model.ChatRoomModel;
import com.example.demochat.model.UserModel;
import com.example.demochat.utils.AndroidUtil;
import com.example.demochat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, ChatRoomAdapter.ChatRoomModelViewHolder> {

    private Context context;

    public ChatRoomAdapter(FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_recycler_row, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        // Ensure position is within bounds
        if (position >= 0 && position < getItemCount()) {
            FirebaseUtil.getOtherUserFromChatRoom(model.getUserIds())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                            FirebaseUtil.getOtherProfilePicStoraRef(otherUserModel.getUserId()).getDownloadUrl()
                                    .addOnCompleteListener(t ->{
                                        if(t.isSuccessful()){
                                            Uri uri = t.getResult();
                                            AndroidUtil.setProfilePic(context, uri, holder.profilePicView);
                                        }
                                    });

                            if (otherUserModel != null) {
                                holder.userName.setText(otherUserModel.getUserName());
                                holder.lastMessageText.setText(model.getLastMessage());
                                holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AndroidUtil.hideKeyboard(context);
                                        Intent intent = new Intent(context, ChatActivity.class);
                                        AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        context.startActivity(intent);
                                    }
                                });
                            }
                        }
                    });
        } else {
            // Handle invalid position
            Log.e("MessageAdapter", "Invalid position: " + position);
        }
    }

    public class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        private TextView userName, lastMessageText, lastMessageTime;
        private CircleImageView profilePicView;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            profilePicView = itemView.findViewById(R.id.profile_pic_image);
            lastMessageText = itemView.findViewById(R.id.last_message);
            lastMessageTime = itemView.findViewById(R.id.unseen_message);
        }
    }
}
