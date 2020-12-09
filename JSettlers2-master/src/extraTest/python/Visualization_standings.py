import matplotlib
import matplotlib.pyplot as plt
import numpy as np


epsilon = []
episode = []
accuracy = []
loss = []
reward = []
placement = []
with open('epsilon.txt') as f:
    for line1 in f:
        epsilon.append(line1)
with open('episode.txt') as f:
    for line2 in f:
        episode.append(line2)
with open('accuracy.txt') as f:
    for line3 in f:
        accuracy.append(line3)
with open('loss.txt') as f:
    for line4 in f:
        loss.append(line4)
with open('reward.txt') as f:
    for line5 in f:
        reward.append(line5)

with open('placement.txt') as f:
    for line6 in f:
        placement.append(line6)


epsilon = [i for i in line1.split(',')]
epsilon = epsilon[:-1]
epsilon = [float(i) for i in epsilon]

episode = [i for i in line2.split(',')]
episode = episode[:-1]
episode = [float(i) for i in episode]

accuracy = [i for i in line3.split(',')]
accuracy = accuracy[:-1]
accuracy = [float(i) for i in accuracy]

loss = [i for i in line4.split(',')]
loss = loss[:-1]
loss = [float(i) for i in loss]

reward = [i for i in line5.split(',')]
reward = reward[:-1]
reward = [float(i) for i in reward]

print(episode)
ep = episode
es = epsilon
ac = accuracy
ls = loss
rw = reward
fig, ax = plt.subplots()
fig1, ax1 = plt.subplots()
fig2, ax2 = plt.subplots()
fig3, ax3 = plt.subplots()

ax.plot(ep, es)
ax1.plot(ep, ac)
ax2.plot(ep, ls)
ax3.plot(ep, rw)


ax.set(xlabel='Episode', ylabel='Epsilon',title='Episode - Epsilon')
ax1.set(xlabel='Episode', ylabel='Accuracy',title='Episode - Accuracy')
ax2.set(xlabel='Episode', ylabel='Loss',title='Episode - Loss')
ax3.set(xlabel='Episode', ylabel='Reward',title='Episode - Reward')
ax.grid()

fig.savefig("test.png")
plt.show()







