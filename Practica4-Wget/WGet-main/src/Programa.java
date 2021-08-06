import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Programa {

  private static ArrayList<URL> linksAbsolutos = new ArrayList<URL>();

  public static void main(String[] args) {
    long TInicio, TFin, tiempo; //Variables para determinar el tiempo de ejecución
    Thread t1 = null, t2 = null, t3 = null, t4 = null, t5 = null;
    TInicio = System.currentTimeMillis();
    try{
      System.out.println("--------------------------");
      System.out.println("           Wget           ");
      System.out.println("--------------------------");
      System.out.println("");
      System.out.println("Ingresa la ruta o nombre de la carpeta a crear. Esto para guardar los archivos de la página.");
      String nombreCarpeta = getNombreCarpeta();
      crearCarpeta(nombreCarpeta); //Creamos la carpeta donde guardaremos la página web
      System.out.println("Ingresa la URL de la página a descargar");
      System.out.println("Ejemplo: http://148.204.58.221/axel/aplicaciones/");
      URL url = getURLPagina(); //Obtenemos el link de la página ingresado

      //System.out.println("HOST: "+ url.getHost());
      System.out.println("PATH: "+ url.getPath());
      //System.out.println("Link chido: "+ url.getProtocol() + "://" + url.getHost() + url.getPath());


      Document html = Jsoup.connect(url.toString()).get();
      File index = new File(nombreCarpeta+"/index.html");
      if(index.createNewFile()){
        Elements imagesEditarRuta = html.getElementsByTag("img");
        for(Element image : imagesEditarRuta){
          String rutaRelativa = image.attr("src");
          if(rutaRelativa.startsWith("/")){
            rutaRelativa = rutaRelativa.substring(1);
          }
          image.attr("src",rutaRelativa);
          System.out.println("Ruta Style: "+ rutaRelativa);
          String rutaAbsoluta = url.toString() + rutaRelativa;
          URL urlAbsoluto = new URL(rutaAbsoluta);
          if(linksAbsolutos.contains(urlAbsoluto)) continue;
          linksAbsolutos.add(urlAbsoluto);
        }
        System.out.println();
        crearCarpeta(nombreCarpeta + url.getPath());
        FileWriter myWriter = new FileWriter(nombreCarpeta+ url.getPath() +"index.html");
        BufferedWriter bw  = new BufferedWriter(myWriter);
        PrintWriter wr = new PrintWriter(bw);
        wr.append(html.toString());
        wr.close();
        bw.close();
        System.out.println("Index creado con éxito");
      }


      System.out.println("Leyendo las carpetas de la página...");
      setLinks(url);
      System.out.println("Generando las carpetas de la página, por favor espere...");
      for(int i=0;i<linksAbsolutos.size();i++){
        setLinks(linksAbsolutos.get(i));
      }
      //System.out.println(linksAbsolutos);

int i=0;
     // for(int i=0;i<linksAbsolutos.size();i++){

      while (i<linksAbsolutos.size()){
      String directorio = nombreCarpeta + linksAbsolutos.get(i).getPath();

        if(directorio.endsWith("/")){
          crearCarpeta(directorio);
          i++;
        } else{
          //System.out.println("Ruta Especifica: "+linksAbsolutos.get(i));
          //System.out.println("Directortio especifico: "+directorio);

           Hilos hilo1= new Hilos(linksAbsolutos.get(i++), directorio,"Hilo1");
          directorio = nombreCarpeta + linksAbsolutos.get(i).getPath();
          Hilos hilo2= new Hilos(linksAbsolutos.get(i++), directorio, "Hilo2");
          directorio = nombreCarpeta + linksAbsolutos.get(i).getPath();
          Hilos hilo3= new Hilos(linksAbsolutos.get(i++), directorio, "Hilo3");
          directorio = nombreCarpeta + linksAbsolutos.get(i).getPath();
          Hilos hilo4= new Hilos(linksAbsolutos.get(i++), directorio, "Hilo4");
          directorio = nombreCarpeta + linksAbsolutos.get(i).getPath();
          Hilos hilo5= new Hilos(linksAbsolutos.get(i++), directorio, "Hilo5");
          directorio = nombreCarpeta + linksAbsolutos.get(i).getPath();

          t1= new Thread(hilo1);
          t2 = new Thread(hilo2);
          t3 = new Thread(hilo3);
          t4 = new Thread(hilo4);
          t5 = new Thread(hilo5);
          t1.start();
          t2.start();
          t3.start();
          t4.start();
          t5.start();




          WGet.Download(linksAbsolutos.get(i), directorio);
        i++;

        }
      }

      while(true){
        if(t1.isAlive() || t2.isAlive() || t3.isAlive() || t4.isAlive() || t5.isAlive()){
          continue;
        } else{
          System.out.println("Acabaron mis hilos, me rompo");
          break;
        }
      }



    } catch (DataFormatException e){
      System.out.println("Error en el nombre del directorio "+ e.getMessage());
    } catch (NoSuchElementException e){
      System.out.println("No ingresaste el nombre de la carpeta."+ e.getMessage());
    } /*catch (MalformedURLException e){
      System.out.println("Se ingresó una URL erronea."+ e.getMessage());
    }*/ catch (Exception e){
      System.out.println("Error general: "+ e.getMessage());
    }

    //wGet("PaginaProfe", "http://148.204.58.221/axel/aplicaciones/");

    TFin = System.currentTimeMillis(); //Tomamos la hora en que finalizó el algoritmo y la almacenamos en la variable T
    tiempo = TFin - TInicio; //Calculamos los milisegundos de diferencia
    System.out.println("Tiempo de ejecución en milisegundos: " + tiempo); //Mostramos en pantalla el tiempo de ejecución en milisegundos
    System.out.println("Tiempo de ejecucion en segundos: "+ tiempo/1000.0);
  }

  private static URL getLinkAtributo(URL url,String atributo){
  if(atributo.startsWith("//")){
    return  null;
  }
    URL urlBienFormada;
    String host = url.getHost(); // 148.204.58.221 | www.escom.ipn.mx
    try {
      if(atributo.contains( host )){ //Si es una url absoluta http://148.204.58.221/axel/sfdsffg
        return new URL(atributo);
      } else { // Absoluta:  http://google.com.mx/dgdhrt  Relativa: /img/df.jpg | smite/foto.png | ../smite | #contacto
        if(atributo.startsWith("http")) return null; //Es una página externa
        if(atributo.startsWith("../")) return null; //Los directorios padres no se descargan
        if(atributo.startsWith("#")) return null;
        if(atributo.startsWith("?")) return null;
        if(atributo.startsWith("//")) return null;
        System.out.println("Atributo: "+ atributo);
        if(atributo.startsWith("./")){
          atributo = atributo.substring(1); //Le quitamos el punto
          if(atributo.startsWith("/../")){
            atributo = atributo.substring(2); //Le quitamos la primer diagonal y los 2 puntos
          }
          atributo = url.getProtocol() + "://" + url.getHost() + atributo;
          return new URL(atributo);
        }
        //Si no es absoluta ni otro dominio entonces es una relativa
        if(atributo.startsWith("/")){
          atributo = url.getProtocol() + "://" + url.getHost() + atributo;
          return new URL(atributo);
        } else{
          atributo = url.getProtocol() + "://" + url.getHost() + url.getPath() + atributo;
          return new URL(atributo);
        }
      }
    } catch (MalformedURLException e) {
      System.out.println("Error al tratar de convertir el atributo: "+ atributo);
    }
    return null;
  }

  private static ArrayList<URL> obtenerLlaveAtributo(Elements elementos, URL url){
    ArrayList<URL> linksObtenidos = new ArrayList<URL>();
    String linksNuevos;
    URL urlNueva;
    for(Element elemento : elementos){
      if( elemento.hasAttr("src") ){
        linksNuevos = elemento.attr("src");
        urlNueva = getLinkAtributo(url, linksNuevos);
        if(urlNueva != null){
          if(!linksAbsolutos.contains(urlNueva)){
            if(!linksObtenidos.contains(urlNueva)){
              linksObtenidos.add(urlNueva);
            }
          }
        }
      } else if( elemento.hasAttr("href") ){
        linksNuevos = elemento.attr("href");
        urlNueva = getLinkAtributo(url, linksNuevos);
        if(urlNueva != null){
          if(!linksAbsolutos.contains(urlNueva)){
            if(!linksObtenidos.contains(urlNueva)){
              linksObtenidos.add(urlNueva);
            }
          }
        }
      }
    }
    return linksObtenidos;
  }

  private static void setLinks(URL url){
    //ArrayList<URL> links = new ArrayList<URL>();
    Elements href = obtenerElementosPorAtributo(url,"href"); //Obtenemos los elementos con atributo
    Elements src = obtenerElementosPorAtributo(url,"src");
    if(href != null && src != null) {
      linksAbsolutos.addAll(obtenerLlaveAtributo(href, url));
      linksAbsolutos.addAll(obtenerLlaveAtributo(src, url));
    }
  }


  private static Elements obtenerElementosPorAtributo(URL url, String atributo){
    try {
      if(url.getPath().contains(".")) return null; //Si tiene extension no es un html
      Document doc = Jsoup.connect(url.toString()).get(); //Obtenemos el documento html
      //System.out.println("Atributos: "+ doc.getElementsByAttribute(atributo));
      return doc.getElementsByAttribute(atributo);
    } catch (HttpStatusException err) {
      System.out.println("Fallo Fetching url . url prohibido o caido: "+err);
      //err.printStackTrace();

    }catch(IOException e){
    // e.printStackTrace();

    }
    return null;
  }

  private static String getNombreCarpeta(){
    Scanner entrada = new Scanner(System.in);
    return entrada.nextLine();
  }

  private static URL getURLPagina(){
    Scanner entrada = new Scanner(System.in);
    try {
      String url = entrada.nextLine();
      if(url.endsWith("/")){
        return new URL(url);
      } else{
        url = url + "/";
        return new URL(url);
      }
    } catch (MalformedURLException e) {
      System.out.println("El url ingresado es incorrecto.");
      return null;
    }
  }

  private static void crearCarpeta(String directorio) throws DataFormatException {
    if(directorio != null){
      if(directorio.matches("[-_. A-Za-z0-9áéíóúÁÉÍÓÚ/]+")){ // \ / : * ? " < > |
        File file = new File(directorio);
        if(file.mkdirs()){
          System.out.println("Directorio creado: "+ file.getPath() );
        } else{
          System.out.println("Error al crear el directorio o ya existe uno creado con el mismo nombre.");
        }
      } else{
        throw new DataFormatException("El nombre del directorio tiene caracteres inválidos.");
      }
    } else{
      throw new DataFormatException("El nombre del directorio no puede estar vacío");
    }
  }


}