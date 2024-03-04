import java.util.Scanner;

public class matrixProduct {
    public static void onMult(int m_ar, int m_br) {
        double[] pha = new double [m_ar * m_ar];
        double[] phb = new double [m_ar * m_ar];
        double[] phc = new double [m_ar * m_ar];

        for(int i = 0; i < m_ar; i++){
            for(int j = 0; j<m_ar;j++){
                pha[i*m_ar + j] = i + 1;
            }
        }

        for(int i = 0; i < m_br; i++){
            for(int j = 0; j<m_br;j++){
                phb[i*m_br + j] = i + 1;
            }
        }

        long timeStart = System.currentTimeMillis();

        for(int i = 0; i < m_ar; i++){
            for(int j = 0; j < m_br;j++){
                double temp = 0;
                for(int k = 0; k < m_ar; k++){
                    temp += pha[i * m_ar + k] * phb[k*m_br + j];
                }
                phc[i*m_ar+j]=temp;
            }
        }
        
        long timeEnd = System.currentTimeMillis();

        System.out.println("Resulting Matrix: ");
        for(int i = 0; i < 10;i++){
            System.out.printf("%d    ",(int)phc[i]);
        }

        System.out.printf("\nTime passed: %.3f Seconds\n",(timeEnd - timeStart)/1000.0 );
    }
    
    public static void onMultLine(int m_ar, int m_br) {
        double[] pha = new double [m_ar * m_ar];
        double[] phb = new double [m_ar * m_ar];
        double[] phc = new double [m_ar * m_ar];

        for(int i = 0; i < m_ar; i++){
            for(int j = 0; j<m_ar;j++){
                pha[i*m_ar + j] = i + 1;
            }
        }

        for(int i = 0; i < m_br; i++){
            for(int j = 0; j<m_br;j++){
                phb[i*m_br + j] = i + 1;
            }
        }
        
        for(int i = 0; i < m_br; i++){
            for(int j = 0; j<m_br;j++){
                phc[i*m_br + j] = 0;
            }
        }

        long timeStart = System.currentTimeMillis();

        for(int i = 0; i < m_ar; i++){
            for(int k = 0; k < m_br;k++){
                for(int j = 0; j < m_ar; j++){
                    phc[i*m_ar+j] += pha[i * m_ar + k] * phb[k*m_br + j];
                }
            }
        }
        
        long timeEnd = System.currentTimeMillis();

        System.out.println("Resulting Matrix: ");
        for(int i = 0; i < 10;i++){
            System.out.printf("%d    ",(int)phc[i]);
        }

        System.out.printf("\nTime passed: %.3f Seconds",(timeEnd - timeStart)/1000.0 );
    }

    public static void OnMultBlock(int m_ar, int m_br, int bkSize){
    	double[] pha = new double [m_ar * m_ar];
        double[] phb = new double [m_ar * m_ar];
        double[] phc = new double [m_ar * m_ar];

        for(int i = 0; i < m_ar; i++){
            for(int j = 0; j<m_ar;j++){
                pha[i*m_ar + j] = i + 1;
            }
        }

        for(int i = 0; i < m_br; i++){
            for(int j = 0; j<m_br;j++){
                phb[i*m_br + j] = i + 1;
            }
        }
        
        for(int i = 0; i < m_br; i++){
            for(int j = 0; j<m_br;j++){
                phc[i*m_br + j] = 0;
            }
        }

        long timeStart = System.currentTimeMillis();
	    
	 for(int ii = 0; ii < m_ar; ii+= bkSize){
			for(int kk = 0; kk < m_ar; kk+= bkSize){
				for(int jj = 0; jj < m_br; jj+= bkSize){
					for(int i = ii; i < ii*bkSize; i++){
						for(int k = kk; k < kk*bkSize; k++){
							for(int j = jj; j < jj*bkSize; j++){
								phc[i*m_ar+j] += pha[i*m_ar+k] * phb[k*m_br+j];
							}
						}
					}	
				}
			}
		}
		long timeEnd = System.currentTimeMillis();

        System.out.println("Resulting Matrix: ");
        for(int i = 0; i < 10;i++){
            System.out.printf("%d    ",(int)phc[i]);
        }

        System.out.printf("\nTime passed: %.3f Seconds",(timeEnd - timeStart)/1000.0 );
   }


    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give size of the matrix: ");
        Integer sizeMatrix = Integer.parseInt(scanner.nextLine());
        System.out.println("Give blockSize: ");
        Integer bkSize = Integer.parseInt(scanner.nextLine());
        onMult(sizeMatrix,sizeMatrix);
        onMultLine(sizeMatrix,sizeMatrix);
        OnMultBlock(sizeMatrix,sizeMatrix,bkSize);   
        }
}
