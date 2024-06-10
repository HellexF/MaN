import os
import shutil
import uuid

from django.core.files.storage import default_storage
from django.shortcuts import render
from rest_framework import status
from rest_framework.generics import get_object_or_404
from rest_framework.views import APIView

from MaN import settings
from .models import Content
from rest_framework.response import Response
from django.http import JsonResponse
from note.views import Note
from .serializers import NoteContentRequestSerializer, ContentSerializer


class SearchNoteView(APIView):
    def patch(self, request):
        data = request.data
        try:
            userId = data['user_id']
            categoryId = data['category_id']
            key = data['key']
            if categoryId != -1:
                contents = Content.objects.filter(user_id=userId, category_id=categoryId, content__icontains=key)
            else:
                contents = Content.objects.filter(user_id=userId, content__icontains=key)

            content_note_ids = contents.values_list('note_id', flat=True).distinct()

            # 获取符合 title 条件的 note_id
            title_note_ids = Note.objects.filter(user_id=userId, title__icontains=key).values_list('note_id',
                                                                                                   flat=True).distinct()

            # 合并两个查询的结果
            note_ids = list(set(content_note_ids) | set(title_note_ids))
            response_data = []
            for note_id in note_ids:
                note = Note.objects.get(note_id=note_id)
                response_data.append({
                    'id': note.user,
                    'title': note.title,
                    'date': note.last_modified.strftime('%Y-%m-%d'),
                    'emotion': note.emotion,
                    'image': note.image_url
                })
            return JsonResponse({'noteInfo': response_data})
        except:
            return Response({'message': 'API Error'}, status=status.HTTP_400_BAD_REQUEST)

class GetNoteContentsView(APIView):
    def post(self, request):
        serializer = NoteContentRequestSerializer(data=request.data)
        if serializer.is_valid():
            note_id = serializer.validated_data['note_id']
            user_id = serializer.validated_data['user_id']

            # 获取 note 对象并验证 user_id 是否匹配
            note = get_object_or_404(Note, note_id=note_id, user_id=user_id)

            # 获取对应的所有 Content 项
            contents = Content.objects.filter(note=note)

            # 序列化内容项
            content_serializer = ContentSerializer(contents, many=True)
            return Response(content_serializer.data, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class DeleteNoteContentsView(APIView):
    def post(self, request):
        serializer = NoteContentRequestSerializer(data=request.data)
        if serializer.is_valid():
            note_id = serializer.validated_data['note_id']
            user_id = serializer.validated_data['user_id']

            # 获取 note 对象并验证 user_id 是否匹配
            note = get_object_or_404(Note, note_id=note_id, user_id=user_id)

            # 删除对应的所有 Content 项
            Content.objects.filter(note=note).delete()

            # 删除对应的文件夹
            image_path = os.path.join(settings.MEDIA_ROOT, 'images', 'note_images', f'{id}')
            if os.path.exists(image_path):
                shutil.rmtree(image_path)
            audio_path = os.path.join(settings.MEDIA_ROOT, 'audios', 'note_audios', f'{id}')
            if os.path.exists(audio_path):
                shutil.rmtree(audio_path)

            return Response({"message": "Contents deleted successfully."}, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class UploadView(APIView):
    def post(self, request):
        file = bytes([(byte + 256) if byte < 0 else byte for byte in request.data.get('file')])
        type = request.data.get('type')
        id = request.data.get('id')

        random_filename = str(uuid.uuid4())
        directory_path = ""
        file_path = ""
        content = "http://10.0.0.2:8000/media"

        if type == 1:
            directory_path = os.path.join(settings.MEDIA_ROOT, 'images', 'note_images', f'{id}')
            file_path = os.path.join(directory_path, random_filename + '.jpg')
            content = content + f'/media/images/note_images/{id}/{random_filename}.jpg'
        elif type == 2:
            directory_path = os.path.join(settings.MEDIA_ROOT, 'audios', 'note_audios', f'{id}')
            file_path = os.path.join(directory_path, random_filename + '.mp3')
            content = content + f'/media/audios/note_audios/{id}/{random_filename}.mp3'

        os.makedirs(directory_path)

        with open(file_path, 'wb') as f:
            f.write(file)
        return Response({'type': type, 'content': content}, status=status.HTTP_200_OK)

class UploadNoteContentsView(APIView):
    def post(self, request):
        serializer = ContentSerializer(data=request.data, many=True)
        if serializer.is_valid():
            serializer.save()
            return Response({"message": "Contents saved successfully."}, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)