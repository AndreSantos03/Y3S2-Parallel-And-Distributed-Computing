import java.util.Scanner;
import java.io.*;

public class matrixProduct {
    public static double onMult(int m_ar, int m_br) {
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
        return (timeEnd - timeStart)/1000.0 ;
    }
    
    public static double onMultLine(int m_ar, int m_br) {
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
        return (timeEnd - timeStart)/1000.0;
    }

    public static double OnMultBlock(int m_ar, int m_br, int bkSize){
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
		long timeEnd = System.currentTimeMillis();

        System.out.println("Resulting Matrix: ");
        for(int i = 0; i < 10;i++){
            System.out.printf("%d    ",(int)phc[i]);
        }

        System.out.printf("\nTime passed: %.3f Seconds",(timeEnd - timeStart)/1000.0 );
        return (timeEnd - timeStart)/1000.0;
   }

    public static void main(String[] args) {
        
        int op, lin, col, blockSize;
        double res;

        op = Integer.parseInt(args[0]);
        lin = Integer.parseInt(args[1]);
        col = lin;
        
        switch (op) {
            case 1:
                System.out.println("Simple Multiplication");
                res = onMult(lin, col);
                break;
            case 2:
                System.out.println("Line Multiplication");
                res = onMultLine(lin, col);
                    break;
            case 3:
                System.out.println("Block Multiplication");
                blockSize = Integer.parseInt(args[2]);
                res = OnMultBlock(lin, col, blockSize);
                break;
            default:
                res = 0;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(args[3], true))) {
            switch (op) {
                case 1:
                    writer.write("Simple Multiplication\n");
                    break;
                case 2:
                    writer.write("Line Multiplication\n");
                    break;
                default:
                    break;
            }
            writer.write("Matrix size:" + lin + "*" + "col\n");
            writer.write("Processing Time: " + res +"\n");
            writer.write("######################################\n");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
            
    }
}
