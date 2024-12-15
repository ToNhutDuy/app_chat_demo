package com.example.demochat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demochat.ChatActivity;
import com.example.demochat.R;
import com.example.demochat.SearchActivity;
import com.example.demochat.model.UserModel;
import com.example.demochat.utils.AndroidUtil;
import com.example.demochat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserRecyclerAdapter extends RecyclerView.Adapter<SearchUserRecyclerAdapter.UserModelViewHolder> implements Filterable {

    private List<UserModel> userList;
    private List<UserModel> originalUserList;
    private Context context;
    private String searchQuery = ""; // Field to hold the search query
    private OnEmptyResultListener onEmptyResultListener; // Callback for empty results

    public SearchUserRecyclerAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = new ArrayList<>(userList);
        this.originalUserList = new ArrayList<>(userList);
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        UserModel model = userList.get(position);
        String userName = model.getUserName();

        // Highlight search query
        if (!searchQuery.isEmpty() && userName.toLowerCase().contains(searchQuery.toLowerCase())) {
            int startPos = userName.toLowerCase().indexOf(searchQuery.toLowerCase());
            int endPos = startPos + searchQuery.length();
            Spannable spannable = new SpannableString(userName);
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#EF5F5F")), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.userName.setText(spannable);
        } else {
            holder.userName.setText(userName);
        }

        // Check if the user is the current user and append " (Me)"
        if (model.getUserId().equals(FirebaseUtil.currentUserId())) {
            holder.userName.setText(userName + context.getString(R.string.me));
        }


        // Load profile picture
        if (model.getProfilePic() != null && !model.getProfilePic().isEmpty()) {
            Picasso.get().load(model.getProfilePic()).into(holder.profilePicView);
        } else {
            holder.profilePicView.setImageResource(R.drawable.ic_person); // Default image
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidUtil.hideKeyboard(context);
                Intent intent = new Intent(context, ChatActivity.class);
                AndroidUtil.passUserModelAsIntent(intent, model);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Filter functionality
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();
                if (query.isEmpty()) {
                    userList = new ArrayList<>(originalUserList);
                } else {
                    List<UserModel> filteredList = new ArrayList<>();
                    for (UserModel user : originalUserList) {
                        if (user.getUserName().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(user);
                        }
                    }
                    userList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = userList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userList = (List<UserModel>) filterResults.values;

                // Callback for empty results
                if (onEmptyResultListener != null) {
                    onEmptyResultListener.onResult(userList.isEmpty());
                }

                notifyDataSetChanged();
            }
        };
    }

    // Set search query
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    // Set callback for empty results
    public void setOnEmptyResultListener(OnEmptyResultListener listener) {
        this.onEmptyResultListener = listener;
    }

    public class UserModelViewHolder extends RecyclerView.ViewHolder {
        private TextView userName, lastMessageText, lastMessageTime;
        private CircleImageView profilePicView;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            profilePicView = itemView.findViewById(R.id.profile_pic_image);

        }
    }

    // Interface for empty results callback
    public interface OnEmptyResultListener {
        void onResult(boolean isEmpty);
    }
}