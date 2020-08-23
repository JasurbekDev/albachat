package com.example.simplechatapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private List<Message> messages;
    private Activity activity;

    public MessageAdapter(Activity context, int resource,
                                 List<Message> messages) {
        super(context, resource, messages);

        this.messages = messages;
        this.activity = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        LayoutInflater layoutInflater =
                (LayoutInflater)activity.getSystemService(
                        Activity.LAYOUT_INFLATER_SERVICE);

        Message message = getItem(position);
        int layoutResource = 0;
        int viewType = getItemViewType(position);

        if (viewType == 0) {
            layoutResource = R.layout.your_message_item;
        } else {
            layoutResource = R.layout.my_message_item;
        }

        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(
                    layoutResource, parent, false
            );
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        boolean isText = message.getImageUrl() == null;

        if (isText) {
            viewHolder.messageTextView.setVisibility(View.VISIBLE);
            viewHolder.photoImageView.setVisibility(View.GONE);
            viewHolder.messageTextView.setText(message.getText());
            viewHolder.nameTextView.setText(message.getName());
            viewHolder.timeTextView.setText(message.getTime());
        } else {
            viewHolder.messageTextView.setVisibility(View.GONE);
            viewHolder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.photoImageView.getContext())
                    .load(message.getImageUrl())
                    .into(viewHolder.photoImageView);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {

        int flag;
        Message message = messages.get(position);
        if (message.isMine()) {
            flag = 0;
        } else {
            flag = 1;
        }

        return flag;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ViewHolder {

        private ImageView photoImageView;
        private TextView messageTextView;
        private TextView nameTextView;
        private TextView timeTextView;

        public ViewHolder(View view) {
            photoImageView = view.findViewById(R.id.photoImageView);
            messageTextView = view.findViewById(R.id.messageTextView);
            nameTextView = view.findViewById(R.id.nameTextView);
            timeTextView = view.findViewById(R.id.timeTextView);

        }

    }
}
