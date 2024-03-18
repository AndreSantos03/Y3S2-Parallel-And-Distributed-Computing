import matplotlib.pyplot as plt
import csv
from matplotlib.ticker import FuncFormatter

numThreads = 8

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
    plt.plot(sizeJava, timeJava, marker='o' ,linestyle='--', color='red', label='Time Java')

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
        for row in csv_reader:
            size.append(int(row[0]))
            block_size = int(row[2])
            block.append(block_size)            
            time.append(float(row[3]))
            l1.append(float(row[4]))
            l2.append(float(row[5]))
            block_sizes[block_size].append((int(row[0]), float(row[3]), float(row[4]), float(row[5])))


    # plt.xlabel('Matrix Size')
    # plt.ylabel('Time (Seconds)')

    # # formatter = FuncFormatter(lambda x, _: "{:.0e}".format(x))
    # # plt.gca().yaxis.set_major_formatter(formatter)

    # plt.title('Execution time for different  and block size ')

    # # Plot time data for each block size
    # for blk_size, data in block_sizes.items():
    #     size_data = [entry[0] for entry in data]
    #     time_data = [entry[1] for entry in data]
        
    #     plt.plot(size_data, time_data, marker='o', label=f'Block Size: {blk_size}')

    # plt.legend()
    # plt.grid(True) 

    # plt.show()

    plt.xlabel('Matrix Size')
    plt.ylabel('Cache Misses')

    plt.title('Cache Misses for different block sizes')
    
    # Plot cache miss data for each block size
    for blk_size, data in block_sizes.items():
        size_data = [entry[0] for entry in data]
        l1_data = [entry[2] for entry in data]
        l2_data = [entry[3] for entry in data]
        
        plt.plot(size_data, l1_data, marker='o', label=f'L1 Cache Misses (Block Size: {blk_size})')
        plt.plot(size_data, l2_data, marker='o', label=f'L2 Cache Misses (Block Size: {blk_size})')

    formatter = FuncFormatter(lambda x, _: "{:.0e}".format(x))
    plt.gca().yaxis.set_major_formatter(formatter)
    plt.legend()
    plt.grid(True) 

    plt.show()

def multiThreadingFirst():
    size = []
    time = []
    l1 = []
    l2 = []

    with open('data/FirstThreading.txt', newline='') as txtFile:
        csv_reader = csv.reader(txtFile)
        for row in csv_reader:
            size.append(int(row[0]))
            time.append(float(row[2]))
            l1.append(float(row[3]))
            l2.append(float(row[4]))
    

    # plt.plot(size, l1, marker='o',linestyle='-', color='blue', label='L1 Cache Misses')
    # plt.plot(size, l2, marker='o' ,linestyle='--', color='red', label='L2 Cache Misses')

    # plt.xlabel('Size Matrix')
    # plt.ylabel('Cache Misses')

    # formatter = FuncFormatter(lambda x, _: "{:.0e}".format(x))
    # plt.gca().yaxis.set_major_formatter(formatter)

    # plt.title('L1 and L2 Cache Misses First Multi Threading Type Line Multiplication')
    # plt.legend()
    # plt.grid(True) 

    # plt.show()



    # plt.plot(size, time, marker='o',linestyle='-', color='blue', label='Execution Time')

    # plt.xlabel('Size Matrix')
    # plt.ylabel('Time (seconds)')


    # plt.title('Execution times First Multi Threading Type Line Multiplication')
    # plt.legend()
    # plt.grid(True) 

    # plt.show()




    # sizeLine = []
    # timeLine = []
      
    # with open('data/Line.txt', newline='') as txtFile:
    #     csv_reader = csv.reader(txtFile)
    #     for row in csv_reader:
    #         sizeLine.append(int(row[0]))
    #         timeLine.append(float(row[2]))
    
    
    # speedup = [timeLine[i] / time[i] for i in range(len(sizeLine))]
    # efficiency = [speedup[i] / numThreads for i in range(len(sizeLine))]

    # print(efficiency)

    # plt.plot(sizeLine, speedup, marker='o',linestyle='-', color='blue', label='Speedup')
    # plt.plot(sizeLine, efficiency, marker='o',linestyle='--', color='red', label='Efficiency')

    # plt.xlabel('Size Matrix')
    # plt.ylabel('Efficiency/ Speedup')


    # plt.title('Efficiency and Speedup First Multi Threading Type Line Multiplication')
    # plt.legend()
    # plt.grid(True) 

    # plt.show()
            


    # print(size)
    # print(time)

    mFlops = [(2 * (pow(size[i], 3))) / (time[i] * 1000000) for i in range(len(size))]
    plt.plot(size, mFlops, marker='o',linestyle='-', color='blue', label='MFLOPs')
    plt.xlabel('Size Matrix')
    plt.ylabel('MFLOPs (Millions of Floating-Point Operations Per Second)')


    plt.title('MFLOPs First Multi Threading Type Line Multiplication')
    plt.legend()
    plt.grid(True) 

    plt.show()


def multiThreadingSecond():
    size = []
    time = []
    l1 = []
    l2 = []

    with open('data/SecondThreading.txt', newline='') as txtFile:
        csv_reader = csv.reader(txtFile)
        for row in csv_reader:
            size.append(int(row[0]))
            time.append(float(row[2]))
            l1.append(float(row[3]))
            l2.append(float(row[4]))
    

    # plt.plot(size, l1, marker='o',linestyle='-', color='blue', label='L1 Cache Misses')
    # plt.plot(size, l2, marker='o' ,linestyle='--', color='red', label='L2 Cache Misses')

    # plt.xlabel('Size Matrix')
    # plt.ylabel('Cache Misses')

    # formatter = FuncFormatter(lambda x, _: "{:.0e}".format(x))
    # plt.gca().yaxis.set_major_formatter(formatter)

    # plt.title('L1 and L2 Cache Misses Second Multi Threading Type Line Multiplication')
    # plt.legend()
    # plt.grid(True) 

    # plt.show()



    # plt.plot(size, time, marker='o',linestyle='-', color='blue', label='Execution Time')

    # plt.xlabel('Size Matrix')
    # plt.ylabel('Time (seconds)')


    # plt.title('Execution times Second Multi Threading Type Line Multiplication')
    # plt.legend()
    # plt.grid(True) 

    # plt.show()




    # sizeLine = []
    # timeLine = []
      
    # with open('data/Line.txt', newline='') as txtFile:
    #     csv_reader = csv.reader(txtFile)
    #     for row in csv_reader:
    #         sizeLine.append(int(row[0]))
    #         timeLine.append(float(row[2]))
    
    
    # speedup = [timeLine[i] / time[i] for i in range(len(sizeLine))]
    # efficiency = [speedup[i] / numThreads for i in range(len(sizeLine))]

    # print(efficiency)

    # plt.plot(sizeLine, speedup, marker='o',linestyle='-', color='blue', label='Speedup')
    # plt.plot(sizeLine, efficiency, marker='o',linestyle='--', color='red', label='Efficiency')

    # plt.xlabel('Size Matrix')
    # plt.ylabel('Efficiency/ Speedup')


    # plt.title('Efficiency and Speedup Second Multi Threading Type Line Multiplication')
    # plt.legend()
    # plt.grid(True) 

    # plt.show()
            


    print(size)
    print(time)

    mFlops = [(2 * (pow(size[i], 3))) / (time[i] * 1000000) for i in range(len(size))]
    plt.plot(size, mFlops, marker='o',linestyle='-', color='blue', label='MFLOPs')
    plt.xlabel('Size Matrix')
    plt.ylabel('MFLOPs (Millions of Floating-Point Operations Per Second)')


    plt.title('MFLOPs  Second Multi Threading Type Line Multiplication')
    plt.legend()
    plt.grid(True) 

    plt.show()

multiThreadingFirst()