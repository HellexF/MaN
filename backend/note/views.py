from django.utils import timezone
from django.shortcuts import render
from rest_framework import status
from rest_framework.views import APIView

from category.models import Category
from user.models import NoteUser
from .models import Note
from rest_framework.response import Response
from django.http import JsonResponse

class GetNoteInfoView(APIView):
    def patch(self, request):
        data = request.data
        try:
            userId = data['user_id']
            categoryId = data['category_id']
            if categoryId != -1:
                notes = Note.objects.filter(user_id=userId, category_id=categoryId)
            else:
                notes = Note.objects.filter(user_id=userId)

            response_data = [{'id': note.note_id, 'title': note.title, 'date': note.last_modified.strftime("%Y-%m-%d"), 'time': note.last_modified.strftime('%Y-%m-%d %H:%M'), 'emotion': note.emotion, 'image': note.image_url} for note in notes]
            return JsonResponse({'noteInfo': response_data})
        except:
            return Response({'message': 'API Error'}, status=status.HTTP_400_BAD_REQUEST)


class CreateNoteView(APIView):
    def post(self, request):
        data = request.data
        try:
            # 获取请求中的数据
            print(data)
            user_id = data['userId']
            category_id = data['categoryId']

            user = NoteUser.objects.get(id=user_id)
            category = Category.objects.get(id=category_id)

            note = Note.objects.create(
                user=user,
                title='未命名',
                category=category,
                emotion='',
                image_url='',
                last_modified=timezone.now()
            )

            # 准备响应数据
            response_data = {
                'id': note.note_id,
                'time': timezone.now().strftime('%Y-%m-%d %H:%M')
            }

            return Response(response_data, status=status.HTTP_201_CREATED)
        except NoteUser.DoesNotExist:
            return Response({'message': 'User not found'}, status=status.HTTP_404_NOT_FOUND)
        except Category.DoesNotExist:
            return Response({'message': 'Category not found'}, status=status.HTTP_404_NOT_FOUND)
        except Exception as e:
            print(str(e))
            return Response({'message': 'Error creating note', 'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)

class DeleteNoteView(APIView):
    def delete(self, request, note_id):
        try:
            note = Note.objects.get(note_id=note_id)
            note.delete()
            return Response({'message': 'Successful'}, status=status.HTTP_200_OK)
        except Note.DoesNotExist:
            return Response({'message': 'Note not found'}, status=status.HTTP_404_NOT_FOUND)

class UpdateNoteTitleView(APIView):
    def put(self, request, note_id):
        try:
            note = Note.objects.get(note_id=note_id)
            note.title = request.data.get('title', note.title)
            note.save()
            return Response({'message': 'Successful'}, status=status.HTTP_200_OK)
        except Note.DoesNotExist:
            return Response({'message': 'Note not found'}, status=status.HTTP_404_NOT_FOUND)

class UpdateNoteEmotionView(APIView):
    def put(self, request, note_id):
        try:
            note = Note.objects.get(note_id=note_id)
            note.title = request.data.get('title', note.title)
            note.last_modified = timezone.now()
            note.save()
            return Response({'message': 'Successful'}, status=status.HTTP_200_OK)
        except Note.DoesNotExist:
            return Response({'message': 'Note not found'}, status=status.HTTP_404_NOT_FOUND)
