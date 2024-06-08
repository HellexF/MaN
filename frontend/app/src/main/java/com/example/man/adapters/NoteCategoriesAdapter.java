package com.example.man.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.man.LoginActivity;
import com.example.man.NoteActivity;
import com.example.man.R;
import com.example.man.api.ApiClient;
import com.example.man.api.ApiService;
import com.example.man.api.models.Category;
import com.example.man.api.models.DeleteCategoryResponse;
import com.example.man.api.models.LoginResponse;
import com.example.man.api.models.UserInfoResponse;
import com.example.man.utils.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteCategoriesAdapter extends RecyclerView.Adapter<NoteCategoriesAdapter.MyViewHolder> {
    private List<Category> data;
    private int selectedItem = 0;
    private OnItemSelectedListener onItemSelectedListener;
    private Context mContext;

    public interface OnItemSelectedListener {
        void onItemSelected(Category item);
    }

    public void setContext (Context context){
        this.mContext = context;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    public NoteCategoriesAdapter(List<Category> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MyViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(data.get(position).getName(), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeItem(int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<DeleteCategoryResponse> call = apiService.deleteCategory(data.get(position).getId());;
        call.enqueue(new Callback<DeleteCategoryResponse>() {
            @Override
            public void onResponse(Call<DeleteCategoryResponse> call, Response<DeleteCategoryResponse> response) {
                data.remove(position);
                notifyItemRemoved(position);
            }

            @Override
            public void onFailure(Call<DeleteCategoryResponse> call, Throwable t) {
                Toast.makeText(mContext, "网络连接错误", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isItemExist(String text) {
        return data.contains(text);
    }

    public boolean isDeletable(int position) {
        return position != 0;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView;
        private Context context;
        private NoteCategoriesAdapter adapter;

        public MyViewHolder(@NonNull View itemView, NoteCategoriesAdapter adapter) {
            super(itemView);
            context = itemView.getContext();
            textView = itemView.findViewById(android.R.id.text1);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        public void bind(String text, int position) {
            textView.setText(text);
            if (position == adapter.getSelectedItem()) {
                textView.setTextColor(ContextCompat.getColor(context, R.color.purple));
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                GradientDrawable bgDrawable = new GradientDrawable();
                bgDrawable.setColor(ContextCompat.getColor(context, R.color.slight_light_yellow));
                bgDrawable.setCornerRadius(20);
                textView.setBackground(bgDrawable);
            } else {
                textView.setTextColor(ContextCompat.getColor(context, R.color.purple));
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
                textView.setBackground(null);
            }
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            params.leftMargin = params.rightMargin = (int) context.getResources().getDimension(R.dimen.margin_10dp);
            params.topMargin = position == 0 ? (int) context.getResources().getDimension(R.dimen.margin_10dp) : 0;
            textView.setLayoutParams(params);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            adapter.setSelectedItem(position);
            adapter.notifyDataSetChanged();
            if (adapter.onItemSelectedListener != null) {
                adapter.onItemSelectedListener.onItemSelected(adapter.data.get(position));
            }
        }
    }
}

