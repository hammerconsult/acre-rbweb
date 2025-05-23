package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class SolicitacaoEmpenhoEstornoFacade extends AbstractFacade<SolicitacaoEmpenhoEstorno> {

    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SolicitacaoEmpenhoEstornoFacade() {
        super(SolicitacaoEmpenhoEstorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<SolicitacaoEmpenhoEstorno> buscarSolicitacaoEmpenhoEstorno(UnidadeOrganizacional unidadeOrganizacional, CategoriaOrcamentaria categoriaOrcamentaria, Empenho empenho, Exercicio exercicio) {
        String sql = " select sol.* from solicitacaoempenhoestorno sol " +
            "          inner join empenho emp on emp.id = sol.empenho_id " +
            "          where sol.empenhoestorno_id is null " +
            "          and emp.unidadeOrganizacional_id = :idUnidade " +
            "          and emp.categoriaorcamentaria = :categoria " +
            "          and emp.exercicio_id = :idExercicio " +
            (empenho != null ? " and emp.id = :idEmpenho " : "") +
            "          order by sol.dataSolicitacao desc ";
        Query q = em.createNativeQuery(sql, SolicitacaoEmpenhoEstorno.class);
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("categoria", categoriaOrcamentaria.name());
        if (empenho != null) {
            q.setParameter("idEmpenho", empenho.getId());
        }
        q.setParameter("idExercicio", exercicio.getId());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }


    public List<SolicitacaoEmpenhoEstorno> buscarSolicitacoesPendentesPorUnidadeAndEmpenho(UnidadeOrganizacional unidadeOrganizacional, Empenho empenho, CategoriaOrcamentaria categoriaOrcamentaria) {
        String sql = " select solest.* from solicitacaoempenho sol " +
            "           inner join empenho emp on emp.id = sol.empenho_id " +
            "           inner join solicitacaoempenhoestorno solest on solest.empenho_id = emp.id " +
            "          where solest.empenhoestorno_id is null " +
            "           and emp.id = :idEmpenho" +
            "           and emp.categoriaorcamentaria = :categoria" +
            "           and emp.unidadeorganizacional_id = :idUnidade ";
        Query q = em.createNativeQuery(sql, SolicitacaoEmpenhoEstorno.class);
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("idEmpenho", empenho.getId());
        q.setParameter("categoria", categoriaOrcamentaria.name());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public List<ExecucaoContratoEmpenhoEstornoItem> buscarExecucaoContratoEstornoItem(ExecucaoContratoItem execucaoContratoItem) {
        String sql = " select item.* from execucaocontratoestorno exEst " +
            "  inner join execucaocontratoempenhoest est on est.execucaocontratoestorno_id = exEst.id " +
            "  inner join execucaocontratoempestitem item on item.execucaocontratoempenhoest_id = est.id " +
            "where item.execucaocontratoitem_id = :idItemExecucao ";
        Query q = em.createNativeQuery(sql, ExecucaoContratoEmpenhoEstornoItem.class);
        q.setParameter("idItemExecucao", execucaoContratoItem.getId());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public List<ExecucaoContratoEmpenhoEstornoItem> buscarExecucaoContratoEstornoItem(Long idExecucaoEstorno) {
        String sql = " select item.* from execucaocontratoempestitem item " +
            "          inner join execucaocontratoempenhoest est on est.id = item.execucaocontratoempenhoest_id " +
            "           where est.execucaocontratoestorno_id = :idEstornoExecucao " +
            "           and item.valortotal > 0 ";
        Query q = em.createNativeQuery(sql, ExecucaoContratoEmpenhoEstornoItem.class);
        q.setParameter("idEstornoExecucao", idExecucaoEstorno);
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public String getHistoricoSolicitacaoEmpenho(ClasseCredor classeCredor, FonteDespesaORC fonteDespesaORC, ExecucaoContrato execucao) {
        HierarquiaOrganizacional hoContrato = contratoFacade.buscarHierarquiaVigenteContrato(execucao.getContrato(), new Date());
        return " Estorno de Execução nº: " + execucao.getNumero() + " - " + DataUtil.getDataFormatada(execucao.getDataLancamento())
            + " | " + " Contrato nº: " + execucao.getContrato().getNumeroContratoAno()
            + " | " + execucao.getContrato().getNumeroAnoTermo()
            + " | " + DataUtil.getDataFormatada(execucao.getContrato().getDataLancamento())
            + " | " + execucao.getContrato().getContratado()
            + " | " + classeCredor
            + " | " + " Unidade Administrativa: " + hoContrato
            + " | " + " Elemento de Despesa: " + fonteDespesaORC.getDespesaORC() + "/" + fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao()
            + " | " + " Destinação de Recurso: " + fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().toString().trim();
    }

    public String getHistoricoSolicitacaoEmpenhoExecucaoProcessoEstorno(SolicitacaoEmpenho solEmp, ExecucaoProcesso execucao) {
        HierarquiaOrganizacional hoOrc = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(execucao.getUnidadeOrcamentaria(), TipoHierarquiaOrganizacional.ORCAMENTARIA, new Date());
        return " Estorno de Execução Sem Contrato nº: " + execucao.getNumero() + " - " + DataUtil.getDataFormatada(execucao.getDataLancamento())
            + " | " + execucao.getDescricaoProcesso()
            + " | " + execucao.getFornecedor()
            + " | " + solEmp.getClasseCredor()
            + " | " + " Unidade Orçamentária: " + hoOrc
            + " | " + " Elemento de Despesa: " + solEmp.getFonteDespesaORC().getDespesaORC() + "/" + solEmp.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao()
            + " | " + " Destinação de Recurso: " + solEmp.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().toString().trim();
    }

}
