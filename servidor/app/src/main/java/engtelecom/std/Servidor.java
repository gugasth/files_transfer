package engtelecom.std;

import java.io.*;
import java.net.*;


public class Servidor implements Runnable {
    private Socket conexao;
    private String diretorio;
    private final int TAMANHOBUFFER = 1024;

    public Servidor(Socket conexao, String diretorio) {
        this.conexao = conexao;
        // adiciona / ou \ no final do diretório se o usuário esquecer
        if (!diretorio.endsWith(File.separator))
            diretorio += File.separator;
        this.diretorio = diretorio;
        // imprime o diretório
        System.out.println(diretorio);
    }

    public static void main(String[] args) {
        // verifica se o usuário passou a porta e o diretório
        if (args.length != 2){
            System.err.println("Quantidade de argumentos inválida.");
            return;
        }
        // le a porta
        int porta;
        try {
            porta = Integer.parseInt(args[0]);
        } catch (Exception e) {
            e.toString();
            return;
        }
        // cria o socket do servidor
        try (ServerSocket servidorSocket = new ServerSocket(porta)) {
            // mostra que deu certo e já ta ouvindo na porta especificada
            System.out.println("Servidor aguardando conexões na porta " + porta + "...");

            while (true) { // aguarda uma conexão
                Socket conexao = servidorSocket.accept();

                // mostra que a conexão foi estabelecida quando for aceita
                System.out.println("Conexão estabelecida! " + conexao);

                // cria uma thread para tratar a conexão
                Thread clienteThread = new Thread(new Servidor(conexao, args[1]));
                clienteThread.start();
            }
            // tratamento de exceções
        } catch (IOException e) {
            e.toString();
        }
    }

    /**
     * Método executado pela thread
     */
    @Override
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());

            // descobre o que o cliente quer fazer
            String mensagem = entrada.readLine();

            // imprime a operação desejada pelo cliente
            System.out.println("Operação desejada do cliente: " + mensagem);

            // verifica se o cliente quer enviar ou baixar o arquivo
            if (mensagem.equals("enviar")) {
                String nomeArquivo = entrada.readLine();
                System.out.println("Recebendo arquivo " + nomeArquivo);

                InputStream inputStream = conexao.getInputStream();

                FileOutputStream fileOutputStream = new FileOutputStream(diretorio + nomeArquivo);

                byte[] buffer = new byte[TAMANHOBUFFER];
                int bytes;
                while ((bytes = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, bytes);
                }
                // sinaliza o recebimento
                System.out.println("Arquivo " + nomeArquivo + " recebido com sucesso.");
                // fecha os canais
                inputStream.close();
                fileOutputStream.close();


            } else if (mensagem.equals("baixar")) {
                String nomeArquivo = entrada.readLine();
                System.out.println("Enviando arquivo " + nomeArquivo);
                FileInputStream fileInputStream = new FileInputStream(diretorio + nomeArquivo);
                DataOutputStream outputStream = new DataOutputStream(conexao.getOutputStream());


                byte[] buffer = new byte[TAMANHOBUFFER];
                int bytes;
                while ((bytes = fileInputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, bytes);
                }
                System.out.println("Arquivo " + nomeArquivo + " enviado com sucesso");
                System.out.println("Tamanho: " + outputStream.size() + " bytes.");                fileInputStream.close();
                outputStream.close();
            } else {
                saida.writeBytes("Comando inválido!\n");
            }
            entrada.close();
            saida.close();
            conexao.close();
        } catch (IOException e) {
            e.toString();
        }
    }
}