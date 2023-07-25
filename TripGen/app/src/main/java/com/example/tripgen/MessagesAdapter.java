package com.example.tripgen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messages;

    public interface OnOptionClickListener {
        void onOptionClick(int position);
    }

    public interface OnPlaceClickListener {
        void onPlaceClick(String placename);
    }

    private OnOptionClickListener listener;
    private OnPlaceClickListener placeListener;

    public void setOnPlaceClickListener(OnPlaceClickListener listener) {
        this.placeListener = listener;
    }
    public void setOnOptionClickListener(OnOptionClickListener listener) {
        this.listener = listener;
    }


    public MessagesAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Message.TYPE_AI:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_ai, parent, false);
                return new MessageViewHolder(view);
            case Message.TYPE_USER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_user, parent, false);
                return new MessageViewHolder(view);
            case Message.TYPE_OPTIONS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_options, parent, false);
                return new OptionViewHolder(view);
            case Message.TYPE_LISTS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
                return new ListItemViewHolder(view);
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message currentMessage = messages.get(position);
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).messageTextView.setText(currentMessage.getContent());
        } else if (holder instanceof OptionViewHolder) {
            ((OptionViewHolder) holder).optionButton.setText(currentMessage.getContent());
            ((OptionViewHolder) holder).optionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onOptionClick(position);
                    }
                }
            });
        }else if (holder instanceof ListItemViewHolder) {
            ((ListItemViewHolder) holder).textView1.setText(currentMessage.getContent());
            // Set Bitmap as ImageView
            ((ListItemViewHolder) holder).imageView.setImageBitmap(currentMessage.getImageBitmap());

            ((ListItemViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (placeListener != null) {
                        String placeName = currentMessage.getContent(); // If the place name is stored in getContent()
                        placeListener.onPlaceClick(placeName);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView1, textView2,textView3;;

        ImageView imageView;

        public ListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }



    public class OptionViewHolder extends RecyclerView.ViewHolder {

        Button optionButton;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            optionButton = itemView.findViewById(R.id.messageTextView);
        }
    }
}
