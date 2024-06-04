package com.example.man.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.man.NoteActivity;
import com.example.man.NoteContent;
import com.example.man.NoteContentActivity;
import com.example.man.R;
import com.example.man.views.NoteEditText;

import java.util.List;

public class NoteContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NoteContent> mData;
    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemViewClickListener mListener;

    // 定义视图类型常量
    private static final int TEXT_TYPE = 0;
    private static final int IMAGE_TYPE = 1;
    private static final int AUDIO_TYPE = 2;

    public interface OnItemViewClickListener {
        void onItemViewClick(View view);
    }

    public NoteContentAdapter(Context context, List<NoteContent> data, OnItemViewClickListener listener) {
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        // 根据 NoteContent 的 type 属性返回不同的视图类型
        NoteContent noteContent = mData.get(position);
        switch (noteContent.getType()) {
            case NoteContent.TEXT_TYPE_CONTENT:
                return TEXT_TYPE;
            case NoteContent.IMAGE_TYPE_CONTENT:
                return IMAGE_TYPE;
            case NoteContent.AUDIO_TYPE_CONTENT:
                return AUDIO_TYPE;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TEXT_TYPE:
                View textView = mInflater.inflate(R.layout.note_text_content_layout, parent, false);
                return new TextTypeViewHolder(textView);
            case IMAGE_TYPE:
                View imgaeView = mInflater.inflate(R.layout.note_image_content_layout, parent, false);
                return new ImageTypeViewHolder(imgaeView);
            case AUDIO_TYPE:
                // Inflate audio type view layout
            default:
                throw new IllegalArgumentException("Unknown view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NoteContent noteContent = mData.get(position);
        switch (holder.getItemViewType()) {
            case TEXT_TYPE:
                ((TextTypeViewHolder) holder).bind(noteContent);
                break;
            case IMAGE_TYPE:
                ((ImageTypeViewHolder) holder).bind(noteContent);
                break;
            case AUDIO_TYPE:
                ((AudioTypeViewHolder) holder).bind(noteContent);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // ViewHolder for text type content
    class TextTypeViewHolder extends RecyclerView.ViewHolder {
        NoteEditText editText;

        TextTypeViewHolder(View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.text_content_edit);
            editText.setListener(mListener);
        }

        void bind(NoteContent noteContent) {
            editText.setText(noteContent.getContent());
        }

    }

    // ViewHolder for image type content
    class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageTypeViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_content_view);
        }

        void bind(NoteContent noteContent) {
            Glide.with(imageView)
                    .load(noteContent.getContent())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(imageView);
        }
    }

    // ViewHolder for audio type content
    class AudioTypeViewHolder extends RecyclerView.ViewHolder {
        // Define audio view elements here

        AudioTypeViewHolder(View itemView) {
            super(itemView);
            // Initialize audio view elements
        }

        void bind(NoteContent noteContent) {
            // Bind audio content to audio view elements
        }
    }
}