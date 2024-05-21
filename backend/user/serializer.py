from rest_framework import serializers
from .models import NoteUser


class NoteUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = NoteUser
        fields = ['id', 'username', 'email', 'phone_number', 'password']
        extra_kwargs = {
            'password': {'write_only': True}  # 密码字段设置为只写
        }

    def validate(self, data):
        # 检查是否提供了邮箱或电话号码
        if not data.get('email') and not data.get('phone_number'):
            raise serializers.ValidationError('Either email or phone number must be provided.')

        # 检查用户名是否已经存在
        if NoteUser.objects.filter(username=data['username']).exists():
            raise serializers.ValidationError('Username is already taken.')

        return data

    def create(self, validated_data):
        user = NoteUser.objects.create_user(
            username=validated_data['username'],
            email=validated_data.get('email'),
            phone_number=validated_data.get('phone_number'),
            password=validated_data['password'],
            signature=""
        )
        return user

class LoginInfoSerializer(serializers.Serializer):
    type = serializers.IntegerField()
    password = serializers.CharField(write_only=True)
    phone_number = serializers.CharField(required=False, allow_blank=True)
    email = serializers.CharField(required=False, allow_blank=True)
