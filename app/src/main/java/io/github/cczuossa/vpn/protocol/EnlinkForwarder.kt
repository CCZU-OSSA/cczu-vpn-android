package io.github.cczuossa.vpn.protocol

import io.github.cczuossa.vpn.utils.log
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.concurrent.thread

class EnlinkForwarder(
    fileDescriptor: FileDescriptor,
    val proxyIn: EnlinkDataInputStream,// vpn入口
    val proxyOut: EnlinkDataOutputStream// vpn出口
) {

    private var vpnOut = FileOutputStream(fileDescriptor)// tun出口
    private var vpnIn = FileInputStream(fileDescriptor)// tun入口
    var desc = fileDescriptor// tun接口
    private var writer = false// proxy => tun
    private var reader = false// tun => proxy
    var status = false// 全局状态

    fun start() {
        status = true
        reader()
        writer()
    }

    private fun writer() {
        if (writer) return
        thread {
            // 从代理到vpn
            runCatching {
                while (status && desc.valid()) {
                    runCatching {
                        val data = proxyIn.readData()
                        if (data.isNotEmpty()) {
                            //val packet = Packet(ByteBuffer.wrap(data))
                            //println("read packet: $packet")
                            vpnOut.write(data, 0, data.size)
                        }
                    }.onFailure {
                        EnlinkVPN.socket.close()
                        //it.printStackTrace()
                    }
                }
                "status: $status,desc: ${desc.valid()}".log()
                writer = false
            }.onFailure {
                it.printStackTrace()
                writer = false
            }

        }
        writer = true
    }


    private fun reader() {
        if (reader) return
        thread {
            runCatching {
                // 从vpn到代理
                // 一次读取2048字节
                var temp = ByteArray(2048)
                var read = 0
                while (status && desc.valid()) {
                    runCatching {
                        read = vpnIn.read(temp)
                        if (read > 0) {// 有读取到有效字节
                            //val packet = Packet(ByteBuffer.wrap(temp))
                            // 转发到vpn
                            proxyOut.writeData(temp, read)
                        }
                    }.onFailure {
                        EnlinkVPN.socket.close()
                        //it.printStackTrace()
                    }
                }
                "status: $status,desc: ${desc.valid()}".log()
                reader = false
            }.onFailure {
                it.printStackTrace()
                reader = false
            }
        }
        reader = true
    }

    fun stop() {
        status = false
    }

    fun update(fileDescriptor: FileDescriptor) {
        desc = fileDescriptor
        vpnIn = FileInputStream(fileDescriptor)
        vpnOut = FileOutputStream(fileDescriptor)
    }


}