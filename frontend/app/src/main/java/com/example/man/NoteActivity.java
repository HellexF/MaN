package com.example.man;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.man.adapters.NoteCategoriesAdapter;
import com.example.man.adapters.NoteListAdapter;
import com.example.man.api.ApiClient;
import com.example.man.api.ApiService;
import com.example.man.api.models.Category;
import com.example.man.api.models.CreateCategoryRequest;
import com.example.man.api.models.CreateCategoryResponse;
import com.example.man.api.models.GetCategoriesResponse;
import com.example.man.api.models.GetNoteInfoRequest;
import com.example.man.api.models.GetNoteInfoResponse;
import com.example.man.api.models.SearchNoteRequest;
import com.example.man.api.models.SearchNoteResponse;
import com.example.man.utils.SharedPreferencesManager;
import com.google.android.material.navigation.NavigationView;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import at.markushi.ui.CircleButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NoteCategoriesAdapter.OnItemSelectedListener {
    private String username = "";
    private String signature = "";
    private DrawerLayout drawerLayout;
    private Button sidebarButton;
    private Button displayButton;
    private Button addMenuButton;
    private SearchView searchView;
    private CircleButton addNoteButton;
    private TextView categoryTextView;
    private int display;
    private String category;
    private int categoryId;
    private NavigationView navigationView;
    private TextView usernameTextView;
    private TextView signatureTextView;
    private RecyclerView recyclerView;
    private RecyclerView noteRecyclerView;
    private ImageView avatarImageView;
    private NoteCategoriesAdapter adapter;
    private NoteListAdapter noteListAdapter;
    private List<Category> data = new ArrayList<>();
    private List<NoteInfo> noteInfo = new ArrayList<>();
    private ApiService apiService;
    GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(2, 50, false);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // 设置初始类别
        data.add(new Category(-1, "收集箱"));
        category = data.get(0).getName();
        categoryId = data.get(0).getId();

        // 设置笔记显示列表
        noteRecyclerView = findViewById(R.id.note_recycler_view);

        apiService = ApiClient.getClient().create(ApiService.class);
        // 获取当前登录用户的用户名
        username = SharedPreferencesManager.getUserName(this);
        signature = SharedPreferencesManager.getUserSignature(this);
        if (Objects.equals(signature, "")) signature = "未设置";

        // 获取当前用户笔记类别信息
        Call<GetCategoriesResponse> call = apiService.getCategories(Integer.parseInt(SharedPreferencesManager.getUserId(NoteActivity.this)));
        call.enqueue(new Callback<GetCategoriesResponse>() {
            @Override
            public void onResponse(Call<GetCategoriesResponse> call, Response<GetCategoriesResponse> response) {
                display = 0;
                data.addAll(response.body().getCategories());
                // 设置侧边栏按钮的ICON
                sidebarButton = findViewById(R.id.sidebar_button);
                Drawable icon = getResources().getDrawable(R.drawable.sidebar_icon);
                icon.setBounds(0, 0, 70, 70);
                sidebarButton.setCompoundDrawables(icon, null, null, null);
                // 设置侧边栏单击事件
                drawerLayout = findViewById(R.id.drawer_layout);
                navigationView = findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(NoteActivity.this);
                View headerView = navigationView.getHeaderView(0);
                usernameTextView = headerView.findViewById(R.id.username);
                usernameTextView.setText(username);
                signatureTextView = headerView.findViewById(R.id.signature);
                signatureTextView.setText(signature);
                sidebarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                });

                // 设置头像
                avatarImageView = headerView.findViewById(R.id.sidebar_avatar);
                Glide.with(NoteActivity.this)
                        .load("http://10.0.2.2:8000" + SharedPreferencesManager.getUserAvatar(NoteActivity.this))
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.user_avatar)
                                .error(R.drawable.user_avatar))
                        .into(avatarImageView);

                // 设置笔记显示方式按钮的ICON
                displayButton = findViewById(R.id.display_button);
                icon = getResources().getDrawable(R.drawable.list_icon);
                icon.setBounds(0, 0, 70, 70);
                displayButton.setCompoundDrawables(icon, null, null, null);
                // 设置笔记显示方式按钮的点击逻辑
                displayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (display == 0) {
                            display = 1;
                            Drawable displayIcon = getResources().getDrawable(R.drawable.grid_icon);
                            displayIcon.setBounds(0, 0, 70, 70);
                            displayButton.setCompoundDrawables(displayIcon, null, null, null);
                            noteRecyclerView.setLayoutManager(new GridLayoutManager(NoteActivity.this, 2));
                            noteRecyclerView.addItemDecoration(itemDecoration);
                            noteListAdapter.setGrid(true);
                            noteListAdapter.notifyDataSetChanged();
                        } else if (display == 1) {
                            display = 0;
                            Drawable displayIcon = getResources().getDrawable(R.drawable.list_icon);
                            displayIcon.setBounds(0, 0, 70, 70);
                            displayButton.setCompoundDrawables(displayIcon, null, null, null);
                            noteRecyclerView.setLayoutManager(new LinearLayoutManager(NoteActivity.this));
                            noteRecyclerView.removeItemDecoration(itemDecoration);
                            noteListAdapter.setGrid(false);
                            noteListAdapter.notifyDataSetChanged();
                        }
                    }
                });

                // 设置当前笔记类别
                categoryTextView = findViewById(R.id.category_text);
                categoryTextView.setText(category);

                // 设置侧边栏底部添加分类菜单按钮的ICON
                addMenuButton = findViewById(R.id.add_menu_button);
                icon = ContextCompat.getDrawable(NoteActivity.this, R.drawable.add_icon);
                icon.setBounds(0, 0, 50, 50);
                addMenuButton.setCompoundDrawables(icon, null, null, null);

                // 设置添加分类按钮的事件处理
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(NoteActivity.this));
                adapter = new NoteCategoriesAdapter(data);
                adapter.setContext(NoteActivity.this);
                adapter.setOnItemSelectedListener(NoteActivity.this);
                recyclerView.setAdapter(adapter);
                // 设置滑动菜单项删除
                ItemTouchHelperCallback mCallback = new ItemTouchHelperCallback(adapter);
                ItemTouchHelperExtension mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
                mItemTouchHelper.attachToRecyclerView(recyclerView);
                addMenuButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInputDialog();
                    }
                });

                // 设置跳转到个人信息页面
                avatarImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NoteActivity.this, InfoActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<GetCategoriesResponse> call, Throwable t) {
                Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
            }
        });

        // 获取笔记列表
        Call<GetNoteInfoResponse> call_ = apiService.getNoteInfo(new GetNoteInfoRequest(Integer.parseInt(SharedPreferencesManager.getUserId(this)), categoryId));
        call_.enqueue(new Callback<GetNoteInfoResponse>() {
            @Override
            public void onResponse(Call<GetNoteInfoResponse> call, Response<GetNoteInfoResponse> response) {
                noteInfo.addAll(response.body().getNoteInfo());
                noteListAdapter = new NoteListAdapter(NoteActivity.this, noteInfo);
                noteRecyclerView.setAdapter(noteListAdapter);
                noteRecyclerView.setLayoutManager(new LinearLayoutManager(NoteActivity.this));
            }

            @Override
            public void onFailure(Call<GetNoteInfoResponse> call, Throwable t) {
                Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
            }
        });

        // 设置添加笔记按钮
        addNoteButton = findViewById(R.id.add_note);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this, NoteContentActivity.class);
                startActivity(intent);
            }
        });

        // 设置笔记搜索
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (Objects.equals(newText, "")) {
                    // 获取笔记列表
                    Call<GetNoteInfoResponse> call_ = apiService.getNoteInfo(new GetNoteInfoRequest(Integer.parseInt(SharedPreferencesManager.getUserId(NoteActivity.this)), categoryId));
                    call_.enqueue(new Callback<GetNoteInfoResponse>() {
                        @Override
                        public void onResponse(Call<GetNoteInfoResponse> call, Response<GetNoteInfoResponse> response) {
                            noteInfo.clear();
                            noteInfo.addAll(response.body().getNoteInfo());
                            noteListAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<GetNoteInfoResponse> call, Throwable t) {
                            Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // 获取笔记列表
                    Call<SearchNoteResponse> call = apiService.searchNote(new SearchNoteRequest(Integer.parseInt(SharedPreferencesManager.getUserId(NoteActivity.this)), categoryId, newText));
                    call.enqueue(new Callback<SearchNoteResponse>() {
                        @Override
                        public void onResponse(Call<SearchNoteResponse> call, Response<SearchNoteResponse> response) {
                            noteInfo.clear();
                            noteInfo.addAll(response.body().getNoteInfo());
                            noteListAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<SearchNoteResponse> call, Throwable t) {
                            Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showInputDialog() {
        // 创建对话框的布局视图
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        // 创建 AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        // 获取对话框布局中的视图
        final EditText editTextInput = dialogView.findViewById(R.id.dialog_input);
        Button buttonOK = dialogView.findViewById(R.id.dialog_button_ok);
        Button buttonCancel = dialogView.findViewById(R.id.dialog_button_cancel);

        // 设置按钮点击事件
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = editTextInput.getText().toString();
                TextView dialogErrorTextView = dialogView.findViewById(R.id.dialog_error);;
                if (inputText.equals("")) {
                    dialogErrorTextView.setText("类别名不能为空");
                    dialogErrorTextView.setVisibility(TextView.VISIBLE);
                }
                else if (adapter.isItemExist(inputText)) {
                    dialogErrorTextView.setText("类别已存在");
                    dialogErrorTextView.setVisibility(TextView.VISIBLE);
                }
                else {
                    dialogErrorTextView.setVisibility(TextView.GONE);
                    // 处理用户输入的文字
                    Call<CreateCategoryResponse> call = apiService.createCategory(new CreateCategoryRequest(Integer.parseInt(SharedPreferencesManager.getUserId(NoteActivity.this)), inputText));
                    call.enqueue(new Callback<CreateCategoryResponse>() {
                        @Override
                        public void onResponse(Call<CreateCategoryResponse> call, Response<CreateCategoryResponse> response) {
                            if (response.isSuccessful()) {
                                data.add(new Category(response.body().getId(), inputText));
                                // 更新 RecyclerView
                                adapter.notifyItemInserted(data.size() - 1);
                                // 滚动到最后一个位置
                                recyclerView.scrollToPosition(data.size() - 1);
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<CreateCategoryResponse> call, Throwable t) {
                            Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                        }
                    });
                }
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
    public void onItemSelected(Category item) {
        category = item.getName();
        categoryId = item.getId();
        categoryTextView.setText(category);
        // 获取笔记列表
        Call<GetNoteInfoResponse> call = apiService.getNoteInfo(new GetNoteInfoRequest(Integer.parseInt(SharedPreferencesManager.getUserId(this)), categoryId));
        call.enqueue(new Callback<GetNoteInfoResponse>() {
            @Override
            public void onResponse(Call<GetNoteInfoResponse> call, Response<GetNoteInfoResponse> response) {
                noteInfo.clear();
                noteInfo.addAll(response.body().getNoteInfo());
                noteListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<GetNoteInfoResponse> call, Throwable t) {
                Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
            }
        });
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}