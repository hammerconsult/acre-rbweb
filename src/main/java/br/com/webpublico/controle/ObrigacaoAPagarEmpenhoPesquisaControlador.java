package br.com.webpublico.controle;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.TipoContaDespesa;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by mga on 23/06/2017.
 */
@ManagedBean
@ViewScoped
public class ObrigacaoAPagarEmpenhoPesquisaControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getHqlContador() {
        return " select count(obj.id) from Empenho obj ";
    }

    @Override
    public String getComplementoQuery() {
        return " "
            + " where (obj.saldoObrigacaoPagarAntesEmp > 0 or obj.saldo > 0) "
            + " and (select e.valor - e.saldoObrigacaoPagarAntesEmp from Empenho e where e.id = obj.id) > 0 "
            + " and to_char(obj.dataEmpenho, 'yyyy') = " + "'" + getSistemaControlador().getExercicioCorrente() + "'"
            + " and obj.categoriaOrcamentaria = '" + CategoriaOrcamentaria.NORMAL.name() + "'"
            + " and (obj.tipoContaDespesa = '" + TipoContaDespesa.VARIACAO_PATRIMONIAL_DIMINUTIVA.name() + "' "
            + "  or obj.tipoContaDespesa = '" + TipoContaDespesa.SERVICO_DE_TERCEIRO.name() + "' "
            + "  or obj.tipoContaDespesa = '" + TipoContaDespesa.PESSOAL_ENCARGOS.name() + "')"
            + " and obj.unidadeOrganizacional  = " + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + " "
            + " and obj.usuarioSistema = " + getSistemaControlador().getUsuarioCorrente().getId() + " "
            + " and " + montaCondicao() + montaOrdenacao();
    }
}
