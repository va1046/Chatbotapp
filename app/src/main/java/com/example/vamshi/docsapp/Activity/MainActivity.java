package com.example.vamshi.docsapp.Activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vamshi.docsapp.Adapter.ChatAdapter;
import com.example.vamshi.docsapp.BroadcastListeners.NetworkChangeReceiver;
import com.example.vamshi.docsapp.Model.ChatMessage;
import com.example.vamshi.docsapp.R;
import com.example.vamshi.docsapp.Util.NetworkUtil;
import com.orm.SugarContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NetworkChangeReceiver.NetworkChangeInterface, TextToSpeech.OnInitListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public final static String PAR_KEY = "com.example.vamshi.parcelable";
    ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
    RequestQueue requestQueue;
    boolean isUserMessage = false;
    Context context;
    NetworkChangeReceiver networkChangeReceiver;
    TextToSpeech textToSpeech;
    boolean speak = true;
    @BindView(R.id.cl_main)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.ChatView)
    RecyclerView recyclerView;
    @BindView(R.id.messagetext)
    EditText mMssgtxt;
    @BindView(R.id.messagesend)
    ImageView mButtonSend;
    @BindView(R.id.sound_image)
    ImageView iv_sound_image;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            chatMessages = savedInstanceState.getParcelableArrayList(PAR_KEY);
        }
        context = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SugarContext.init(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        networkChangeReceiver = new NetworkChangeReceiver();
        requestQueue = Volley.newRequestQueue(this);
        if (networkChangeReceiver != null)
            registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        init();
        initRecyclerView();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void init() {
        textToSpeech = new TextToSpeech(this, this);
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mMssgtxt.getText().toString().isEmpty()) {
                    isUserMessage = true;
                    addItem(isUserMessage, new ChatMessage(false, mMssgtxt.getText().toString(), NetworkUtil.getConnectivityStatusString(context)));
                } else {
                    Toast.makeText(MainActivity.this, "Please input your message!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        iv_sound_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (speak) {
                    speak = false;
                    iv_sound_image.setBackground(null);
                    iv_sound_image.setBackground(ContextCompat.getDrawable(context, R.drawable.sound_on));
                } else {
                    speak = true;
                    iv_sound_image.setBackground(null);
                    iv_sound_image.setBackground(ContextCompat.getDrawable(context, R.drawable.sound_off));
                }
            }
        });
    }

    private void initRecyclerView() {
        checkOfflineMessages();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(chatMessages.size() - 1);
            }
        }, 300);
    }

    private void checkOfflineMessages() {
        chatMessages = (ArrayList<ChatMessage>) ChatMessage.listAll(ChatMessage.class);
        chatAdapter = new ChatAdapter(chatMessages, this);

        if (chatMessages.size() > 0) {
            ArrayList<String> strings = new ArrayList<>();
            for (Iterator<ChatMessage> iter = chatMessages.iterator(); iter.hasNext(); ) {
                ChatMessage obj = iter.next();
                if (!obj.isOffline()) {
                    strings.add(obj.getMessage());
                    ChatMessage chatMessage = ChatMessage.findById(ChatMessage.class, obj.getId());
                    chatMessage.offline = NetworkUtil.getConnectivityStatusString(context);
                    chatMessage.save();
                    iter.remove();
                }
            }

            for (String s : strings) {
                addItem(true, new ChatMessage(false, s, NetworkUtil.getConnectivityStatusString(context)));
            }
            strings.clear();
        }
    }


    private void addItem(boolean isUsermessage, ChatMessage item) {
        if (isUsermessage) {

            if (speak) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(item.getMessage());
                } else {
                    ttsUnder20(item.getMessage());
                }
            }
            item.save();
            receiveMessage(item.getMessage());
            mMssgtxt.setText("");
            chatMessages.add(item);
            chatAdapter.notifyDataSetChanged();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.scrollToPosition(chatMessages.size() - 1);
                }
            }, 400);
        } else {

            if (speak) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(item.getMessage());
                } else {
                    ttsUnder20(item.getMessage());
                }
            }
            item.save();
            chatMessages.add(item);
            chatAdapter.notifyDataSetChanged();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.scrollToPosition(chatMessages.size() - 1);
                }
            }, 400);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PAR_KEY, chatMessages);
        super.onSaveInstanceState(outState);
    }

    private void receiveMessage(String message) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.personalityforge.com")
                .appendPath("api")
                .appendPath("chat")
                .appendQueryParameter("apiKey", "6nt5d1nJHkqbkphe")
                .appendQueryParameter("chatBotID", "63906")
                .appendQueryParameter("externalID", "chirag1")
                .appendQueryParameter("message", message);
        String url = builder.build().toString();
        Log.d(TAG, "receiveMessage: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("message");
                    String message = jsonObject1.getString("message");
                    Log.d(TAG, "onResponse: " + message);
                    addItem(false, new ChatMessage(true, message, NetworkUtil.getConnectivityStatusString(context)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onError: " + error.getMessage());
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    public void networkavailablelistener() {
        initRecyclerView();
    }

    @Override
    public void networknotavailablelistener() {
        Snackbar.make(coordinatorLayout, "Intent not avaiable!!", Snackbar.LENGTH_LONG);
    }

    @Override
    public void onInit(int i) {
        if (i != TextToSpeech.ERROR) {
            textToSpeech.setPitch(0.0f);
            textToSpeech.setSpeechRate(0.0f);
            textToSpeech.setLanguage(Locale.US);
        }
    }
}
