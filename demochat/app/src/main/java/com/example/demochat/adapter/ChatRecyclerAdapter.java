package com.example.demochat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demochat.R;
import com.example.demochat.model.ChatMessageModel;
import com.example.demochat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {
    private final Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        String formattedTimestamp = formatTimestamp(model.getTimestamp().toDate());  // Assume this is pre-formatted

        // Check if the message is from the current user
        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            // Sender's message (sent by current user, shown on the right)
            holder.senderMessageLayout.setVisibility(View.GONE);
            holder.receiverMessageLayout.setVisibility(View.VISIBLE);
            holder.tvReceiverMessage.setText(model.getMessage());
            holder.tvReceiverTimestamp.setText(formattedTimestamp);
            holder.profileImageSender.setVisibility(View.GONE);

        } else {
            // Receiver's message (sent by other user, shown on the left)
            holder.receiverMessageLayout.setVisibility(View.GONE);
            holder.senderMessageLayout.setVisibility(View.VISIBLE);
            holder.tvSenderMessage.setText(model.getMessage());
            holder.tvSenderTimestamp.setText(formattedTimestamp);

            holder.profileImageSender.setVisibility(View.VISIBLE);
            String senderImageUrl = model.getSenderImageUrl() != null ? model.getSenderImageUrl() : "";
            Glide.with(context)
                    .load(senderImageUrl.isEmpty() ? R.drawable.ic_person : senderImageUrl)
                    .circleCrop()
                    .into(holder.profileImageSender);
        }

    }


    // Method to format timestamp into a human-readable format
    private String formatTimestamp(Date timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(timestamp);
    }

    @Override
    public int getItemCount() {
        return super.getSnapshots().size();
    }

    // ViewHolder class
    public static class ChatModelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout senderMessageLayout, receiverMessageLayout;
        TextView tvSenderMessage, tvReceiverMessage, tvSenderTimestamp, tvReceiverTimestamp;
        CircleImageView profileImageSender;  // Profile image of sender

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageLayout = itemView.findViewById(R.id.layout_message_sender);
            receiverMessageLayout = itemView.findViewById(R.id.layout_message_receiver);
            tvSenderMessage = itemView.findViewById(R.id.tv_message_sender);
            tvReceiverMessage = itemView.findViewById(R.id.tv_message_receiver);
            tvSenderTimestamp = itemView.findViewById(R.id.tv_sender_timestamp);
            tvReceiverTimestamp = itemView.findViewById(R.id.tv_receiver_timestamp);
            profileImageSender = itemView.findViewById(R.id.profile_image_sender);  // Image of sender
        }
    }
}
