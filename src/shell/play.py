#coding=utf-8
import numpy as np
import matplotlib.pyplot as plt
import string

input = open('/home/huirong/graph/netflow/out/part-00000')
data = input.readlines()
times = []
counts = []
index = 0
for elem in data:
    split = elem.split(",")
    time = split[0]
    count = string.atof(split[1])
    index = index + 1
    plt.scatter(index, count)
    #times.append(index)
    #counts.append(count)
plt.show()
