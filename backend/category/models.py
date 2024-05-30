from django.db import models

from user.models import NoteUser


class Category(models.Model):
    id = models.AutoField(primary_key=True)
    user = models.ForeignKey(NoteUser, on_delete=models.CASCADE)
    name = models.CharField(max_length=100)

    def __str__(self):
        return f'Category {self.name} by user {self.user}'
