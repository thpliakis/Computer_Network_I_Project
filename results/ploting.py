import matplotlib.pyplot as plt
import numpy as np


with open('echo_package_time_results') as my_file:
    testsite_array = my_file.readlines()

int_list = [int(i) for i in testsite_array]


plt.plot(int_list)
plt.xlabel('packages')
plt.ylabel('time in miliseconds')
plt.title("G1")
plt.savefig('G1')   #Echo packages

plt.close()
#plt.hist(x,bins=10)


with open('nackResults.txt') as file:
    result = [[int(x) for x in line.split()] for line in file]

result = np.array(result)
timeForArqPackage = result[:,0]
plt.plot(timeForArqPackage)
plt.xlabel('packages')
plt.ylabel('time in miliseconds')
plt.title("G2")
plt.savefig('G2')  # Arq Packages

plt.close()

numOfrepeats = result[:,1]
maximum = max(numOfrepeats)
minimum = min(numOfrepeats)
repetitions = [i for i in range(minimum,maximum +1)]
numOfeachrepeats = np.zeros((len(repetitions)))
for i in range(1,len(numOfeachrepeats)+1):
    numOfeachrepeats[i-1] = np.count_nonzero(numOfrepeats == i)

plt.bar(repetitions,numOfeachrepeats)
plt.xlabel('packages')
plt.ylabel('time in miliseconds')
plt.title("G3")
plt.savefig('G3') # reapeted packages

plt.close()
