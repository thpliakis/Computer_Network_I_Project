import matplotlib.pyplot as plt
import numpy as np


with open('echo_package_time_results') as my_file:
    testsite_array = my_file.readlines()

int_list = [int(i) for i in testsite_array]


plt.plot(int_list)
plt.xlabel('Packages')
plt.ylabel('Time in miliseconds')
plt.title("G1  Number of packages: " + str(len(int_list)))
plt.savefig('G1')   #Echo packages

plt.close()
#plt.hist(x,bins=10)


with open('nackResults.txt') as file:
    result = [[int(x) for x in line.split()] for line in file]

result = np.array(result)
timeForArqPackage = result[:,0]
plt.plot(timeForArqPackage)
plt.xlabel('Packages')
plt.ylabel('Time in miliseconds')
plt.title("G2  Number of packages: " + str(len(result)))
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
plt.xlabel('Attempts')
plt.ylabel('Number of packages')
plt.title("G3  Number of packages: " + str(len(result)))
plt.savefig('G3') # reapeted packages

plt.close()
numOfeachrepeats = numOfeachrepeats[:] / numOfrepeats.shape[0]
plt.bar(repetitions,numOfeachrepeats)
plt.xlabel('Attempts')
plt.ylabel('Number of packages in %')
plt.title("G4  Number of packages: "  + str(len(result)))
plt.savefig('G4') # reapeted packages

plt.close()

print(numOfeachrepeats)
