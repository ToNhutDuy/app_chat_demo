package com.example.demochat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demochat.adapter.ChatRoomAdapter;
import com.example.demochat.model.ChatRoomModel;
import com.example.demochat.utils.AndroidUtil;
import com.example.demochat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatRoomAdapter chatRoomAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = rootView.findViewById(R.id.chat_recycler_view);
        //recyclerView.setHasFixedSize(true);

        setUpRecyclerView();


        return rootView;
    }

    private void setUpRecyclerView() {
        Query query = FirebaseUtil.allChatRoomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatRoomModel> options = new FirestoreRecyclerOptions
                .Builder<ChatRoomModel>()
                .setQuery(query, ChatRoomModel.class)
                .build();

        if (getActivity() != null) {
            chatRoomAdapter = new ChatRoomAdapter(options, getActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            AndroidUtil.setItemDecorationHoriAndVerti(recyclerView, getActivity());
            recyclerView.setAdapter(chatRoomAdapter);
            chatRoomAdapter.startListening();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (chatRoomAdapter != null) {
            chatRoomAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatRoomAdapter != null) {
            chatRoomAdapter.stopListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FragmentLifecycle", "Fragment resumed");
        if (chatRoomAdapter != null) {
            chatRoomAdapter.startListening();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("FragmentLifecycle", "Fragment paused");
    }


}
