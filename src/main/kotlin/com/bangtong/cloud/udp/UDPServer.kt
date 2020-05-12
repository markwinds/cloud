package com.bangtong.cloud.udp

import com.bangtong.cloud.controller.UserController
import java.net.DatagramPacket
import java.net.DatagramSocket

class UDPServer(private val controller: UserController):Runnable {
	private var reply = "null"
	override fun run() {
		println("Server started")
		while (true){
			val socket = DatagramSocket(8800)
			val data = ByteArray(1024)
			val packet = DatagramPacket(data, data.size)
			socket.receive(packet) // 此方法在接收到数据报之前会一直阻塞
			val info = String(data, 0, packet.length)
			println("Received:$info")
			reply = parseData(info) // 解析数据
			if (reply == "null"){ // 不需要服务器应答
				socket.close()
				continue
			}
			println(reply)
			reply += "\n"
			// 1.定义客户端的地址、端口号、数据
			val address = packet.address
			val port = packet.port
			val data2 = reply.toByteArray()
			// 2.创建数据报，包含响应的数据信息
			val packet2 = DatagramPacket(data2, data2.size, address, port)
			// 3.响应客户端
			socket.send(packet2)
			// 4.关闭资源
			socket.close()
		}
	}

	private fun parseData(data:String):String{
		when(data[0]){
			'@' -> {
				val res = data.split('#')
				val boxId = res[1].toLong()
				when(res[0]){
					"@LockEnable" ->{
						return if (controller.lock(boxId,true)) "true" else "false"
					}
					"@CheckLock" ->{
						return if (controller.lock(boxId,false)) "true" else "false"
					}
					"@UnlockEnable" ->{
						return if (controller.unlock(boxId,true)) "true" else "false"
					}
					"@CheckUnlock" ->{
						return if (controller.unlock(boxId,false)) "true" else "false"
					}
				}
			}
			'!' ->{
				val boxId = data.substring(1).toLong()
				return if(controller.getLockStatus(boxId)) "true" else "false"
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
					controller.saveBoxIfo(boxId.toLong(),lat.toDouble(),lon.toDouble(),temperature.toInt())
				}
				return "null"
			}
		}
		return "null"
	}
}