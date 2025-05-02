package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;

public class LoteImportacaoDocumentoRecebidoNfseDTO implements Serializable, NfseDTO {

    private Long id;
    private String file;
    private String log;
    private String numero;
    private Situacao situacao;
    private PrestadorServicoNfseDTO prestador;

    public LoteImportacaoDocumentoRecebidoNfseDTO() {
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public PrestadorServicoNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PrestadorServicoNfseDTO prestador) {
        this.prestador = prestador;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public enum Situacao implements NfseDTO {
        PROCESSADO("Processado"), AGUARDANDO("Aguardando"), INCONSISTENTE("Iconsistênte");

        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
