from django.shortcuts import render
from rest_framework.views import APIView
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

            response_data = [{'id': note.note_id, 'title': note.title, 'date': note.created_at.strftime("%Y-%m-%d"), 'emotion': note.emotion, 'image': note.image_url} for note in notes]
            return JsonResponse({'noteInfo': response_data})
        except:
            return Response({'message': 'API Error'}, status=status.HTTP_400_BAD_REQUEST)

class DeleteNoteView(APIView):
    def delete(self, request, note_id):
        try:
            note = Note.objects.get(note_id=note_id)
            note.delete()
            return Response({'message': 'Successful'}, status=status.HTTP_200_OK)
        except Note.DoesNotExist:
            return Response({'message': 'Note not found'}, status=status.HTTP_404_NOT_FOUND)
