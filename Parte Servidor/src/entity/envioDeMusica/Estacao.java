package entity.envioDeMusica;


import interfaces.Observer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Estacao implements Observer {
    Thread t, t2;
    private ArrayList<Socket> clientes = new ArrayList<>();
    private Map<Integer, Integer> portasUDP = new HashMap<>();
    private DatagramSocket socketUDP;
    private DatagramPacket packetUDP;
    private ArrayList<byte[]> musicParts;
    private int contadorByte;//Conta a parte da música que esta executando.
    private String nomeMusic;

    public Estacao(String caminhoMusica){
        String[] caminhoSeparado = caminhoMusica.split("/");
        nomeMusic = caminhoSeparado[2];
        t = new Thread(){
            @Override
            public void run() {
                carregaMusica(caminhoMusica);//Transforma a Música em um Array de Bytes[]
                t2 = new Thread(){
                    @Override
                    public void run() {
                        try {
                            tocaMusica();//Fica tocando a música infinitas vezes
                        } catch (InterruptedException ex) {
                            
                        }
                    }
                };
                t2.start();
                enviaDados();//Envia Dados para os clientes conectados
            }

        };
        this.iniciaThread();

    }

    public void iniciaThread() {
        t.start();
    }

    public void paraThread() {
        t.interrupt();
        t2.interrupt();
    }

    private void carregaMusica(String caminho) {
        musicParts = new <byte[]>ArrayList();
        try {
            FileInputStream in = new FileInputStream(new File(caminho));
            BufferedInputStream bufferMusic = new BufferedInputStream(in);
            int n = 0;
            while (n != -1) { //Enquanto o buffer ainda tiver arquivo                                                    
                byte[] byteMusic = new byte[50000];//Tamanho que vai ser a parte da música
                n = bufferMusic.read(byteMusic);//passa do buffer para o byteMusic e retorna um int de controle
                musicParts.add(byteMusic);//add esta parte da música ao array
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Estacao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Estacao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void tocaMusica() throws InterruptedException {
        while (true) {
            for (int i = 0; i < musicParts.size(); i++) {
                Thread.sleep(2650);
                contadorByte = i;
            }
        }

    }

    private byte[] enviaDados() {
        try {
            while (true) {
                if (clientes.size() > 0) {
                    socketUDP = new DatagramSocket();
                    for (Socket s : clientes) {
                        int portaUDP = portasUDP.get(s.getPort());
                        packetUDP = new DatagramPacket(musicParts.get(contadorByte), musicParts.get(contadorByte).length, s.getInetAddress(), portaUDP);
                        socketUDP.send(packetUDP);
                    }
                }
                try {
                    Thread.sleep(2650);
                } catch (InterruptedException ex) {
                    
                }

            }

        } catch (SocketException ex) {
            socketUDP.close();
        } catch (IOException ex) {
            
        }

        return null;
    }
    
    public void addCliente(Socket s, int portaUDP) {
        clientes.add(s);//Adiciona o cliente na lista de clientes
        portasUDP.put(s.getPort(), portaUDP);

    }

    public void removerCliente(Socket s) {
        for(int i = 0; i< clientes.size(); i++){
            if(s.getPort() == clientes.get(i).getPort()){
                clientes.remove(s);
            }
        }
    }

    public ArrayList<Socket> getClientes() {
        return clientes;
    }

    public void listarClientes(){
        int i= 1;
        for(Socket s : clientes){
            System.out.println("     "+i+":"+s.getPort());
            i++;
        }
    }
    
    @Override
    public void upDate(Object ob) {
        Socket s = (Socket) ob;
        removerCliente(s);

    }

    @Override
    public void upDate(Object ob, Object ob2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getNomeMusic() {
        return nomeMusic;
    }

    
}
