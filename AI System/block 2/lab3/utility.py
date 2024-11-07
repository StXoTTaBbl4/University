import numpy as np
import pandas as pd
from pandas import DataFrame


def initialize_features(path: str, dependent_feature: str) -> tuple[DataFrame, DataFrame]:
    """
    Метод читает .csv файл и возвращает два DataFrame'a - с зависимым и независимыми параметрами
    :param path: Путь к .csv файлу
    :param dependent_feature: Имя зависимого параметра
    :return: dependent_feature, independent_feature
    """
    dataframe = pd.read_csv(path)
    # Обработка строк с пустыми полями
    dataframe.replace('', np.nan, inplace=True)
    dataframe.dropna(axis=0, how='any', inplace=True)
    # Кодирование категориального признака
    dataframe = dataframe.replace({"Extracurricular Activities": {"Yes": 1, "No": 0}})
    # Визуализация статистики
    print(dataframe.info())
    print(dataframe.describe())
    print(f"\nЗависимый параметр: {dependent_feature}")
    # Перемешивание индексов
    dataframe = dataframe.sample(frac=1, random_state=42).reset_index(drop=True)
    # Разделение на зависимую и независимые переменные
    return dataframe[dependent_feature], dataframe.drop(dependent_feature, axis=1)


def split_data(x: DataFrame, y: DataFrame, length_of_frame: int) -> tuple[DataFrame, DataFrame, DataFrame, DataFrame]:
    """
    Разбивает данные на тестовые и тренировочные сеты
    :param x: независимые параметры
    :param y: зависимый параметр
    :param length_of_frame: длина массива, для вычисления кол-ва строк в выборках
    :return: x_train, x_test, y_train, y_test
    """
    split_index = int(length_of_frame * 0.8)
    # Мне нужны были массивы ndarray, так что вот такой вот костыль
    return (x[:split_index].to_numpy(),
            x[split_index:].to_numpy(),
            y[:split_index].to_numpy(),
            y[split_index:].to_numpy())


def normalized_matrix(matrix: DataFrame) -> DataFrame:
    """
    Нормализует значения данных
    :param matrix: Матрица значений
    :return: Нормализованная матрица
    """
    return (matrix-matrix.min())/(matrix.max()-matrix.min())
