package br.com.webpublico.controle;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.util.EntidadeMetaData;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 16/10/13
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class LiquidacaoRestoPagarPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {


    @Override
    public void filtrar() {
        executarConsulta(null, null);
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        lista = new ArrayList();
        Object objeto = null;

        try {
            objeto = (Object) classe.newInstance();

            // EMPENHO
            sql = getHqlConsulta() + getComplementoQueryEmpenho();
            lista.addAll(facade.filtar(sql, objeto, inicio, maximoRegistrosTabela));


            // LIQUIDACAO
            sql = getHqlConsulta() + getComplementoQueryLiquidacao();
            lista.addAll(facade.filtar(sql, objeto, inicio, maximoRegistrosTabela));

            // PGTO
            sql = getHqlConsulta() + getComplementoQueryPagamento();
            lista.addAll(facade.filtar(sql, objeto, inicio, maximoRegistrosTabela));

            lista = new ArrayList(new HashSet(lista));

            if (lista.size() > 0) {
                setTotalDeRegistrosExistentes(lista.size());
                metadata = new EntidadeMetaData(lista.get(0).getClass());
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar Pesquisar!", ex.getMessage()));
        }
    }

    public String getComplementoQueryEmpenho() {
        return " where obj.exercicio = " + getSistemaControlador().getExercicioCorrente().getId()
                + " and obj.categoriaOrcamentaria = '" + CategoriaOrcamentaria.RESTO + "' "
                + " and obj.saldo > 0 "
                + " and obj.importado = 0 "
                + " and obj.unidadeOrganizacional = '" + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + "' "
                + " and " + montaCondicao() + montaOrdenacao();
    }

    public String getComplementoQueryLiquidacao() {
        return " inner join obj.liquidacoes l "
                + " where obj.exercicio = " + getSistemaControlador().getExercicioCorrente().getId()
                + " and obj.categoriaOrcamentaria = '" + CategoriaOrcamentaria.RESTO + "' "
                + " and l.saldo > 0 "
                + " and obj.importado = 0 "
                + " and obj.unidadeOrganizacional = '" + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + "' "
                + " and " + montaCondicao() + montaOrdenacao();
    }

    public String getComplementoQueryPagamento() {
        return " inner join obj.liquidacoes l"
                + " inner join l.pagamentos p"
                + " where obj.exercicio = " + getSistemaControlador().getExercicioCorrente().getId()
                + " and obj.categoriaOrcamentaria = '" + CategoriaOrcamentaria.RESTO + "' "
                + " and p.saldo > 0 "
                + " and p.status != '"+ StatusPagamento.PAGO +"'"
                + " and obj.importado = 0 "
                + " and obj.unidadeOrganizacional = '" + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + "' "
                + " and " + montaCondicao() + montaOrdenacao();
    }
}
