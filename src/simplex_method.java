/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;

/**
 *
 * @author acer
 */
public class simplex_method {

    static float[][] table;
    static float[][] matrix_min;
    static String[] row_variables;        // all variables in a row
    static String[] basic_variables;       // contain basic variables
    static boolean problemType = false;      // Problem Type
    static boolean canPrint = true;
    static Scanner sc = new Scanner(System.in);

    public static void initialize() {

        System.out.println("Choose Problem Type:\n" + "\t\t 1) Maximization Problem \n" + "\t\t 2) Minimization Problem");
        System.out.print("Enter chosen type: ");
        int type = sc.nextInt();
        while (type > 2 || type <= 0) {
            System.out.println("!!!Invalid Input");
            System.out.print("Enter chosen type: ");
            type = sc.nextInt();
        }
        problemType = type == 1;
        System.out.print("Enter number of variables: ");
        int num_val = sc.nextInt();
        int n = num_val;
        System.out.print("Enter number of constraints: ");
        int num_constraint = sc.nextInt();
        int m = num_constraint;
        int tmp;
        if (problemType == false) {
            table = new float[num_val + 1][num_val + num_constraint + 2];
            tmp = num_val;
            n = num_constraint;
            m = tmp;
        } else {
            table = new float[num_constraint + 1][num_constraint + num_val + 2];
        }
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                table[i][j] = 0;
            }
        }
        System.out.println("Enter coefficients of Objective Function:");
        table[0][0] = 1;
        int[] objective_arr = new int[num_val];
        //scan for coefficient of objective function
        for (int i = 0; i < num_val; i++) {
            System.out.print("--->Enter the value of " + "x" + (i + 1) + ": ");
            objective_arr[i] = sc.nextInt();
        }

        for (int i = 0; i < objective_arr.length; i++) {
            //if minimize problem=> coefficient of objective function is right hand side
            if (problemType == false) {
                table[i + 1][table[0].length - 1] = objective_arr[i];
                //System.out.println(table[i + 1][table[0].length - 1]);
            } else {
                table[0][i + 1] = -objective_arr[i];
            }
        }
        //scan for each of constraint
        for (int j = 0; j < num_constraint; j++) {
            System.out.println("Enter left coefficients of constraints(" + (j + 1) + ")");
            if (j < num_val) {
                table[j + 1][0] = 0;
            }
            for (int i = 0; i < num_val; i++) {
                System.out.print("--->Enter the value of " + "x" + (i + 1) + ": ");
                //if minimize problem=> put value to column else to row
                if (problemType == false) {
                    table[i + 1][j + 1] = sc.nextInt();
                } else {
                    table[j + 1][i + 1] = sc.nextInt();
                }
            }
            System.out.println("--->Choose Inequality option: \n" + "\t\t 1) ≤ \n" + "\t\t 2) ≥ \n");
            System.out.print("--->Enter chosen option: ");
            int choice = sc.nextInt();
            if (problemType == false) {
                //if minimize problem=>only >=
                while (choice != 2) {
                    System.out.println("!!!Invalid Input");
                    System.out.print("--->Enter chosen option: ");
                    choice = sc.nextInt();
                }
            } else {
                while (choice != 1) {
                    System.out.println("!!!Invalid Input");
                    System.out.print("--->Enter chosen option: ");
                    choice = sc.nextInt();
                }
            }
            if (j < num_val) {
                table[j + 1][n + j + 1] = 1;   // slack variable added
            }
            System.out.print("Enter right coefficient of constraints(" + (j + 1) + "): ");
            int b_val = sc.nextInt();
            //right hand side is non-negative
            while (b_val < 0) {
                System.out.println("!!!Invalid Input");
                System.out.print("Enter right coefficient of constraints(" + (j + 1) + "): ");
                b_val = sc.nextInt();
            }
            //if minimize problem=> put b_val by row else by column
            if (problemType == false) {
                table[0][j + 1] = -b_val;
            } else {
                table[j + 1][table[0].length - 1] = b_val;
            }

        }
        System.out.println();
        fill_variables(n, m);
        System.out.println("Init table:");
        printTable();
        optimize_table();

        if (canPrint) {
            System.out.println("Final table: ");
            printTable();
            print_solution(num_constraint);
        }
    }

    public static void optimize_table() {
        int iter = 1;
        while (checkMinExists()) {
            int index = min_index();
            float min_ratio = Float.MAX_VALUE;
            int min_index = 0;
            boolean state = false;
            for (int j = 1; j < table.length; j++) {
                if (table[j][index] > 0) {           // must be >= 0
                    state = true;
                    float ratio = table[j][table[0].length - 1] / table[j][index]; //calculate to find pivot row
                    if (ratio < min_ratio) {
                        min_ratio = ratio;
                        min_index = j;
                    }
                }
            }
            if (!state) {
                System.out.println("******* This system has unbounded solution *******");
                canPrint = false;
                break;
            } else {
                System.out.format("Iteration %d:\n", iter);
                System.out.format("--->Variable {%s} is replaced by variable {%s} ", basic_variables[min_index], row_variables[index]);
                System.out.println();
                basic_variables[min_index] = row_variables[index];   // swap basic variables
                row_operation(index, min_index);       // row operation in table
                iter++;
            }
        }
    }

    public static void row_operation(int index, int min_index) {
        float num = table[min_index][index];//pivot value
        // operation in pivot row: make pivot value =1
        for (int i = 0; i < table[0].length; i++) {
            table[min_index][i] = table[min_index][i] / num;
        }
        //row operation to make all element in pivot column equal 0 except pivot value
        for (int i = 0; i < table.length; i++) {
            if (i != min_index) {
                float cal = -table[i][index];
                for (int j = 0; j < table[0].length; j++) {
                    table[i][j] = cal * table[min_index][j] + table[i][j];
                }
            }
        }
    }

    public static void printTable() {
        int number_col = row_variables.length;
        int number_row = basic_variables.length;
        for (int k = 0; k < number_col; k++) {
            System.out.print("\t" + row_variables[k]);
        }
        System.out.println();
        for (int i = 0; i < number_row; i++) {
            System.out.print(basic_variables[i] + "\t");
            for (int j = 0; j < number_col; j++) {
                System.out.format("%.2f\t", table[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void print_solution(int num_constraint) {
        System.out.println("*************** Optimal Solution: *********************");
        if (problemType == false) {
            for (int k = num_constraint + 1; k < row_variables.length - 1; k++) {
                System.out.format("The value of %s is: %.2f\n", row_variables[k], table[0][k]);
            }
            for (int i = 1; i <= num_constraint; i++) {
                boolean state = false;
                for (int j = 1; j < basic_variables.length; j++) {
                    if (row_variables[i].equals(basic_variables[j])) {
                        System.out.format("The value of %s is: %.2f\n", row_variables[i], table[j][table[0].length - 1]);
                        state = true;
                        break;
                    }
                }
                if (!state) {
                    System.out.format("The value of %s is: 0\n", row_variables[i]);
                }
            }
            System.out.format("The value of P_min is: %.2f\n", table[0][table[0].length - 1]);
        } else {
            for (int i = 1; i < row_variables.length - 1; i++) {
                boolean state = false;
                for (int j = 1; j < basic_variables.length; j++) {
                    if (row_variables[i].equals(basic_variables[j])) {
                        System.out.format("The value of %s is: %.2f\n", row_variables[i], table[j][table[0].length - 1]);
                        state = true;
                        break;
                    }
                }
                if (!state) {
                    System.out.format("The value of %s is: 0\n", row_variables[i]);
                }
            }
            System.out.format("The value of P_max is: %.2f\n", table[0][table[0].length - 1]);
        }
    }

    public static void fill_variables(int n, int m) {
        basic_variables = new String[m + 1];
        basic_variables[0] = "P";
        for (int i = 0; i < m; i++) {
            basic_variables[i + 1] = "s" + (i + 1);
        }
        row_variables = new String[n + m + 2];
        row_variables[0] = "P";
        if (problemType == false) {
            for (int i = 0; i < n; i++) {
                row_variables[i + 1] = "y" + (i + 1);
            }
            for (int i = 0; i < m; i++) {
                row_variables[n + i + 1] = "x" + (i + 1);
            }
        } else {
            for (int i = 0; i < n; i++) {
                row_variables[i + 1] = "x" + (i + 1);
            }
            for (int i = 0; i < m; i++) {
                row_variables[n + i + 1] = "s" + (i + 1);
            }
        }
        row_variables[n + m + 1] = "c";
    }

    public static boolean checkMinExists() {
        boolean state = false;
        for (int i = 0; i < table[0].length; i++) {
            if (table[0][i] < 0) {
                state = true;
                break;
            }
        }
        return state;
    }

    public static int min_index() {
        int index = 0;
        float min = Float.MAX_VALUE;
        for (int i = 0; i < table[0].length; i++) {
            if (table[0][i] < min) { //find pivot column
                index = i;
                min = table[0][i];
            }
        }
        //System.out.println(min);
        return index;
    }

    public static void main(String[] args) {
        String ans;
        while (true) {
            initialize();
            while (true) {
                System.out.println("--------------------------------------");
                System.out.print("Do you want to continue (Y/N)?");
                ans = sc.next();
                if (!ans.matches("[yYnN]")) {
                    System.out.println("!!!Invalid Input");
                }else{
                    break;
                }
            }
            if(ans.matches("[nN]")){
                break;
            }
        }
    }
}
