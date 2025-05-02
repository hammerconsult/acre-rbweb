package br.com.webpublico.interfaces;


import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.HashMap;

public interface GeradorRelatorioAssincrono {

    public HashMap getParametros();

    public Boolean getGerando();

    public void setGerando(Boolean gerando);

    public String getReport();

    public ByteArrayOutputStream getDadosByte();

    public Connection recuperaConexaoJDBC();

    public void setDadosByte(ByteArrayOutputStream dadosByte);

}
