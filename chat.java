import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

class Chat {

    // Función que envía un mensaje a un grupo multicast a través de una IP y un
    // Puerto
    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }

    // Función que recibe un mensaje que alguien envió a un grupo multicast
    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }

    static class Worker extends Thread {
        public void run() {
            try {
                MulticastSocket socket = new MulticastSocket(10000);
                InetAddress grupo = InetAddress.getByName("239.10.10.10"); // Obtenemos el grupo
                socket.joinGroup(grupo); // Nos unimos al grupo
                for (;;) {
                    byte[] mensaje = recibe_mensaje_multicast(socket, 256);
                    System.out.println(new String(mensaje, "UTF-8"));
                }
            } catch (Exception e) {
                System.out.println("Ha habido un error");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        new Worker().start();
        try {

            Scanner leer = new Scanner(System.in);
            String nombre = args[0];
            String mensaje;
            String paquete;
            for (;;) {
                System.out.println("Mensaje: ");
                mensaje = leer.nextLine();
                paquete = nombre + " :- " + mensaje;
                byte[] arreglo = paquete.getBytes();
                envia_mensaje_multicast(arreglo, "239.10.10.10", 10000);
            }
        } catch (Exception e) {
            System.out.println("Ha habido un error");
        }
    }
}