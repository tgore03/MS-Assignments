
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tgore03
 */
public class NaiveBayesTG{
    
    //wordlist - dictionary
    //label_map - hashmap
    //train_label - vector
    //train_data - matrix
    //test_label - vector
    //test_data - matrix
    
    static Map<Integer, String> dict = null;        //Starts with 1 index
    static Map<Integer, String> label_map = null;   //Starts with 1 index
    static int totalwords=0;
    static int totaltraindocs = 11269;
    static int totaltestdocs = 7505;
    static int totalclass = 20;
    static int[] train_label = new int[totaltraindocs+1];   //Starts with 1 index
    static int[] test_label = new int[totaltestdocs+1];     //Starts with 1 index
    static float[] prior = new float[totalclass+1];         //Starts with 1 index
    
    static int[] nj = new int[totalclass+1];                //number of words in class wj
    static int[] docsperclass = new int[totalclass+1];      //Number of documents in each class
    static int[] testdocsperclass = new int[totalclass +1]; //Number of testing documents in each class
    static int[][] nk;                                      //Number of times word k appears in all docs of class wj
    static double pmle[][]; 
    static double pbe[][];
    static boolean test_stats_collected = false;
    
    public static void main(String args[]){
        System.out.println("");
        if(args.length != 6){
            System.out.println("Incorrect number of arguments");
            System.exit(1);
        }
        
        //Training Phase
        getDict(args[0]);
        getLabelMap(args[1]);
        getTrainLabel(args[2]);
        calculatePrior();
        getTrainData(args[3]);
        calculatePMLEandPBE();
        
        //Testing Phase
        getTestLabel(args[4]);
        estimateTrainingAccuracy(args[3]); //Calculate accuracy on training data
        estimateTestingAccuracy(args[5], "BE");
        estimateTestingAccuracy(args[5], "MLE");
    }
    
    static void getDict(String path){
        BufferedReader br = null;
        
        try{
            File file = new File(path);
            br = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException fe){
            System.err.println("File not found at " + path);
        }
        
        dict = new HashMap<Integer, String>();
        int i=1;
        String word;
        try{
            while((word = br.readLine()) != null){
                dict.put(i++, word.trim());
                totalwords++;
            }
        }catch(IOException ioe){
            System.err.println(ioe);
        }
        
//        for(i=1; i<=10; i++){
//            System.out.println(dict.get(i));
//        }
//        System.out.println("");
    }
    
    static void getLabelMap(String path){
        BufferedReader br = null;
        
        try{
            File file = new File(path);
            br = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException fe){
            System.err.println("File not found at " + path);
        }
        
        label_map = new HashMap<Integer,String>();
        String line;
        try{
            while((line = br.readLine()) != null){
                String[] att = line.split(",");
                label_map.put(Integer.parseInt(att[0]), att[1].trim());
            }
        }catch(IOException ioe){
            System.err.println(ioe);
        }
        
//        for(int i = 1; i<=label_map.size(); i++){
//            System.out.println(label_map.get(i));
//        }
//        System.out.println("");
    }
    
    static void getTrainLabel(String path){
        BufferedReader br = null;
        
        try{
            File file = new File(path);
            br = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException fe){
            System.err.println("File not found at " + path);
        }
        
        int i=1;
        String word;
        try{
            while((word = br.readLine()) != null){
                train_label[i++] = Integer.parseInt(word.trim());
            }
        }catch(IOException ioe){
            System.err.println(ioe);
        }
        
//        for(i=0; i<10; i++){
//            System.out.print("train label: " +train_label[i]+" ");
//        }
//        System.out.println("");
        
    }
    
    static void getTestLabel(String path){
        BufferedReader br = null;
        
        try{
            File file = new File(path);
            br = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException fe){
            System.err.println("File not found at " + path);
        }
        
        int i=1;
        String word;
        try{
            while((word = br.readLine()) != null){
                test_label[i++] = Integer.parseInt(word.trim());
            }
        }catch(IOException ioe){
            System.err.println(ioe);
        }
        
//        for(i=0; i<10; i++){
//            System.out.println(train_label[i]);
//        }
//        System.out.println("");
    }
    
    static void calculatePrior(){
        //calculate # of label i / Total labels
        for (int i=1; i<train_label.length; i++){
            prior[train_label[i]] += 1;
        }
        
        System.out.println("\n**************************************************\n");
        for(int i=1;i<prior.length;i++){
            prior[i] = prior[i]/totaltraindocs;
            System.out.println("P(Omega = "+i +") = "+ String.format("%.3g", prior[i]));
        }
        System.out.println("");
        //System.out.println(Arrays.toString(prior));
    }
    
    static void getTrainData(String 
            path){
        BufferedReader br = null;
        
        try{
            File file = new File(path);
            br = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException fe){
            System.err.println("File not found at " + path);
        }
        
        String line;
        nk = new int[totalwords+1][totalclass+1];
        
        try{
            while((line = br.readLine()) != null){
                String[] att = line.split(",");
                int docid = Integer.parseInt(att[0]);
                int wordid = Integer.parseInt(att[1]);
                int count = Integer.parseInt(att[2]);
                
                int label = train_label[docid];
                
                //nj - total number of words in doc of class wj
                nj[label] += count;
                
                //nk - number of times word k occurs in all documents in class wj
                nk[wordid][label] += count;
            }
        }catch(IOException ioe){
            System.err.println(ioe);
        }
        
//        System.out.println("nj:");
//        System.out.println(Arrays.toString(nj));
//        System.out.println("nk:");
//        for(int i=0; i<=10;i++){
//            System.out.println(Arrays.toString(nk[i]) +" ");
//        }
//        System.out.println("");
    }
    
    static void calculatePMLEandPBE(){
        pmle = new double[totalwords+1][totalclass+1]; 
        pbe = new double[totalwords+1][totalclass+1];
        for(int k=1; k<=totalwords;k++){
            for(int j=1; j<=totalclass; j++){
                pmle[k][j] = (float)nk[k][j]/nj[j];
                pbe[k][j] = (float)(nk[k][j]+1) / (nj[j]+totalwords);
            }
        }
        
//        System.out.println("");
//        for(int k=1; k<=10; k++){
//             for(int j=1; j<=20; j++)
//                 System.out.printf("%.2f ", pmle[k][j]*1000000);
//             System.out.println("");
//         }
//         System.out.println("");
//        
//        for(int k=1; k<=10; k++){
//            for(int j=1; j<=20; j++)
//                System.out.printf("%.2f ", pbe[k][j]*1000000);
//            System.out.println("");
//        }
//        System.out.println("");
    }
    
    static void estimateTrainingAccuracy(String path){
        BufferedReader br = null;
        try{
            File file = new File(path);
            br = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException fe){
            System.err.println("File not found at " + path);
        }
        
        String line;
        String positions = "";
        int docid=0;
        //int[] doclabel = new int[totaltraindocs+1]; //Starts with 1
        int[][] confusionmatrix = new int[totalclass+1][totalclass+1];
        int correctlyclassified=0;
        
        System.out.println("\n**************************************************\n");
        
        //Predict Label on Training Data
        try{
            while((line = br.readLine()) != null){
                String[] att = line.split(",");
                
                if(docid ==0){
                    docid = Integer.parseInt(att[0]);
                    positions = att[1]; 
                } else if(docid != Integer.parseInt(att[0])){
                    //Predict label for the document.
                    int label = predictlabelBE(positions);
                    
                    //obtain accuracy measure
                    if(label == train_label[docid]){
                        correctlyclassified++;
                    }
                    
                    //Get number of documents per class
                    docsperclass[train_label[docid]]+=1;
                    
                    //Store in ConfusionMatrix
                    confusionmatrix[train_label[docid]][label]+=1;
                    //doclabel[docid] = label;
                    
                    //prep vars for next iteration
                    docid = Integer.parseInt(att[0]);
                    positions = att[1];
                } else {
                    positions = positions + "," + att[1];
                }
            }
        }catch(IOException ioe){
            System.err.println(ioe);
        }
        
        
        //Estimate accuracy of training on Training data
        System.out.println("Overall Accuracy = " + (float)correctlyclassified/totaltraindocs);
        System.out.println("Class Accuracy:");
        for(int i=1; i<=totalclass; i++){
            System.out.println("Group "+ i +": "+ (float)confusionmatrix[i][i]/docsperclass[i]);
        }
        
        System.out.println("\n Confusion Matrix: ");
        for(int i=1; i<=totalclass; i++){
            for(int j=1; j<=totalclass; j++)
                System.out.printf("%4d",confusionmatrix[i][j]);
            System.out.println("");
        }
        System.out.println("");
    }
    
    static void estimateTestingAccuracy(String path, String method){
        BufferedReader br = null;
        try{
            File file = new File(path);
            br = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException fe){
            System.err.println("File not found at " + path);
        }
        
        String line;
        String positions = "";
        String testwordcount = "";
        int docid=0;
        int label=0;
        int[][] confusionmatrix = new int[totalclass+1][totalclass+1];
        int correctlyclassified=0;
        
        
        System.out.println("\n**************************************************\n");
        
        //Predict Label on Training Data
        try{
            while((line = br.readLine()) != null){
                String[] att = line.split(",");
                
                if(docid ==0){
                    docid = Integer.parseInt(att[0]);
                    positions = att[1]; 
                    testwordcount = att[2];
                } else if(docid != Integer.parseInt(att[0])){
                    //Predict label for the document.
                    if(method.contentEquals("BE"))
                        label = predictlabelBE(positions);
                    else if(method.contentEquals("MLE"))
                        label = predictLabelMLE(positions, testwordcount);
                    else
                        System.out.println("Incorrect parameter method");
                    
                    //obtain accuracy measure
                    if(label == test_label[docid]){
                        correctlyclassified++;
                    }
                    //System.out.println(label +"  |  "+ test_label[docid]);
                    
                    //Get number of documents per class
                    if(!test_stats_collected)
                        testdocsperclass[test_label[docid]]+=1;
                    
                    //Store in ConfusionMatrix
                    confusionmatrix[test_label[docid]][label]+=1;
                    //doclabel[docid] = label;
                    
                    //prep vars for next iteration
                    docid = Integer.parseInt(att[0]);
                    positions = att[1];
                    testwordcount = att[2];
                } else {
                    positions = positions + "," + att[1];
                    testwordcount = testwordcount +","+ att[2];
                }
            }
        }catch(IOException ioe){
            System.err.println(ioe);
        }
        test_stats_collected = true;
        
        //Estimate accuracy of training on Training data
        System.out.println("Using "+ method +" for classification \n");
        System.out.println("Overall Accuracy = " + (float)correctlyclassified/totaltestdocs);
        System.out.println("Class Accuracy:");
        for(int i=1; i<=totalclass; i++){
            System.out.println("Group "+ i +": "+ (float)confusionmatrix[i][i]/testdocsperclass[i]);
        }
        
        System.out.println("\n Confusion Matrix: ");
        for(int i=1; i<=totalclass; i++){
            for(int j=1; j<=totalclass; j++)
                System.out.printf("%4d",confusionmatrix[i][j]);
            System.out.println("");
        }
        System.out.println("");
    } 
    
    static int predictlabelBE(String positions){
        double sum, maxsum=0.0;
        int maxclass=0;
        
        //Get the positions
        String[] att = positions.split(",");
        
        for(int j=1; j<=totalclass; j++){
            sum=0.0;
            for(int i=0; i<att.length; i++){
                //Calculate pieof P(xi|wj) using BE
                int wordid = Integer.parseInt(att[i]);
                sum = sum + Math.log(pbe[wordid][j]); 
            }
            sum = sum + Math.log(prior[j]);
            
            //Get the label when the product is max
            if(maxsum == 0.0 || sum>maxsum){
                maxsum = sum;
                maxclass = j;
            }
        }
        return maxclass;
    }
    
    static int predictLabelMLE(String positions, String testwordcount){
        double sum, maxsum=Integer.MIN_VALUE;
        int maxclass=1;
        
        //Get the positions
        String[] att = positions.split(",");
        String[] attcount = testwordcount.split(",");
        
        for(int j=1; j<=totalclass; j++){
            sum=0.0;
            maxsum=Integer.MIN_VALUE;
            for(int i=0; i<att.length; i++){
                //Calculate pieof P(xi|wj) using BE
                int wordid = Integer.parseInt(att[i]);
                int wordcount = Integer.parseInt(attcount[i]);
                sum = sum + Math.log(pmle[wordid][j])*wordcount; 
            }
            sum = sum + Math.log(prior[j]);
            
            //Get the label when the product is max
            if(sum>maxsum){
                maxsum = sum;
                maxclass = j;
            }
        }
        return maxclass;
    }
}
