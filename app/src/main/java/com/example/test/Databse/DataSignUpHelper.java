package com.example.test.Databse;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.test.Interface.CompletionListener;
import com.example.test.Interface.getDataType;
import com.example.test.Interface.getImageDataType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataSignUpHelper implements CompletionListener, getDataType, getImageDataType {


    public DataSignUpHelper() {}

//done
    public boolean addData(String email, String password, String fullName, String mobile,ImageView profileImage, String userType){

        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        String base64String;
        if (imageViewToByte(profileImage)!=null) base64String = Base64.encodeToString(imageViewToByte(profileImage), Base64.DEFAULT);
        else {base64String="";}

        Map<String, Object> user = new HashMap<>();
        user.put("email",email);
        user.put("password", password);
        user.put("fullName", fullName);
        user.put("mobile", mobile);
        user.put("profileImage", base64String);
        user.put("userType", userType);
        FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userData").setValue(user);

        return true;

    }

    public void getUserTypeData(String email,getDataType dataType){
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userData").child("userType");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataType.onGetData(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dataType.onGetData(null);
            }
        });

    }
//done
    private byte[] imageViewToByte(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            return null;
        }
        ByteArrayOutputStream stream= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        return stream.toByteArray();
    }



//done
    public void getProfileData(String email, CompletionListener listener) {
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onComplete(!dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onComplete(true);
            }
        });
    }


    public void onComplete(boolean result){}
    public void onGetData(String data) {}


    //done


    public void onGetImageData(byte[] imageData) {}

    //done
    public void getImageData(String email,getImageDataType imageDataType) {

        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userData").child("profileImage");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Objects.equals(snapshot.getValue(String.class), "") || snapshot.getValue(String.class) == null){
                    imageDataType.onGetImageData(null);}
                else{
                    imageDataType.onGetImageData(Base64.decode(snapshot.getValue(String.class), Base64.DEFAULT));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    //done
    public void getNameData(String email,getDataType dataType){
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userData").child("fullName");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataType.onGetData(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dataType.onGetData(null);
            }
        });


    }

    //done
    public void getPhoneData(String email,getDataType dataType){
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userData").child("mobile");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataType.onGetData(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dataType.onGetData(null);
            }
        });

    }
//done
    public void getPassData(String email,getDataType dataType){
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userData").child("password");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataType.onGetData(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dataType.onGetData(null);
            }
        });
    }



//done
    public void updatePassword(String email,  String newPassword) {

        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userData");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().child("password").setValue(newPassword);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //done
    public void updatePhone(String email, String newPhone) {

        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userData");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().child("mobile").setValue(newPhone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    //done
    public void updateName(String email, String newName) {
        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userData");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().child("fullName").setValue(newName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



//done
    public void checkUsernamePassword(String email, String password,  CompletionListener listener){

        String encodedEmail = email.replace(".", ",").replace("+", "%2B");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(encodedEmail).child("userData");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.onComplete(Objects.equals(snapshot.child("email").getValue(String.class), email) && Objects.equals(snapshot.child("password").getValue(String.class), password));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });




    }



}
