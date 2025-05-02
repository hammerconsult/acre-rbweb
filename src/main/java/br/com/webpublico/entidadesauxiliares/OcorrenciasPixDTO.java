package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class OcorrenciasPixDTO implements Serializable {
    private String codigo;
    private String versao;
    private String mensagem;
    private String ocorrencia;

    public OcorrenciasPixDTO() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(String ocorrencia) {
        this.ocorrencia = ocorrencia;
    }
}
