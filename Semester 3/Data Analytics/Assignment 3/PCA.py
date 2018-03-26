import numpy as np
import math
import matplotlib.pyplot as plt
from sklearn.decomposition import PCA
from sklearn.decomposition import TruncatedSVD


f = open("places.txt", "r")
no_features = 9
no_records = 329
features = np.empty([no_records, no_features])
target = ["" for x in range(no_records)]

#Read the labels
f.readline()

#Read the file
lineno=-1
colno=-1
i=0
for line in f:
    lineno+=1
    for word in line.split():
        #Store label column
        if colno == -1:
            colno+=1
            target[lineno]=str(word)
            continue
        colno+=1
        #Skip last 5 columns
        if colno > no_features:
            break;
        #Store word in matrix
        features[lineno,colno-1] = word
    colno=-1
    i+=1
f.close()


print "PCA using Log10"
#Taking log of matrix
x = np.log10(features)

#Taking mean of features and subtracting it from the features matrix
means = np.mean(x, axis=0)
std = np.std(x, axis=0)

for i in range(no_records):
    x[i]=(x[i] - means)

#SVD
u,s,v = np.linalg.svd(x, full_matrices=True)
d = np.diag(s[0:2])
scores = np.dot(u[:,0:2],d)
print "Principle Directions"
print v[0]
print v[1]

svd = TruncatedSVD(n_components=2)
svd.fit(x.T)
print "Variance of indivial features"
print svd.explained_variance_
print "Total Variance of Principle Components =", svd.explained_variance_ratio_.sum()

#Plot the 2 principle components
components = svd.components_.T

plt.subplot(211)
plt.scatter(components[:,0], components[:,1])
for row in range(no_records):
    plt.annotate(str(row+1), (components[row,0], components[row,1]))
plt.xlabel("PC 1")
plt.ylabel("PC 2")
plt.title("Scatter Plot with Log10")


print "\n\n PCA using z-score"
#using z-scores normalize data
means = np.mean(features, axis=0)
std = np.std(features, axis=0)

for i in range(no_records):
    for j in range(no_features):
        x[i][j]=(features[i][j] - means[j])/std[j]

u,s,v = np.linalg.svd(x, full_matrices=True)
print "Principle Directions:"
print v[0]
print v[1]

svd = TruncatedSVD(n_components=2)
svd.fit(x.T)
components = svd.components_.T
print "Variance of each features: \n",svd.explained_variance_
print "Total variance of Principle components =",svd.explained_variance_ratio_.sum()

plt.subplot(212)
plt.scatter(components[:,0], components[:,1])
for row in range(no_records):
    plt.annotate(str(row+1), (components[row,0], components[row,1]))
plt.xlabel("PC 1")
plt.ylabel("PC 2")
plt.title("Scatter Plot with z-score")
plt.show()




