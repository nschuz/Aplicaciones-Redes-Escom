package main;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.util.Arrays;
import java.util.Scanner;

public class Cliente {
public static String nombre;
public  static String ruta;

public static void main(String[] args) throws IOException {
        final String IP = "localhost";
        final int PUERTO = 25001;
        

        try {
            Socket cl = new Socket(IP, PUERTO);
            System.out.println("Conexion con servidor establecida.. recibiendo datos");

            InputStream is = cl.getInputStream(); //Asociamos el stream con el cliente
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr); //El BufferedReader se usa para leer del cliente al servidor.

            OutputStream os = cl.getOutputStream(); //Asociamos el stream con el cliente
            OutputStreamWriter osr = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osr); //El BufferedWriter se usa para escribir del servidor al cliente.

          
            File archivosCliente = new File("F:\\Escritorio\\JavaSwing\\Practica1\\src\\archivosCliente");
            System.out.println("");
            System.out.println("Directorio actual: "+ archivosCliente.getAbsolutePath());
            System.out.println("");
            System.out.println("Archivos en mi computadora:");
            File[] listaArchivos = archivosCliente.listFiles();

            String archivos = "";

            for(File f: listaArchivos){
                if(f.isDirectory()){
                    System.out.println("Dir: "+ f.getName());
                    archivos += "Dir: "+f.getName() + "{salto}";
                } else{
                    System.out.println("File: "+f.getName());
                    archivos += "File: "+ f.getName() + "{salto}";
                }
            }

            bw.write(archivos); //Le mandamos los directorios que tenemos al servidor
            bw.newLine();
            bw.flush();

            String archivosServidor = br.readLine(); //Obtenemos los directorios del servidor

            System.out.println("Archivos del servidor: ");
            System.out.println(archivosServidor.replace("{salto}", "\n"));

            Scanner sc = new Scanner(System.in);
            for(;;) {
                System.out.println("¿Que quieres hacer? \n 1) bajar \n 2) subir");
                String opcion = sc.nextLine();

                System.out.println(opcion);

                bw.write(opcion); //Le mandamos la instruccion al servidor de que queremos hacer
                bw.newLine();
                bw.flush();
                if(opcion.equalsIgnoreCase("subir")){
                    System.out.println("Hola");
                    JFileChooser jf = new JFileChooser();
                    File workingDirectory = new File(System.getProperty("user.dir"));
                    jf.setCurrentDirectory(workingDirectory);
                    jf.setMultiSelectionEnabled(true);
                    int r = jf.showOpenDialog(null);
                    if(r==JFileChooser.APPROVE_OPTION) {
                        File[] f = jf.getSelectedFiles();
                        for (int i = 0; i < f.length; ++i) {
                            System.out.println("Entra");
                            cl = new Socket(IP,PUERTO);
                            String nombre = f[i].getName();
                            String path = f[i].getAbsolutePath();
                           
                           
                            
                            long tam = f[i].length();
                            System.out.println("Preparandose pare enviar archivo " + path + " de " + tam + " bytes\n\n");
                            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                            DataInputStream dis = new DataInputStream(new FileInputStream(path));
                            dos.writeUTF(nombre);
                            dos.flush();
                            dos.writeLong(tam);
                            dos.flush();
                            long enviados = 0;
                            int l = 0, porcentaje = 0;
                            while (enviados < tam) {
                                byte[] b = new byte[1500];
                                l = dis.read(b);
                                System.out.println("enviados: " + l);
                                dos.write(b, 0, l);
                                dos.flush();
                                enviados = enviados + l;
                                porcentaje = (int) ((enviados * 100) / tam);
                                System.out.print("\rEnviado el " + porcentaje + " % del archivo");
                            }//while
                            System.out.println("\nArchivo enviado..");
                            dis.close();
                            dos.close();
                            cl.close();
                        }
                    }
                }
                else if(opcion.equalsIgnoreCase("bajar")){

                }
                if(opcion.equals("salir")){
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}