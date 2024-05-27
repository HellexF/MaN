from django.shortcuts import render
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework import status
from django.contrib.auth import authenticate

from .models import *
from .serializer import NoteUserSerializer
from .serializer import LoginInfoSerializer


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
            serializer.save()
            return Response({'message': 'User registered successfully'}, status=status.HTTP_201_CREATED)

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

            return Response({ 'message': 'Success', 'username': user.get_username(), 'signature': user.get_signature() }, status=status.HTTP_200_OK)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

