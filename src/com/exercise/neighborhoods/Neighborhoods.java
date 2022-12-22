package com.exercise.neighborhoods;
import java.util.*;
import java.io.*;
import javafx.util.Pair;
import java.lang.Math;
import java.lang.Integer;

public class Neighborhoods{

    public static int[][] findNeighborhoods(int cells[][], int height, int width, int n) {
        //Iterate through the cells array searching for positive cells
        //When a positive cell is found push the coordinates onto a stack
        //Calculate adjacent cells and push those coordinates onto the stack
        //Continue to do so until the distance threshold is reached
        //If another positive cell is located at one of the adjacent cells, recalculate distance

        //Enumeration for cells in neighborhoods array
        // 0 = non-neighborhood cell
        // 1 = neighborhood cell

        //Declare neighborhoods array
        int [][]neighborhoods = new int[height][width];

        //Declare stack to track adjacent coordinates
        Stack<Pair<Integer, Integer>> st = new Stack<Pair<Integer, Integer>>();

        //Declare variable to track distance traveled from positive cell
        int distance_traveled;

        //Initialize direction vectors for adjacent cells
        int d_x[] = { 0, 1, 0, -1 };
        int d_y[] = { -1, 0, 1, 0 };

        //Declare x and y variables for use tracking coordinates
        int x, y;

        //Declare array for tracking positive cell (home) locations
        //pair<int, int> homes[100];
        ArrayList<Pair<Integer,Integer>> homes = new ArrayList<Pair<Integer, Integer>>();

        //Iterate through cells array until a positive cell is located
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {

                //Check if cell is positive and not already part of a neighborhood
                if(cells[i][j] == 1 && neighborhoods[i][j] == 0) {

                    Pair coord = new Pair(i, j);
                    //Push coordinates onto stack
                    st.push(coord);

                    //Reset distance traveled variable
                    distance_traveled = 0;

                    //Add location to homes array
                    homes.add(coord);

                    //Iterate through stack until empty
                    while(!st.empty()) {

                        //Get value of top element in stack and pop
                        Pair<Integer, Integer> curr = st.peek();
                        st.pop();

                        y = curr.getKey();
                        x = curr.getValue();

                        //Check that coordinates are outside array bounds, if so skip
                        if(y < 0 || x < 0 || y >= height || x >= width)
                            continue;

                        //Check if cell has already been visited, skip if so
                        if(neighborhoods[y][x] != 0)
                            continue;

                        int smallest_distance, temp_distance;

                        smallest_distance = 100;

                        //Calculate distance traveled from nearest home
                        for(int k = 0; k < homes.size(); k++) {

                            //Calculate Manhattan distance from current cell to home
                            temp_distance = Math.abs(y - homes.get(k).getKey()) + Math.abs(x - homes.get(k).getValue());

                            //If calculated distance is less than the current smallest distance, smallest distance becomes temp distance
                            if(temp_distance < smallest_distance)
                                smallest_distance = temp_distance;
                        }

                        //Set distance_travelled to smallest calculated distance
                        distance_traveled = smallest_distance;

                        //Check if within distance threshold (distance_traveled < n)
                        if(distance_traveled <= n) {

                            //Mark current cell as in neighborhood
                            neighborhoods[y][x] = 1;

                            //Check if cell is positive
                            if(cells[y][x] > 0) {

                                //Initialize flag to signify home presence
                                boolean home_present = false;

                                //Check if cell does not exist in homes array
                                for(int k = 0; k < homes.size(); k++) {

                                    //If cell exists in array, set presence flag to true
                                    if(homes.get(k).getKey() == y && homes.get(k).getValue() == x)
                                        home_present = true;
                                }

                                //Add location to homes array if not already present
                                if(!home_present) {

                                    Pair loc = new Pair(y,x);
                                    homes.add(loc);
                                }

                            }

                            //Push adjacent cells onto stack
                            for(int k = 0; k < 4; k++) {

                                //Ensure all cells on adjacent vectors and within distance threshold are pushed onto stack
                                for(int l = 1; l <= n; l++) {

                                    int adjx = x + l*d_x[k];
                                    int adjy = y + l*d_y[k];
                                    Pair adj = new Pair(adjy, adjx);
                                    st.push(adj);

                                    //For example given y = 2, x = 2 or (2,2) with a distance threshold of 2:
                                    //(2,0), (4,2), (2,4), (0,2) and
                                    //(2,1), (3,2), (2,3), (1,2) will be pushed onto the stack

                                    //This fixes an issue where a cell within the distance threshold will be "hidden"
                                    //due to its adjacent cells already being captured by a previous home's neighborhood
                                }
                            }

                        }

                    }

                    //Clean up homes array
                    homes.clear();
                }
            }
        }

        return neighborhoods;
    }

    public static int calcNeighborhoodArea(int cells[][], int height, int width, int n) {

        //Initialize neighborhood_area variable to 0
        int neighborhood_area = 0;

        //Declare array to contain coordinates of homes
        ArrayList<Pair<Integer, Integer>> homes = new ArrayList<Pair<Integer, Integer>>();

        //Find cells that contain a positive integer
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if(cells[i][j] == 1) {
                    //Add coordinates to homes array
                    Pair coord = new Pair(i,j);
                    homes.add(coord);
                }
            }
        }

        //Declare distance, area, overlap, and overhang variables
        int d, d_x, d_y;
        int area = 0;
        float overlap;
        int overhang = 0;
        int overhang_overlap = 0;

        //Loop through homes array
        for(int i = 0; i < homes.size(); i++) {

            //Reset overhang and overhang_overlap
            overhang = 0;
            overhang_overlap = 0;

            //Set Pair p to current home
            Pair<Integer, Integer> p = new Pair<Integer, Integer>(homes.get(i).getKey(), homes.get(i).getValue());

            //Calculate area for p using equation n^2 + (n+1)^2
            area += n*n + (n+1)*(n+1);

            //Set variables for x and y
            int x = homes.get(i).getValue();
            int y = homes.get(i).getKey();

            //Account for lower bound x edge case (x < n)
            if(x < n) {

                //Calculate overhang area as (n - x)^2
                overhang += (n - x)*(n - x);
            }

            //Account for upper bound x edge case (x > width - 1 - n)
            if(x > (width - 1 - n)) {

                //Calculate overhang area as (n - (width - 1- x))^2
                overhang += (n - (width - 1 - x))*(n - (width - 1 - x));
            }

            //Account for lower bound y edge case (y < n)
            if(y < n) {

                //Calculate overhang area as (n - y)^2
                overhang += (n - y)*(n - y);
            }

            //Account for upper bound y edge case (y > height - 1 - n)
            if(y > (height - 1 - n)) {

                //Calculate overhang area as (n - (height - 1- y))^2
                overhang += (n - (height - 1 - y))*(n - (height - 1 - y));
            }

            //Account for Upper Left corner case (y < n && x < n)
            if(y < n && x < n) {

                //Calculate overlap from overhang calculations in edge cases

                //Overlap limit is (n - y) + (n - x) - n - 1
                //Simplified down to limit = n - y - x - 1
                int overlap_limit = n - y - x - 1;

                //Sum overlap until limit is reached
                for(int j = 1; j <= overlap_limit; j++) {
                    overhang_overlap += j;
                }
            }

            //Account for Upper Right corner case (y < n && x > width - 1 - n)
            if(y < n && x > (width - 1 - n)) {

                //Calculate overlap from overhang calculations in edge cases

                //Overlap limit is (n - y) + (n - (width - 1 - x)) - n - 1
                //Simplified down to limit = n - y - width + x
                int overlap_limit = n - y - width + x;

                //Add up  overlap until limit is reached
                for(int j = 1; j <= overlap_limit; j++) {
                    overhang_overlap += j;
                }
            }

            //Account for Bottom Left corner case (y > height - 1 - n && x < n)
            if(y > (height - 1 - n) && x < n) {

                //Calculate overlap from overhang calculations in edge cases

                //Overlap limit is (n - (height - 1 - y)) + (n - x) - n - 1
                //Simplified down to limit = n - height + y - x
                int overlap_limit = n - height + y - x;

                //Add up  overlap until limit is reached
                for(int j = 1; j <= overlap_limit; j++) {
                    overhang_overlap += j;
                }
            }

            //Account for Bottom Right corner case (y > height - 1 - n && x > width - 1 - n)
            if(y > (height - 1 - n) && x > (width - 1 - n)) {

                //Calculate overlap from overhang calculations in edge cases

                //Overlap limit is (n - (height - 1 - y)) + (n - (width - 1 - x)) - n - 1
                //Simplified down to limit = n - height + y - width + x + 1
                int overlap_limit = n - height + y - width + x + 1;

                //Add up  overlap until limit is reached
                for(int j = 1; j <= overlap_limit; j++) {
                    overhang_overlap += j;
                }
            }

            area = area - overhang + overhang_overlap;


            //Loop through homes array again, starting from i
            for(int j = i; j < homes.size(); j++) {

                //Reset overlap
                overlap = 0;

                //Calculate distance between p and current home
                d_y = Math.abs(p.getKey() - homes.get(j).getKey());
                d_x = Math.abs(p.getValue() - homes.get(j).getValue());
                d = d_y + d_x;

                if(d == 0)
                    continue;

                //Check if distance between p and current home is less than 2*n + 1
                if( d < 2*n + 1 ) {

                    //Calculate overlap
                    //num_diags is max number of diagonal lines overlapping between points
                    // 2*n + 1 is max distance between points to cause overlap, d is manhattan distance between points
                    int num_diags = 2*n + 1 - d;

                    //Calculate overlap with the equation
                    // overlap = num_diags * (n - |(d_x - d_y)/2|) + num_diags/2 + (num_diags % 2)
                    // where length of each diagonal line is n - |(d_x - d_y)/2|
                    // and extra length not captured by n - |(d_x - d_y)/2| because of lines alternating size
                    // is captured by num_diags/2 + (num_diags % 2)

                    float diag_length = (float)n - Math.abs(((float)d_x - (float)d_y)/2);
                    int diag_extra = (num_diags / 2) + (num_diags % 2);

                    overlap = num_diags * diag_length + diag_extra;
                }

                //Subtract the overlap area calculated from the total area calculated
                area = area - (int)overlap;
            }

        }

        return area;

    }

    public static void main(String[] args) {

        int height = 0;
        int width = 0;
        int n = 0;
        String gridType = "random";

        try {
            //Initialize InputStreamReader (character input stream) object to read input from system console
            InputStreamReader isr = new InputStreamReader(System.in);

            //Initialize BufferedReader object to buffer text from input stream
            BufferedReader br = new BufferedReader(isr);

            //Prompt user for grid setup information
            System.out.println("Would you like to set up a random or directed grid?");
            gridType = br.readLine();

            //Prompt user for grid height
            System.out.println("Grid height (1 to 100):");
            height = Integer.parseInt(br.readLine());

            //Prompt user for grid width
            System.out.println("Grid width (1 to 100):");
            width = Integer.parseInt(br.readLine());

            //Prompt user for distance threshold
            System.out.println("Neighborhood Distance Threshold (1 to 10):");
            n = Integer.parseInt(br.readLine());
        }
        catch(IOException e) {
            System.out.println("IO exception");
            return;
        }

        //Create 2 dimensional array of height and width
        int[][] cells = new int[height][width];

        //Random Setup
        if (gridType.equals("random")) {

            //Create instance of Random class
            Random rand = new Random();

            //Initialize cells array to have either 1 or -1
            for(int i = 0; i < cells.length; i++) {
                for(int j = 0; j < cells[i].length; j++ ) {

                    //Generate random int between 0 and 2^22
                    int rand_int = rand.nextInt();

                    if(rand_int % 17 == 0)
                        cells[i][j] = 1;
                    else
                        cells[i][j] = -1;
                }
            }

        }
        //Manual Setup
        else if (gridType.equals("directed")) {

            int row = 0;
            int col = 0;

            //Prompt user to enter cell locations in row, col format
            System.out.println("Enter locations for positive integers in row, column format. Enter -1 to finish.");

            //loop until invalid row or col value is entered
            while(row >= 0 && col >= 0 && row < height && col < width) {

                try {
                    //Initialize InputStreamReader (character input stream) object to read input from system console
                    InputStreamReader isr = new InputStreamReader(System.in);

                    //Initialize BufferedReader object to buffer text from input stream
                    BufferedReader br = new BufferedReader(isr);

                    System.out.println("Row: ");
                    row = Integer.parseInt(br.readLine());
                    if(row < 0 || row >= height)
                        break;

                    System.out.println("Col: ");
                    col = Integer.parseInt(br.readLine());
                    if(col < 0 || col >= width)
                        break;

                }
                catch(IOException e) {
                    System.out.println("IO exception");
                    return;
                }

                cells[row][col] = 1;

            }
        }
        //Incorrect gridType input
        else {
            System.out.println("Incorrect input received. Application exiting.");
            return;
        }

        //Print initial array
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if(cells[i][j] == 1) {
                    if(j == width-1)
                        System.out.println(" +");
                    else
                        System.out.print(" +");
                }
                else {
                    if(j == width-1)
                        System.out.println(" .");
                    else
                        System.out.print(" .");
                }
            }
        }

        //Print breaker
        System.out.println("*******************************************************");

        //Declare an array that is of equivalent size to cells to track neighborhoods
        int [][]neighborhoods = new int[height][width];

        //Initialize the neighborhoods array to 0s (signifying not yet visited)
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                neighborhoods[i][j] = 0;
            }
        }

        //Find neighborhoods
        neighborhoods = findNeighborhoods(cells, height, width, n);

        int n_cells = 0;

        //Print neighborhoods array
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if(neighborhoods[i][j] == 1) {
                    if(j == width-1)
                        System.out.println(" o");
                    else
                        System.out.print(" o");
                    n_cells++;

                }
                else {
                    if(j == width-1)
                        System.out.println("  ");
                    else
                        System.out.print("  ");
                }
            }
        }

        System.out.println("There are " + n_cells + " cells within neighborhoods.");

        System.out.println("calcNeighborhoodArea output: " + calcNeighborhoodArea(cells, height, width, n));

        return;
    }

}
