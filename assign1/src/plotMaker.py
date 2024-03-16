import matplotlib.pyplot as plt
import csv
from matplotlib.ticker import FuncFormatter

def simpleGraphs():
    sizeCpp = []
    timeCpp = []
    l1Cpp = []
    l2Cpp = []
    
    sizeJava = []
    timeJava = []

    with open('data/Simple.txt', newline='') as cppfile:
        csv_reader = csv.reader(cppfile)

        for row in csv_reader:
            sizeCpp.append(int(row[0]))
            timeCpp.append(float(row[2]))
            l1Cpp.append(float(row[3]))
            l2Cpp.append(float(row[4]))


    with open('data/javaSimple.txt', newline='') as javaFile:
        csv_reader = csv.reader(javaFile)
        for row in csv_reader:
            sizeJava.append(int(row[0]))
            timeJava.append(float(row[2]))

    # # Comparison between java and cpp 
    # plt.plot(sizeCpp, timeCpp, marker='o', label='C++ Time')
    # plt.plot(sizeJava, timeJava, marker='o', label='Java Time')

    # # Set axis limits based on data points
    # plt.axis((min(min(sizeCpp), min(sizeJava)) - 100, max(max(sizeCpp), max(sizeJava)),0, max(max(timeCpp), max(timeJava))))

    # # Add labels and title
    # plt.xlabel('Size')
    # plt.ylabel('Time (seconds)')
    # plt.legend()
    # plt.title('Comparison between execution time C++ and Java Simple Multiplication')
    # plt.show()

    plt.plot(sizeCpp, l1Cpp, marker='o',linestyle='-', color='blue', label='L1 Cache Misses')
    plt.plot(sizeCpp, l2Cpp, marker='o', linestyle='--', color='red', label='L2 Cache Misses')

    plt.xlabel('Size Matrix')
    plt.ylabel('Cache Misses')

    formatter = FuncFormatter(lambda x, _: "{:.0e}".format(x))
    plt.gca().yaxis.set_major_formatter(formatter)

    plt.title('L1 and L2 Cache Misses Simple Multiplication')
    plt.legend()
    plt.grid(True) 

    plt.show()


def lineGraphs():
    sizeCpp = []
    timeCpp = []
    l1Cpp = []
    l2Cpp = []
    
    sizeJava = []
    timeJava = []

    with open('data/Line.txt', newline='') as cppfile:
        csv_reader = csv.reader(cppfile)

        for row in csv_reader:
            sizeCpp.append(int(row[0]))
            timeCpp.append(float(row[2]))
            l1Cpp.append(float(row[3]))
            l2Cpp.append(float(row[4]))


    with open('data/javaLine.txt', newline='') as javaFile:
        csv_reader = csv.reader(javaFile)
        for row in csv_reader:
            sizeJava.append(int(row[0]))
            timeJava.append(float(row[2]))


    # plt.plot(sizeCpp, l1Cpp, marker='o',linestyle='-', color='blue', label='L1 Cache Misses')
    # plt.plot(sizeCpp, l1Cpp, marker='o' ,linestyle='--', color='red', label='L2 Cache Misses')

    # plt.xlabel('Size Matrix')
    # plt.ylabel('Cache Misses')

    # formatter = FuncFormatter(lambda x, _: "{:.0e}".format(x))
    # plt.gca().yaxis.set_major_formatter(formatter)

    # plt.title('L1 and L2 Cache Misses Line Multiplication')
    # plt.legend()
    # plt.grid(True) 

    # plt.show()

    plt.plot(sizeCpp, timeCpp, marker='o',linestyle='-', color='blue', label='Time C++')
    plt.plot(sizeJava, timeJava, marker='o' ,linestyle='--', color='red', label='L2 Cache Misses')

    plt.xlabel('Size Matrix')
    plt.ylabel('Time (Seconds)')

    plt.title('Comparison between execution time C++ and Java Line Multiplication')
    plt.legend()
    plt.grid(True) 

    plt.show()

def blockGraphs():
    size = []
    block = []
    time = []
    l1 = []
    l2 = []
    
    block_sizes = {128: [], 256: [], 512: []} 


    with open('data/Block.txt', newline='') as cppfile:
        csv_reader = csv.reader(cppfile)
        next(csv_reader)  # Skip the header row
        for row in csv_reader:
            size.append(int(row[0]))
            block_size = int(row[2])
            block.append(block_size)            
            time.append(float(row[3]))
            l1.append(float(row[4]))
            l2.append(float(row[5]))
            
        block_sizes[block_size].append((int(row[0]), float(row[3]), float(row[4]), float(row[5])))

    print(block_sizes)

    plt.xlabel('Time (Seconds)')
    plt.ylabel('Cache Misses')

    # formatter = FuncFormatter(lambda x, _: "{:.0e}".format(x))
    # plt.gca().yaxis.set_major_formatter(formatter)

    plt.title('Execution time for different  and block size ')

    # Plot time data for each block size
    for blk_size, data in block_sizes.items():
        size_data = [entry[0] for entry in data]
        time_data = [entry[1] for entry in data]
        
        plt.plot(size_data, time_data, marker='o', label=f'Block Size: {blk_size}')

    plt.legend()
    plt.grid(True) 

    plt.show()


blockGraphs()