package cliente;

import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteTCP {

    private static Scanner scan;
    private Socket socket;
    private int portTCP;
    private int portUDP;

    public ClienteTCP(int portServer) throws IOException {
        socket = new Socket("localhost", portServer);//Socket de ConexãoTCP entre o Cliente e o Servidor    

    }

    private String hello() {
        String msg = "0 " + getPortUDP();

        return msg;
    }

    private String setStation(int stationSelected) {
        String msg = "1 " + stationSelected;

        return msg;
    }

    private void enviaMensagem(DataOutputStream output, String msg) throws IOException {
        output.writeUTF(msg);
        output.flush();
    }

    private String recebeMensagem(DataInputStream input) {
        String msg = null;
        try {
            msg = input.readUTF();
        } catch (IOException ex) {
            try {
                socket.close();
            } catch (IOException ex1) {
                //Logger.getLogger(ClienteTCP.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        return msg;
    }

    public void clienteConectado(ClienteTCP cliente, ClienteUDP clienteUDP, DataOutputStream output, DataInputStream input) {
        int replyType = 0;
        Thread tocaMusica = new Thread() {
            @Override
            public void run() {
                clienteUDP.tocarMusica();
            }
        };
        while (replyType != 2 || input != null) {
            String msgServidor = cliente.recebeMensagem(input);//Resposta do Servidor
            String[] reply = null;
            try{
            reply = msgServidor.split(" ");
            }catch(NullPointerException e){
                System.out.println("SERVIDOR FINALIZADO!!");
                System.exit(0);
            }
            if (reply != null) {
                replyType = Integer.parseInt(reply[0]);
                if (replyType == 0) {
                    System.out.println(reply[1] + " Estações Disponíveis!");
                    System.out.print("Digite a estação que deseja ouvir:");
                    try {
                        cliente.enviaMensagem(output, cliente.setStation(scan.nextInt()));
                    } catch (IOException ex) {
                        System.out.println("Servidor foi Finalizado!");
                    }

                } else if (replyType == 1) {
                    System.out.println("Musica/Estação foi(ram) atualizado(s)!");
                    System.out.println(msgServidor);
                    tocaMusica.start();

                } else if (replyType == 2) {
                    System.out.println("TYPE|SIZE|REPLY");
                    System.out.println(msgServidor);
                    System.exit(0);
                }

            }else{
                System.exit(0);
            }
            
        }//Fim While

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        scan = new Scanner(System.in);

        ClienteTCP cliente = new ClienteTCP(13002);//ConexãoTCP na porta do Servidor(13002)
        ClienteUDP clienteUDP = new ClienteUDP();//ConexãoUDP
        cliente.setPortUDP(clienteUDP.getSocketUDP().getLocalPort());//Regista a portaUDP do ClienteTCP com a porta do ClienteUDP

        DataOutputStream output = new DataOutputStream(cliente.getSocket().getOutputStream());//Objeto para Enviar msg para o Servidor
        DataInputStream input = new DataInputStream(cliente.getSocket().getInputStream());//Objeto para Receber msg do Servidor

        cliente.enviaMensagem(output, cliente.hello());//Envia o Hello para o Servidor
        cliente.clienteConectado(cliente, clienteUDP, output, input);//Executa o Protocolo do cliente conectado

        output.close();
        input.close();
        scan.close();

        cliente.getSocket().close();
        clienteUDP.getSocketUDP().close();

    }

    public int getPortUDP() {
        return portUDP;
    }

    public void setPortUDP(int portUDP) {
        this.portUDP = portUDP;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

}
