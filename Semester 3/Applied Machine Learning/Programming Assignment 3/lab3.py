from keras.models import Sequential
from keras.layers import Dense, Activation, Flatten
from keras.utils import np_utils
from keras.callbacks import EarlyStopping
from keras.layers.convolutional import Conv2D
from keras import optimizers
import time
import numpy as np
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix
from tensorflow import set_random_seed



#Read file and preprocess it
def read_file(train_file, test_file):
    #Read data and seperate labels
    train = np.loadtxt(train_file, delimiter=",")
    test = np.loadtxt(test_file, delimiter=",")
    X_train = train[:, :64].astype('float32')/16
    y_train = train[:,64]
    X_test = test[:, :64].astype('float32')/16
    y_test = test[:,64]

    #Normalize the input
    #X_train = X_train/16 #Since value are between 0 & 16
    #X_test = X_test/16 

    # Convert y to 1-of-c output format
    #y_train = np_utils.to_categorical(y_train)
    #y_test = np_utils.to_categorical(y_test)

    return X_train, y_train, X_test, y_test


def print_statistics(model, x_data, y_data):
    predictions = model.predict(x_data)
    fin_prediction = []
    for row in predictions:
        fin_prediction.append(np.argmax(row))
    matrix = confusion_matrix(y_data, fin_prediction)
    sum = 0
    print("\nClass Accuracies:")
    for i in range(10):
        sum += matrix[i][i]
        print("Class ", i, ": ", round(matrix[i][i]/np.sum(matrix[i]), 4))
    print("\nOverall Accuracy: ", round(sum/np.sum(matrix), 4))
    print("\nConfusion Matrix:\n", matrix)        


def nn_model(X_train, y_train, X_test, y_test, no_layers, no_units, activation, loss, learning_rate, momentum_rate):
    start_time=time.time()
    set_random_seed(2)
    np.random.seed(2)
    epochs=200
    batch_size=100
    
    #Define Model
    model = Sequential()
    model.add(Dense(no_units, input_dim=64, activation=activation))
    for i in range(no_layers-1):
        model.add(Dense(no_units, activation=activation))
    model.add(Dense(10, activation="softmax"))
    #model.summary()

    # Stochastic Gradient Descent for Optimization
    sgd = optimizers.SGD(lr=learning_rate, decay=1e-6, momentum=momentum_rate, nesterov=True)
    
    #compile model
    model.compile(loss=loss, optimizer=sgd, metrics=['accuracy'])

    #Early Stopping
    earlystop = EarlyStopping(monitor='val_acc', min_delta=0.0001, patience=5, verbose=0, mode='auto')
    callbacks_list = [earlystop]

    #Fit the model
    labels = np_utils.to_categorical(y_train)
    model.fit(X_train, labels, epochs=epochs, batch_size=batch_size, verbose=0, validation_split=0.2, callbacks=callbacks_list)
    train_time = time.time() - start_time

    #Evaluate the models
    #y_test = np_utils.to_categorical(y_test)
    #metrics=model.evaluate(X_test,y_test, batch_size=batch_size, verbose=0)

    #Print Results
    print()
    print("Feed forward neural network")
    print_statistics(model, X_test, y_test)
    print("Activation:          ", activation)
    print("Loss function:       ", loss)
    print("Training Time:       ", train_time,"sec")
    print("No. of Hidden Layers:", no_layers)
    print("No. of Neurons:      ", no_units)
    print("Learning Rate:       ", learning_rate)
    print("Momentum Rate:       ", momentum_rate)


def cnn_model(X_train, y_train, X_test, y_test, filters, no_layers, no_units, activation, loss, learning_rate, momentum_rate):
    start_time=time.time()
    set_random_seed(2)
    np.random.seed(2)
    epochs = 200
    batch_size = 100

    #Define Model
    model = Sequential()
    model.add(Conv2D(filters, (1,1), input_shape=(1, 1, 64), activation=activation))
    model.add(Flatten())
    for i in range(no_layers-1):
        model.add(Dense(no_units, activation=activation))
    model.add(Dense(10, activation='softmax'))

    sgd = optimizers.SGD(lr=learning_rate, decay=1e-6, momentum=momentum_rate, nesterov=True)
    model.compile(loss=loss, optimizer=sgd, metrics=['accuracy'])

    #Early Stopping
    earlystop = EarlyStopping(monitor='val_acc', min_delta=0.0001, patience=5, verbose=0, mode='auto')
    callbacks_list = [earlystop]

    cnn_train = X_train.reshape(X_train.shape[0], 1, 1, 64)
    cnn_test = X_test.reshape(X_test.shape[0], 1, 1, 64)
    labels = np_utils.to_categorical(y_train)
    model.fit(cnn_train, labels, epochs=epochs, batch_size=batch_size, verbose=0, validation_split=0.2, callbacks=callbacks_list)
    train_time = time.time() - start_time

    

    #Print Results
    print("\n\n")
    print("Convoluted neural network")
    print_statistics(model, cnn_test, y_test)
    print("Activation:          ", activation)
    print("Loss function:       ", loss)
    print("Training Time:       ", train_time,"sec")
    print("No. of Hidden Layers:", no_layers)
    print("No. of Neurons:      ", no_units)
    print("Learning Rate:       ", learning_rate)
    print("Momentum Rate:       ", momentum_rate)


    


def main():
    #Read file
    X_train, y_train, X_test, y_test = read_file('optdigits.tra', 'optdigits.tes')

    no_layers = 2
    no_units = 37
    activation = 'relu'
    loss_func = 'categorical_crossentropy'
    learning_rate = 0.05
    momentum_rate = 0.9
    filters = 1024

    nn_model(X_train, y_train, X_test, y_test, no_layers, no_units, activation, loss_func, learning_rate, momentum_rate)
    cnn_model(X_train, y_train, X_test, y_test,filters, no_layers, no_units, activation, loss_func, learning_rate, momentum_rate)

main()

