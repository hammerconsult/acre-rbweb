package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;

public class LoteImportacaoRPSNfseDTO implements Serializable, NfseDTO {

    private Long id;
    private String file;
    private String log;
    private String numero;
    private String protocolo;
    private SituacaoLoteRps situacao;
    private PrestadorServicoNfseDTO prestador;

    public LoteImportacaoRPSNfseDTO() {
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

    public SituacaoLoteRps getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoLoteRps situacao) {
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public enum SituacaoLoteRps implements NfseDTO {
        PROCESSADO("Processado", 3), AGUARDANDO("Aguardando", 1), INCONSISTENTE("Iconsistênte", 2);

        private String descricao;
        private Integer codigo;

        SituacaoLoteRps(String descricao, Integer codigo) {
            this.descricao = descricao;
            this.codigo = codigo;
        }

        public String getDescricao() {
            return descricao;
        }

        public Integer getCodigo() {
            return codigo;
        }
    }

}
