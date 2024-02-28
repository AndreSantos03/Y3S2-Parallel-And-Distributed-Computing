all: cpp java

cpp: matrixProduct.cpp
	g++ -O2 $^ -o fileout -lpapi
	./fileout

java: matrixProduct.java
	javac matrixProduct.java
	java matrixProduct

clean:
	rm -f fileout
	rm -f *.class

	
