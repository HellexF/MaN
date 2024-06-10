package com.example.man.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.man.models.NoteContent;
import com.example.man.R;
import com.example.man.utils.CustomedAnimation;
import com.example.man.views.NoteEditText;

import java.io.IOException;
import java.util.List;


public class NoteContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NoteContent> mData;
    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemViewClickListener mItemListener;
    private OnItemClickListener mListener;
    private OnItemDeleteListener mItemDeleteListener;
    private OnItemReplaceListener mItemReplaceListener;
    private OnTextChangedListener mTextChangedListener;

    // 定义视图类型常量
    public static final int TEXT_TYPE = 0;
    public static final int IMAGE_TYPE = 1;
    public static final int AUDIO_TYPE = 2;

    public interface OnItemViewClickListener {
        void onItemViewClick(View view);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    public interface OnItemReplaceListener{
        void onItemReplace(int position);
    }

    public interface OnTextChangedListener{
        void onTextChanged(int position, String content);
    }

    public NoteContentAdapter(Context context, List<NoteContent> data, OnItemViewClickListener listener, OnItemDeleteListener itemDeleteListener,
    OnItemReplaceListener itemReplaceListener, OnTextChangedListener textChangedListener, OnItemClickListener itemClickListener
    ) {
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mListener = itemClickListener;
        this.mItemListener = listener;
        this.mItemDeleteListener = itemDeleteListener;
        this.mItemReplaceListener = itemReplaceListener;
        this.mTextChangedListener = textChangedListener;
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
                return new TextTypeViewHolder(textView, mListener);
            case IMAGE_TYPE:
                View imgaeView = mInflater.inflate(R.layout.note_image_content_layout, parent, false);
                return new ImageTypeViewHolder(imgaeView, mListener);
            case AUDIO_TYPE:
                View audioView = mInflater.inflate(R.layout.note_audio_content_layout, parent, false);
                return new AudioTypeViewHolder(audioView, mListener);
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
                try {
                    ((AudioTypeViewHolder) holder).bind(noteContent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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

        TextTypeViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            editText = itemView.findViewById(R.id.text_content_edit);
            editText.setListener(mItemListener, listener);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mTextChangedListener != null) {
                        mTextChangedListener.onTextChanged(position, s.toString());
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText.show();
                }
            });

        }

        void bind(NoteContent noteContent) {
            editText.setText(noteContent.getContent());
            editText.setTextView(noteContent.getContent());
            editText.setPosition(getAdapterPosition());
        }

    }

    // ViewHolder for image type content
    class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        //LinearLayout menuLayout;
        RelativeLayout modifyContentLayout;
        Button deleteButton;
        Button replaceButton;

        ImageTypeViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_content_view);

            //menuLayout = itemView.findViewById(R.id.menu_layout);
            modifyContentLayout = itemView.findViewById(R.id.modify_content_layout);
            deleteButton = itemView.findViewById(R.id.delete_button);
            replaceButton = itemView.findViewById(R.id.replace_button);

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                    itemView.performClick();
                    return false;
                }
            });

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN && modifyContentLayout.getVisibility() == View.VISIBLE){
                        CustomedAnimation.fadeOutAnimation(modifyContentLayout);
                    }
                    return false;
                }
            });

            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    CustomedAnimation.fadeInAnimation(modifyContentLayout);
                    return true;
                }
            });

            imageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        CustomedAnimation.fadeOutAnimation(modifyContentLayout);
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mItemDeleteListener != null) {
                        CustomedAnimation.fadeOutAnimation(modifyContentLayout);
                        mItemDeleteListener.onItemDelete(position);
                    }
                }
            });

            replaceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mItemDeleteListener != null) {
                        CustomedAnimation.fadeOutAnimation(modifyContentLayout);
                        mItemReplaceListener.onItemReplace(position);
                    }
                }
            });
        }

        void bind(NoteContent noteContent) {
            Glide.with(imageView)
                    .load(noteContent.getContent())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(imageView);

            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            String ratio = width + ":" + height;

            ConstraintLayout constraintLayout = itemView.findViewById(R.id.image_content_constraint_layout);
            ConstraintSet set = new ConstraintSet();
            set.clone(constraintLayout);
            set.setDimensionRatio(imageView.getId(), ratio);
            set.applyTo(constraintLayout);
        }
    }

    // ViewHolder for audio type content
    class AudioTypeViewHolder extends RecyclerView.ViewHolder {
        private MediaPlayer mediaPlayer;
        private SeekBar seekBar;
        private ImageButton playPauseButton;
        private TextView progressTextView;
        private ImageButton deleteAudioButton;
        private ImageButton replaceAudioButton;

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int totalDuration = mediaPlayer.getDuration();
                    seekBar.setMax(totalDuration);
                    seekBar.setProgress(currentPosition);
                    progressTextView.setText(formatDuration(currentPosition) + " / " + formatDuration(totalDuration));
                    sendEmptyMessageDelayed(0, 50);
                }
            }
        };

        AudioTypeViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            mediaPlayer = new MediaPlayer();
            seekBar = itemView.findViewById(R.id.seek_bar);
            playPauseButton = itemView.findViewById(R.id.play_pause_button);
            progressTextView = itemView.findViewById(R.id.progressTextView);
            deleteAudioButton = itemView.findViewById(R.id.delete_audio_button);
            replaceAudioButton = itemView.findViewById(R.id.replace_audio_button);

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                    itemView.performClick();
                    return false;
                }
            });

            playPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        playPauseButton.setImageResource(R.drawable.play_button);
                    } else {
                        mediaPlayer.start();
                        playPauseButton.setImageResource(R.drawable.pause_button);
                        handler.sendEmptyMessage(0);
                    }
                }
            });

            deleteAudioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mItemDeleteListener != null) {
                        mItemDeleteListener.onItemDelete(position);
                    }
                }
            });

            replaceAudioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && mItemDeleteListener != null) {
                        mItemReplaceListener.onItemReplace(position);
                    }
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        public void setAudioUri(Uri uri) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(itemView.getContext(), uri);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        int totalDuration = mediaPlayer.getDuration();
                        progressTextView.setText("0:00 / " + formatDuration(totalDuration));
                        seekBar.setMax(totalDuration);
                    }
                });
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String formatDuration(int milliseconds) {
            int seconds = milliseconds / 1000;
            int minutes = seconds / 60;
            seconds = seconds % 60;
            return String.format("%d:%02d", minutes, seconds);
        }


        void bind(NoteContent noteContent) throws IOException {
            setAudioUri(Uri.parse(noteContent.getContent()));
        }
    }

}