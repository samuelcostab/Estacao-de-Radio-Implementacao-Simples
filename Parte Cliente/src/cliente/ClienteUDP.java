package cliente;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class ClienteUDP {
    
    /*
    Esta Classe é responsável por receber os dados que a Estação
    enviar e assim reproduzir, "Tocando a música". 
    */
    
    private DatagramSocket socketUDP;

    public ClienteUDP() throws SocketException{
            socketUDP = new DatagramSocket();

    }

    public void tocarMusica() {
        byte[] dadosRecebidos = new byte[50000];
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        DatagramPacket pckRecebido = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
                        socketUDP.receive(pckRecebido);
                        play(pckRecebido.getData());
                    } catch (IOException ex) {
                        Logger.getLogger(ClienteTCP.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }.start();

    }

    private void play(byte[] dadosMusica) {
        try {
            Player reprodutor = new Player(new ByteArrayInputStream(dadosMusica)); //Play para toca o BufferByte
            reprodutor.play();

        } catch (JavaLayerException ex) {
            Logger.getLogger(ClienteTCP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public DatagramSocket getSocketUDP() {
        return socketUDP;
    }

    public void setSocketUDP(DatagramSocket cnxUDP) {
        this.socketUDP = cnxUDP;
    }

}
