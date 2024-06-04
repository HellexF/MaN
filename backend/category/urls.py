from django.urls import path

from category import views

urlpatterns = [
    path(
        "get_categories/<int:user_id>/",
        views.GetCategoriesView.as_view(),
        name='get_categories'
    ),
    path(
        "create_category",
        views.CreateCategoryView.as_view(),
        name='create_category'
    ),
    path(
        "delete_category/<int:category_id>/",
        views.DeleteCategoryView.as_view(),
        name='delete_category'
    )
]