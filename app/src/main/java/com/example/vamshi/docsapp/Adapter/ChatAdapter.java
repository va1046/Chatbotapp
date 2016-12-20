package com.example.vamshi.docsapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.example.vamshi.docsapp.Model.ChatMessage;
import com.example.vamshi.docsapp.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vamshi on 20-12-2016.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.CustomViewHolder> {

    private List<ChatMessage> chatMessages = new ArrayList<>();
    private Context context;

    public ChatAdapter(List<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.CustomViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (chatMessage.isLeft()) {
            Log.d(ChatAdapter.class.getSimpleName(), "onBindViewHolder: " + chatMessage.getMessage());
            holder.mMssgBubble.setArrowDirection(ArrowDirection.LEFT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 15, 10);
            params.gravity = (Gravity.START|Gravity.CENTER_VERTICAL);
            holder.mMssgBubble.setLayoutParams(params);
            holder.mMssg.setText(chatMessage.getMessage());
        } else {
            holder.mMssgBubble.setArrowDirection(ArrowDirection.RIGHT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(15, 10, 10, 10);
            params.gravity = (Gravity.END|Gravity.CENTER_VERTICAL);
            holder.mMssgBubble.setLayoutParams(params);
            holder.mMssg.setText(chatMessage.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return (null != chatMessages ? chatMessages.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message)
        TextView mMssg;
        @BindView(R.id.bubblelayout)
        BubbleLayout mMssgBubble;

        public CustomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
