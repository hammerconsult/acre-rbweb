package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.arquivo.dto.ArquivoDTO;

/**
 * Created by william on 05/09/17.
 */
public class ManualDTO implements NfseDTO {

    private Long id;
    private TipoManualDTO tipoManualDTO;
    private String nome;
    private String resumo;
    private String link;
    private Integer ordem;
    private Boolean habilitarExibicao;
    private ArquivoDTO arquivoDTO;

    public ManualDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoManualDTO getTipoManualDTO() {
        return tipoManualDTO;
    }

    public void setTipoManualDTO(TipoManualDTO tipoManualDTO) {
        this.tipoManualDTO = tipoManualDTO;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Boolean getHabilitarExibicao() {
        return habilitarExibicao;
    }

    public void setHabilitarExibicao(Boolean habilitarExibicao) {
        this.habilitarExibicao = habilitarExibicao;
    }

    public ArquivoDTO getArquivoDTO() {
        return arquivoDTO;
    }

    public void setArquivoDTO(ArquivoDTO arquivoDTO) {
        this.arquivoDTO = arquivoDTO;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
