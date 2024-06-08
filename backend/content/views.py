from django.shortcuts import render
from rest_framework.views import APIView
from .models import Content
from rest_framework.response import Response
from django.http import JsonResponse
from note.views import Note

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

            note_ids = contents.values_list('note_id', flat=True).distinct()
            response_data = []
            for note_id in note_ids:
                note = Note.objects.get(note_id=note_id)
                response_data.append({
                    'id': note.user,
                    'title': note.title,
                    'date': note.created_at.strftime('%Y-%m-%d'),
                    'emotion': note.emotion,
                    'image': note.image_url
                })
            return JsonResponse({'noteInfo': response_data})
        except:
            return Response({'message': 'API Error'}, status=status.HTTP_400_BAD_REQUEST)