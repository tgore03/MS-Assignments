import csv
import numpy as np
import random
from sklearn import neighbors
import sys
import matplotlib.pyplot as plt

#Variable definition
CLASS1 = 59
CLASS2 = 71
CLASS3 = 48
totalrecords = 178
train_rows = (CLASS1-5)+(CLASS2-5)+(CLASS3-5)
train_cols = 13

global train_data, train_label, test_data, test_label, label, data
global knn, wrong_class, neighbors, error

def main():
    #readcsv data
    data = np.genfromtxt('wine.data.csv', delimiter=',')
    label = data[:, 0]
    data = data[:,1:]

    #train data
    global wrong_class, neighbors, error
    error=[0]*10
    splitdata(data, label)
    
    
    for i in range(1,10):
        train_model(i)

        #predict data label
        iter_no = 100
        wrong_class=0
        predict_label(i, iter_no)
        error_prob(i, 15, iter_no)
    #plot_error()

def normalize(dataset=[]):
    mean=[]
    std=[]
    mean = np.mean(dataset,0)
    std = np.std(dataset, axis=0)

    for i in range(len(train_data)):
        for j in range(13):
            dataset[i][j] = float(dataset[i][j]-mean[j])/std[j]

    return dataset
            
    
    
    

def plot_error():
    plt.plot(error[:])
    plt.axis([1, 9, 0, 100])
    plt.show()
    
def error_prob(k, total_data, iter_no):
    global wrong_class, error
    error[k] = (wrong_class / (total_data*iter_no)) * 100.0 
    print "Error Percentage for k =",k,":", error[k]
    

def predict_label(k, iter_no):
    global test_data, test_label
    global knn, wrong_class

    
    for i in range(iter_no):
        result = KNN(k)
        #result = knn.predict(test_data)

        for i in range(0, len(result)):
            if(result[i] != test_label[i]):
                wrong_class+=1.0
    
def train_model(k):
    global train_data, train_label
    global knn

    #Train Model
    knn = neighbors.KNeighborsClassifier(n_neighbors = k, algorithm='kd_tree')
    knn.fit(train_data, train_label)
    
    

def splitdata(data, label):
    global train_data, train_label, test_data, test_label
    train_data = data
    train_label = label
    
    test_data = np.empty([15, train_cols])
    test_label = np.empty([15, 1])
    
    no1 = random.sample(range(0, 59-1), 5)
    no2 = random.sample(range(59, 130-1), 5)
    no3 = random.sample(range(130, totalrecords-1), 5)
    no4 = no1 + no2 + no3
    train_data = np.delete(train_data, (no4), axis=0)
    train_label = np.delete(train_label, (no4), axis=0)

    i=0
    for no in no4:
        test_data[i] = data[no]
        test_label[i] = label[no]
        i+=1

    print train_data
    train_data = normalize(train_data)
    print train_data
        


def calc_EHdist(train_row, test_row):
    dist=0.0
    for i in range(0, len(train_row)):
        dist += pow((train_row[i]-test_row[i]),2)
    #print dist
    return dist

def kneighbors(min_val, dist, row_index, k):
    
    largest=0.0
    #print min_val
    index=0
    for i in range(0,k):
        if min_val[i][0] > largest:
            largest = min_val[i][0]
            index = i
    if min_val[index][0] > dist:
        min_val[index][0] = dist
        min_val[index][1] = row_index
    return min_val

def KNN(k):
    #compute manhattan distance between test data and training data
    global test_data, train_data
    result = []
    #For each test record
    for test_row in test_data:
        #argmin of manhattan distance
        min_val=[]
        for i in range(0,k):
            if(i==0):
                min_val=[[sys.float_info.max,0]]
                continue
            
            min_val.append([sys.float_info.max, 0])
            
        row_index=0
        for train_row in train_data:
            dist = calc_EHdist(train_row, test_row)
            min_val = kneighbors(min_val, dist, row_index,k)
            row_index+=1
            
        #return max label
        a, b, c = 0,0,0

        for minv in min_val:
            label = train_label[minv[1]]
            if label == 1:
                a+=1
            elif label == 2:
                b+=1
            else:
                c+=1
        if (a>=b & a>=c):
            result.append(1)
        elif (b>a & b>=c):
            result.append(2)
        else:
            result.append(3)

    return result
main()
