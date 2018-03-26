import numpy as np
import matplotlib.pyplot as plt
import itertools
from scipy.spatial import distance
from time import time
from sklearn.cluster import KMeans
from PIL import Image

def quantization(color_set, i, j):
    min_dst = np.inf
    min_lst = []
    for lst in color_set:
        dst = distance.euclidean(np.array(lst),x[i,j])
        if dst < min_dst:
            min_dst = dst
            min_lst = lst
    return np.array(min_lst)

def cubic_quantization(cset):
    #generate color representative set
    color_set =  list(itertools.product(cset, repeat=3))
    
    x_quantized = np.empty_like(x)
    x_shape = x.shape
    for i in range(x_shape[0]):
        for j in range(x_shape[1]):
            x_quantized[i,j] = quantization(color_set, i, j)
    return x_quantized

#K-Means
def kmeans_quantization(k):
    #Train K-Means and predict clusters
    kmeans=KMeans(n_clusters=k)
    clusters = kmeans.fit_predict(nx)

    #Reconstruct image
    x_centroid = np.empty_like(x)
    idx = 0
    for i in range(x.shape[0]):
        for j in range(x.shape[1]):
            x_centroid[i,j] = kmeans.cluster_centers_[clusters[idx]]
            idx+=1
    return x_centroid

def plot_images(k, x_cubic, x_kmeans):
    #Plot Kmeans vs cubic quantized image
    plt.figure(k)
    plt.subplot(1, 2, 1)
    plt.imshow(x_cubic)
    plt.title("Cubic Quantized Image ("+str(k)+")")
    plt.subplot(1,2,2)
    plt.title("Kmeans Quantized Image ("+str(k)+")")
    plt.imshow(x_kmeans)
    plt.show()


    


#Read image
im = Image.open("palace.png")
im.load()
x = np.array(im)

#Reshape x
nx = np.reshape(x, (x.shape[0]*x.shape[1], x.shape[2]))



#Perform cubic quantization on k=8
t0 = time()
x_cubic = cubic_quantization([0,255])
print "Cubic Quantizatoin: time required =", time() - t0,"secs\n"

#Plot quantized vs original image
plt.figure(1)
plt.subplot(1, 2, 1)
plt.imshow(x)
plt.title("Original Image")
plt.subplot(1,2,2)
plt.title("Quantized image with k=8")
plt.imshow(x_cubic)
plt.show()

#Perform Kmeans quantization on k=8
t0 = time()
x_kmeans = kmeans_quantization(8)
print "Kmeans Quantization: time required =", time() - t0,"secs\n"

#Plot Kmeans vs cubic quantized image
plot_images(8, x_cubic, x_kmeans)



#Perform cubic quantization on k=27
t0 = time()
x_cubic = cubic_quantization([0,127,255])
print "Cubic Quantizatoin: time required =", time() - t0,"secs\n"

#Perform Kmeans quantization on k=27
t0 = time()
x_kmeans = kmeans_quantization(27)
print "Kmeans Quantization: time required =", time() - t0,"secs\n"

#Plot Kmeans vs cubic quantized image
plot_images(27, x_cubic, x_kmeans)



#Perform cubic quantization on k=64
t0 = time()
x_cubic = cubic_quantization([0,85,170,255])
print "Cubic Quantizatoin: time required =", time() - t0,"secs\n"

#Perform Kmeans quantization on k=64
t0 = time()
x_kmeans = kmeans_quantization(64)
print "Kmeans Quantization: time required =", time() - t0,"secs\n"

#Plot Kmeans vs cubic quantized image
plot_images(64, x_cubic, x_kmeans)









