package entity.Servidor;

import entity.Conexão.Conexão;
import entity.envioDeMusica.Estacao;
import entity.requisicaoCliente.RequisicaoCliente;
import entity.respostaServidor.RespostaServidor;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

public class Servidor {

    static Scanner scan;
    ServerSocket serverSocket;
    static public Conexão modConnection;
    static public RequisicaoCliente modRequest;
    static public RespostaServidor modReply;
    static private ArrayList<Estacao> estacoes = new <Estacao>ArrayList();

    public Servidor(ServerSocket server) {
        this.serverSocket = server;

        Estacao e1 = new Estacao("D:/Guns N Roses/Paradise City" + ".mp3");
        Estacao e2 = new Estacao("D:/Guns N Roses/I love rock and roll" + ".mp3");
        estacoes.add(e1);
        estacoes.add(e2);

        modConnection = new Conexão(serverSocket);
        modRequest = new RequisicaoCliente();
        modReply = new RespostaServidor(getEstacoes());

        modConnection.registerObserver(modRequest);
        modRequest.registerObserver(modReply);
        modRequest.registerObserver(e1);
        modRequest.registerObserver(e2);

    }

    public void setEstacoes(Estacao estacao) {
        this.getEstacoes().add(estacao);
    }

    public ArrayList<Estacao> getEstacoes() {
        return estacoes;
    }

    public void closeServer() throws IOException {
        serverSocket.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        scan = new Scanner(System.in);
        ServerSocket serverSocket = new ServerSocket(13002);
        Servidor server = new Servidor(serverSocket);

        int opc = 0;
        do {
            System.out.print("1)Listar Estações e Clientes\n0)Encerrar Servidor\nOpção:");
            opc = scan.nextInt();
            if (opc == 1) {
                int estacao = 1;
                for (Estacao s : server.getEstacoes()) {
                    System.out.println("ESTAÇÂO:" + estacao);
                    System.out.println("   Clientes Conectados:");
                    s.listarClientes();
                    estacao++;
                }
                System.out.println();
            }
            

        } while (opc != 0);
        System.out.println("Fechando Servidor...");
        Thread.sleep(2000);
        for (Estacao s : estacoes) {
            s.paraThread();
        }
        modReply.paraThread();
        modRequest.paraThread();
        modConnection.paraThread();

        server.closeServer();
        scan.close();
        System.out.println("fim");
        System.exit(0);

    }

}
