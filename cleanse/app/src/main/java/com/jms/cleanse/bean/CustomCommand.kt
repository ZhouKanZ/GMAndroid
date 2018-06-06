package com.jms.cleanse.bean

import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * Created by zhoukan on 2018/5/17.
 * @desc: 自定义的指令
 *
 */

interface RosCommand

enum class MSG_TYPE(val s: String) {

    Motor_OnOFF("motor_onoff"),
    TASK_EXEC("task_exec"),
    Scheduled_Task_Exec("scheduled_task_exec"),
    Scheduled_Task_Cancel("scheduled_task_cancel")

}

/**
 *  统一的命令
 */
data class CustomCommand(val msg_type: MSG_TYPE, val rosCommand: RosCommand)

/**
 * 上位机状态
 */
data class MotorOnOff(var state: String) : RosCommand

/**
 * 任务执行
 */
data class TaskExecution(var taskName: String) : RosCommand

/**
 *  预约任务的执行
 */
data class ClockTaskExeC(var taskName: String) : RosCommand

/**
 *  预约任务的取消
 */
data class ClockTaskCancel(var taskName: String) : RosCommand

/**
 *  点完成情况
 */
data class PointCompletion(var pointName:String,var img:String,var cleased:Boolean)

/**
 *  任务记录
 */
data class TaskRecoder(var taskName:String,var startTime:String,var endTime:String,var taskPointState:ArrayList<PointCompletion>?)

/**
 *  拼接自定义的命令
 */
fun appendCustomCommand(rosCommand: RosCommand, macAddress: String, msg_type: MSG_TYPE): String {

    var jb = JsonObject()
    jb.addProperty("message_type","custom_data")
    jb.addProperty("data",Gson().toJson(CustomCommand(msg_type, rosCommand)))
    jb.addProperty("robot_mac_address",macAddress)

    return jb.toString()
}



