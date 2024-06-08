from django.conf.urls.static import static
from django.urls import path

from MaN import settings
from . import views

urlpatterns = [
    path(
        "check_phone_number_available",
        views.CheckPhoneNumberAvailableView.as_view(),
        name="check_phone_number_available"
    ),
    path(
        "check_email_available",
        views.CheckEmailAvailableView.as_view(),
        name="check_email_available"
    ),
    path(
        "register",
        views.RegisterView.as_view(),
        name="register"
    ),
    path(
        "login",
        views.LoginView.as_view(),
        name="login"
    ),
    path(
        "user_info/<int:user_id>/",
        views.UserInfoView.as_view(),
        name='user_info'
    ),
    path(
        'upload_avatar',
         views.UploadAvatarView.as_view(),
         name='upload_avatar'
    ),
    path(
        'update_username',
         views.UpdateUsernameView.as_view(),
         name='update_username'
    ),
    path(
        'update_signature',
         views.UpdateSiagnatureView.as_view(),
         name='update_signature'
    ),
    path(
        'update_email',
         views.UpdateEmailView.as_view(),
         name='update_email'
    ),
    path(
        'update_phone_number',
         views.UpdatePhoneNumberView.as_view(),
         name='update_phone_number'
    ),
    path(
        'update_password',
         views.UpdatePasswordView.as_view(),
         name='update_password'
    ),
    path(
        "get_emotion",
        views.GetEmotionView.as_view(),
        name='get_emotion'
    ),
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)