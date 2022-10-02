/* Testing data
 * Avail: 3 3 2
 * Alloc:   Req_safe:    Req_not_safe:
 * 0 1 0    7 4 3   7 4 3
 * 2 0 0    1 2 2   1 2 2
 * 3 0 2    6 0 0   6 0 0
 * 2 1 1    0 1 1   4 7 1
 * 0 0 2    4 3 1   4 3 1
 */
import java.util.Scanner;
class Banker_Detection {
    int[] Available;        //Available Vector
    int[][] Allocation;     //Allocation Matrix
    int[][] Request;        //Request Matrix 
    int m, n;               //m = amount of Thread and Matrices' height; n = amount of Resources and Matrices' width

    Scanner input = new Scanner(System.in);

    public void banker_detection(){  //method setting each matrix
        System.out.print("number of total Thread(m): ");
        m = input.nextInt();
        System.out.print("number of total Resource(n): ");
        n = input.nextInt();
        set_available();
        set_allocation();       //method calling each thread's Allocation
        set_request();
        print_variable();       //method printing all kinds of matrices
        Safe();                 //method checking if the program is in a safe state
    }

    /**
     * setting Allocation
     * */
    
    public void set_available(){
        Available = new int[n];
        System.out.println("Available vector for each resource (array like, separated by space):");
        for(int i = 0; i < n; i++){
            Available[i] = input.nextInt();
        }
    }
    
    public void set_allocation(){
        Allocation = new int[m][n];
        System.out.println("Setting each thread's Allocation:");
        for (int i = 0; i < m; i++) {
            System.out.println("Input of Thread_"+i+" allocation (array like, separated by space): ");
            for (int j = 0; j < n; j++) {
                Allocation[i][j] = input.nextInt();
            }
        }
    }
   /**
    * setting Request
    */
    public void set_request(){ 
        Request = new int[m][n];
        System.out.println("Setting each thread's Request:");
        for (int i = 0; i < m; i++) {
            System.out.println("Input of Thread_"+i+" request (array like, separated by space): ");
            for (int j = 0; j < n; j++) {
                Request[i][j] = input.nextInt();
            }
        }
    }
    /**
     * checking values by printing out
     * */
    public void print_variable(){
        System.out.println();
        System.out.println("System updated as below:");
        System.out.println("Thread =>Allocation => Request => Available");
        for (int i = 0; i < m; i++) {
            System.out.print("Thread"+i+"     ");
            for (int j = 0; j < n; j++) {
                System.out.print(Allocation[i][j]+" ");
            }
            System.out.print("  |   ");
            for (int j = 0; j < n; j++) {
                System.out.print(Request[i][j]+" ");
            }
            System.out.print("  |   ");
            if (i == 0){ //Available is only 1-dimensioned 
                for (int j = 0; j < n; j++) {
                    System.out.print(Available[j]+" ");
                }
            }
            System.out.println();
        }
        System.out.println("Proceed in 3 secs...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    private void Safe() {
        /*declare and initialize in-function parameters */
        int[] Work = new int[n];
        System.arraycopy(Available, 0, Work, 0, n); //Initialize Work as Available
        boolean[] Finish = new boolean[m];      // declare and initialize Finish
        for(int i = 0; i < m; i++){
            Finish[i] = true;
            for(int j = 0; j < n; j++){
                if (Allocation[i][j] != 0){
                    Finish[i] = false;
                }
            }
        }
        int[] order = new int[m];
        int count = 0;                          // count how many threads have been checked 
        boolean all_good = false;               // loops indicator
        boolean deadlock = false;               // if a deadlock is detected;

        /*implenment safety examination */
        while ((!deadlock) && (!all_good)) {    //when no deadlock is detected and not yet all checked
            boolean flag = true;                //indicator; true: through out the examination, no Thread can maintain the safe stae
            for (int i = 0; i < m; i++) {
                boolean smaller_finish = true;  //chech if Finish <= Work
                if (!Finish[i]){                //examine the request only when this thread has not been proved safe
                    for(int j = 0; j < n; j++){
                        if (Request[i][j] > Work[j]){
                            smaller_finish = false;
                        }
                    }
                    if (smaller_finish) { 
                        //if Finish[i] = false and Request_i <= Work
                        for (int j = 0; j < n;j++){
                            Work[j]+=Allocation[i][j];
                        }
                        Finish[i] = true;
                        order[count] = i;
                        flag = false; //There's at least one Thread that can maintain safe state
                        count++;
                        System.out.println("Work updated as below:");
                        for(int j = 0; j < n; j++){
                            System.out.print(Work[j]+"  ");
                        }
                        System.out.println();
                    }
                }
            }

            if (flag){
                deadlock = true;
            }
            else{
                if(count == m){
                    all_good = true;
                }
            }
        }
        if(all_good){
            System.out.println("No risk of deadlock");
            System.out.print("Safe sequence: <");
            for(int i = 0; i < m; i++){
                if(i == (m - 1))
                {System.out.println("Thread_" + order[i] + ">");}
                else
                {System.out.print("Thread_" + order[i] + ", ");}
            }
        }
        else{
            int k = 0;
            System.out.println("The system is now at risk of deadlock.");
            System.out.print("Unsafe sequence: <");
            for(int i = 0; i < count; i++){
                System.out.print("Thread_"  + order[i]+", ");
                k++;
            }

            for(int i = 0; i < m; i++){
                if(!Finish[i]){
                    k++;
                    if(k == m)
                    {System.out.println("Thread_"  + i + ">");}
                    else
                    {System.out.print("Thread_"  + i + ", ");}
                }
            }
        }
    }
}

public class  B10641020_Banker_Deadlock_Detection {
    public static void main(String[] args) {
        Banker_Detection bank = new Banker_Detection(); //Bank's Object
        bank.banker_detection();     //build up everything and run
    }
}

