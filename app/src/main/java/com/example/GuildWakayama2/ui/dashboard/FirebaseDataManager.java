package com.example.GuildWakayama2.ui.dashboard;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.GuildWakayama2.ui.notifications.Post;
import java.util.ArrayList;
import java.util.List;

public class FirebaseDataManager {
    public interface OnDataLoadedListener {
        void onDataLoaded(List<Post> posts);
        void onDataLoadError(String errorMessage);
    }

    public static void getPosts(String userId, OnDataLoadedListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("requests").child(userId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        posts.add(post);
                    }
                }

                listener.onDataLoaded(posts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onDataLoadError(databaseError.getMessage());
            }
        });
    }
}
