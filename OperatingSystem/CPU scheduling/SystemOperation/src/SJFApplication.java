import entity.Job;
import util.QueueUtil;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Description:
 * Date:2018/10/23
 *
 * @author:Lee
 */
public class SJFApplication {

    private static LinkedList<Job> activeJobQueue=new LinkedList<>();
    private static LinkedList<Job> staticJobQueue=new LinkedList<>();
    private static int jobAmount=4;
    private static int excuteJobAmount=0;
    private static int nowTime=-1;
    private static int endTime=0;
    private static int nextJobReachTime=0;
    private static int runingProcessId=0;
    private static Integer optimalIndex=0;

    public static void main(String[] args) {

        endTime= QueueUtil.initQueue(staticJobQueue,jobAmount);

        Runnable dispatchRunable=new Runnable() {
            @Override
            public void run() {
                while(staticJobQueue.size()>0){
                    if(nowTime>=staticJobQueue.element().getArriveTime()) {
                            synchronized (activeJobQueue){
                                optimalIndex=QueueUtil.selectOptimalJob(staticJobQueue,nextJobReachTime);
                                if(staticJobQueue.get(optimalIndex).getArriveTime()<=nowTime){
                                    Job job = staticJobQueue.get(optimalIndex);
                                    staticJobQueue.remove((int)optimalIndex);
                                    activeJobQueue.add(job);
                                    System.out.println("***进程调度***\n" + "Process " + job.getProcessId() + "调入主存中\n");
                                }
                            }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        Runnable excuteRunable=new Runnable() {
            @Override
            public void run() {
                while(excuteJobAmount<jobAmount){
                    if(activeJobQueue.size()>0){
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
                        Thread.sleep(500);
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
