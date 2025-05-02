package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.negocios.TransferenciaSaldoContaAuxiliarFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

@ManagedBean
@ViewScoped
public class TransferenciaSaldoContaAuxiliarPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @EJB
    private TransferenciaSaldoContaAuxiliarFacade facade;

    @Override
    public void getCampos() {
        itens.add(new ItemPesquisaGenerica("", "", null));
        itens.add(new ItemPesquisaGenerica("obj.numero", "Número", String.class, false, false, true));
        itens.add(new ItemPesquisaGenerica("obj.dataLancamento", "Data de Lançamento", Date.class, false, false));
        itens.add(new ItemPesquisaGenerica("vworc.codigo", "Unidade Orçamentária.Código", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("vworc.descricao", "Unidade Orçamentária.Descrição", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("contabil.codigo", "Conta Contabil.codigo", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("contabil.descricao", "Conta Contabil.Descrição", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("auxiliarDebito.codigo", "Conta Auxiliar Débito.codigo", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("auxiliarDebito.descricao", "Conta Auxiliar Débito.Descrição", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("auxiliarCredito.codigo", "Conta Auxiliar Crédito.codigo", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("auxiliarCredito.descricao", "Conta Auxiliar Crédito.Descrição", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.valor", "Valor", BigDecimal.class, false, false));
        itensOrdenacao.addAll(itens);
    }


    private String getSelect() {
        return " from TRANSFSALDOCONTAAUXILIAR obj " +
            " inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = obj.unidadeorganizacional_id " +
            " inner join conta contabil on obj.contaContabil_id = contabil.id " +
            " inner join conta auxiliarDebito on obj.contaAuxiliarDebito_id = auxiliarDebito.id " +
            " inner join conta auxiliarCredito on obj.contaAuxiliarCredito_id = auxiliarCredito.id ";
    }

    @Override
    public String montaCondicao() {
        String montaCondicao = super.montaCondicao();
        String condicaoVigencia = " to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))";
        return condicaoVigencia + " and " + montaCondicao;
    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id) " + getSelect();
    }

    @Override
    public String getHqlConsulta() {
        String sql = " select obj.id, " +
            " obj.numero, " +
            " obj.dataLancamento, " +
            " contabil.id as contaContabil, " +
            " auxiliarDebito.id as contaAuxDebito, " +
            " auxiliarCredito.id as contaAuxCredito, " +
            " vworc.codigo || ' - ' || vworc.descricao as unidadeOrc, " +
            " obj.valor ";
        return sql + getSelect();
    }


    @Override
    public void executarConsulta(String sql, String sqlCount) {
        try {
            Object[] retorno = facade.filtrarComContadorDeRegistros(sql, sqlCount, inicio, maximoRegistrosTabela);
            processarRetorno(retorno);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao tentar pesquisar: " + ex.getMessage());
            logger.debug("executarConsulta transferencia saldo conta auxiliar {}", ex);
        }
    }

    @Override
    public Integer getTotalDeRegistrosExistentes() {
        if (totalDeRegistrosExistentes == null) {
            String hql = getHqlParaTotalDeRegistros();
            atribuirHqlConsultaRelatorioTodosRegistros();
            totalDeRegistrosExistentes = facade.count(hql).intValue();
        }

        return totalDeRegistrosExistentes;
    }

    public void processarRetorno(Object[] retorno) {
        try {
            lista = (ArrayList) retorno[0];
            totalDeRegistrosExistentes = ((Long) retorno[1]).intValue();
            if (!lista.isEmpty()) {
                metadata = new EntidadeMetaData(lista.get(0).getClass());
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao tentar Pesquisar!" + ex.getMessage());
            logger.debug("Erro ao pesquisar: ", ex);
        }
    }
}
