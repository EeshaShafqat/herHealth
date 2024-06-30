package com.example.herhealth;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    static Context context;
    static List<CombinedForumPost> ls;

    public PostAdapter(Context context, List<CombinedForumPost> ls) {
        this.context = context;
        this.ls = ls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(context).inflate(R.layout.yourpostlayout, parent, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {

        Log.d("PostAdapter", "onBindViewHolder called for position: " + position);

        String ForumImage = ls.get(position).getImage();
        String ForumName = ls.get(position).getName();

        // Check if posts is not null and has at least one post
        if (ls.get(position).getPost() != null) {
            Log.d("PostAdapter", "in first if: " + position);


            Posts firstPost = ls.get(position).getPost();


            if (firstPost != null) {

                Log.d("PostAdapter", "inside first post");

                String PostImage = firstPost.getImage();
                String author = firstPost.getAuthor();
                Log.d("HomePageAdapter", "author:" + author);
                String title = firstPost.getTitle();
                String timeAgo = "";


                Long time = firstPost.getTime();

                if (time != null) {
                    long currentTimeMillis = System.currentTimeMillis();
                    long timeDifferenceMillis = currentTimeMillis - time;
                    timeAgo = TimeConverter.convertMillisToTimeString(timeDifferenceMillis);
                    // Use timeAgo as needed
                } else {
                    // Handle the case where time is null
                }

                String PostVideo = firstPost.getVideo();

                String content = firstPost.getContent();
                Long likes = firstPost.getLikes();
                Long dislikes = firstPost.getDislikes();

                holder.setData(ForumImage, ForumName, author, PostImage,PostVideo, title, timeAgo, content, likes, dislikes);

            }

        } else {

            Log.d("PostAdapter", "no post in forum");
            // Handle the case where the list of posts is null or empty
            holder.setData(ForumImage, ForumName, null, null, null,null, null, null, null, null);
        }

        CombinedForumPost combinedForumPost = ls.get(position);

        Log.d("HomePageAdapter", "combinedForumPost: " + combinedForumPost);


        if (combinedForumPost != null) {
            // Set other views
            holder.upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onUpvoteClick(combinedForumPost, holder.upvote, holder.downvote);
                }
            });
        }

        if (combinedForumPost != null) {
            holder.downvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDownvoteClick(combinedForumPost, holder.downvote, holder.upvote);
                }
            });
        }


        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (combinedForumPost != null) {
                    // Fetch comments for the post
                    // Launch the CommentsBottomSheetFragment passing the postId
                    String postId = combinedForumPost.getPost().getPostId();

                    CommentsBottomSheetFragment bottomSheetFragment = CommentsBottomSheetFragment.newInstance(postId);
                    bottomSheetFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), bottomSheetFragment.getTag());
                }
            }
        });


        // Set onClickListener for the video view
        holder.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the playback state
                if (holder.video.isPlaying()) {
                    // If video is playing, pause it
                    holder.video.pause();
                } else {
                    // If video is not playing, resume it
                    holder.video.play();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return ls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image, postImage, upvote, downvote, comment;

        TextureVideoView video;
        TextView category, title, time, content, likes, dislikes;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            postImage = itemView.findViewById(R.id.postImage);
            category = itemView.findViewById(R.id.category);
            title = itemView.findViewById(R.id.PostTitle);
            time = itemView.findViewById(R.id.time);
            content = itemView.findViewById(R.id.postContent);
            likes = itemView.findViewById(R.id.upCount);
            dislikes = itemView.findViewById(R.id.downCount);

            upvote = itemView.findViewById(R.id.upvote);
            downvote = itemView.findViewById(R.id.downvote);

            comment = itemView.findViewById(R.id.comment);

            video = itemView.findViewById(R.id.cropTextureView);

        }


        public void setData(String ForumImage, String ForumName, String author, String PostImage, String PostVideo, String title, String time, String content, Long likes, Long dislikes) {

            if (ForumName != null) {
                this.category.setText(ForumName);
            } else {
                Log.e("HomePageAdapter", "ForumName is null");
            }

            if (ForumImage != null) {

                Picasso.get()
                        .load(ForumImage)
                        .placeholder(R.drawable.womens_day1) // Replace R.drawable.placeholder_image with your placeholder image resource
                        .into(this.image);

            }


            //for post image
            if (PostImage != null) {
                // If there is a postImage URL, set visibility to VISIBLE
                this.postImage.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load(PostImage)
                        .placeholder(R.drawable.womens_day1)
                        .into(this.postImage);
            } else {
                // If there is no postImage URL, set visibility to GONE
                this.postImage.setVisibility(View.GONE);

                //check if there is a video instead

                if (PostVideo != null) {
                    // If there is a postImage URL, set visibility to VISIBLE
                    this.video.setVisibility(View.VISIBLE);

                    this.video.setDataSource(PostVideo);
                    this.video.play();

                } else{

                    this.video.setVisibility(View.GONE);


                }




            }

            if (title != null) {
                this.title.setText(title);
            }


            if (time != null) {

                this.time.setText(time);
            }

            if (content != null) {

                this.content.setText(content);
            }


            // Check if likes and dislikes are not null before setting them
            if (likes != null) {
                this.likes.setText(String.valueOf(likes));


                if (likes > 0) {

                    Picasso.get().load(R.drawable.upvote_clicked).into(this.upvote);

                } else if (likes == 0) {

                    Picasso.get().load(R.drawable.growth_1_1).into(this.upvote);

                }

            } else {
                this.likes.setText("0"); // Set default value if likes is null
            }

            if (dislikes != null) {

                this.dislikes.setText(String.valueOf(dislikes));


                if (dislikes > 0) {

                    Picasso.get().load(R.drawable.downvote_clicked).into(this.downvote);

                } else if (dislikes == 0) {

                    Picasso.get().load(R.drawable.growth_1_2).into(this.downvote);

                }

            } else {
                this.dislikes.setText("0"); // Set default value if dislikes is null
            }


        }
    }


    public void onUpvoteClick(CombinedForumPost combinedForumPost, ImageView upvote, ImageView downvote) {
        if (combinedForumPost != null) {
            Posts post = combinedForumPost.getPost();
            if (post != null) {
                String postId = post.getPostId();
                if (postId != null && !postId.isEmpty()) {
                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference userLikesRef = FirebaseDatabase.getInstance().getReference("user_likes").child(currentUserId);

                    userLikesRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                removeDislike(postId, combinedForumPost, downvote); // Remove dislike if exists
                                incrementLikeCount(postId, combinedForumPost); // Increment like count
                            } else {
                                // User has already liked this post, allow them to unlike it
                                removeLike(postId, combinedForumPost, upvote);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                        }
                    });
                } else {
                    Log.e("HomeFragment", "postId is null or empty");
                }
            } else {
                Log.e("HomeFragment", "Post is null");
            }
        } else {
            Log.e("HomeFragment", "CombinedForumPost is null");
        }
    }

    private void incrementLikeCount(String postId, CombinedForumPost clickedPost) {
        if (postId != null && clickedPost != null && clickedPost.getName() != null) {
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("forums")
                    .child(clickedPost.getForumId())
                    .child("posts")
                    .child(postId)
                    .child("likes");
            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Long likesCount = dataSnapshot.getValue(Long.class);
                        if (likesCount == null) {
                            likesCount = 0L;
                        }
                        // Increment the dislike count
                        likesCount++;

                        clickedPost.getPost().setLikes(clickedPost.getPost().getLikes() + 1);

                        notifyDataSetChanged();
                        // Update the dislike count in the Firebase database
                        postRef.setValue(likesCount);
                        DatabaseReference userlikesRef = FirebaseDatabase.getInstance().getReference("user_likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userlikesRef.child(postId).setValue(true);
                    } else {
                        // If the dislikes field doesn't exist, initialize it to 1
                        clickedPost.getPost().setLikes(1L);
                        notifyDataSetChanged();
                        postRef.setValue(1L);
                        DatabaseReference userlikesRef = FirebaseDatabase.getInstance().getReference("user_likes").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userlikesRef.child(postId).setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        } else {
            Log.e("HomePageAdapter", "postId or clickedPost is null");
        }
    }

    private void removeLike(String postId, CombinedForumPost combinedForumPost, ImageView upvote) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userLikesRef = FirebaseDatabase.getInstance().getReference("user_likes").child(currentUserId);

        userLikesRef.child(postId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Successfully removed the like, update the UI accordingly
                Log.d("PostAdpater", "You have unliked this post");
                // Update the like count in the database and UI
                Picasso.get().load(R.drawable.growth_1_1).into(upvote);
                decrementLikeCount(postId, combinedForumPost);
            } else {
                // Failed to remove the like, handle the error
                Log.d("PostAdpater", "Failed to unlike the post");
            }
        });
    }

    private void decrementLikeCount(String postId, CombinedForumPost combinedForumPost) {
        if (postId != null && combinedForumPost != null && combinedForumPost.getName() != null) {
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("forums")
                    .child(combinedForumPost.getForumId())
                    .child("posts")
                    .child(postId)
                    .child("likes");

            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long likesCount = dataSnapshot.getValue(Long.class);
                    if (likesCount != null && likesCount > 0) {
                        // Decrement the like count
                        likesCount--;

                        combinedForumPost.getPost().setLikes(combinedForumPost.getPost().getLikes() - 1);
                        notifyDataSetChanged();
                        // Update the like count in the Firebase database
                        postRef.setValue(likesCount);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        } else {
            // Handle null values appropriately
            Log.e("HomePageAdapter", "postId or combinedForumPost is null");
        }


    }


    //DownVote functionality

    // Add a new method for handling downvote clicks


    public void onDownvoteClick(CombinedForumPost combinedForumPost, ImageView downvote, ImageView upvote) {
        if (combinedForumPost != null) {
            Posts post = combinedForumPost.getPost();
            if (post != null) {
                String postId = post.getPostId();
                if (postId != null && !postId.isEmpty()) {
                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference userLikesRef = FirebaseDatabase.getInstance().getReference("user_dislikes").child(currentUserId);

                    userLikesRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                removeLike(postId, combinedForumPost, upvote); // Remove like if exists
                                incrementDislikeCount(postId, combinedForumPost); // Increment dislike count
                            } else {
                                // User has already disliked this post, allow them to remove dislike
                                removeDislike(postId, combinedForumPost, downvote);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                        }
                    });
                } else {
                    Log.e("HomeFragment", "postId is null or empty");
                }
            } else {
                Log.e("HomeFragment", "Post is null");
            }
        } else {
            Log.e("HomeFragment", "CombinedForumPost is null");
        }
    }


    // Add methods for incrementing and decrementing dislike count
    private void incrementDislikeCount(String postId, CombinedForumPost clickedPost) {
        if (postId != null && clickedPost != null && clickedPost.getName() != null) {
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("forums")
                    .child(clickedPost.getForumId())
                    .child("posts")
                    .child(postId)
                    .child("dislikes");
            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Long dislikesCount = dataSnapshot.getValue(Long.class);
                        if (dislikesCount == null) {
                            dislikesCount = 0L;
                        }
                        // Increment the dislike count
                        dislikesCount++;

                        clickedPost.getPost().setDislikes(clickedPost.getPost().getDislikes() + 1);

                        notifyDataSetChanged();
                        // Update the dislike count in the Firebase database
                        postRef.setValue(dislikesCount);
                        DatabaseReference userDislikesRef = FirebaseDatabase.getInstance().getReference("user_dislikes").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userDislikesRef.child(postId).setValue(true);
                    } else {
                        // If the dislikes field doesn't exist, initialize it to 1
                        clickedPost.getPost().setDislikes(1L);
                        notifyDataSetChanged();
                        postRef.setValue(1L);
                        DatabaseReference userDislikesRef = FirebaseDatabase.getInstance().getReference("user_dislikes").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userDislikesRef.child(postId).setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        } else {
            Log.e("HomePageAdapter", "postId or clickedPost is null");
        }
    }


    private void removeDislike(String postId, CombinedForumPost combinedForumPost, ImageView downvote) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userDislikesRef = FirebaseDatabase.getInstance().getReference("user_dislikes").child(currentUserId);
        userDislikesRef.keepSynced(true);
        userDislikesRef.child(postId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("PostAdpater", "You have removed your dislike for this post");
                Picasso.get().load(R.drawable.growth_1_2).into(downvote);
                decrementDislikeCount(postId, combinedForumPost);
            } else {
                Log.d("PostAdpater", "Failed to remove dislike for the post");

            }
        });
    }


    private void decrementDislikeCount(String postId, CombinedForumPost combinedForumPost) {
        if (postId != null && combinedForumPost != null && combinedForumPost.getName() != null) {
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("forums")
                    .child(combinedForumPost.getForumId())
                    .child("posts")
                    .child(postId)
                    .child("dislikes");
            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long dislikesCount = dataSnapshot.getValue(Long.class);
                    if (dislikesCount != null && dislikesCount > 0) {
                        // Decrement the dislike count
                        dislikesCount--;
                        combinedForumPost.getPost().setDislikes(dislikesCount); // Set the updated dislike count to the post
                        notifyDataSetChanged(); // Notify the adapter of the data change
                        postRef.setValue(dislikesCount); // Update the dislike count in the database
                    } else {
                        // Handle the case where dislikesCount is null or <= 0
                        Log.e("HomePageAdapter", "Dislikes count is null or <= 0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        } else {
            Log.e("HomePageAdapter", "postId or combinedForumPost is null");
        }
    }
}



