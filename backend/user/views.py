from django.shortcuts import render
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework import status

from .models import *
from .serializer import NoteUserSerializer


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

class LoginByEmailView(APIView):
    def post(self, request):
        data = request.data
        try:
            user = NoteUser.objects.get(
                email=data['email'],
                password=data['password'])
        except NoteUser.DoesNotExist:
            return Response({"message": "Invalid credentials"},
                            status=status.HTTP_401_UNAUTHORIZED)
        return Response({"userId": user.id,
                         "username": user.username,
                         "email": user.email,
                         "phone_number": user.phone_number,
                         "signature": user.signature,
                         "message": "Logged in"}, status=status.HTTP_200_OK)

class LoginByPhoneNumberView(APIView):
    def post(self, request):
        data = request.data
        try:
            user = NoteUser.objects.get(
                phone_number=data['phone_number'],
                password=data['password'])
        except NoteUser.DoesNotExist:
            return Response({"message": "Invalid credentials"},
                            status=status.HTTP_401_UNAUTHORIZED)
        return Response({"userId": user.id,
                         "username": user.username,
                         "email": user.email,
                         "phone_number": user.phone_number,
                         "signature": user.signature,
                         "message": "Logged in"}, status=status.HTTP_200_OK)

class RegisterView(APIView):
    def post(self, request):
        serializer = NoteUserSerializer(data=request.data)

        if serializer.is_valid():
            serializer.save()
            return Response({'message': 'User registered successfully'}, status=status.HTTP_201_CREATED)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
