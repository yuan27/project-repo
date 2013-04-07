
import java.util.*;
import java.io.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class BCoder{
    //DEBUGGING
    final boolean printPlainTextAndHash=false;
    final boolean printHMAC=false;
    final boolean printInput=false;
    //CONSTANTS  
    final int DIFF=128;//difference between int value and byte value
    //key and message
    byte[] key;
    Mac mac;
    //FUNCTIONS
    /*Constructor with msgfile and keyfile specified.*/
    //public Coder(String msgFile,String keyFile)
    /*Calculate hmac based on a string concatenation*/
    //public String calcHMAC(String msgStr);
    
    public static void main(String[] args){
        BCoder bc=new BCoder("BST/test_key.txt"); 
        int[] msg=bc.getIntInput("BST/bst_test_data.txt");
        System.out.println(bc.getHMACstring(msg).substring(0,256));
        
    }
    
    
    /*Major Functions*/
    //COnstructor
    public BCoder(String keyFile){
        key=getByteInput(keyFile);
        
        SecretKeySpec speckey = new SecretKeySpec(key,"HmacSHA256");
        try{
            mac = Mac.getInstance("HmacSHA256");
            mac.init(speckey);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    //Calculate HMAC from a string
    public String calcHMAC(String msgStr){
        byte[] byteArr=msgStr.getBytes();
        byte[] h3=mac.doFinal(byteArr);
       
        String hmac=getBinStr(h3);
        if(printHMAC){
            System.out.println("HMAC in Binary: "+hmac);
        }
        return hmac;   
    }
    
    
    //Get all strings from a file
    public String getAllInput(String fileName){   
        try {
            FileInputStream file = new FileInputStream(fileName);
            if (file == null) {
                System.out.printf("  Error reading the file %s.\n",fileName);
                System.exit(0);
            }
            byte b;
            String str="";
            while((b=(byte)file.read())!=-1){
                str=str+(char)b;
            }
            file.close();
            return str;
        }catch(java.io.IOException e){
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    //Get an array of bytes from a file
    public byte[] getByteInput(String fileName){   
        String text=getAllInput(fileName);
        byte[] byteArr=text.getBytes();
        if(printInput){
            System.out.println();
            System.out.printf("input - %s: ",fileName);
            for(int i=0;i<byteArr.length;i++){
                if(i==0){
                    System.out.printf("[%b",byteArr[i]);
                }else if (i==(byteArr.length-1)){
                    System.out.printf(", %b]\n",byteArr[i]);
                }else{
                    System.out.printf(", %b",byteArr[i]);
                }
            }
        }
        return byteArr;
    }
    
    //Get an array of integers from a file
    public int[] getIntInput(String fileName){   
        try {
            FileInputStream file = new FileInputStream(fileName);
            Scanner in=new Scanner(file);
            ArrayList<Integer> text = new ArrayList<Integer>();
            if (file == null || in==null) {
                System.out.printf("  Error reading the file %s.\n",fileName);
                System.exit(0);
            }
            while (in.hasNextInt()) {
                text.add(in.nextInt());
            }
            file.close();
            in.close();
            int[] origMsg=getIntPrimitive(text);
            if(printInput)
                System.out.printf("input - %s: %s\n",fileName,text.toString());
            return origMsg;
        }catch(java.io.IOException e){
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    //Get an concatenation of data from a file
    public String getConcatOfData(int[] data){   
        String text="";
        int len=data.length;
        for(int i=0;i<len;i++){
            text=text+data[i];
        }
        return text;
    }
    
    public String getHMACstring(int[] msg){
        int msglen=msg.length;
        int len=(int)Math.ceil(Math.log(msglen)/Math.log(2));
        String[] hmacarr=new String[len+1];
        String catstr="";
        
        //construct original strings
        for(int i=0;i<len+1;i++){
            hmacarr[i]="";
        }
        for(int j=0;j<msglen;j++){
            hmacarr[len]=hmacarr[len]+msg[j];
            for(int i=0;i<len;i++){
                if(((j+1)&(1<<i))!=0){
                    hmacarr[i]=hmacarr[i]+msg[j];
                }
            }
        }
        
        //convert them to hmac

       
        for(int i=0;i<len+1;i++){
            hmacarr[i]=calcHMAC(hmacarr[i]);
        }
        
        //get the concatenation string
        catstr=hmacarr[len];
        for(int i=0;i<len;i++){
            catstr=catstr+hmacarr[i];
        }
        //catstr=catstr+hmacarr[len];
        return catstr;
    }
    
    
    
    public TreeFileTriplet getTreeStructure(String treeFile){
        String a="",c="";
        int[] b=null;
        try {
            FileInputStream file = new FileInputStream(treeFile);
            Scanner in=new Scanner(file);
            ArrayList<Integer> values = new ArrayList<Integer>();
            if (file == null || in==null) {
                System.out.printf("  Error reading the file %s.\n",treeFile);
                System.exit(0);
            }
            a=in.next();

            while(in.hasNextInt()){
                values.add(in.nextInt());
            }
            if(in.hasNext()){
                c=in.next();
            }
            b=getIntPrimitive(values);
            file.close();
            in.close();
        }catch(java.io.IOException e){
            e.printStackTrace();
            System.exit(0);
        }
        return new TreeFileTriplet(a,b,c);
    }
    
    
    
    /*Helper functions*/
    
    //1. Get primitive type array from an arraylist of int
    public int[] getIntPrimitive(ArrayList<Integer> alist){
        int len=alist.size();
        int[] list=new int[len];
        for(int i=0;i<len;i++){
            list[i]=alist.get(i).intValue();
        }
        return list;
    }
    
    //2. Get the result of "or"ing two byte arrays, arr2 has size 64
    public byte[] orOfByteArrays(byte[] arr1,byte[] arr2){
        System.out.printf("%d %d\n",arr1.length,arr2.length);
        
        int len1=arr1.length,len2=arr2.length,len=(len1>len2)?len1:len2;
        
        boolean onegreater=len1>len2;
        byte arr[]=new byte[len];
        for(int i=0;i<len;i++){
            if(onegreater){
                if(i>=len2){
                    arr[i]=(byte)arr1[i]; 
                }else{
                    arr[i]=(byte)(arr1[i%len1] | arr2[i%len2]);
                }
            }else{
                if(i>=len1){
                    arr[i]=(byte)arr2[i]; 
                }else{
                    arr[i]=(byte)(arr1[i%len1] | arr2[i%len2]);
                } 
            }
            
        }
        return arr;
    }
    
    
    //3. Get the result of "xor"ing two byte arrays 
    public byte[] xorOfByteArrays(byte[] arr1,byte[] arr2){
        if(arr1.length!=arr2.length){
            System.out.println("WARNING: key length not equal to 64.");
        }
        int len1=arr1.length,len2=arr2.length,len=(len1>len2)?len1:len2;
        byte arr[]=new byte[len];
        for(int i=0;i<len;i++){
            arr[i]=(byte)(arr1[i%len1] ^ arr2[i%len2]);
        }
        return arr;
    }
    
    //4. Get binary string of a byte array
    public String getBinStr(byte[] arr){
        String str="",temp;
        for(int i=0;i<arr.length;i++){
            temp=String.format("%8s", Integer.toBinaryString(arr[i] & 0xFF)).replace(' ', '0');
            str=str+temp;
        }
        return str;
    }
    
    //5. Get the number of 0's in a tring
    int numberOfZero(String str){
        int slen=str.length(),count=0;
        for(int i=0;i<slen;i++){
            if(str.charAt(i)=='0')
                count++;
        }
        return count;
    }
    
    
    //6. Get the mark for a specific number
    public String getMarkForInt(int count, int num){
        String temp=Integer.toBinaryString(num);
        int len=temp.length();
        if(len<count){
            for(int i=0;i<count-len;i++){
                temp="0"+temp;           
            }
        }
        return temp;
    }
    
}