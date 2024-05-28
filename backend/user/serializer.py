from rest_framework import serializers
from .models import NoteUser


class NoteUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = NoteUser
        fields = ['id', 'username', 'email', 'phone_number', 'password', 'signature', 'avatar']
        extra_kwargs = {
            'password': {'write_only': True}  # 密码字段设置为只写
        }

    def validate(self, data):
        errors = {}
        # 检查是否提供了邮箱或电话号码
        if not data.get('email') and not data.get('phone_number'):
            errors['non_field_errors'] = 'Either email or phone number must be provided.'

        # 检查用户名是否已经存在
        if NoteUser.objects.filter(username=data['username']).exists():
            raise serializers.ValidationError({'username': 'note user with this username already exists.'})
        if data.get('phone_number') and NoteUser.objects.filter(phone_number=data['phone_number']).exists():
            raise serializers.ValidationError({'phone_number': 'note user with this phone number already exists.'})
        if data.get('email') and NoteUser.objects.filter(email=data['email']).exists():
            raise serializers.ValidationError({'email': 'note user with this email already exists.'})

        return data

    def create(self, validated_data):
        user = NoteUser.objects.create_user(
            username=validated_data['username'],
            email=validated_data.get('email'),
            phone_number=validated_data.get('phone_number'),
            password=validated_data['password'],
            signature="",
            avatar="images/avatars/default.png"
        )
        return user

class LoginInfoSerializer(serializers.Serializer):
    type = serializers.IntegerField()
    password = serializers.CharField(write_only=True)
    phone_number = serializers.CharField(required=False, allow_blank=True)
    email = serializers.CharField(required=False, allow_blank=True)

class AvatarUploadSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    avatar = serializers.ImageField()


class UsernameUpdateSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    username = serializers.CharField(max_length=40)

    def validate_username(self, value):
        if NoteUser.objects.filter(username=value).exists():
            raise serializers.ValidationError({'username': 'note user with this username already exists.'})
        return value

    def update(self, instance, validated_data):
        instance.username = validated_data.get('username', instance.username)
        instance.save()
        return instance

class SignatureUpdateSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    signature = serializers.CharField(max_length=40)

    def update(self, instance, validated_data):
        instance.signature = validated_data.get('signature', instance.signature)
        instance.save()
        return instance

class EmailUpdateSerializer(serializers.Serializer):
    id = serializers.IntegerField(required=True)
    old_email = serializers.CharField(required=True)
    new_email = serializers.CharField(required=True)

    def validate(self, data):
        # 验证用户是否存在
        try:
            user = NoteUser.objects.get(id=data['id'])
        except NoteUser.DoesNotExist:
            raise serializers.ValidationError("User not found")

        # 验证旧邮箱是否与用户的当前邮箱匹配
        if user.email != data['old_email']:
            raise serializers.ValidationError({'wrong_email': 'email does not match with user'})

        # 验证新邮箱是否已存在
        if NoteUser.objects.exclude(id=data['id']).filter(email=data['new_email']).exists():
            raise serializers.ValidationError({"email": 'note user with this email already exists.'})

        return data

    def update(self, instance, validated_data):
        instance.email = validated_data['new_email']
        instance.save()

class PhoneUpdateSerializer(serializers.Serializer):
    id = serializers.IntegerField(required=True)
    old_phone_number = serializers.CharField(required=True)
    new_phone_number = serializers.CharField(required=True)

    def validate(self, data):
        # 验证用户是否存在
        try:
            user = NoteUser.objects.get(id=data['id'])
        except NoteUser.DoesNotExist:
            raise serializers.ValidationError("User not found")

        # 验证旧手机是否与用户的当前手机匹配
        if user.phone_number != data['old_phone_number']:
            raise serializers.ValidationError({'wrong_phone_number': 'phone number does not match with user'})

        # 验证新手机是否已存在
        if NoteUser.objects.exclude(id=data['id']).filter(phone_number=data['new_phone_number']).exists():
            raise serializers.ValidationError({"phone_number": 'note user with this phone number already exists.'})

        return data

    def update(self, instance, validated_data):
        instance.phone_number = validated_data['new_phone_number']
        instance.save()

class PasswordUpdateSerializer(serializers.Serializer):
    id = serializers.IntegerField(required=True)
    old_password = serializers.CharField(required=True)
    new_password = serializers.CharField(required=True)

    def validate(self, data):
        # 验证用户是否存在
        try:
            user = NoteUser.objects.get(id=data['id'])
        except NoteUser.DoesNotExist:
            raise serializers.ValidationError("User not found")

        # 验证旧密码是否与用户的当前密码匹配
        if user.password != data['old_password']:
            raise serializers.ValidationError({'wrong_password': 'password number does not match with user'})

        return data

    def update(self, instance, validated_data):
        instance.password = validated_data['new_password']
        instance.save()
