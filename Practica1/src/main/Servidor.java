package main;


import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static String relativePath = System.getProperty("user.dir"); //Ruta relativa de nuestro directorio

    public static void main(String[] args) {
     final int port = 25001;//Puerto de nuestro Servidor ( puede ser del 1 al 2^16)


        try {
            ServerSocket servidor = new ServerSocket(port); // Escuchamos el servidor en el puerto
            servidor.setReuseAddress(true); //Esto es para que no se congele 15 segundos algo asi xd
            System.out.println("Servidor Iniciado con exito en el puerto: " + servidor.getLocalPort());

            for(;;){
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

                File archivosServidor = new File(relativePath+"\\src\\archivosServidor");
                System.out.println();
                System.out.println("Directorio actual: " + archivosServidor.getAbsolutePath());
                System.out.println("");
                System.out.println("Archivos del servidor:");
                File[] listaArchivos = archivosServidor.listFiles();

                String archivos = "";
                for (File f : listaArchivos) {
                    if (f.isDirectory()) {
                        System.out.println("Dir: " + f.getName());
                        archivos += "Dir: " + f.getName() + "{salto}";
                    } else {
                        System.out.println("File: " + f.getName());
                        archivos += "File: " + f.getName() + "{salto}";
                    }
                }

                String archivosCliente = br.readLine(); //Obtenemos los directorios del cliente

                System.out.println("Archivos del cliente: ");
                System.out.println(archivosCliente.replace("{salto}", "\n"));

                bw.write(archivos); //Le mandamos los directorios que tenemos al cliente
                bw.newLine();
                bw.flush();

                //Una vez el cliente ve los archivos, hay que preguntarle que quiere hacer
                String opcion = "";
                File f = new File("");
                String ruta = f.getAbsolutePath();
                String carpeta="archivosServidor";
//                String ruta_archivos = ruta+"\\"+carpeta+"\\";
                String ruta_archivos = relativePath+"\\src\\archivosServidor\\";
                do {
                    System.out.println("Entra2");
                    //Hasta aqui entra
                    /*if(cliente.isClosed()){
                        cliente = servidor.accept();
                        // Para leer en el socket cliente /
                        is = cliente.getInputStream(); //Asociamos el stream con el cliente
                        isr = new InputStreamReader(is);
                        br = new BufferedReader(isr); //El BufferedReader se usa para leer del cliente al servidor.

                        // Para escribir en el socket cliente
                        os = cliente.getOutputStream(); //Asociamos el stream con el cliente
                        osr = new OutputStreamWriter(os);
                        bw = new BufferedWriter(osr); //El BufferedWriter se usa para escribir del servidor al cliente.
                    }*/
                    System.out.println("Esperando a que me den una opción");
                    opcion = br.readLine();
                    System.out.println("opcion escogida: "+opcion);
                    if(opcion.equalsIgnoreCase("Subir")){
                        int numeroArchivos = Integer.parseInt(br.readLine());
                        System.out.println("Veces del for: "+numeroArchivos);
                        int aux = 0;
                        for(int i = 0; i<numeroArchivos; i++) {
                            Socket cliente2 = servidor.accept();
                            System.out.println("Subir archivo");
                            System.out.println("Entra aqui");
                            DataInputStream dis = new DataInputStream(cliente2.getInputStream());
                            String nombre = dis.readUTF();
                            long tam = dis.readLong();
                            System.out.println("Comienza descarga del archivo " + nombre + " de " + tam + " bytes\n\n");
                            DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos+nombre));
                            long recibidos = 0;
                            int l = 0, porcentaje = 0;
                            while (recibidos < tam) {
                                byte[] b = new byte[1500];
                                l = dis.read(b);
                                System.out.println("leidos: " + l);
                                dos.write(b, 0, l);
                                dos.flush();
                                recibidos = recibidos + l;
                                porcentaje = (int) ((recibidos * 100) / tam);
                                System.out.println("\rRecibido el " + porcentaje + " % del archivo");
                            }//while
                            System.out.println("Archivo recibido..");
                            //dos.close();
                            //dis.close();
                            cliente2.close();
                        }
                    }
                    if(opcion.equalsIgnoreCase("Bajar")) {
                        System.out.println("Entra");
                        System.out.println("Ya no entra");

                        System.out.println("Bajar archivo");
                        String rutaArchivo = "";
                        rutaArchivo = br.readLine();
                        File file = new File(rutaArchivo);


                        System.out.println("-----Archivos xp: " + file.getName());
                        String nombre = file.getName();
                        String path = file.getAbsolutePath();

                        long tam = file.length();
                        System.out.println("Preparandose pare enviar archivo " + path + " de " + tam + " bytes\n\n");
                        DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
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
                        //dis.close();
                        //dos.close();
                        //cliente.close();
                    }
                } while(!opcion.equals("salir"));


            }//Fin primer forever

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getFile(String path){
        File file = new File(path);
        if(file.exists()){
            return file;
        } else{
            System.out.println("La ruta especificada no encontro ningun archivo");
            return file;
        }
    }

}