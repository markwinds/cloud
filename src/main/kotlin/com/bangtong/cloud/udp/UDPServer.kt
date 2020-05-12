package com.bangtong.cloud.udp

import com.bangtong.cloud.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.Router
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.annotation.Transformer
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.integration.ip.dsl.Udp
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter
import org.springframework.messaging.Message


@Configuration
class UDPServer {

    private var reply:String = "null"

    @Autowired
    private val userRepository: UserRepository? = null
    @Autowired
    private val boxIfoRepository: BoxIfoRepository? = null
    @Autowired
    private val orderRepository: OrderRepository? = null
    @Autowired
    private val boxStatusRepository: BoxStatusRepository? = null

    @Bean
    fun udpIn(): UnicastReceivingChannelAdapter? {
        val adapter = UnicastReceivingChannelAdapter(8800) //实例化一个udp 4567端口
        adapter.setOutputChannelName("udp")
        return adapter
    }

    @Transformer(inputChannel = "udp", outputChannel = "udpString")
    fun transformer(message: Message<*>): String? {
        return String((message.payload as ByteArray)) //把接收的数据转化为字符串
    }

    @Router(inputChannel = "udpString")
    fun routing(message: String): String? {
        println("udp received:$message")
        reply = parseData(message) // 解析数据
        return if (reply == "null") { //当接收数据包含数字1时
            "udpRoute1"
        } else {
            reply+='\n'
            "udpRoute2"
        }
    }

    @ServiceActivator(inputChannel = "udpRoute1")
    fun doNothing(message: String) {
    }

    @ServiceActivator(inputChannel = "udpRoute2",outputChannel = "out")
    fun udpMessageHandle(message: String):String {
        print("replay:$reply")
        return reply
    }

    @Bean
    fun udpOut(): IntegrationFlow? {
        return IntegrationFlows.from("out")
                .handle(Udp.outboundAdapter("headers['ip_packetAddress']")
                        .socketExpression("@udpIn.socket"))
                .get()
    }

    private fun parseData(data:String):String{
        when(data[0]){
            '@' -> {
                val res = data.split('#')
                val boxId = res[1].toLong()
                when(res[0]){
                    "@LockEnable" ->{
                        return if (lock(boxId,true)) "true" else "false"
                    }
                    "@CheckLock" ->{
                        return if (lock(boxId,false)) "true" else "false"
                    }
                    "@UnlockEnable" ->{
                        return if (unlock(boxId,true)) "true" else "false"
                    }
                    "@CheckUnlock" ->{
                        return if (unlock(boxId,false)) "true" else "false"
                    }
                }
            }
            '!' ->{
                val boxId = data.substring(1).toLong()
                return if(getLockStatus(boxId)) "true" else "false"
            }
            else ->{
                var dataOK = true
                val res = data.split('#')
                val boxId = res[0]
                val temperature = res[1]
                var lon = res[2].substring(0,res[2].length-1) // y
                var lat = res[3].substring(0,res[3].length-1)
                if (res[2][0] == '0' || res[2][0] == '0' || temperature[0] == '0'){
                    dataOK = false
                }
                if (res[2].last() != 'E'){
                    lon = "-$lon"
                }
                if (res[3].last() != 'N'){
                    lat = "-$lat"
                }
                if (dataOK){
                    saveBoxIfo(boxId.toLong(),lat.toDouble(),lon.toDouble(),temperature.toInt())
                }
                return "null"
            }
        }
        return "null"
    }

    fun lock(boxId:Long,enable:Boolean):Boolean{
        val boxStatus = getBoxStatusByBoxId(boxId)
        return if (enable){
            if (!boxStatus.status){
                boxStatus.lockt = true
                boxStatusRepository!!.save(boxStatus)
                true
            }else false
        }else{
            boxStatus.lockt = false
            boxStatusRepository!!.save(boxStatus)
            boxStatus.status
        }
    }

    fun unlock(boxId:Long,enable:Boolean):Boolean{
        val boxStatus = getBoxStatusByBoxId(boxId)
        return if (enable){
            if (boxStatus.status){
                boxStatus.unlockt = true
                boxStatusRepository!!.save(boxStatus)
                true
            }else false
        }else{
            boxStatus.unlockt = false
            boxStatusRepository!!.save(boxStatus)
            !boxStatus.status
        }
    }

    fun getBoxStatusByBoxId(boxId:Long): BoxStatus {
        val result = boxStatusRepository!!.findById(boxId)
        val boxStatus: BoxStatus?
        if (result.isEmpty){
            boxStatus = BoxStatus(boxId,0,false, unlockt = false, status = false)
            boxStatusRepository.save(boxStatus)
        }else{
            boxStatus = result.get()
        }
        return boxStatus
    }

    fun saveBoxIfo(boxId:Long,x:Double,y:Double,temperature:Int){
        boxIfoRepository!!.save(BoxIfo(1,boxId,System.currentTimeMillis(),x,y,temperature))
    }

    fun getLockStatus(boxId:Long):Boolean{
        val boxStatus = getBoxStatusByBoxId(boxId)
        return boxStatus.status
    }

}









//    @Bean
//    fun udpOut(): IntegrationFlow? {
//        return IntegrationFlows.from("udpOut")
//                .handle(Udp.outboundAdapter("headers['ip_packetAddress']")
//                        .socketExpression("@udpIn.socket"))
//                .get()
//    }
//
//    @Bean
//    //@ServiceActivator(inputChannel = "udpOut")
//    fun handler(): UnicastSendingMessageHandler? {
//        return UnicastSendingMessageHandler(address, 11111)
//    }