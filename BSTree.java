import java.util.*;

//Balanced Search Tree for part1a.
public class BSTree{
    //DEBUGGING
    final boolean printConstruct=false;
    final boolean printWholeTree=false;
    final boolean printHMACInput=false;
    //Variables
    Node root;
    //Message info
    int[] msg;
    int msglen;
    //Hmac info
    String hmac;
    int hmaclen;
    int pos=0;//position of next char in m to use
    //Bcoder
    BCoder bc;
    
    //results
    boolean modified=false;
    int respin=-1000;//part2
    
    public static void main(String[] args){        
        /************PART1 TEST*************/
        
        //construct a clean tree
        /*BSTree bst=new BSTree(false, "BST/bst_test_data.txt", "BST/test_key.txt");
        System.out.print(bst.getBFSTree());*/
        
        //test a file
        /*BSTree bst=new BSTree("BST/1a_clean.txt", "BST/test_key.txt", false);
        System.out.println(bst.getResult(false));*/
        
        /************PART2 TEST*************/
        
        //construct a clean tree
        /*BSTree bst1=new BSTree(true, "BST/bst_test_data.txt", "BST/test_key.txt");
        System.out.print(bst1.getBFSTree());*/
        
        //test a file
        BSTree bst=new BSTree("BST/2_dirty.txt", "BST/test_key.txt", true);
        System.out.println(bst.getResult(true));
        
    }
    
    /*********************************for part1a use*********************************/ 
    public String getResult(boolean part2){
        if(!part2){
            return (modified)?"Modified.":"Unmodified.";
        }else{
            if(respin>0)
                return "Number modified: "+msg[respin-1]+".\nIndex of the number:(starting from 0) "+(respin-1)+".\n";
            else if (respin==0)
                return "Unmodified.";
            else
                return "Some error occured.\nThe tree was modified.";
        }
    }
    
    public BSTree(boolean part2, String msgFile,String keyFile){
        if(!part2){//for part1a use
            bc=new BCoder(keyFile);
            msg=bc.getIntInput(msgFile);
            msglen=msg.length;
            root=new Node(0,msglen-1,null);
            hmac=bc.calcHMAC(bc.getConcatOfData(msg));
            hmaclen=hmac.length();
            constructTree();
        }else{//for part2 use
            bc=new BCoder(keyFile);
            msg=bc.getIntInput(msgFile);
            msglen=msg.length;
            root=new Node(0,msglen-1,null);
            hmac=bc.getHMACstring(msg);
            hmaclen=hmac.length();
            constructTree();
        }
    }
    
    
    public BSTree(String treeFile, String keyFile, boolean part2){
        if(!part2){
            bc=new BCoder(keyFile);
            TreeFileTriplet tri=bc.getTreeStructure(treeFile);
            boolean success=constructTreeFromTriplet(tri);
            msg=goOverTree();
            msglen=msg.length;
            respin=verify(success,msg,keyFile,false);            
        }else{
            bc=new BCoder(keyFile);
            TreeFileTriplet tri=bc.getTreeStructure(treeFile);
            boolean success=constructTreeFromTriplet(tri);
            msg=goOverTree();
            msglen=msg.length;
            respin=verify(success,msg,keyFile,true);
        }
    }
    
    public void constructTree(){
        LinkedList<Node> list=new LinkedList<Node>();
        list.add(root);
        Node node;
        while(list.size()>0){
            node=list.getFirst();
            constructNode(node);
            if(node.left!=null)list.addLast(node.left);
            if(node.right!=null)list.addLast(node.right);
            list.removeFirst();
        }
    }
    
    public void constructNode(Node node){
        if(node==null)return;
        int n,n2,n4,count,temp,addition,logn2,start;
        n=node.upp-node.low+1;
        if(n==1){node.value=msg[node.low];return;}
        if(n==2){
            node.left=new Node(node.low,node.low,msg[node.low],node);
            node.right=new Node(node.upp,node.upp,msg[node.low],node);
            return;
        }
        n2=(int)Math.ceil(n/2.0);
        n4=(int)Math.ceil(n/4.0);
        count=(int)Math.floor(Math.log(n2)/Math.log(2));
        //get start
        addition=0;
        for(int i=0;i<count;i++){
            temp=(hmac.charAt((pos)%hmaclen)=='0')?0:1;
            pos++;
            addition=2*addition+temp;
        }
        start=n4+addition;
        //split and construct
        if(printConstruct){
            System.out.printf("\n%d\t %d\t %d\t %d\t %d\t %d\t %d\t %d\t\n",pos,n,n2,n4,count,start,node.low,node.upp);
        }
        node.splitLoc=node.low+start;
        Node leftNode=new Node(node.low,node.low+start-1,node);
        Node rightNode=new Node(node.low+start,node.upp,node);
        node.left=leftNode;node.right=rightNode;
        
    }
    

    /*********************************for part2 use*********************************/
    public BSTree(int[] arr, String keyFile){
        bc=new BCoder(keyFile);
        msg=arr;
        msglen=msg.length;
        
        root=new Node(0,msglen-1,null);
        hmac=bc.getHMACstring(msg);
        
        hmaclen=hmac.length();
        
        constructTree();
        if(printHMACInput){
            System.out.println("HMAC: "+hmac);
        }
    }
    
    public int verify(boolean success, int[] arr, String keyFile,boolean part2){
        if(!part2){
            String mark=obtainMark();
            String correct_mark=bc.calcHMAC(bc.getConcatOfData(arr));
            modified=!(mark.substring(0,256).equals(correct_mark));
            return ((modified)?1:0);
        }else{
            if(!success)
                return -1;
            
            String mark=obtainMark();
            String correct_mark=bc.getHMACstring(msg);
            
            String sub1=mark.substring(0,256);
            String sub2=correct_mark.substring(0,256);
            
            if(sub1.equals(sub2)){
                return 0;
            }
           
            return pinpoint(mark,correct_mark);
        }
    }
    
    public int pinpoint(String hmac1, String hmac2){
        int len=(int)Math.ceil(Math.log(msglen)/Math.log(2));
        int val=0;
        String sub1,sub2;
             
        for(int i=1;i<len+1;i++){
            sub1=hmac1.substring(256*i,256*(i+1));
            sub2=hmac2.substring(256*i,256*(i+1));
            if(!(sub1.equals(sub2))){
                
                val+=1<<(i-1);
            }
        }

        return val;
    }
    
    public boolean constructTreeFromTriplet(TreeFileTriplet tri){
        String treeStructure=tri.A;
        int[] values=tri.B;
        int values_len=values.length;

        root=new Node(0,values_len-1,null);
        LinkedList<Node> list=new LinkedList<Node>();
        list.add(root);
        Node node;
        int count=0,maxcount=treeStructure.length()-1,childNum=0;
        while(count<=maxcount){
            node=list.getFirst();
            if(treeStructure.charAt(count)=='1'){//is a parent
                node.isLeaf=false;
                Node left=new Node(0,1,node);
                Node right=new Node(0,1,node);
                node.left=left;node.right=right;
                list.addLast(left);list.addLast(right);
            }else{//is a child
                node.isLeaf=true;
                node.value=values[(childNum++)%values_len];
                node.low=node.upp=789;
            }
            list.removeFirst();
            count++;
        }
        if(values_len!=bc.numberOfZero(treeStructure)){
            return false;
        }
        return true;
    }
    
    public void reconstructTreeFromTriplet(){
        //construct array list to get index for a specific item
        int len=msg.length;
        ArrayList<Integer> intlist=new ArrayList<Integer>(len);
        for(int i=0;i<len;i++){
            intlist.add(new Integer(msg[i]));
        }
        
        LinkedList<Node> list=new LinkedList<Node>();
        list.add(root);
        Node node;
        int count=0,maxcount=len-1;
        while(list.size()>0){
            node=list.getFirst();
            if(node.isLeaf){//is a child
                node.low=intlist.indexOf(node.value);
                
                node.upp=node.low;
            }else{
                list.add(node.left);
                list.add(node.right);
            }
            list.removeFirst();
            count++;
        }
        
    }
    
    
    public void buildNonLeafNodes(){
        reconstructTreeFromTriplet();
        
        if(root==null)return;
        
        LinkedList<Node> list=new LinkedList<Node>();
        list.add(root);
        Node node;
        while(list.size()>0){
            node=list.getFirst();
            if(node.isLeaf){//is a child
                //do nothing
            }else{
                //get node.low
                Node temp=node;
                while(temp.left!=null){
                    temp=temp.left;
                }
                node.low=temp.low;
                //get node.upp
                temp=node;
                while(temp.right!=null){
                    temp=temp.right;
                }
                node.upp=temp.upp;
                
                Node right=node.right;
                if(right!=null){
                    Node curr=right;
                    while(curr.left!=null){
                        curr=curr.left;
                    }
                    node.value=curr.value;
                    node.splitLoc=curr.low;
                }
            }
            if(node.left!=null)list.addLast(node.left);
            if(node.right!=null)list.addLast(node.right);
            list.removeFirst();
        }
    }
    
    public String obtainMark(){
        buildNonLeafNodes();
        
        String mark="";
        
        LinkedList<Node> list=new LinkedList<Node>();
        list.add(root);
        Node node;
        while(list.size()>0){
            node=list.getFirst();
            mark=mark+getMarkAtNode(node);
            if(node.left!=null)list.addLast(node.left);
            if(node.right!=null)list.addLast(node.right);
            list.removeFirst();
        }
        
        return mark;
    }
    
    public String getMarkAtNode(Node node){
        if(node==null)return "";
        int n,n2,n4,count,temp,addition,logn2,start;
        n=node.upp-node.low+1;
        if(n==1 || n==2){//no mark
            return "";}
        n2=(int)Math.ceil(n/2.0);
        n4=(int)Math.ceil(n/4.0);
        count=(int)Math.floor(Math.log(n2)/Math.log(2));//number of bits returned
        
        start=node.splitLoc-node.low;
        addition=start-n4;
        
        return bc.getMarkForInt(count,addition);
    }
    
    
    /*********************************common use*********************************/
    //Node class
    public class Node{
        public Node parent,left,right;
        public int low,upp;
        public int value;
        public int splitLoc;
        public boolean isLeaf=false;
        public Node(int low,int upp,Node p){
            this.low=low;this.upp=upp;this.parent=p;
        }
        public Node(int low,int upp,int value,Node p){
            this.low=low;this.upp=upp;this.value=value;this.parent=p;
        }
        public Node(int value,Node p){
            this.value=low;this.parent=p;
        }
    }
    
    public String getBFSTree(){
        if(root==null)return "";
        
        String treeStructure="";
        String values="";
        
        LinkedList<Node> list=new LinkedList<Node>();
        list.add(root);
        Node node;
        int j=0;
        while(list.size()>0){
            node=list.getFirst();
            if(node.left==null || node.right==null){//is a child
                treeStructure=treeStructure+"0";
                if(values.length()!=0){
                    values=values+"\n"+msg[node.low];
                }else{
                    values=values+msg[node.low];
                }
            }else{
                treeStructure=treeStructure+"1";
            }
            if(node.left!=null)list.addLast(node.left);
            if(node.right!=null)list.addLast(node.right);
            list.removeFirst();
        }
        return (treeStructure+"\n"+values+"\n");
    }
    
    public int[] goOverTree(){
        return goOverNode(root);
    }
    
    public int[] goOverNode(Node node){
        if(node==null)return null;
        if(node.left==null && node.right==null){
            int[] arr=new int[1];arr[0]=node.value;
            return arr;
        }
        int[] arr1=goOverNode(node.left);
        int[] arr2=goOverNode(node.right);
        if(arr1==null)
            return arr2;
        if(arr2==null)
            return arr1;
        int len1=arr1.length,len2=arr2.length;
        int len=len1+len2;
        int[] arr=new int[len];
        for(int i=0;i<len1;i++){
            arr[i]=arr1[i];
        }
        for(int i=0;i<len2;i++){
            arr[len1+i]=arr2[i];
        }
        return arr;
        
        
    }
    
    //DEBUGGING FUNCTION
    public void printTree(Node node){
        if(node==null)
            return;
        System.out.printf("[%d,%d]\n",node.low,node.upp);
        System.out.println("LEFT:\n");
        printTree(node.left);
        System.out.println("RIGHT:\n");
        printTree(node.right);
    }
    
    
}