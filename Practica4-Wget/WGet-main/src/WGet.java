import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;

public class WGet {

  public static void Download(URL url, String dir) throws Exception {
    Document doc;
    String urlCompleta=url.toString(); //UrlCompleta imprime toda la url de la pagina ejemplo:http://148.204.58.221/axel/aplicaciones/sockets/java/udp/serializacion/Servidor_O_UDP.java
    String title = null;
    String aux , nombre=null;
    try {
      doc = Jsoup.connect(url.toString()).get();
      //Creamos el archivo en el directorio que se especifica
      aux=dir.substring(dir.lastIndexOf("/"),dir.length());
      nombre=aux.substring(1); //nombre nosrgresa la extension  Ejemplo:  .doc  , .php ,
      createNewFile(dir,WGet.getFileExtension(nombre),doc,urlCompleta);
      //Obtenemos el titulo (sera usado para nombrar el archivo)
      title = getTitleByDoc(doc);

    }catch (HttpStatusException e){
      System.out.println("Error al acceder a la página: "+ e.getMessage());
    } catch (UnsupportedMimeTypeException e){
      System.out.println("Error en el MIME: "+ e.getMessage());
      //aux=dir.substring(dir.lastIndexOf("/"),dir.length());
      //nombre=aux.substring(1); //nombre nosrgresa la extension  Ejemplo:  .doc  , .php ,
      crearArchivoConMimeDiferente(url.getPath(), dir, urlCompleta);
      //createAnyFile(urlCompleta,dir);
      System.out.println("URL PATH: "+ url.getPath());
    } catch (IOException e) {
      System.out.println("No se encontro la ruta especificada");
    }
  }

  //todo obtener la extension del archirvo

  //TODO ESCRIBIR EL HTML DEL ARCHIVO
  private static void createNewFile(String dir, String extension, Document doc, String url){
    try{
      String carpeta = dir.substring(0,dir.lastIndexOf('/'));
      File directorio = new File(carpeta);
      File archivo = new File(dir);
      System.out.println("Tipo de archivo: "+extension);
      System.out.println("Directorio que recibo para crear: "+ dir);
      if(directorio.mkdirs()){
        System.out.println(doc.body().html());
        System.out.println("Durmiendo.....");
        //Hilos.sleep(5000);
        if(archivo.createNewFile()){
          System.out.println("Archivo creado con éxito");
        }
        System.out.println("Directorio creado con éxito");
      }
//este condicional revisa si es un archivo html o htm solo pare estsos dos
      if (extension.startsWith(".html") || extension.startsWith(".htm")){
        if(archivo.createNewFile()){
          System.out.println("Se creo el archivo: "+ dir);
          FileWriter myWriter = new FileWriter(dir);
          BufferedWriter bw  = new BufferedWriter(myWriter);
          PrintWriter wr = new PrintWriter(bw);
          wr.append(doc.toString());
          wr.close();
          bw.close();
        } else{
          System.out.println("Error desconocido al crear el archivo.");
        }
      }
      //Creamos para cualquier archivo
        try {
          archivo.createNewFile();
          if(archivo.exists()){
            System.out.println("Se creo el archivo: "+ dir);
            createAnyFile(url,dir);
          }

        }catch (Exception e){
          System.out.println("Ruta prohibida");
        }
    } catch (IOException e){
      System.out.println("Ocurrio un error al crear el archivo: " + e.getMessage());
    }
  }


  private static String getTitleByDoc(Document doc) {
    String titulo = null;
    try{
      titulo = doc.title();
    } catch (Exception e){
      System.out.println("Error al obtener el título de la página");
    }
    return (titulo != null) ? titulo : "unnamed";
  }

  private static String getFileExtension(String file) {
    String name = file;
    int lastIndexOf = name.lastIndexOf(".");
    if (lastIndexOf == -1) {
      return ""; // empty extension
    }
    return name.substring(lastIndexOf);
  }

  private static  void createAnyFile(String urlStr, String fileStr) throws  Exception{
    URL url = new URL(urlStr);
    BufferedInputStream bis= new BufferedInputStream(url.openStream());
    FileOutputStream fis= new FileOutputStream(fileStr);

    byte[] buffer= new byte[1024];
    //ByteBuffer buffer2 = ByteBuffer.allocate(1024);
    int count=0;
    //(count=bis.read(buffer,0,1024))!=-1
    while(-1 != (count =bis.read(buffer) )){
      fis.write(buffer,0,count);
    }
    fis.close();
    bis.close();
  }

  private static void  crearArchivoConMimeDiferente (String url, String dir , String urlCompleta) {
   try {
     String carpeta = dir.substring(0,dir.lastIndexOf('/'));
     File directorio = new File(carpeta);
     File archivo = new File(dir);
      if(directorio.mkdirs()){
       System.out.println("Directorio creado con éxito");
     }
      if(!archivo.exists()){
      PrintWriter writer = new PrintWriter(archivo, "UTF-8");
      writer.close();
      createAnyFile(urlCompleta,dir);
    }
    } catch (Exception e) {
     System.out.println("Error alencontrar la ruta especificada");
    }
  }

}