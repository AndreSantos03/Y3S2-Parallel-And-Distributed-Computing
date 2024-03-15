#!/bin/bash

make cpp


echo -e "Starting with simple matrix multiplication\n"

# Simple Matrix Multiplication
for i in $(seq 600 400 3000)
do
    ./fileout 1 $i "No1Simple.txt" # C++ program
done

echo -e "Done with simple matrix multiplication\n"
echo -e "Starting with line matrix multiplication\n"

# Line Matrix Multiplication
for i in $(seq 600 400 3000)
do
    ./fileout 2 $i "No1Line.txt" # C++ program
done

for i in $(seq 4096 2048 10240)
do
    ./fileout 2 $i "No1Line.txt" # C++ Program
done

echo -e "Done with line matrix multiplication\n"
echo -e "Starting with block matrix multiplication\n"

# Block Matrix Multiplication
for i in $(seq 4096 2048 10240)
do 
    for j in $(seq 128 256 512)
    do
        ./fileout 3 $i $j "No1Block.txt"
    done
done

echo -e "Done with block matrix multiplication\n"

