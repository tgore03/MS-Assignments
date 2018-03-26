# -*- coding: utf-8 -*-
"""
Created on Thu Mar  1 12:13:14 2018

@author: tgore03
"""

import numpy as np
import matplotlib.pyplot as plt
from sklearn.linear_model import SGDClassifier
from sklearn.linear_model import LogisticRegression
import random
from sklearn.metrics import log_loss




#Generate Data
mu, sigma = 0.5, 0.3
s1 = (np.random.randn(100, 2) / 10) + 0.5
s2 = (np.random.randn(100, 2) / 10) - 0.5
y1 = np.ones(100)
y2 = np.zeros(100)

x = np.vstack((s1, s2))
y = np.hstack((y1, y2))

#Train Logistic Regression Model
def sigmoid(scores):
    return 1 / (1 + np.exp(-scores))


def log_likelihood(features, target, weights):
    z = np.dot(features, weights)
    ll = np.sum( target*z - np.log(1 + np.exp(z)) )
    return ll

def logistic_regression(features, target, num_steps=30000, learning_rate=0.001, add_intercept = False):
    #Preprocess data
    if add_intercept:
        intercept = np.ones((features.shape[0], 1))
        features = np.hstack((intercept, features))
    weights = np.zeros(features.shape[1])
    
    #Initilize variables
    i=0
    loss = [None]*(num_steps)
    epoch = [None]*(num_steps)

    #Train Model
    for step in xrange(num_steps):
        #Predict based on current weights
        z = np.dot(features, weights)
        predictions = sigmoid(z)    

        # Update weights with gradient
        output_error_signal = target - predictions
        gradient = np.dot(features.T, output_error_signal)
        weights += learning_rate * gradient
        
        # Print log-likelihood every so often
        loss[i] = log_likelihood(features, target, weights)
        epoch[i] = i;
        i+=1
            
    #Plot Loss w.r.t. iteration
    global plt
    plt.subplot(211)        
    plt.plot(epoch, loss)
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.title('Logistic Regression using Gradient Descent')
    return weights
            
def stocastic_logistic_regression(features, target, num_steps=30000, batch_size=10, learning_rate=0.1, add_intercept=False):
    #Preprocess data
    if add_intercept:
        intercept = np.ones((features.shape[0], 1))
        features = np.hstack((intercept, features))
    weights = np.zeros(features.shape[1])
    
    #Initilize variables
    i=0
    data_size=len(features[:,0])
    steps_per_epoch = data_size/batch_size
    no_of_epoch = num_steps/steps_per_epoch
    print no_of_epoch
    loss = [None]*(no_of_epoch)
    epoch = [None]*(no_of_epoch)

    #Train Model
    index=0;
    for step in xrange(num_steps):
        
        x = features[index:index+batch_size]
        y = target[index:index+batch_size]
        index = index+batch_size
        
        #Predict based on current weights
        z = np.dot(x, weights)
        predictions = sigmoid(z)    

        # Update weights with gradient
        output_error_signal = y - predictions
        gradient = np.dot(x.T, output_error_signal)
        weights += learning_rate * gradient
        
        # Print log-likelihood after each epoch (Obtained by len(features)/batch_size)
        if step % data_size/batch_size == 0:
            loss[i] = log_likelihood(features, target, weights)
            epoch[i] = i;
            i+=1
            index=0
            
    #Plot Loss w.r.t. iteration
    global plt
    plt.subplot(212)
    plt.plot(epoch, loss)
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.title('Logistic Regression using Stocastic Gradient Descent')
    plt.show()
    return weights



#Define figure for plot
global plt
plt.figure(1)

#Train using Gradient Descent
print "Training using Gradient Descent"
weights = logistic_regression(x, y, num_steps = 300, learning_rate = 0.1, add_intercept=True)


#Train using Stocastic Gradient Descent
print "Training using Stocastic Gradient Descent"
weights = stocastic_logistic_regression(x, y, num_steps = 2000, batch_size=30, learning_rate = 0.1, add_intercept=True)   





