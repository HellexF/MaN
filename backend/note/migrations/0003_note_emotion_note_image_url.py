# Generated by Django 4.1 on 2024-06-08 15:59

from django.db import migrations, models


class Migration(migrations.Migration):
    dependencies = [
        ("note", "0002_alter_note_category"),
    ]

    operations = [
        migrations.AddField(
            model_name="note",
            name="emotion",
            field=models.CharField(default="", max_length=20),
        ),
        migrations.AddField(
            model_name="note",
            name="image_url",
            field=models.URLField(blank=True),
        ),
    ]