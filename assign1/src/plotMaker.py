import matplotlib.pyplot as plt
import csv


def simpleGraphs():

    size = []

    l1Cpp = []
    l2Cpp = []
    timeCpp = []

    timeJava = []

    with open('data/Simple.txt', newline='') as cppfile:



        csv_reader = csv.reader(cppfile)


        for row in csv_reader:
            size.append(row[0])
            timeCpp.append(row[2])
            l1Cpp.append(row[3])
            l2Cpp.append(row[4])

    with open('data/javaSimple.txt',newline='') as javaFile:
        csv_reader = csv.reader(javaFile)


        for row in csv_reader:
            timeJava.append(row[2])

    print(size)
    print(timeCpp)
    print(timeJava)


    plt.plot(size, timeCpp, marker = 'o',label='C++ Time')
    plt.plot(size, timeJava, marker = 'o',label='Java Time')

    # Add labels and title
    plt.xlabel('Size')
    plt.ylabel('Time (seconds)')
    plt.title('Comparison between execution time C++ and Java Simple Multiplication')

    plt.legend()

    plt.show()


simpleGraphs()

