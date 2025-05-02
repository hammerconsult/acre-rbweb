package br.com.webpublico.negocios.tributario.softplan;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class EnviaArquivo {

    public static void main(String[] args) {
        String nomeArquivo = "DVA_ARQ_4020_20150618teste.zip";
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect("127.0.0.1");

            if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                ftp.login("anonymous", "");
                System.out.println("conectou!!!!!");
            } else {
                //erro ao se conectar
                ftp.disconnect();
                System.out.println("Conexão recusada");
                System.exit(1);
            }
            System.out.println(ftp.printWorkingDirectory());
            for (FTPFile ftpFile : ftp.listDirectories()) {
                System.out.println("ftp " + ftpFile.getName());
            }
            boolean b = ftp.changeWorkingDirectory("/EventosSEFAZ/Envio");
            System.out.println("mudou o diretório " + b);

            InputStream is = new FileInputStream("D:/DVA_ARQ_4020_20150618teste.zip");
            System.out.println("is " + is);

            ftp.setFileType(FTPClient.ASCII_FILE_TYPE);

            System.out.println("Enviando arquivo " + nomeArquivo + "...");

            ftp.storeFile(nomeArquivo, is);

            System.out.println("Arquivo " + nomeArquivo + " enviado com sucesso!");

            ftp.disconnect();
            System.out.println("Fim. Tchau!");
        } catch (Exception e) {
            System.out.println("Ocorreu um erro: " + e);
            System.exit(1);
        }
    }
}
