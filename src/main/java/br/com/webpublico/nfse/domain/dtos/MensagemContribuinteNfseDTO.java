package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.arquivo.dto.ArquivoDTO;

import java.util.Date;
import java.util.List;

public class MensagemContribuinteNfseDTO implements NfseDTO {

    private Long id;
    private Date emitidaEm;
    private String titulo;
    private String conteudo;
    private List<ArquivoDTO> anexos;
    private boolean lida;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEmitidaEm() {
        return emitidaEm;
    }

    public void setEmitidaEm(Date emitidaEm) {
        this.emitidaEm = emitidaEm;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public List<ArquivoDTO> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<ArquivoDTO> anexos) {
        this.anexos = anexos;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }
}
