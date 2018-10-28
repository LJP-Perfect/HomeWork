import entity.Job;
import util.QueueUtil;

import java.util.LinkedList;

/**
 * Description:
 * Date:2018/10/23
 *
 * @author:Lee
 */
public class FCFSApplication {

    private static LinkedList<Job> activeJobQueue=new LinkedList<>();
    private static LinkedList<Job> staticJobQueue=new LinkedList<>();
    private static int jobAmount=4;
    private static int excuteJobAmount=0;
    private static int nowTime=-1;
    private static int endTime=0;
    private static int nextJobReachTime=0;
    private static int runingProcessId=0;

    public static void main(String[] args) {
        endTime=QueueUtil.initQueue(staticJobQueue,jobAmount);

        Runnable dispatchRunable=new Runnable() {
            @Override
            public void run() {
                //判定静态就绪队列是否为空
                while(staticJobQueue.size()>0){
                    //现在的时间与下一个到达进程的到达时间进行比较
                    if(nowTime>=staticJobQueue.element().getArriveTime()) {
                        synchronized (activeJobQueue){
                            //先进先出
                            Job job = staticJobQueue.poll();
                            activeJobQueue.add(job);
                            System.out.println("***进程调度***\n" + "Process " + job.getProcessId() + "调入主存中\n");
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        Runnable excuteRunable=new Runnable() {
            @Override
            public void run() {
                //因为是事先初始化好所有进程，所以根据已执行完进程数与总进程数来判定是否结束此线程
                while(excuteJobAmount<jobAmount){
                    if(activeJobQueue.size()>0){
                        //当前时间是否大于下一个就绪队列中可执行进程
                        if(nowTime>=nextJobReachTime) {
                            synchronized (activeJobQueue) {
                                Job job = activeJobQueue.poll();
                                System.out.println("---进程执行---" );
                                if(runingProcessId>0){
                                    System.out.println("Process " + runingProcessId + "执行完成");
                                }
                                System.out.println("Process " + job.getProcessId() + "开始执行\n");
                                excuteJobAmount++;
                                nextJobReachTime += job.getBrustTime();
                                runingProcessId=job.getProcessId();
                            }
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Runnable timeRunable=new Runnable() {
            @Override
            public void run() {
                while(nowTime<=endTime){
                    if(nowTime>=0){
                        System.out.println("########################");
                    }
                    nowTime++;
                    System.out.println("现在的时间:"+nowTime);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread dispatchThread=new Thread(dispatchRunable);
        Thread excuteThread=new Thread(excuteRunable);
        Thread timeThread=new Thread(timeRunable);
        timeThread.start();
        dispatchThread.start();
        excuteThread.start();

    }
}
