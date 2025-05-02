package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoReconhecimentoObrigacaoPagar;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by mga on 22/06/2017.
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Configuração de Obrigação a Pagar")
public class ConfigObrigacaoAPagar extends ConfiguracaoEvento {

    @ManyToOne
    @Tabelavel
    @Etiqueta(value = "Conta de Despesa")
    private Conta conta;

    @Etiqueta("Tipo de Despesa")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    private TipoContaDespesa tipoContaDespesa;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Subtipo de Despesa")
    private SubTipoDespesa subTipoDespesa;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Reconhecimento")
    private TipoReconhecimentoObrigacaoPagar tipoReconhecimento;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Categoria Orçamentária")
    private CategoriaOrcamentaria categoriaOrcamentaria;

    public ConfigObrigacaoAPagar() {
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public SubTipoDespesa getSubTipoDespesa() {
        return subTipoDespesa;
    }

    public void setSubTipoDespesa(SubTipoDespesa subTipoDespesa) {
        this.subTipoDespesa = subTipoDespesa;
    }

    public TipoReconhecimentoObrigacaoPagar getTipoReconhecimento() {
        return tipoReconhecimento;
    }

    public void setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar tipoReconhecimento) {
        this.tipoReconhecimento = tipoReconhecimento;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
