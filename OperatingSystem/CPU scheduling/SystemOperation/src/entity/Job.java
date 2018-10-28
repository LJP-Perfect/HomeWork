package entity;

/**
 * Description:
 * Date:2018/10/23
 *
 * @author:Lee
 */
public class Job {

    private Integer processId;
    private Integer arriveTime;
    private Integer waitTime;
    private Integer brustTime;
    //剩余执行时间
    private Integer restBrustTime;
    //数字越大，优先级越高
    private Integer priority;



    public Job() {
    }

    public Job(Integer processId, Integer arriveTime, Integer waitTime, Integer brustTime,Integer priority) {
        this.processId = processId;
        this.arriveTime = arriveTime;
        this.waitTime = waitTime;
        this.brustTime = brustTime;
        this.priority=priority;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Job{" +
                "processId=" + processId +
                ", arriveTime=" + arriveTime +
                ", waitTime=" + waitTime +
                ", brustTime=" + brustTime +
                ", restBrustTime=" + restBrustTime +
                '}';
    }

    public Integer getRestBrustTime() {
        return restBrustTime;
    }

    public void setRestBrustTime(Integer restBrustTime) {
        this.restBrustTime = restBrustTime;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Integer getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Integer arriveTime) {
        this.arriveTime = arriveTime;
    }

    public Integer getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(Integer waitTime) {
        this.waitTime = waitTime;
    }

    public Integer getBrustTime() {
        return brustTime;
    }

    public void setBrustTime(Integer brustTime) {
        this.brustTime = brustTime;
    }

}
