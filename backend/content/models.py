from django.db import models

from user.models import NoteUser
from note.models import Note
from category.models import Category

class Content(models.Model):
    note = models.ForeignKey(Note, on_delete=models.CASCADE)
    user = models.ForeignKey(NoteUser, on_delete=models.CASCADE)
    category = models.ForeignKey(Category, on_delete=models.CASCADE)
    content_id = models.AutoField(primary_key=True)
    type = models.IntegerField()
    content = models.CharField(max_length=200)
