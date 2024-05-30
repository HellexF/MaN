from django.http import JsonResponse
from django.shortcuts import render
from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView

from category.models import Category
from user.models import NoteUser


class GetCategoriesView(APIView):
    def get(self, request, user_id):
        try:
            user = NoteUser.objects.get(id=user_id)
            categories = Category.objects.filter(user=user).values('id', 'name')
            return JsonResponse({'categories': list(categories)}, safe=False)
        except NoteUser.DoesNotExist:
            return Response({'message': 'User not found'}, status=status.HTTP_404_NOT_FOUND)


class CreateCategoryView(APIView):
    def post(self, request):
        try:
            user_id = request.data.get('id')
            category_name = request.data.get('name')
            if not user_id or not category_name:
                return Response({'message': 'user_id and name are required'}, status=status.HTTP_400_BAD_REQUEST)
            user = NoteUser.objects.get(id=user_id)
            category = Category(user=user, name=category_name)
            category.save()
            return Response({'id': category.id, 'name': category.name},
                            status=status.HTTP_201_CREATED)
        except NoteUser.DoesNotExist:
            return Response({'message': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
