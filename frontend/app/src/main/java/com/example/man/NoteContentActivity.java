package com.example.man;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;

import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.example.man.adapters.NoteContentAdapter;
import com.example.man.views.NoteEditText;

public class NoteContentActivity extends AppCompatActivity implements View.OnClickListener, NoteContentAdapter.OnItemViewClickListener{
    private View mContainer;
    private TextView mTitleTextView;
    private EditText mTitleEdit;
    private EditText mEditText;
    private RecyclerView mNoteContentList;
    private ArrayList<NoteContent> noteContents;
    private NoteContentAdapter noteContentAdapter;
    private LinearLayout toolbarContainer;
    private Button photoTool;
    private Button voiceTool;
    private PopupWindow keyboardToolBar;
    private boolean keyboardShowing = false;
    private static final int REQUEST_CODE_SELECT_PHOTO = 1;
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_AUDIO = 3;
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    private static final int REQUEST_RECORD_PERMISSION_CODE = 0x00000013;
    private Uri mCameraUri;
    private String mCameraImagePath;
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
    private boolean startRecord = false;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private Handler handler;
    private Runnable updateDurationRunnable;
    private long startTime = 0L;
    private long pausedTime = 0L;
    private long pauseStartTime = 0L;
    private Uri audioUri;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardListener;
    LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_content);

        mContainer = findViewById(R.id.container);
        mTitleEdit = findViewById(R.id.title_edit_view);
        mTitleTextView = findViewById(R.id.title_text_view);
        mNoteContentList = findViewById(R.id.note_content_list);
        // TODO 判断应该显示哪个
        mTitleEdit.setVisibility(View.VISIBLE);
        mTitleTextView.setVisibility(View.GONE);

        mTitleEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(keyboardListener);
                closePopupWindow();
                return false;
            }
        });

        mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTitleEdit();
            }
        });

        mContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mTitleEdit.hasFocus()) {
                    mTitleEdit.clearFocus();
                }
                return false;
            }
        });

        // 点击回车时失焦
        mTitleEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    mTitleEdit.clearFocus();
                    return true;
                }
                return false;
            }
        });
        mTitleEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    mTitleEdit.clearFocus();
                    return true;
                }
                return false;
            }
        });

        // 设置失焦时的处理逻辑
        mTitleEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                    getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(keyboardListener);

                    if (mTitleEdit.getText().toString().trim().isEmpty()) {
                        mTitleEdit.setVisibility(View.VISIBLE);
                        mTitleTextView.setVisibility(View.GONE);
                    } else {
                        mTitleTextView.setText(mTitleEdit.getText().toString().trim());
                        mTitleEdit.setVisibility(View.GONE);
                        mTitleTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        // mEditText = findViewById(R.id.edit_view);
        // mEditText.addTextChangedListener(this);

        // 键盘工具栏
        keyboardListener = new KeyboardOnGlobalChangeListener();
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(keyboardListener);

        noteContents = new ArrayList<NoteContent>();
        noteContents.add(new NoteContent(0, ""));
        noteContentAdapter = new NoteContentAdapter(this, noteContents, this);
        mNoteContentList.setLayoutManager(new LinearLayoutManager(this));
        mNoteContentList.setAdapter(noteContentAdapter);
        mNoteContentList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mTitleEdit.clearFocus();
                    clearEditTextFocus(v);
                }
                return false;
            }
        });
        mLayoutManager = new LinearLayoutManager(this);
        mNoteContentList.setLayoutManager(mLayoutManager);

        mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTitleEdit();
            }
        });
    }

    private void clearEditTextFocus(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        mNoteContentList.clearFocus();
    }

    private void showTitleEdit() {
        mTitleTextView.setVisibility(View.GONE);
        mTitleEdit.setVisibility(View.VISIBLE);
        mTitleEdit.requestFocus();
        showKeyboard();
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(mTitleEdit, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTitleEdit.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.photo_tool) {
            showPhotoOptions();
        } else if (id == R.id.voice_tool) {
            showVoiceOptions();
        }
    }

    @Override
    public void onItemViewClick(View view) {
        NoteEditText editText = (NoteEditText) view;
        editText.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int selStart = editText.getSelectionStart();
                int selEnd = editText.getSelectionEnd();

                Layout layout = editText.getLayout();
                int line = layout.getLineForOffset(selStart);
                int baseline = layout.getLineBaseline(line);

                // 获取光标的纵坐标
                int cursorY = baseline + 10;

                // 如果光标超过了屏幕底部，说明被软键盘遮挡
                if (cursorY > 970) {
                    // 计算需要滚动的距离
                    int scrollDistance = cursorY - 970;

                    // 滚动RecyclerView，使焦点所在的行位于软键盘上方
                    mNoteContentList.scrollBy(0, scrollDistance);
                }

                return true;
            }
        });
    }

    private void showPhotoOptions() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_media, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        Button buttonChoosePhoto = dialogView.findViewById(R.id.dialog_button_first);
        Button buttonTakePhoto = dialogView.findViewById(R.id.dialog_button_second);

        buttonChoosePhoto.setText("选取照片");
        buttonTakePhoto.setText("拍照");
        Drawable icon = getResources().getDrawable(R.drawable.gallery_icon);
        icon.setBounds(0, 0, 60, 60);
        buttonChoosePhoto.setCompoundDrawables(null, null, icon, null);
        icon = getResources().getDrawable(R.drawable.photo_icon);
        icon.setBounds(0, 0, 60, 60);
        buttonTakePhoto.setCompoundDrawables(null, null, icon, null);
        buttonChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoFromGallery();
                dialog.dismiss();
            }
        });
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndCamera();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void choosePhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO);
    }

    private void checkPermissionAndCamera() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            // 有调起相机拍照。
            openCamera();
        } else {
            // 没有权限，申请权限。
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 允许权限，拍照
                openCamera();
            } else {
                // 拒绝权限，弹出提示框。
                Toast.makeText(this,"拍照权限被拒绝",Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_RECORD_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 允许权限，录音
                showRecord();
            } else {
                // 拒绝权限，弹出提示框。
                Toast.makeText(this,"录音权限被拒绝",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Uri photoUri = null;

            if (isAndroidQ) {
                // 适配android 10
                photoUri = createImageUri();
            } else {
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    mCameraImagePath = photoFile.getAbsolutePath();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        // 适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }

            mCameraUri = photoUri;
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, REQUEST_CODE_TAKE_PHOTO);
            }
        }
    }

    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    private File createImageFile() throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    private void showVoiceOptions() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_media, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        Button buttonChooseVoice = dialogView.findViewById(R.id.dialog_button_first);
        Button buttonRecordVoice = dialogView.findViewById(R.id.dialog_button_second);

        buttonChooseVoice.setText("选择音频");
        buttonRecordVoice.setText("录音");
        Drawable icon = getResources().getDrawable(R.drawable.audio_icon);
        icon.setBounds(0, 0, 60, 60);
        buttonChooseVoice.setCompoundDrawables(null, null, icon, null);
        icon = getResources().getDrawable(R.drawable.voice_icon);
        icon.setBounds(0, 0, 60, 60);
        buttonRecordVoice.setCompoundDrawables(null, null, icon, null);
        buttonChooseVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVoice();
            }
        });
        buttonRecordVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkPermissionAndRecord();
            }
        });

        dialog.show();
    }

    private void chooseVoice() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_AUDIO);
    }

    private void checkPermissionAndRecord() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_PERMISSION_CODE);
        } else {
            showRecord();
        }
    }

    private void showRecord() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_record, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        Button buttonRetry = dialogView.findViewById(R.id.record_button_retry);
        Button buttonRecord = dialogView.findViewById(R.id.record_button_record);
        Button buttonStop = dialogView.findViewById(R.id.record_button_stop);
        TextView recordingDuration = dialogView.findViewById(R.id.record_duration);;

        Drawable icon = getResources().getDrawable(R.drawable.retry_icon_grey);
        icon.setBounds(0, 0, 100, 100);
        buttonRetry.setCompoundDrawables(null, icon, null, null);
        icon = getResources().getDrawable(R.drawable.voice_icon);
        icon.setBounds(0, 0, 100, 100);
        buttonRecord.setCompoundDrawables(null, icon, null, null);
        icon = getResources().getDrawable(R.drawable.stop_icon_grey);
        icon.setBounds(0, 0, 100, 100);
        buttonStop.setCompoundDrawables(null, icon, null, null);
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording && startRecord) {
                    stopRecording();
                    if (audioUri != null) {
                        getContentResolver().delete(audioUri, null, null);
                        audioUri = null;
                    }
                    startRecord = false;
                    isRecording = false;
                    recordingDuration.setText("00:00");
                    Drawable icon = getResources().getDrawable(R.drawable.voice_icon);
                    icon.setBounds(0, 0, 100, 100);
                    buttonRecord.setCompoundDrawables(null, icon, null, null);
                    buttonRecord.setText("开始录音");
                    icon = getResources().getDrawable(R.drawable.retry_icon_grey);
                    icon.setBounds(0, 0, 100, 100);
                    buttonRetry.setCompoundDrawables(null, icon, null, null);
                    buttonRetry.setTextColor(ContextCompat.getColor(v.getContext(), R.color.grey));
                    icon = getResources().getDrawable(R.drawable.stop_icon_grey);
                    icon.setBounds(0, 0, 100, 100);
                    buttonStop.setCompoundDrawables(null, icon, null, null);
                    buttonStop.setTextColor(ContextCompat.getColor(v.getContext(), R.color.grey));
                }
            }
        });
        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startRecord) {
                    startRecord = true;
                    isRecording = true;
                    Drawable icon = getResources().getDrawable(R.drawable.pause_icon);
                    icon.setBounds(0, 0, 100, 100);
                    buttonRecord.setCompoundDrawables(null, icon, null, null);
                    buttonRecord.setText("暂停录音");
                    icon = getResources().getDrawable(R.drawable.retry_icon_grey);
                    icon.setBounds(0, 0, 100, 100);
                    buttonRetry.setCompoundDrawables(null, icon, null, null);
                    buttonRetry.setTextColor(ContextCompat.getColor(v.getContext(), R.color.grey));
                    icon = getResources().getDrawable(R.drawable.stop_icon_grey);
                    icon.setBounds(0, 0, 100, 100);
                    buttonStop.setCompoundDrawables(null, icon, null, null);
                    buttonStop.setTextColor(ContextCompat.getColor(v.getContext(), R.color.grey));
                    startRecording(recordingDuration);
                } else if (isRecording) {
                    isRecording = false;
                    Drawable icon = getResources().getDrawable(R.drawable.start_icon);
                    icon.setBounds(0, 0, 100, 100);
                    buttonRecord.setCompoundDrawables(null, icon, null, null);
                    buttonRecord.setText("继续录音");
                    icon = getResources().getDrawable(R.drawable.retry_icon);
                    icon.setBounds(0, 0, 100, 100);
                    buttonRetry.setCompoundDrawables(null, icon, null, null);
                    buttonRetry.setTextColor(ContextCompat.getColor(v.getContext(), R.color.purple));
                    icon = getResources().getDrawable(R.drawable.stop_icon);
                    icon.setBounds(0, 0, 100, 100);
                    buttonStop.setCompoundDrawables(null, icon, null, null);
                    buttonStop.setTextColor(ContextCompat.getColor(v.getContext(), R.color.purple));
                    pauseRecording();
                } else {
                    isRecording = true;
                    Drawable icon = getResources().getDrawable(R.drawable.pause_icon);
                    icon.setBounds(0, 0, 100, 100);
                    buttonRecord.setCompoundDrawables(null, icon, null, null);
                    buttonRecord.setText("暂停录音");
                    icon = getResources().getDrawable(R.drawable.retry_icon_grey);
                    icon.setBounds(0, 0, 100, 100);
                    buttonRetry.setCompoundDrawables(null, icon, null, null);
                    buttonRetry.setTextColor(ContextCompat.getColor(v.getContext(), R.color.grey));
                    icon = getResources().getDrawable(R.drawable.stop_icon_grey);
                    icon.setBounds(0, 0, 100, 100);
                    buttonStop.setCompoundDrawables(null, icon, null, null);
                    buttonStop.setTextColor(ContextCompat.getColor(v.getContext(), R.color.grey));
                    resumeRecording();
                }
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording && startRecord) {
                    stopRecording();
                    startRecord = false;
                    isRecording = false;
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void startRecording(TextView recordingDuration) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "my_audio.3gp");
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings");

        audioUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        if (audioUri != null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mediaRecorder.setOutputFile(getContentResolver().openFileDescriptor(audioUri, "w").getFileDescriptor());
                mediaRecorder.prepare();
                mediaRecorder.start();
                startTime = System.currentTimeMillis();
                handler = new Handler();
                updateDurationRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (isRecording) {
                            long currentTime = System.currentTimeMillis();
                            long elapsedTime = currentTime - startTime - pausedTime;
                            int seconds = (int) (elapsedTime / 1000) % 60;
                            int minutes = (int) (elapsedTime / (1000 * 60)) % 60;
                            String timeString = String.format("%02d:%02d", minutes, seconds);
                            recordingDuration.setText(timeString);
                            handler.postDelayed(this, 1000);
                        }
                    }
                };
                handler.postDelayed(updateDurationRunnable, 1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pauseRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.pause();
            pauseStartTime = System.currentTimeMillis();
            handler.removeCallbacks(updateDurationRunnable);
        }
    }

    private void resumeRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.resume();
            pausedTime += System.currentTimeMillis() - pauseStartTime;
            handler.postDelayed(updateDurationRunnable, 1000);
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            handler.removeCallbacks(updateDurationRunnable);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_PHOTO) {
                Uri selectedImageUri = data.getData();
                addImageContent(selectedImageUri);
            } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                if (isAndroidQ) {
                    // Android 10 使用图片uri加载
                    // ivPhoto.setImageURI(mCameraUri);
                } else {
                    // 使用图片路径加载
                    // ivPhoto.setImageBitmap(BitmapFactory.decodeFile(mCameraImagePath));
                }
            } else if (requestCode == REQUEST_CODE_PICK_AUDIO) {
                if (data != null) {
                    Uri audioUri = data.getData();
                    // playAudio(audioUri);
                }
            }
        }
    }

    private void addImageContent(Uri imageUri) {
        noteContents.add(new NoteContent(NoteContent.IMAGE_TYPE_CONTENT, imageUri.toString()));
        noteContents.add(new NoteContent(NoteContent.TEXT_TYPE_CONTENT, ""));

        noteContentAdapter.notifyItemInserted(noteContents.size() - 2);
        noteContentAdapter.notifyItemInserted(noteContents.size() - 1);
    }

    private void showKeyboardTopPopupWindow(int x, int y) {
        if (keyboardToolBar != null && keyboardToolBar.isShowing()) {
            // 可能是输入法切换了输入模式，高度会变化（比如切换为语音输入）
            updateKeyboardTopPopupWindow(x, y);
            return;
        }

        View popupView = getLayoutInflater().inflate(R.layout.soft_keyboard_top_tool_view, null);

        photoTool = popupView.findViewById(R.id.photo_tool);
        voiceTool = popupView.findViewById(R.id.voice_tool);
        toolbarContainer = popupView.findViewById(R.id.toolbar_container);

        // 设置工具栏按钮的ICON
        Drawable icon = getResources().getDrawable(R.drawable.photo_icon);
        icon.setBounds(0, 0, 60, 60);
        photoTool.setCompoundDrawables(icon, null, null, null);
        icon = getResources().getDrawable(R.drawable.voice_icon);
        icon.setBounds(0, 0, 60, 60);
        voiceTool.setCompoundDrawables(icon, null, null, null);

        photoTool.setOnClickListener(this);
        voiceTool.setOnClickListener(this);

        keyboardToolBar = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        keyboardToolBar.setTouchable(true);
        keyboardToolBar.setOutsideTouchable(false);
        keyboardToolBar.setFocusable(false);
        keyboardToolBar.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        keyboardToolBar.showAtLocation(mContainer, Gravity.BOTTOM, x, y);
    }

    private void updateKeyboardTopPopupWindow(int x, int y) {
        if (keyboardToolBar != null && keyboardToolBar.isShowing()) {
            keyboardToolBar.update(x, y, keyboardToolBar.getWidth(), keyboardToolBar.getHeight());
        }
    }

    private void closePopupWindow() {
        if (keyboardToolBar != null && keyboardToolBar.isShowing()) {
            keyboardToolBar.dismiss();
            keyboardToolBar = null;
            photoTool = null;
            voiceTool = null;
        }
    }

    public class KeyboardOnGlobalChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private int getScreenHeight() {
            return  ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getHeight();
        }

        private int getScreenWidth() {
            return ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getWidth();
        }

        public int getKeyboardHeight(){
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            return getWindow().getDecorView().getBottom() - rect.bottom;
        }

        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int screenHeight = getScreenHeight();
            int keyboardHeight = getWindow().getDecorView().getBottom() - rect.bottom;
            boolean preShowing = keyboardShowing;
            if (Math.abs(keyboardHeight) > screenHeight / 5) {
                keyboardShowing = true;
                showKeyboardTopPopupWindow(getScreenWidth() / 2, keyboardHeight);
            } else {
                if (preShowing) {
                    closePopupWindow();
                }
                keyboardShowing = false;
            }
        }
    }
}