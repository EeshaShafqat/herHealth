package com.example.herhealth;


import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.os.Build;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.media.MediaRecorder;
import android.os.Environment;
import android.Manifest;

import org.json.JSONException;
import org.json.JSONObject;


public class AIChat extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    LinearLayout samplePromptLayout;
    LinearLayout conversationLayout;

    ScrollView conversationScrollView;

    TextView chatHeading;

    private EditText userInput;


    private ImageView sendIcon;


    //private ImageView menu;
    private TextView askanyQ;

    MediaPlayer mediaPlayerSent;

    private ProgressBar loadingIndicator;

    MediaPlayer mediaPlayerReceive;

    private boolean conversationStarted = false;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    Toolbar toolbar;

    private MediaPlayer mediaPlayer;


    //voice recording
    // Add these variables to your AIChat class

    private static int MICROPHONE_PERMISSION_CODE = 200;

    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 1001;
    // Get the external storage directory
    String relativePath = "audio_files/audio_record.3gp";
    private String AUDIO_FILE_PATH;

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    ImageView micImageView;


    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aichat);

        mContext = this;

        conversationScrollView = findViewById(R.id.conversationScrollView);

        micImageView = findViewById(R.id.mic);

        // Initialize mediaPlayer
        mediaPlayer = new MediaPlayer();


        if(isMicrophonePresent()){
            getMicrophonePermission();
        }

        checkInternalStoragePermission();

        // Change the directory to use internal storage
        File directory = new File(getFilesDir(), "audio_files");
        if (!directory.exists()) {
            directory.mkdirs();
        }

// Now create the file object
        AUDIO_FILE_PATH = new File(directory, "audio_record.3gp").getAbsolutePath();




        // hooks
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        navigationView.setItemIconTintList(null);


        //toolbar
        setSupportActionBar(toolbar);

//navigation drawer menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                // Adjust the translationX property of the specific views you want to slide
                View existingLayout = findViewById(R.id.existing_layout);
                Toolbar toolbar = findViewById(R.id.toolbar);

                // Calculate the new translationX value based on the drawer's slideOffset
                float translationX = drawerView.getWidth() * slideOffset;

                // Apply translationX to the specific views
                existingLayout.setTranslationX(translationX);
                // toolbar.setTranslationX(translationX);


            }


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Set a custom icon and title when the drawer is open
                // getSupportActionBar().setIcon(R.drawable.ic_menu);
                getSupportActionBar().setTitle("herHealth");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Reset the icon and title when the drawer is closed
                // getSupportActionBar().setIcon(R.drawable.ic_menu);
                getSupportActionBar().setTitle("herHealth");
            }

        };


        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Adjust layout parameters of the Toolbar

        ViewGroup.MarginLayoutParams toolbarParams = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        toolbarParams.setMarginStart(0);  // Set the desired start margin (adjust as needed)
        toolbar.setLayoutParams(toolbarParams);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.settings);


        conversationLayout = findViewById(R.id.conversationLayout);

        chatHeading = findViewById(R.id.chatHeading);

        // Get the layout where you want to add sample prompts
        samplePromptLayout = findViewById(R.id.samplePromptLayout); // Replace with your layout ID

        // Add sample prompts dynamically
        addSamplePrompt(samplePromptLayout, "Why is my menstrual cycle irregular?");
        addSamplePrompt(samplePromptLayout, "How to Cope with postpartum depression");
        addSamplePrompt(samplePromptLayout, "How to manage stress?");



        // Initialize views
        userInput = findViewById(R.id.input);
        sendIcon = findViewById(R.id.sendIcon);
        askanyQ = findViewById(R.id.askAnyQ);
        //menu = findViewById(R.id.menu);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        mediaPlayerSent = MediaPlayer.create(this, R.raw.message_sent_sound);

        mediaPlayerReceive = MediaPlayer.create(this, R.raw.notification_sound);

        // Set an OnClickListener for the send button
        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                // Get the text entered by the user
                String userText = userInput.getText().toString().trim();


                // Check if the user has entered text
                if (!userText.isEmpty()) {

                    mediaPlayerSent.start();

                    askanyQ.setVisibility(View.GONE);

                    loadingIndicator.setVisibility(View.VISIBLE);

                    hideKeyboard();

                    addMessageToConversation(userText, true); // Add user message

                    // Clear the userInput EditText
                    userInput.setText("");

                    // Generate and display a "garbage reply" in the chatbotReply TextView
                    generateReply(userText);

                }
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.settings) {

            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);

        } else if (itemId == R.id.forums) {

            Intent intent = new Intent(this, BottomNavigation.class);
            intent.putExtra("FRAGMENT_TO_LOAD", "home");
            startActivity(intent);

        } else if (itemId == R.id.chatbot) {

            Intent intent = new Intent(this, AIChat.class);
            startActivity(intent);

        } else if (itemId == R.id.signout) {
            MyUtility.signOut(this);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // Function to add messages to the conversation
    private void addMessageToConversation(String message, boolean isUser) {
        View messageView;
        if (isUser) {
            messageView = getLayoutInflater().inflate(R.layout.user_message_layout, null);
        } else {
            messageView = getLayoutInflater().inflate(R.layout.chatbot_message_layout, null);
        }

        TextView messageTextView = messageView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        samplePromptLayout.setVisibility(View.GONE);
        chatHeading.setVisibility(View.GONE);
        conversationLayout.addView(messageView);


        conversationScrollView.post(() -> {
            // Scroll to the bottom
            conversationScrollView.fullScroll(View.FOCUS_DOWN);
        });


    }

    // Function to hide the keyboard
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
        }
    }

    private void generateReply(String inputText) {
       // String url = "http://192.168.43.40:5000/predict"; // URL for the POST request
        String url ="";

        // Create a JSON object to hold the input text
//        JSONObject postData = new JSONObject();
//        try {
//            postData.put("query", inputText);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        // Create a new AsyncTask to handle the HTTP POST request
       // new PostRequestTask().execute(url, postData.toString());
        new PostRequestTask().execute(url, inputText);
    }

    // AsyncTask to handle the HTTP POST request
    class PostRequestTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String url = params[0];
            String postData = params[1];
            try {
                URL urlObject = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // Set content type to form data
                connection.setDoOutput(true);

                // Write the form data to the output stream
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write("query=" + URLEncoder.encode(postData, "UTF-8")); // Encode form data
                writer.flush();
                writer.close();
                outputStream.close();

                // Read the response from the server
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                inputStream.close();

                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        protected void onPostExecute(String response) {
            if (response != null) {
                // Handle the response from the server
                mediaPlayerReceive.start();
                loadingIndicator.setVisibility(View.GONE);
                response = response.replaceAll("^\"|\"$", ""); // Remove leading and trailing quotes if present

                response = response.replace("\n\n", "\n");
                response = response.replace("\n\n", System.getProperty("line.separator"));
                addMessageToConversation(response, false);
            } else {
                // Handle the case where no response is received
                loadingIndicator.setVisibility(View.GONE);
                Toast.makeText(AIChat.this, "No response received", Toast.LENGTH_SHORT).show();
            }
        }
    }


//******************** speech to text request ******************************


    private void generateReplyFromSpeech() {
        // Replace with your machine's IP address
       // String url = "http://192.168.43.40:5000/predict_speech";
        String url = "https://wholly-assuring-bobcat.ngrok-free.app/predict_speech";

      //  Toast.makeText(this, url, Toast.LENGTH_SHORT).show();

        new RetrieveFeedTask().execute(url);
    }


    // AsyncTask to handle the HTTP request
    class RetrieveFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    // Additional log statements for debugging
                    Log.d("AIChat", "Trying to connect to: " + urls[0]);

                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();

                // Additional log statements for debugging
                Log.e("AIChat", "Error in AsyncTask: " + e.getMessage());
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response != null) {

                mediaPlayerSent.start();

                askanyQ.setVisibility(View.GONE);

                loadingIndicator.setVisibility(View.VISIBLE);

                response = response.replaceAll("^\"|\"$|\\n$", "");

                addMessageToConversation(response, true);

                generateReply(response);

            } else {

                loadingIndicator.setVisibility(View.GONE);
                Toast.makeText(AIChat.this, "Didn't quite get that!", Toast.LENGTH_SHORT).show();

            }
        }

    }




    //***********************************************************************************************
    // Function to convert dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void addSamplePrompt(LinearLayout layout, String promptText) {

//        LinearLayout samplePromptLayout = new LinearLayout(this);
//        samplePromptLayout.setOrientation(LinearLayout.HORIZONTAL);
//        samplePromptLayout.setGravity(Gravity.CENTER_VERTICAL);
//        samplePromptLayout.setElevation(4 * getResources().getDisplayMetrics().density);

        CardView samplePromptLayout = new CardView(this);
        samplePromptLayout.setCardElevation(dpToPx(4)); // Set card elevation (shadow)
        samplePromptLayout.setRadius(dpToPx(8)); // Set corner radius

        // Create a TextView for the prompt text
        TextView samplePrompt = new TextView(this);

        samplePrompt.setText(promptText);
        samplePrompt.setTextSize(20);

        Typeface customFont = ResourcesCompat.getFont(this, R.font.outfit_regular); // Replace with your font resource
        samplePrompt.setTypeface(customFont);

        // Custom background drawable for rounded rectangle with inner shadow

       // Drawable roundedRectangle = ContextCompat.getDrawable(mContext, R.drawable.rounded_rect_inner_shadow);

       // Drawable roundedRectangle = ContextCompat.getDrawable(mContext, R.drawable.);
       // samplePrompt.setBackground(roundedRectangle);

        samplePrompt.setTextColor(getResources().getColor(R.color.black)); // Replace with your color
        samplePrompt.setPadding(40, 40, 40, 40); // Adjust padding as needed

        // Set the layout parameters for the TextView
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        samplePrompt.setLayoutParams(textParams);

        // Add the TextView to the CardView
        samplePromptLayout.addView(samplePrompt);

        // Set layout parameters for the CardView with margin for spacing
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(20)); // Add margin between cards
        samplePromptLayout.setLayoutParams(cardParams);

        // Add the CardView to the main layout
        layout.addView(samplePromptLayout);

        // Add click listener to handle prompt click
        samplePromptLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!conversationStarted) {

                    Animation scaleAnimation = AnimationUtils.loadAnimation(AIChat.this, R.anim.scale);
                    view.startAnimation(scaleAnimation);

                    mediaPlayerSent.start();
                    askanyQ.setVisibility(View.GONE);
                    loadingIndicator.setVisibility(View.VISIBLE);

                    // Set the clicked prompt text to the userInput EditText
                    addMessageToConversation(promptText, true); // Add user message

                    // Clear the userInput EditText
                    userInput.setText("");

                    // Toggle visibility of prompts
                    samplePromptLayout.setVisibility(View.GONE);

                    // Generate and display a "garbage reply" in the chatbotReply TextView
                    generateReply(promptText);
                    conversationStarted = true;
                } else {
                    // Handle prompt click after conversation has started (if needed)
                }
            }
        });
    }




    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//******************************RECORDING **************************************************
    public void onMicButtonClick(View view) {
        ImageView micImageView = findViewById(R.id.mic);
        ImageView voiceImageView = findViewById(R.id.voice);

        if (!isRecording) {
            startRecording();

            // Change the appearance of the microphone button to indicate recording
            micImageView.setVisibility(View.GONE);
            voiceImageView.setVisibility(View.VISIBLE);

            isRecording = true;

        } else {

            isRecording = false;

            stopRecording();

            // Revert the appearance of the microphone button to normal
            micImageView.setVisibility(View.VISIBLE);
            voiceImageView.setVisibility(View.GONE);

          //  playRecordedAudio();
            uploadAudioToFirebase(AUDIO_FILE_PATH);

        }
    }

    private void startRecording() {

        if (isExternalStorageWritable()) {
            try {

                // Use the existing AUDIO_FILE_PATH variable
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setOutputFile(AUDIO_FILE_PATH);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                mediaRecorder.prepare();
                mediaRecorder.start();

                Toast.makeText(this, "Recording has started", Toast.LENGTH_LONG).show();

                // Handle UI changes, if needed
                // For example, change the microphone button color or icon
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Error: External storage not available", Toast.LENGTH_LONG).show();
        }



    }


    private void stopRecording() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                // Notify the system that a new file was created
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(new File(AUDIO_FILE_PATH)));
                sendBroadcast(intent);

              //  Toast.makeText(this, "Recording has stopped", Toast.LENGTH_LONG).show();

                // Upload the recorded audio file to Firebase Storage

            } else {
             //   Toast.makeText(this, "MediaRecorder is null", Toast.LENGTH_LONG).show();
            }

            // Handle UI changes, if needed
            // For example, revert the microphone button color or icon
        } catch (IllegalStateException e) {
            e.printStackTrace();
            // Handle the exception as needed
          //  Toast.makeText(this, "Error stopping recording: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }





    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    private boolean isMicrophonePresent(){

        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }
        else {
            return false;
        }
    }


    private void getMicrophonePermission(){
        // Check for runtime permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.RECORD_AUDIO},MICROPHONE_PERMISSION_CODE);
        } else {
            // Permission already granted, proceed with your logic
        }
    }

    private void playRecordedAudio() {
        Log.d("AI CHAT", "Audio file path: " + AUDIO_FILE_PATH);
        File audioFile = new File(AUDIO_FILE_PATH);

        if (audioFile.exists()) {
            try {
                // Release any resources from previous playback

                mediaPlayer.reset();


                // Initialize the MediaPlayer
                mediaPlayer = new MediaPlayer();

                // Set the data source and prepare the MediaPlayer
                mediaPlayer.setDataSource(AUDIO_FILE_PATH);



                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
                Log.e("AIChat", "Error during playback: " + e.getMessage());
            }
        } else {
            Log.e("AIChat", "Error: Audio file not found at " + AUDIO_FILE_PATH);
        }
    }





    // Method to check and request external storage permission
    private void checkInternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_PERMISSION_CODE);
            } else {
                // Permission already granted, proceed with your logic
            }
        } else {
            // Runtime permissions not required for lower Android versions
            // Proceed with your logic
        }
    }

    // Override onRequestPermissionsResult to handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic
            } else {
                // Permission denied, handle accordingly (e.g., show a message, disable features)
                // You may also want to explain why the permission is needed to the user
            }
        }
        // You can handle other permission requests here if needed
    }

    // Add this function to your AIChat class
    private void playAudioFromFirebase(String downloadUrl) {
        // Get the Firebase Storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String fileName = "audio_record.3gp";

        // Create a reference to the audio file]//]c]]::cc]c:::c]]]]]]]]]]]]:]]:]]]:]c
        StorageReference audioRef = storageRef.child("audio_files/" + fileName);

        // Create a local file to store the downloaded audio
        File localFile = new File(getFilesDir(), "downloaded_audio.3gp");

        // Download the file to the local file path
        audioRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            // File downloaded successfully
        //    Toast.makeText(this, "File downloaded from Firebase Storage", Toast.LENGTH_SHORT).show();

            // Play the downloaded audio
            playLocalAudio(localFile.getAbsolutePath());
        }).addOnFailureListener(exception -> {
            // Handle unsuccessful downloads
           // Toast.makeText(this, "Error downloading file from Firebase Storage", Toast.LENGTH_SHORT).show();
        });
    }

    private void playLocalAudio(String filePath) {
        // Initialize the MediaPlayer
        mediaPlayer.reset();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());

        // Set the data source and prepare the MediaPlayer
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            Log.e("AIChat", "Error during playback: " + e.getMessage());
        }
    }


    private void uploadAudioToFirebase(String filePath) {


        // Get the Firebase Storage reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Generate a unique filename based on timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileName = "audio_record.3gp";

        // Create a reference to the audio file
        StorageReference audioRef = storageRef.child("audio_files/" + fileName);

        // Upload the file to Firebase Storage using putFile
        Uri fileUri = Uri.fromFile(new File(filePath));
        UploadTask uploadTask = audioRef.putFile(fileUri);

        // Register observers to listen for when the upload is done or if it fails
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // File uploaded successfully
           // Toast.makeText(this, "File uploaded to Firebase Storage", Toast.LENGTH_SHORT).show();

            generateReplyFromSpeech();
            // Now you can get the download URL if needed
            audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                // You can use this downloadUrl to access the uploaded file
               // playAudioFromFirebase(downloadUrl);
            });
        }).addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
           // Toast.makeText(this, "Error uploading file to Firebase Storage", Toast.LENGTH_SHORT).show();
        });
    }


}

