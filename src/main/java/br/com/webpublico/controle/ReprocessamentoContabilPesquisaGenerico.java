package br.com.webpublico.controle;

import br.com.webpublico.negocios.contabil.ReprocessamentoContabilHistoricoFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;

@ManagedBean
@ViewScoped
public class ReprocessamentoContabilPesquisaGenerico extends ComponentePesquisaGenerico {
    @EJB
    private ReprocessamentoContabilHistoricoFacade facade;

    private String getSelect() {
        StringBuilder sql = new StringBuilder();
        sql.append("  from REPROCESSCONTABILHISTORICO obj ")
            .append(" inner join usuariosistema usu on obj.USUARIOSISTEMA_ID = usu.id ")
            .append("  left join pessoafisica pf on usu.PESSOAFISICA_ID = pf.id ");
        return sql.toString();
    }

    @Override
    public Integer getTotalDeRegistrosExistentes() {
        if (totalDeRegistrosExistentes == null) {
            String sql = getHqlParaTotalDeRegistros();
            atribuirHqlConsultaRelatorioTodosRegistros();
            totalDeRegistrosExistentes = facade.count(sql).intValue();
        }

        return totalDeRegistrosExistentes;
    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id) " + getSelect();
    }

    @Override
    public String getHqlConsulta() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select obj.id,  ")
            .append("       obj.DATAHISTORICO,  ")
            .append("       obj.dataInicial,  ")
            .append("       obj.dataFinal,  ")
            .append("       obj.PROCESSADOSSEMERRO,  ")
            .append("       obj.total,  ")
            .append("       obj.DATAHORAINICIO,  ")
            .append("       obj.DATAHORATERMINO,  ")
            .append("       obj.decorrido,  ")
            .append("       coalesce(pf.nome, usu.login) ");
        return sql + getSelect();
    }

    @Override
    public void executarConsulta(String sql, String sqlCount) {
        try {
            Object[] retorno = facade.filtrarComContadorDeRegistros(sql, sqlCount, inicio, maximoRegistrosTabela);
            lista = (ArrayList) retorno[0];
            totalDeRegistrosExistentes = ((Long) retorno[1]).intValue();
            if (!lista.isEmpty()) {
                metadata = new EntidadeMetaData(lista.get(0).getClass());
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao tentar pesquisar: " + ex.getMessage());
            logger.debug("executarConsulta reprocessamento contabil {}", ex);
        }
    }
}
