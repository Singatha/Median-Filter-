import java.util.*;  
import java.io.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ParallelFilter extends RecursiveTask<ArrayList<Double>>{

    private int filter;
    private int low;
    private int high;
    private ArrayList<Double> array;
    public ArrayList<Double> newArray;
    public final int SEQUENCIAL_CUTOFF = 750000;
    private ArrayList<Double> ans;
    
    
   static final ForkJoinPool fjPool = new ForkJoinPool();
   static long startTime = 0;
	
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	private static float toc(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}

   ParallelFilter(ArrayList<Double> arr, int low, int high, int filter){
          this.array = arr;
          this.low = low;
          this.high = high;
          this.filter = filter;
   }
   
   public ArrayList<Double> filter(ArrayList<Double> arr, int size){
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
   
   
   
   
   protected ArrayList<Double> compute(){
       if (this.high - this.low < SEQUENCIAL_CUTOFF){
           ArrayList<Double> serial = new ArrayList<Double>(this.array.subList(this.low,this.high));
           return filter(serial,this.filter);       // call the sequential method for filtering
       }
       
       else {
          ParallelFilter left = new ParallelFilter(this.array,this.low,(this.high+this.low)/2,this.filter);
          ParallelFilter right = new ParallelFilter(this.array,(this.high+this.low)/2,this.high,this.filter);
          left.fork();
          
          ArrayList<Double> rightAns = right.compute();
          ArrayList<Double> leftAns = left.join();
          
          newArray = new ArrayList(leftAns);
          newArray.addAll(rightAns);
          return newArray;
          
          
       }
       
   }
    
   public static ArrayList<Double> medianFilter(ArrayList<Double> arr, int size){
         ParallelFilter parallelFilter = new ParallelFilter(arr,0,arr.size(),size);
         return fjPool.commonPool().invoke(parallelFilter);
         
   }
   
   public static void main(String [] args){

      Scanner input = new Scanner(System.in);
      
      String entry = input.nextLine();
      String filename = entry.substring(0, entry.indexOf(" "));
      
      String filter = entry.substring(entry.indexOf(" ")+1, entry.lastIndexOf(" "));
      String output = entry.substring(entry.lastIndexOf(" ")+1, entry.length());
      
      int filterSize = Integer.parseInt(filter);
      
      try {
            Scanner inputFile = new Scanner(new FileInputStream(filename));
            PrintWriter outputFile = new PrintWriter(new FileOutputStream(output));
            ArrayList<Double> x = new ArrayList<Double>();
            int counter = 0;
            String data = null;
            while(inputFile.hasNextLine()){
                 data = inputFile.nextLine();
                 if (counter == 0){
                    counter++;
              
                 }
                 
                 else if (counter == 1000000){
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
             ArrayList<Double> finalAnswer = medianFilter(x, filterSize);
             float time = toc();
             
             for (int a = 0; a < finalAnswer.size(); a++){
                 outputFile.println(a+" "+finalAnswer.get(a));
             }
             
             System.out.println("Parallel took "+ time +" seconds");

             
             
       }
       
       catch(FileNotFoundException e){
            System.out.println("File not found");
       }
   }



}