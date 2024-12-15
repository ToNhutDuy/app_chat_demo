package com.example.demochat.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {

    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // Returns the current logged-in user's ID, or null if not logged in
    public static String currentUserId() {
        return (auth.getCurrentUser() != null) ? auth.getUid() : null;
    }

    // Returns true if a user is logged in, false otherwise
    public static boolean isLoggedId() {
        return currentUserId() != null;
    }

    // Returns the Firestore document reference of the current user's details
    public static DocumentReference currentUserDetails() {
        String userId = currentUserId();
        return (userId != null) ? firestore.collection("users").document(userId) : null;
    }

    // Returns the Firestore collection reference for all users
    public static CollectionReference allUserCollectionReference() {
        return firestore.collection("users");
    }
    public static DocumentReference getChatRoomDocumentReference(String chatRoomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId);
    }
    public static CollectionReference getChatRoomMessageCollectionReference(String chatRoomId){
        return getChatRoomDocumentReference(chatRoomId).collection("chats");
    }
    public static String getChatRoomId(String userId1, String userId2){
        if(userId1.hashCode() < userId2.hashCode()){
            return userId1 + "_" + userId2;
        }else{
            return userId2 + "_" + userId1;
        }
    }
    public static DocumentReference getOtherUserFromChatRoom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }
    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    public static CollectionReference allChatRoomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }
    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }
    public static StorageReference getCurrentProfilePicStoraRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }
    public static StorageReference getOtherProfilePicStoraRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
}
