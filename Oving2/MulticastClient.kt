import java.net.MulticastSocket
import java.net.DatagramPacket
import java.net.InetAddress

fun main(){
   val port:Int = 5664
   val ip: String = "224.224.224.224"
   val socket: MulticastSocket = MulticastSocket(port)
   val group: InetAddress = InetAddress.getByName(ip)
   socket.joinGroup(group)
   var keepGoing: Boolean = true

   println("Joined group on ip-address: $ip and port $port")

   while(keepGoing){
      val buffer: ByteArray = ByteArray(400)
      val packet: DatagramPacket = DatagramPacket(buffer, buffer.size)
      socket.receive(packet)
      val message: String = String(buffer)
      if("Exit".equals(message.trim{it <= ' '})) keepGoing = false
      println(message)
   }

   socket.leaveGroup(group)
   socket.close()
}
