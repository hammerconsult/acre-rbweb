package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.contabil.FiltroQDDOCA;
import br.com.webpublico.entidades.contabil.FiltroQDDOCAFuncao;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.UtilRelatorioContabil;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 09/03/2017.
 */
@Stateless
public class RelatorioQDDSuperFacade extends RelatorioContabilSuperFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProjetoAtividadeFacade acaoFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private AcaoPrincipalFacade acaoPrincipalFacade;

    @Override
    public EntityManager getEm() {
        return em;
    }

    protected StringBuilder buscarConsultaPrincipal(List<ParametrosRelatorios> parametros) {
        StringBuilder sb = new StringBuilder();
        sb.append("  select ac.id as id_acao, ")
            .append("       sb.id as id_subacao, ")
            .append("       sb.descricao as desc_acao, ")
            .append("       vw.subordinada_id as unidade_id, ")
            .append("       vw.codigo as codigo_unidade, ")
            .append("       vw.descricao as desc_unidade, ")
            .append("       vworg.codigo as codigo_orgao, ")
            .append("       vworg.descricao as desc_orgao, ")
            .append("       desp.codigo as funcional ")
            .append("  from conta c ")
            .append(" inner join provisaoppadespesa pd on c.id = pd.contadedespesa_id ")
            .append(" inner join provisaoppafonte provfonte on pd.id = provfonte.provisaoppadespesa_id ")
            .append(" inner join despesaorc desp on desp.provisaoppadespesa_id = pd.id ")
            .append(" inner join subacaoppa sb on pd.subacaoppa_id = sb.id ")
            .append(" inner join acaoppa ac on ac.id = sb.acaoppa_id and ac.exercicio_id = :ID_EXERCICIO ")
            .append(" inner join funcao func on ac.funcao_id = func.id ")
            .append(" inner join subfuncao sub on sub.id = ac.subfuncao_id ")
            .append(" inner join programappa prog on prog.id = ac.programa_id ")
            .append(" inner join tipoacaoppa tpa on tpa.id = ac.tipoacaoppa_id ")
            .append(" inner join vwhierarquiaorcamentaria vw on pd.unidadeorganizacional_id = vw.subordinada_id ")
            .append(" inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append("  left JOIN extensaoFonteRecurso extfr ON extfr.id = provfonte.extensaoFonteRecurso_id ")
            .append(" where to_date(:DATAREFERENCIA, 'DD/MM/YYYY') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:DATAREFERENCIA, 'DD/MM/YYYY')) ")
            .append("   and to_date(:DATAREFERENCIA, 'DD/MM/YYYY') between vworg.iniciovigencia and coalesce(vworg.fimvigencia, to_date(:DATAREFERENCIA, 'DD/MM/YYYY')) ")
            .append(UtilRelatorioContabil.concatenarParametros(parametros, 1, " AND "))
            .append(" group by ac.id, ")
            .append("       sb.descricao, ")
            .append("       vw.subordinada_id, ")
            .append("       vw.codigo, ")
            .append("       vw.descricao, ")
            .append("       vworg.codigo, ")
            .append("       vworg.descricao, ")
            .append("       desp.codigo, ")
            .append("       sb.id, ")
            .append("       desp.codigoreduzido ")
            .append(" order by vworg.codigo, vw.codigo, desp.codigoreduzido ");
        return sb;
    }

    protected StringBuilder buscarComplementoQuerySaldo() {
        StringBuilder sb = new StringBuilder();
        sb.append("   from saldofontedespesaorc saldo ")
            .append("   inner join fontedespesaorc fonte on saldo.fontedespesaorc_id = fonte.id ")
            .append("   inner join provisaoppafonte provfonte on fonte.provisaoppafonte_id = provfonte.id ")
            .append("   inner join contadedestinacao cd on provfonte.destinacaoderecursos_id = cd.id ")
            .append("   inner join fontederecursos fte on cd.fontederecursos_id = fte.id ")
            .append("   inner join despesaorc desp on fonte.despesaorc_id = desp.id ")
            .append("   inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id ")
            .append("   inner join vwhierarquiaorcamentaria vw on prov.unidadeorganizacional_id = vw.subordinada_id ")
            .append("   inner join vwhierarquiaorcamentaria vworg on vw.superior_id = vworg.subordinada_id ")
            .append("   inner join conta c on prov.contadedespesa_id = c.id ")
            .append("   inner join subacaoppa sb on prov.subacaoppa_id = sb.id and sb.exercicio_id = :ID_EXERCICIO ")
            .append("   inner join acaoppa ac on ac.id = sb.acaoppa_id and ac.exercicio_id = :ID_EXERCICIO ")
            .append("   inner join funcao func on ac.funcao_id = func.id ")
            .append("   inner join subfuncao sub on sub.id = ac.subfuncao_id ")
            .append("   inner join programappa prog on prog.id = ac.programa_id ")
            .append("   inner join tipoacaoppa tpa on tpa.id = ac.tipoacaoppa_id ")
            .append("   inner join ( select trunc(max(sld.datasaldo)) as maiordata ,  ")
            .append("                           sld.unidadeorganizacional_id, ")
            .append("                           sld.fontedespesaorc_id  ")
            .append("                           from saldofontedespesaorc sld ")
            .append("                            where trunc(sld.datasaldo) <= to_date(:DATAREFERENCIA, 'dd/mm/yyyy') ")
            .append("                 group by sld.unidadeorganizacional_id, ")
            .append("                          sld.fontedespesaorc_id ")
            .append("              ) maxdata on trunc(maxdata.maiordata) = trunc(saldo.datasaldo) ")
            .append("                   and maxdata.unidadeorganizacional_id = saldo.unidadeorganizacional_id  ")
            .append("                   and maxdata.fontedespesaorc_id = saldo.fontedespesaorc_id ");
        return sb;
    }

    public List<FiltroQDDOCA> buscarFiltrosQddOcaPorExercicio(Exercicio exercicio) {
        String sql = " select * from FiltroQDDOCA where exercicio_id = :idExercicio ";
        Query q = em.createNativeQuery(sql, FiltroQDDOCA.class);
        q.setParameter("idExercicio", exercicio.getId());
        List<FiltroQDDOCA> resultado = q.getResultList();
        for (FiltroQDDOCA filtroQDDOCA : resultado) {
            Hibernate.initialize(filtroQDDOCA.getFuncoes());
        }
        return q.getResultList();
    }

    public FiltroQDDOCA salvarFiltroQDDOCA(FiltroQDDOCA entity) {
        return em.merge(entity);
    }

    public FiltroQDDOCAFuncao salvarFiltroQDDOCAFuncao(FiltroQDDOCAFuncao entity) {
        return em.merge(entity);
    }

    public void removerFiltroQDDOCAFuncao(FiltroQDDOCAFuncao entity) {
        if (entity != null) {
            em.remove(em.getReference(FiltroQDDOCAFuncao.class, entity.getId()));
        }
    }

    public ProjetoAtividadeFacade getAcaoFacade() {
        return acaoFacade;
    }

    public FuncaoFacade getFuncaoFacade() {
        return funcaoFacade;
    }

    public SubFuncaoFacade getSubFuncaoFacade() {
        return subFuncaoFacade;
    }

    public SubProjetoAtividadeFacade getSubProjetoAtividadeFacade() {
        return subProjetoAtividadeFacade;
    }

    public AcaoPrincipalFacade getAcaoPrincipalFacade() {
        return acaoPrincipalFacade;
    }
}
