/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.TipoCalculoISS;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author William
 */
@ManagedBean
@ViewScoped
public class PesquisaCalculoISS extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("cadastroEconomico.pessoa", "Razão Social / Nome", Pessoa.class));
        itens.add(new ItemPesquisaGenerica("cadastroEconomico.pessoa", "CPF/CNPJ", Pessoa.class));
        itens.add(new ItemPesquisaGenerica("cadastroEconomico.inscricaoCadastral", "C.M.C", String.class));
        itens.add(new ItemPesquisaGenerica("processoCalculoISS.mesReferencia", "Mês de Referencia", Integer.class));
        itens.add(new ItemPesquisaGenerica("baseCalculo", "Base de Cálculo", BigDecimal.class));
        itens.add(new ItemPesquisaGenerica("faturamento", "Valor Faturamento", BigDecimal.class));

        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("cadastroEconomico.pessoa", "Razão Social / Nome", Pessoa.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("cadastroEconomico.pessoa", "CPF/CNPJ", Pessoa.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("cadastroEconomico.inscricaoCadastral", "C.M.C", String.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("processoCalculoISS.mesReferencia", "Mês de Referencia", Integer.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("baseCalculo", "Base de Cálculo", BigDecimal.class));
        itensOrdenacao.add(new ItemPesquisaGenerica("faturamento", "Valor Faturamento", BigDecimal.class));
    }

    @Override
    public String getHqlConsulta() {
        return "select new CalculoISS(obj.id, obj.cadastroEconomico, obj.processoCalculoISS, obj.baseCalculo, obj.faturamento, obj.valorCalculado, obj.taxaSobreIss, obj.sequenciaLancamento) from " + classe.getSimpleName() + " obj ";
    }

    private TipoCalculoISS tipoCalculo;

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        if (tipoCalculo != null) {
            condicao += " and tipoCalculoISS = '" + tipoCalculo.name() + "'";
        }
        return condicao;
    }

    public void defineTipoDoCalculoHomologado() {
        tipoCalculo = TipoCalculoISS.MENSAL;
    }

    public void defineTipoDoCalculoEstimado() {
        tipoCalculo = TipoCalculoISS.ESTIMADO;
    }

    public void defineTipoDoCalculoFixo() {
        tipoCalculo = TipoCalculoISS.FIXO;
    }
}
