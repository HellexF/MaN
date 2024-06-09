from rest_framework import serializers

from content.models import Content

from rest_framework import serializers
from .models import Content, Note, NoteUser, Category

class ContentSerializer(serializers.ModelSerializer):
    note_id = serializers.IntegerField()
    user_id = serializers.IntegerField()
    category_id = serializers.IntegerField()
    content = serializers.CharField(required=False, allow_blank=True, allow_null=True)

    class Meta:
        model = Content
        fields = ['note_id', 'user_id', 'category_id', 'type', 'content']

    def create(self, validated_data):
        note_id = validated_data.pop('note_id')
        user_id = validated_data.pop('user_id')
        category_id = validated_data.pop('category_id')

        note = Note.objects.get(note_id=note_id)
        user = NoteUser.objects.get(id=user_id)
        category = Category.objects.get(id=category_id)

        content = Content.objects.create(note=note, user=user, category=category, **validated_data)
        return content

class NoteContentRequestSerializer(serializers.Serializer):
    note_id = serializers.IntegerField()
    user_id = serializers.IntegerField()
