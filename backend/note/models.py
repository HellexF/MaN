from django.db import models

from category.models import Category
from user.models import NoteUser


class Note(models.Model):
    user = models.ForeignKey(NoteUser, on_delete=models.CASCADE)
    note_id = models.AutoField(primary_key=True)
    title = models.CharField(max_length=50)
    category = models.ForeignKey(Category, on_delete=models.CASCADE)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f'Note {self.note_id} by user {self.user}'