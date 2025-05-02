package br.com.webpublico.negocios.rh.arquivos.integracao;

import br.com.webpublico.controle.ConfiguracaoIntegracaoRHContabilControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.viewobjects.IntegracaoEquiplanoRHVO;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.AsyncResult;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
public class ConfiguracaoIntegracaoRHContabilFacade {
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracaoIntegracaoRHContabilFacade.class);

    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    public List<IntegracaoEquiplanoRHVO> buscarConfiguracoes(Mes mes, Exercicio exercicio, List<RecursoFP> filtroRecursos, List<EventoFP> filtroEventos, boolean apenasEventosCadastrados) {
        List<IntegracaoEquiplanoRHVO> retorno = Lists.newArrayList();
        List<Long> idsRecursos = filtroRecursos != null ? filtroRecursos.stream().map(RecursoFP::getId).collect(Collectors.toList()) : Lists.newArrayList();
        List<String> codigosEventos = filtroEventos != null ? filtroEventos.stream().map(EventoFP::getCodigo).collect(Collectors.toList()) : Lists.newArrayList();

        for (RecursoFP recursoFP : recursoFPFacade.buscarRecursosFPVigente(mes, exercicio, idsRecursos, codigosEventos)) {
            retorno.addAll(converterRecursoFP(recursoFP, mes, exercicio, codigosEventos));
        }
        if (!apenasEventosCadastrados) {
            retorno.addAll(buscarEventosNaoConfigurados(mes, exercicio, idsRecursos, codigosEventos));
        }
        Collections.sort(retorno);
        return retorno;
    }

    private List<IntegracaoEquiplanoRHVO> converterRecursoFP(RecursoFP recursoFP, Mes mes, Exercicio exercicio, List<String> codigosEventos) {
        List<IntegracaoEquiplanoRHVO> retorno = Lists.newArrayList();
        Date dataParam = DataUtil.getDateParse("01/" + mes.getNumeroMesString() + "/" + exercicio.getAno());
        for (FontesRecursoFP fontesRecursoFP : recursoFP.getFontesRecursoFPs()) {
            if (fontesRecursoFP.getInicioVigencia().equals(dataParam)) {
                for (FonteEventoFP fonteEventoFP : fontesRecursoFP.getFonteEventoFPs()) {
                    if (codigosEventos == null || codigosEventos.isEmpty() ||
                        (fonteEventoFP.getEventoFP() != null && codigosEventos.contains(fonteEventoFP.getEventoFP().getCodigo()))) {
                        IntegracaoEquiplanoRHVO item = new IntegracaoEquiplanoRHVO();
                        item.setIdFonteEvento(fonteEventoFP.getId());
                        item.setRecursoFP(recursoFP);
                        item.setFontesRecursoFP(fontesRecursoFP);
                        item.setTipoContabilizacao(fonteEventoFP.getTipoContabilizacao());
                        item.setDespesaORC(fonteEventoFP.getDespesaORC());
                        item.setFonteDespesaORC(fonteEventoFP.getFonteDespesaORC());
                        item.setFontesRecursoFP(fonteEventoFP.getFontesRecursoFP());
                        item.setFornecedor(fonteEventoFP.getFornecedor());
                        item.setCredor(fonteEventoFP.getCredor());
                        item.setClasseCredor(fonteEventoFP.getClasseCredor());
                        item.setContaExtraorcamentaria(fonteEventoFP.getContaExtraorcamentaria());
                        item.setSubConta(fonteEventoFP.getSubConta());
                        item.setEventoFP(fonteEventoFP.getEventoFP());
                        item.setOperacaoFormula(fonteEventoFP.getOperacaoFormula());
                        item.setDesdobramento(fonteEventoFP.getDesdobramento());
                        item.setTipoContribuicao(fonteEventoFP.getTipoContribuicao());
                        retorno.add(item);
                    }
                }
            }
        }
        return retorno;
    }

    public Date getDataOperacao() {
        return sistemaFacade.getDataOperacao();
    }

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public Exercicio getExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public HierarquiaOrganizacional getOrcamentariaLogada() {
        return hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente(), getDataOperacao());
    }

    public HierarquiaOrganizacional getAdministrativaLogada() {
        return hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente(), getDataOperacao());
    }

    public List<FonteDespesaORC> completarFontesDespesaORC(String filtro, DespesaORC despesaORC) {
        if (despesaORC != null) {
            return fonteDespesaORCFacade.completaFonteDespesaORC(filtro, despesaORC);
        } else {
            return fonteDespesaORCFacade.completaFonteDespesaORC(filtro);
        }
    }

    public List<Conta> completarDesdobramentos(String filtro, DespesaORC despesaORC) {
        if (despesaORC != null) {
            return contaFacade.buscarContaDespesaFilhasDespesaOrcPorTipoAndExercicio(filtro, despesaORC.getProvisaoPPADespesa().getContaDeDespesa(), despesaORC.getExercicio(), null, 10);
        } else {
            return Lists.newArrayList();
        }
    }

    public List<VinculoFP> completarVinculosFP(String filtro) {
        return contratoFPFacade.buscarContratoPorNomeOrUnidadeOrNumeroOrMatriculaFP(filtro);
    }

    public List<Conta> completarContaExtraorcamentaria(String filtro) {
        return contaFacade.buscarContasExtraorcamentario(filtro, getExercicioCorrente(), null);
    }

    public List<ContaDeDestinacao> completarContasDeDestinacao(Exercicio exercicio, String filtro) {
        return contaFacade.buscarContaDaProvissaoPPAFonte(exercicio, filtro);
    }

    public void removerConfiguracao(IntegracaoEquiplanoRHVO selecionado) {
        recursoFPFacade.removerFonteEventoFP(selecionado.getIdFonteEvento());
    }

    public void salvarConfiguracao(AssistenteBarraProgresso assistente, IntegracaoEquiplanoRHVO selecionado, Mes mes, Exercicio exercicioDestino, Exercicio exercicioOrigem) {
        FonteEventoFP entity = new FonteEventoFP();
        entity.setId(selecionado.getIdFonteEvento());
        if (exercicioOrigem.getAno() < exercicioDestino.getAno()) {
            if (selecionado.getContaExtraorcamentaria() != null) {
                List<Conta> contasEquivalentes = contaFacade.recuperarContaExtraorcamentariaEquivalentePorId(selecionado.getContaExtraorcamentaria(), exercicioDestino);
                if (contasEquivalentes.isEmpty()) {
                    logger.error("A conta extraorçamentária " + selecionado.getContaExtraorcamentaria() + " não possui conta extraorçamentária equivalente no exercício " + exercicioDestino.getAno() + ".");
                    String mensagem = "A conta extraorçamentária " + selecionado.getContaExtraorcamentaria() + " não possui conta extraorçamentária equivalente no exercício " + exercicioDestino.getAno() + ".";
                    if (assistente.getValidacaoException().getMensagens().isEmpty() || !getMensagensAsString(assistente.getValidacaoException()).contains(mensagem)) {
                        assistente.getValidacaoException().adicionarMensagemDeOperacaoNaoPermitida(mensagem);
                    }
                } else {
                    entity.setContaExtraorcamentaria((ContaExtraorcamentaria) contasEquivalentes.get(0));
                }
            }
        } else {
            entity.setDespesaORC(selecionado.getDespesaORC());
            entity.setFonteDespesaORC(selecionado.getFonteDespesaORC());
            if (selecionado.getFonteDespesaORC() != null) {
                entity.setFonteDeRecursos(selecionado.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos());
            }
            entity.setContaExtraorcamentaria(selecionado.getContaExtraorcamentaria());
            entity.setFontesRecursoFP(selecionado.getFontesRecursoFP());
        }
        entity.setTipoContabilizacao(selecionado.getTipoContabilizacao());
        entity.setFornecedor(selecionado.getFornecedor());
        entity.setCredor(selecionado.getCredor());
        entity.setClasseCredor(selecionado.getClasseCredor());
        entity.setSubConta(selecionado.getSubConta());
        entity.setEventoFP(selecionado.getEventoFP());
        entity.setOperacaoFormula(selecionado.getOperacaoFormula());
        entity.setDesdobramento(selecionado.getDesdobramento());
        entity.setTipoContribuicao(selecionado.getTipoContribuicao());
        entity.setFontesRecursoFP(recursoFPFacade.recuperarOrCriarVigente(selecionado.getRecursoFP(), mes, exercicioDestino.getAno()));

        recursoFPFacade.salvarFonteEventoFP(entity);
    }

    private List<String> getMensagensAsString(ValidacaoException ve) {
        List<String> retorno = Lists.newArrayList();
        for (FacesMessage facesMessage : ve.getMensagens()) {
            retorno.add(facesMessage.getDetail());
        }
        return retorno;
    }

    public ContaDeDestinacao recuperarContaDeDestinacao(FonteDeRecursos fonteDeRecursos) {
        return (ContaDeDestinacao) contaFacade.recuperarContaDestinacaoPorFonte(fonteDeRecursos);
    }

    private List<IntegracaoEquiplanoRHVO> buscarEventosNaoConfigurados(Mes mes, Exercicio exercicio, List<Long> idsRecursos, List<String> codigosEventos) {
        List<IntegracaoEquiplanoRHVO> retorno = Lists.newArrayList();
        List<Object[]> eventosNaoConfigurados = eventoFPFacade.buscarEventosNaoConfigurados(mes, exercicio.getAno(), idsRecursos, codigosEventos);

        for (Object[] obj : eventosNaoConfigurados) {
            IntegracaoEquiplanoRHVO item = new IntegracaoEquiplanoRHVO();
            item.setEventoFP(eventoFPFacade.recuperar(((BigDecimal) obj[3]).longValue()));
            item.setRecursoFP(recursoFPFacade.recuperar(((BigDecimal) obj[4]).longValue()));
            retorno.add(item);
        }
        return retorno;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future copiarConfiguracao(AssistenteBarraProgresso assistente, Mes mesOrigem, Exercicio exercicioOrigem, Mes mesDestino, Exercicio exercicioDestino, ConfiguracaoIntegracaoRHContabilControlador.TipoCopia tipoCopia) {
        try {
            assistente.setMensagensValidacaoFacesUtil(new ArrayList<FacesMessage>());
            List<IntegracaoEquiplanoRHVO> eventosOrigem = Lists.newArrayList();
            for (RecursoFP recursoFP : recursoFPFacade.buscarRecursosFPVigente(mesOrigem, exercicioOrigem, null, null)) {
                eventosOrigem.addAll(converterRecursoFP(recursoFP, mesOrigem, exercicioOrigem, null));
            }

            List<IntegracaoEquiplanoRHVO> eventosDestino = Lists.newArrayList();
            if (ConfiguracaoIntegracaoRHContabilControlador.TipoCopia.COPIA_SUBSTITUICAO.equals(tipoCopia)) {
                for (RecursoFP recursoFP : recursoFPFacade.buscarRecursosFPVigente(mesDestino, exercicioDestino, null, null)) {
                    eventosDestino.addAll(converterRecursoFP(recursoFP, mesDestino, exercicioDestino, null));
                }
            }
            eventosDestino.addAll(buscarEventosNaoConfigurados(mesDestino, exercicioDestino, null, null));

            assistente.setDescricaoProcesso("Cópia da configuração de eventos da competência " + mesOrigem + "/" + exercicioOrigem.getAno() + " para " + mesDestino + "/" + exercicioDestino.getAno());
            assistente.setCalculados(0);
            assistente.setTotal(eventosDestino.size());

            for (IntegracaoEquiplanoRHVO eventoDestino : eventosDestino) {
                for (IntegracaoEquiplanoRHVO eventoOrigem : eventosOrigem) {
                    if (eventoDestino.equals(eventoOrigem)) {
                        IntegracaoEquiplanoRHVO correspondente = (IntegracaoEquiplanoRHVO) Util.clonarObjeto(eventoOrigem);
                        if (correspondente == null)
                            continue;
                        if (correspondente.getTipoContabilizacao() == null) {
                            correspondente.setIdFonteEvento(null);
                            salvarConfiguracao(assistente, correspondente, mesDestino, exercicioDestino, exercicioOrigem);
                        } else {
                            correspondente.setIdFonteEvento(eventoDestino.getIdFonteEvento());
                            salvarConfiguracao(assistente, correspondente, mesDestino, exercicioDestino, exercicioOrigem);
                        }
                        break;
                    }
                }
                assistente.conta();
            }
        } catch (Exception ex) {

            logger.error(ex.getMessage());
        }
        return new AsyncResult<>(null);
    }

    public FolhaDePagamento buscarFolhaDePagamento(Mes mes, Exercicio exercicio, TipoFolhaDePagamento tipoFolhaDePagamento) {
        Optional<FolhaDePagamento> optional = folhaDePagamentoFacade.getFolhasDePagamentoEfetivadasNoMesDoTipo(mes, exercicio.getAno(), tipoFolhaDePagamento, false).stream().findFirst();
        return optional.orElse(null);
    }

    public List<RecursoFP> buscarRecursosFP(String parte, Mes mes, Exercicio exercicio) {
        return recursoFPFacade.buscarRecursosFP(parte, mes, exercicio);
    }

    public List<EventoFP> buscarEventos(String parte, Mes mes, Integer ano, List<Long> idsRecursos) {
        return eventoFPFacade.buscarEventos(parte, mes, ano, idsRecursos);
    }
}
