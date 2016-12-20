package com.example.vamshi.docsapp.Activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vamshi.docsapp.Adapter.ChatAdapter;
import com.example.vamshi.docsapp.Model.ChatMessage;
import com.example.vamshi.docsapp.R;
import com.example.vamshi.docsapp.Util.NetworkUtil;
import com.orm.SugarContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public final static String PAR_KEY = "com.example.vamshi.parcelable";
    @BindView(R.id.ChatView)
    RecyclerView recyclerView;
    @BindView(R.id.messagetext)
    EditText mMssgtxt;
    @BindView(R.id.messagesend)
    Button mButtonSend;
    ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
    RequestQueue requestQueue;
    boolean isUserMessage = false;
    Context context;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(savedInstanceState != null){
//            chatMessages = (ArrayList<ChatMessage>) savedInstanceState.getParcelableArrayList(PAR_KEY);
//        }
        context = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SugarContext.init(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        init();
        initRecyclerView();
    }

    private void init() {
        requestQueue = Volley.newRequestQueue(this);
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
    }

    private void initRecyclerView() {
        chatMessages = (ArrayList<ChatMessage>) ChatMessage.listAll(ChatMessage.class);
        chatAdapter = new ChatAdapter(chatMessages, this);
        for (ChatMessage yp : chatMessages) {
            if (!yp.isOffline()) {
                Log.d(TAG, "initRecyclerView: " + yp.getMessage());
                addItem(true, new ChatMessage(false, yp.getMessage(), NetworkUtil.getConnectivityStatusString(context)));
                chatMessages.remove(yp);
            }

        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatAdapter);
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    private void addItem(boolean isUsermessage, ChatMessage item) {
        if (isUsermessage) {
            item.save();
            receiveMessage(item.getMessage());
            mMssgtxt.setText("");
            chatMessages.add(item);
            chatAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(chatMessages.size() - 1);
        } else {
            item.save();
            chatMessages.add(item);
            chatAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(chatMessages.size() - 1);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putParcelableArrayList(MainActivity.PAR_KEY, chatMessages);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return chatMessages;
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
}
