package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Desenvolvimento on 02/02/2017.
 */
@Entity
@Audited
@Table(name = "ITEMEFETIVLEVANTGRUPO")
public class ItemEfetivacaoLevantamentoGrupoMaterial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Obrigatorio
    @ManyToOne
    private EfetivacaoLevantamentoEstoque efetivacao;

    @Obrigatorio
    @ManyToOne
    private GrupoMaterial grupoMaterial;

    @Enumerated(EnumType.STRING)
    private TipoEstoque tipoEstoque;

    private BigDecimal valorLevantamento;
    private BigDecimal valorSaldoGrupo;
    private BigDecimal valorMaterial;
    private BigDecimal quantidade;

    public ItemEfetivacaoLevantamentoGrupoMaterial() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EfetivacaoLevantamentoEstoque getEfetivacao() {
        return efetivacao;
    }

    public void setEfetivacao(EfetivacaoLevantamentoEstoque efetivacao) {
        this.efetivacao = efetivacao;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public BigDecimal getValorLevantamento() {
        return valorLevantamento;
    }

    public void setValorLevantamento(BigDecimal valorLevantamento) {
        this.valorLevantamento = valorLevantamento;
    }

    public BigDecimal getValorSaldoGrupo() {
        return valorSaldoGrupo;
    }

    public void setValorSaldoGrupo(BigDecimal valorSaldoGrupo) {
        this.valorSaldoGrupo = valorSaldoGrupo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public String getQuantidadeFormatada() {
        return Util.formataQuandoDecimal(getQuantidade(), TipoMascaraUnidadeMedida.QUATRO_CASAS_DECIMAL);
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public BigDecimal getValorDiferenca() {
        return getValorSaldoGrupo().subtract(getValorMaterial()).subtract(getValorLevantamento());
    }

    public BigDecimal getValorMaterial() {
        return valorMaterial !=null ? valorMaterial : BigDecimal.ZERO;
    }

    public void setValorMaterial(BigDecimal valorMaterial) {
        this.valorMaterial = valorMaterial;
    }
}
