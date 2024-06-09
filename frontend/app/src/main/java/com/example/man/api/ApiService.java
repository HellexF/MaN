package com.example.man.api;

import com.example.man.api.models.ChangeCategoryRequest;
import com.example.man.api.models.ChangeCategoryResponse;
import com.example.man.api.models.CheckEmailAvailableResponse;
import com.example.man.api.models.CheckPhoneNumberAvailableResponse;
import com.example.man.api.models.CreateCategoryRequest;
import com.example.man.api.models.CreateCategoryResponse;
import com.example.man.api.models.CreateNoteRequest;
import com.example.man.api.models.DeleteCategoryResponse;
import com.example.man.api.models.NoteContentsRequest;
import com.example.man.api.models.DeleteNoteResponse;
import com.example.man.api.models.EmailRequest;
import com.example.man.api.models.EmotionRequest;
import com.example.man.api.models.EmotionResponse;
import com.example.man.api.models.GetCategoriesResponse;
import com.example.man.api.models.GetNoteInfoRequest;
import com.example.man.api.models.GetNoteInfoResponse;
import com.example.man.api.models.LoginInfoRequest;
import com.example.man.api.models.LoginResponse;
import com.example.man.api.models.CreateNoteResponse;
import com.example.man.api.models.MessageResponse;
import com.example.man.api.models.PhoneNumberRequest;
import com.example.man.api.models.RegisterInfoRequest;
import com.example.man.api.models.RegistrationResponse;
import com.example.man.api.models.SearchNoteRequest;
import com.example.man.api.models.SearchNoteResponse;
import com.example.man.api.models.UpdateEmailRequest;
import com.example.man.api.models.UpdateNoteEmotionRequest;
import com.example.man.api.models.UpdateNoteTitleRequest;
import com.example.man.api.models.UpdatePasswordRequest;
import com.example.man.api.models.UpdatePhoneNumberRequest;
import com.example.man.api.models.UpdateSignatureRequest;
import com.example.man.api.models.UpdateUserInfoResponse;
import com.example.man.api.models.UpdateUsernameRequest;
import com.example.man.api.models.UploadAvatarResponse;
import com.example.man.api.models.UploadRequest;
import com.example.man.api.models.UploadResponse;
import com.example.man.api.models.UserInfoResponse;
import com.example.man.models.NoteContent;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/user/check_phone_number_available")
    Call<CheckPhoneNumberAvailableResponse> checkPhoneNumberAvailable(@Body PhoneNumberRequest phoneNumberRequest);
    @POST("/user/check_email_available")
    Call<CheckEmailAvailableResponse> checkEmailAvailable(@Body EmailRequest emailRequest);
    @POST("/user/register")
    Call<RegistrationResponse> register(@Body RegisterInfoRequest registerInfoRequest);
    @POST("/user/login")
    Call<LoginResponse> login(@Body LoginInfoRequest loginInfoRequest);
    @GET("/user/user_info/{user_id}/")
    Call<UserInfoResponse> getUser(@Path("user_id") int Id);
    @Multipart
    @POST("/user/upload_avatar")
    Call<UploadAvatarResponse> uploadAvatar(
            @Part MultipartBody.Part avatar,
            @Part("id") RequestBody userId
    );
    @PATCH("/user/update_username")
    Call<UpdateUserInfoResponse> updateUsername(@Body UpdateUsernameRequest request);
    @PATCH("/user/update_signature")
    Call<UpdateUserInfoResponse> updateSignature(@Body UpdateSignatureRequest request);
    @PATCH("/user/update_email")
    Call<UpdateUserInfoResponse> updateEmail(@Body UpdateEmailRequest request);
    @PATCH("/user/update_phone_number")
    Call<UpdateUserInfoResponse> updatePhoneNumber(@Body UpdatePhoneNumberRequest request);
    @PATCH("/user/update_password")
    Call<UpdateUserInfoResponse> updatePassword(@Body UpdatePasswordRequest request);
    @POST("/user/get_emotion")
    Call<EmotionResponse> getEmotion(@Body EmotionRequest request);
    @GET("/category/get_categories/{user_id}/")
    Call<GetCategoriesResponse> getCategories(@Path("user_id") int Id);
    @POST("/category/create_category")
    Call<CreateCategoryResponse> createCategory(@Body CreateCategoryRequest createCategoryRequest);
    @DELETE("/category/delete_category/{category_id}/")
    Call<DeleteCategoryResponse> deleteCategory(@Path("category_id") int Id);
    @PATCH("/note/get_note_info")
    Call<GetNoteInfoResponse> getNoteInfo(@Body GetNoteInfoRequest request);
    @POST("/note/create_note")
    Call<CreateNoteResponse> createNote(@Body CreateNoteRequest request);
    @POST("/note/change_category")
    Call<ChangeCategoryResponse> changeCategory(@Body ChangeCategoryRequest request);
    @DELETE("/note/delete_note/{note_id}/")
    Call<DeleteNoteResponse> deleteNote(@Path("note_id") int Id);
    @PATCH("/content/search_note")
    Call<SearchNoteResponse> searchNote(@Body SearchNoteRequest request);
    @POST("/content/get_note_contents")
    Call<List<NoteContent>> getNoteContents(@Body NoteContentsRequest request);
    @POST("/content/delete_note_contents")
    Call<MessageResponse> deleteNoteContents(@Body NoteContentsRequest request);
    @POST("/content/upload_note_contents")
    Call<MessageResponse> uploadNoteContents(@Body List<NoteContent> request);
    @PUT("/note/update_title/{note_id}/")
    Call<MessageResponse> updateNoteTitle(
            @Path("note_id") int noteId,
            @Body UpdateNoteTitleRequest request
    );
    @PUT("/note/update_emotion/{note_id}/")
    Call<MessageResponse> updateNoteEmotion(
            @Path("note_id") int noteId,
            @Body UpdateNoteEmotionRequest request
    );
    @POST("/content/upload")
    Call<UploadResponse> uploadContent(@Body UploadRequest request);
}