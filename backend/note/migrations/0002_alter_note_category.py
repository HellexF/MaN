# Generated by Django 4.2.13 on 2024-05-29 03:05

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ("category", "0001_initial"),
        ("note", "0001_initial"),
    ]

    operations = [
        migrations.AlterField(
            model_name="note",
            name="category",
            field=models.ForeignKey(
                on_delete=django.db.models.deletion.CASCADE, to="category.category"
            ),
        ),
    ]
