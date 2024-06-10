import json

from openai import OpenAI
from django.shortcuts import render
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework import status
from django.contrib.auth import authenticate

from category.models import Category
from .models import *
from .serializer import NoteUserSerializer, AvatarUploadSerializer, UsernameUpdateSerializer, SignatureUpdateSerializer, \
    EmailUpdateSerializer, PhoneUpdateSerializer, PasswordUpdateSerializer
from .serializer import LoginInfoSerializer
from django.shortcuts import get_object_or_404


# Create your views here.

class CheckEmailAvailableView(APIView):
    def post(self, request):
        data = request.data
        try:
            user = NoteUser.objects.get(
                email=data["email"]
            )
        except NoteUser.DoesNotExist:
            return Response({"message": "Email has not been registered", "is_available": False},
                            status=status.HTTP_200_OK)
        return Response({"message": "Email exists", "is_available": True}, status=status.HTTP_200_OK)


class CheckPhoneNumberAvailableView(APIView):
    def post(self, request):
        data = request.data
        try:
            user = NoteUser.objects.get(
                phone_number=data["phone_number"]
            )
        except NoteUser.DoesNotExist:
            return Response({"message": "Phone number has not been registered", "is_available": False},
                            status=status.HTTP_200_OK)
        return Response({"message": "Phone number exists", "is_available": True}, status=status.HTTP_200_OK)

class RegisterView(APIView):
    def post(self, request):
        serializer = NoteUserSerializer(data=request.data)

        if serializer.is_valid():
            user = serializer.save()
            return Response({'message': 'User registered successfully', 'id': user.id}, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class LoginView(APIView):
    def post(self, request):
        serializer = LoginInfoSerializer(data=request.data)
        if serializer.is_valid():
            login_type = serializer.validated_data['type']
            password = serializer.validated_data['password']

            if login_type != 0:
                email = serializer.validated_data['email']
                try:
                    user = NoteUser.objects.get(email=email, password=password)
                except NoteUser.DoesNotExist:
                    return Response({'message': 'Invalid credentials'}, status=status.HTTP_400_BAD_REQUEST)
            else:
                phone_number = serializer.validated_data['phone_number']
                try:
                    user = NoteUser.objects.get(phone_number=phone_number, password=password)
                except NoteUser.DoesNotExist:
                    return Response({ 'message': 'Invalid credentials' }, status=status.HTTP_400_BAD_REQUEST)

            return Response({'message': 'Success', 'id': user.id}, status=status.HTTP_200_OK)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class UserInfoView(APIView):
    def get(self, request, user_id):
        user = get_object_or_404(NoteUser, id=user_id)
        serializer = NoteUserSerializer(user)
        return Response(serializer.data, status=status.HTTP_200_OK)

class UploadAvatarView(APIView):
    def post(self, request):
        user_id = request.data.get('id')
        try:
            user = NoteUser.objects.get(id=user_id)
        except NoteUser.DoesNotExist:
            return Response({'message': 'user not found'}, status=status.HTTP_404_NOT_FOUND)

        serializer = AvatarUploadSerializer(data=request.data)

        if serializer.is_valid():
            user.avatar = serializer.validated_data['avatar']
            user.save()
            return Response({'message': 'success', 'avatar_url': user.avatar.url}, status=status.HTTP_200_OK)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class UpdateUsernameView(APIView):
    def patch(self, request):
        serializer = UsernameUpdateSerializer(data=request.data)
        if serializer.is_valid():
            try:
                user = NoteUser.objects.get(id=serializer.validated_data['id'])
                serializer.update(user, serializer.validated_data)
                return Response({'message': 'success'}, status=status.HTTP_200_OK)
            except NoteUser.DoesNotExist:
                return Response({'message': 'user not found'}, status=status.HTTP_404_NOT_FOUND)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class UpdateSiagnatureView(APIView):
    def patch(self, request):
        serializer = SignatureUpdateSerializer(data=request.data)
        try:
            user = NoteUser.objects.get(id=request.data['id'])
            serializer.update(user, request.data)
            return Response({'message': 'success'}, status=status.HTTP_200_OK)
        except NoteUser.DoesNotExist:
            return Response({'message': 'user not found'}, status=status.HTTP_404_NOT_FOUND)

class UpdateEmailView(APIView):
    def patch(self, request):
        serializer = EmailUpdateSerializer(data=request.data)
        if serializer.is_valid():
            try:
                user = NoteUser.objects.get(id=request.data['id'])
                serializer.update(user, request.data)
                return Response({'message': 'Success'}, status=status.HTTP_200_OK)
            except NoteUser.DoesNotExist:
                return Response({'message': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class UpdatePhoneNumberView(APIView):
    def patch(self, request):
        serializer = PhoneUpdateSerializer(data=request.data)
        if serializer.is_valid():
            try:
                user = NoteUser.objects.get(id=request.data['id'])
                serializer.update(user, request.data)
                return Response({'message': 'Success'}, status=status.HTTP_200_OK)
            except NoteUser.DoesNotExist:
                return Response({'message': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class UpdatePasswordView(APIView):
    def patch(self, request):
        serializer = PasswordUpdateSerializer(data=request.data)
        if serializer.is_valid():
            try:
                user = NoteUser.objects.get(id=request.data['id'])
                serializer.update(user, request.data)
                return Response({'message': 'Success'}, status=status.HTTP_200_OK)
            except NoteUser.DoesNotExist:
                return Response({'message': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class GetEmotionView(APIView):
    def post(self, request):
        try:
            client = OpenAI(
                api_key="sk-3pFwaXe1DVsYCSLVc2QX47CKDHQXrMTDzGySjjrV7HckpvaB",
                base_url="https://api.moonshot.cn/v1",
            )

            completion = client.chat.completions.create(
                model="moonshot-v1-8k",
                messages=[
                    {"role": "user", "content": f"用一个词概括以下整段文本的情感：{request.data['prompt']}，你的回答只能包含一个表达情感的词语，不超过四个字"}
                ],
                temperature=0.3,
            )
            result = completion.choices[0].message.content
            if len(result) > 5:
                result = "无"
            return Response({'message': result}, status=status.HTTP_200_OK)
        except:
            return Response({'message': 'API Error'}, status=status.HTTP_400_BAD_REQUEST)
