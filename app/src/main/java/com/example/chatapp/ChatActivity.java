package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<String> chatMessages = new ArrayList<>();
    EditText edChatAct;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menu bağlama işemini yapıyoruz
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Menunün içindeki şeylere tıklayınca ne yapılacagını yazıyoruz
        if (item.getItemId() == R.id.optionsMenuProfile) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.optionsMenuSignOut) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
  /*      chatMessages.add("gazi1");
        chatMessages.add("gazi2");
        chatMessages.add("gazi3");
*/

        edChatAct = findViewById(R.id.edChatAct);
        recyclerView = findViewById(R.id.recyclerView);
        // adapterimizi oluşturuyoruz
        recyclerViewAdapter = new RecyclerViewAdapter(chatMessages);

        RecyclerView.LayoutManager recViewManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recViewManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // recyclerView ile adapterimizi birbirine bağlıyoruz
        recyclerView.setAdapter(recyclerViewAdapter);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        getData();
        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(final String userId, String registrationId) {
                // System.out.println("userID : "+userId);

                UUID uuid = UUID.randomUUID();
                final String uuidString = uuid.toString();

                DatabaseReference newReference = firebaseDatabase.getReference("PlayerID");
                newReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ArrayList<String> playerIDFromServer = new ArrayList<>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                            String currentPlayerId = hashMap.get("playerID");
                            playerIDFromServer.add(currentPlayerId);
                        }
                        if (!playerIDFromServer.contains(userId)) {
                            databaseReference.child("PlayerID").child(uuidString).child("playerID").setValue(userId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }

    public void sendMessage(View view) {
        final String messageToSend = edChatAct.getText().toString();
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();
        databaseReference.child("Chats").child(uuidString).child("userMessage").setValue(messageToSend);
        databaseReference.child("Chats").child(uuidString).child("userEmail").setValue(userEmail);
        databaseReference.child("Chats").child(uuidString).child("userMessageTime").setValue(ServerValue.TIMESTAMP);
        //databaseReference.child("test").child("test1").child("test2").child("text 3").setValue(messageToSend);
        edChatAct.setText("");
        getData();

        // oneSignal


        DatabaseReference newReference = firebaseDatabase.getReference("PlayerID");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();

                    String playerID1 = hashMap.get("playerID");
                    //  System.out.println("PlayerID server "+playerID);

                    try {
                        OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + messageToSend + "'}, 'include_player_ids': ['" + playerID1 + "']}"), null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getData() {

        DatabaseReference newDatabaseReference = firebaseDatabase.getReference("Chats");

        Query sorting = newDatabaseReference.orderByChild("userMessageTime");
        sorting.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              /*  System.out.println("datasnapshot children : "+dataSnapshot.getChildren());
                System.out.println("datasnapshot value : "+dataSnapshot.getValue());
                System.out.println("datasnapshot key : "+dataSnapshot.getKey());*/
                chatMessages.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // System.out.println("data value : "+ ds.getValue());

                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    String userEmail = hashMap.get("userEmail");
                    String userMessage = hashMap.get("userMessage");
                    chatMessages.add(userEmail + " : " + userMessage);
                    recyclerViewAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String error = databaseError.getMessage();
                Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
