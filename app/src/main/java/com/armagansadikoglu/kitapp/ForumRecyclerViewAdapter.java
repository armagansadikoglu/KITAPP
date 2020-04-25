package com.armagansadikoglu.kitapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class ForumRecyclerViewAdapter extends RecyclerView.Adapter<ForumRecyclerViewAdapter.MyViewHolder> {
    //// on clikc ınterface'i

    private RecyclerViewAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(RecyclerViewAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    /////////////////

    Context mContext;
    List<Topic> mData;

    public ForumRecyclerViewAdapter(Context mContext, List<Topic> mData) {
        this.mContext = mContext;
        this.mData = mData;

    }

    @NonNull
    @Override
    public ForumRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.topicrow, parent, false);
        ForumRecyclerViewAdapter.MyViewHolder viewHolder = new ForumRecyclerViewAdapter.MyViewHolder(view, mListener); // burada m listener gönderildi

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForumRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.rowTopic.setText(mData.get(position).getTopicName());
        holder.rowCreator.setText(mData.get(position).getTopicCreatorUID());
        holder.rowDate.setText(mData.get(position).getTopicDate());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // interface buradan devam
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView rowTopic;
        private TextView rowCreator;
        private TextView rowDate;


        public MyViewHolder(@NonNull View itemView, final RecyclerViewAdapter.OnItemClickListener listener) {
            super(itemView);

            rowTopic = itemView.findViewById(R.id.forumTextViewTopic);
            rowCreator = itemView.findViewById(R.id.forumTextViewCreator);
            rowDate = itemView.findViewById(R.id.forumTextViewDate);

            // onclick interface
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }


    }
}
