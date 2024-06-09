package com.example.man.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.man.models.NoteInfo;
import com.example.man.R;
import com.example.man.api.ApiClient;
import com.example.man.api.ApiService;
import com.example.man.api.models.DeleteNoteResponse;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteListViewHolder> {
    private List<NoteInfo> noteInfo;
    private boolean isGrid = false;
    private Context mContext;
    private static final int TYPE_LIST = 0;
    private static final int TYPE_GRID = 1;
    private NoteListViewHolder lastSelectedHolder;
    private OnItemChangeCategoryListener listener;

    public interface OnItemChangeCategoryListener {
        void onItemChangeCategory(int position);
    }

    public NoteListAdapter(Context context, List<NoteInfo> data, OnItemChangeCategoryListener listener_) {
        mContext = context;
        noteInfo = data;
        listener = listener_;
    }

    @Override
    public int getItemViewType(int position) {
        return isGrid ? TYPE_GRID : TYPE_LIST;
    }

    public void setGrid(boolean isGrid) {
        this.isGrid = isGrid;
    }

    @NonNull
    @Override
    public NoteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_LIST) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_grid_item, parent, false);
        }
        return new NoteListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListViewHolder holder, int position) {
        NoteInfo item = noteInfo.get(holder.getAdapterPosition());
        if (!Objects.equals(item.image, "")) {
            Glide.with(mContext)
                    .load(item.image)
                    .into(holder.note_image);
        } else {
            holder.note_image.setImageResource(R.drawable.default_note_item);
        }
        holder.note_title.setText(item.title);
        holder.note_date.setText(item.date);
        holder.note_emotion.setText(item.emotion);
        // 长按删除
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (lastSelectedHolder != null && lastSelectedHolder != holder) {
                    lastSelectedHolder.modifyNoteLayout.setVisibility(View.GONE);
                }
                holder.modifyNoteLayout.setVisibility(View.VISIBLE);
                lastSelectedHolder = holder;
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteDialog(holder.getAdapterPosition());
                        holder.modifyNoteLayout.setVisibility(View.GONE);
                    }
                });
                holder.modifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemChangeCategory(holder.getAdapterPosition());
                        holder.modifyNoteLayout.setVisibility(View.GONE);
                    }
                });
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.modifyNoteLayout.getVisibility() == View.VISIBLE) {
                    holder.modifyNoteLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showDeleteDialog(int position) {
        // 创建对话框的布局视图
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.dialog_delete_note, null);

        // 创建 AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // 获取对话框布局中的视图
        TextView hint = dialogView.findViewById(R.id.dialog_text);
        Button buttonOK = dialogView.findViewById(R.id.dialog_button_ok);
        Button buttonCancel = dialogView.findViewById(R.id.dialog_button_cancel);

        // 设置提示文字
        String hint_text = "删除“" + noteInfo.get(position).title + "”？";
        hint.setText(hint_text);

        // 设置按钮点击事件
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<DeleteNoteResponse> call = apiService.deleteNote(noteInfo.get(position).id);
                call.enqueue(new Callback<DeleteNoteResponse>() {
                    @Override
                    public void onResponse(Call<DeleteNoteResponse> call, Response<DeleteNoteResponse> response) {
                        noteInfo.remove(position);
                        notifyItemRemoved(position);
                    }

                    @Override
                    public void onFailure(Call<DeleteNoteResponse> call, Throwable t) {
                        Toast.makeText(mContext, "网络连接错误", Toast.LENGTH_LONG).show();
                    }
                });
                dialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 显示对话框
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return noteInfo.size();
    }

    public static class NoteListViewHolder extends RecyclerView.ViewHolder {
        ImageView note_image;
        TextView note_title;
        TextView note_date;
        TextView note_emotion;
        RelativeLayout modifyNoteLayout;
        Button deleteButton;
        Button modifyButton;

        public NoteListViewHolder(@NonNull View itemView) {
            super(itemView);
            note_image = itemView.findViewById(R.id.list_note_image);
            note_title = itemView.findViewById(R.id.list_note_title);
            note_date = itemView.findViewById(R.id.list_note_date);
            note_emotion = itemView.findViewById(R.id.list_note_emotion);
            modifyNoteLayout = itemView.findViewById(R.id.modify_note_layout);
            deleteButton = itemView.findViewById(R.id.delete_button);
            modifyButton = itemView.findViewById(R.id.modify_button);
        }
    }
}
