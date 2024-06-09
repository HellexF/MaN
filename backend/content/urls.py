from django.urls import path
from . import views

urlpatterns = [
    path(
        "search_note",
        views.SearchNoteView.as_view(),
        name="search_note"
    ),
]