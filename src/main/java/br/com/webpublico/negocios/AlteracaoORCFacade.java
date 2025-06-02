/*
 * Codigo gerado automaticamente em Tue Nov 08 15:05:04 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.AlteracaoOrcPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteAlteracaoOrc;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidaLDOExeption;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class AlteracaoORCFacade extends SuperFacadeContabil<AlteracaoORC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private ReservaFonteDespesaOrcFacade reservaFonteDespesaOrcFacade;
    @EJB
    private LiberacaoFonteDespesaORCFacade liberacaoFonteDespesaORCFacade;
    @EJB
    private AnulacaoOrcFacade anulacaoOrcFacade;
    @EJB
    private SuplementacaoOrcFacade suplementacaoOrcFacade;
    @EJB
    private GrupoCotaORCFacade grupoCotaORCFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private MinutaAlteracaoOrcamentariaFacade minutaAlteracaoOrcamentariaFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private CotaOrcamentariaFacade cotaOrcamentariaFacade;
    @EJB
    private GrupoOrcamentarioFacade grupoOrcamentarioFacade;
    @EJB
    private SaldoReceitaORCFacade saldoReceitaORCFacade;
    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;
    @EJB
    private ConfigAlteracaoOrcFacade configAlteracaoOrcFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private ExtensaoFonteRecursoFacade extensaoFonteRecursoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private MedicaoSubAcaoPPAFacade medicaoSubAcaoPPAFacade;
    @EJB
    private ParticipanteAcaoPPAFacade participanteAcaoPPAFacade;
    @EJB
    private ProvisaoPPADespesaFacade provisaoPPADespesaFacade;

    public AlteracaoORCFacade() {
        super(AlteracaoORC.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AlteracaoORC recuperar(Object id) {
        Long cd;
        AlteracaoORC alter;
        if (id instanceof AlteracaoORC) {
            alter = (AlteracaoORC) id;
            cd = alter.getId();
        } else {
            cd = (Long) id;
        }
        alter = getEntityManager().find(AlteracaoORC.class, cd);
        alter.getAnulacoesORCs().size();
        alter.getReceitasAlteracoesORCs().size();
        alter.getSuplementacoesORCs().size();
        alter.getSolicitacoes().size();
        return alter;
    }

    public AlteracaoORC salvarRetornando(AlteracaoORC entity) {
        em.merge(entity);
        return entity;
    }

    @Override
    public void salvarNovo(AlteracaoORC entity) {
        entity.setCodigo(buscarUltimoCodigoAlteracaoOrcamentaria(entity.getExercicio()));
        em.persist(entity);
    }

    public void salvar(AlteracaoORC entity, List<AnulacaoORC> anulacoesRemovidas) {
        entity = em.merge(entity);
        if (StatusAlteracaoORC.EM_ANALISE.equals(entity.getStatus())) {
            adicionarValorBloqueadoSaldoFonteDespesaOrcParaNovaAnulacao(entity);
            removerValorBloqueadoSaldoFonteDespesaOrcAnulacoesRemovidas(entity, anulacoesRemovidas);
        }
        em.merge(entity);
    }

    public void enviarAlteracaoParaAnalise(AlteracaoORC entity) {
        entity.setStatus(StatusAlteracaoORC.EM_ANALISE);
        entity = em.merge(entity);
        if (entity.getAnulacoesORCs() != null) {
            for (AnulacaoORC a : entity.getAnulacoesORCs()) {
                gerarSaldoFonteDespesaOrcColunaBloqueadoPorTipoLancamento(entity, a, TipoOperacaoORC.NORMAL);
                a.setBloqueado(false);
            }
        }
        salvarNotificacaoAlteracaoOrcEmAnalise(entity);
    }

    public void enviarAlteracaoParaElaboracao(AlteracaoORC entity) {
        if (entity.getAnulacoesORCs() != null) {
            for (AnulacaoORC a : entity.getAnulacoesORCs()) {
                gerarSaldoFonteDespesaOrcColunaBloqueadoPorTipoLancamento(entity, a, TipoOperacaoORC.ESTORNO);
                a.setBloqueado(false);
                saldoFonteDespesaORCFacade.executarDeleteMovimentoDespesaOrcPorIdOrigem(a.getId());
            }
        }
        entity.setStatus(StatusAlteracaoORC.ELABORACAO);
        entity = em.merge(entity);
        salvarNotificacaoAlteracaoOrcEmElaboracao(entity);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteAlteracaoOrc> deferirAlteracaoOrc(AssistenteAlteracaoOrc assistente) throws Exception {

        AlteracaoORC entity;
        assistente.getBarraProgressoItens().inicializa();
        assistente.zerarContadoresProcesso();

        try {
            entity = deferir(assistente);
        } catch (ValidacaoException ve) {
            for (FacesMessage facesMessage : ve.getAllMensagens()) {
                assistente.getMensagens().add(facesMessage.getDetail());
            }
            assistente.getBarraProgressoItens().finaliza();
            throw ve;
        } catch (Exception e) {
            logger.error("Erro no deferimento da alteração orçamentária: ", e);
            assistente.getMensagens().add(e.getMessage());
            assistente.getBarraProgressoItens().finaliza();
            throw e;
        }
        assistente.setAlteracaoORC(entity);
        assistente.getBarraProgressoItens().finaliza();
        return new AsyncResult<>(assistente);
    }

    private AlteracaoORC deferir(AssistenteAlteracaoOrc assistente) throws Exception {

        assistente.getBarraProgressoItens().setMensagens("Preparando alteração orçamentária...");
        AlteracaoORC entity = assistente.getAlteracaoORC();
        salvarArquivo(entity);
        entity = em.merge(entity);

        Integer qtdeRegistros = assistente.getTotalListas();
        assistente.adicionarQuantidadeRegistroParaProcessar(qtdeRegistros);
        assistente.setTotal(qtdeRegistros);

        gerarSaldoOrcamentarioSuplementar(entity, assistente);
        adicionarValorBloqueadoSaldoFonteDespesaOrcParaNovaAnulacao(entity, assistente);
        removerValorBloqueadoSaldoFonteDespesaOrcAnulacoesRemovidas(entity, assistente);
        estornarValorBloqueadoSaldoFonteDespesaOrcParaNovaAnulacao(assistente, entity);
        gerarSaldoFonteDespesaOrcAnulacao(entity, assistente);

        //Comentado por solicitação do gestor do orçamento, mas pode haver solicitação de retorno.
//                movimentarCotaOrcamentaria(entity);

        entity.setEfetivado(true);
        entity.setStatus(StatusAlteracaoORC.DEFERIDO);
        entity = em.merge(entity);

        gerarContabilizacao(entity, assistente);
        salvarNotificacaoAlteracaoOrcDeferida(entity);
        portalTransparenciaNovoFacade.salvarPortal(new AlteracaoOrcPortal(entity));
        return entity;
    }

    private void estornarValorBloqueadoSaldoFonteDespesaOrcParaNovaAnulacao(AssistenteAlteracaoOrc assistente, AlteracaoORC entity) {
        assistente.getBarraProgressoItens().setMensagens("Gerando Saldo Orçamentário Anulação - Estorno...");
        for (AnulacaoORC anulacaoORC : entity.getAnulacoesORCs()) {
            gerarSaldoFonteDespesaOrcColunaBloqueadoPorTipoLancamento(entity, anulacaoORC, TipoOperacaoORC.ESTORNO);
            assistente.contarRegistroProcessado();
            assistente.conta();
        }
    }

    private void salvarArquivo(AlteracaoORC entity) throws Exception {
        if (entity.getArquivo() != null) {
            Arquivo arquivo = arquivoFacade.retornaArquivoSalvo(entity.getArquivo(), entity.getArquivo().getInputStream());
            entity.setArquivo(arquivo);
        }
    }

    public void indeferirAlteracaoOrc(AlteracaoORC alteracaoORC, List<AnulacaoORC> listaAnulacoesRemovidas) {
        try {
            if (!alteracaoORC.getAnulacoesORCs().isEmpty()) {
                for (AnulacaoORC anulacaoORC : alteracaoORC.getAnulacoesORCs()) {
                    removerValorBloqueadoSaldoFonteDespesaOrcAnulacoesRemovidas(alteracaoORC, anulacaoORC);
                }
            }
            removerValorBloqueadoSaldoFonteDespesaOrcAnulacoesRemovidas(alteracaoORC, listaAnulacoesRemovidas);
            alteracaoORC.setStatus(StatusAlteracaoORC.INDEFERIDO);
            alteracaoORC = em.merge(alteracaoORC);
            salvarNotificacaoAlteracaoOrcIndeferida(alteracaoORC);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void adicionarValorBloqueadoSaldoFonteDespesaOrcParaNovaAnulacao(AlteracaoORC entity, AssistenteAlteracaoOrc assistente) {
        assistente.getBarraProgressoItens().setMensagens("Gerando Saldo Orçamentário Anulação...");
        for (AnulacaoORC anulacaoORC : entity.getAnulacoesORCs()) {
            adicionarValorBloqueadoSaldoFonteDespesaOrcParaNovaAnulacao(entity, anulacaoORC);
            assistente.contarRegistroProcessado();
            assistente.conta();
        }
    }

    private void adicionarValorBloqueadoSaldoFonteDespesaOrcParaNovaAnulacao(AlteracaoORC entity) {
        for (AnulacaoORC anulacaoORC : entity.getAnulacoesORCs()) {
            adicionarValorBloqueadoSaldoFonteDespesaOrcParaNovaAnulacao(entity, anulacaoORC);
        }
    }

    private void adicionarValorBloqueadoSaldoFonteDespesaOrcParaNovaAnulacao(AlteracaoORC entity, AnulacaoORC anulacaoORC) {
        if (anulacaoORC.getBloqueado()) {
            gerarSaldoFonteDespesaOrcColunaBloqueadoPorTipoLancamento(entity, anulacaoORC, TipoOperacaoORC.NORMAL);
            anulacaoORC.setBloqueado(false);
        }
    }

    private void removerValorBloqueadoSaldoFonteDespesaOrcAnulacoesRemovidas(AlteracaoORC entity, AssistenteAlteracaoOrc assistente) {
        if (!assistente.getAnulacoesRemovidas().isEmpty()) {
            assistente.getBarraProgressoItens().setMensagens("Gerando Saldo Orçamentário Anulação Removidas...");
            for (AnulacaoORC anulacaoORC : assistente.getAnulacoesRemovidas()) {
                removerValorBloqueadoSaldoFonteDespesaOrcAnulacoesRemovidas(entity, anulacaoORC);
                assistente.contarRegistroProcessado();
                assistente.conta();
            }
        }
    }

    private void removerValorBloqueadoSaldoFonteDespesaOrcAnulacoesRemovidas(AlteracaoORC entity, List<AnulacaoORC> anulacoesRemovidas) {
        if (!anulacoesRemovidas.isEmpty()) {
            for (AnulacaoORC anulacaoORC : anulacoesRemovidas) {
                removerValorBloqueadoSaldoFonteDespesaOrcAnulacoesRemovidas(entity, anulacaoORC);
            }
        }
    }

    private void removerValorBloqueadoSaldoFonteDespesaOrcAnulacoesRemovidas(AlteracaoORC entity, AnulacaoORC anulacaoORC) {
        anulacaoOrcFacade.removerReservaAnulacao(anulacaoORC);
        gerarSaldoFonteDespesaOrcColunaBloqueadoPorTipoLancamento(entity, anulacaoORC, TipoOperacaoORC.ESTORNO);
    }


    public void aprovarSolicitacao(AlteracaoORC selecionado, SolicitacaoDespesaOrc solicitacaoDespesaOrc, GrupoOrcamentario grupoOrcamentario) throws ValidaLDOExeption {
        try {
            ProvisaoPPADespesa provisaoPPADespesa = getProvisaoPPADespesa(solicitacaoDespesaOrc);
            ProvisaoPPAFonte provisaoPPAFonte = criarAndAdicionarFonteNaProvisaoPpaDespesaSocilicitacao(provisaoPPADespesa, solicitacaoDespesaOrc);
            validarFontesIguais(provisaoPPADespesa, provisaoPPAFonte);
            provisaoPPAFacade.getProvisaoPPADespesaFacade().salvarDespesaOrcamentaria(provisaoPPADespesa, new ArrayList<ProvisaoPPAFonte>(), grupoOrcamentario);
            solicitacaoDespesaOrc.setDeferidaEm(sistemaFacade.getDataOperacao());
            setSomenteLeituraAcaoESubAcaoPPA(solicitacaoDespesaOrc);
            solicitacaoDespesaOrc = salvarRetornandoSolicitacao(solicitacaoDespesaOrc, selecionado);
            salvarNotificacaoNovoElementoDespesa(selecionado, solicitacaoDespesaOrc);
        } catch (ValidacaoException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void setSomenteLeituraAcaoESubAcaoPPA(SolicitacaoDespesaOrc solicitacaoDespesaOrc) {
        solicitacaoDespesaOrc.getAcaoPPA().setSomenteLeitura(true);
        solicitacaoDespesaOrc.getSubAcaoPPA().setSomenteLeitura(true);
        for (MedicaoSubAcaoPPA medicao : medicaoSubAcaoPPAFacade.recuperaMedicoes(solicitacaoDespesaOrc.getSubAcaoPPA())) {
            medicao.setSomenteLeitura(true);
        }
        for (ProvisaoPPADespesa provisaoPPADespesa : provisaoPPADespesaFacade.recuperarProvisaoPPADespesaPorSubAcao(solicitacaoDespesaOrc.getSubAcaoPPA())) {
            provisaoPPADespesa.setSomenteLeitura(true);
            for (ProvisaoPPAFonte provisaoPPAFonte : provisaoPPADespesa.getProvisaoPPAFontes()) {
                provisaoPPAFonte.setSomenteLeitura(true);
            }
            em.merge(provisaoPPADespesa);
        }
        em.merge(solicitacaoDespesaOrc.getSubAcaoPPA());
        for (ParticipanteAcaoPPA participante : participanteAcaoPPAFacade.recuperaParticipanteAcaoPPa(solicitacaoDespesaOrc.getAcaoPPA())) {
            participante.setSomenteLeitura(true);
        }
        em.merge(solicitacaoDespesaOrc.getAcaoPPA());
    }

    private void validarFontesIguais(ProvisaoPPADespesa provisaoPPADespesa, ProvisaoPPAFonte provisaoPPAFonte) {
        ValidacaoException ve = new ValidacaoException();
        for (ProvisaoPPAFonte ppaFonte : provisaoPPADespesa.getProvisaoPPAFontes()) {
            if (ppaFonte.getDestinacaoDeRecursos().getId().equals(provisaoPPAFonte.getDestinacaoDeRecursos().getId()) && !ppaFonte.equals(provisaoPPAFonte)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A Fonte de Recurso " + ppaFonte.getDestinacaoDeRecursos() + " já foi adicionada para está conta no orçamento.");
            }
        }
        ve.lancarException();
    }

    private ProvisaoPPAFonte criarAndAdicionarFonteNaProvisaoPpaDespesaSocilicitacao(ProvisaoPPADespesa provisaoPPADespesa, SolicitacaoDespesaOrc solicitacaoDespesaOrc) {
        ProvisaoPPAFonte provisaoPPAFonte = new ProvisaoPPAFonte();
        provisaoPPAFonte.setValor(BigDecimal.ZERO);
        provisaoPPAFonte.setSomenteLeitura(Boolean.TRUE);
        provisaoPPAFonte.setDestinacaoDeRecursos(solicitacaoDespesaOrc.getDestinacaoDeRecursos());
        provisaoPPAFonte.setEsferaOrcamentaria(solicitacaoDespesaOrc.getEsferaOrcamentaria());
        provisaoPPAFonte.setExtensaoFonteRecurso(solicitacaoDespesaOrc.getExtensaoFonteRecurso());
        provisaoPPAFonte.setProvisaoPPADespesa(provisaoPPADespesa);
        provisaoPPADespesa.setProvisaoPPAFontes(Util.adicionarObjetoEmLista(provisaoPPADespesa.getProvisaoPPAFontes(), provisaoPPAFonte));
        return provisaoPPAFonte;
    }

    private ProvisaoPPADespesa getProvisaoPPADespesa(SolicitacaoDespesaOrc solicitacaoDespesaOrc) {
        ProvisaoPPADespesa prov = recuperarDespesaOrcamento(solicitacaoDespesaOrc);
        if (prov == null) {
            ProvisaoPPADespesa provisaoPPADespesa = new ProvisaoPPADespesa();
            provisaoPPADespesa.setUnidadeOrganizacional(solicitacaoDespesaOrc.getUnidadeOrganizacional());
            provisaoPPADespesa.setContaDeDespesa(solicitacaoDespesaOrc.getConta());
            provisaoPPADespesa.setDataRegistro(sistemaFacade.getDataOperacao());
            provisaoPPADespesa.setSubAcaoPPA(solicitacaoDespesaOrc.getSubAcaoPPA());
            provisaoPPADespesa.setSomenteLeitura(Boolean.TRUE);
            provisaoPPADespesa.setTipoDespesaORC(TipoDespesaORC.SUPLEMENTAR);
            provisaoPPADespesa.setValor(BigDecimal.ZERO);
            provisaoPPADespesa.setProvisaoPPAFontes(new ArrayList<ProvisaoPPAFonte>());
            if (solicitacaoDespesaOrc.getSubAcaoPPA() != null) {
                provisaoPPADespesa.setCodigo(provisaoPPAFacade.getProvisaoPPADespesaFacade().getCodigo(sistemaFacade.getExercicioCorrente(), solicitacaoDespesaOrc.getSubAcaoPPA()));
            }
            return provisaoPPADespesa;
        }
        return prov;
    }

    private ProvisaoPPADespesa recuperarDespesaOrcamento(SolicitacaoDespesaOrc solicitacaoDespesaOrc) {
        List<ProvisaoPPADespesa> provisaoPPADespesas = provisaoPPAFacade.getProvisaoPPADespesaFacade().listaProvisaoDispesaPPARecuperandoFontes(solicitacaoDespesaOrc.getSubAcaoPPA());
        for (ProvisaoPPADespesa ppaDespesa : provisaoPPADespesas) {
            if (solicitacaoDespesaOrc.getConta().getId().equals(ppaDespesa.getContaDeDespesa().getId())) {
                return ppaDespesa;
            }
        }
        return null;
    }

    private void gerarSaldoFonteDespesaOrcColunaBloqueadoPorTipoLancamento(AlteracaoORC alteracaoORC, AnulacaoORC anulacaoORC, TipoOperacaoORC tipoOperacaoORC) {
        SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
            anulacaoORC.getFonteDespesaORC(),
            OperacaoORC.RESERVADO,
            tipoOperacaoORC,
            anulacaoORC.getValor(),
            alteracaoORC.getDataEfetivacao() != null ? alteracaoORC.getDataEfetivacao() : alteracaoORC.getDataAlteracao(),
            anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional(),
            anulacaoORC.getId().toString(),
            anulacaoORC.getClass().getSimpleName(),
            alteracaoORC.getCodigo(),
            anulacaoORC.getHistoricoRazao());
        saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
    }

    private void gerarSaldoOrcamentarioSuplementar(AlteracaoORC entity, AssistenteAlteracaoOrc assistente) {
        assistente.getBarraProgressoItens().setMensagens("Gerando Saldo Orçamentário Suplementação...");
        if (!entity.getSuplementacoesORCs().isEmpty()) {
            for (SuplementacaoORC s : entity.getSuplementacoesORCs()) {
                SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                    s.getFonteDespesaORC(),
                    OperacaoORC.SUPLEMENTACAO,
                    TipoOperacaoORC.NORMAL,
                    s.getValor(),
                    entity.getDataEfetivacao(),
                    s.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                    s.getId().toString(),
                    s.getClass().getSimpleName(),
                    entity.getCodigo(),
                    s.getHistoricoRazao());
                saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
                assistente.contarRegistroProcessado();
                assistente.conta();
            }
        }
    }

    private void gerarSaldoFonteDespesaOrcAnulacao(AlteracaoORC entity, AssistenteAlteracaoOrc assistente) {
        assistente.getBarraProgressoItens().setMensagens("Gerando saldo orçamentário anulação...");
        if (!entity.getAnulacoesORCs().isEmpty()) {
            for (AnulacaoORC a : entity.getAnulacoesORCs()) {
                SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                    a.getFonteDespesaORC(),
                    OperacaoORC.ANULACAO,
                    TipoOperacaoORC.NORMAL,
                    a.getValor(),
                    entity.getDataEfetivacao(),
                    a.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                    a.getId().toString(),
                    a.getClass().getSimpleName(),
                    entity.getCodigo(),
                    a.getHistoricoRazao());
                saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
                assistente.contarRegistroProcessado();
                assistente.conta();
            }
        }
    }

    private void movimentarCotaOrcamentaria(AlteracaoORC alteracaoORC) {
        if (!alteracaoORC.getSuplementacoesORCs().isEmpty()) {
            for (SuplementacaoORC sup : alteracaoORC.getSuplementacoesORCs()) {
                FonteDespesaORC fonteSuplementacao = fonteDespesaORCFacade.recuperar(sup.getFonteDespesaORC().getId());
                fonteSuplementacao = fonteDespesaORCFacade.salvarRetornandoComNovaTransacao(fonteSuplementacao);
                cotaOrcamentariaFacade.creditaValorNaCotaOrcamentaria(fonteSuplementacao, sup.getMes(), sup.getValor(), fonteSuplementacao.getGrupoOrcamentario());
            }
        }
        if (!alteracaoORC.getAnulacoesORCs().isEmpty()) {
            for (AnulacaoORC anulacao : alteracaoORC.getAnulacoesORCs()) {
                FonteDespesaORC fonteAnulacao = fonteDespesaORCFacade.recuperar(anulacao.getFonteDespesaORC().getId());
                fonteAnulacao = fonteDespesaORCFacade.salvarRetornandoComNovaTransacao(fonteAnulacao);
                cotaOrcamentariaFacade.debitaValorNaCotaOrcamentaria(fonteAnulacao, anulacao.getMes(), anulacao.getValor(), fonteAnulacao.getGrupoOrcamentario());
            }
        }
    }

    public void salvarNotificacaoAlteracaoOrcEmAnalise(AlteracaoORC alteracaoORC) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Nº " + alteracaoORC.getCodigo() + ", enviada na data: " + DataUtil.getDataFormatada(alteracaoORC.getDataAlteracao()) + ", solicitada pela Unidade: " + alteracaoORC.getUnidadeOrganizacionalOrc());
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo("Alteração Orçamentária Enviada para Análise");
        notificacao.setTipoNotificacao(TipoNotificacao.LIBERACAO_ALTERACAO_ORCAMENTARIA);
        notificacao.setLink("/alteracao-orcamentaria/ver/" + alteracaoORC.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public void salvarNotificacaoAlteracaoOrcEmElaboracao(AlteracaoORC alteracaoORC) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Nº " + alteracaoORC.getCodigo() + ", retorno para 'Em Elaboração' data: " + DataUtil.getDataFormatada(UtilRH.getDataOperacao()) + ", para a Unidade: " + alteracaoORC.getUnidadeOrganizacionalOrc());
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo("Alteração Orçamentária em Elaboração");
        notificacao.setTipoNotificacao(TipoNotificacao.RETORNO_ALTERACAO_ORCAMENTARIA);
        notificacao.setUnidadeOrganizacional(alteracaoORC.getUnidadeOrganizacionalOrc());
        notificacao.setLink("/alteracao-orcamentaria/ver/" + alteracaoORC.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public void salvarNotificacaoAlteracaoOrcIndeferida(AlteracaoORC alteracaoORC) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Nº " + alteracaoORC.getCodigo() + ", indeferida na data: " + DataUtil.getDataFormatada(UtilRH.getDataOperacao()) + ", para a Unidade: " + alteracaoORC.getUnidadeOrganizacionalOrc());
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo("Alteração Orçamentária Indeferida");
        notificacao.setTipoNotificacao(TipoNotificacao.RETORNO_ALTERACAO_ORCAMENTARIA);
        notificacao.setUnidadeOrganizacional(alteracaoORC.getUnidadeOrganizacionalOrc());
        notificacao.setLink("/alteracao-orcamentaria/ver/" + alteracaoORC.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public void salvarNotificacaoAlteracaoOrcDeferida(AlteracaoORC alteracaoORC) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Nº " + alteracaoORC.getCodigo() + ", deferida na data: " + DataUtil.getDataFormatada(alteracaoORC.getDataEfetivacao()) + " para a Unidade: " + alteracaoORC.getUnidadeOrganizacionalOrc());
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo("Alteração Orçamentária Deferida");
        notificacao.setTipoNotificacao(TipoNotificacao.RETORNO_ALTERACAO_ORCAMENTARIA);
        notificacao.setUnidadeOrganizacional(alteracaoORC.getUnidadeOrganizacionalOrc());
        notificacao.setLink("/alteracao-orcamentaria/ver/" + alteracaoORC.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public void salvarNotificacaoNovoElementoDespesa(AlteracaoORC alteracaoORC, SolicitacaoDespesaOrc solicitacaoDespesaOrc) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Nº " + alteracaoORC.getCodigo() + " aprovada na data: " + DataUtil.getDataFormatada(solicitacaoDespesaOrc.getDeferidaEm())
            + ". Elemento de Despesa: " + solicitacaoDespesaOrc.getConta()
            + " para a Unidade: " + alteracaoORC.getUnidadeOrganizacionalOrc());
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo("Aprovado Elemento de Despesa");
        notificacao.setTipoNotificacao(TipoNotificacao.RETORNO_ALTERACAO_ORCAMENTARIA);
        notificacao.setUnidadeOrganizacional(solicitacaoDespesaOrc.getUnidadeOrganizacional());
        notificacao.setLink("/alteracao-orcamentaria/ver/" + alteracaoORC.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public void gerarHistoricos(AlteracaoORC entity) {
        for (SuplementacaoORC suplementacaoORC : entity.getSuplementacoesORCs()) {
            gerarHistoricoSuplementacao(suplementacaoORC);
        }
        for (AnulacaoORC anulacaoORC : entity.getAnulacoesORCs()) {
            gerarHistoricoAnulacao(anulacaoORC);
        }
        for (ReceitaAlteracaoORC receitaAlteracaoORC : entity.getReceitasAlteracoesORCs()) {
            gerarHistoricoReceitaAlteracaoOrc(receitaAlteracaoORC);
        }
    }

    public void gerarHistoricoSuplementacao(SuplementacaoORC suple) {
        suple.gerarHistoricos();
    }

    public void gerarHistoricoAnulacao(AnulacaoORC anu) {
        anu.gerarHistoricos();
    }

    public void gerarHistoricoReceitaAlteracaoOrc(ReceitaAlteracaoORC rec) {
        rec.gerarHistoricos();
    }


    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        if (entidadeContabil instanceof SuplementacaoORC) {
            contabilizarSuplementacao((SuplementacaoORC) entidadeContabil);
        }
        if (entidadeContabil instanceof AnulacaoORC) {
            contabilizarAnulacao((AnulacaoORC) entidadeContabil);
        }
        if (entidadeContabil instanceof ReceitaAlteracaoORC) {
            contabilizarReceita((ReceitaAlteracaoORC) entidadeContabil);
        }
    }

    private void gerarContabilizacao(AlteracaoORC alteracaoORC, AssistenteAlteracaoOrc assistente) {

        assistente.getBarraProgressoItens().setMensagens("Processando Suplementação...");
        for (SuplementacaoORC s : alteracaoORC.getSuplementacoesORCs()) {
            contabilizarSuplementacao(s);
            assistente.contarRegistroProcessado();
            assistente.conta();
        }

        assistente.getBarraProgressoItens().setMensagens("Processando Anulação...");
        for (AnulacaoORC a : alteracaoORC.getAnulacoesORCs()) {
            contabilizarAnulacao(a);
            assistente.contarRegistroProcessado();
            assistente.conta();
        }

        assistente.getBarraProgressoItens().setMensagens("Processando Receita...");
        for (ReceitaAlteracaoORC r : alteracaoORC.getReceitasAlteracoesORCs()) {
            TipoSaldoReceitaORC tipoSaldoReceitaORC;
            if (r.getTipoAlteracaoORC().equals(TipoAlteracaoORC.PREVISAO)) {
                tipoSaldoReceitaORC = TipoSaldoReceitaORC.ALTERACAOORC_ADICIONAL;
            } else {
                tipoSaldoReceitaORC = TipoSaldoReceitaORC.ALTERACAOORC_ANULACAO;
            }
            saldoReceitaORCFacade.geraSaldo(tipoSaldoReceitaORC, alteracaoORC.getDataEfetivacao(), r.getReceitaLOA().getEntidade(), r.getReceitaLOA().getContaDeReceita(), r.getFonteDeRecurso(), r.getValor());
            contabilizarReceita(r);
            assistente.contarRegistroProcessado();
            assistente.conta();
        }
    }

    private void contabilizarSuplementacao(SuplementacaoORC entity) {
        try {
            ConfigAlteracaoOrc configuracao = configAlteracaoOrcFacade.recuperarConfigEventoCreditoSuplementar(entity.getTipoDespesaORC(), entity.getOrigemSuplemtacao(), TipoLancamento.NORMAL, entity.getAlteracaoORC().getDataEfetivacao());
            if (configuracao != null) {
                entity.setEventoContabil(configuracao.getEventoContabil());
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Crédito Adicional.");
            }
            gerarHistoricoSuplementacao(entity);

            ParametroEvento parametroEvento = new ParametroEvento();

            parametroEvento.setDataEvento(entity.getAlteracaoORC().getDataEfetivacao());
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setUnidadeOrganizacional(entity.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setExercicio(entity.getAlteracaoORC().getExercicio());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity, item));
            objetos.add(new ObjetoParametro(entity.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa(), item));
            objetos.add(new ObjetoParametro(entity.getOrigemSuplemtacao(), item));
            objetos.add(new ObjetoParametro(entity.getFonteDespesaORC(), item));
            objetos.add(new ObjetoParametro(entity.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void contabilizarAnulacao(AnulacaoORC entity) {
        try {
            ConfigAlteracaoOrc configuracao = configAlteracaoOrcFacade.recuperarConfigEventoAnulacaoCredito(entity.getTipoDespesaORC(), TipoLancamento.NORMAL, entity.getAlteracaoORC().getDataEfetivacao());
            if (configuracao != null) {
                entity.setEventoContabil(configuracao.getEventoContabil());
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização Anulação de Crédito.");
            }
            gerarHistoricoAnulacao(entity);

            ParametroEvento parametroEvento = new ParametroEvento();

            parametroEvento.setDataEvento(entity.getAlteracaoORC().getDataEfetivacao());
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setUnidadeOrganizacional(entity.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setExercicio(entity.getAlteracaoORC().getExercicio());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());


            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa(), item));
            objetos.add(new ObjetoParametro(entity.getFonteDespesaORC(), item));
            objetos.add(new ObjetoParametro(entity.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos(), item));
            objetos.add(new ObjetoParametro(entity, item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }

    }

    private void contabilizarReceita(ReceitaAlteracaoORC entity) {
        try {
            ConfigAlteracaoOrc configuracao = configAlteracaoOrcFacade.buscarConfigEventoReceita(TipoLancamento.NORMAL, entity);
            if (configuracao != null) {
                entity.setEventoContabil(configuracao.getEventoContabil());
            } else {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Receita.");
            }
            gerarHistoricoReceitaAlteracaoOrc(entity);

            ParametroEvento parametroEvento = new ParametroEvento();

            parametroEvento.setDataEvento(entity.getAlteracaoORC().getDataEfetivacao());
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setUnidadeOrganizacional(entity.getReceitaLOA().getEntidade());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setExercicio(entity.getReceitaLOA().getLoa().getLdo().getExercicio());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getReceitaLOA().getContaDeReceita(), item));
            objetos.add(new ObjetoParametro(entity, item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EntidadeContabil> recuperarAlteracaoORCDespesa(Exercicio exercicio, EventosReprocessar eventosReprocessar) {
        List<EntidadeContabil> retorno = new ArrayList<EntidadeContabil>();
        retorno.addAll(recuperarSuplementacoes(exercicio, eventosReprocessar));
        retorno.addAll(recuperarAnulacoes(exercicio, eventosReprocessar));
        return retorno;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<EntidadeContabil> recuperarAlteracaoORCReceita(Exercicio exercicio, EventosReprocessar eventosReprocessar) {
        List<EntidadeContabil> retorno = new ArrayList<EntidadeContabil>();
        retorno.addAll(recuperarReceitas(exercicio, eventosReprocessar));
        return retorno;
    }

    public List<AlteracaoORC> listaNoExercicio(Exercicio ex) {
        String sql = "SELECT * FROM alteracaoorc "
            + "WHERE to_char (dataalteracao, 'yyyy') = :ano";
        Query q = em.createNativeQuery(sql, AlteracaoORC.class);
        q.setParameter("ano", ex.getAno().toString());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<AlteracaoORC>();
        } else {
            return q.getResultList();
        }
    }

    public List<AlteracaoORC> listaFiltrandoNoExercicio(String parte, Exercicio ex) {
        String sql = "SELECT * FROM alteracaoorc "
            + " WHERE to_char(dataalteracao, 'yyyy') = :ano "
            + " and (codigo like :parte "
            + " or lower(descricao) like :parte)";
        Query q = em.createNativeQuery(sql, AlteracaoORC.class);
        q.setParameter("ano", ex.getAno().toString());
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<AlteracaoORC>();
        } else {
            return q.getResultList();
        }
    }

    public List<AlteracaoORC> getAlteracoesOrcEfetivadas(String parte) {
        String hql = "select alt from AlteracaoORC alt "
            + " where alt.efetivado = true "
            + " and (atoLegal.numero like :parte "
            + " or lower(atoLegal.nome) like :parte"
            + " or lower(alt.codigo) like :parte"
            + " or lower(alt.descricao) like :parte) order by alt.id desc";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<AlteracaoORC>();
    }

    public List<AlteracaoORC> listaAlteracaoORCDeferidaPorExercicio(String parte, Exercicio exercicio) {
        String sql = " select alt.* from alteracaoorc alt                                   " +
            "       left join atolegal ato on ato.id = alt.atolegal_id                  " +
            "       where alt.status = '" + StatusAlteracaoORC.DEFERIDO.name() + "'" +
            "       and (alt.codigo like :filtro or lower(alt.descricao) like :filtro " +
            "         or ato.numero like :filtro or lower(ato.nome) like :filtro)     " +
            "       and alt.exercicio_id = :idExercicio                               " +
            "       order by alt.id desc ";
        Query q = em.createNativeQuery(sql, AlteracaoORC.class);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("idExercicio", exercicio.getId());
        q.setMaxResults(10);
        if (q.getResultList().isEmpty() || q.getResultList() == null) {
            return new ArrayList<AlteracaoORC>();
        }
        return q.getResultList();
    }

    public String buscarUltimoCodigoAlteracaoOrcamentaria(Exercicio exercicio) {
        try {
            String hql = "select coalesce(max(to_number(ao.codigo)),0)+1 from AlteracaoORC ao where exercicio_id = :exercicio";
            Query q = em.createNativeQuery(hql);
            q.setParameter("exercicio", exercicio.getId());
            q.setMaxResults(1);
            BigDecimal valor = (BigDecimal) q.getSingleResult();
            return Util.zerosAEsquerda(valor.toString(), 4);
        } catch (Exception e) {

            return "0001";
        }
    }

    private List<EntidadeContabil> recuperarSuplementacoes(Exercicio exercicio, EventosReprocessar eventosReprocessar) {
        try {
            String sql = "select supl.* from alteracaoorc alt "
                + "   inner join exercicio ex on alt.exercicio_id = ex.id "
                + "   inner join suplementacaoorc supl on alt.id = supl.alteracaoorc_id"
                + "   inner join fontedespesaorc font on supl.fontedespesaorc_id = font.id"
                + "   inner join despesaorc desp on font.despesaorc_id = desp.id"
                + "   inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id"
                + "   where trunc(alt.dataEfetivacao) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')" +
                "       and alt.status in ('DEFERIDO','ESTORNADA')";
            if (eventosReprocessar.hasUnidades()) {
                sql += " and prov.unidadeOrganizacional_id in (:unidades)";
            }
            sql += " order by alt.dataEfetivacao asc ";
            Query consulta = em.createNativeQuery(sql, SuplementacaoORC.class);
            consulta.setParameter("di", DataUtil.getDataFormatada(eventosReprocessar.getDataInicial()));
            consulta.setParameter("df", DataUtil.getDataFormatada(eventosReprocessar.getDataFinal()));
            if (eventosReprocessar.hasUnidades()) {
                consulta.setParameter("unidades", eventosReprocessar.getIdUnidades());
            }
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<EntidadeContabil> recuperarAnulacoes(Exercicio exercicio, EventosReprocessar eventosReprocessar) {
        try {
            String sql = "select supl.* from alteracaoorc alt "
                + "   inner join exercicio ex on alt.exercicio_id = ex.id "
                + "   inner join anulacaoorc supl on alt.id = supl.alteracaoorc_id"
                + "   inner join fontedespesaorc font on supl.fontedespesaorc_id = font.id"
                + "   inner join despesaorc desp on font.despesaorc_id = desp.id"
                + "   inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id"
                + "   where trunc(alt.dataEfetivacao) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')" +
                "       and alt.status in ('DEFERIDO','ESTORNADA')";
            if (eventosReprocessar.hasUnidades()) {
                sql += " and prov.unidadeOrganizacional_id in (:unidades)";
            }
            sql += " order by alt.dataEfetivacao asc ";
            Query consulta = em.createNativeQuery(sql, AnulacaoORC.class);
            consulta.setParameter("di", DataUtil.getDataFormatada(eventosReprocessar.getDataInicial()));
            consulta.setParameter("df", DataUtil.getDataFormatada(eventosReprocessar.getDataFinal()));
            if (eventosReprocessar.hasUnidades()) {
                consulta.setParameter("unidades", eventosReprocessar.getIdUnidades());
            }
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<EntidadeContabil> recuperarReceitas(Exercicio exercicio, EventosReprocessar eventosReprocessar) {
        try {
            String sql = "select supl.* from alteracaoorc alt "
                + "   inner join exercicio ex on alt.exercicio_id = ex.id "
                + "   inner join receitaalteracaoorc supl on alt.id = supl.alteracaoorc_id"
                + "   inner join receitaloa rec on supl.receitaloa_id = rec.id"
                + "   where ex.id = :ex" +
                "       and trunc(alt.dataEfetivacao) between to_date(:di,'dd/mm/yyyy') " +
                "       and to_date(:df,'dd/mm/yyyy')" +
                "       and alt.status in ('DEFERIDO','ESTORNADA') ";
            if (eventosReprocessar.hasUnidades()) {
                sql += " and rec.entidade_id in (:unidades)";
            }
            sql += " order by alt.dataEfetivacao asc ";
            Query consulta = em.createNativeQuery(sql, ReceitaAlteracaoORC.class);
            consulta.setParameter("ex", exercicio);
            consulta.setParameter("di", DataUtil.getDataFormatada(eventosReprocessar.getDataInicial()));
            consulta.setParameter("df", DataUtil.getDataFormatada(eventosReprocessar.getDataFinal()));
            if (eventosReprocessar.hasUnidades()) {
                consulta.setParameter("unidades", eventosReprocessar.getIdUnidades());
            }
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<SuplementacaoORC> recuperarSuplementacaoPorAlteracaoORC(AlteracaoORC alteracaoORC) {
        String sql = " select sup.* from suplementacaoorc sup " +
            " where sup.alteracaoorc_id = :idAlteracao ";
        Query q = em.createNativeQuery(sql, SuplementacaoORC.class);
        q.setParameter("idAlteracao", alteracaoORC.getId());
        if (q.getResultList().isEmpty() || q.getResultList() == null) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public List<AnulacaoORC> recuperarAnulacaoPorAlteracaoORC(AlteracaoORC alteracaoORC) {
        String sql = " select anul.* from anulacaoorc anul " +
            " where anul.alteracaoorc_id = :idAlteracao ";
        Query q = em.createNativeQuery(sql, AnulacaoORC.class);
        q.setParameter("idAlteracao", alteracaoORC.getId());
        if (q.getResultList().isEmpty() || q.getResultList() == null) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public List<ReceitaAlteracaoORC> recuperarReceitaPorAlteracaoORC(AlteracaoORC alteracaoORC) {
        String sql = " select anul.* from receitaalteracaoorc anul " +
            " where anul.alteracaoorc_id = :idAlteracao ";
        Query q = em.createNativeQuery(sql, ReceitaAlteracaoORC.class);
        q.setParameter("idAlteracao", alteracaoORC.getId());
        if (q.getResultList().isEmpty() || q.getResultList() == null) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public ReservaFonteDespesaOrcFacade getReservaFonteDespesaOrcFacade() {
        return reservaFonteDespesaOrcFacade;
    }

    public LiberacaoFonteDespesaORCFacade getLiberacaoFonteDespesaORCFacade() {
        return liberacaoFonteDespesaORCFacade;
    }

    public AnulacaoOrcFacade getAnulacaoOrcFacade() {
        return anulacaoOrcFacade;
    }

    public GrupoCotaORCFacade getGrupoCotaORCFacade() {
        return grupoCotaORCFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ContabilizacaoFacade getContabilizacaoFacade() {
        return contabilizacaoFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public ReceitaLOAFacade getReceitaLOAFacade() {
        return receitaLOAFacade;
    }

    public LOAFacade getLoaFacade() {
        return loaFacade;
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public CotaOrcamentariaFacade getCotaOrcamentariaFacade() {
        return cotaOrcamentariaFacade;
    }

    public SaldoReceitaORCFacade getSaldoReceitaORCFacade() {
        return saldoReceitaORCFacade;
    }

    public ProvisaoPPAFacade getProvisaoPPAFacade() {
        return provisaoPPAFacade;
    }

    public GrupoOrcamentarioFacade getGrupoOrcamentarioFacade() {
        return grupoOrcamentarioFacade;
    }

    public SolicitacaoDespesaOrc salvarRetornandoSolicitacao(SolicitacaoDespesaOrc solicitacaoDespesaOrc, AlteracaoORC alteracaoORC) {
        return em.merge(solicitacaoDespesaOrc);
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public MinutaAlteracaoOrcamentariaFacade getMinutaAlteracaoOrcamentariaFacade() {
        return minutaAlteracaoOrcamentariaFacade;
    }

    public void setMinutaAlteracaoOrcamentariaFacade(MinutaAlteracaoOrcamentariaFacade minutaAlteracaoOrcamentariaFacade) {
        this.minutaAlteracaoOrcamentariaFacade = minutaAlteracaoOrcamentariaFacade;
    }

    public SuplementacaoOrcFacade getSuplementacaoOrcFacade() {
        return suplementacaoOrcFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public ExtensaoFonteRecursoFacade getExtensaoFonteRecursoFacade() {
        return extensaoFonteRecursoFacade;
    }

    public List<AlteracaoORC> buscarAlteracaoOrcamentaria(Exercicio exercicio, List<HierarquiaOrganizacional> listaUnidades) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }
        try {
            String hql = "select alt.* from alteracaoorc alt "
                + "   inner join exercicio ex on alt.exercicio_id = ex.id "
                + "   where ex.id = :ex" +
                "       and alt.status in ('DEFERIDO', 'ESTORNADA') " +
                "       and alt.unidadeOrganizacionalOrc_id in (:unidades) ";
            Query consulta = em.createNativeQuery(hql, AlteracaoORC.class);
            consulta.setParameter("ex", exercicio);
            consulta.setParameter("unidades", unidades);
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public boolean isGestorOrcamento(UsuarioSistema usuario, UnidadeOrganizacional unidadeOrcamentaria, Date dataOperacao) {
        return usuarioSistemaFacade.isGestorOrcamento(usuario, unidadeOrcamentaria, dataOperacao);
    }

    public EsferaDoPoder recuperarEsferaPoderPorUnidade(UnidadeOrganizacional unidade) {
        String sql = " select vw.esferadopoder " +
            "     from vwhierarquiaorcamentaria vw " +
            "     where to_date(:dataOperacao,'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataOperacao,'dd/MM/yyyy')) " +
            "     and vw.subordinada_id = :idUnidade ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("idUnidade", unidade.getId());
        try {
            return EsferaDoPoder.valueOf((String) q.getResultList().get(0));
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        List<ConsultaMovimentoContabil> retorno = Lists.newArrayList();
        retorno.add(criarConsultaSuplementacao(filtros));
        retorno.add(criarConsultaAnulacao(filtros));
        retorno.add(criarConsultaReceitaAlteracao(filtros));
        return retorno;
    }

    public ConsultaMovimentoContabil criarConsultaSuplementacao(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(SuplementacaoORC.class, ConsultaMovimentoContabil.clonarFiltros(filtros));
        consulta.incluirJoinsComplementar(" inner join alteracaoorc alt on obj.alteracaoORC_id = alt.id");
        consulta.incluirJoinsOrcamentoDespesa(" obj.fonteDespesaORC_id ");
        alterarFiltrosComuns(consulta);
        return consulta;
    }

    public ConsultaMovimentoContabil criarConsultaAnulacao(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(AnulacaoORC.class, ConsultaMovimentoContabil.clonarFiltros(filtros));
        consulta.incluirJoinsComplementar(" inner join alteracaoorc alt on obj.alteracaoORC_id = alt.id");
        consulta.incluirJoinsOrcamentoDespesa(" obj.fonteDespesaORC_id ");
        alterarFiltrosComuns(consulta);
        return consulta;
    }

    public void alterarFiltrosComuns(ConsultaMovimentoContabil consulta) {
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(alt.dataEfetivacao)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(alt.dataEfetivacao)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "prov.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "prov.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "alt.codigo");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
    }

    public ConsultaMovimentoContabil criarConsultaReceitaAlteracao(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(ReceitaAlteracaoORC.class, ConsultaMovimentoContabil.clonarFiltros(filtros));
        consulta.incluirJoinsComplementar(" inner join alteracaoorc alt on obj.alteracaoORC_id = alt.id");
        consulta.incluirJoinsComplementar(" inner join receitaloafonte rcfonte on obj.receitaloa_id = rcfonte.receitaloa_id");
        consulta.incluirJoinsOrcamentoReceita("obj.receitaloa_id", "rcfonte.id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "alt.dataEfetivacao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "alt.dataEfetivacao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "rec.entidade_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "rec.entidade_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "alt.codigo");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        return consulta;
    }

    public BigDecimal buscarValorPorFonteDespesaOrc(FonteDespesaORC fonteDespesaORC, Date dataInicial, Date dataFinal) {
        String sql = "select sum(valor) from (" +
            " SELECT coalesce(sum(s.valor), 0) *-1 as valor " +
            "  FROM ALTERACAOORC ALT  " +
            " inner join anulacaoorc s on alt.id = s.alteracaoorc_id  " +
            " inner join FONTEDESPESAORC fontdesp on s.FONTEDESPESAORC_ID = fontdesp.id  " +
            "  WHERE trunc(alt.DATAEFETIVACAO) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')  " +
            "  and alt.status in (:status)   " +
            "  and fontdesp.id = :fonte " +
            "  " +
            " union all " +
            "  " +
            " SELECT coalesce(sum(s.valor), 0) as valor " +
            "  FROM ALTERACAOORC ALT  " +
            " inner join suplementacaoorc s on alt.id = s.alteracaoorc_id  " +
            " inner join FONTEDESPESAORC fontdesp on s.FONTEDESPESAORC_ID = fontdesp.id  " +
            " WHERE trunc(alt.DATAEFETIVACAO) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')  " +
            "  and alt.status in (:status) " +
            "  and fontdesp.id = :fonte " +
            "  " +
            " union all " +
            "  " +
            " SELECT coalesce(sum(s.valor), 0) *-1 as valor " +
            "  FROM estornoalteracaoorc est  " +
            "  inner join ALTERACAOORC ALT on est.alteracaoorc_id = alt.id  " +
            "  inner join suplementacaoorc s on alt.id = s.alteracaoorc_id  " +
            "  inner join FONTEDESPESAORC fontdesp on s.FONTEDESPESAORC_ID = fontdesp.id  " +
            "   WHERE trunc(est.dataestorno) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "  and fontdesp.id = :fonte " +
            "  " +
            " union all " +
            "  " +
            " SELECT coalesce(sum(anul.valor), 0) as valor " +
            "  from estornoalteracaoorc est  " +
            "  inner join ALTERACAOORC ALT on est.alteracaoorc_id = alt.id  " +
            "  inner join anulacaoorc anul on alt.id = anul.alteracaoorc_id  " +
            "  inner join FONTEDESPESAORC fontdesp on anul.FONTEDESPESAORC_ID = fontdesp.id  " +
            "   where trunc(est.dataestorno) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')  " +
            "  and fontdesp.id = :fonte )";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fonte", fonteDespesaORC.getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("status", Arrays.asList(StatusAlteracaoORC.DEFERIDO.name(), StatusAlteracaoORC.ESTORNADA.name()));
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public List<Object[]> buscarAlteracoesOrcamentarias(Exercicio exercicio, Date dataOperacao) {
        String sql = " select extract(year from dataMovimento) as ano, " +
            "        extract(month from dataMovimento) as mes, " +
            "        extract(day from dataMovimento) as dia, " +
            "        substr(contaCodigo, 0, 12) as contaCodigo, " +
            "        unidade, " +
            "        funcaoCodigo, " +
            "        programaCodigo, " +
            "        subFuncaoCodigo, " +
            "        tipoAcaoCodigo, " +
            "        acaoCodigo, " +
            "        fonteCodigo, " +
            "        sum(valor) as valor " +
            "   from ( " +
            buscarSqlSuplementacoes() +
            "  union all " +
            " select ALT.DATAEFETIVACAO as dataMovimento, " +
            "        c.codigo as contaCodigo, " +
            "        vw.codigo as unidade, " +
            "        f.codigo as funcaoCodigo, " +
            "        prog.codigo as programaCodigo, " +
            "        sf.codigo as subFuncaoCodigo, " +
            "        tpa.codigo as tipoAcaoCodigo, " +
            "        a.codigo as acaoCodigo, " +
            "        fr.codigo as fonteCodigo, " +
            "        anul.valor * -1 as valor " +
            "   FROM ANULACAOORC anul   " +
            "  INNER JOIN ALTERACAOORC ALT ON ALT.ID = anul.ALTERACAOORC_ID   " +
            "  INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = anul.FONTEDESPESAORC_ID   " +
            "  INNER JOIN PROVISAOPPAFONTE PfF ON PfF.ID = FONT.PROVISAOPPAFONTE_ID  " +
            "  INNER JOIN DESPESAORC DESP ON FONT.DESPESAORC_ID = DESP.ID  " +
            "  INNER JOIN PROVISAOPPADESPESA provdesp ON provdesp.ID = DESP.PROVISAOPPADESPESA_ID  " +
            "  INNER join contadedestinacao cd on pff.destinacaoderecursos_id = cd.id  " +
            "  INNER join fontederecursos fr on cd.fontederecursos_id = fr.id  " +
            "  INNER JOIN CONTA C ON provdesp.CONTADEDESPESA_ID = C.ID " +
            "  INNER JOIN PLANODECONTASEXERCICIO PCE ON pce.planodedespesas_id = C.PLANODECONTAS_ID " +
            "  inner join exercicio ex on pce.exercicio_id = ex.id " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = PROVDESP.UNIDADEORGANIZACIONAL_ID  " +
            "  INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " +
            "  INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            "  INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            "  INNER JOIN programappa PROG ON PROG.ID = A.programa_id " +
            "  INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            "  INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            "  where ex.id = :exercicio " +
            "    AND TO_DATE(:dataOperacao, 'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataOperacao, 'DD/MM/YYYY'))  " +
            "    and alt.STATUS in ('DEFERIDO', 'ESTORNADA' ) " +
            "  union all " +
            " select EST.DATAESTORNO as dataMovimento, " +
            "        c.codigo as contaCodigo, " +
            "        vw.codigo as unidade, " +
            "        f.codigo as funcaoCodigo, " +
            "        prog.codigo as programaCodigo, " +
            "        sf.codigo as subFuncaoCodigo, " +
            "        tpa.codigo as tipoAcaoCodigo, " +
            "        a.codigo as acaoCodigo, " +
            "        fr.codigo as fonteCodigo, " +
            "        est.valor as valor " +
            "   FROM ANULACAOORC anul   " +
            "  INNER JOIN ALTERACAOORC ALT ON ALT.ID = anul.ALTERACAOORC_ID   " +
            "  INNER JOIN ESTORNOALTERACAOORC EST ON EST.ALTERACAOORC_ID = ALT.ID " +
            "  INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = anul.FONTEDESPESAORC_ID   " +
            "  INNER JOIN PROVISAOPPAFONTE PfF ON PfF.ID = FONT.PROVISAOPPAFONTE_ID  " +
            "  INNER JOIN DESPESAORC DESP ON FONT.DESPESAORC_ID = DESP.ID  " +
            "  INNER JOIN PROVISAOPPADESPESA provdesp ON provdesp.ID = DESP.PROVISAOPPADESPESA_ID  " +
            "  INNER join contadedestinacao cd on pff.destinacaoderecursos_id = cd.id  " +
            "  INNER join fontederecursos fr on cd.fontederecursos_id = fr.id  " +
            "  INNER JOIN CONTA C ON provdesp.CONTADEDESPESA_ID = C.ID " +
            "  INNER JOIN PLANODECONTASEXERCICIO PCE ON pce.planodedespesas_id = C.PLANODECONTAS_ID " +
            "  inner join exercicio ex on pce.exercicio_id = ex.id " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = PROVDESP.UNIDADEORGANIZACIONAL_ID  " +
            "  INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " +
            "  INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            "  INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            "  INNER JOIN programappa PROG ON PROG.ID = A.programa_id " +
            "  INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            "  INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            "  where ex.id = :exercicio " +
            "    AND TO_DATE(:dataOperacao, 'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataOperacao, 'DD/MM/YYYY'))  " +
            " ) " +
            "  group by dataMovimento, " +
            "           contaCodigo, " +
            "           unidade, " +
            "           funcaoCodigo, " +
            "           programaCodigo, " +
            "           subFuncaoCodigo, " +
            "           tipoAcaoCodigo, " +
            "           acaoCodigo, " +
            "           fonteCodigo " +
            "  order by dataMovimento, " +
            "           contaCodigo, " +
            "           unidade, " +
            "           funcaoCodigo, " +
            "           programaCodigo, " +
            "           subFuncaoCodigo, " +
            "           tipoAcaoCodigo, " +
            "           acaoCodigo, " +
            "           fonteCodigo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        return q.getResultList();
    }

    public String buscarSqlSuplementacoes() {
        return " select ALT.DATAEFETIVACAO as dataMovimento, " +
            "        c.codigo as contaCodigo, " +
            "        vw.codigo as unidade, " +
            "        f.codigo as funcaoCodigo, " +
            "        prog.codigo as programaCodigo, " +
            "        sf.codigo as subFuncaoCodigo, " +
            "        tpa.codigo as tipoAcaoCodigo, " +
            "        a.codigo as acaoCodigo, " +
            "        fr.codigo as fonteCodigo, " +
            "        sup.valor as valor " +
            "   FROM SUPLEMENTACAOORC SUP   " +
            "  INNER JOIN ALTERACAOORC ALT ON ALT.ID = SUP.ALTERACAOORC_ID   " +
            "  INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = SUP.FONTEDESPESAORC_ID   " +
            "  INNER JOIN PROVISAOPPAFONTE PfF ON PfF.ID = FONT.PROVISAOPPAFONTE_ID  " +
            "  INNER JOIN DESPESAORC DESP ON FONT.DESPESAORC_ID = DESP.ID  " +
            "  INNER JOIN PROVISAOPPADESPESA provdesp ON provdesp.ID = DESP.PROVISAOPPADESPESA_ID  " +
            "  INNER join contadedestinacao cd on pff.destinacaoderecursos_id = cd.id  " +
            "  INNER join fontederecursos fr on cd.fontederecursos_id = fr.id  " +
            "  INNER JOIN CONTA C ON provdesp.CONTADEDESPESA_ID = C.ID " +
            "  INNER JOIN PLANODECONTASEXERCICIO PCE ON pce.planodedespesas_id = C.PLANODECONTAS_ID " +
            "  inner join exercicio ex on pce.exercicio_id = ex.id " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = PROVDESP.UNIDADEORGANIZACIONAL_ID  " +
            "  INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " +
            "  INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            "  INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            "  INNER JOIN programappa PROG ON PROG.ID = A.programa_id " +
            "  INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            "  INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            "  where ex.id = :exercicio " +
            "    AND TO_DATE(:dataOperacao, 'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataOperacao, 'DD/MM/YYYY'))  " +
            "    and alt.STATUS in ('DEFERIDO', 'ESTORNADA' ) " +
            "  union all " +
            " select EST.DATAESTORNO as dataMovimento, " +
            "        c.codigo as contaCodigo, " +
            "        vw.codigo as unidade, " +
            "        f.codigo as funcaoCodigo, " +
            "        prog.codigo as programaCodigo, " +
            "        sf.codigo as subFuncaoCodigo, " +
            "        tpa.codigo as tipoAcaoCodigo, " +
            "        a.codigo as acaoCodigo, " +
            "        fr.codigo as fonteCodigo, " +
            "        est.valor * - 1 as valor " +
            "   FROM SUPLEMENTACAOORC SUP   " +
            "  INNER JOIN ALTERACAOORC ALT ON ALT.ID = SUP.ALTERACAOORC_ID   " +
            "  INNER JOIN ESTORNOALTERACAOORC EST ON EST.ALTERACAOORC_ID = ALT.ID " +
            "  INNER JOIN FONTEDESPESAORC FONT ON FONT.ID = SUP.FONTEDESPESAORC_ID   " +
            "  INNER JOIN PROVISAOPPAFONTE PfF ON PfF.ID = FONT.PROVISAOPPAFONTE_ID  " +
            "  INNER JOIN DESPESAORC DESP ON FONT.DESPESAORC_ID = DESP.ID  " +
            "  INNER JOIN PROVISAOPPADESPESA provdesp ON provdesp.ID = DESP.PROVISAOPPADESPESA_ID  " +
            "  INNER join contadedestinacao cd on pff.destinacaoderecursos_id = cd.id  " +
            "  INNER join fontederecursos fr on cd.fontederecursos_id = fr.id  " +
            "  INNER JOIN CONTA C ON provdesp.CONTADEDESPESA_ID = C.ID " +
            "  INNER JOIN PLANODECONTASEXERCICIO PCE ON pce.planodedespesas_id = C.PLANODECONTAS_ID " +
            "  inner join exercicio ex on pce.exercicio_id = ex.id " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON VW.SUBORDINADA_ID = PROVDESP.UNIDADEORGANIZACIONAL_ID  " +
            "  INNER JOIN SUBACAOPPA SUB ON SUB.ID = provdesp.subacaoppa_id " +
            "  INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            "  INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            "  INNER JOIN programappa PROG ON PROG.ID = A.programa_id " +
            "  INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            "  INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            "  where ex.id = :exercicio " +
            "    AND TO_DATE(:dataOperacao, 'dd/mm/yyyy') BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, TO_DATE(:dataOperacao, 'DD/MM/YYYY'))  ";
    }

    public List<Object[]> buscarSuplementacoesOrcamentarias(Exercicio exercicio, Date dataOperacao) {
        String sql = " select extract(year from dataMovimento) as ano, " +
            "        extract(month from dataMovimento) as mes, " +
            "        extract(day from dataMovimento) as dia, " +
            "        substr(contaCodigo, 0, 12) as contaCodigo, " +
            "        unidade, " +
            "        funcaoCodigo, " +
            "        programaCodigo, " +
            "        subFuncaoCodigo, " +
            "        tipoAcaoCodigo, " +
            "        acaoCodigo, " +
            "        fonteCodigo, " +
            "        sum(valor) as valor " +
            "   from ( " +
            buscarSqlSuplementacoes() +
            " ) " +
            "  group by dataMovimento, " +
            "           contaCodigo, " +
            "           unidade, " +
            "           funcaoCodigo, " +
            "           programaCodigo, " +
            "           subFuncaoCodigo, " +
            "           tipoAcaoCodigo, " +
            "           acaoCodigo, " +
            "           fonteCodigo " +
            "  order by dataMovimento, " +
            "           contaCodigo, " +
            "           unidade, " +
            "           funcaoCodigo, " +
            "           programaCodigo, " +
            "           subFuncaoCodigo, " +
            "           tipoAcaoCodigo, " +
            "           acaoCodigo, " +
            "           fonteCodigo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        return q.getResultList();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}

