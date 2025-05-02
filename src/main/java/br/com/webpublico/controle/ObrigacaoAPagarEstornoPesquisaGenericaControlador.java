package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by mga on 26/07/2017.
 */
@ManagedBean
@ViewScoped
public class ObrigacaoAPagarEstornoPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {

    public String getHqlConsulta() {
        return "select distinct obj  from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id)  from ObrigacaoAPagar obj ";
    }

    @Override
    public String getComplementoQuery() {
        return " where (case when obj.empenho.id is not null then obj.saldo else obj.saldoEmpenho end) > 0 "
            + " and obj.exercicio.id = " + getSistemaControlador().getExercicioCorrente().getId() + " "
            + " and obj.usuarioSistema.id = " + getSistemaControlador().getUsuarioCorrente().getId() + " "
            + " and to_date(obj.dataLancamento, 'dd/mm/yyyy') <= '" + getSistemaControlador().getDataOperacaoFormatada() + "' "
            + " and obj.unidadeOrganizacional.id = " + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + " "
            + " and " + montaCondicao() + montaOrdenacao();
    }
}
