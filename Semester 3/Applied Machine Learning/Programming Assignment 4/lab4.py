import numpy as np
import pandas
from sklearn import model_selection
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import confusion_matrix
from sklearn.ensemble import AdaBoostClassifier
from sklearn.tree import DecisionTreeClassifier

from sklearn.naive_bayes import GaussianNB
from sklearn.linear_model import LogisticRegression
from sklearn.neighbors import KNeighborsClassifier
from sklearn.neural_network import MLPClassifier

from sklearn.ensemble import VotingClassifier
    


#Read the data
train_data = np.loadtxt('lab4-train.csv', delimiter=",")
X_train = train_data[:,:4]
Y_train = train_data[:, 4]

test_data = np.loadtxt('lab4-test.csv', delimiter=",")
X_test = test_data[:,:4]
Y_test = test_data[:, 4]


def print_stats(model, X_test, Y_test):
    prediction = model.predict(X_test)
    matrix = confusion_matrix(Y_test, prediction)
    print("Confusion Matrix:\n", matrix)
    print("Overall Accuracy:\n",
          (matrix[0,0]+matrix[1,1])/sum(sum(matrix)))

def random_forest():

    #Hyper-parameters
    seed = None
    no_trees = 95

    #Random Forest Classifier
    print("\n\nRandom Forest")
    rf = RandomForestClassifier(n_estimators=no_trees,
                                   random_state=seed)
    rf.fit(X_train, Y_train)
    print_stats(rf, X_test, Y_test)
    return rf

def adaboost():

    #Hyper-parameters
    n_estimator=210
    learning_rate=0.9
    seed = None

    #AdaBoost.M1
    print("\n\nAdaBoost.M1")
    adb = AdaBoostClassifier(DecisionTreeClassifier(max_depth=1,
                                                      presort=True),
                               algorithm="SAMME",
                               random_state=seed,
                               learning_rate=learning_rate,
                               n_estimators = n_estimator)
    adb.fit(X_train, Y_train)
    print_stats(adb, X_test, Y_test)
    return adb


def logistic_regression():

    #Hyper-parameters
    penalty='l2'   #l1, l2
    tol=0.01

    #Logistic Regression
    print("\n\nLogistic Regression")
    logr = LogisticRegression(penalty=penalty,
                              tol=tol)
    logr.fit(X_train, Y_train)
    print_stats(logr, X_test, Y_test)
    return logr


def naive_bayes():
    
    #Hyper-parameters

    #Naive Bayes
    print("\n\nNaive Bayes")
    nb = GaussianNB()
    nb.fit(X_train, Y_train)
    print_stats(nb, X_test, Y_test)
    return nb



def knn():
    
    #Hyper-parameters
    n_neighbors=5

    #KNN
    print("\n\nk-Nearest Neighbors")
    knn = KNeighborsClassifier(n_neighbors=n_neighbors)
    knn.fit(X_train, Y_train)
    print_stats(knn, X_test, Y_test)
    return knn


def dt():
    
    #Hyper-parameters
    max_depth = 3
    max_features = 4
    
    #Decision Tree
    print("\n\nDecision Tree")
    dt = DecisionTreeClassifier(max_depth=max_depth, max_features=max_features)
    dt.fit(X_train, Y_train)
    print_stats(dt, X_test, Y_test)
    return dt


def nn():
    #Hyper-parameters
    solver='adam'
    activation='relu'
    alpha=0.001
    max_iter=200
    early_stopping=True
    hidden_layer_sizes=(100,3)
    random_state=1
    
    print("\n\nNeural Network")
    nn = MLPClassifier(solver=solver,
                       activation=activation,
                       alpha=alpha,
                       max_iter=max_iter,
                       early_stopping=early_stopping,
                       hidden_layer_sizes=hidden_layer_sizes,
                       random_state=random_state)
    nn.fit(X_train, Y_train)
    print_stats(nn, X_test, Y_test)

    return nn

def vc(voting, nn, knn, logr, nb, dt, weights = None):
    
    #Voting Classifier
    vc = VotingClassifier(estimators=[('NN', nn),
                          ('KNN', knn),
                          ('lr', logr),
                          ('NB', nb),
                          ('DT', dt)],
                          voting=voting,
                          weights=weights)
    vc.fit(X_train, Y_train)
    print_stats(vc, X_test, Y_test)
    return vc

def vc7(voting, nn, knn, logr, nb, dt, rf, adb, weights = None):
    
    #Voting Classifier
    vc7 = VotingClassifier(estimators=[('NN', nn),
                          ('KNN', knn),
                          ('lr', logr),
                          ('NB', nb),
                          ('DT', dt),
                          ('RF', rf),
                          ('adb', adb)],
                          voting=voting,
                          weights=weights)
    vc7.fit(X_train, Y_train)
    print_stats(vc7, X_test, Y_test)
    return vc7

rf = random_forest()
adb = adaboost()
nn = nn()
knn = knn()
logr = logistic_regression()
nb = naive_bayes()
dt = dt()


#Hyper-parameters
voting='hard'
weights = [1, 1, 2, 1, 2]                               

#VotingClassifier -unweighted
print("\n\nVotingClassifier -unweighted")
vc(voting, nn, knn, logr, nb, dt, None)

#VotingClassifier - Weighted
print("\n\nVotingClassifier -weighted")
vc(voting, nn, knn, logr, nb, dt, weights)

#VotingClassifier (7) -unweighted
print("\n\nVotingClassifier (7) -unweighted")
vc7(voting, nn, knn, logr, nb, dt, rf, adb, None)

#VotingClassifier (7) - Weighted
weights = [1, 1, 2, 1, 2, 1, 3]
voting = 'hard'
print("\n\nVotingClassifier (7) -weighted")
vc7(voting, nn, knn, logr, nb, dt, rf, adb, weights)





