all: cpp java

cpp: matrixProduct.cpp
	g++ -O2 $^ -o fileout -lpapi

java: matrixProduct.java
	javac matrixProduct.java

clean:
	rm -f fileout
	rm -f *.class

	
