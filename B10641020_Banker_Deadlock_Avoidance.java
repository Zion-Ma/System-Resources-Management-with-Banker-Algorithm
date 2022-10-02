/*
 * Testing data
 * Avail: 3 3 2
 * Alloc:   Max:
 * 0 1 0    7 5 3
 * 2 0 0    3 2 2
 * 3 0 2    9 0 2
 * 2 1 1    2 2 2
 * 0 0 2    4 3 3
 */

import java.util.Scanner;
import java.util.concurrent.*;

class Banker_Avoidance {
    int[] Available;        //Available Vector
    int[][] Allocation;     //Allocation Matrix
    int[][] Max;            //Max Matrix
    int[][] Need;           //Need Matrix
    int m, n;               //m: amount of Thread and Matrices' height; n: amount of Resources and Matrices' width
    boolean safe_after_request;

    Scanner input = new Scanner(System.in);

    public void banker_avoidance(){  //method setting each matrix
        System.out.print("number of total Thread(m): ");
        m = input.nextInt();
        System.out.print("number of total Resource(n): ");
        n = input.nextInt();
        set_available();        //method settinging Available
        set_allocation();       //method settinging each thread's Allocation
        set_max_and_need();     //method settinging each thread's Max
        print_variable();       //method printing all kinds of matrices
        Safety();               //method checking if the program is in a safe state
        set_request();          //method settinging each thread's Allocation
        System.out.println("The algorithm has come to its end.");
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

    public void set_max_and_need(){
        Max = new int[m][n];
        Need = new int[m][n];
        System.out.println("Setting each thread's Max:");
        for (int i = 0; i < m; i++) {
            System.out.println("Input of Thread_"+i+" max (array like, separated by space): ");
            for (int j = 0; j < n; j++) {
                Max[i][j] = input.nextInt();
                Need[i][j] = Max[i][j] - Allocation[i][j];
            }
        }
    }

    public void set_request(){
        int[] Request = new int[n];
        System.out.println("Proceed to Request test (Y/N)?: ");
        String proceed = input.next(); 
        while(proceed.equals("Y") || proceed.equals("y")){
            System.out.print("Thread's serial number (0,1,2,...,n-1): ");
            int k = input.nextInt(); //Request of which Thread
            System.out.println("Input of Thread_"+k+" request (array like, separated by space): ");
            for(int i = 0; i < n; i++){
                Request[i] = input.nextInt();
            }
            Resource_Request(k, Request);
            System.out.println("Continue Request test (Y/N)?: ");
            proceed = input.next();
        }
        System.out.println("Leaving Request test...");
    }

    /**
     * checking values by printing out
     * */
    public void print_variable(){
        System.out.println();
        System.out.println("System updated as below:");
        System.out.println("Allocation => Max => Need => Available");
        for (int i = 0; i < m; i++) {
            System.out.print("Thread"+i+" \t");
            for (int j = 0; j < n; j++) {
                System.out.print(Allocation[i][j]+" ");
            }
            System.out.print("  |   ");
            for (int j = 0; j < n; j++) {
                System.out.print(Max[i][j]+" ");
            }
            System.out.print("  |   ");
            for (int j = 0; j < n; j++) {
                System.out.print(Need[i][j]+" ");
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

    private void Safety() {
        safe_after_request = true; //used for Request recover
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
        int[] order = new int[m];               //show the sequence that will not cause deadlock, essentialliy if the safe state is maintained
        boolean[] S = new boolean[m];           // indicating which threads have not been checked that is safe for deadlock
        for (int i = 0; i < m; i++){
            S[i] = true;
        }
        int count = 0;                          // count how many threads have been checked 
        boolean all_good = false;               // loops indicator
        boolean deadlock = false;               // if a deadlock is detected; 
        while ((!deadlock) && (!all_good)) {    //when no deadlock is detected and not yet all checked
            boolean flag = true;                //indicator; true: through out the examination, no Thread can maintain the safe state
            for (int i = 0; i < m; i++) {
                boolean smaller_need = true;    //chech if Finish <= Work
                if (!Finish[i]){                //examine the request only when this thread has not been proved safe
                    for(int j = 0; j < n; j++){
                        if (Need[i][j] > Work[j]){
                            smaller_need = false;
                        }
                    }
                    if (smaller_need) { 
                        //if Finish[i] = false and Request_i <= Work
                        for (int j = 0; j < n;j++){
                            Work[j]+=Allocation[i][j];
                        }
                        Finish[i] = true;
                        flag = false; //There's at least one Thread that can maintain safe state
                        order[count] = i; //add one to safe sequence;
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
                if(i == (m - 1)){
                    System.out.println("Thread_" + order[i] + ">");
                }
                else{
                    System.out.print("Thread_" + order[i] + ", ");
                }
            }
        }
        else{
            safe_after_request = false; //used for Request recover
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
                    if(k == m){
                        System.out.println("Thread_"  + i + ">");
                    }
                    else{
                        System.out.print("Thread_"  + i + ", ");
                    }
                }
            }
        }
    }

    private void Resource_Request(int k, int[] Request){
        for(int i = 0; i < n; i++){ // if Request_k <= Need_k
            if (Request[i] > Need[k][i]){
                System.out.println("Request_" + k + " exceeded the maximum claim.");
                return;
            }
        }
        for(int i = 0; i < n; i++){ // if Request_k <= Need_k
            if (Request[i] > Available[i]){
                System.out.println("Thread_" + k + " must wait.");
                return;
            }
        }
        for(int i = 0; i < n; i++){ // update related matrices
            Available[i] = Available[i] - Request[i];
            Allocation[k][i] = Allocation[k][i] + Request[i];
            Need[k][i] = Need[k][i] - Request[i];
        }
        Safety();
        if(safe_after_request){ //even the Request passed, it might still be unsafe to the system.
            System.out.println("Request test passed.");
            print_variable();
        }
        else{
            System.out.println("Even the Request passed, it is still unsafe to the system and thus cancelled.");
            for(int i = 0; i < n; i++){ // recover related matrices
                Available[i] = Available[i] + Request[i];
                Allocation[k][i] = Allocation[k][i] - Request[i];
                Need[k][i] = Need[k][i] + Request[i];
            }
        }
    }
}

public class B10641020_Banker_Deadlock_Avoidance{
    public static void main(String[] args) {
        Banker_Avoidance banker = new Banker_Avoidance();   //Banker's Object
        banker.banker_avoidance();          //build up everything and run
    }
}

/*
 * Testing data
 * Avail: 3 3 2
 * Alloc:   Max:
 * 0 1 0    7 5 3
 * 2 0 0    3 2 2
 * 3 0 2    9 0 2
 * 2 1 1    2 2 2
 * 0 0 2    4 3 3
 */


