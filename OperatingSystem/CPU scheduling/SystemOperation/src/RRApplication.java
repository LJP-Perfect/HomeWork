import entity.Job;
import util.QueueUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * 轮转时间片算法
 * Date:2018/10/24
 *
 * @author:Lee
 */
public class RRApplication {

    //活动就绪队列
    private static LinkedList<Job> activeJobQueue=new LinkedList<>();
    //静止就绪队列
    private static LinkedList<Job> staticJobQueue=new LinkedList<>();
    private static int jobAmount=4;
    private static int nowTime=-1;
    private static int endTime=0;
    //现在到下一个进程到来的时间
    private static int nextJobReachTime=0;
    //正在执行的进程ID
    private static int runingProcessId=0;
    //上一个执行的进程ID
    private static Integer lastProcessId=1;
    private static Integer timeSlice=2;
    //提前完成的标识，若提前完成为true
    private static boolean aheadFlag=false;

    public static void main(String[] args) {
        endTime= QueueUtil.initQueue(staticJobQueue,jobAmount);
        activeJobQueue=new LinkedList<>(staticJobQueue);

        while(++nowTime<=endTime) {
            System.out.println("现在时间为: "+nowTime);
            if(nowTime==endTime){
                System.out.println("    Process "+runingProcessId+"执行完成 ✔");
                break;
            }
            if(nextJobReachTime>1){
                nextJobReachTime--;
                System.out.println("    Process "+runingProcessId+"正在执行");
                continue;
            }else if(nextJobReachTime==1){
                if (aheadFlag){
                    System.out.println("    Process "+runingProcessId+"提前执行完成，调度到队首进程");
                    aheadFlag=false;
                }else{
                    System.out.println("    Process "+runingProcessId+"中断完成，调度到队尾");
                }
            }
            //从活动就绪队列取出队首进程
            Job excutingJob=activeJobQueue.poll();
            //进程剩余执行时间与时间片比较
            //如果时间片内执行不完，进程剩余时间-时间片，并且将此进程放入队尾
            //时间片内提前执行完，则结束当前片，取出队首进程。
            if(excutingJob.getRestBrustTime()>timeSlice){
                nextJobReachTime=timeSlice;
                excutingJob.setRestBrustTime(excutingJob.getRestBrustTime()-timeSlice);
                activeJobQueue.add(excutingJob);
            }else{
                nextJobReachTime=excutingJob.getRestBrustTime();
                aheadFlag=true;
            }
            runingProcessId=excutingJob.getProcessId();
            System.out.println("    Process "+runingProcessId+"开始执行");
        }
    }
}
