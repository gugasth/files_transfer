package engtelecom.std;

import java.io.*;
import java.net.*;

public class Cliente {
    private static Socket conexao;
    private final static int TAMANHOBUFFER = 1024;

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("Quantidade de argumentos inválida.");
            return;
        }

        // leitura dos argumentos
        String servidor = args[0];
        int porta = Integer.parseInt(args[1]);
        String operacao = args[2];
        String nomeArquivo = args[3];
        String caminhoArquivo = System.getProperty("user.dir") + File.separator + "arquivos" + File.separator + nomeArquivo;

        // cria o socket do cliente
        try {
            conexao = new Socket(servidor, porta); // conecta na ip e porta do servidor

            /* Estabelece fluxos de entrada e saída */
            BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());

            // envia a operação desejada do cliente para o servidor
            saida.writeBytes(operacao + "\n");

            // verifica se é pra enviar ou baixar um arquivo do servidor
            if (operacao.equals("enviar")) {
                saida.writeBytes(nomeArquivo + "\n");
                enviarArquivo(caminhoArquivo, saida);

            } else if (operacao.equals("baixar")) {
                saida.writeBytes(nomeArquivo + "\n");
                receberArquivo(caminhoArquivo, conexao.getInputStream());

            } else {
                System.err.println("Operação inválida!");
            }
            // fecha os fluxos e o socket
            entrada.close();
            saida.close();
            conexao.close();
            // tratamento de exceções
        } catch (IOException e) {
            e.toString();
        }
    }

    /**
     * Método que envia um arquivo para o servidor
     * @param nomeArquivo é o nome do arquivo a ser enviado
     * @param saida é o fluxo de saída para os dados
     * @throws IOException
     */
    private static void enviarArquivo(String nomeArquivo, DataOutputStream saida) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(nomeArquivo);
        System.out.println("Enviando arquivo " + nomeArquivo);
        DataOutputStream outputStream = new DataOutputStream(conexao.getOutputStream());

        // vai lendo de 1024 em 1024 até o fim do arquivo
        byte[] buffer = new byte[TAMANHOBUFFER];
        int bytes;
        while ((bytes = fileInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, bytes);
        }
        System.out.println("Arquivo " + nomeArquivo + " enviado com sucesso");
        System.out.println("Tamanho: " + outputStream.size() + " bytes.");
        fileInputStream.close();
        outputStream.close();
    }

    /**
     * Método que recebe um arquivo do servidor
     * @param caminhoArquivo é o diretório onde o arquivo será salvo
     * @param inputStream é o fluxo de entrada para os dados
     * @throws IOException
     */
    private static void receberArquivo(String caminhoArquivo, InputStream inputStream) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(caminhoArquivo);

        // vai lendo de 1024 em 1024 até o fim do arquivo
        byte[] buffer = new byte[TAMANHOBUFFER];
        int bytes;
        while ((bytes = inputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, bytes);
        }
        System.out.println("Arquivo recebido com sucesso.");
        inputStream.close();
        fileOutputStream.close();
    }
}