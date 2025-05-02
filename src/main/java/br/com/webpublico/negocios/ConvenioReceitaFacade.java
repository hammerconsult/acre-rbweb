/*
 * Codigo gerado automaticamente em Thu Apr 05 10:40:13 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.ConvenioReceitaPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ConvenioReceitaFacade extends AbstractFacade<ConvenioReceita> {

    @EJB
    private EntidadeRepassadoraFacade entidadeRepassadoraFacade;
    @EJB
    private UnidadeMedidaFacade unidadeMedidaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private IntervenienteFacade intervenienteFacade;
    @EJB
    private TipoIntervenienteFacade tipoIntervenienteFacade;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private OcorrenciaConvenioDespFacade ocorrenciaConvenioDespFacade;
    @EJB
    private PeriodicidadeFacade periodicidadeFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConvenioReceitaFacade() {
        super(ConvenioReceita.class);
    }

    public String retornaUltimoNumeroConvenioReceita() {
        String sql = "SELECT cr.numero FROM convenioreceita cr "
            + "ORDER BY cr.numero DESC";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return "0";
        } else {
            String retorno = (String) q.getSingleResult();
            if (retorno == null) {
                return "0";
            } else {
                return retorno;
            }
        }
    }

    public Boolean verificaSubContaExistente(SubConta sb) {
        String sql = "SELECT cr.* FROM CONVENIORECEITA CR"
            + " INNER JOIN CONVENIORECEITASUBCONTA CRS ON CRS.CONVENIORECEITA_ID = CR.ID"
            + " INNER JOIN SUBCONTA SB ON CRS.SUBCONTA_ID = SB.ID "
            + " WHERE SB.ID = :param";
        Query q = em.createNativeQuery(sql, ConvenioReceita.class);
        q.setParameter("param", sb.getId());
        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public List<LancamentoReceitaOrc> listaReceitasRealizadasPorConvenioReceita(ConvenioReceita cr, List<Long> listaSubContas) {
        if (cr.getId() == null || listaSubContas == null) {
            return new ArrayList<>();
        }
        String sql = " SELECT LRORC.* FROM LANCAMENTORECEITAORC LRORC "
            + " INNER JOIN CONVENIORECEITA CR ON LRORC.CONVENIORECEITA_ID = CR.ID "
            + " INNER JOIN SUBCONTA SUB ON LRORC.SUBCONTA_ID = SUB.ID"
            + " where CR.ID = :cr "
            + " and sub.id IN (:listaContas) ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), LancamentoReceitaOrc.class);
        q.setParameter("cr", cr.getId());
        q.setParameter("listaContas", listaSubContas);
        try {
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<ReceitaORCEstorno> listaEstornoReceitasRealizadasPorConvenioReceita(ConvenioReceita cr, List<Long> listaSubcontas) {
        if (cr.getId() == null || listaSubcontas == null) {
            return new ArrayList<>();
        }
        String sql = "  SELECT ROE.* FROM RECEITAORCESTORNO ROE "
            + " INNER JOIN SUBCONTA sub ON roe.contafinanceira_id = sub.ID "
            + " where sub.id IN (:listaContas) ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), ReceitaORCEstorno.class);
        q.setParameter("listaContas", listaSubcontas);
        try {
            return q.getResultList();
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<Empenho> listaEmpenhosPorConvenioReceita(ConvenioReceita cr) {
        String sql = " SELECT EMP.* FROM EMPENHO EMP "
            + " INNER JOIN CONVENIORECEITA CR ON EMP.CONVENIORECEITA_ID = CR.ID "
            + " WHERE CR.ID = :cr ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), Empenho.class);
        q.setParameter("cr", cr.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Liquidacao> listaLiquidacaoPorConvenio(ConvenioReceita cr) {
        String sql = " SELECT LIQ.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN CONVENIORECEITA CR ON EMP.CONVENIORECEITA_ID = CR.ID "
            + " WHERE CR.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), Liquidacao.class);
        q.setParameter("param", cr.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<LiquidacaoEstorno> listaLiquidacaoEstornoPorConvenio(ConvenioReceita cr) {
        String sql = " SELECT LIQEST.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN LIQUIDACAOESTORNO LIQEST ON LIQEST.LIQUIDACAO_ID = LIQ.ID "
            + " INNER JOIN CONVENIORECEITA CR ON EMP.CONVENIORECEITA_ID = CR.ID "
            + " WHERE CR.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), LiquidacaoEstorno.class);
        q.setParameter("param", cr.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Pagamento> listaPagamentoPorConvenio(ConvenioReceita cr) {
        String sql = " SELECT PAG.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN PAGAMENTO PAG ON PAG.LIQUIDACAO_ID = LIQ.ID "
            + " INNER JOIN CONVENIORECEITA CR ON EMP.CONVENIORECEITA_ID = CR.ID "
            + " WHERE CR.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), Pagamento.class);
        q.setParameter("param", cr.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<PagamentoEstorno> listaPagamentoEstornoPorConvenio(ConvenioReceita cr) {
        String sql = " SELECT PAGEST.* FROM EMPENHO EMP "
            + " INNER JOIN LIQUIDACAO LIQ ON LIQ.EMPENHO_ID = EMP.ID "
            + " INNER JOIN PAGAMENTO PAG ON PAG.LIQUIDACAO_ID = LIQ.ID "
            + " INNER JOIN PAGAMENTOESTORNO PAGEST ON PAGEST.PAGAMENTO_ID = PAG.ID "
            + " INNER JOIN CONVENIORECEITA CR ON EMP.CONVENIORECEITA_ID = CR.ID "
            + " WHERE CR.ID = :param ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), PagamentoEstorno.class);
        q.setParameter("param", cr.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Pessoa> listaPessoaPorTipoClasseCredor(String parte) {
        String sql = " SELECT P.* FROM PESSOA P "
            + " LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID "
            + " LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID "
            + " INNER JOIN CLASSECREDORPESSOA CCP ON CCP.PESSOA_ID = P.ID "
            + " INNER JOIN CLASSECREDOR CC ON CC.ID = CCP.CLASSECREDOR_ID "
            + " WHERE CC.TIPOCLASSECREDOR = 'CONVENIIO_RECEITA' "
            + " AND ((lower(pj.nomefantasia) LIKE :parte) OR (lower(pj.nomereduzido) LIKE :parte) OR (pj.cnpj LIKE :parte) OR (lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte)) ";
        Query q = em.createNativeQuery(sql, Pessoa.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        List<Pessoa> pessoas = q.getResultList();

        if (pessoas.isEmpty()) {
            return new ArrayList<Pessoa>();
        } else {
            return pessoas;
        }
    }

    public List<Pessoa> llllistaPessoaPorTipoClasseCredor(String parte) {
        String sql = " SELECT P.* FROM CLASSECREDORPESSOA CCP "
            + " INNER JOIN CLASSECREDOR CC ON CC.ID = CCP.CLASSECREDOR_ID "
            + " LEFT JOIN PESSOA P ON P.ID = CCP.PESSOA_ID "
            + " LEFT JOIN PESSOAJURIDICA PJ ON P.ID = PJ.ID "
            + " LEFT JOIN PESSOAFISICA PF ON P.ID = PF.ID "
            + " WHERE CC.TIPOCLASSECREDOR = 'CONVENIIO_RECEITA' "
            + " AND ((lower(pj.nomefantasia) LIKE :parte) OR (lower(pj.nomereduzido) LIKE :parte) OR (pj.cnpj LIKE :parte) OR (lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte)) ";
        Query q = em.createNativeQuery(sql, Pessoa.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        List<Pessoa> pessoas = q.getResultList();

        if (pessoas.isEmpty()) {
            return new ArrayList<Pessoa>();
        } else {
            return pessoas;
        }
    }

    public List<Pessoa> listaPessoaPorTipoClasseCredorr(String parte) {
        Query q = em.createQuery("SELECT p FROM Pessoa p, PessoaJuridica pj, PessoaFisica pf, ClasseCredorPessoa ccp, ClasseCredor cc where cc.tipoClasseCredor = :parametro and cc = ccp.classeCredor and p = ccp.pessoa " +
            " and ((p = pf and pf.cpf like :parte or pf.nome like :parte) or (p = pj and pj.razaoSocial like :parte or pj.cnpj like :parte)) ");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("parametro", TipoClasseCredor.CONVENIIO_RECEITA);
        List<Pessoa> pessoas = q.getResultList();

        if (pessoas.isEmpty()) {
            return new ArrayList<Pessoa>();
        } else {
            return pessoas;
        }
    }

    public List<EmpenhoEstorno> listaEmpenhoEstornosPorConvenioReceita(ConvenioReceita cr) {
        String sql = " SELECT EMPEST.* FROM EMPENHO EMP "
            + " INNER JOIN EMPENHOESTORNO EMPEST ON EMP.ID = EMPEST.EMPENHO_ID "
            + " INNER JOIN CONVENIORECEITA CR ON EMP.CONVENIORECEITA_ID = CR.ID "
            + " WHERE CR.ID = :cr ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), EmpenhoEstorno.class);
        q.setParameter("cr", cr.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<HierarquiaOrganizacional> listaUnidadesOrganizacionais(ConvenioReceita cr) {
        String sql = " "
            + " WHERE CR.ID = :cr ";
        Query q = getEntityManager().createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("cr", cr.getId());
        if (q.getResultList() != null) {
            return q.getResultList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<ConvenioReceita> buscarFiltrandoConvenioReceita(String parte) {
        String sql = "SELECT cr.* "
            + " FROM convenioreceita cr"
            + " WHERE (lower(cr.numeroTermo) LIKE :param OR cr.numero LIKE :param OR lower(cr.nomeConvenio) LIKE :param) order by cr.numero desc";
        Query q = getEntityManager().createNativeQuery(sql, ConvenioReceita.class);
        q.setParameter("param", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public ConvenioReceita recuperaConvenioPorSubConta(SubConta sc) throws ExcecaoNegocioGenerica {
        String hql = " select cr "
            + " from ConvenioReceita cr "
            + " inner join cr.convenioReceitaSubContas crs "
            + " where crs.subConta = :sc";
        Query q = em.createQuery(hql);
        q.setParameter("sc", sc);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado nenhum Convênio de Receita para a Conta Financeira: " + sc);
        } else {
            return (ConvenioReceita) q.getSingleResult();
        }
    }

    @Override
    public ConvenioReceita recuperar(Object id) {
        ConvenioReceita cr = em.find(ConvenioReceita.class, id);
        Hibernate.initialize(cr.getConvenioRecIntervenientes());
//        cr.getAndamentoConvenioReceitas().size();
        Hibernate.initialize(cr.getTramitesConvenioRec());
        Hibernate.initialize(cr.getConvenioReceitaUnidades());
        Hibernate.initialize(cr.getConvenioReceitaSubContas());
        Hibernate.initialize(cr.getConvenioRecConta());
        Hibernate.initialize(cr.getAditivosConvenioReceitas());
        Hibernate.initialize(cr.getConvenioRecUnidMedida());
        Hibernate.initialize(cr.getPrestacaoContas());
        Hibernate.initialize(cr.getConvenioReceitaLiberacoes());
        if (cr.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(cr.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return cr;
    }

    public AndamentoConvenio recuperarAndamento(Object id) {
        AndamentoConvenio ac = em.find(AndamentoConvenio.class, id);
        return ac;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public EntidadeRepassadoraFacade getEntidadeRepassadoraFacade() {
        return entidadeRepassadoraFacade;
    }

    public UnidadeMedidaFacade getUnidadeMedidaFacade() {
        return unidadeMedidaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public IntervenienteFacade getIntervenienteFacade() {
        return intervenienteFacade;
    }

    public TipoIntervenienteFacade getTipoIntervenienteFacade() {
        return tipoIntervenienteFacade;
    }

    public HierarquiaOrganizacionalFacadeOLD getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public OcorrenciaConvenioDespFacade getOcorrenciaConvenioDespFacade() {
        return ocorrenciaConvenioDespFacade;
    }

    public PeriodicidadeFacade getPeriodicidadeFacade() {
        return periodicidadeFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public void adicionaLiberacao(LancamentoReceitaOrc lancamentoReceitaOrc) {
        for (LancReceitaFonte f : lancamentoReceitaOrc.getLancReceitaFonte()) {
            ConvenioReceitaLiberacao cvrl = new ConvenioReceitaLiberacao();
            cvrl.setConvenioReceita(lancamentoReceitaOrc.getConvenioReceita());
            cvrl.setDataLiberacaoRecurso(lancamentoReceitaOrc.getDataLancamento());
            if (f.getReceitaLoaFonte().getDestinacaoDeRecursos().getFonteDeRecursos().getCodigo() == "01") {
                cvrl.setValorLiberadoContrapartida(f.getValor());
                cvrl.setValorLiberadoConcedente(BigDecimal.ZERO);
            } else {
                cvrl.setValorLiberadoConcedente(f.getValor());
                cvrl.setValorLiberadoContrapartida(BigDecimal.ZERO);
            }
            em.persist(cvrl);
        }
    }

    public void salvarNotificaoConvenioReceita(ConvenioReceita convenioReceita) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Solicitado pelo Convênio de Receita: " + convenioReceita.toString() + " " + Util.formataValor(convenioReceita.getValorConvenio()) + " na data de " + DataUtil.getDataFormatada(convenioReceita.getDataAssinatura()));
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo("Solicitação para Abertura de Conta Financeira!");
        notificacao.setTipoNotificacao(TipoNotificacao.SOLICITACAO_ABERTURA_CONTA_FINANCEIRA_CONVENIO_RECEITA);
        notificacao.setLink("/conta-financeira/convenio-receita/" + convenioReceita.getId() + "/notificacao/$idNotificacao/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public List<ConvenioReceita> recuperarTodosPorExercicioUnidades(List<HierarquiaOrganizacional> hieraquias) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : hieraquias) {
            unidades.add(lista.getSubordinada());
        }

        String hql = "select convenio from ConvenioReceita convenio" +
            " left join convenio.entidadeConvenente entidade" +
            " where entidade.unidadeOrganizacionalOrc in (:unidades)";
        Query consulta = em.createQuery(hql, ConvenioReceita.class);
        consulta.setParameter("unidades", unidades);
        List<ConvenioReceita> resultList = consulta.getResultList();
        for (ConvenioReceita convenioReceita : resultList) {
            convenioReceita.getConvenioReceitaLiberacoes().size();
            convenioReceita.getPrestacaoContas().size();
        }

        if (resultList.isEmpty()) {
            return new ArrayList<>();
        } else {
            return resultList;
        }
    }

    @Override
    public void salvarNovo(ConvenioReceita entity) {
        super.salvarNovo(entity);
        salvarPortal(entity);
    }

    private void salvarPortal(ConvenioReceita entity) {
        portalTransparenciaNovoFacade.salvarPortal(new ConvenioReceitaPortal(entity));
    }

    @Override
    public void salvar(ConvenioReceita entity) {
        super.salvar(entity);
        salvarPortal(entity);
    }
}
