package DijkstraBank;

import java.util.*;

/**
 * Description:
 * Dijkstra 银行家算法
 * @Param MaxMatrix            :最大需求矩阵
 * @Param AllocationMatrix     :分配矩阵
 * @Param NeedMatrix           :需求矩阵
 * @Param Available            :可利用资源向量
 * @Param AllResources         :总资源向量
 *
 * 设Request是某个进程P的请求向量。
 * （1）如果Request<Need,转向步骤（2）。否则因为它所需要的资源数已超过它所宣布的最大值出错。
 * （2）如果Request<Available,转向步骤（3）。否则因为它尚无足够资源出错。
 * （3）系统试探性分配给进程P，并修改Available、AllocationMatrix、NeedMatrix的数据。
 * （4）执行安全性算法，如果安全，则完成本次分配。如果不安全，则作废本次分配，回滚到原来的数据资源分配状态
 * Date:2018/11/16
 *
 * @author:Lee
 */
public class Application {
    private static final int rows=5;
    private static final int cols=3;
    //不可修改的初始化参数，为了重复初始化可修改的初始化参数
    private static final int[][] InitMaxMatrix=new int[][]{
            {7,5,3},{3,2,2},{9,0,2},{2,2,2},{4,3,3}
    };
    private static final int[][] InitAllocationMatrix=new int[][]{
            {0,1,0},{2,0,0},{3,0,2},{2,1,1},{0,0,2}
    };
    private static final int[][] InitNeedMatrix=new int[][]{
            {7,4,3},{1,2,2},{6,0,0},{0,1,1},{4,3,1}
    };
    private static final int[] InitAvailable=new int[]{3,3,2};
    private static final int[] InitAllResources=new int[]{10,5,7};
    //可修改的初始化参数
    private static int[][] MaxMatrix=new int[rows][cols];
    private static int[][] AllocationMatrix=new int[rows][cols];
    private static int[][] NeedMatrix=new int[rows][cols];
    private static int[] Available=new int[cols];
    private static int[] AllResources=new int[cols];
    //存储进程的状态，true表示已执行
    private static Map<Integer,Boolean> processIsDoneMap=new HashMap<>();
    //存储安全序列
    private static LinkedList<Integer> securityList=new LinkedList<>();
    public static void main(String[] args) {
        initAll();
        int[] requestRes=new int[cols];
        int requestProcessId;
        Scanner in=new Scanner(System.in);
        //初始状态
        boolean isSecurity=isSecurity();
        System.out.println("初始状态下:");
        printTable();
        printInfo(isSecurity);
        initAll();
        System.out.print("请依次输入分配的进程ID 和各类资源数:");
        while (in.hasNext()){
            requestProcessId=in.nextInt();
            for (int i=0;i<cols;i++){
                requestRes[i]=in.nextInt();
            }
            Tentative_Distribution(requestProcessId,requestRes);
            System.out.println("试探性分配状态下:");
            printTable();
            isSecurity=isSecurity();
            if(!isSecurity){
                rollBack(requestProcessId,requestRes);
            }
            printInfo(isSecurity);
            System.out.print("是否重新分配一次(0=NO,1=YES):");
            int flag=in.nextInt();
            if(flag==0){
                break;
            }
            initAll();
            System.out.print("请依次输入分配的进程ID 和各类资源数:");
        }



    }

    //打印资源分配表
    private static void printTable() {
        System.out.println("---------------资源分配表---------------");
        System.out.println("Process         Max             Allocation              Need                Available");
        System.out.println("            A   B   C           A   B   C           A   B   C               A   B   C");
        for (int i=0;i<rows;i++) {
            System.out.format("P%d          ",i);
            System.out.format("%d   %d   %d           ",MaxMatrix[i][0],MaxMatrix[i][1],MaxMatrix[i][2]);
            System.out.format("%d   %d   %d           ",AllocationMatrix[i][0],AllocationMatrix[i][1],AllocationMatrix[i][2]);
            System.out.format("%d   %d   %d               ",NeedMatrix[i][0],NeedMatrix[i][1],NeedMatrix[i][2]);
            if(i==0){
                System.out.format("%d   %d   %d",Available[0],Available[1],Available[2]);
            }
            System.out.println();
        }
        System.out.println();
    }

    //分配失败后回滚
    private static void rollBack(int requestProcessId, int[] requestRes) {
        for (int i=0;i<cols;i++){
            Available[i]+=requestRes[i];
            NeedMatrix[requestProcessId][i]+=requestRes[i];
            AllocationMatrix[requestProcessId][i]-=requestRes[i];
        }
    }

    //试探性分配
    private static void Tentative_Distribution(int requestProcessId, int[] requestRes) {
        for (int i=0;i<cols;i++){
            Available[i]-=requestRes[i];
            NeedMatrix[requestProcessId][i]-=requestRes[i];
            AllocationMatrix[requestProcessId][i]+=requestRes[i];
        }
    }

    //打印安全信息
    private static void printInfo(boolean isSecurity) {
        if (isSecurity){
            System.out.print("✔所以进程已执行完成，当前系统是安全的,安全序列为:");
            while (securityList.size()>0){
                System.out.print(securityList.pop()+" ");
            }
            System.out.println();
            System.out.println();
        }else {
            System.out.println("❌当前系统不安全,无安全序列");
            System.out.println();
        }
    }

    //初始化全部参数
    private static void initAll() {
        for (int i=0;i<rows;i++){
            processIsDoneMap.put(i,false);
        }
        securityList.clear();
        for (int i=0;i<rows;i++){
            for (int j=0;j<cols;j++){
                MaxMatrix[i][j]=InitMaxMatrix[i][j];
                AllocationMatrix[i][j]=InitAllocationMatrix[i][j];
                NeedMatrix[i][j]=InitNeedMatrix[i][j];
            }
        }
        for (int m=0;m<cols;m++){
            Available[m]=InitAvailable[m];
            AllResources[m]=InitAllResources[m];
        }
    }

    //判断系统是否为安全
    private static boolean isSecurity() {
        //判断当前进程是否满足条件执行
        boolean executeFlag=true;
        //isDone判断是否执行完所以进程
        boolean isDone=false;
        while (!isDone){
            int currentFinishNum=securityList.size();
            for(int i=0;i<rows;i++){
                executeFlag=true;
                if(processIsDoneMap.get(i)){
                    continue;
                }
                for (int j=0;j<cols;j++){
                    if (NeedMatrix[i][j]>Available[j]){
                        executeFlag=false;
                        //System.out.format("进程 P%d 不符合资源申请条件\n",i);
                        break;
                    }
                }
                if(executeFlag){
                    //System.out.format("进程 P%d 符合资源申请条件,执行\n",i);
                    for (int m=0;m<cols;m++){
                        Available[m]+=AllocationMatrix[i][m];
                    }
                    processIsDoneMap.put(i,true);
                    securityList.add(i);
                }
            }
            //判断是否执行完所有进程
            isDone=isEquals(Available,AllResources);
            if (currentFinishNum==securityList.size()){
                break;
            }
        }



        return isDone;
    }

    //初始化进程执行Map
    private static void initProcessIsDoneMap(Map<Integer, Boolean> processIsDoneMap) {
        for (int i=0;i<rows;i++){
            processIsDoneMap.put(i,false);
        }
    }


    public static boolean isEquals(int[] a, int[] b) {
        if(a == null || b == null) {
            return false;
        }
        if(a.length != b.length) {
            return false;
        }
        for(int i = 0; i < a.length; i++) {
            if(a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }
}
