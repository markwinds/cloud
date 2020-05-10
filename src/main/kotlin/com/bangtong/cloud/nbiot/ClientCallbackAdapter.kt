package com.bangtong.cloud.nbiot

import cn.usr.UsrCloudMqttCallbackAdapter
import com.bangtong.cloud.controller.UserController

class ClientCallbackAdapter(val controller: UserController) : UsrCloudMqttCallbackAdapter() {

	var clientAdapter:ClientAdapter? = null

	override fun onConnectAck(returnCode: Int, description: String) {
		super.onConnectAck(returnCode, description)
		when(returnCode){
			0 -> {
				print("Connect successful\n")
			}
			1 -> {
				print("Disconnected\n")
			}
			2 -> {
				print("Connected to server\n")
				clientAdapter!!.SubscribeForDevId("863866040038465")
				//val data = byteArrayOf(0x00, 0x01, 0x02)
				//clientAdapter!!.publishForDevId("863866040038465", data)
			}
			4 -> {
				print("Disconnected from service\n")
			}
		}
	}
	/** 订阅回调*/
	override fun onSubscribeAck(messageId:Int, clientId:String, topics:String, returnCode:Int) {
		super.onSubscribeAck(messageId, clientId, topics,returnCode)
		when(returnCode){
			0 ->{
				print("Subscribe successful\n")
			}
			1 ->{
				print("Subscribe failed\n")
			}
		}
	}
	override fun onReceiveEvent(messageId: Int, topic: String?, data: ByteArray?) {
		super.onReceiveEvent(messageId, topic, data)
		var dataOK = true
		val dataString = clientAdapter!!.USRToolBytesToUtf8str(data)
		print("Received: $dataString\n")
		val res = dataString.split("#")
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
			controller.saveBoxIfo(boxId.toLong(),lat.toDouble(),lon.toDouble(),temperature.toInt())
		}
	}

}