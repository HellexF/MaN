from django.utils import timezone
from django.shortcuts import render
from rest_framework import status
from rest_framework.views import APIView
import pytz

from category.models import Category
from user.models import NoteUser
from .models import Note
from content.models import Content
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

            response_data = []
            for note in notes:
                contents = Content.objects.filter(note_id=note.note_id, type=1)
                if len(contents) > 0:
                    response_data.append({'id': note.note_id, 'title': note.title,
                                          'date': note.last_modified.astimezone(pytz.timezone('Asia/Shanghai')).strftime("%Y-%m-%d"),
                                          'time': note.last_modified.astimezone(pytz.timezone('Asia/Shanghai')).strftime('%Y-%m-%d %H:%M'),
                                          'emotion': note.emotion, 'image': contents[0].content})
                else:
                    response_data.append({'id': note.note_id, 'title': note.title,
                                          'date': note.last_modified.astimezone(pytz.timezone('Asia/Shanghai')).strftime("%Y-%m-%d"),
                                          'time': note.last_modified.astimezone(pytz.timezone('Asia/Shanghai')).strftime('%Y-%m-%d %H:%M'),
                                          'emotion': note.emotion, 'image': note.image_url})

            return JsonResponse({'noteInfo': response_data})
        except:
            return Response({'message': 'API Error'}, status=status.HTTP_400_BAD_REQUEST)


class CreateNoteView(APIView):
    def post(self, request):
        data = request.data
        try:
            # 获取请求中的数据
            user_id = data['userId']
            category_id = data['categoryId']

            user = NoteUser.objects.get(id=user_id)
            category = Category.objects.get(id=category_id)

            temp = timezone.now().astimezone(pytz.timezone('Asia/Shanghai'))

            note = Note.objects.create(
                user=user,
                title='未命名',
                category=category,
                emotion='',
                image_url='',
                last_modified=temp
            )

            # 准备响应数据
            response_data = {
                'id': note.note_id,
                'time': temp.strftime('%Y-%m-%d %H:%M')
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
            Content.objects.filter(note_id=note_id).delete()
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
            note.emotion = request.data.get('emotion', note.emotion)
            temp = timezone.now().astimezone(pytz.timezone('Asia/Shanghai'))
            note.last_modified = temp
            note.save()
            return Response({'message': 'Successful'}, status=status.HTTP_200_OK)
        except Note.DoesNotExist:
            return Response({'message': 'Note not found'}, status=status.HTTP_404_NOT_FOUND)
class ChangeCategoryView(APIView):
    def post(self, request):
        data = request.data
        try:
            note_id = data['noteId']
            new_category_name = data['newCategoryName']
            note = Note.objects.get(note_id=note_id)
            category = Category.objects.get(name=new_category_name)
            note.category_id = category.id
            note.save()
            return Response({'message': 'Successful'}, status=status.HTTP_200_OK)
        except :
            return Response({'message': 'API Error'}, status=status.HTTP_400_BAD_REQUEST)
