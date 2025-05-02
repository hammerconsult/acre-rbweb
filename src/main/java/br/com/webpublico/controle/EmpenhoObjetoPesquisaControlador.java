package br.com.webpublico.controle;

import br.com.webpublico.enums.NaturezaDividaPublica;
import br.com.webpublico.enums.SituacaoDiaria;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoProposta;
import br.com.webpublico.util.Util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 07/11/13
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean
public class EmpenhoObjetoPesquisaControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getComplementoQuery() {
        EmpenhoControlador empenhoControlador = (EmpenhoControlador) Util.getControladorPeloNome("empenhoControlador");
        String consulta = montarCondicoesDeConsulta(empenhoControlador.getClassePesquisaGenerica(), empenhoControlador.selecionado.getTipoContaDespesa());
        //System.out.println("tipo de despesa..: " + empenhoControlador.selecionado.getTipoContaDespesa());
        //System.out.println(consulta);
        return consulta;
    }

    private String montarCondicoesDeConsulta(String classePesquisaGenerica, TipoContaDespesa tipoContaDespesa) {
        String retorno = " where " + super.montaCondicao() + " and ";
        if (tipoContaDespesa.equals(TipoContaDespesa.SUPRIMENTO_FUNDO)) {
            retorno += "  obj.unidadeOrganizacional.id in (" + super.getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + ")" +
                    " and obj.tipoProposta = '" + TipoProposta.SUPRIMENTO_FUNDO.name() + "'" +
                    " and obj.situacao = '" + SituacaoDiaria.DEFERIDO.name() + "'" +
                    " and obj.id not in (select prop.id from Empenho emp inner join emp.propostaConcessaoDiaria prop)";

        } else if (tipoContaDespesa.equals(TipoContaDespesa.DIVIDA_PUBLICA)) {
            retorno += " obj.categoriaDividaPublica.naturezaDividaPublica not in ('" + NaturezaDividaPublica.PRECATORIO.name() + "')";
            retorno += " and obj.categoriaDividaPublica.naturezaDividaPublica not in ('" + NaturezaDividaPublica.OPERACAO_CREDITO.name() + "')";

        } else if (tipoContaDespesa.equals(TipoContaDespesa.PRECATORIO)) {
            retorno += " obj.categoriaDividaPublica.naturezaDividaPublica in ('" + NaturezaDividaPublica.PRECATORIO.name() + "')";

        } else if (tipoContaDespesa.equals(TipoContaDespesa.DIARIA_CAMPO)) {
            retorno += " obj.unidadeOrganizacional.id in (" + super.getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + ")" +
                    " and obj.tipoProposta = '" + TipoProposta.CONCESSAO_DIARIACAMPO.name() + "'";

        } else if (tipoContaDespesa.equals(TipoContaDespesa.DIARIA_CIVIL)) {
            retorno += " obj.unidadeOrganizacional.id in (" + super.getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + ")" +
                    " and obj.tipoProposta = '" + TipoProposta.CONCESSAO_DIARIA.name() + "'" +
                    " and obj.situacao = '" + SituacaoDiaria.DEFERIDO.name() + "'" +
                    " and obj.id not in (select prop.id from Empenho emp inner join emp.propostaConcessaoDiaria prop)";

        }
        return retorno.endsWith(" and ") ? retorno + " 1=1" : retorno;
    }
}
