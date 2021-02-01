import java.net.MulticastSocket
import java.net.DatagramPacket
import java.net.InetAddress

fun main(){
   val port:Int = 5664
   val socket: MulticastSocket = MulticastSocket(port)
   val group: InetAddress = InetAddress.getByName("224.224.224.224")
   socket.joinGroup(group)
   var keepGoing: Boolean = true

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
