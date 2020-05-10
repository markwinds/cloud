package com.bangtong.cloud.nbiot

import cn.usr.UsrCloudMqttClientAdapter
import org.eclipse.paho.client.mqttv3.MqttException

class ClientAdapter : UsrCloudMqttClientAdapter() {
	@Throws(MqttException::class)
	override fun Connect(userName: String?, passWord: String?) {
		super.Connect(userName, passWord)
		print("Connected to device\n")
	}

}