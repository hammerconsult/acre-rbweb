package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;
import java.util.Date;

public class CartaCorrecaoNfseDTO implements Serializable, br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private Long id;
    private Date dataEmissao;
    private Long idNotaFiscal;
    private Long sequencialCartaCorrecao;
    private String codigoVerificacao;
    private String descricaoAlteracao;
    private TomadorServicoDTO tomadorServicoNfse;
    private PrestadorServicoNfseDTO prestadorServicoNfseDTO;

    public CartaCorrecaoNfseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Long getIdNotaFiscal() {
        return idNotaFiscal;
    }

    public void setIdNotaFiscal(Long idNotaFiscal) {
        this.idNotaFiscal = idNotaFiscal;
    }

    public Long getSequencialCartaCorrecao() {
        return sequencialCartaCorrecao;
    }

    public void setSequencialCartaCorrecao(Long sequencialCartaCorrecao) {
        this.sequencialCartaCorrecao = sequencialCartaCorrecao;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public String getDescricaoAlteracao() {
        return descricaoAlteracao;
    }

    public void setDescricaoAlteracao(String descricaoAlteracao) {
        this.descricaoAlteracao = descricaoAlteracao;
    }

    public TomadorServicoDTO getTomadorServicoNfse() {
        return tomadorServicoNfse;
    }

    public void setTomadorServicoNfse(TomadorServicoDTO tomadorServicoNfse) {
        this.tomadorServicoNfse = tomadorServicoNfse;
    }

    public PrestadorServicoNfseDTO getPrestadorServicoNfseDTO() {
        return prestadorServicoNfseDTO;
    }

    public void setPrestadorServicoNfseDTO(PrestadorServicoNfseDTO prestadorServicoNfseDTO) {
        this.prestadorServicoNfseDTO = prestadorServicoNfseDTO;
    }
}
