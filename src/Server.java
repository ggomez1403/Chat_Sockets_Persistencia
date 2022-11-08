import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server  
	{
		public static void main(String[] args) // Se realiza el llamado de la clase ventana 
			{
				VentanaServidor VentanaServidor = new VentanaServidor();
				VentanaServidor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}	
	}

class VentanaServidor extends JFrame implements Runnable
	{
		private	JTextArea areatexto; // se declara la varible que es un objeto de la ventana donde se mostrara el estado de los usuarios 
		public VentanaServidor()// se le dan caracteristicas a la ventana 
			{
				setBounds(1300,350,400,350);
				JPanel pantalla = new JPanel();
				pantalla.setLayout(new BorderLayout());
				areatexto = new JTextArea();
				pantalla.add(areatexto,BorderLayout.CENTER);
				add(pantalla);
				setVisible(true);
				Thread hilo = new Thread(this);
				hilo.start();
			}
			
		public void run()
			{
				String usuario, ip, mensaje; // se declaran variables donde se almacena los datos del cliente que se conectan
				Paquete paquete_recibido; // se declara variable para almacenar los paquetes enviados por el cliente 
				ArrayList<String> listausuarios = new ArrayList<String>(); //se crea varible para tener el control de la lista de clientes que estan conectados
				try 
					{
						ServerSocket servidor = new ServerSocket(9999); // se crea una varible con el objeto ServerSocket notificando por que puerto se conectan 
						while (true)// se crea un bucle para que este verificando constantemente las peticiones de los clientes 
							{
								Socket misocket = servidor.accept(); // Se resive la petición del cliente 
								ObjectInputStream paquete_datos = new ObjectInputStream(misocket.getInputStream()); // se resiben los datos enviados por el cliente
								paquete_recibido =(Paquete) paquete_datos.readObject(); // se guardan en la varible los datos enviados por el cliente
								usuario = paquete_recibido.getUsuario().toString(); // se guarda en varible el usuario del cliente 
								ip      = paquete_recibido.getIp().toString(); //Se guarda en varible la IP del receptor del mensaje
								mensaje = paquete_recibido.getMensaje().toString(); // se guarda en varible el mensaje 
								InetAddress localizacion = misocket.getInetAddress(); 
								String ipRemota = localizacion.getHostAddress();// Se captura la IP del cliente
								if (mensaje.toLowerCase().equals("nuevo"))// se valida si el mensaje es nuevo 
									{
										listausuarios.add(ipRemota); // se actuliza la lista de IP a los clientes
										areatexto.append("\n Usuario " + usuario.toUpperCase() + " conectado"); // se notifica en pantalla al servidor que hay nueva conexion
									}
								else
									{	
										areatexto.append("\n" + usuario.toString().toUpperCase() + ": " + mensaje.toLowerCase() + " PARA " + ip); // se notifica en pantalla al servidor para que cliente va el mansaje 
										Socket enviaDestinatario= new Socket(ip,9090); // se abre comunicacion con el cliente receptor  
										ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());  
										paqueteReenvio.writeObject(paquete_recibido); // se envia el mensaje al receptor con los datos de quien envio  
										paqueteReenvio.close(); 
										enviaDestinatario.close();
										misocket.close(); // se cierran las conexiones al cliente receptor
									}
								if (mensaje.toLowerCase().equals("chao")) // se verifica si le mensaje es chao
									{
										int pos = listausuarios.indexOf(ipRemota); // se guarda en varible la ip del cliente  
										listausuarios.remove(pos); // se elimina de la lista la ip del cliente que envia el chao
										areatexto.append("\n El ususario " + usuario.toUpperCase() + " Abandono"); // se notifica en pantalla al servidor que el cliente se salio del chat
									}
								if (mensaje.toLowerCase().equals("chao") || mensaje.toLowerCase().equals("nuevo")) // se valida si es nuevo o chao 
									{
										paquete_recibido.setListaIp(listausuarios);// se lista las ip de los clientes conectados
										for (String pc: listausuarios)
											{
												Socket enviaDestinatario= new Socket(pc,9090); //  conecta con los clientes
												ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream()); // se grada el listado de ip en objeto
												paqueteReenvio.writeObject(paquete_recibido); //se envia el objeto al cliente 
												paqueteReenvio.close();
												enviaDestinatario.close();
												misocket.close();// se cierra conexion con el cliente
											}
									}
							}
					}
				catch (IOException | ClassNotFoundException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}