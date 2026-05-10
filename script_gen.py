import random as rnd
from uuid import uuid4

# Генерация тестового файла для стресс-теста многопоточности
LENGTH = 1000
sem_options = ["Первый", "Второй", "Четвертый", "Пятый", "Восьмой"]
admin_options = ["Админ", "Абракадаб", "Ибн Хуссейн", "Гений мысли", "Армстронг", "Ткемали", "Кривин"]
color_options = ["Красный", "Синий", "Оранжевый", "Коричневый"]
with open(input("Введите имя файла: "), "w", encoding="cp1125") as f:
    for i in range(LENGTH):
        f.write("add\n")
        f.write(str(uuid4()) + "\n")
        for j in range(5):
            f.write(f"{rnd.randint(1, 10**6)}\n")
        f.write(rnd.choice(sem_options) + "\n")
        f.write(rnd.choice(admin_options) + "\n")
        for j in range(2):
            f.write(f"{rnd.randint(1, 10**6)}\n")
        f.write(rnd.choice(color_options) + "\n")
