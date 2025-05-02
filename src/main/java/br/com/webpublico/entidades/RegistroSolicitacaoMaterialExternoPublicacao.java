package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPublicacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.annotation.Generated;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 01/08/14
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "REGISTROSOLMATEXTPUBLIC")
@Etiqueta("Registro Solicitação Material Externo Publicação")
public class RegistroSolicitacaoMaterialExternoPublicacao implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Registro Solicitacao Material Externo")
    @ManyToOne
    private RegistroSolicitacaoMaterialExterno registroSolMatExterno;

    @Etiqueta("Veículo de Publicação")
    @Obrigatorio
    @ManyToOne
    private VeiculoDePublicacao veiculoDePublicacao;

    @Etiqueta("Data da Publicação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataPublicacao;

    @Etiqueta("Tipo Publicação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoPublicacao tipoPublicacao;

    @Etiqueta("Número da Edição do Veiculo de Publicação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Integer numeroEdicaoPublicacao;

    @Etiqueta("Número da Página")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Integer numeroPagina;

    @Etiqueta("Observação")
    @Pesquisavel
    @Tabelavel
    private String observacao;

    @Invisivel
    @Transient
    private Long criadoEm;

    public RegistroSolicitacaoMaterialExternoPublicacao() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegistroSolicitacaoMaterialExterno getRegistroSolMatExterno() {
        return registroSolMatExterno;
    }

    public void setRegistroSolMatExterno(RegistroSolicitacaoMaterialExterno registroSolMatExterno) {
        this.registroSolMatExterno = registroSolMatExterno;
    }

    public VeiculoDePublicacao getVeiculoDePublicacao() {
        return veiculoDePublicacao;
    }

    public void setVeiculoDePublicacao(VeiculoDePublicacao veiculoDePublicacao) {
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public TipoPublicacao getTipoPublicacao() {
        return tipoPublicacao;
    }

    public void setTipoPublicacao(TipoPublicacao tipoPublicacao) {
        this.tipoPublicacao = tipoPublicacao;
    }

    public Integer getNumeroEdicaoPublicacao() {
        return numeroEdicaoPublicacao;
    }

    public void setNumeroEdicaoPublicacao(Integer numeroEdicaoPublicacao) {
        this.numeroEdicaoPublicacao = numeroEdicaoPublicacao;
    }

    public Integer getNumeroPagina() {
        return numeroPagina;
    }

    public void setNumeroPagina(Integer numeroPagina) {
        this.numeroPagina = numeroPagina;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    public boolean isEdital() {
        try {
            return TipoPublicacao.EDITAL.equals(this.getTipoPublicacao());
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isHomologacao() {
        try {
            return TipoPublicacao.HOMOLOGACAO.equals(this.getTipoPublicacao());
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isAtaDeRegistroDePreco() {
        try {
            return TipoPublicacao.ATA_REGISTRO_PRECO.equals(this.getTipoPublicacao());
        } catch (Exception ex) {
            return false;
        }
    }
}
