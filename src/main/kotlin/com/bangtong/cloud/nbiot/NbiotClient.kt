package com.bangtong.cloud.nbiot

import com.bangtong.cloud.controller.UserController

class NbiotClient(controller: UserController) :Runnable{

	private val clientAdapter = ClientAdapter()
	private val clientCallbackAdapter = ClientCallbackAdapter(controller)

	override fun run() {
		clientAdapter.setUsrCloudMqttCallback(clientCallbackAdapter)
		clientCallbackAdapter.clientAdapter = clientAdapter
		clientAdapter.Connect("k0oz6091", "zbt123456789")
	}
}