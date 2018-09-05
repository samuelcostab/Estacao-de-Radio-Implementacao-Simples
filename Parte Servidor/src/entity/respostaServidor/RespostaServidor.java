package entity.respostaServidor;

import entity.envioDeMusica.Estacao;
import interfaces.Observable;
import interfaces.Observer;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RespostaServidor implements Observer{
    /*
    Esta Classe é responsável por tratar as requisições dos Clientes e
    responder para o cliente ou encaminhar para o Observador, a classe Estação, os clientes
    que solicitaram sua sintonia, para que assim possa ouvir as músicas.
    */
    
    private Thread t;
    private String[] request;
    private int portUDP;
    private ArrayList<Estacao> estacoesDisponivel = new <Estacao>ArrayList();
    
    public RespostaServidor (ArrayList<Estacao> estacoes){
        this.estacoesDisponivel = estacoes;
    }

    private void iniciaModulo(Socket socket) {
        t = new Thread(){
            @Override
            public void run() {
                try {
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    int commandType = Integer.parseInt(request[0]);//Tipo de Comando a executar
                    switch (commandType) {
                        case 0:
                            enviaMensagem(output, welcome(estacoesDisponivel.size()));
                            break;
                        case 1:
                            int numStation = Integer.parseInt(request[1]) - 1;
                            if (numStation >= 0 && numStation < 2) {
                                enviaMensagem(output, annouce(numStation));
                                estacoesDisponivel.get(numStation).addCliente(socket, portUDP);
                                
                            } else {
                                String msg = "Estação Não Existe.";
                                enviaMensagem(output, invalidCommand(msg.length(), msg));
                            }
                            break;
                        default:
                            String msg = "Requisição Inválida!";
                            enviaMensagem(output, invalidCommand(msg.length(), msg));
                    }

                } catch (IOException ex) {

                }
            }
        };
        this.iniciaThread();
        
        
    }
    
    public void iniciaThread(){
     t.start();
    }
    
    public void paraThread(){
        if(t != null)
        t.interrupt();
    }

    @Override
    public void upDate(Object ob) {

    }
    
    @Override
    public void upDate(Object ob, Object ob2) {
        request = (String[]) ob2;/
        Socket socket = (Socket) ob;
        if (Integer.parseInt(request[1]) > 10){//Controle para identificar a Requisição
            portUDP = Integer.parseInt(request[1]);
        }
        iniciaModulo(socket);
        
    }

    
    public void enviaMensagem(DataOutputStream output, String msg) throws IOException {
        output.writeUTF(msg);
        output.flush();

    }

    private String welcome(int numStations) {
        String msg = "0 " + numStations;

        return msg;
    }

    private String annouce(int station) {
        String msg = "1 ";

        msg += estacoesDisponivel.get(station).getNomeMusic().length() + " ";
        msg += estacoesDisponivel.get(station).getNomeMusic();


        return msg;
    }

    private String invalidCommand(int replySize, String reply) {
        String msg = "2 " + replySize + " " + reply;

        return msg;
    }

    
    
}
