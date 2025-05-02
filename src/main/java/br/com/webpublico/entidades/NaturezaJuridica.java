package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoNaturezaJuridica;
import br.com.webpublico.enums.TipoNaturezaJuridica;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.dtos.NaturezaJuridicaNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Wellington
 */
@Entity
@GrupoDiagrama(nome = "CadastroEconomico")
@Audited
@Etiqueta("Natureza Jurídica")
public class NaturezaJuridica implements Serializable, NfseEntity {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 1)
    @Obrigatorio
    @Etiqueta("Código")
    private Integer codigo;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 2)
    @Obrigatorio
    @Etiqueta("Tipo de Pessoa")
    @Enumerated(EnumType.STRING)
    private TipoNaturezaJuridica tipoNaturezaJuridica;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 5)
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 3)
    @Etiqueta("Autônomo")
    private Boolean autonomo;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 4)
    @Etiqueta("Necessita CNAE")
    private Boolean necessitaCNAE;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 5)
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoNaturezaJuridica situacao;
    @Transient
    private Long criadoEm;

    public NaturezaJuridica() {
        necessitaCNAE = Boolean.TRUE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoNaturezaJuridica getTipoNaturezaJuridica() {
        return tipoNaturezaJuridica;
    }

    public void setTipoNaturezaJuridica(TipoNaturezaJuridica tipoNaturezaJuridica) {
        this.tipoNaturezaJuridica = tipoNaturezaJuridica;
    }

    public Boolean getAutonomo() {
        return autonomo;
    }

    public void setAutonomo(Boolean autonomo) {
        this.autonomo = autonomo;
    }

    public Boolean getNecessitaCNAE() {
        return necessitaCNAE != null ? necessitaCNAE : false;
    }

    public void setNecessitaCNAE(Boolean necessitaCNAE) {
        this.necessitaCNAE = necessitaCNAE;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public SituacaoNaturezaJuridica getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoNaturezaJuridica situacao) {
        this.situacao = situacao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        try {
            return this.codigo + " - " + this.descricao;
        } catch (NullPointerException ex) {
            return "";
        }
    }

    public NaturezaJuridicaNfseDTO toNfseDto() {
        return new NaturezaJuridicaNfseDTO(this.id, this.descricao);
    }
}
