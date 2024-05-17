from django.db import models
from django.contrib.auth.models import AbstractUser


# Create your models here.
class NoteUser(models.Model):
    username = models.CharField(max_length=15)
    password = models.CharField(max_length=20)
    email = models.CharField(max_length=30)
    phone_number = models.CharField(max_length=20)
    signature = models.CharField(max_length=40)
