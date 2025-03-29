"""
Просто набор функций которые нужны, но в другие файлы логически невпихуемы
"""
import numpy as np
import pandas as pd
from matplotlib import pyplot as plt
import seaborn as sn

import knnClassifier


def normalize(df):
    """
    Нормализация значений(min/max)
    :param df: Dataframe с данными
    :return: Dataframe с нормализованными данными
    """
    for c_name, params in df.items():
        # mean = params.mean()
        # std = params.std()
        # ind_features[c_name] = (ind_features[c_name] - mean) / std
        minimum = min(params)
        maximum = max(params)
        diff = maximum - minimum
        df[c_name] = (df[c_name] - minimum) / diff
    return df


def predict_multiple(ks, x_train, y_train, x_test, y_test, show_matrix, _type="name"):
    """
    Производит предсказание класса для набора параметров k.
    :param ks: Массив со значениями k
    :param x_train: Тренировочный набор признаков
    :param y_train: Тренировочный набор классов
    :param x_test: Тестовый набор признаков
    :param y_test: Тестовый набор классов
    :param show_matrix: Boolean параметр, от которого зависит, будут ли строится матрицы ошибок
    :param _type: Тип матрицы по набору признаков(фиксированный или случайный)
    :return: Массив Tuple-ов вида (k, y_pred)
    """
    predictions = []
    for k in ks:
        pred = knnClassifier.test(x_train.values, y_train.values, x_test.values, k)
        predictions.append((k, pred))
        print(f'FOR K = {k}')
        # Вывод результатов
        c = 0
        for (real, predicted) in zip(y_test, pred):
            if real == predicted:
                c += 1
            # print(f' real: {real} ?= {predicted} :pred => {real == predicted}')

        # Вывод точности предсказаний
        print(f'accuracy: {round(c / len(y_test) * 100, 2)}%')
        if show_matrix:
            print('Матрица ошибок')
            confusion_matrix(y_test, pred, 3, k, _type)
        print('\n')
    return predictions


def confusion_matrix(y_true, y_pred, num_classes, k, _type="name"):
    """
    Строит матрицу ошибок
    :param y_true: Массив реальных значения y
    :param y_pred: Массив предсказанных значений y
    :param num_classes: Кол-во классов(y)
    :param k: Кол-во соседей(нужно для подписи матрицы)
    :param _type: Тип матрицы по набору признаков(фиксированный или случайный)
    :return: Двумерный массив - матрица ошибок
    """
    matrix = np.zeros((num_classes, num_classes))
    for true, pred in zip(y_true, y_pred):
        matrix[true - 1, pred - 1] += 1
    print(np.matrix(matrix))
    df_cm = pd.DataFrame(matrix, index=[1, 2, 3], columns=[1, 2, 3])
    plt.figure(figsize=(10, 9))
    sn.heatmap(df_cm, annot=True)
    plt.title(f'{_type} k = {k}', fontsize=15)
    plt.show()
    return matrix
