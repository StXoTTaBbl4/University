import random
import pandas as pd
import utility
from knnClassifier import *

pd.set_option('display.max_columns', None)
dataframe = pd.read_csv('diabetes.csv')
dataframe.replace('', np.nan, inplace=True)
dataframe.dropna()
print(dataframe.info())
print(dataframe.describe())

dep_feature = dataframe['Outcome']
ind_features = dataframe.drop('Outcome', axis=1)
ind_features = utility.normalize(ind_features)

x_train = ind_features.sample(frac=0.8, random_state=42)
x_test = ind_features.drop(x_train.index)

y_train = dep_feature.sample(frac=0.8, random_state=42)
y_test = dep_feature.drop(y_train.index)

ks = [3, 5, 68, 100]
utility.predict_multiple(ks, x_train, y_train, x_test, y_test, True, "Fixed features")

rnd_indexes = random.sample(range(0, 7), random.randrange(0, 8, 1))
rnd_indexes.sort()
print(f'\nrnd columns indexes:\n {rnd_indexes}')
rnd_features = []
for i in range(len(rnd_indexes)):
    rnd_features.append(dataframe.columns[rnd_indexes[i]])
print(f'Selected features:\n {rnd_features}')

ind_features = utility.normalize(dataframe.loc[:, rnd_features])

x_train = ind_features.sample(frac=0.8, random_state=42)
x_test = ind_features.drop(x_train.index)

utility.predict_multiple(ks, x_train, y_train, x_test, y_test, True, "Random features")
