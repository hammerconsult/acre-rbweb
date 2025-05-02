/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMetasFiscais;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Paschualleto
 */
@Entity
@Audited

@Etiqueta("Metas Fiscais")
public class MetasFiscais extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("LDO")
    @ManyToOne
    private LDO ldo;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo de Metas Fiscais")
    @Enumerated(EnumType.STRING)
    private TipoMetasFiscais tipoMetasFiscais;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Etiqueta("Receita Total")
    private BigDecimal receitaTotal;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Etiqueta("Despesa Total")
    private BigDecimal despesaTotal;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @Etiqueta("Receita Primária")
    private BigDecimal receitaPrimaria;
    @Tabelavel
    @Obrigatorio
    @Monetario
    @Pesquisavel
    @Etiqueta("Despesa Primária")
    private BigDecimal despesaPrimaria;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Monetario
    @Etiqueta("Resultado Nominal")
    private BigDecimal resultadoNominal;
    @Pesquisavel
    @Obrigatorio
    @Monetario
    @Etiqueta("Dívida Pública Consolidada")
    private BigDecimal dividaPublicaConsolidada;
    @Pesquisavel
    @Obrigatorio
    @Monetario
    @Etiqueta("Dívida Pública Líquida")
    private BigDecimal dividaPublicaLiquida;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Fonte de Informação")
    private String fonteInformacao;

    public MetasFiscais() {
        super();
        this.receitaTotal = BigDecimal.ZERO;
        this.despesaTotal = BigDecimal.ZERO;
        this.receitaPrimaria = BigDecimal.ZERO;
        this.despesaPrimaria = BigDecimal.ZERO;
        this.resultadoNominal = BigDecimal.ZERO;
        this.dividaPublicaLiquida = BigDecimal.ZERO;
        this.dividaPublicaConsolidada = BigDecimal.ZERO;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoMetasFiscais getTipoMetasFiscais() {
        return tipoMetasFiscais;
    }

    public void setTipoMetasFiscais(TipoMetasFiscais tipoMetasFiscais) {
        this.tipoMetasFiscais = tipoMetasFiscais;
    }

    public BigDecimal getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(BigDecimal receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public BigDecimal getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(BigDecimal despesaTotal) {
        this.despesaTotal = despesaTotal;
    }

    public BigDecimal getReceitaPrimaria() {
        return receitaPrimaria;
    }

    public void setReceitaPrimaria(BigDecimal receitaPrimaria) {
        this.receitaPrimaria = receitaPrimaria;
    }

    public BigDecimal getDespesaPrimaria() {
        return despesaPrimaria;
    }

    public void setDespesaPrimaria(BigDecimal despesaPrimaria) {
        this.despesaPrimaria = despesaPrimaria;
    }


    public BigDecimal getResultadoNominal() {
        return resultadoNominal;
    }

    public void setResultadoNominal(BigDecimal resultadoNominal) {
        this.resultadoNominal = resultadoNominal;
    }

    public BigDecimal getDividaPublicaConsolidada() {
        return dividaPublicaConsolidada;
    }

    public void setDividaPublicaConsolidada(BigDecimal dividaPublicaConsolidada) {
        this.dividaPublicaConsolidada = dividaPublicaConsolidada;
    }

    public BigDecimal getDividaPublicaLiquida() {
        return dividaPublicaLiquida;
    }

    public void setDividaPublicaLiquida(BigDecimal dividaPublicaLiquida) {
        this.dividaPublicaLiquida = dividaPublicaLiquida;
    }

    public String getFonteInformacao() {
        return fonteInformacao;
    }

    public void setFonteInformacao(String fonteInformacao) {
        this.fonteInformacao = fonteInformacao;
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }

    @Override
    public String toString() {
        return ldo + " - " + exercicio;
    }
}
