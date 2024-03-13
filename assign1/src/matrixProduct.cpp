#include <stdio.h>
#include <iostream>
#include <iomanip>
#include <time.h>
#include <cstdlib>
#include <papi.h>
#include <fstream>
#include <sstream>
#include <omp.h>
#include <vector>
#include <limits>

using namespace std;


 
string OnMult(int m_ar, int m_br) 
{
	
	
	char st[100];
	double temp;
	int i, j, k;

	double *pha, *phb, *phc;
	

		
    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

	for(i=0; i<m_ar; i++)
		for(j=0; j<m_ar; j++)
			pha[i*m_ar + j] = (double)1.0;



	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phb[i*m_br + j] = (double)(i+1);



    double Time1 = omp_get_wtime();

	for(i=0; i<m_ar; i++)
	{	for( j=0; j<m_br; j++)
		{	temp = 0;
			for( k=0; k<m_ar; k++)
			{	
				temp += pha[i*m_ar+k] * phb[k*m_br+j];
			}
			phc[i*m_ar+j]=temp;
		}
	}


    double Time2 = omp_get_wtime();
	//string res;
	sprintf(st, "Time: %3.3f seconds\n", Time2 - Time1);
	cout << st;


	// display 10 elements of the result matrix tto verify correctness
	cout << "Result matrix: " << endl;
	for(i=0; i<1; i++)
	{	for(j=0; j<min(10,m_br); j++)
			cout << phc[j] << " ";
	}
	cout << endl;

    free(pha);
    free(phb);
    free(phc);
	
	//cout << res;
	return to_string((double)(Time2 - Time1) );
	
}

// add code here for line x line matriz multiplication
string OnMultLine(int m_ar, int m_br)
{
	
	char st[100];
	double temp;
	int i, j, k;

	double *pha, *phb, *phc;
		
    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

	for(i=0; i<m_ar; i++)
		for(j=0; j<m_ar; j++)
			pha[i*m_ar + j] = (double)1.0;

	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phb[i*m_br + j] = (double)(i+1);
	
	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phc[i*m_br + j] = (double)(0.0);

    double Time1 = omp_get_wtime();

	for(i=0; i<m_ar; i++){	
		for( k=0; k<m_ar; k++){	
			for( j=0; j<m_br; j++){	
				phc[i*m_ar+j] += pha[i*m_ar+k] * phb[k*m_br+j];
			}
		}
	}


    double Time2 = omp_get_wtime();
	string res;  
	res = sprintf(st, "Time: %3.3f seconds\n", (double)(Time2 - Time1));
	cout << st;

	// display 10 elements of the result matrix tto verify correctness
	cout << "Result matrix: " << endl;
	for(i=0; i<1; i++)
	{	for(j=0; j<min(10,m_br); j++)
			cout << phc[j] << " ";
	}
	cout << endl;

    free(pha);
    free(phb);
    free(phc);
    
	return to_string((double)(Time2 - Time1));
}

// add code here for block x block matriz multiplication
string OnMultBlock(int m_ar, int m_br, int bkSize)
{
	
	char st[100];
	double temp;
	int ii, jj, kk, i, j, k;

	double *pha, *phb, *phc;
		
    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

	for(i=0; i<m_ar; i++)
		for(j=0; j<m_ar; j++)
			pha[i*m_ar + j] = (double)1.0;

	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phb[i*m_br + j] = (double)(i+1);
	
	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phc[i*m_br + j] = (double)(0.0);

    double Time1 = omp_get_wtime();

	for(int ii = 0; ii < m_ar; ii+= bkSize){
		for(int kk = 0; kk < m_ar; kk+= bkSize){
			for(int jj = 0; jj < m_br; jj+= bkSize){
				for(int i = ii; i < ii+bkSize; i++){
					for(int k = kk; k < kk+bkSize; k++){
						for(int j = jj; j < jj+bkSize; j++){
							phc[i*m_ar+j] += pha[i*m_ar+k] * phb[k*m_br+j];
						}
					}
				}	
			}
		}
	}
    double Time2 = omp_get_wtime();
	string res;
	res = sprintf(st, "Time: %3.3f seconds\n", (double)(Time2 - Time1) );
	cout << st;
	cout << res;
	// display 10 elements of the result matrix tto verify correctness
	cout << "Result matrix: " << endl;
	for(i=0; i<1; i++)
	{	for(j=0; j<min(10,m_br); j++)
			cout << phc[j] << " ";
	}
	cout << endl;

    free(pha);
    free(phb);
    free(phc);
    return to_string((double)(Time2 - Time1));
}

string OnMultLineParallelOne(int m_ar, int m_br) {
    
    char st[100];
    double temp;
    int i, j, k;

    double *pha, *phb, *phc;

    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

    for(i = 0; i < m_ar; i++)
        for(j = 0; j < m_ar; j++)
            pha[i * m_ar + j] = (double)1.0;

    for(i = 0; i < m_br; i++)
        for(j = 0; j < m_br; j++)
            phb[i * m_br + j] = (double)(i + 1);
    
    for(i = 0; i < m_br; i++)
        for(j = 0; j < m_br; j++)
            phc[i * m_br + j] = (double)(0.0);

    double Time1 = omp_get_wtime();

    #pragma omp parallel for
    for(i = 0; i < m_ar; i++) {
        for(k = 0; k < m_ar; k++) {
            for(j = 0; j < m_br; j++) {
                phc[i * m_ar + j] += pha[i * m_ar + k] * phb[k * m_br + j];
            }
        }
    }

    double Time2 = omp_get_wtime();
    sprintf(st, "Time: %3.3f seconds\n", (double)(Time2 - Time1));
    cout << st;

    // Display 10 elements of the result matrix to verify correctness
    cout << "Result matrix: " << endl;
    for(i = 0; i < 1; i++) {
        for(j = 0; j < min(10, m_br); j++)
            cout << phc[j] << " ";
    }
    cout << endl;

    free(pha);
    free(phb);
    free(phc);

    return to_string((double)(Time2 - Time1));
}

string OnMultLineParallelTwo(int m_ar, int m_br) {
    
    char st[100];
    double temp;
    int i, j, k;

    double *pha, *phb, *phc;

    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
    phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

    for(i = 0; i < m_ar; i++)
        for(j = 0; j < m_ar; j++)
            pha[i * m_ar + j] = (double)1.0;

    for(i = 0; i < m_br; i++)
        for(j = 0; j < m_br; j++)
            phb[i * m_br + j] = (double)(i + 1);
    
    for(i = 0; i < m_br; i++)
        for(j = 0; j < m_br; j++)
            phc[i * m_br + j] = (double)(0.0);

    double Time1 = omp_get_wtime();

	#pragma omp parallel
    for(i = 0; i < m_ar; i++) {    
        for(k = 0; k < m_ar; k++) {    
            #pragma omp for
            for(j = 0; j < m_br; j++) {    
                phc[i * m_ar + j] += pha[i * m_ar + k] * phb[k * m_br + j];
            }
        }
    }

    double Time2 = omp_get_wtime();
    string res;  
    res = sprintf(st, "Time: %3.3f seconds\n", (double)(Time2 - Time1));
    cout << st;

    // Display 10 elements of the result matrix to verify correctness
    cout << "Result matrix: " << endl;
    for(i = 0; i < 1; i++) {
        for(j = 0; j < min(10, m_br); j++)
            cout << phc[j] << " ";
    }
    cout << endl;

    free(pha);
    free(phb);
    free(phc);
    
    return to_string((double)(Time2 - Time1));
}


void handle_error (int retval)
{
  printf("PAPI error %d: %s\n", retval, PAPI_strerror(retval));
  exit(1);
}

void init_papi() {
  int retval = PAPI_library_init(PAPI_VER_CURRENT);
  if (retval != PAPI_VER_CURRENT && retval < 0) {
    printf("PAPI library version mismatch!\n");
    exit(1);
  }
  if (retval < 0) handle_error(retval);

  cout << "PAPI Version Number: MAJOR: " << PAPI_VERSION_MAJOR(retval)
            << " MINOR: " << PAPI_VERSION_MINOR(retval)
            << " REVISION: " << PAPI_VERSION_REVISION(retval) << "\n";
}


int main (int argc, char *argv[])
{

	char c;
	string res;
	int lin, col, blockSize;
	int op;
	
	int EventSet = PAPI_NULL;
  	long long values[2];
  	int ret;
	

	ret = PAPI_library_init( PAPI_VER_CURRENT );
	if ( ret != PAPI_VER_CURRENT )
		cout << "FAIL" << endl;


	ret = PAPI_create_eventset(&EventSet);
		if (ret != PAPI_OK) cout << "ERROR: create eventset" << endl;


	ret = PAPI_add_event(EventSet,PAPI_L1_DCM );
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L1_DCM" << endl;


	ret = PAPI_add_event(EventSet,PAPI_L2_DCM);
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L2_DCM" << endl;

	ret = PAPI_add_event(EventSet, PAPI_FP_OPS);
	if (ret != PAPI_OK) cout << "ERROR: PAPI_FP_OPS" << endl;


	op=1;
	do {
		cout << endl << "1. Multiplication" << endl;
		cout << "2. Line Multiplication" << endl;
		cout << "3. Block Multiplication" << endl;
		cout << "4. Multi Threading Line Multiplication First Implementation" << endl;
		cout << "5. Multi Threading Line Multiplication Second Implementation" << endl;
		cout << "6. Data File: 1) Multiplication" << endl;
		cout << "7. Data File: 2) Line Multiplication" << endl;
		cout << "8. Data File: 3) Bloc Multiplication" << endl;
		cout << "9. Data File: 4) Multi Threading Line Multiplication First Implementation" << endl;
		cout << "10. Data File: 5) Multi Threading Line Multiplication Second Implementation" << endl;		


		cout << "Selection?: ";
		cin >>op;

		if( 0 < op && op <= 5){

			printf("Dimensions: lins=cols ? ");
			cin >> lin;
			col = lin;

			ret = PAPI_start(EventSet);
			if (ret != PAPI_OK) cout << "ERROR: Start PAPI" << endl;

			switch (op){
				case 1: 
					OnMult(lin, col);
					break;
				case 2:
					OnMultLine(lin, col);  
					break;
				case 3:
					cout << "Block Size? ";
					cin >> blockSize;
					OnMultBlock(lin, col, blockSize);  
					break;
				case 4:
					OnMultLineParallelOne(lin,col);
					break;
				case 5: 
					OnMultLineParallelTwo(lin,col);
					break;
			}

			ret = PAPI_stop(EventSet, values);
			cout << "papi ret:" << ret << endl;
			if (ret != PAPI_OK) cout << "ERROR: Stop PAPI" << endl;
			printf("L1 DCM: %lld \n",values[0]);
			printf("L2 DCM: %lld \n",values[1]);

			ret = PAPI_reset( EventSet );
			if ( ret != PAPI_OK )
				cout << "FAIL reset" << endl; 
		}
		else if (op <= 10) {
			ofstream f;
    		cout << "Give a list of Matrix Sizes: (Format: {size1 size2 size3})" << endl;
    		cin.ignore(numeric_limits<streamsize>::max(), '\n');

			string matrixValuesString;
			getline(cin, matrixValuesString); 

			vector<int> matrixSizes;

			stringstream ss(matrixValuesString); 
			int size;
			while (ss >> size) {
				cout << size << endl;
				matrixSizes.push_back(size);
			}

					
			switch (op) {
				case 6: {
					const char* filename = "MultiplicationDataOutput.txt";
					remove(filename); 
					f.open(filename, ios::app); 
					if (!f) {
						cerr << "Failed to open file for writing!" << endl;
						return 1; 
					}

					for (int i = 0; i < matrixSizes.size(); ++i) {
						int size = matrixSizes[i];
						cout << "Processing matrix size: " << size << "x" << size << endl;
						PAPI_start(EventSet);

						string res = OnMult(size, size); 
						PAPI_stop(EventSet, values);

						f <<  size << "," << size << "," << res << "," << values[0] << "," << values[1] << "," << values[2] << endl;
						PAPI_reset(EventSet);
 
						
					}

					f.close(); 
					break;
				}
				case 7: {
					const char* filename = "MultiplicationLineDataOutput.txt";
					remove(filename); 
					f.open(filename, ios::app); 
					if (!f) {
						cerr << "Failed to open file for writing!" << endl;
						return 1; 
					}

					for (int i = 0; i < matrixSizes.size(); ++i) {
						int size = matrixSizes[i];
						cout << "Processing matrix size: " << size << "x" << size << endl;
						PAPI_start(EventSet);
						string res = OnMultLine(size, size); 
						PAPI_stop(EventSet,values);
						f <<  size << "," << size << ","  << res << "," << values[0] << "," << values[1] << "," << values[2] << endl; 
						PAPI_reset(EventSet);
					}

					f.close(); 
					break;
				}
				case 8: {

					cout << "Give a list of Block Sizes: (Format:{size1 size2 size3})" << endl;

					cin.ignore(numeric_limits<streamsize>::max(), '\n');

					string blockValuesString;
					getline(cin,blockValuesString);
					vector<int> blockSizes;

					istringstream iss(blockValuesString); // Use istringstream to process the string
					int size;
					while (iss >> size) {
						blockSizes.push_back(size);
					}


					const char* filename = "MultiplicationBlockDataOutput.txt";
					remove(filename); 
					f.open(filename, ios::app); 
					if (!f) {
						cerr << "Failed to open file for writing!" << endl;
						return 1; 
					}

					for (int i = 0; i < matrixSizes.size(); ++i) {
						for (int j = 0; j < blockSizes.size(); ++j) {
							int matrixSize = matrixSizes[i];
							int blockSize = blockSizes[j];
							cout << "Processing matrix size: " << matrixSize << "x" << matrixSize << endl;
							cout << "Processing block size: " << blockSize << endl;
							PAPI_start(EventSet);
							string res = OnMultBlock(matrixSize, matrixSize, blockSize); 
							PAPI_stop(EventSet,values);
							f <<  matrixSize << "," << matrixSize << "," << blockSize << "," << res << "," << values[0] << "," << values[1] << "," << values[2] << endl;

							PAPI_reset(EventSet);
						}
					}

					f.close(); 
					break;
				}
				case 9:{
					const char* filename = "FirstThreadingDataOutput.txt";
					remove(filename); 
					f.open(filename, ios::app); 
					if (!f) {
						cerr << "Failed to open file for writing!" << endl;
						return 1; 
					}

					for (int i = 0; i < matrixSizes.size(); ++i) {
						int size = matrixSizes[i];
						cout << "Processing matrix size: " << size << "x" << size << endl;
						PAPI_start(EventSet);
						string res = OnMultLineParallelOne(size, size); 
						PAPI_stop(EventSet,values);
						f <<  size << "," << size << "," << res << "," << values[0] << "," << values[1] << "," << values[2] << endl;
						PAPI_reset(EventSet);

					}
					f.close();
					break;
				}
				case 10:{
					const char* filename = "SecondThreadingDataOutput.txt";
					remove(filename); 
					f.open(filename, ios::app); 
					if (!f) {
						cerr << "Failed to open file for writing!" << endl;
						return 1; 
					}

					for (int i = 0; i < matrixSizes.size(); ++i) {
						int size = matrixSizes[i];
						cout << "Processing matrix size: " << size << "x" << size << endl;
						PAPI_start(EventSet);
						string res = OnMultLineParallelTwo(size, size); 
						PAPI_stop(EventSet,values);
						f <<  size << "," << size << "," << res << "," << values[0] << "," << values[1] << "," << values[2] << endl;
						PAPI_reset(EventSet);
					}
					f.close();
					break;
				}
			}
		}
	}
	while(op != 0);



	ret = PAPI_reset(EventSet);
	if (ret != PAPI_OK)
		cout << "FAIL reset" << endl;

	ret = PAPI_remove_event( EventSet, PAPI_L1_DCM );
	if ( ret != PAPI_OK )
		cout << "FAIL remove event" << endl; 

	ret = PAPI_remove_event( EventSet, PAPI_L2_DCM );
	if ( ret != PAPI_OK )
		cout << "FAIL remove event" << endl; 

	ret = PAPI_destroy_eventset( &EventSet );
	if ( ret != PAPI_OK )
		cout << "FAIL destroy" << endl;

}
