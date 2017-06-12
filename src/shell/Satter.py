import numpy as np
import matplotlib.pyplot as plt

n = 1024
X = [1, 2, 3]
Y = [2, 3, 2]

plt.scatter(X,Y)

plt.xlim(0, 4), plt.xticks([])
plt.ylim(0, 4), plt.yticks([])
plt.show()