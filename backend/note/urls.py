from django.urls import path
from . import views

urlpatterns = [
    path(
        "get_note_info",
        views.GetNoteInfoView.as_view(),
        name="get_note_info"
    ),
    path(
        "create_note",
        views.CreateNoteView.as_view(),
        name="crate_note"
    ),
    path(
        "change_category",
        views.ChangeCategoryView.as_view(),
        name="change_category"
    ),
    path(
        "delete_note/<int:note_id>/",
        views.DeleteNoteView.as_view(),
        name='delete_note'
    ),
]