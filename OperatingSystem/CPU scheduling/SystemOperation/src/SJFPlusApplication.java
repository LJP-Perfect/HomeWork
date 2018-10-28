import entity.Job;
import util.QueueUtil;

import java.util.*;

/**
 * Description:
 * Date:2018/10/23
 *
 * @author:Lee
 */
public class SJFPlusApplication {

    private static LinkedList<Job> staticJobQueue=new LinkedList<>();
    private static int jobAmount=4;
    private static int nowTime=-1;
    private static int endTime=0;
    //现在到下一个进程到来的时间
    private static int nextJobReachTime=0;
    //正在执行的进程ID
    private static int runingProcessId=0;
    //存储当前可以执行的进程
    private static Map<Integer,Job> reachableJob=new HashMap<>();
    //上一个执行的进程ID
    private static Integer lastProcessId=1;

    public static void main(String[] args) {
        endTime= QueueUtil.initQueue(staticJobQueue,jobAmount);
        List<Integer> jobPriority=QueueUtil.getJobPriorityByTime(staticJobQueue);

        while(++nowTime<=endTime){
            System.out.println("现在时间为:"+nowTime);
            if(nowTime==endTime){
                System.out.println("    Process "+lastProcessId+"执行完成 ✔");
                break;
            }
            if(nextJobReachTime>1){
                System.out.println("    Process "+lastProcessId+"正在执行");
                nextJobReachTime--;
                continue;
            }
            if(staticJobQueue.size()>0&&nowTime>=staticJobQueue.element().getArriveTime()){
                reachableJob.put(staticJobQueue.element().getProcessId(),staticJobQueue.poll());
            }
            Integer highestJobIndex=QueueUtil.getHighestIndexFromReachJobs(reachableJob,jobPriority);
            if(staticJobQueue.size()>0){

                nextJobReachTime=(staticJobQueue.element().getArriveTime()-nowTime)>reachableJob.get(highestJobIndex).getRestBrustTime()
                                    ?reachableJob.get(highestJobIndex).getRestBrustTime()
                                    :(staticJobQueue.element().getArriveTime()-nowTime);
            }else{
                if(reachableJob.get(highestJobIndex)!=null){
                    nextJobReachTime=reachableJob.get(highestJobIndex).getRestBrustTime();
                }
            }
            reachableJob.get(highestJobIndex).setRestBrustTime(reachableJob.get(highestJobIndex).getRestBrustTime()-nextJobReachTime);
            if(nowTime!=0){
                if (reachableJob.get(lastProcessId).getRestBrustTime()>0){
                    System.out.println("    Process "+lastProcessId+"中断执行 🛑");
                }else{
                    System.out.println("    Process "+lastProcessId+"执行完成 ✔");
                    reachableJob.remove(lastProcessId);
                }
            }
            runingProcessId=highestJobIndex;
            lastProcessId=runingProcessId;
            System.out.println("    Process "+runingProcessId+"开始执行");

        }
    }
}
