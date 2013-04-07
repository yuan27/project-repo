import java.util.*;
import java.io.*;
//Huffman coding tree for part 1b
//read input as array of bytes
public class HCTree{
    //CONSTANTS  
    final int DIFF=128;//difference between int value and byte value
    final int MAXSIZE=256;
    
    HCNode root;
    //a.Variables
    int occTable[];//SIZE:MAXSIZE
    BCoder bc;
    String msg;
    String codeTable[];
    //b.Variables
    int intres=-2;
    
    //DEBUGGING
    final boolean printOCCTable=false;
    final boolean printHuffTree=false;
    
    /*3 LOCATIONS TO CHANGE IN SIMPLE VERSION
     * 1. in main, change the filename;
     * 2. above, change NOZERO=true;
     * 3. in reconstructTree(), change HMAC;
     */
    
    
    public static void main(String[] args){
        //construct a tree
        HCTree hct=new HCTree("HCT/huff_test_mssg.txt","HCT/test_key.txt");
        System.out.println(hct.getBFSTree());
        //test a tree
        /*HCTree hct=new HCTree("HCT/1b_dirty.txt","HCT/test_key.txt",true);
         System.out.println(hct.getResult());*/
        /*HCTree hct=new HCTree("HC_clean_t.txt","HCT/test_key.txt",true);
         System.out.println(hct.getResult());*/
    }
    
    
    /*********************************for partA use*********************************/ 
    public HCTree(String msgFile,String keyFile){
        bc=new BCoder(keyFile);
        codeTable=new String[MAXSIZE];
        msg=bc.getAllInput(msgFile);
        
        constructOccTable();
        
        constructDefaultTree();
        reconstructTree();
        
        constructCode();
    }
    
    
    public void constructDefaultTree(){
        //get the priority queue
        PriorityQueue<HCNode> queue=new PriorityQueue<HCNode>(256);
        
        for(int i=0;i<MAXSIZE;i++){
            HCNode nnode=new HCNode(""+(char)(i-DIFF),occTable[i], null);
            queue.add(nnode);
        }
        
        //constructing the Huffman Tree
        HCNode n1=null,n2=null;
        while(((n2=queue.poll())!=null) && ((n1=queue.poll())!=null)){
            HCNode nnode=new HCNode(n1.str+n2.str,n1.occ+n2.occ, null);
            nnode.left=n1;nnode.right=n2;
            n1.parent=nnode;n2.parent=nnode;
            queue.add(nnode);
        }
        if(n1==null&&n2==null){
            System.out.println("Something went wrong when constructing the Huffman tree.");
        }
        if(n1!=null){
            root=n1;
        }else{
            root=n2;
        }
    }
    
    public void reconstructTree(){
        String hmac=bc.calcHMAC(msg);
        LinkedList<HCNode> list=new LinkedList<HCNode>();
        list.add(root);
        HCNode node,temp;
        
        int count=0;
        while(list.size()>0){
            node=list.getFirst();
            if(node.left==null && node.right==null){
                list.removeFirst();
                continue;
            }
            if(hmac.charAt((count++)%256)=='0'){//swap left and right child
                temp=node.left;
                node.left=node.right;
                node.right=temp;
            }
            if(node.left!=null)list.addLast(node.left);
            if(node.right!=null)list.addLast(node.right);
            list.removeFirst();
        }
    }
    
    //construct the whole tree
    public void constructCode(){
        //encode the tree here
        if(root==null)return;
        LinkedList<HCNode> list=new LinkedList<HCNode>();
        root.code="";
        list.add(root);
        HCNode node;
        while(list.size()>0){
            node=list.getFirst();
            if(node.left==null && node.right==null){
                codeTable[(byte)node.str.charAt(0)+DIFF]=node.code;
            }
            if(node.left!=null){
                node.left.code=node.code+"0";
                list.addLast(node.left);
            }
            if(node.right!=null){
                node.right.code=node.code+"1";
                list.addLast(node.right);
            }
            list.removeFirst();
        } 
    }
    
    public String encodeMessage(){
        byte[] barr=msg.getBytes();
        int len=barr.length;
        String codedmsg="";
        for(int i=0;i<len;i++){
            codedmsg=codedmsg+codeTable[barr[i]+DIFF];
        }
        return codedmsg;
    }
    
    /*********************************for partB use*********************************/ 
    public String getResult(){
        if(intres==1){
            return "Modified.";
            
        }else if(intres==0){
            return "Unmodified.";
        }
        return "Modified.";     
    }
    
    public HCTree(String treeFile,String keyFile,boolean dummy){
        bc=new BCoder(keyFile);
        codeTable=new String[MAXSIZE];
        for(int i=0;i<MAXSIZE;i++){
            codeTable[i]="x";
        }
        TreeFileTriplet tri=bc.getTreeStructure(treeFile);
        
        boolean success=constructTreeFromTriplet(tri);
        
        intres=verify(success,tri);
    }
    
    //Constrution of TREE from file
    public int verify(boolean success,TreeFileTriplet tri){
        if(!success){
            return -1;
        }
        
        if(!decodeMessage(tri.C)){
            return -1;
        }
        constructOccTable();
        constructTreeOCC();
        
        String mark="";
        mark=obtainMark();
        
        
        String hmac1=mark;
        String hmac2=bc.calcHMAC(msg).substring(0,255);
        
        //System.out.printf("%s %s\n",hmac1,hmac2);
        
        if(hmac1.equals(hmac2)){
            return 0;
        }
        return 1;
    }
    
    public boolean constructTreeFromTriplet(TreeFileTriplet tri){
        String treeStructure=tri.A;
        int[] values=tri.B;
        int values_len=values.length;
        
        root=new HCNode("", "", null);
        LinkedList<HCNode> list=new LinkedList<HCNode>();
        list.add(root);
        HCNode node;
        int count=0,maxcount=treeStructure.length()-1,childNum=0;
        while(count<=maxcount){
            node=list.getFirst();
            if(treeStructure.charAt(count)=='1'){//is a parent
                HCNode left=new HCNode("",node.code+"0",node);
                HCNode right=new HCNode("",node.code+"1",node);
                node.left=left;node.right=right;
                list.addLast(left);list.addLast(right);
            }else{//is a child
                byte val=(byte)values[(childNum++)%values_len];
                node.str=""+(char)val;
                codeTable[val+DIFF]=node.code;
            }
            list.removeFirst();
            count++;
        }
        if(values_len!=childNum){
            return false;
        }
        return true;
    }
    
    public boolean decodeMessage(String amsg){
        String cmsg="";
        int pos=0;
        String temp=new String(amsg);
        boolean found=true;
        while(temp.length()>0 && found){
            found=false;
            for(int i=0;i<MAXSIZE;i++){
                if(temp.startsWith(codeTable[i])){
                    cmsg=cmsg+(char)(i-DIFF);
                    temp=temp.substring(codeTable[i].length());
                    found=true;
                    break;
                }
            }
        }
        if(temp.length()!=0)
            return false;
        msg=cmsg;
        return true;
    }
    
    public void constructTreeOCC(){
        constructNodeOCC(root);
        constructNodeStr(root);
    }
    
    public void constructNodeStr(HCNode node){
        if(node.str.length()!=0){
            return;
        }
        if(node.left==null && node.right==null){
            return;
        }
        constructNodeStr(node.left);
        constructNodeStr(node.right);
        
        int val=node.left.compareTo(node.right);
        
        if(val==0){
            System.out.println("Something impossible occured");
        }
        if(val>=0){
            node.str=node.left.str+node.right.str;
        }else{
            node.str=node.right.str+node.left.str;
        }
    }
    
    public void constructNodeOCC(HCNode node){
        if(node.occ>=0){
            return;
        }
        if(node.left==null && node.right==null){
            node.occ=occTable[(byte)node.str.charAt(0)+DIFF];
        }else{
            constructNodeOCC(node.left);
            constructNodeOCC(node.right);
            node.occ=node.left.occ+node.right.occ;
            
        }
    }
    
    public String obtainMark(){
        if(root==null)return "";
        LinkedList<HCNode> list=new LinkedList<HCNode>();
        list.add(root);
        HCNode node;
        String mark="";
        while(list.size()>0){
            node=list.getFirst();
            if(node.left!=null || node.right!=null){
                int val=node.left.compareTo(node.right);
                if(val==0){
                    System.out.println("Something impossible occured");
                }
                if(val>=0){
                    mark=mark+"1";
                }else{
                    mark=mark+"0";
                }
            }
            if(node.left!=null){list.addLast(node.left);}
            if(node.right!=null){list.addLast(node.right);}
            list.removeFirst();
        }
        
        return mark;
    }
    
    
    /*********************************for common use*********************************/
    
    
    
    //construct occTable
    public void constructOccTable(){
        occTable=new int[MAXSIZE];
        byte[] bytearr=msg.getBytes();
        int len=bytearr.length;
        for(int i=0;i<len;i++){
            int index=(bytearr[i]+DIFF);
            occTable[index]+=1;
        }
    }
    
    
    //get the string concatenation of the whole tree
    public String getTreeString(){
        if(root==null)return "";
        
        String treestr="";
        LinkedList<HCNode> list=new LinkedList<HCNode>();
        list.add(root);
        HCNode node;
        while(list.size()>0){
            node=list.getFirst();
            if(node.left==null || node.right==null){//is child
                treestr=treestr+node.code;
            }
            if(node.left!=null){list.addLast(node.left);}
            if(node.right!=null){list.addLast(node.right);}
            list.removeFirst();
        }
        return treestr;
    }
    
    
    
    
    
    public class HCNode implements Comparable<HCNode>{
        public HCNode parent,left,right;
        public int occ=-1;
        public String str;
        public String code;
        
        public HCNode(String str, int occ, HCNode p){
            this.occ=occ;
            this.str=str;
            parent=p;left=right=null;
        }
        
        public HCNode(String str, String code, HCNode p){
            this.code=code;
            this.str=str;
            parent=p;left=right=null;
        }
        public int compareTo(HCNode n1) {
            if(n1.occ!=occ){
                return (occ-n1.occ);
            }
            if(n1.str.length()!=str.length()){
                return str.length()-n1.str.length();
            }
            return (byte)str.charAt(0)-(byte)n1.str.charAt(0);
        }
        
    }
    
    public String getBFSTree(){
        if(root==null)return "";
        
        String treeStructure="";
        String values="";
        
        LinkedList<HCNode> list=new LinkedList<HCNode>();
        list.add(root);
        HCNode node;
        int j=0;
        while(list.size()>0){
            node=list.getFirst();
            if(node.left==null && node.right==null){//is a child
                treeStructure=treeStructure+"0";
                values=values+(byte)node.str.charAt(0)+"\n";
            }else{
                treeStructure=treeStructure+"1";
            }
            if(node.left!=null)list.addLast(node.left);
            if(node.right!=null)list.addLast(node.right);
            list.removeFirst();
        }
        String codedmsg=encodeMessage();
        return (treeStructure+"\n"+values+codedmsg);
    }
    
    
    
    
    
    /*********************************for debug use*********************************/
    public void printTable(){
        int sum=0;
        for(int i=0;i<MAXSIZE;i++){
            if(occTable[i]!=0){
                sum+=occTable[i];
                System.out.printf("%d:%d\n",i-DIFF,occTable[i]);
            }
        }
        System.out.printf("SUM:%d\n",sum);
    }
    
    public void printTree(){
        printNode(root);
    }
    
    public void printNode(HCNode node){
        if(node==null)
            return;
        System.out.printf("[%s,%d]\n",node.str,node.occ);
        if(node.left!=null){
            System.out.printf("LEFT:\n");
            printNode(node.left);
        }
        if(node.right!=null){
            System.out.printf("RIGHT:\n");
            printNode(node.right);
        }
    }
}