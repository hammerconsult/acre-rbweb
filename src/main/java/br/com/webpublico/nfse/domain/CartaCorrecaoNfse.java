package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.CartaCorrecaoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Audited
@Etiqueta("Carta Correção Nfse")
public class CartaCorrecaoNfse extends SuperEntidade implements Serializable, NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEmissao;
    @ManyToOne
    private NotaFiscal notaFiscal;
    private Long sequencialCartaCorrecao;
    private String codigoVerificacao;
    private String descricaoAlteracao;
    @ManyToOne
    private TomadorServicoNfse tomadorServicoNfse;
    @ManyToOne
    private CadastroEconomico prestador;

    @Override
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

    public NotaFiscal getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(NotaFiscal notaFiscal) {
        this.notaFiscal = notaFiscal;
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

    public TomadorServicoNfse getTomadorServicoNfse() {
        return tomadorServicoNfse;
    }

    public void setTomadorServicoNfse(TomadorServicoNfse tomadorServicoNfse) {
        this.tomadorServicoNfse = tomadorServicoNfse;
    }

    public CadastroEconomico getPrestador() {
        return prestador;
    }

    public void setPrestador(CadastroEconomico prestador) {
        this.prestador = prestador;
    }

    @Override
    public CartaCorrecaoNfseDTO toNfseDto() {
        CartaCorrecaoNfseDTO dto = new CartaCorrecaoNfseDTO();
        dto.setId(getId());
        dto.setIdNotaFiscal(getNotaFiscal().getId());
        dto.setDataEmissao(getDataEmissao());
        dto.setSequencialCartaCorrecao(getSequencialCartaCorrecao());
        dto.setCodigoVerificacao(getCodigoVerificacao());
        dto.setDescricaoAlteracao(getDescricaoAlteracao());
        dto.setTomadorServicoNfse(getTomadorServicoNfse() == null ? null : getTomadorServicoNfse().toSimpleNfseDto());
        dto.setPrestadorServicoNfseDTO(getPrestador().toNfseDto());
        return dto;
    }
}
