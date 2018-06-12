package com.jms.cleanse.bean

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jms.cleanse.config.RobotConfig

/**
 * Created by zhoukan on 2018/5/17.
 * @desc: 自定义的指令
 *
 */

interface RosCommand

enum class MSG_TYPE(val s: String) {

    motor_onoff("motor_onoff"),
    disinfection_task_exec("disinfection_task_exec"),
    scheduled_task_exec("scheduled_task_exec"),
    scheduled_task_cancel("scheduled_task_cancel")

}

/**
 *  统一的命令
 */
data class CustomCommand(val msg_type: MSG_TYPE, val rosCommand: RosCommand,val uuid:String = RobotConfig.current_uuid)

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


/*{feedback: {mode: 1, name: t1}, msg_type: disinfection_task_exec}*/

data class FeedBack(val mode:Int,val name:String)

data class Task(var msg_type:String,val feedback:FeedBack)

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



