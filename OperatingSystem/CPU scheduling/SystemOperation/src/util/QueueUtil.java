package util;

import entity.Job;

import java.util.*;

/**
 * Description:
 * Date:2018/10/23
 *
 * @author:Lee
 */
public class QueueUtil {

    /*
     * 初始化静态就绪队列
     */
    public static int initQueue(LinkedList<Job> jobQueue,int jobAmount){
        int endTime=0;
        //到达时间
        int[] arriveTimeArray={0,2,4,6};
        //执行时间
        int[] burstTimeArray={3,3,4,3};
        //优先级，用于高优先级算法
        int[] priorityArray={2,4,5,3};
        for(int i=0;i<jobAmount;i++){
            Job job=new Job(i+1,arriveTimeArray[i],null,burstTimeArray[i],priorityArray[i]);
            job.setRestBrustTime(burstTimeArray[i]);
            jobQueue.add(job);
            endTime+=burstTimeArray[i];
        }
        //返回所有进程执行完所需的时间
        return endTime;
    }

    /*
     * 选择队列中时间最短的作业，返回作业ID（简单版本，即一个进程过程中不会被中断）
     */
    public static Integer selectOptimalJob(LinkedList<Job> jobQueue,int expectFinishTime){
        int size=jobQueue.size();
        Integer optimalIndex=null;
        Integer minBurstTime=null;
        for(int i=0;i<size;i++){
            if(jobQueue.get(i).getArriveTime()<=expectFinishTime){
                if(minBurstTime==null) {
                    minBurstTime = jobQueue.get(i).getBrustTime();
                    optimalIndex = i;
                }else if(minBurstTime>jobQueue.get(i).getBrustTime()){
                    minBurstTime = jobQueue.get(i).getBrustTime();
                    optimalIndex = i;
                }
            }
        }
        if(optimalIndex==null){
            for(int i=0;i<size;i++){
                if(minBurstTime>jobQueue.get(i).getBrustTime()){
                    minBurstTime = jobQueue.get(i).getBrustTime();
                    optimalIndex = i;
                }
            }
        }
        return optimalIndex;
    }

    /*
     * 根据短作业原则，返回就绪队列作业的优先权队列。抢占式
     * 优先权队列：队列位置越前，优先权越高
     */
    public static List<Integer> getJobPriorityByTime(LinkedList<Job> staticJobQueue) {
        LinkedList<Job> jobQueue=new LinkedList<>(staticJobQueue);
        List<Integer> jobPriority=new LinkedList<>();
        while(jobQueue.size()>0){
            //getHighestPriorityjobsByTime()，每次获取jobQueue优先级最高的进程，并且在jobQueue删除
            for (Integer jobId:getHighestPriorityjobsByTime(jobQueue)){
                jobPriority.add(jobId);
                removeJobByJobId(jobQueue,jobId);
            }
        }
        return jobPriority;
    }
    /*
     * 根据队列和作业ID删除队列中的作业
     */
    public static void removeJobByJobId(LinkedList<Job> queue,Integer jobId){
        try{
            for(Job job:queue){
                if (job.getProcessId().equals(jobId)){
                    queue.remove(job);
                }
            }
        }catch (Exception e){
        }
    }
    /*
     * 根据最短
     */
    public static List<Integer> getHighestPriorityjobsByTime(LinkedList<Job> jobQueue){
        List<Integer> jobIndexList=new ArrayList<>();
        int minBrustTime=Integer.MAX_VALUE;
        int minArriveTime=Integer.MAX_VALUE;
        for(Job job:jobQueue){
            if(minBrustTime>job.getBrustTime()){
                minBrustTime=job.getBrustTime();
            }
        }
        for(Job job:jobQueue){
            if(job.getBrustTime()==minBrustTime){
                if(minArriveTime>job.getArriveTime()){
                    minArriveTime=job.getArriveTime();
                }
            }
        }
        for (Job job:jobQueue){
            if(job.getBrustTime()==minBrustTime&&job.getArriveTime()==minArriveTime){
                jobIndexList.add(job.getProcessId());
            }
        }
        return jobIndexList;
    }




    /*
     * 优先级调度，获取最高优先级的作业ID
     */
    public static Integer getHighestPriorityJobByPriority(Map<Integer,Job> reachableJob){
        Integer tempPriority=-1;
        Integer perfectId=null;

        for (Integer id:reachableJob.keySet()){
            if(reachableJob.get(id).getPriority()>tempPriority&&reachableJob.get(id).getRestBrustTime()>0){
                tempPriority=reachableJob.get(id).getPriority();
                perfectId=id;
            }else if(reachableJob.get(id).getPriority().equals(tempPriority)){
                if(reachableJob.get(id).getArriveTime()<reachableJob.get(perfectId).getArriveTime()
                        &&reachableJob.get(id).getRestBrustTime()>0){
                    tempPriority=reachableJob.get(id).getPriority();
                    perfectId=id;
                }
            }
        }
        return perfectId;
    }


    /*
     * 根据可执行作业队列中和优先级队列，获取最高优先级作业的ID
     */
    public static Integer getHighestIndexFromReachJobs(Map<Integer,Job> reachableJob, List<Integer> jobPriority) {
        for(int i=0;i<jobPriority.size();i++){
            for (Integer id:reachableJob.keySet()){
                if(reachableJob.get(id).getRestBrustTime()>0&&id.equals(jobPriority.get(i))){
                    return id;
                }
            }
        }
        return null;
    }


}
