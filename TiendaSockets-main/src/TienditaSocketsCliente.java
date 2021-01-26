import java.io.*;
import java.net.Socket;

public class TienditaSocketsCliente {
  //Aqui va a ir la interfaz del usuario
  static final String IP = "localhost";
  static final int PUERTO = 25001;

  public static void main(String[] args) {
    try{
      Socket cl = new Socket(IP, PUERTO);
      System.out.println("Conexion con servidor establecida.. recibiendo datos");

      InputStream is = cl.getInputStream(); //Asociamos el stream con el cliente
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr); //El BufferedReader se usa para leer del cliente al servidor.

      OutputStream os = cl.getOutputStream(); //Asociamos el stream con el cliente
      OutputStreamWriter osr = new OutputStreamWriter(os);
      BufferedWriter bw = new BufferedWriter(osr); //El BufferedWriter se usa para escribir del servidor al cliente.

      //Recibimos el número de artículos disponibles
      int numeroArticulos = br.read();
      System.out.println("Número de articulos: "+ numeroArticulos);

      for(int i=0;i<numeroArticulos;i++){
        //Leemos los datos de los articulos
        String nombre = br.readLine();
        double precio = Double.parseDouble(br.readLine());
        int stock = br.read();
        String imagen = br.readLine();
      }


    } catch(IOException E){
      E.printStackTrace();
    }
  }

}
