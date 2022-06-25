/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author acer
 */
public class printTable {
    static float[][] table;
    public static void main(String[] args) {
        int rows = 2;
        int columns = 3;
        String[] label=new String[rows];
        label[0]="row1";
        label[1]="row2";
        table=new float[rows][columns];
        table[0][0]=0;
        table[0][1]=1;
        table[0][2]=2;
        table[1][0]=3;
        table[1][1]=4;
        table[1][2]=5;
    for (int i = 0; i<rows; i++) {
        System.out.format("variable %s is replaced by variable %s",label[0],label[1]);       
    System.out.println();
}
    }
}
