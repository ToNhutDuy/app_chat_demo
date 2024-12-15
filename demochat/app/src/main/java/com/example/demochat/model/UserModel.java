package com.example.demochat.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phoneNumber, userName, profilePic, userId;
    private Timestamp createdTimestamp;
    private String fcmToken;
    public UserModel(){}

    public UserModel(String userId, String phoneNumber, String userName, String profilePic, Timestamp createdTimestamp) {
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        this.profilePic = profilePic;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    // Phương thức chuyển đổi Timestamp thành dạng ngày tháng dễ sử dụng
    public String getFormattedDate() {
        if (createdTimestamp != null) {
            return createdTimestamp.toDate().toString(); // Chuyển đổi Timestamp thành Date và lấy chuỗi
        }
        return "N/A"; // Trả về "N/A" nếu createdTimestamp là null
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }


    @Override
    public String toString() {
        return "UserModel{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", userName='" + userName + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                '}';
    }
}
