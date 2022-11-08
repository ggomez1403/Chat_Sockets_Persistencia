import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.*;

public class Cliente 
	{
		public static String wip; //se declara variable para ip del servidor
		public static String wusuario; // se declara variable para usuario del cliente
				
		public static void Ventana0()
			{
				wusuario = JOptionPane.showInputDialog("Nombre del usuario: "); //se nombre de usuario
				wip      = JOptionPane.showInputDialog("IP del servidor: "); //se solicita ip del servidor
			}
					
		/**
		 * @param args
		 */
		public static void main(String[] args) 
			{
			    Ventana0(); //se llama ventanas de inicio
				Ventana ventana = new Ventana(wip,wusuario); //se envian variables a nueva ventana
				ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
	}

class Paquete implements Serializable
	{
		private String usuario;
		private String ip;
		private String mensaje;//se declaran varibles a enviar
		private ArrayList<String> listaIp = new ArrayList<String>();//se guarda lista de IP de cliente 

		public String getUsuario() 
			{
				return usuario;
			}

		public void setUsuario(String abrev) 
			{
				this.usuario = abrev;
			}

		public String getIp() 
			{
				return ip;
			}
	
		public void setIp(String ip) 
			{
				this.ip = ip;
			}
	
		public String getMensaje() 
			{
				return mensaje;
			}

		public void setMensaje(String mensaje) 
			{
				this.mensaje = mensaje;
			}

		public ArrayList<String> getListaIp() 
			{
				return listaIp;
			}

		public void setListaIp(ArrayList<String> listaIp) 	
			{
				this.listaIp = listaIp;
			}
	}//funcion para enviar variables a servidor

class Ventana extends JFrame
	{
		public Ventana(String wip, String wusuario)//se inicia ventana 
			{
				setBounds(400,400,700,500);
				Pantalla pantalla = new Pantalla(wip, wusuario);// se envian varibles a clase pantalla
				add(pantalla);
				setVisible(true);
				addWindowListener(new envioOnline(wip, wusuario));// se envia varibles a fincion envio
			}	
	}


class envioOnline extends WindowAdapter
	{
		String xip;//se declara variables de ip del cliente
		String xusuario; //se declara varible usuario cliente
		public envioOnline(String wip, String wusuario)// se declara objeto
			{
				xip = wip;
				xusuario = wusuario;
			}
		
		public void windowOpened(WindowEvent e)// se declara ventana
			{
				try
					{
						Socket misocket = new Socket(xip,9999);
						Paquete datos = new Paquete();
						datos.setMensaje("nuevo");
						datos.setIp("");
						datos.setUsuario(xusuario);
						ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());
						paquete_datos.writeObject(datos);
						misocket.close();//se envian datos si es nuevo cliente conectado
					}
				catch (Exception e2)
					{
						e2.printStackTrace();//se envian datos si cliente ya conectado
					}
			}
	
}

class Pantalla extends JPanel implements Runnable //se declara la clase pantalla
	{
		private JLabel label1,label2,label3, label4;
		public  JLabel lblip, lblpuerto, lblusuario;
		private JTextField txtmensaje;
		private JButton miboton;
		private JTextArea areamensajes;
		private JComboBox combousuarios;
		private JPanel panel1,panel2;
		private JPanel panel3,panel4;// se definen los objetos de la clase pantalla
		
		public Pantalla(String wip, String wusuario)// se publica la clase pantalla
			{
				lblusuario = new JLabel();
				lblusuario.setText(wusuario.toUpperCase());
				lblip = new JLabel();
				lblip.setText(wip);
				panel1 		 = new JPanel(new GridLayout(3,2,5,5));
				panel2 		 = new JPanel(new FlowLayout());
				label1 		 = new JLabel("CHAT");
				label2 		 = new JLabel("IP del Servidor:");
				label3 		 = new JLabel("Puerto de la conexion:");
				label4 		 = new JLabel("Usuario:");
				lblpuerto 	 = new JLabel();
				areamensajes = new JTextArea(20,50);
				combousuarios = new JComboBox();// se almacena en varible cada unos de los opjetos de la clase
								
				lblpuerto.setText("9999");//se captura puerto de comunicación 
								
				txtmensaje	 = new JTextField(60);
				miboton		 = new JButton("Enviar");
				
				panel1.add(label2);
				panel1.add(lblip);
				panel1.add(label3);
				panel1.add(lblpuerto);
				panel1.add(label4);
				panel1.add(lblusuario);
				add(panel1);
				//add (panel2);
				add(areamensajes);
				add(combousuarios);
				add(txtmensaje);
				add(miboton);
				enviaTexto mievento = new enviaTexto();
				miboton.addActionListener(mievento);
				Thread hilo = new Thread(this);
				hilo.start();//se muestran los datos de los objetos de la clase pantalla
			}
		
		private class enviaTexto implements ActionListener //se declara la clase de para enviar mensaje
			{
				public  void actionPerformed(ActionEvent e) // se declara funcion de envio de datos al server
					{
						try 
							{
								areamensajes.append("\n" + lblusuario.getText().toUpperCase() + ": " +   txtmensaje.getText().toLowerCase() + "   PARA: " + combousuarios.getSelectedItem());// se captura ip del reseptor
								Socket misocket = new Socket(lblip.getText(),Integer.parseInt(lblpuerto.getText())); // se abre socket de envio de datos al servidor
								Paquete paquete = new Paquete();
								paquete.setUsuario(lblusuario.getText());
								paquete.setIp(combousuarios.getSelectedItem().toString());
								paquete.setMensaje(txtmensaje.getText());
								ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());
								paquete_datos.writeObject(paquete);// envia el paquete de datos al servidor
								misocket.close();//se cierra la conexión al socket 
							}
						catch (UnknownHostException e1)
							{
								e1.printStackTrace();
							} 
						catch (IOException e1) 
							{
								e1.printStackTrace();
							}
						if (txtmensaje.getText().toLowerCase().equals("chao"))
								System.exit(0);//se cierra la venta si el mensaje es chao
					}
			}

		@Override
		public void run() {
			
					try 
						{
							ServerSocket servidor_cliente = new ServerSocket(9090); //se conecta al servidor 
							Socket cliente;
							Paquete PaqueteRecibido;//se declara varible para almasenar datos resividos
							while (true)// se crea bucle para estar pendiente de cualquier paquete enviado al cliente
								{
									cliente = servidor_cliente.accept();//se resive la peticion del servidor
									ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());
									PaqueteRecibido = (Paquete) flujoentrada.readObject(); //se resiven datos de parte del server
									if (PaqueteRecibido.getMensaje().toLowerCase().equals("nuevo") || PaqueteRecibido.getMensaje().toLowerCase().equals("chao"))// se diferencia que no sea ni mensaje nuevo ni chao
										{
											combousuarios.removeAllItems();// se limpian clientes conectados
											ArrayList <String> listacombo = new ArrayList<String>();// se resive lista de ip clientes activos 
											listacombo=PaqueteRecibido.getListaIp();
											for (String ip:listacombo)
											{
												combousuarios.addItem(ip);// se lista ip de clientes activos
											}
										}
										
									else
											areamensajes.append("\n" + PaqueteRecibido.getUsuario().toString().toUpperCase() + ": " + PaqueteRecibido.getMensaje().toString().toLowerCase());// se muestran los mensajes resividos
									
								}
						} 
					catch (IOException | ClassNotFoundException e) 
						{
							e.printStackTrace();
						}
				}
	}