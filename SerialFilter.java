import java.util.*;
import java.io.*;

public class SerialFilter{

   static long startTime = 0;
	
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	private static float toc(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}


    public ArrayList<Double> sequentialFilter(ArrayList<Double> arr, int size){
        ArrayList<Double> y = new ArrayList<Double>();
        double [] filterArray = new double[size];
        
        int filterBorder = (size-1)/2;
        int median = (size+1)/2;
        
        for (int i = 0; i < arr.size(); i++){ 
            if (i >= filterBorder && arr.size() - i > filterBorder){
                int k = 0;  
                for (int j = i - filterBorder; j < i + filterBorder + 1; j++){
               
                    filterArray[k] = arr.get(j);
                    k++;
                }
                Arrays.sort(filterArray);
                y.add(i,filterArray[median-1]);      
            }
            
            else{
                y.add(i,arr.get(i));
            }
       }
       return y; 
   }



   public static void main(String [] agrs){
      SerialFilter medianFilter = new SerialFilter();
      Scanner input = new Scanner(System.in);
      
      String entry = input.nextLine();
      String filename = entry.substring(0, entry.indexOf(" "));
      
      String filter = entry.substring(entry.indexOf(" ")+1, entry.lastIndexOf(" "));
      String output = entry.substring(entry.lastIndexOf(" ")+1, entry.length());
      
      int filterSize = Integer.parseInt(filter);
      Scanner inputFile = null;
      PrintWriter outputFile = null;
      
      try {
            
            inputFile = new Scanner(new File(filename));
            outputFile = new PrintWriter(new FileOutputStream(output));
            int counter = 0;
            

            ArrayList<Double> x = new ArrayList<Double>();
            String data = null;
            while(inputFile.hasNextLine()){
                 data = inputFile.nextLine();
                 
                
                 if (counter == 0){
                    counter++;
              
                 }
                 
                 else if(counter == 1000000){
                    break;
                 }
                 
                 else {
              
                    
                    
                    String list1 = data.substring(data.indexOf(" ")+1, data.length());
                    double value1 = Double.parseDouble(list1);
                    x.add(counter-1,value1);
                    counter++;
            
                 }
                 
             }
             inputFile.close();
             
             tick();
             ArrayList<Double> damn = medianFilter.sequentialFilter(x,filterSize);
             float time = toc();
             
             for (int a = 0; a < damn.size(); a++){
                 outputFile.println(a+" "+damn.get(a));
             }
             System.out.println("Serial took "+ time +" seconds");
             
       }
       
       catch(IOException e){
            System.out.println("File not found");
       }

   }
} 