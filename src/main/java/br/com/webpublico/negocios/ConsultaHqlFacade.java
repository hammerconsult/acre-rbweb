/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.comum.consultasql.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Jaime
 */
@Stateless
public class ConsultaHqlFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public View recuperarObjetos(View view) {
        view.limparVariaveis();
        List<Object> objects = criarConsulta(view);
        List<String> nomeDasColunas = montarNomeDasColunas(view);
        montarColunas(view, objects, nomeDasColunas);
        return view;
    }

    private List<String> montarNomeDasColunas(View view) {
        String nomeTabelTemporaria = "TEMP_TESTE";
        criarTabelaTemporaria(view, nomeTabelTemporaria);
        List<String> nomeDasColunas = getNomeColunas(nomeTabelTemporaria);
        droparTabelaTemporaria(nomeTabelTemporaria);
        return nomeDasColunas;
    }

    private List<Object> criarConsulta(View view) {
        Query consulta = em.createNativeQuery(view.getSqlRemovendoBarraNEBarraR());
        for (ParametroView parametroView : view.getParametros()) {
            consulta.setParameter(parametroView.getCondicao(), parametroView.getValor());
        }
        if (view.getMaximoRegistrosTabela() != 0) {
            consulta.setMaxResults(view.getMaximoRegistrosTabela());
            consulta.setFirstResult(view.getInicio());
        }
        view.getObjetos().clear();
        return consulta.getResultList();
    }

    private void montarColunas(View view, List<Object> objetos, List<String> nomeDasColunas) {
        for (Object objeto : objetos) {
            List<ColunaView> colunaViews = Lists.newArrayList();
            if (objeto instanceof Object[]) {
                Object[] array = (Object[]) objeto;
                int posicao = 0;
                for (Object o : array) {
                    ColunaView coluna = preparaInstanciaColunaView(o, nomeDasColunas.get(posicao));
                    colunaViews.add(coluna);
                    posicao++;
                }
            } else {
                ColunaView coluna = preparaInstanciaColunaView(objeto, nomeDasColunas.get(0));
                colunaViews.add(coluna);
            }
            if (view.getColunas().isEmpty()) {
                view.getColunas().addAll(colunaViews);
            }

            ObjetoView objetoView = new ObjetoView(objeto, colunaViews);
            view.getObjetos().add(objetoView);
        }
    }

    private void droparTabelaTemporaria(String nomeTabelTemporaria) {
        String sqlDropTable = "DROP TABLE " + nomeTabelTemporaria;
        Query queryDropTable = em.createNativeQuery(sqlDropTable);
        queryDropTable.executeUpdate();
    }

    private List<String> getNomeColunas(String nomeTabelTemporaria) {
        String sqlGetColumNames = "select column_name from ALL_TAB_COLUMNS where table_name = '" + nomeTabelTemporaria + "'";
        Query queryGetColumNames = em.createNativeQuery(sqlGetColumNames);
        return (List<String>) queryGetColumNames.getResultList();
    }

    private void criarTabelaTemporaria(View view, String nomeTabelTemporaria) {
        String sqlCreateTable = "create table " + nomeTabelTemporaria + " as select * from ( " + view.getSqlRemovendoBarraNEBarraR() + ") ";
        Query queryCreateTable = em.createNativeQuery(sqlCreateTable);
        queryCreateTable.executeUpdate();
    }

    private ColunaView preparaInstanciaColunaView(Object o, String nomeDaColuna) {
        if (o == null) {
            return new ColunaView(" - ", nomeDaColuna, String.class, false, o);
        }
        Class classe = o.getClass();

        ColunaView colunaView = new ColunaView(o, nomeDaColuna, classe, false, o);
        if (classe.equals(BigDecimal.class) && !nomeDaColuna.toLowerCase().contains("id")) {
            colunaView.setValor("<b>" + Util.getValorRemovendoRS(new BigDecimal(((Number) o).doubleValue())) + "</b>");
        }
        if (classe.equals(java.sql.Date.class)) {
            colunaView.setValor(DataUtil.getDataFormatada((Date) o));
        }
        if (classe.equals(java.sql.Timestamp.class)) {
            colunaView.setValor(DataUtil.getDataFormatadaDiaHora((Date) o));
        }
        if (classe.equals(Boolean.class)) {
            colunaView.setValor(Util.converterBooleanSimOuNao((Boolean) o));
        }
        if (classe.isEnum()) {
            try {
                for (Field field : classe.getDeclaredFields()) {
                    if (field.getName().equals(o.toString())) {
                        colunaView.setValor(Enum.valueOf(classe, field.getName()));
                    }
                }
            } catch (Exception e) {
                colunaView.setValor(o.toString());
            }
        }
        return colunaView;
    }

    public View executarUpdate(View view) {
        view.limparVariaveis();
        String[] sqls = view.getSqlRemovendoBarraNEBarraR().split(";");
        int linhasAlteradas = 0;
        for (String sql : sqls) {
            if (!sql.trim().isEmpty()) {
                linhasAlteradas = linhasAlteradas + em.createNativeQuery(sql).executeUpdate();
            }
        }
        view.setQuantidadeDeLinhasAlteradas(linhasAlteradas);
        gerarNovoHistorico(view);
        return view;
    }

    public void gerarNovoHistorico(View view) {
        HistoricoConsultarSql historico = new HistoricoConsultarSql();
        historico.setUsuarioSistema(view.getUsuarioSistema());
        historico.setDataRegistro(new Date());
        historico.setQuantidadeDeLinhasAlteradas(view.getQuantidadeDeLinhasAlteradas());
        historico.setMotivo(view.getMotivo());
        historico.setSqlExecutado(view.getSql());
        em.persist(historico);

        List<HistoricoConsultarSql> historicoAtualziado = Lists.newArrayList(historico);
        historicoAtualziado.addAll(view.getHistoricos());
        view.setHistoricos(historicoAtualziado);
    }

    public List<HistoricoConsultarSql> buscarHistoricoUsuarioLogado() {
        String sql = "select * from HistoricoConsultarSql where usuariosistema_id = :idUsuarioCorrente order by id desc ";
        Query q = em.createNativeQuery(sql, HistoricoConsultarSql.class);
        q.setParameter("idUsuarioCorrente", sistemaFacade.getUsuarioCorrente().getId());
        return q.getResultList();
    }

    public HistoricoConsultarSql recuperarHistorico(Long id) {
        return em.find(HistoricoConsultarSql.class, id);
    }
}
