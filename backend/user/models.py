from django.contrib.auth.base_user import BaseUserManager, AbstractBaseUser
from django.db import models
from django.contrib.auth.models import AbstractUser


class NoteUserManager(BaseUserManager):
    def create_user(self, username, email=None, phone_number=None, password=None, signature=None):
        if not username:
            raise ValueError('The Username field is required')
        user = self.model(
            username=username,
            password=password,
            email=email,
            phone_number=phone_number,
            signature=signature,
        )
        user.save(using=self._db)
        return user

# Create your models here.
class NoteUser(models.Model):
    username = models.CharField(max_length=15)
    password = models.CharField(max_length=20)
    email = models.CharField(max_length=30, blank=True, null=True)
    phone_number = models.CharField(max_length=20, blank=True, null=True)
    signature = models.CharField(max_length=40, blank=True, null=True)

    objects = NoteUserManager()

    USERNAME_FIELD = 'username'
    REQUIRED_FIELDS = []

    def __str__(self):
        return self.username
