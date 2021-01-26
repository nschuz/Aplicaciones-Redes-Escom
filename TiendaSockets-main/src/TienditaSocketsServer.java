import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TienditaSocketsServer {
  public static String relativePath = System.getProperty("user.dir"); //Ruta relativa de nuestro directorio
  String stock [] = {"ZAPATOS", "CONSOLAS"};
  
  public static void main(String[] args) {
    final int port = 25001;//Puerto de nuestro Servidor ( puede ser del 1 al 2^16)

    try {
      ServerSocket servidor = new ServerSocket(port); // Escuchamos el servidor en el puerto
      servidor.setReuseAddress(true); //Esto es para que no se congele 15 segundos algo asi xd
      System.out.println("Servidor Iniciado con exito en el puerto: " + servidor.getLocalPort());

      Articulos item1 = new Articulos("1000 recetas de tomate",500,14,0,"/imagenes/1000RecetasDeTomate.jpg");
      Articulos item2 = new Articulos("La biblia del tomate",1000,26,20,"/imagenes/LaBibliaDelTomate.jpg");
      Articulos item3 = new Articulos("Tomate en polvo para rebozar 1kg",80,84,0,"/imagenes/TomateEnPolvoCocinar.jpg");
      Articulos item4 = new Articulos("Tomate en polvo para gelatinas 1kg",50,26,0,"/imagenes/TomateEnPolvoGelatina.jpg");
      Articulos item5 = new Articulos("Tomate entero pelado 250gr",15,55,0,"/imagenes/TomateEnteroPelado.jpg");
      Articulos item6 = new Articulos("Zumo de tomate 2L pack de 3",300,77,50,"/imagenes/ZumoDeTomatejpg.jpg");

      ArrayList<Articulos> items = new ArrayList<Articulos>();
      items.add(item1);
      items.add(item2);
      items.add(item3);
      items.add(item4);
      items.add(item5);
      items.add(item6);

      for(int i=0;i<6;i++){
        System.out.println(items.get(i).getNombre());
      }

      for(;;) {
        Socket cliente = servidor.accept(); // Esperamos a que algun cliente de conecte al servidor.
        System.out.println("Un cliente se ha conectado: " + cliente);

        /* Para leer en el socket cliente */
        InputStream is = cliente.getInputStream(); //Asociamos el stream con el cliente
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr); //El BufferedReader se usa para leer del cliente al servidor.

        /* Para escribir en el socket cliente */
        OutputStream os = cliente.getOutputStream(); //Asociamos el stream con el cliente
        OutputStreamWriter osr = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osr); //El BufferedWriter se usa para escribir del servidor al cliente.

        //Le enviamos al cliente el número de articulos disponibles
        bw.write(6);
        //bw.newLine();
        //bw.flush();

        //Le enviamos los datos de los items al cliente
        for(int i=0;i<6;i++){
          bw.write(items.get(i).getNombre());//Nombre del item
          bw.newLine();
          bw.flush();
          String precio = Double.toString(items.get(i).getPrecio());
          bw.write(precio);//Precio del item
          bw.newLine();
          bw.flush();
          bw.write(items.get(i).getStock());//Stock del item
          bw.write(items.get(i).getDescuento());//Descuento del item
          bw.write(items.get(i).getImagen());//Imagen del item
          bw.newLine();
          bw.flush();
        }

        System.out.println("Esperamos respuesta del cliente... ");
        String opcion = br.readLine();
        //System.out.println(opcion);
        //System.out.println("Respuesta recibida");
        if (opcion.equals("comprar")){
          System.out.println("Compraron articulos!");
          for(int i =0;i<6;i++){
            int itemsComprados = br.read();
            System.out.println(itemsComprados);
            items.get(i).setStock( items.get(i).getStock() - itemsComprados);
          }
          /*
          int itemsComprados0 = br.read();
          System.out.println(itemsComprados0);
          item1.setStock( item1.getStock() - itemsComprados0);
          int itemsComprados1 = br.read();
          System.out.println(itemsComprados1);
          item2.setStock( item2.getStock() - itemsComprados1);
          int itemsComprados2 = br.read();
          System.out.println(itemsComprados2);
          item3.setStock( item3.getStock() - itemsComprados2);
          int itemsComprados3 = br.read();
          System.out.println(itemsComprados3);
          item4.setStock( item4.getStock() - itemsComprados3);
          int itemsComprados4 = br.read();
          System.out.println(itemsComprados4);
          item5.setStock( item5.getStock() - itemsComprados4);
          int itemsComprados5 = br.read();
          System.out.println(itemsComprados5);
          item6.setStock( item6.getStock() - itemsComprados5);
*/
        }



      } //FIN FOREVER
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
