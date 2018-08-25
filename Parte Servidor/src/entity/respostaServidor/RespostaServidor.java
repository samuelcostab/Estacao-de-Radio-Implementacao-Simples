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
    Thread t;
    String[] request;
    int portUDP;
    private ArrayList<Estacao> estacoesDisponivel = new <Estacao>ArrayList();
    
    public RespostaServidor (ArrayList<Estacao> estacoes){
        this.estacoesDisponivel = estacoes;
    }

    private void iniciaModulo(Socket socket) {
        t = new Thread(){
            @Override
            public void run() {
                //System.out.println("3)Mod de Resposta iniciado!");
                try {
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());//Objeto de envio de mensagemTCP
                    int commandType = Integer.parseInt(request[0]);//Tipo de Comando a executar
                    switch (commandType) {
                        case 0:
                            enviaMensagem(output, welcome(estacoesDisponivel.size()));//Envia Welcome com qtd de Estações
                            break;
                        case 1:
                            int numStation = Integer.parseInt(request[1]) - 1;
                            if (numStation >= 0 && numStation < 2) {
                                enviaMensagem(output, annouce(numStation));//Mensagem de Confirmação ao cliente.
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
        request = (String[]) ob2;//A Porta UDP do socket vem na requisição HELLO
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

    private String welcome(int numStations) {//"Resposta" após o cliente conectar-se ao servidor(HANDSHAKE)
        String msg = "0 " + numStations;

        return msg;
    }

    private String annouce(int station) {//"Resposta" Ao cliente setar uma estação ou, ao a estação mudar a musica executada
        String msg = "1 ";

        msg += estacoesDisponivel.get(station).getNomeMusic().length() + " ";
        msg += estacoesDisponivel.get(station).getNomeMusic();


        return msg;
    }

    private String invalidCommand(int replySize, String reply) {//Quando recebido um comando Inválido
        String msg = "2 " + replySize + " " + reply;

        return msg;
    }

    
    
}
