/*
 * Codigo gerado automaticamente em Fri Apr 01 10:09:43 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoLancamentoCLPRealizado;
import br.com.webpublico.util.Persistencia;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class PPAFacade extends AbstractFacade<PPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private IndicadorProgramaPPAFacade indicadorProgramaPPAFacade;
    @EJB
    private ParticipanteAcaoPPAFacade participanteAcaoPPAFacade;
    @EJB
    private MedicaoSubAcaoPPAFacade medicaoSubAcaoPPAFacade;
    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;
    @EJB
    private ProvisaoPPADespesaFacade provisaoPPADespesaFacade;
    @EJB
    private ProvisaoPPAFonteFacade provisaoPPAFonteFacade;
    @EJB
    private PublicoAlvoProgramaPPAFacade publicoAlvoProgramaPPAFacade;
    @EJB
    private ReceitaExercicioPPAFacade receitaExercicioPPAFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private LDOFacade lDOFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CLPRealizadoFacade cLPRealizadoFacade;
    @EJB
    private AcaoPrincipalFacade acaoPrincipalFacade;
    @EJB
    private ProdutoPPAFacade produtoPPAFacade;
    @EJB
    private ParticipanteAcaoPrincipalFacade participanteAcaoPrincipalFacade;
    @EJB
    private MedicaoProvisaoPPAFacade medicaoProvisaoPPAFacade;
    @EJB
    private PlanejamentoEstrategicoFacade planejamentoEstrategicoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;

    public PPAFacade() {
        super(PPA.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Método que recebe o PPA selecionado como parametro e faz as devidas
     * contabilizações de débito e crédito nas contas do plano de contas de
     * acordo com a Configuração da CLP definida anteriormente.
     *
     * @param entity
     * @throws ExcecaoNegocioGenerica
     * @author Rafael Major
     */
    public void geraContabilizacao(PPA entity, Date dtContabilizacao) throws ExcecaoNegocioGenerica {
        BigDecimal somaAcoes = new BigDecimal(BigInteger.ZERO);
        List<CLPConfiguracaoParametro> listaPar = new ArrayList<CLPConfiguracaoParametro>();
        CLPRealizado cr = new CLPRealizado();
        cr.setDataCLPRealizado(dtContabilizacao);
        cr.setUnidadeOrganizacional(null);
        cr.setTipoLancamentoCLP(TipoLancamentoCLPRealizado.AUTOMATICO);
        cr.setClp(cLPRealizadoFacade.getClpPorEvento("APROVAÇÃO DO PPA"));
        for (ProgramaPPA prog : programaPPAFacade.recuperaProgramasPPA(entity)) {
            somaAcoes = new BigDecimal(BigInteger.ZERO);
            //ARRUMAR PLANEJAMENTO - TODO
//            for (AcaoPPA acao : prog.getAcoes()) {
//                somaAcoes = somaAcoes.add(acao.getTotalFinanceiro());
//            }

//            if (prog.getResponsavel().getClassificacaoUO() == null) {
//                throw new ExcecaoNegocioGenerica("Favor informar uma classificação na Unidade Organizacional vinculada a este PPA");
//            } else if (prog.getResponsavel().getClassificacaoUO().equals(ClassificacaoUO.PODEREXECUTIVO)) {
//                listaPar.add(new CLPConfiguracaoParametro(prog.getResponsavel(), somaAcoes, null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("19"), cr, entity.getComplementoContabil()));
//            } else if (prog.getResponsavel().getClassificacaoUO().equals(ClassificacaoUO.PODERLEGISLATIVO)) {
//                listaPar.add(new CLPConfiguracaoParametro(prog.getResponsavel(), somaAcoes, null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("20"), cr, entity.getComplementoContabil()));
//            } else if (prog.getResponsavel().getClassificacaoUO().equals(ClassificacaoUO.RPPS)) {
//                listaPar.add(new CLPConfiguracaoParametro(prog.getResponsavel(), somaAcoes, null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("21"), cr, entity.getComplementoContabil()));
//            } else if (prog.getResponsavel().getClassificacaoUO().equals(ClassificacaoUO.DEMAISENTIDADES)) {
//                listaPar.add(new CLPConfiguracaoParametro(prog.getResponsavel(), somaAcoes, null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("22"), cr, entity.getComplementoContabil()));
//            }
        }
        cr.setcLPConfiguracaoParametro(listaPar);
        cLPRealizadoFacade.geraContabilizacao(cr);
    }

    public void geraContabilizacaoAlocacaoPPA(PPA entity, Date dtContabilizacao) throws ExcecaoNegocioGenerica {
        BigDecimal somaAcoes = new BigDecimal(BigInteger.ZERO);
        List<CLPConfiguracaoParametro> listaPar = new ArrayList<CLPConfiguracaoParametro>();
        CLPRealizado cr = new CLPRealizado();
        cr.setDataCLPRealizado(dtContabilizacao);
        cr.setUnidadeOrganizacional(null);
        cr.setTipoLancamentoCLP(TipoLancamentoCLPRealizado.AUTOMATICO);
        cr.setClp(cLPRealizadoFacade.getClpPorEvento("ALOCAÇÃO DO PPA APROVADO NA LOA"));
        for (ProgramaPPA prog : programaPPAFacade.recuperaProgramasPPA(entity)) {
            somaAcoes = new BigDecimal(BigInteger.ZERO);

            for (AcaoPPA acao : prog.getProjetosAtividades()) {
                somaAcoes = somaAcoes.add(acao.getTotalFinanceiro());
            }
//            if (prog.getResponsavel().getClassificacaoUO() == null) {
//                throw new ExcecaoNegocioGenerica("Favor informar uma classificação na Unidade Organizacional vinculada a este PPA");
//            } else if (prog.getResponsavel().getClassificacaoUO().equals(ClassificacaoUO.PODEREXECUTIVO)) {
//                listaPar.add(new CLPConfiguracaoParametro(prog.getResponsavel(), somaAcoes, null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("19"), cr, entity.getComplementoContabil()));
//            } else if (prog.getResponsavel().getClassificacaoUO().equals(ClassificacaoUO.PODERLEGISLATIVO)) {
//                listaPar.add(new CLPConfiguracaoParametro(prog.getResponsavel(), somaAcoes, null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("20"), cr, entity.getComplementoContabil()));
//            } else if (prog.getResponsavel().getClassificacaoUO().equals(ClassificacaoUO.RPPS)) {
//                listaPar.add(new CLPConfiguracaoParametro(prog.getResponsavel(), somaAcoes, null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("21"), cr, entity.getComplementoContabil()));
//            } else if (prog.getResponsavel().getClassificacaoUO().equals(ClassificacaoUO.DEMAISENTIDADES)) {
//                listaPar.add(new CLPConfiguracaoParametro(prog.getResponsavel(), somaAcoes, null, null, cLPRealizadoFacade.getRecuperaClpTagPorCodigoDescricao("22"), cr, entity.getComplementoContabil()));
//            }
        }
        cr.setcLPConfiguracaoParametro(listaPar);
        cLPRealizadoFacade.geraContabilizacao(cr);
    }

    /**
     * Método que recebe o exercicio como parametro e retorna a todos os PPA do
     * exercicio.
     *
     * @param exercício
     * @return {@link java.util.List}&lt;{@link br.com.webpublico.entidades.PPA}&gt;
     * @author Wlademir
     * @see PPA
     * @see PlanejamentoEstrategico
     * @see ExercicioPlanoEstrategico
     */
    public List<PPA> listaTodosPpaExercicio(Exercicio exerc, String s) {
        String hql = " select p from PPA p,PlanejamentoEstrategico pe,ExercicioPlanoEstrategico e";
        hql += " where p.planejamentoEstrategico=pe and e.planejamentoEstrategico=pe";
        hql += " and e.exercicio=:paramExerc and lower(p.descricao) like:paramEx";

        Query q = em.createQuery(hql);
        q.setParameter("paramExerc", exerc);
        q.setParameter("paramEx", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<PPA> listaTodosPpaExericicioCombo(Exercicio exerc) {
        List<PPA> listaT = new ArrayList<PPA>();
        String hql = " select p from PPA p,PlanejamentoEstrategico pe,ExercicioPlanoEstrategico e";
        hql += " where p.planejamentoEstrategico=pe and e.planejamentoEstrategico=pe";
        hql += " and e.exercicio=:paramExerc";
        Query q;
        if (exerc != null) {
            q = em.createQuery(hql);
            q.setParameter("paramExerc", exerc);
            listaT = q.getResultList();
        }
        return listaT;
    }

    public List<PPA> listaFiltrandoPPA(String parte) {
        String hql = "from PPA p where lower(p.descricao) like :parte order by p.versao desc";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<PPA> buscarPpas(String parte){
        String sql = " select p.* from PPA p where lower(p.DESCRICAO) like :parte order by p.DATAREGISTRO ";
        Query q = em.createNativeQuery(sql, PPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<PPA> listaAprovados(String s) {
        String hql = "from PPA p where p.somenteLeitura = 1 and lower(p.descricao) like :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", "%" + s + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<PPA> listaNaoAprovados(String s) {
        String hql = "from PPA p where p.somenteLeitura = 0 and lower(p.descricao) like :param";
        Query q = em.createQuery(hql);
        q.setParameter("param", "%" + s + "%");
        q.setMaxResults(30);
        return q.getResultList();
    }

    public List<PPA> listaTotosPPANaoAprovados() {
        String hql = "from PPA p where p.somenteLeitura = 0";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public List<Exercicio> listaPpaEx(PPA ppa) {
        List<Exercicio> listaExercicio = new ArrayList<Exercicio>();
        List<ExercicioPlanoEstrategico> lista = new ArrayList<ExercicioPlanoEstrategico>();
        if (ppa != null) {
            if (ppa.getPlanejamentoEstrategico() != null) {
                PlanejamentoEstrategico p = em.find(PlanejamentoEstrategico.class, ppa.getPlanejamentoEstrategico().getId());
                lista = p.getExerciciosPlanoEstrategico();
                for (ExercicioPlanoEstrategico e : lista) {
                    listaExercicio.add(e.getExercicio());
                }
            }
        }
        return listaExercicio;
    }

    public List<Exercicio> exercicioPorPPA(PPA ppa) {
        String sql = " SELECT  EX.* FROM PPA A "
            + " INNER JOIN PLANEJAMENTOESTRATEGICO ESTRATEG ON A.PLANEJAMENTOESTRATEGICO_ID = ESTRATEG.ID "
            + " INNER JOIN EXERCPLANOESTRATEGICO EXERC ON EXERC.PLANEJAMENTOESTRATEGICO_ID = ESTRATEG.ID "
            + " INNER JOIN EXERCICIO EX ON EXERC.EXERCICIO_ID = EX.ID "
            + " WHERE A.ID = :ppa"
            + " ORDER BY ex.ano ASC ";
        Query q = em.createNativeQuery(sql, Exercicio.class);
        q.setParameter("ppa", ppa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public PPA ultimoPpaDoExercicio(Exercicio exercicio) throws ExcecaoNegocioGenerica {
        try {
            String sql = " SELECT * FROM( ";
            sql += " SELECT pp.* FROM PPA pp ";
            sql += " INNER JOIN planejamentoestrategico pljestrat ON pljestrat.id = pp.planejamentoestrategico_id ";
            sql += " INNER JOIN exercplanoestrategico exercplj ON exercplj.planejamentoestrategico_id = pljestrat.id ";
            sql += " AND exercplj.exercicio_id = :paramExercicio ";
//            sql += " WHERE pp.aprovacao IS NULL ";
            sql += " ORDER BY pp.versao DESC)x ";
            Query q = em.createNativeQuery(sql, PPA.class);
            q.setParameter("paramExercicio", exercicio.getId());
            q.setMaxResults(1);
            return (PPA) q.getSingleResult();

        } catch (NoResultException nre) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado nenhum PPA vigente para o Exercicio " + exercicio.getAno());
        } catch (NonUniqueResultException nue) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de um PPA vigente para o Exercicio " + exercicio.getAno() + ". Entre em contato com o suporte.");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro a recuperar versão do PPA:" + ex.getMessage());
        }

    }

    @Override
    public PPA recuperar(Object id) {
        PPA p = em.find(PPA.class, id);
        p.getPlanejamentoEstrategico().getExerciciosPlanoEstrategico().size();
        p.getReceitasExerciciosPPAs().size();
        p.getReceitaPPAs().size();
        for (ReceitaPPA receitaPPA : p.getReceitaPPAs()) {
            receitaPPA.getReceitaPPAContas().size();
        }

        return p;
    }


    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void aprovaPpa(PPA ppa) {
        try {
            ppa.setAprovacao(SistemaFacade.getDataCorrente());
            ppa.setSomenteLeitura(true);
            em.merge(ppa);

            for (ProgramaPPA programaPpa : programaPPAFacade.recuperaProgramasPPA(ppa)) {
                programaPpa.setSomenteLeitura(true);
                for (PublicoAlvoProgramaPPA pAlvo : publicoAlvoProgramaPPAFacade.recuperaPublicoAlvo(programaPpa)) {
                    pAlvo.setSomenteLeitura(true);
                }
                for (IndicadorProgramaPPA ind : indicadorProgramaPPAFacade.recuperaIndicadorProgramaPPa(programaPpa)) {
                    ind.setSomenteLeitura(true);
                }
                em.merge(programaPpa);
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public String geraNumeroVersao(PPA ppa) {
        String versao = ppa.getVersao();
//        Long versaoPPA = Long.parseLong(ppa.getVersao());
        Integer lista = listaPorOrigem(ppa);
        String s;
        if (lista == 0) {
            s = (String.valueOf(versao + "." + 1));
        } else {
            lista = lista + 1;
            s = (String.valueOf(versao + "." + lista));
        }
        return s;
    }

    public Integer listaPorOrigem(PPA p) {
        String sql = "SELECT count(p.id) FROM ppa p WHERE p.origem_id =:param";
        Query q = em.createNativeQuery(sql);
        q.setParameter("param", p.getId());
        return Integer.parseInt(q.getSingleResult() + "");
    }

    public List<PPA> listaPorPlanejamentoEstrategico(PlanejamentoEstrategico pe) {
        String hql = "from PPA p where p.planejamentoEstrategico =:plan";
        Query q = em.createQuery(hql);
        q.setParameter("plan", pe);
        return q.getResultList();
    }

    public List<PPA> verificaVersoes(PPA p, PlanejamentoEstrategico pe) {
        String hql = "from PPA p where p.origem =:ppa and p.planejamentoEstrategico =:plan";
        Query q = em.createQuery(hql);
        q.setParameter("ppa", p);
        q.setParameter("plan", pe);
        return q.getResultList();
    }

    public void removerPPA(PPA entity) throws RuntimeException {
//        try {
        Object chave = Persistencia.getId(entity);
        Object obj = recuperar(chave);
        if (obj != null) {
            em.remove(obj);
        }
//        } catch (Exception ex) {
//            Util.getRootCauseEJBException(ex);
////            //System.out.println("Ex : " + ex.getClass().getSimpleName());
////            if (ex instanceof EJBTransactionRolledbackException) {
////                throw new ExcecaoNegocioGenerica("Não foi possível excluir o registro, pois possui relacionamento com outras tabelas. " + ex.getMessage());
////            } else {
////                throw new ExcecaoNegocioGenerica(" AAAAAA " + ex.getMessage() + "  -  " + ex.getClass());
////            }
//        }
    }

    private PPA duplicaPPA(PPA pPA) {
        pPA.setSomenteLeitura(true);
        PPA ppaNovo = new PPA();
        ppaNovo.setId(null);
        ppaNovo.setPlanejamentoEstrategico(pPA.getPlanejamentoEstrategico());
        ppaNovo.setDescricao(pPA.getDescricao());
        ppaNovo.setDataRegistro(SistemaFacade.getDataCorrente());
        ppaNovo.setAprovacao(null);
        ppaNovo.setVersao(geraNumeroVersao(pPA));
        ppaNovo.setSomenteLeitura(false);
        ppaNovo.setAtoLegal(null);
        ppaNovo.setOrigem(pPA);
        List<ReceitaExercicioPPA> receitasExerciciosPPAs = receitaExercicioPPAFacade.recuperaReceitasPorPPa(pPA);
        List<ReceitaExercicioPPA> receitasNovas = new ArrayList<ReceitaExercicioPPA>();
        for (ReceitaExercicioPPA receita : receitasExerciciosPPAs) {
            receita.setId(null);
            receitasNovas.add(receita);
        }
        ppaNovo.setReceitasExerciciosPPAs(receitasNovas);
        ppaNovo.setReceitaPPAs(duplicaReceitasPPA(pPA, ppaNovo));
        ppaNovo.setContabilizado(Boolean.FALSE);
        return salvarRetornando(ppaNovo);
    }

    private List<ReceitaPPA> duplicaReceitasPPA(PPA pPA, PPA ppaNovo) {
        List<ReceitaPPA> retorno = new ArrayList<ReceitaPPA>();
        for (ReceitaPPA r : pPA.getReceitaPPAs()) {
            ReceitaPPA receitaPPA = new ReceitaPPA();
            receitaPPA.setId(null);
            receitaPPA.setPpa(ppaNovo);
            receitaPPA.setReceitaPPAContas(duplicaReceitasPPAConta(r, receitaPPA));
            retorno.add(receitaPPA);
        }
        return retorno;
    }

    private List<ReceitaPPAContas> duplicaReceitasPPAConta(ReceitaPPA r, ReceitaPPA receitaPPA) {
        List<ReceitaPPAContas> retorno = new ArrayList<ReceitaPPAContas>();
        for (ReceitaPPAContas conta : r.getReceitaPPAContas()) {
            ReceitaPPAContas receitaPPAContas = new ReceitaPPAContas();
            receitaPPAContas.setContaReceita(conta.getContaReceita());
            receitaPPAContas.setExercicio(conta.getExercicio());
            receitaPPAContas.setReceitaPPA(receitaPPA);
            receitaPPAContas.setValor(conta.getValor());
            retorno.add(receitaPPAContas);
        }
        return retorno;
    }

    private List<PublicoAlvoProgramaPPA> duplicaPublicoAlvo(ProgramaPPA programaAntigo, ProgramaPPA programaNovo) {
        List<PublicoAlvoProgramaPPA> listaPublicoAlvo = new ArrayList<PublicoAlvoProgramaPPA>();
        for (PublicoAlvoProgramaPPA paAntigo : getPublicoAlvoProgramaPPAFacade().recuperaPublicoAlvo(programaAntigo)) {
            PublicoAlvoProgramaPPA paNovo = new PublicoAlvoProgramaPPA();
            paNovo.setId(null);
            paNovo.setPublicoAlvo(paAntigo.getPublicoAlvo());
            paNovo.setProgramaPPA(programaNovo);
            paNovo.setDataRegistro(SistemaFacade.getDataCorrente());
            paNovo.setOrigem(paAntigo);
            paNovo.setSomenteLeitura(false);
            listaPublicoAlvo.add(paNovo);
        }
        return listaPublicoAlvo;

    }

    private List<MedicaoSubAcaoPPA> duplicaMedicoesSubAcaoPPA(SubAcaoPPA subAntigo, SubAcaoPPA subNovo) {
        List<MedicaoSubAcaoPPA> listaMedicoes = new ArrayList<MedicaoSubAcaoPPA>();
        for (MedicaoSubAcaoPPA medAntigo : getMedicaoSubAcaoPPAFacade().recuperaMedicoes(subAntigo)) {
            medAntigo.setSomenteLeitura(true);
            MedicaoSubAcaoPPA medNovo = new MedicaoSubAcaoPPA();
            medNovo.setId(null);
            medNovo.setDataMedicao(medAntigo.getDataMedicao());
            medNovo.setValorFisico(medAntigo.getValorFisico());
            medNovo.setValorFinanceiro(medAntigo.getValorFinanceiro());
            medNovo.setSubAcaoPPA(subNovo);
            medNovo.setDataRegistro(SistemaFacade.getDataCorrente());
            medNovo.setOrigem(medAntigo);
            medNovo.setSomenteLeitura(false);
            listaMedicoes.add(medNovo);
        }
        return listaMedicoes;
    }

    private List duplicaIndicadores(ProgramaPPA programaAntigo, ProgramaPPA programaNovo) {
        List<IndicadorProgramaPPA> listaIndicadores = new ArrayList<IndicadorProgramaPPA>();
        for (IndicadorProgramaPPA indAntigo : getIndicadorProgramaPPAFacade().recuperaIndicadorProgramaPPa(programaAntigo)) {
            indAntigo.setSomenteLeitura(true);
            IndicadorProgramaPPA indNovo = new IndicadorProgramaPPA();
            indNovo.setId(null);
            indNovo.setPrograma(programaNovo);
            indNovo.setIndicador(indAntigo.getIndicador());
            indNovo.setValorReferencia(indAntigo.getValorReferencia());
            indNovo.setPeriodicidade(indAntigo.getPeriodicidade());
            indNovo.setValorDesejado(indAntigo.getValorDesejado());
            indNovo.setDataRegistro(SistemaFacade.getDataCorrente());
            indNovo.setOrigem(indAntigo);
            indNovo.setSomenteLeitura(false);
            listaIndicadores.add(indNovo);
        }
        return listaIndicadores;
    }

    private List<ParticipanteAcaoPrincipal> duplicandoParticipanteAcaoPrincipal(AcaoPrincipal apAntigo, AcaoPrincipal apNovo) {
        List<ParticipanteAcaoPrincipal> listaParticipantesAP = new ArrayList<ParticipanteAcaoPrincipal>();
        for (ParticipanteAcaoPrincipal participanteAntigo : getParticipanteAcaoPrincipalFacade().recuperarParticipanteAcaoPrincipal(apAntigo)) {
            participanteAntigo.setSomenteLeitura(true);
            ParticipanteAcaoPrincipal participanteNovo = new ParticipanteAcaoPrincipal();
            participanteNovo.setId(null);
            participanteNovo.setAcaoPrincipal(apNovo);
            participanteNovo.setUnidadeOrganizacional(participanteAntigo.getUnidadeOrganizacional());
            participanteNovo.setSomenteLeitura(false);
            participanteNovo.setOrigem(participanteAntigo);
            listaParticipantesAP.add(participanteNovo);
        }
        return listaParticipantesAP;
    }

    private List<MedicaoProvisaoPPA> duplicarMedicoesProvisoesPPA(ProvisaoPPA provisaoAntigo, ProvisaoPPA provisaoNovo) {
        List<MedicaoProvisaoPPA> listaMedicao = new ArrayList<>();
        for (MedicaoProvisaoPPA mpAntigo : getMedicaoProvisaoPPAFacade().recuperarMedicoes(provisaoAntigo)) {
            MedicaoProvisaoPPA mpNovo = new MedicaoProvisaoPPA();
            mpNovo.setId(null);
            mpNovo.setDataRegistro(SistemaFacade.getDataCorrente());
            mpNovo.setProvisaoPPA(provisaoNovo);
            mpNovo.setAcumulada(mpAntigo.getAcumulada());
            mpNovo.setHistorico(mpAntigo.getHistorico());
            listaMedicao.add(mpNovo);
        }
        return listaMedicao;
    }

    private List<ProvisaoPPA> duplicaProvisaoPPA(ProdutoPPA produtoAntigo, ProdutoPPA produtoNovo, Exercicio exercicioNovo) {
        List<ProvisaoPPA> listaProvisoes = new ArrayList<ProvisaoPPA>();
        for (ProvisaoPPA provAntigo : getProvisaoPPAFacade().recuperaProvisoesPPA(produtoAntigo)) {
            provAntigo.setSomenteLeitura(true);
            ProvisaoPPA provNovo = new ProvisaoPPA();
            provNovo.setId(null);
            provNovo.setExercicio(provAntigo.getExercicio());
            provNovo.setProdutoPPA(produtoNovo);
            provNovo.setMetaFisica(provAntigo.getMetaFisica());
            provNovo.setMetaFinanceiraCorrente(provAntigo.getMetaFinanceiraCorrente());
            provNovo.setMetaFinanceiraCapital(provAntigo.getMetaFinanceiraCapital());
            provNovo.setDataRegistro(SistemaFacade.getDataCorrente());
            provNovo.setOrigem(provAntigo);
            provNovo.setSomenteLeitura(false);
            provNovo.setMedicaoProvisaoPPAS(duplicarMedicoesProvisoesPPA(provAntigo, provNovo));
            listaProvisoes.add(provNovo);
        }
        return listaProvisoes;
    }

    private List<ProdutoPPA> duplicaProdutoPPA(AcaoPrincipal apAntigo, AcaoPrincipal apNovo, Exercicio exercicioNovo) {
        List<ProdutoPPA> listaProdutoPPA = new ArrayList<ProdutoPPA>();
        for (ProdutoPPA produtoAntigo : getProdutoPPAFacade().recuperarProdutosPPA(apAntigo)) {
            produtoAntigo.setSomenteLeitura(true);
            ProdutoPPA produtoNovo = new ProdutoPPA();
            produtoNovo.setId(null);
            produtoNovo.setAcaoPrincipal(apNovo);
            produtoNovo.setDescricao(produtoAntigo.getDescricao());
            produtoNovo.setCodigo(produtoAntigo.getCodigo());
            produtoNovo.setTotalFisico(produtoAntigo.getTotalFisico());
            produtoNovo.setTotalFinanceiro(produtoAntigo.getTotalFinanceiro());
            produtoNovo.setDataRegistro(SistemaFacade.getDataCorrente());
            produtoNovo.setOrigem(produtoAntigo);
            produtoNovo.setSomenteLeitura(false);
            produtoNovo.setExercicio(produtoAntigo.getExercicio());
            produtoNovo.setProvisoes(duplicaProvisaoPPA(produtoAntigo, produtoNovo, exercicioNovo));
            listaProdutoPPA.add(produtoNovo);
        }
        return listaProdutoPPA;

    }

    private List<ParticipanteAcaoPPA> duplicaParticipanteAcaoPPA(AcaoPPA acaoAntiga, AcaoPPA acaoNovo) {
        List<ParticipanteAcaoPPA> listaParticipantes = new ArrayList<ParticipanteAcaoPPA>();
        for (ParticipanteAcaoPPA partAntigo : getParticipanteAcaoPPAFacade().recuperaParticipanteAcaoPPa(acaoAntiga)) {
            partAntigo.setSomenteLeitura(true);
            ParticipanteAcaoPPA partNovo = new ParticipanteAcaoPPA();
            partNovo.setId(null);
            partNovo.setAcaoPPA(acaoNovo);
            partNovo.setUnidadeOrganizacional(partAntigo.getUnidadeOrganizacional());
            partNovo.setOrigem(partAntigo);
            partNovo.setSomenteLeitura(false);
            listaParticipantes.add(partNovo);
        }
        return listaParticipantes;
    }

    private List<SubAcaoPPA> duplicaSubAcaoPPA(List<SubAcaoPPA> listaSubAcoes2, AcaoPPA acaoAntiga, AcaoPPA acaoNovo, Exercicio exercicioNovo) {
        List<SubAcaoPPA> listaSubAcaoPPa = new ArrayList<SubAcaoPPA>();
        for (SubAcaoPPA subAntigo : getSubProjetoAtividadeFacade().recuperarSubAcaoPPA(acaoAntiga)) {
            subAntigo.setSomenteLeitura(true);
            SubAcaoPPA subNovo = new SubAcaoPPA();
            subNovo.setId(null);
            subNovo.setCodigo(subAntigo.getCodigo());
            subNovo.setDescricao(subAntigo.getDescricao());
            subNovo.setAcaoPPA(acaoNovo);
            subNovo.setTotalFisico(subAntigo.getTotalFisico());
            subNovo.setTotalFinanceiro(subAntigo.getTotalFinanceiro());
            subNovo.setProvisaoPPA(subAntigo.getProvisaoPPA());
            subNovo.setDataRegistro(SistemaFacade.getDataCorrente());
            subNovo.setOrigem(subAntigo);
            subNovo.setSomenteLeitura(false);
            subNovo.setExercicio(exercicioNovo);
            subNovo.setMedicoesSubAcaoPPA(duplicaMedicoesSubAcaoPPA(subAntigo, subNovo));
            listaSubAcoes2.add(subNovo);
            listaSubAcaoPPa.add(subNovo);
        }
        return listaSubAcaoPPa;

    }

    private List<AcaoPPA> duplicaAcaoPPA(List<SubAcaoPPA> listaSubAcoes2, ProgramaPPA programaAntigo, ProgramaPPA programaNovo, AcaoPrincipal apAntigo, AcaoPrincipal apNovo, Exercicio exercicioNovo) {
        List<AcaoPPA> listaAcaoPPA = new ArrayList<AcaoPPA>();
        for (AcaoPPA acaoAntiga : getProjetoAtividadeFacade().recuperaAcoesPpaPorAcaoPrincipal(apAntigo)) {
            acaoAntiga.setSomenteLeitura(true);
            AcaoPPA acaoNovo = new AcaoPPA();
            acaoNovo.setId(null);
            acaoNovo.setCodigo(acaoAntiga.getCodigo());
            acaoNovo.setPrograma(programaNovo);
            acaoNovo.setTipoAcaoPPA(acaoAntiga.getTipoAcaoPPA());
            acaoNovo.setDescricao(acaoAntiga.getDescricao());
            acaoNovo.setPeriodicidadeMedicao(acaoAntiga.getPeriodicidadeMedicao());
            acaoNovo.setDescricaoProduto(acaoAntiga.getDescricaoProduto());
            acaoNovo.setUnidadeMedidaProduto(acaoAntiga.getUnidadeMedidaProduto());
            acaoNovo.setFuncao(acaoAntiga.getFuncao());
            acaoNovo.setSubFuncao(acaoAntiga.getSubFuncao());
            acaoNovo.setResponsavel(acaoAntiga.getResponsavel());
            acaoNovo.setTotalFisico(acaoAntiga.getTotalFisico());
            acaoNovo.setTotalFinanceiro(acaoAntiga.getTotalFinanceiro());
            acaoNovo.setAndamento(acaoAntiga.getAndamento());
            acaoNovo.setDataRegistro(SistemaFacade.getDataCorrente());
            acaoNovo.setOrigem(acaoAntiga);
            acaoNovo.setSomenteLeitura(false);
            acaoNovo.setExercicio(exercicioNovo);
            acaoNovo.setAcaoPrincipal(apNovo);
            acaoAntiga = getProjetoAtividadeFacade().recuperar(acaoAntiga.getId());
            acaoNovo.setSubAcaoPPAs(duplicaSubAcaoPPA(listaSubAcoes2, acaoAntiga, acaoNovo, exercicioNovo));
            acaoNovo.setParticipanteAcaoPPA(duplicaParticipanteAcaoPPA(acaoAntiga, acaoNovo));
            listaAcaoPPA.add(acaoNovo);
        }
        return listaAcaoPPA;

    }

    private List<AcaoPrincipal> duplicaAcaoPrincipal(List<SubAcaoPPA> listaSubAcoes2, ProgramaPPA programaAntigo, ProgramaPPA programaNovo, Exercicio exercicioNovo) {
        List<AcaoPrincipal> listaAcaoPrincipal = new ArrayList<AcaoPrincipal>();
        for (AcaoPrincipal apAntigo : getAcaoPrincipalFacade().recuperaAcoesPpa(programaAntigo)) {
            apAntigo.setSomenteLeitura(true);
            AcaoPrincipal apNovo = new AcaoPrincipal();
            apNovo.setId(null);
            apNovo.setCodigo(apAntigo.getCodigo());
            apNovo.setPrograma(programaNovo);
            apNovo.setTipoAcaoPPA(apAntigo.getTipoAcaoPPA());
            apNovo.setDescricao(apAntigo.getDescricao());
            apNovo.setPeriodicidadeMedicao(apAntigo.getPeriodicidadeMedicao());
            apNovo.setDescricaoProduto(apAntigo.getDescricaoProduto());
            apNovo.setUnidadeMedidaProduto(apAntigo.getUnidadeMedidaProduto());
            apNovo.setFuncao(apAntigo.getFuncao());
            apNovo.setSubFuncao(apAntigo.getSubFuncao());
            apNovo.setResponsavel(apAntigo.getResponsavel());
            apNovo.setTotalFisico(apAntigo.getTotalFisico());
            apNovo.setTotalFinanceiro(apAntigo.getTotalFinanceiro());
            apNovo.setAndamento(apAntigo.getAndamento());
            apNovo.setDataRegistro(apAntigo.getDataRegistro());
            apNovo.setOrigem(apAntigo);
            apNovo.setSomenteLeitura(false);
            apNovo.setProjetosAtividades(new ArrayList<AcaoPPA>());
            apNovo.setExercicio(exercicioNovo);
            apNovo.setProdutoPPAs(duplicaProdutoPPA(apAntigo, apNovo, exercicioNovo));
            apNovo.setParticipanteAcaoPrincipals(duplicandoParticipanteAcaoPrincipal(apAntigo, apNovo));
            listaAcaoPrincipal.add(apNovo);
        }
        return listaAcaoPrincipal;
    }

    private void duplicaProgramaPPA(List<SubAcaoPPA> listaSubAcoes2, PPA pPA, PPA ppaNovo, Exercicio exercicioNovo) {
        for (ProgramaPPA programaAntigo : getProgramaPPAFacade().recuperaProgramasPPA(pPA)) {
            programaAntigo.setSomenteLeitura(Boolean.TRUE);
            ProgramaPPA programaNovo = new ProgramaPPA();
            programaNovo.setId(null);
            programaNovo.setPpa(ppaNovo);
            programaNovo.setDenominacao(programaAntigo.getDenominacao());
            programaNovo.setObjetivo(programaAntigo.getObjetivo());
            programaNovo.setCodigo(programaAntigo.getCodigo());
            programaNovo.setResponsavel(programaAntigo.getResponsavel());
            programaNovo.setItemPlanejamentoEstrategico(programaAntigo.getItemPlanejamentoEstrategico());
            programaNovo.setMacroObjetivoEstrategico(programaAntigo.getMacroObjetivoEstrategico());
            programaNovo.setTipoPrograma(programaAntigo.getTipoPrograma());
            programaNovo.setHorizonteTemporal(programaAntigo.getHorizonteTemporal());
            programaNovo.setInicio(programaAntigo.getInicio());
            programaNovo.setFim(programaAntigo.getFim());
            programaNovo.setMultisetorial(programaAntigo.getMultisetorial());
            programaNovo.setOrigem(programaAntigo);
            programaNovo.setSomenteLeitura(false);
            programaNovo.setExercicio(exercicioNovo);
            programaNovo.setBaseGeografica(programaAntigo.getBaseGeografica());
            programaNovo.setPublicoAlvo(duplicaPublicoAlvo(programaAntigo, programaNovo));
            programaNovo.setIndicadores(duplicaIndicadores(programaAntigo, programaNovo));
            programaNovo.setAcoes(duplicaAcaoPrincipal(listaSubAcoes2, programaAntigo, programaNovo, exercicioNovo));
            getProgramaPPAFacade().salvarNovoPrograma(programaNovo);
        }
    }

    public void geraNovaVersao(PPA pPA, Exercicio exercicioNovo) {
        List<SubAcaoPPA> listaSubAcoes2 = new ArrayList<SubAcaoPPA>();
        PPA ppaNovo = duplicaPPA(pPA);
        duplicaProgramaPPA(listaSubAcoes2, pPA, ppaNovo, exercicioNovo);
    }

    public List<PPA> buscarPpasPorLoasEfetivadas(Exercicio exercicio) {
        Query q = em.createNativeQuery(" select distinct ppa.* from loa " +
            " inner join ldo on loa.ldo_id = ldo.id " +
            " inner join ppa on ldo.ppa_id = ppa.id " +
            " where loa.efetivada = :efetivada " +
            " and ldo.exercicio_id = :exercicio ", PPA.class);
        q.setParameter("efetivada", Boolean.TRUE);
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    public PlanejamentoEstrategicoFacade getPlanejamentoEstrategicoFacade() {
        return planejamentoEstrategicoFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public LDOFacade getlDOFacade() {
        return lDOFacade;
    }

    public MedicaoProvisaoPPAFacade getMedicaoProvisaoPPAFacade() {
        return medicaoProvisaoPPAFacade;
    }

    public ProgramaPPAFacade getProgramaPPAFacade() {
        return programaPPAFacade;
    }

    public ProjetoAtividadeFacade getProjetoAtividadeFacade() {
        return projetoAtividadeFacade;
    }

    public IndicadorProgramaPPAFacade getIndicadorProgramaPPAFacade() {
        return indicadorProgramaPPAFacade;
    }

    public ParticipanteAcaoPPAFacade getParticipanteAcaoPPAFacade() {
        return participanteAcaoPPAFacade;
    }

    public MedicaoSubAcaoPPAFacade getMedicaoSubAcaoPPAFacade() {
        return medicaoSubAcaoPPAFacade;
    }

    public ProvisaoPPAFacade getProvisaoPPAFacade() {
        return provisaoPPAFacade;
    }

    public ProvisaoPPADespesaFacade getProvisaoPPADespesaFacade() {
        return provisaoPPADespesaFacade;
    }

    public ProvisaoPPAFonteFacade getProvisaoPPAFonteFacade() {
        return provisaoPPAFonteFacade;
    }

    public PublicoAlvoProgramaPPAFacade getPublicoAlvoProgramaPPAFacade() {
        return publicoAlvoProgramaPPAFacade;
    }

    public ReceitaExercicioPPAFacade getReceitaExercicioPPAFacade() {
        return receitaExercicioPPAFacade;
    }

    public SubProjetoAtividadeFacade getSubProjetoAtividadeFacade() {
        return subProjetoAtividadeFacade;
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public CLPRealizadoFacade getcLPRealizadoFacade() {
        return cLPRealizadoFacade;
    }

    public AcaoPrincipalFacade getAcaoPrincipalFacade() {
        return acaoPrincipalFacade;
    }

    public ProdutoPPAFacade getProdutoPPAFacade() {
        return produtoPPAFacade;
    }

    public ParticipanteAcaoPrincipalFacade getParticipanteAcaoPrincipalFacade() {
        return participanteAcaoPrincipalFacade;
    }

    public EntityManager getEm() {
        return em;
    }

    public PPA salvarRetornando(PPA entity) {
        return em.merge(entity);
    }
}
