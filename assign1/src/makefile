all: cpp java

cpp: 
	g++ -O2 matrixProduct.cpp -o fileout -lpapi
	./fileout

java: 
	javac matrixProduct.java
	java matrixProduct


clean:
	rm -f fileout
	rm -f *.class
	> dataOutJava.txt
	> dataOutCPP.txt
	
