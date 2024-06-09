from django.urls import path
from . import views

urlpatterns = [
    path(
        "search_note",
        views.SearchNoteView.as_view(),
        name="search_note"
    ),
    path(
        "get_note_contents",
        views.GetNoteContentsView.as_view(),
        name="get_note_contents"
    ),
    path(
        "delete_note_contents",
        views.DeleteNoteContentsView.as_view(),
        name="delete_note_contents"
    ),
    path(
        "upload_note_contents",
        views.UploadNoteContentsView.as_view(),
        name="upload_note_contents"
    ),
    path(
        "upload",
        views.UploadView.as_view(),
        name='upload'
    ),
]