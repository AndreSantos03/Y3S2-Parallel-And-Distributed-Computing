all: cpp java

cpp: 
	g++ -O2 matrixProduct.cpp -o fileout -lpapi -fopenmp

java: 
	javac matrixProduct.java
	java matrixProduct


clean:
	rm -f fileout
	rm -f *.class

	
