import java.net.DatagramSocket
import java.net.DatagramPacket
import java.net.InetAddress

fun main(){
   val socket: DatagramSocket = DatagramSocket()
   val group: InetAddress = InetAddress.getByName("224.224.224.224")
   val port: Int = 5664
   
   
   var keepGoing: Boolean = true
   while(keepGoing){
      print("\nEnter a message to be multicasted (Exit to quit multicasting): ")
      val message:String = readLine()!!
      if("Exit".equals(message.trim())) keepGoing = false
      val buffer: ByteArray = message.toByteArray()
      socket.send(DatagramPacket(buffer, buffer.size, group, port))
   }

}
