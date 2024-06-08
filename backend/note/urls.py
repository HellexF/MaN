from django.urls import path
from . import views

urlpatterns = [
    path(
        "get_note_info",
        views.GetNoteInfoView.as_view(),
        name="get_note_info"
    ),
    path(
        "delete_note/<int:note_id>/",
        views.DeleteNoteView.as_view(),
        name='delete_note'
    ),
]