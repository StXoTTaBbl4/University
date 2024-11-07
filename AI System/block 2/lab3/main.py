import sys

import numpy as np
import pandas as pd

import linear_regression
from utility import initialize_features, normalized_matrix, split_data

pd.set_option('display.max_columns', None)

dependent_f, independent_f = initialize_features('Student_Performance.csv', "Performance Index")

# Нормализация
dependent_f = normalized_matrix(dependent_f)
independent_f = normalized_matrix(independent_f)

# Разбиение
dataset_length = len(dependent_f)
x_train, x_test, y_train, y_test = split_data(independent_f, dependent_f, dataset_length)

weights_matrix = linear_regression.get_weights_matrix(x_train, y_train).reshape(-1, 1)
pred = linear_regression.test(weights_matrix, x_test)
r2 = linear_regression.r_squared(y_test, pred)

print(f'\nКоэффициенты: \n{weights_matrix}')
print(f'r^2: {r2}')

r2_scores = {}

print('\nВлияние признаков:')
# Поиск наиболее влиятельного признака
for feature in independent_f.columns:
    tmp_X = normalized_matrix(independent_f.drop(columns=[feature]))
    tmp_x_train, tmp_x_test, tmp_y_train, tmp_y_test = split_data(tmp_X, dependent_f, dataset_length)
    tmp_weights = linear_regression.get_weights_matrix(tmp_x_train, tmp_y_train).reshape(-1, 1)
    tmp_pred = linear_regression.test(tmp_weights, tmp_x_test)

    tmp_r2 = linear_regression.r_squared(tmp_y_test, tmp_pred)
    r2_scores[feature] = (tmp_r2, tmp_r2 - r2)

max_diff = sys.float_info.min
max_diff_name = ""
for key in r2_scores.keys():
    if abs(r2_scores[key][1]) > max_diff:
        max_diff = abs(r2_scores[key][1])
        max_diff_name = key
    print(f'name: {key} r2: {r2_scores[key][0]} diff: { r2_scores[key][1]}')
print(f'\nНаибольшее влияние: {max_diff_name}')
