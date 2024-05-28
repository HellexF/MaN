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
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)