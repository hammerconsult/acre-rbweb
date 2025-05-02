package br.com.webpublico.controle;

import br.com.webpublico.enums.SituacaoReceitaExtra;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Edi on 03/08/2016.
 */
@ManagedBean
@ViewScoped
public class ReceitaExtraSemRetencaoPesquisaGenerica extends ComponentePesquisaGenerico {

    @Override
    public String getHqlConsulta() {
        return super.getHqlConsulta();
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        super.executarConsulta(sql, sqlCount);
    }

    @Override
    public String getComplementoQuery() {
        return " where to_char (obj.dataReceita, 'yyyy') = " + super.getSistemaControlador().getExercicioCorrente().getAno().toString() +
            " and obj.unidadeOrganizacional.id = " + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + " " +
            " and obj.retencaoPgto is null " +
            " and obj.transportado = 0 " +
            " and obj.saldo > 0 " +
            " and obj.situacaoReceitaExtra = '" + SituacaoReceitaExtra.ABERTO.name() + "'" +
            " and " + montaCondicao() + montaOrdenacao();
    }
}
