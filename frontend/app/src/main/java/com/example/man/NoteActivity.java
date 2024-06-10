package com.example.man;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.example.man.api.models.ChangeCategoryRequest;
import com.example.man.api.models.ChangeCategoryResponse;
import com.example.man.api.models.CreateCategoryRequest;
import com.example.man.api.models.CreateCategoryResponse;
import com.example.man.api.models.CreateNoteRequest;
import com.example.man.api.models.GetCategoriesResponse;
import com.example.man.api.models.GetNoteInfoRequest;
import com.example.man.api.models.GetNoteInfoResponse;
import com.example.man.api.models.CreateNoteResponse;
import com.example.man.api.models.SearchNoteRequest;
import com.example.man.api.models.SearchNoteResponse;
import com.example.man.callbacks.ItemTouchHelperCallback;
import com.example.man.decorations.GridSpacingItemDecoration;
import com.example.man.models.NoteInfo;
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

public class NoteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NoteCategoriesAdapter.OnItemSelectedListener,
        NoteListAdapter.OnNoteClickListener, NoteListAdapter.OnItemChangeCategoryListener
{
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
    private final int CREATE_CATEGORY = 0;
    private final int SET_CATEGORY = 1;
    private final int CHANGE_CATEGORY = 2;
    private ApiService apiService;
    GridSpacingItemDecoration itemDecoration = new GridSpacingItemDecoration(2, 50, false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();
        categoryId = intent.getIntExtra("category_id", -1);
        display = intent.getIntExtra("display", 0);

        // 设置初始类别
        data.add(new Category(-1, "收集箱"));

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
                        searchView.clearFocus();
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
                if (display == 0) {
                    icon = getResources().getDrawable(R.drawable.list_icon);
                    icon.setBounds(0, 0, 70, 70);
                    displayButton.setCompoundDrawables(icon, null, null, null);
                } else if (display == 1) {
                    icon = getResources().getDrawable(R.drawable.grid_icon);
                    icon.setBounds(0, 0, 70, 70);
                    displayButton.setCompoundDrawables(icon, null, null, null);
                }
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
                        searchView.clearFocus();
                    }
                });

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

                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getId() == categoryId) {
                        category = data.get(i).getName();
                        adapter.setSelectedItem(i);
                        adapter.notifyDataSetChanged();
                    }
                }

                // 设置当前笔记类别
                categoryTextView = findViewById(R.id.category_text);
                categoryTextView.setText(category);

                // 设置滑动菜单项删除
                ItemTouchHelperCallback mCallback = new ItemTouchHelperCallback(adapter);
                ItemTouchHelperExtension mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
                mItemTouchHelper.attachToRecyclerView(recyclerView);
                addMenuButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInputDialog(CREATE_CATEGORY, -1);
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
                noteListAdapter = new NoteListAdapter(NoteActivity.this, noteInfo, NoteActivity.this, NoteActivity.this);
                noteRecyclerView.setAdapter(noteListAdapter);
                if (display == 0) {
                    noteRecyclerView.removeItemDecoration(itemDecoration);
                    noteListAdapter.setGrid(false);
                    noteRecyclerView.setLayoutManager(new LinearLayoutManager(NoteActivity.this));
                } else if (display == 1) {
                    noteRecyclerView.addItemDecoration(itemDecoration);
                    noteListAdapter.setGrid(true);
                    noteRecyclerView.setLayoutManager(new GridLayoutManager(NoteActivity.this, 2));
                }
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
                if (categoryId == -1) {
                    showInputDialog(SET_CATEGORY, -1);
                } else {
                    createNote(categoryId);
                }
            }
        });

        // 设置笔记搜索
        searchView = findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                }
            }
        });
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

        // 点击屏幕空白地方，搜索框失焦
        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                searchView.clearFocus();
                return false;
            }
        });
        noteRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                searchView.clearFocus();
                return false;
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onNoteClick(int position){
        Intent intent = new Intent(NoteActivity.this, NoteContentActivity.class);
        intent.putExtra("note_id", noteInfo.get(position).id);
        intent.putExtra("category_id", categoryId);
        intent.putExtra("last_modified", noteInfo.get(position).time);
        intent.putExtra("title", noteInfo.get(position).title);
        intent.putExtra("not_saved", false);
        intent.putExtra("display", display);
        startActivity(intent);
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

    private void createNote(int categoryId){
        Call<CreateNoteResponse> call = apiService.createNote(new CreateNoteRequest(Integer.parseInt(SharedPreferencesManager.getUserId(NoteActivity.this)), categoryId));
        call.enqueue(new Callback<CreateNoteResponse>() {
            @Override
            public void onResponse(Call<CreateNoteResponse> call, Response<CreateNoteResponse> response) {
                Intent intent = new Intent(NoteActivity.this, NoteContentActivity.class);
                intent.putExtra("note_id", response.body().getId());
                intent.putExtra("category_id", categoryId);
                intent.putExtra("last_modified", response.body().getTime());
                intent.putExtra("title", "未命名");
                intent.putExtra("not_saved", true);
                intent.putExtra("display", display);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<CreateNoteResponse> call, Throwable t) {
                Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showInputDialog(int type, int position) {
        // 创建对话框的布局视图
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_input, null);

        // 创建 AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        TextView titleTextView = dialogView.findViewById(R.id.dialog_text);
        if(type == SET_CATEGORY){
            titleTextView.setText("设置类别");
        }
        else if(type == CHANGE_CATEGORY){
            titleTextView.setText("修改类别");
        }

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
                int index = adapter.isItemExist(inputText);
                if (inputText.equals("")) {
                    dialogErrorTextView.setText("类别名不能为空");
                    dialogErrorTextView.setVisibility(TextView.VISIBLE);
                }
                else if (inputText.equals("收集箱")){
                    dialogErrorTextView.setText("不能使用此类名");
                    dialogErrorTextView.setVisibility(TextView.VISIBLE);
                }
                else if (inputText.equals(category)){
                    dialogErrorTextView.setText("笔记已经位于当前类内");
                    dialogErrorTextView.setVisibility(TextView.VISIBLE);
                }
                else if (index != -1) {
                    if(type == CREATE_CATEGORY){
                        dialogErrorTextView.setText("类别已存在");
                        dialogErrorTextView.setVisibility(TextView.VISIBLE);
                    }
                    else if(type == SET_CATEGORY){
                        // 创建笔记
                        createNote(data.get(index).getId());
                    }
                    else if (type == CHANGE_CATEGORY) {
                        Call<ChangeCategoryResponse> call = apiService.changeCategory(new ChangeCategoryRequest(noteInfo.get(position).id, inputText));
                        call.enqueue(new Callback<ChangeCategoryResponse>() {
                            @Override
                            public void onResponse(Call<ChangeCategoryResponse> call, Response<ChangeCategoryResponse> response) {
                                if (response.isSuccessful()) {
                                    // 获取笔记列表
                                    Call<GetNoteInfoResponse> call_ = apiService.getNoteInfo(new GetNoteInfoRequest(Integer.parseInt(SharedPreferencesManager.getUserId(NoteActivity.this)), categoryId));
                                    call_.enqueue(new Callback<GetNoteInfoResponse>() {
                                        @Override
                                        public void onResponse(Call<GetNoteInfoResponse> call, Response<GetNoteInfoResponse> response) {
                                            noteInfo.clear();
                                            noteInfo.addAll(response.body().getNoteInfo());
                                            noteListAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onFailure(Call<GetNoteInfoResponse> call, Throwable t) {
                                            Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<ChangeCategoryResponse> call, Throwable t) {
                                Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
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
                                if(type == CHANGE_CATEGORY){
                                    Call<ChangeCategoryResponse> call1 = apiService.changeCategory(new ChangeCategoryRequest(noteInfo.get(position).id, inputText));
                                    call1.enqueue(new Callback<ChangeCategoryResponse>() {
                                        @Override
                                        public void onResponse(Call<ChangeCategoryResponse> call, Response<ChangeCategoryResponse> response) {
                                            if (response.isSuccessful()) {
                                                // 获取笔记列表
                                                Call<GetNoteInfoResponse> call_ = apiService.getNoteInfo(new GetNoteInfoRequest(Integer.parseInt(SharedPreferencesManager.getUserId(NoteActivity.this)), categoryId));
                                                call_.enqueue(new Callback<GetNoteInfoResponse>() {
                                                    @Override
                                                    public void onResponse(Call<GetNoteInfoResponse> call, Response<GetNoteInfoResponse> response) {
                                                        noteInfo.clear();
                                                        noteInfo.addAll(response.body().getNoteInfo());
                                                        noteListAdapter.notifyDataSetChanged();
                                                        dialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<GetNoteInfoResponse> call, Throwable t) {
                                                        Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ChangeCategoryResponse> call, Throwable t) {
                                            Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    // 滚动到最后一个位置
                                    recyclerView.scrollToPosition(data.size() - 1);
                                }
                                else if(type == SET_CATEGORY){
                                    createNote(data.get(data.size() - 1).getId());
                                    dialog.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CreateCategoryResponse> call, Throwable t) {
                            Toast.makeText(NoteActivity.this, "网络连接错误", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                searchView.clearFocus();
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

    @Override
    public void onItemChangeCategory(int position) {
        showInputDialog(CHANGE_CATEGORY, position);
    }
}