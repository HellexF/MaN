from django.urls import path

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
]