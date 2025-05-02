/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoReferenciaFP;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author andre
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")

@Etiqueta("Referência Folha de Pagamento")
public class ReferenciaFP extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Código")
    private String codigo;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Referência FP")
    private TipoReferenciaFP tipoReferenciaFP;
    @OneToMany(mappedBy = "referenciaFP", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("inicioVigencia")
    private List<ValorReferenciaFP> valoresReferenciasFPs = Lists.newArrayList();
    @OneToMany(mappedBy = "referenciaFP", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("inicioVigencia")
    private List<FaixaReferenciaFP> faixasReferenciasFPs = Lists.newArrayList();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoReferenciaFP getTipoReferenciaFP() {
        return tipoReferenciaFP;
    }

    public void setTipoReferenciaFP(TipoReferenciaFP tipoReferenciaFP) {
        this.tipoReferenciaFP = tipoReferenciaFP;
    }

    public List<FaixaReferenciaFP> getFaixasReferenciasFPs() {
        return faixasReferenciasFPs;
    }

    public void setFaixasReferenciasFPs(List<FaixaReferenciaFP> faixasReferenciasFPs) {
        this.faixasReferenciasFPs = faixasReferenciasFPs;
    }

    public List<ValorReferenciaFP> getValoresReferenciasFPs() {
        return valoresReferenciasFPs;
    }

    public void setValoresReferenciasFPs(List<ValorReferenciaFP> valoresReferenciasFPs) {
        this.valoresReferenciasFPs = valoresReferenciasFPs;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void adicionarFaixaReferencia(FaixaReferenciaFP faixaReferencia) {
        if (!faixasReferenciasFPs.contains(faixaReferencia)) {
            faixasReferenciasFPs.add(faixaReferencia);
            Collections.sort(faixasReferenciasFPs);
        }
    }

    public void adicionarValorReferencia(ValorReferenciaFP valorReferenciaSelecionado) {
        if (!valoresReferenciasFPs.contains(valorReferenciaSelecionado)) {
            valoresReferenciasFPs.add(valorReferenciaSelecionado);
            Collections.sort(valoresReferenciasFPs);
        }
    }

    public void removerFaixaReferencia(FaixaReferenciaFP faixaReferencia) {
        if (faixasReferenciasFPs.contains(faixaReferencia)) {
            faixasReferenciasFPs.remove(faixaReferencia);
        }
    }

    public void removerValorReferencia(ValorReferenciaFP valorReferenciaFP) {
        if (valoresReferenciasFPs.contains(valorReferenciaFP)) {
            valoresReferenciasFPs.remove(valorReferenciaFP);
        }
    }

    public boolean isTipoFaixa() {
        return TipoReferenciaFP.FAIXA.equals(tipoReferenciaFP);
    }

    public boolean isTipoPercentual() {
        return TipoReferenciaFP.VALOR_PERCENTUAL.equals(tipoReferenciaFP);
    }

    public boolean isTipoValor() {
        return TipoReferenciaFP.VALOR_VALOR.equals(tipoReferenciaFP);
    }

}
