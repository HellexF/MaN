# Generated by Django 4.2.13 on 2024-05-29 02:33

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ("user", "0003_alter_noteuser_avatar_alter_noteuser_email_and_more"),
    ]

    operations = [
        migrations.CreateModel(
            name="Note",
            fields=[
                ("note_id", models.AutoField(primary_key=True, serialize=False)),
                ("title", models.CharField(max_length=50)),
                ("category", models.CharField(max_length=100)),
                ("created_at", models.DateTimeField(auto_now_add=True)),
                (
                    "user",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE, to="user.noteuser"
                    ),
                ),
            ],
        ),
    ]