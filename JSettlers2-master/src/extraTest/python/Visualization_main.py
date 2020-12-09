
import numpy as np
import matplotlib.pyplot as plt
placement=[]
with open('placement.txt') as f:
    for line6 in f:
        placement.append(line6)

placement = [i for i in line6.split(',')]
placement = placement[:-1]
placement = [int(i) for i in placement]
placement=placement[-50:]
list1 = placement
my_list = {}
for i in list1:
    my_list[i] = my_list.get(i, 0) + 1
print(my_list)
x_val = list(my_list.keys())
y_val = list(my_list.values())
fig = plt.figure(figsize=(10, 5))
plt.bar(x_val, y_val, color=['red', 'blue', 'green', 'black', 'maroon'], width=0.4)
plt.xticks(range(0, 5))
plt.xlabel("Agent Standings")
plt.ylabel("No. of occurances")
plt.title("DQN Agent Standings(4)")
ax = plt.axes()
ax.yaxis.grid(True)
plt.show()

