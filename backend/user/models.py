import os

from django.contrib.auth.base_user import BaseUserManager, AbstractBaseUser
from django.db import models
from django.contrib.auth.models import AbstractUser
from django.utils.deconstruct import deconstructible


@deconstructible
class PathAndRename:
    def __init__(self, sub_path):
        self.path = sub_path

    def __call__(self, instance, filename):
        ext = filename.split('.')[-1]
        # 设置文件名为用户ID
        filename = f"user_{instance.id}_avatar.{ext}"
        # 返回上传的路径和重命名后的文件名
        return os.path.join(self.path, filename)

class NoteUserManager(BaseUserManager):
    def create_user(self, username, email=None, phone_number=None, password=None, signature=None, avatar=None):
        if not username:
            raise ValueError('The Username field is required')
        if email and NoteUser.objects.filter(email=email).exists():
            raise ValueError('The Email field must be unique')
        if phone_number and NoteUser.objects.filter(phone_number=phone_number).exists():
            raise ValueError('The Phone number field must be unique')
        user = self.model(
            username=username,
            password=password,
            email=email,
            phone_number=phone_number,
            signature=signature,
            avatar=avatar,
        )
        user.save(using=self._db)
        return user

# Create your models here.
class NoteUser(models.Model):
    id = models.AutoField(primary_key=True)
    username = models.CharField(max_length=40, unique=True)
    password = models.CharField(max_length=40)
    email = models.CharField(max_length=50, blank=True, null=True, unique=True)
    phone_number = models.CharField(max_length=20, blank=True, null=True, unique=True)
    signature = models.CharField(max_length=60, blank=True, null=True)
    avatar = models.ImageField(upload_to=PathAndRename('images/avatars/'), default='images/avatars/default.png')

    objects = NoteUserManager()

    USERNAME_FIELD = 'username'
    REQUIRED_FIELDS = []

    def __str__(self):
        return self.username

    def get_username(self):
        return self.username

    def get_signature(self):
        return self.signature