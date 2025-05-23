/*
 * Codigo gerado automaticamente em Thu Apr 28 09:09:59 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.LDOPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.LOAPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.PPAPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidaLDOExeption;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcDespesaOrcDAO;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcFonteDespesaOrcDAO;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcProvisaoPPADespesaDAO;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcProvisaoPPAFonteDAO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class LOAFacade extends SuperFacadeContabil<LOA> {

    private static final String CODIGO_EVENTO_PREVISAO_INICIAL_RECEITA_NORMAL = "11111";
    private static final String CODIGO_EVENTO_PREVISAO_INICIAL_RECEITA_DEDUCAO = "13111";
    private static final String CODIGO_EVENTO_DESPESA_LOA_CREDITO_ORCAMENTARIO = "15111";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private List<ProvisaoPPADespesa> listaDespesa;
    private List<ProvisaoPPAFonte> listaProvisaoPPAFonte;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private BigDecimal total;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @EJB
    private CLPRealizadoFacade cLPRealizadoFacade;
    @EJB
    private SaldoReceitaORCFacade saldoReceitaORCFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private LDOFacade ldoFacade;
    @EJB
    private ParticipanteAcaoPPAFacade participanteAcaoPPAFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private MedicaoSubAcaoPPAFacade medicaoSubAcaoPPAFacade;
    @EJB
    private AcaoPrincipalFacade acaoPrincipalFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    private JdbcProvisaoPPAFonteDAO jdbcProvisaoPPAFonteDAO;
    private JdbcProvisaoPPADespesaDAO jdbcProvisaoPPADespesaDAO;
    private JdbcFonteDespesaOrcDAO jdbcFonteDespesaOrcDAO;
    private JdbcDespesaOrcDAO jdbcDespesaOrcDAO;


    public LOAFacade() {
        super(LOA.class);
    }

    @PostConstruct
    private void init() {
        jdbcFonteDespesaOrcDAO = (JdbcFonteDespesaOrcDAO) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcFonteDespesaOrcDAO");
        jdbcDespesaOrcDAO = (JdbcDespesaOrcDAO) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcDespesaOrcDAO");
        jdbcProvisaoPPADespesaDAO = (JdbcProvisaoPPADespesaDAO) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcProvisaoPPADespesaDAO");
        jdbcProvisaoPPAFonteDAO = (JdbcProvisaoPPAFonteDAO) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcProvisaoPPAFonteDAO");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProvisaoPPADespesa> listaPorProvisao(SubAcaoPPA subAcaoPPA) {
        Query q = em.createQuery("from ProvisaoPPADespesa p where p.subAcaoPPA = :sub");
        q.setParameter("sub", subAcaoPPA);
        return q.getResultList();
    }

    public List<ProvisaoPPAFonte> listaPorProvisaoPPADespesa(ProvisaoPPADespesa provisaoPPADespesa) {
        Query q = em.createQuery("from ProvisaoPPAFonte p where p.provisaoPPADespesa = :prov");
        q.setParameter("prov", provisaoPPADespesa);
        return q.getResultList();
    }

    public BigDecimal getTotal() {
        return total;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void validaLoa(LOA l) throws ValidaLDOExeption, ExcecaoNegocioGenerica {
        listaDespesa = new ArrayList<ProvisaoPPADespesa>();
        if (l.getLdo() == null) {
            throw new ValidaLDOExeption("A LDO não foi informada!");
        }
        LDO ldo = em.find(LDO.class, l.getLdo().getId());
        ldo.getProvisaoPPALDOs().size();
        total = new BigDecimal(BigInteger.ZERO);
        recuperarDespesa(l);
        recuperarReceita(l);
    }

    private void recuperarReceita(LOA l) {
        BigDecimal totalReceita = new BigDecimal(BigInteger.ZERO);
        for (ReceitaLOA r : l.getReceitaLOAs()) {
            if (!r.getContaDeReceita().getCodigo().startsWith("9")) {
                totalReceita = totalReceita.add(r.getValor());
            } else {
                totalReceita = totalReceita.subtract(r.getValor());
            }
        }
    }

    private void recuperarDespesa(LOA loa) {
        LDO ldo = loa.getLdo();
        List<SubAcaoPPA> subAcaoPPAs = subProjetoAtividadeFacade.recuperarSubAcaoPPAPorExercicio(ldo.getExercicio());
        for (SubAcaoPPA subAcaoPPA : subAcaoPPAs) {
            for (ProvisaoPPADespesa provisaoPPADespesa : subAcaoPPA.getProvisaoPPADespesas()) {
                total = total.add(provisaoPPADespesa.getValor());
                listaDespesa.add(provisaoPPADespesa);
            }
        }
    }

    private void validaEmpenhosAssociadosLOA(List<FonteDespesaORC> listaFontes) throws ValidaLDOExeption {
        List<Long> listaDeIdsDaFonte = getIdsListaFonteDese(listaFontes);
        montaMensagemDosEmpenhosAssociadosDe1000Em1000(listaDeIdsDaFonte);
    }

    private void montaMensagemDosEmpenhosAssociadosDe1000Em1000(List<Long> lista) throws ValidaLDOExeption {

        if (lista.size() > 1000) {

            List<Long> primeirosMilregistros = lista.subList(0, 1000);
            montaMensagemDosEmpenhosAssociadosDe1000Em1000(primeirosMilregistros);


            List<Long> restanteDaLista = lista.subList(1001, lista.size());
            montaMensagemDosEmpenhosAssociadosFonte(restanteDaLista);
        } else {
            montaMensagemDosEmpenhosAssociadosFonte(lista);
        }
    }

    private void montaMensagemDosEmpenhosAssociadosFonte(List<Long> restanteDaLista) throws ValidaLDOExeption {
        List<Empenho> empenhos = listaEmpenhoFontesDespesas(restanteDaLista);
        if (empenhos != null) {
            if (!empenhos.isEmpty()) {
                StringBuilder emp = new StringBuilder();
                for (Empenho e : empenhos) {
                    emp.append(e.toString() + "; ");
                }
                if (emp.toString() != null) {
                    throw new ValidaLDOExeption("Não é possivel desefetivar a LOA! Existem Empenhos associados! <br/> " + emp.toString());
                }
            }
        }
    }

    private List<Long> getIdsListaFonteDese(List<FonteDespesaORC> fonte) {
        StringBuilder stb = new StringBuilder();
        List<Long> l = new ArrayList<Long>();
        if (fonte != null) {
            for (FonteDespesaORC f : fonte) {
                l.add(f.getId());
            }
        }
        return l;
    }

    private void validaAlteracoesORCAssociadosLOA(List<FonteDespesaORC> listaFontes) throws ValidaLDOExeption {
        List<Long> listaDeIdsDaFonte = getIdsListaFonteDese(listaFontes);
        validaAlteracaoOrcAssociadasLOADe1000em1000(listaDeIdsDaFonte);
    }

    private void validaAlteracaoOrcAssociadasLOADe1000em1000(List<Long> listaDeIdsDaFonte) throws ValidaLDOExeption {
        if (listaDeIdsDaFonte.size() > 1000) {
            List<Long> primeirosMilregistros = listaDeIdsDaFonte.subList(0, 1000);
            validaAlteracaoOrcAssociadasLOAQuebrado(primeirosMilregistros);


            List<Long> restanteDaLista = listaDeIdsDaFonte.subList(1001, listaDeIdsDaFonte.size());
            validaAlteracaoOrcAssociadasLOADe1000em1000(restanteDaLista);
        } else {
            validaAlteracaoOrcAssociadasLOAQuebrado(listaDeIdsDaFonte);
        }
    }

    private void validaAlteracaoOrcAssociadasLOAQuebrado(List<Long> listaDeIdsDaFonte) throws ValidaLDOExeption {
        StringBuilder sql = new StringBuilder();

        sql.append("select fdo.* from FonteDespesaORC fdo  ");
        sql.append("inner JOIN AnulacaoORC anu on anu.fontedespesaorc_id = fdo.id ");
        sql.append("where fdo.id in(:lista) ");
        sql.append("UNION ALL ");
        sql.append("select forc.* from FonteDespesaORC forc ");
        sql.append("inner JOIN SuplementacaoORC sup on sup.fontedespesaorc_id = forc.id ");
        sql.append("where forc.id in(:lista) ");

        Query q = getEntityManager().createNativeQuery(sql.toString(), FonteDespesaORC.class);
        q.setParameter("lista", listaDeIdsDaFonte);
        if (!q.getResultList().isEmpty()) {
            throw new ValidaLDOExeption("Não é possivel desefetivar a LOA! Existem alterações Orçamentarias Vinculadas a LOA!");
        }
    }

    public void validaReversaoLoa(LOA l) throws ValidaLDOExeption, ExcecaoNegocioGenerica {
        validaLoa(l);
        List<FonteDespesaORC> listaFontes = new ArrayList<FonteDespesaORC>();
        for (ProvisaoPPADespesa provDespesa : listaDespesa) {
            for (DespesaORC despesa : listaDespesaOrcPorProvisao(provDespesa)) {
//                if ((despesa.getTipoDespesaORC().equals(TipoDespesaORC.ESPECIAL)) || (despesa.getTipoDespesaORC().equals(TipoDespesaORC.EXTRAORDINARIA))) {
//                    throw new ValidaLDOExeption("Não é possivel desefetivar a LOA! Existem despesas Extraordinarias ou Especiais Vinculadas a essa LOA!");
//                }
                listaFontes.addAll(fonteDespesaORCFacade.listaPorDespesaORC(despesa));
            }
        }
        if (!listaFontes.isEmpty()) {
            validaAlteracoesORCAssociadosLOA(listaFontes);
            validaEmpenhosAssociadosLOA(listaFontes);
        }

    }

    public List<Empenho> listaEmpenhoFontesDespesas(List<Long> listaFonte) {
        String hql = "from Empenho emp where emp.fonteDespesaORC.id in (:lista)";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("lista", listaFonte);
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public void efetivacaoLoa(LOA loa) throws ValidaLDOExeption {
        validaLoa(loa);
        loa.setEfetivada(true);
        loa = em.merge(loa);
        efetuaDespesa(loa, listaDespesa, OperacaoORC.DOTACAO, null);
        efetuaReceita(loa);
        setSomenteLeituraAcaoPPA(loa, true);
        salvarPortal(loa);
    }

    public void salvarPortal(LOA loa) {
        portalTransparenciaNovoFacade.salvarPortal(new LOAPortal(loa));
        portalTransparenciaNovoFacade.salvarPortal(new LDOPortal(loa.getLdo()));
        portalTransparenciaNovoFacade.salvarPortal(new PPAPortal(loa.getLdo().getPpa()));
    }

    private void setSomenteLeituraAcaoPPA(LOA loa, Boolean trueOrFalse) {
        for (AcaoPPA acao : projetoAtividadeFacade.buscarAcoesPPAPorLoa(loa)) {
            acao.setSomenteLeitura(trueOrFalse);
            for (SubAcaoPPA subacao : subProjetoAtividadeFacade.recuperarSubAcaoPPA(acao)) {
                subacao.setSomenteLeitura(trueOrFalse);

                for (MedicaoSubAcaoPPA medicao : medicaoSubAcaoPPAFacade.recuperaMedicoes(subacao)) {
                    medicao.setSomenteLeitura(trueOrFalse);
                }
                for (ProvisaoPPADespesa provisaoPPADespesa : subacao.getProvisaoPPADespesas()) {
                    provisaoPPADespesa.setSomenteLeitura(trueOrFalse);
                    for (ProvisaoPPAFonte provisaoPPAFonte : provisaoPPADespesa.getProvisaoPPAFontes()) {
                        provisaoPPAFonte.setSomenteLeitura(trueOrFalse);
                    }
                    em.merge(provisaoPPADespesa);
                }
                em.merge(subacao);
            }
            for (ParticipanteAcaoPPA participante : participanteAcaoPPAFacade.recuperaParticipanteAcaoPPa(acao)) {
                participante.setSomenteLeitura(trueOrFalse);
            }
            em.merge(acao);
        }
    }

    private void efetuaReceita(LOA l) {
        for (ReceitaLOA r : l.getReceitaLOAs()) {
            for (ReceitaLOAFonte rf : r.getReceitaLoaFontes()) {
                saldoReceitaORCFacade.geraSaldo(TipoSaldoReceitaORC.RECEITALOA, r.getLoa().getDataEfetivacao(), r.getEntidade(), r.getContaDeReceita(), rf.getDestinacaoDeRecursos().getFonteDeRecursos(), rf.getValor());
            }
        }
    }

    public List<ProvisaoPPAFonte> recuperaProvisaoPPAJDBC(ProvisaoPPADespesa provisao) {
        return jdbcProvisaoPPAFonteDAO.recuperar(provisao);
    }

    public void salvarProvisaoPPA(ProvisaoPPADespesa provisao, List<ProvisaoPPAFonte> fontes) {
        if (provisao.getId() == null) {
            jdbcProvisaoPPADespesaDAO.persistir(provisao);
        } else {
            jdbcProvisaoPPADespesaDAO.atualizar(provisao);
        }

        for (ProvisaoPPAFonte fonte : fontes) {
            fonte.setProvisaoPPADespesa(provisao);
            salvarProvisaoPPAFonte(fonte);
        }
    }

    private void salvarProvisaoPPAFonte(ProvisaoPPAFonte fonte) {
        if (fonte.getId() == null) {
            jdbcProvisaoPPAFonteDAO.persistir(fonte);
        } else {
            jdbcProvisaoPPAFonteDAO.atualizar(fonte);
        }
    }

    public void efetuaMovimentoDespesa(LOA loa, OperacaoORC op, List<ProvisaoPPAFonte> listaProvisaoPPAFonteLoaEfetivada, ProvisaoPPADespesa provisaoPPADespesa, GrupoOrcamentario grupoOrcamentario) throws ValidaLDOExeption {
        try {
            List<FonteDespesaORC> fontesDespesaOrc = new ArrayList<FonteDespesaORC>();

            DespesaORC desp = recuperaDespesaOrcPorProvisao(provisaoPPADespesa);
            for (ProvisaoPPAFonte provFonte : listaProvisaoPPAFonteLoaEfetivada) {
                if (!existeFonteNaDespesaOrc(provFonte, desp)) {
                    FonteDespesaORC fonte = new FonteDespesaORC();
                    fonte.setId(null);
                    fonte.setDespesaORC(desp);
                    fonte.setProvisaoPPAFonte(provFonte);
                    fonte.setGrupoOrcamentario(grupoOrcamentario);
                    jdbcFonteDespesaOrcDAO.persistir(fonte);
                    fontesDespesaOrc = Util.adicionarObjetoEmLista(fontesDespesaOrc, fonte);
                }
            }
            for (FonteDespesaORC fonte : fontesDespesaOrc) {
                SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                    fonte,
                    op,
                    TipoOperacaoORC.NORMAL,
                    fonte.getProvisaoPPAFonte().getValor(),
                    loa.getDataEfetivacao(),
                    provisaoPPADespesa.getUnidadeOrganizacional(),
                    fonte.getProvisaoPPAFonte().getId().toString(),
                    fonte.getProvisaoPPAFonte().getClass().getSimpleName(),
                    loa.getVersao(),
                    "Efetivação da Loa  " + loa.getLdo().getExercicio().getAno() + " versão " + loa.toString());
                saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
            }
        } catch (Exception e) {
            throw new ValidaLDOExeption(e.getMessage());
        }
    }

    private boolean existeFonteNaDespesaOrc(ProvisaoPPAFonte provFonte, DespesaORC desp) {
        for (FonteDespesaORC fonteDespesaORC : desp.getFonteDespesaORCs()) {
            if (fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().equals(provFonte.getDestinacaoDeRecursosAsContaDeDestinacao())) {
                return true;
            }
        }
        return false;

    }

    public void efetuaDespesa(LOA loa, List<ProvisaoPPADespesa> lista, OperacaoORC op, GrupoOrcamentario grupoOrcamentario) throws ValidaLDOExeption {
        try {
            List<FonteDespesaORC> fontesDespesaOrc = new ArrayList<FonteDespesaORC>();
            listaProvisaoPPAFonte = new ArrayList<ProvisaoPPAFonte>();
            for (ProvisaoPPADespesa provDespesa : lista) {
                DespesaORC desp = new DespesaORC();
                desp.setId(null);
                desp.setCodigo(gerarCodigoDespesa(provDespesa));
                desp.setCodigoReduzido(gerarCodigoReduzidoDespesa(provDespesa));
                desp.setExercicio(provDespesa.getSubAcaoPPA().getExercicio());
                desp.setTipoDespesaORC(provDespesa.getTipoDespesaORC());
                desp.setProvisaoPPADespesa(provDespesa);
                fontesDespesaOrc = new ArrayList<FonteDespesaORC>();
                for (ProvisaoPPAFonte provFonte : jdbcProvisaoPPAFonteDAO.recuperar(provDespesa)) {
                    listaProvisaoPPAFonte.add(provFonte);
                    FonteDespesaORC fonte = new FonteDespesaORC();
                    fonte.setId(null);
                    fonte.setDespesaORC(desp);
                    if (grupoOrcamentario != null) {
                        fonte.setGrupoOrcamentario(grupoOrcamentario);
                    }
                    fonte.setProvisaoPPAFonte(provFonte);
                    fontesDespesaOrc.add(fonte);
                }
                desp.setFonteDespesaORCs(fontesDespesaOrc);

                jdbcDespesaOrcDAO.persistir(desp);
                for (FonteDespesaORC fonte : fontesDespesaOrc) {
                    jdbcFonteDespesaOrcDAO.persistir(fonte);

                    SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                        fonte,
                        op,
                        TipoOperacaoORC.NORMAL,
                        fonte.getProvisaoPPAFonte().getValor(),
                        loa.getDataEfetivacao(),
                        provDespesa.getUnidadeOrganizacional(),
                        fonte.getProvisaoPPAFonte().getId().toString(),
                        fonte.getProvisaoPPAFonte().getClass().getSimpleName(),
                        loa.getVersao(),
                        "Efetivação da Loa  " + loa.getLdo().getExercicio().getAno() + " versão " + loa.toString()
                    );
                    saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao efetivar a loa: ", e);
            throw new ValidaLDOExeption(e.getMessage());
        }
    }

    private String gerarCodigoReduzidoDespesa(ProvisaoPPADespesa provDespesa) {
        String tipoA = provDespesa.getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getCodigo().toString();
        String acaoS = provDespesa.getSubAcaoPPA().getAcaoPPA().getCodigo();
        String subAca = provDespesa.getSubAcaoPPA().getCodigo();
        return tipoA + acaoS + subAca;
    }

    @Override
    public LOA recuperar(Object id) {
        LOA l;
        try {
            if (id instanceof LOA) {
                l = (LOA) id;
                l = em.find(LOA.class, l.getId());
                l.getReceitaLOAs().size();
                for (ReceitaLOA rl : l.getReceitaLOAs()) {
                    rl = em.find(ReceitaLOA.class, rl.getId());
                    if (!rl.getReceitaLoaFontes().isEmpty()) {
                        rl.getReceitaLoaFontes().size();
                    }
                    if (!rl.getPrevisaoReceitaOrc().isEmpty()) {
                        //find aqui ???
                        rl.getPrevisaoReceitaOrc().size();
                    } else {
                        receitaLOAFacade.geraPrevisoesReceitaOrc(rl);
                    }
                }
            } else {
                Long cd = (Long) id;
                l = em.find(LOA.class, cd);
                l.getReceitaLOAs().size();

                for (ReceitaLOA rl : l.getReceitaLOAs()) {
                    rl = em.find(ReceitaLOA.class, rl.getId());
                    if (!rl.getReceitaLoaFontes().isEmpty()) {
                        rl.getReceitaLoaFontes().size();
                    }
                    if (!rl.getPrevisaoReceitaOrc().isEmpty()) {
                        //find aqui ???
                        rl.getPrevisaoReceitaOrc().size();
                    } else {
                        receitaLOAFacade.geraPrevisoesReceitaOrc(rl);
                    }
                }

            }
            return l;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LOA();
    }

    //Arrumar!!! Recuperar a mascara de Entidade, Orgão e Unidade para UnidadeOrganizacional.
    private String gerarCodigoDespesa(ProvisaoPPADespesa provDespesa) {
        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), provDespesa.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        //recuperar os 10 digitos abaixo da configuracao contabil
        String orgaoUnidade = ho.getCodigo().substring(0, 10);
        String funcao = provDespesa.getSubAcaoPPA().getAcaoPPA().getFuncao().getCodigo().toString();
        String subFuncao = provDespesa.getSubAcaoPPA().getAcaoPPA().getSubFuncao().getCodigo().toString();
        String progra = provDespesa.getSubAcaoPPA().getAcaoPPA().getPrograma().getCodigo().toString();
        String tipoA = provDespesa.getSubAcaoPPA().getAcaoPPA().getTipoAcaoPPA().getCodigo().toString();
        String acaoS = provDespesa.getSubAcaoPPA().getAcaoPPA().getCodigo();
        String subAca = provDespesa.getSubAcaoPPA().getCodigo();


        String codigo = orgaoUnidade + "." + funcao + "." + subFuncao + "." + progra + "." + tipoA + acaoS + "." + subAca;
        return codigo;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void reverteEfetivacaoLoa(LOA loa) {
        loa.setEfetivada(false);
        loa.setDataEfetivacao(null);
        em.merge(loa);
        setSomenteLeituraAcaoPPA(loa, false);
        removeDespesa(listaDespesa);
        //Ainda falta ver como vai ser revertida os lançamentos padronizados.
    }

    public void removeDespesa(List<ProvisaoPPADespesa> listaDespesa) {
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        for (ProvisaoPPADespesa provDespesa : listaDespesa) {
            for (DespesaORC despOrc : listaDespesaOrcPorProvisao(provDespesa)) {
                for (FonteDespesaORC fonteDespORC : despOrc.getFonteDespesaORCs()) {
                    for (MovimentoDespesaORC mov : listaMovimentoDespPorFonte(fonteDespORC)) {
                        em.remove(mov);
                    }
                    for (SaldoFonteDespesaORC saldo : listaSaldoPorFonte(fonteDespORC)) {
                        em.remove(saldo);
                    }

                }
                em.remove(despOrc);
            }
        }
    }

    public List<LOA> validaEfetivacaoLoa(Exercicio ex) {
        String hql = "select lo from LOA lo inner join lo.ldo as ld where ld.exercicio= :param and lo.efetivada = true";
        Query q = em.createQuery(hql);
        q.setParameter("param", ex);
        return q.getResultList();
    }

    private List<DespesaORC> listaDespesaOrcPorProvisao(ProvisaoPPADespesa provDespesa) {
        String hql = "from DespesaORC d where d.provisaoPPADespesa = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", provDespesa);
        return q.getResultList();
    }

    private DespesaORC recuperaDespesaOrcPorProvisao(ProvisaoPPADespesa provDespesa) {
        try {
            String sql = "select *  FROM DespesaORC WHERE provisaoPPADespesa_id = :param ";
            Query q = em.createNativeQuery(sql, DespesaORC.class);
            q.setParameter("param", provDespesa.getId());
            q.setMaxResults(1);
            return (DespesaORC) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private List<MovimentoDespesaORC> listaMovimentoDespPorFonte(FonteDespesaORC fonteDespORC) {
        String hql = "from MovimentoDespesaORC d where d.fonteDespesaORC = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", fonteDespORC);
        return q.getResultList();
    }

    private List<SaldoFonteDespesaORC> listaSaldoPorFonte(FonteDespesaORC fonteDespORC) {
        String hql = "from SaldoFonteDespesaORC d where d.fonteDespesaORC = :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", fonteDespORC);
        return q.getResultList();
    }

    public boolean hasLoaEfetivadaNoExercicio(Exercicio exercicio) {
        String sql = " SELECT loa.* FROM LOA "
            + " INNER JOIN LDO ON LOA.LDO_ID = LDO.ID AND LDO.EXERCICIO_ID = :exercicio"
            + " WHERE LOA.EFETIVADA = 1";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }

    @Override
    public void salvar(LOA entity) {
        List<ReceitaLOA> receitasLoaNovas = new ArrayList<ReceitaLOA>();
        for (ReceitaLOA receitaLOA : entity.getReceitaLOAs()) {
            if (receitaLOA.getId() == null) {
                receitasLoaNovas.add(receitaLOA);
            }
        }
        for (ReceitaLOA receitaLOA : receitasLoaNovas) {
            for (ReceitaLOAFonte receitaLOAFonte : receitaLOA.getReceitaLoaFontes()) {
                saldoReceitaORCFacade.geraSaldo(TipoSaldoReceitaORC.RECEITALOA, receitaLOA.getDataRegistro(), receitaLOA.getEntidade(), receitaLOA.getContaDeReceita(), receitaLOAFonte.getDestinacaoDeRecursos().getFonteDeRecursos(), receitaLOAFonte.getValor());
            }
        }
        super.salvar(entity);
    }

    public SubProjetoAtividadeFacade getSubProjetoAtividadeFacade() {
        return subProjetoAtividadeFacade;
    }

    /**
     * Método que recebe a LOA selecionada como parametro e faz as devidas
     * contabilizações de débito e crédito nas contas do plano de contas de
     * acordo com a Configuração da CLP definida anteriormente.
     *
     * @param entity
     * @throws ExcecaoNegocioGenerica
     * @author Rafael Major
     */
    public void geraContabilizacao(LOA entity, Date dtContabilizacao) throws ExcecaoNegocioGenerica {
        List<CLPConfiguracaoParametro> listaPar = new ArrayList<CLPConfiguracaoParametro>();
        //contabilizacao da receita.
        CLPRealizado cr = new CLPRealizado();
        cr.setDataCLPRealizado(dtContabilizacao);
        cr.setUnidadeOrganizacional(null);
        cr.setClp(cLPRealizadoFacade.getClpPorEvento("PREVISÃO INICIAL DA RECEITA BRUTA"));
        cr.setTipoLancamentoCLP(TipoLancamentoCLPRealizado.AUTOMATICO);
        for (ReceitaLOA r : entity.getReceitaLOAs()) {
            if (!r.getContaDeReceita().getCodigo().startsWith("9")) {
                listaPar.add(new CLPConfiguracaoParametro(r.getEntidade(), r.getValor(), null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("14"), cr, entity.getComplementoContabil()));
            } else if (r.getContaDeReceita().getCodigo().startsWith("9.7")) {
                listaPar.add(new CLPConfiguracaoParametro(r.getEntidade(), r.getValor(), null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("15"), cr, entity.getComplementoContabil()));
            } else if (r.getContaDeReceita().getCodigo().startsWith("9.2")) {
                listaPar.add(new CLPConfiguracaoParametro(r.getEntidade(), r.getValor(), null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("16"), cr, entity.getComplementoContabil()));
            } else if (r.getContaDeReceita().getCodigo().startsWith("9.1")) {
                listaPar.add(new CLPConfiguracaoParametro(r.getEntidade(), r.getValor(), null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("17"), cr, entity.getComplementoContabil()));
            } else if (r.getContaDeReceita().getCodigo().startsWith("9.3") || r.getContaDeReceita().getCodigo().startsWith("9.9")) {
                listaPar.add(new CLPConfiguracaoParametro(r.getEntidade(), r.getValor(), null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("18"), cr, entity.getComplementoContabil()));
            }
        }
        cr.setcLPConfiguracaoParametro(listaPar);
        cLPRealizadoFacade.geraContabilizacao(cr);

        //Contabilizacao da despesa.
        listaPar = new ArrayList<CLPConfiguracaoParametro>();
        CLPRealizado cr2 = new CLPRealizado();
        cr2.setTipoLancamentoCLP(TipoLancamentoCLPRealizado.AUTOMATICO);
        cr2.setDataCLPRealizado(dtContabilizacao);
        cr2.setUnidadeOrganizacional(null);
        cr2.setClp(cLPRealizadoFacade.getClpPorEvento("CRÉDITO INICIAL ORIGINÁRIO DO ORÇAMENTO - DESPESA"));
        for (ProvisaoPPADespesa ppaD : listaDespesa) {
            listaPar.add(new CLPConfiguracaoParametro(ppaD.getSubAcaoPPA().getAcaoPPA().getPrograma().getResponsavel(), ppaD.getValor(), null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("13"), cr2, entity.getComplementoContabil()));
        }
        cr2.setcLPConfiguracaoParametro(listaPar);
        cLPRealizadoFacade.geraContabilizacao(cr2);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void geraContabilizacaoNovo(LOA entity) throws ExcecaoNegocioGenerica {
        if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
            throw new ExcecaoNegocioGenerica(reprocessamentoLancamentoContabilSingleton.getMensagemConcorrenciaEnquantoReprocessa());
        } else {
            if (listaDespesa == null) {
                try {
                    validaLoa(entity);
                } catch (Exception e) {
                    throw new ExcecaoNegocioGenerica(e.getMessage());
                }

            }
            for (ReceitaLOA receitaLOA : entity.getReceitaLOAs()) {
                geraContabilizacaoReceitaLoa(receitaLOA, entity);
            }

            for (ProvisaoPPADespesa provisaoPPADespesa : listaDespesa) {
                geraContabilizacaoProvisaoPPADespesa(provisaoPPADespesa, entity.getDataContabilizacao());
            }
        }
    }

    private void geraContabilizacaoProvisaoPPADespesa(ProvisaoPPADespesa entity, Date dtContabilizacao) {
        try {
            EventoContabil eventoContabil = recuperarEventoContabilDespesaLOA();
            entity = em.find(ProvisaoPPADespesa.class, entity.getId());
            entity.getProvisaoPPAFontes().size();

            for (ProvisaoPPAFonte provisaoPPAFonte : entity.getProvisaoPPAFontes()) {
                geraContabilizacaoProvisaoPPADespesaFonte(provisaoPPAFonte, dtContabilizacao);
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void geraHistoricoProvisaoPPAFonte(ProvisaoPPAFonte entity) {
        entity.gerarHistoricos();
    }

    private void geraContabilizacaoProvisaoPPADespesaFonte(ProvisaoPPAFonte entity, Date dtContabilizacao) {
        EventoContabil eventoContabil = recuperarEventoContabilDespesaLOA();
        entity.setEventoContabil(eventoContabil);
        geraHistoricoProvisaoPPAFonte(entity);

        ParametroEvento parametroEvento = new ParametroEvento();

        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
//        parametroEvento.setComplementoHistorico(entity.getProvisaoPPADespesa().getSubAcaoPPA().getCodigo() + " - " + entity.getProvisaoPPADespesa().getUnidadeOrganizacional() + " - " + Util.formataValor(entity.getValor()));
        parametroEvento.setDataEvento(dtContabilizacao);
        parametroEvento.setUnidadeOrganizacional(entity.getProvisaoPPADespesa().getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(eventoContabil);
        parametroEvento.setExercicio(((ContaDeDestinacao) entity.getDestinacaoDeRecursos()).getFonteDeRecursos().getExercicio());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId().toString());

        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValor());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);

        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity.getId().toString(), ProvisaoPPAFonte.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getProvisaoPPADespesa().getContaDeDespesa().getId().toString(), ContaDespesa.class.getSimpleName(), item));
        item.setObjetoParametros(objetos);

        parametroEvento.getItensParametrosEvento().add(item);

        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    private void geraContabilizacaoReceitaLoaFonte(ReceitaLOAFonte entity, LOA loa) {

        Date dataContabilizacao = loa.getDataContabilizacao();
        if (dataContabilizacao == null) {
            throw new ExcecaoNegocioGenerica("A LOA " + loa + " não possui data de contabilização.");
        }
        EventoContabil eventoContabil = buscarEventoContabilReceitaLOA(entity.getReceitaLOA());
        entity.setEventoContabil(eventoContabil);
        geraHistoricoReceitaLOAFonte(entity);

        ParametroEvento parametroEvento = new ParametroEvento();

        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(dataContabilizacao);
        parametroEvento.setUnidadeOrganizacional(entity.getReceitaLOA().getEntidade());
        parametroEvento.setEventoContabil(eventoContabil);
        parametroEvento.setExercicio(loa.getLdo().getExercicio());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId().toString());

        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValor());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);

        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity.getId().toString(), ReceitaLOAFonte.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getReceitaLOA().getContaDeReceita().getId().toString(), ContaReceita.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getDestinacaoDeRecursos().getFonteDeRecursos().getId().toString(), FonteDeRecursos.class.getSimpleName(), item));
        item.setObjetoParametros(objetos);

        parametroEvento.getItensParametrosEvento().add(item);

        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    private void geraContabilizacaoReceitaLoa(ReceitaLOA entity, LOA loa) {
        try {
            EventoContabil eventoContabil = buscarEventoContabilReceitaLOA(entity);

            entity = em.find(ReceitaLOA.class, entity.getId());
            entity.getReceitaLoaFontes().size();

            for (ReceitaLOAFonte receitaLOAFonte : entity.getReceitaLoaFontes()) {
                geraContabilizacaoReceitaLoaFonte(receitaLOAFonte, loa);
            }

        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void geraHistoricoReceitaLOAFonte(ReceitaLOAFonte entity) {
        entity.gerarHistoricos();
    }

    private EventoContabil buscarEventoContabilReceitaLOA(ReceitaLOA entity) {
        EventoContabil eventoContabil = null;
        String codigo = "";
        if (entity.getOperacaoReceita() != null) {
            if (OperacaoReceita.retornarOperacoesReceitaDeducao().contains(entity.getOperacaoReceita())) {
                eventoContabil = eventoContabilFacade.recuperarEventoPorCodigo(CODIGO_EVENTO_PREVISAO_INICIAL_RECEITA_DEDUCAO);
                codigo = CODIGO_EVENTO_PREVISAO_INICIAL_RECEITA_DEDUCAO;
            } else {
                eventoContabil = eventoContabilFacade.recuperarEventoPorCodigo(CODIGO_EVENTO_PREVISAO_INICIAL_RECEITA_NORMAL);
                codigo = CODIGO_EVENTO_PREVISAO_INICIAL_RECEITA_NORMAL;
            }
            if (eventoContabil == null) {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil com o código " + codigo + " para a operação selecionada na contabilização da LOA (RECEITA).");
            }
        }
        return eventoContabil;

    }

    private EventoContabil recuperarEventoContabilDespesaLOA() {
        EventoContabil eventoContabil = eventoContabilFacade.recuperarEventoPorCodigo(CODIGO_EVENTO_DESPESA_LOA_CREDITO_ORCAMENTARIO);
        if (eventoContabil == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil com o código " + CODIGO_EVENTO_DESPESA_LOA_CREDITO_ORCAMENTARIO + " para a operação selecionada na contabilização da LOA (DESPESA).");
        }
        return eventoContabil;
    }

    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EntidadeContabil> recuperarLoaDespesa(Exercicio exercicio, EventosReprocessar er) {
        List<EntidadeContabil> retorno = Lists.newArrayList();

        String sql = "select ldo.* from loa l "
            + " inner join ldo on l.ldo_id = ldo.id " +
            " where ldo.exercicio_id = :ex " +
            "  and trunc(l.dataContabilizacao) between to_date(:dataInicio,'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        Query consulta = em.createNativeQuery(sql, LDO.class);
        consulta.setParameter("ex", exercicio.getId());
        consulta.setParameter("dataInicio", DataUtil.getDataFormatada(er.getDataInicial()));
        consulta.setParameter("dataFinal", DataUtil.getDataFormatada(er.getDataFinal()));
        consulta.setMaxResults(1);
        if (!consulta.getResultList().isEmpty()) {
            LDO ldo = (LDO) consulta.getSingleResult();
            List<SubAcaoPPA> subAcaoPPAs = subProjetoAtividadeFacade.recuperarSubAcaoPPAPorExercicio(ldo.getExercicio());
            for (SubAcaoPPA subAcaoPPA : subAcaoPPAs) {
                for (ProvisaoPPADespesa provisaoPPADespesa : subAcaoPPA.getProvisaoPPADespesas()) {
                    for (ProvisaoPPAFonte fonte : provisaoPPADespesa.getProvisaoPPAFontes()) {
                        if (!er.getUnidades().isEmpty()) {
                            if (er.getUnidades().contains(provisaoPPADespesa.getUnidadeOrganizacional())) {
                                retorno.add(fonte);
                            }
                        } else {
                            retorno.add(fonte);
                        }
                    }
                }
            }
        } else {
            throw new ExcecaoNegocioGenerica("Ldo não encontrada para este exercício e data " + DataUtil.getDataFormatada(er.getDataInicial()) + " e " + DataUtil.getDataFormatada(er.getDataFinal()) + ".");
        }
        return retorno;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EntidadeContabil> recuperarLoaReceita(Exercicio exercicio, Date dataInicio, Date dataFinal) {
        String sql = "select recfonte.* from loa l "
            + " inner join ldo on l.ldo_id = ldo.id "
            + " inner join ReceitaLOA rec on l.id = rec.loa_id "
            + " inner join ReceitaLOAFonte recfonte on rec.id = recfonte.receitaloa_id"
            + " where ldo.exercicio_id = :ex"
            + "  and trunc(l.dataContabilizacao) between to_date(:dataInicio,'dd/MM/yyyy') and to_date(:dataFinal,'dd/MM/yyyy') ";

        Query consulta = em.createNativeQuery(sql, ReceitaLOAFonte.class);
        consulta.setParameter("ex", exercicio.getId());
        consulta.setParameter("dataInicio", DataUtil.getDataFormatada(dataInicio));
        consulta.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        return consulta.getResultList();
    }

    public LOA listaUltimaLoaPorExercicio(Exercicio e) {
        try {
            Query consulta = em.createNativeQuery(" select loa.* from loa " +
                " inner join ldo on loa.ldo_id = ldo.id " +
                " inner join exercicio e on ldo.exercicio_id = e.id" +
                " where e.id = :ex " +
                " order by loa.versao desc", LOA.class);
            consulta.setParameter("ex", e.getId());
            consulta.setMaxResults(1);
            return (LOA) consulta.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<LOA> buscarTodasLOA() {
        try {
            Query sql = em.createNativeQuery(" select loa.* from loa " +
                " inner join ldo on loa.ldo_id = ldo.id " +
                " inner join exercicio e on ldo.exercicio_id = e.id" +
                " order by loa.versao desc ", LOA.class);
            return sql.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<LOA> buscarLoasEfetivadas(Exercicio exercicio) {
        Query q = em.createNativeQuery(" select loa.* from loa " +
            " inner join ldo on loa.ldo_id = ldo.id " +
            " where loa.efetivada = :efetivada " +
            " and ldo.exercicio_id = :exercicio " +
            " order by loa.versao desc ", LOA.class);
        q.setParameter("efetivada", Boolean.TRUE);
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void gerarNovaVersao(LOA selecionado, Exercicio novoExercicio) {
        LDO ldo = ldoFacade.listaVigenteNoExercicio(novoExercicio);
        if (ldo == null) {
            throw new ExcecaoNegocioGenerica("Não existe nenhuma LDO para o exercício de " + novoExercicio.getAno() + ".");
        }

        HashSet<AcaoPPA> projetoAtividades = new HashSet<AcaoPPA>();
        List<SubAcaoPPA> subPA = getSubProjetoAtividadeFacade().recuperarSubAcaoPPAPorExercicio(selecionado.getLdo().getExercicio());
        for (SubAcaoPPA subAcaoPPA : subPA) {
            projetoAtividades.add(subAcaoPPA.getAcaoPPA());
        }

        for (AcaoPPA antigo : projetoAtividades) {
            AcaoPPA novo = (AcaoPPA) Util.clonarObjeto(antigo);
            novo.setId(null);
            novo.setExercicio(novoExercicio);
            novo.setOrigem(antigo);
            novo.setDataRegistro(UtilRH.getDataOperacao());
            novo.setAcaoPrincipal(acaoPrincipalFacade.getAcaoPrincipalNovoDaOrigem(antigo.getAcaoPrincipal()));
            novo.setPrograma(novo.getAcaoPrincipal() != null ? novo.getAcaoPrincipal().getPrograma() : null);
            novo.setSomenteLeitura(false);
            novo.setParticipanteAcaoPPA(duplicandoParticipanteAcaoPPA(antigo, novo));
            novo.setSubAcaoPPAs(duplicaSubAcaoPPA(antigo, novo, novoExercicio));
            if (antigo.getPrograma() != null) {
                novo.setPrograma(programaPPAFacade.getProgramaPPANovoDaOrigem(antigo.getPrograma()));
                if (!projetoAtividadeFacade.hasCodigoIgualParaPrograma(novo, novoExercicio)) {
                    throw new ExcecaoNegocioGenerica("O código informado: " + novo.getCodigo() + ", pertence a outro Projeto/Atividade para o mesmo Programa.");
                }
            }
            if (!projetoAtividadeFacade.validaCodigoIgualParaProgramaDiferente(novo)) {
                throw new ExcecaoNegocioGenerica("O código informado: " + novo.getCodigo() + ", pertence a outro Projeto/Atividade cadastrado no sistema.");
            }
            projetoAtividadeFacade.salvarNovo(novo);
        }
    }

    private List<SubAcaoPPA> duplicaSubAcaoPPA(AcaoPPA apAntigo, AcaoPPA apNovo, Exercicio exercicioNovo) {
        List<SubAcaoPPA> listaProdutoPPA = new ArrayList<SubAcaoPPA>();
        for (SubAcaoPPA antigo : projetoAtividadeFacade.recuperaSubAcaoPPA(apAntigo)) {
            antigo.setSomenteLeitura(true);
            SubAcaoPPA novo = new SubAcaoPPA();
            novo.setId(null);
            novo.setAcaoPPA(apNovo);
            novo.setDescricao(antigo.getDescricao());
            novo.setCodigo(antigo.getCodigo());
            novo.setTotalFisico(antigo.getTotalFisico());
            novo.setTotalFinanceiro(antigo.getTotalFinanceiro());
            novo.setDataRegistro(SistemaFacade.getDataCorrente());
            novo.setOrigem(antigo);
//            novo.setProvisaoPPA(produtoPPAFacade.getProdutoPPANovoDaOrigem(antigo.getP));
            novo.setDataRegistro(UtilRH.getDataOperacao());
            novo.setSomenteLeitura(false);
            novo.setExercicio(exercicioNovo);
            novo.setMedicoesSubAcaoPPA(duplicaMedicoesMedicaoSubAcaoPPA(antigo, novo));
            novo.setProvisaoPPADespesas(new ArrayList<ProvisaoPPADespesa>());
            listaProdutoPPA.add(novo);
        }
        return listaProdutoPPA;

    }

    private List<MedicaoSubAcaoPPA> duplicaMedicoesMedicaoSubAcaoPPA(SubAcaoPPA subAntigo, SubAcaoPPA subNovo) {
        List<MedicaoSubAcaoPPA> listaMedicao = new ArrayList<MedicaoSubAcaoPPA>();
        for (MedicaoSubAcaoPPA antigo : medicaoSubAcaoPPAFacade.recuperaMedicoes(subAntigo)) {
            antigo.setSomenteLeitura(true);
            MedicaoSubAcaoPPA novo = new MedicaoSubAcaoPPA();
            novo.setId(null);
            novo.setDataRegistro(UtilRH.getDataOperacao());
            novo.setDataMedicao(antigo.getDataMedicao());
            novo.setValorFisico(antigo.getValorFisico());
            novo.setValorFinanceiro(antigo.getValorFinanceiro());
            novo.setSubAcaoPPA(subNovo);
            novo.setDataRegistro(SistemaFacade.getDataCorrente());
            novo.setOrigem(antigo);
            novo.setSomenteLeitura(false);
            listaMedicao.add(novo);
        }
        return listaMedicao;
    }

    private List<ParticipanteAcaoPPA> duplicandoParticipanteAcaoPPA(AcaoPPA apAntigo, AcaoPPA apNovo) {
        List<ParticipanteAcaoPPA> listaParticipantesAP = new ArrayList<ParticipanteAcaoPPA>();
        for (ParticipanteAcaoPPA participanteAntigo : participanteAcaoPPAFacade.recuperaParticipanteAcaoPPa(apAntigo)) {
            participanteAntigo.setSomenteLeitura(true);
            ParticipanteAcaoPPA participanteNovo = new ParticipanteAcaoPPA();
            participanteNovo.setId(null);
            participanteNovo.setAcaoPPA(apNovo);
            participanteNovo.setUnidadeOrganizacional(participanteAntigo.getUnidadeOrganizacional());
            participanteNovo.setSomenteLeitura(false);
            participanteNovo.setOrigem(participanteAntigo);
            listaParticipantesAP.add(participanteNovo);
        }
        return listaParticipantesAP;
    }

    private LOA criarNovaLoa(LOA selecionado, LDO ldo) {
        LOA loa = (LOA) Util.clonarObjeto(selecionado);
        loa.setId(null);
        loa.setEfetivada(Boolean.FALSE);
        loa.setVersao("1");
        loa.setAprovacao(null);
        loa.setContabilizado(false);
        loa.setDataEfetivacao(null);
        loa.setDataRegistro(UtilRH.getDataOperacao());
        loa.setLdo(ldo);
        loa.setAtoLegal(null);
        loa.setReceitaLOAs(new ArrayList<ReceitaLOA>());
        loa.setValorDaDespesa(BigDecimal.ZERO);
        loa.setValorDaReceita(BigDecimal.ZERO);
        return em.merge(loa);
    }

    public Exercicio getExercicioCorrenteLoa() {
        try {
            String sql = "SELECT e.* FROM LOA LOA " +
                "INNER JOIN LDO LDO ON LOA.LDO_ID = LDO.ID " +
                "INNER JOIN EXERCICIO E ON LDO.EXERCICIO_ID = E.ID " +
                "WHERE LOA.APROVACAO is null " +
                " order by e.ano desc";
            Query q = em.createNativeQuery(sql, Exercicio.class);
            if (q.getSingleResult() == null) {
                return sistemaFacade.getExercicioCorrente();
            }
            return (Exercicio) q.getSingleResult();
        } catch (Exception ex) {
            return sistemaFacade.getExercicioCorrente();
        }
    }

    public List<Exercicio> buscarExerciciosLoa() {
        String sql = "SELECT e.* FROM LOA LOA " +
            "INNER JOIN LDO LDO ON LOA.LDO_ID = LDO.ID " +
            "INNER JOIN EXERCICIO E ON LDO.EXERCICIO_ID = E.ID " +
            "WHERE e.ano >= :ano " +
            " order by e.ano desc";
        Query q = em.createNativeQuery(sql, Exercicio.class);
        q.setParameter("ano", sistemaFacade.getExercicioCorrente().getAno());
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        if (entidadeContabil instanceof ReceitaLOAFonte) {
            ReceitaLOAFonte receitaLOAFonte = (ReceitaLOAFonte) entidadeContabil;
            geraContabilizacaoReceitaLoaFonte(receitaLOAFonte, receitaLOAFonte.getReceitaLOA().getLoa());
        } else if (entidadeContabil instanceof ProvisaoPPAFonte) {
            ProvisaoPPAFonte provisaoPPAFonte = (ProvisaoPPAFonte) entidadeContabil;
            LOA loa = listaUltimaLoaPorExercicio(provisaoPPAFonte.getProvisaoPPADespesa().getSubAcaoPPA().getExercicio());
            Date dtContabilizacao = loa.getDataContabilizacao();
            if (dtContabilizacao == null) {
                throw new ExcecaoNegocioGenerica("A LOA " + loa + " não possui data de contabilização.");
            }
            geraContabilizacaoProvisaoPPADespesaFonte(provisaoPPAFonte, dtContabilizacao);
        }
    }

    public BigDecimal getValorReceitaArrecadada(Date dataInicial, Date dataFinal, HierarquiaOrganizacional uo) {
        String sql = "SELECT sum(VALOR) FROM ( " +
            " SELECT COALESCE(SUM(lr.VALOR),0) as valor " +
            " FROM LANCAMENTORECEITAORC LR" +
            " inner join vwhierarquiaorcamentaria vw on lr.unidadeorganizacional_id = vw.subordinada_id" +
            "  and lr.dataLancamento between vw.iniciovigencia and coalesce(vw.fimvigencia,lr.dataLancamento)" +
            " INNER JOIN RECEITALOA RE ON LR.RECEITALOA_ID = RE.ID " +
            " INNER JOIN CONTA CONT ON  RE.CONTADERECEITA_ID = CONT.ID " +
            " WHERE LR.DATALANCAMENTO BETWEEN TO_DATE(:dataInicial, 'dd/MM/yyyy') AND TO_DATE(:dataFinal, 'dd/MM/yyyy') ";
        if (uo != null) {
            sql += " and vw.id = :id ";
        }
        sql += " union all                " +
            " SELECT COALESCE(SUM(recorcfonte.VALOR),0) *-1 as valor " +
            " FROM RECEITAORCESTORNO LRE" +
            " inner join vwhierarquiaorcamentaria vw on lre.UNIDADEORGANIZACIONALORC = vw.subordinada_id" +
            "  and lre.dataEstorno between vw.iniciovigencia and coalesce(vw.fimvigencia,lre.dataEstorno)" +
            " INNER JOIN ReceitaORCFonteEstorno recorcfonte ON recorcfonte.receitaorcestorno_id = LRE.id" +
            " INNER JOIN ReceitaLOAFonte lr ON recorcfonte.receitaloafonte_id = lr.ID" +
            " INNER JOIN RECEITALOA RE ON LR.RECEITALOA_ID = RE.ID " +
            " INNER JOIN CONTA CONT ON  RE.CONTADERECEITA_ID = CONT.ID " +
            " WHERE lre.DATAESTORNO BETWEEN TO_DATE(:dataInicial, 'dd/MM/yyyy') AND TO_DATE(:dataFinal, 'dd/MM/yyyy') ";
        if (uo != null) {
            sql += " and vw.id = :id ";
        }
        sql += ")";

        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        consulta.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        if (uo != null) {
            consulta.setParameter("id", uo.getId());
        }
        consulta.setMaxResults(1);
        try {
            return (BigDecimal) consulta.getSingleResult();
        } catch (Exception e) {
            return new BigDecimal("0");
        }
    }

    public BigDecimal getValorDespesaEmpenhada(Date dataInicial, Date dataFinal, HierarquiaOrganizacional uo) {
        String sql = "SELECT sum(valor) as valor FROM( " +
            " SELECT COALESCE(sum(A.valor), 0) AS valor FROM empenho A" +
            " inner join vwhierarquiaorcamentaria vw on A.unidadeorganizacional_id = vw.subordinada_id" +
            "  and a.dataempenho between vw.iniciovigencia and coalesce(vw.fimvigencia,a.dataempenho)" +
            " INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID " +
            " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  " +
            " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID" +
            " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID" +
            " WHERE C.EXERCICIO_ID = (select id from exercicio where ano = :exercicio) " +
            " and cast(a.dataempenho as Date) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            " and a.categoriaorcamentaria = 'NORMAL'";
        if (uo != null) {
            sql += " and vw.id = :id ";
        }
        sql += " union all " +
            " SELECT COALESCE(sum(emp.valor), 0) * - 1 AS valor FROM empenhoestorno emp " +
            " inner join empenho a on a.id = emp.empenho_id" +
            " inner join vwhierarquiaorcamentaria vw on A.unidadeorganizacional_id = vw.subordinada_id" +
            "  and emp.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia,emp.dataestorno)" +
            "  INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID " +
            " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  " +
            " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID                " +
            " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID " +
            " WHERE C.EXERCICIO_ID = (select id from exercicio where ano = :exercicio) and cast(emp.dataestorno as Date) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            " and a.categoriaorcamentaria = 'NORMAL' ";
        if (uo != null) {
            sql += " and vw.id = :id";
        }
        sql += " )";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        consulta.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        consulta.setParameter("exercicio", DataUtil.getAno(dataFinal));
        if (uo != null) {
            consulta.setParameter("id", uo.getId());
        }
        consulta.setMaxResults(1);
        try {
            return (BigDecimal) consulta.getSingleResult();
        } catch (Exception e) {
            return new BigDecimal("0");
        }
    }

    public BigDecimal getValorDespesaLiquidada(Date dataInicial, Date dataFinal, HierarquiaOrganizacional uo) {
        String sql = "SELECT sum(valor) as valor FROM( " +
            " SELECT COALESCE(sum(A.valor), 0) AS valor FROM liquidacao a" +
            " inner join vwhierarquiaorcamentaria vw on A.unidadeorganizacional_id = vw.subordinada_id" +
            "  and a.dataLiquidacao between vw.iniciovigencia and coalesce(vw.fimvigencia,a.dataLiquidacao)" +
            " inner join empenho emp on a.empenho_id = emp.id" +
            " INNER JOIN FONTEDESPESAORC B ON emp.FONTEDESPESAORC_ID = B.ID " +
            " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  " +
            " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID" +
            " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID" +
            " WHERE C.EXERCICIO_ID = (select id from exercicio where ano = :exercicio) " +
            " and cast(a.dataLiquidacao as Date) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            " and a.categoriaorcamentaria = 'NORMAL'";
        if (uo != null) {
            sql += " and vw.id = :id ";
        }
        sql += " union all " +
            " SELECT COALESCE(sum(le.valor), 0) * - 1 AS valor FROM liquidacaoestorno le" +
            " inner join liquidacao l on le.liquidacao_id = l.id" +
            " inner join empenho a on a.id = l.empenho_id" +
            " inner join vwhierarquiaorcamentaria vw on A.unidadeorganizacional_id = vw.subordinada_id" +
            "  and le.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia,le.dataestorno)" +
            "  INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID " +
            " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  " +
            " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID                " +
            " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID " +
            " WHERE C.EXERCICIO_ID = (select id from exercicio where ano = :exercicio) " +
            " and cast(le.dataestorno as Date) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            " and le.categoriaorcamentaria = 'NORMAL' ";
        if (uo != null) {
            sql += " and vw.id = :id";
        }
        sql += " )";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        consulta.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        consulta.setParameter("exercicio", DataUtil.getAno(dataFinal));
        if (uo != null) {
            consulta.setParameter("id", uo.getId());
        }
        try {
            return (BigDecimal) consulta.getSingleResult();
        } catch (Exception e) {
            return new BigDecimal("0");
        }
    }

    public BigDecimal getValorDespesaPagamento(Date dataInicial, Date dataFinal, HierarquiaOrganizacional uo) {
        String sql = "SELECT sum(valor) as valor FROM( " +
            " SELECT COALESCE(sum(A.valor), 0) AS valor FROM pagamento a" +
            " inner join liquidacao l on a.liquidacao_id = l.id" +
            " inner join vwhierarquiaorcamentaria vw on A.unidadeorganizacional_id = vw.subordinada_id" +
            "  and a.dataPagamento between vw.iniciovigencia and coalesce(vw.fimvigencia,a.dataPagamento)" +
            " inner join empenho emp on l.empenho_id = emp.id" +
            " INNER JOIN FONTEDESPESAORC B ON emp.FONTEDESPESAORC_ID = B.ID " +
            " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  " +
            " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID" +
            " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID" +
            " WHERE C.EXERCICIO_ID = (select id from exercicio where ano = :exercicio) " +
            " and cast(a.dataPagamento as Date) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            " and a.categoriaorcamentaria = 'NORMAL'";
        if (uo != null) {
            sql += " and vw.id = :id ";
        }
        sql += " union all " +
            " SELECT COALESCE(sum(pe.valor), 0) * - 1 AS valor FROM pagamentoestorno pe" +
            " inner join pagamento p on pe.pagamento_id = p.id" +
            " inner join liquidacao l on p.liquidacao_id = l.id" +
            " inner join empenho a on a.id = l.empenho_id" +
            " inner join vwhierarquiaorcamentaria vw on A.unidadeorganizacional_id = vw.subordinada_id" +
            "  and pe.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia,pe.dataestorno)" +
            "  INNER JOIN FONTEDESPESAORC B ON A.FONTEDESPESAORC_ID = B.ID " +
            " INNER JOIN DESPESAORC C ON B.DESPESAORC_ID = C.ID  " +
            " INNER JOIN PROVISAOPPADESPESA D ON C.PROVISAOPPADESPESA_ID = D.ID                " +
            " INNER JOIN CONTA E ON D.CONTADEDESPESA_ID = E.ID " +
            " WHERE C.EXERCICIO_ID = (select id from exercicio where ano = :exercicio) " +
            " and cast(pe.dataestorno as Date) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            " and pe.categoriaorcamentaria = 'NORMAL' ";
        if (uo != null) {
            sql += " and vw.id = :id";
        }
        sql += " )";
        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        consulta.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        consulta.setParameter("exercicio", DataUtil.getAno(dataFinal));
        if (uo != null) {
            consulta.setParameter("id", uo.getId());
        }
        try {
            return (BigDecimal) consulta.getSingleResult();
        } catch (Exception e) {
            return new BigDecimal("0");
        }
    }

    public Integer getAnoDaLoa() {
        LOA loa = listaUltimaLoaPorExercicio(sistemaFacade.getExercicioCorrente());
        if (loa != null) {
            return loa.getLdo().getExercicio().getAno();
        } else {
            throw new ExcecaoNegocioGenerica("Loa não encontrada para o exercício de " + sistemaFacade.getExercicioCorrente() + ".");
        }
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        List<ConsultaMovimentoContabil> retorno = Lists.newArrayList();
        retorno.add(criarConsultarReceitaLoa(filtros));
        retorno.add(criarConsultarProvisaoDespesa(filtros));
        return retorno;
    }

    public ConsultaMovimentoContabil criarConsultarReceitaLoa(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(ReceitaLOAFonte.class, filtros);
        consulta.incluirJoinsOrcamentoReceita("obj.receitaloa_id", "obj.id");
        consulta.incluirJoinsComplementar(" inner join loa loa on rec.loa_id = loa.id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "rec.entidade_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "rec.entidade_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "loa.dataContabilizacao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "loa.dataContabilizacao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        return consulta;
    }


    public ConsultaMovimentoContabil criarConsultarProvisaoDespesa(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(ProvisaoPPAFonte.class, ConsultaMovimentoContabil.clonarFiltros(filtros));
        consulta.incluirJoinsComplementar(" inner join fontedespesaorc fon on obj.id = fon.provisaoppafonte_id ");
        consulta.incluirJoinsOrcamentoDespesa(" fon.id ");
        consulta.incluirJoinsComplementar(" inner join exercicio ex on desp.exercicio_id = ex.id ");
        consulta.incluirJoinsComplementar(" inner join ldo on ex.id = ldo.exercicio_id");
        consulta.incluirJoinsComplementar(" inner join loa on ldo.id = loa.ldo_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoNota");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "prov.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "prov.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "loa.dataContabilizacao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "loa.dataContabilizacao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        return consulta;
    }
}
