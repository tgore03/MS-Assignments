import numpy as np
import matplotlib.pyplot as plt
import math


#Read the Image file
from PIL import Image
im = Image.open("mandrill-grayscale.jpg") #Can be many different formats.
pix = im.load()

l,h= im.size
x = np.empty((l,h), dtype=float)

#Take Average of the pixels and save the matrix
for i in range(l):
    for j in range(h):
        val = im.getpixel((i,j))
        x[j][i] = sum(val)/3

plt.figure(1)
plt.imshow(x, cmap='gray')
plt.title("Original Image in gray scale")
plt.show()
#Calculate the SVD of x
u,s,v = np.linalg.svd(x)

#Print singular values
s_index = np.empty(s.size, dtype=int)
for i in range(s.size):
    s_index[i] = i
plt.scatter(s_index, s)
plt.xlabel("Index")
plt.ylabel("Sigma Value")
plt.title("Singular Matrix")
plt.show()

def reconstruct(k):
    uk = u[:,:k]
    sk = np.diag(s[:k])
    vk = v[:k,:]
    no_stored = uk.size + k + vk.size
    #print "Numbers stored for k =",k,":", no_stored
    compression_ratio = float(no_stored)/(u.size+v.size+s.size)
    #print "Compression Ratio =", compression_ratio*100,"%\n"

    #Reconstruct image
    t = np.dot(uk,sk)
    return np.dot(t,vk), compression_ratio
    
    
#Reconstruct x
plt.figure(2)
plt.tight_layout()
print "k     Compression Ratio"

plt.subplot(2, 3, 1)
plt.title("k=10")
nx, ratio = reconstruct(10)
plt.imshow(nx, cmap='gray')
print "10   ", ratio

plt.subplot(2, 3, 2)
plt.title("k=20")
nx, ratio = reconstruct(20)
plt.imshow(nx, cmap='gray')
print "20   ", ratio

plt.subplot(2, 3, 3)
plt.title("k=30")
nx, ratio = reconstruct(30)
plt.imshow(nx, cmap='gray')
print "30   ", ratio

plt.subplot(2, 3, 4)
plt.title("k=40")
nx, ratio = reconstruct(40)
plt.imshow(nx, cmap='gray')
print "40   ", ratio

plt.subplot(2, 3, 5)
plt.title("k=50")
nx, ratio = reconstruct(50)
plt.imshow(nx, cmap='gray')
print "50   ", ratio

plt.subplot(2, 3, 6)
plt.title("k=60")
nx, ratio = reconstruct(60)
plt.imshow(nx, cmap='gray')
print "60   ", ratio
plt.show()



