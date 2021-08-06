import java.net.URL;

public class Hilos implements Runnable
{
    URL url;
    String dir;
    String name;
public Hilos(URL url, String dir, String name){

    this.url=url;
    this.dir=dir;
    this.name=name;
}

@Override
public void  run(){

    try {
       // System.out.println("\nHilo: "+this.name);
        WGet.Download(url, dir);

    } catch (Exception e) {
        e.printStackTrace();
    }
}



}
