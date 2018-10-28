import entity.Job;
import util.QueueUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Date:2018/10/27
 *
 * @author:Lee
 */
public class HighPriorityApplication {
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
    private static Integer highestJobIndex=1;

    public static void main(String[] args) {
        endTime= QueueUtil.initQueue(staticJobQueue,jobAmount);
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
            //判断静态队列是否为空并且当前时间是否大于静态队列下一个进程的到达时间
            //如果成立，加入可执行队列reachableJob
            if(staticJobQueue.size()>0&&nowTime>=staticJobQueue.element().getArriveTime()){
                reachableJob.put(staticJobQueue.element().getProcessId(),staticJobQueue.poll());
            }
            //根据reachableJob获取到最高优先级进程的在reachableJob的key
            //注：reachableJob数据结构为Map<Integer,Job>,key为进程ID，value为对应进程
            highestJobIndex=QueueUtil.getHighestPriorityJobByPriority(reachableJob);
            runingProcessId=highestJobIndex;
            //获取下一个CPU调度时间节点，如果静态队列为空，则时间节点为当前进程的剩余执行时间
            //如果静态就绪队列不为空，则需要比较当前进程的剩余执行时间和下一个新进程的到达时间，取较小值
            if(staticJobQueue.size()>0){
                nextJobReachTime=(staticJobQueue.element().getArriveTime()-nowTime)>reachableJob.get(highestJobIndex).getRestBrustTime()
                        ?reachableJob.get(highestJobIndex).getRestBrustTime()
                        :(staticJobQueue.element().getArriveTime()-nowTime);
            }else{
                if(reachableJob.get(highestJobIndex)!=null){
                    nextJobReachTime=reachableJob.get(highestJobIndex).getRestBrustTime();
                }
            }

            if(nowTime!=0){
                if (reachableJob.get(lastProcessId).getRestBrustTime()>0){
                    if(!lastProcessId.equals(runingProcessId)){
                        System.out.println("    Process "+lastProcessId+"中断执行 🛑");
                    }
                }else{
                    System.out.println("    Process "+lastProcessId+"执行完成 ✔");
                    reachableJob.remove(lastProcessId);
                }
            }
            //为当前执行进程的剩余执行时间减去到下一个CPU调度时间节点的这段时间（即nextJobReachTime）
            reachableJob.get(highestJobIndex).setRestBrustTime(reachableJob.get(highestJobIndex).getRestBrustTime()-nextJobReachTime);

            if(!lastProcessId.equals(runingProcessId)){
                System.out.println("    Process "+runingProcessId+"开始执行");
            }
            lastProcessId=runingProcessId;

        }
    }
}
