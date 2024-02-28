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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int op, lin, col, blockSize;
        
        do {
            System.out.println("1. Multiplication");
            System.out.println("2. Line Multiplication");
            System.out.println("3. Block Multiplication");
            System.out.println("0. Exit");
            System.out.print("Selection?: ");
            op = scanner.nextInt();
            
            if (op == 0)
                break;
            
            System.out.print("Dimensions: lins=cols ? ");
            lin = scanner.nextInt();
            col = lin;
            
            switch (op) {
                case 1:
                    onMult(lin, col);
                    break;
                case 2:
                    onMultLine(lin, col);
                    break;
                case 3:
                    System.out.print("Block Size? ");
                    blockSize = scanner.nextInt();
                    // OnMultBlock(lin, col, blockSize);
                    break;
            }
            
        } while (op != 0);
        
        scanner.close();
    }
}
