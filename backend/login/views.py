from django.shortcuts import render
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework import status

from .models import *

# Create your views here.

class LoginByEmailView(APIView):
    def post(self, request):
        data = request.data
        try:
            user = NoteUser.objects.get(
                email=data['email'],
                password=data['password'])
        except BaseException:
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
        except BaseException:
            return Response({"message": "Invalid credentials"},
                            status=status.HTTP_401_UNAUTHORIZED)
        return Response({"userId": user.id,
                         "username": user.username,
                         "email": user.email,
                         "phone_number": user.phone_number,
                         "signature": user.signature,
                         "message": "Logged in"}, status=status.HTTP_200_OK)