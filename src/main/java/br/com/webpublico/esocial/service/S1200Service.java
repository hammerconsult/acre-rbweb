package br.com.webpublico.esocial.service;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.EventoFPEmpregador;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.entidades.rh.esocial.VinculoFPEventoEsocial;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.rh.esocial.TipoApuracaoFolha;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.periodicos.EventoS1200;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.negocios.FichaRPAFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.negocios.rh.esocial.RegistroESocialFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1200Service")
public class S1200Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1200Service.class);
    public static final int EMPREGADO_GERAL_ADM_DIRETA_OU_INDIRETA_CONTRATADO_CLT = 101;

    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    private PessoaFisicaFacade pessoaFisicaFacade;
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private FichaRPAFacade fichaRPAFacade;
    private RegistroESocialFacade registroESocialFacade;
    private EventoFPFacade eventoFPFacade;

    @Autowired
    private ESocialService eSocialService;

    @PostConstruct
    public void init() {
        try {
            configuracaoEmpregadorESocialFacade = (ConfiguracaoEmpregadorESocialFacade) new InitialContext().lookup("java:module/ConfiguracaoEmpregadorESocialFacade");
            pessoaFisicaFacade = (PessoaFisicaFacade) new InitialContext().lookup("java:module/PessoaFisicaFacade");
            fichaFinanceiraFPFacade = (FichaFinanceiraFPFacade) new InitialContext().lookup("java:module/FichaFinanceiraFPFacade");
            registroESocialFacade = (RegistroESocialFacade) new InitialContext().lookup("java:module/RegistroESocialFacade");
            eventoFPFacade = (EventoFPFacade) new InitialContext().lookup("java:module/EventoFPFacade");
            fichaRPAFacade = (FichaRPAFacade) new InitialContext().lookup("java:module/FichaRPAFacade");
        } catch (NamingException e) {
            logger.error("Não foi possivel criar a instancia: " + e.getMessage());
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    public void enviarS1200(RegistroEventoEsocial registroEventoEsocial, EmpregadorESocial empregadorESocial,
                            ConfiguracaoEmpregadorESocial config,
                            List<Long> idFicha,
                            PessoaFisica pf, VinculoFPEventoEsocial vinculoEvento,
                            ConfiguracaoRH configuracaoRH) {
        ValidacaoException val = new ValidacaoException();
        EventoS1200 s1200 = criarEventoS1200(registroEventoEsocial, empregadorESocial, val, idFicha, config, pf, vinculoEvento, configuracaoRH);
        logger.debug("Antes de Enviar: " + s1200.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1200(s1200);
    }

    private EventoS1200 criarEventoS1200(RegistroEventoEsocial registroEventoEsocial, EmpregadorESocial empregador,
                                         ValidacaoException val, List<Long> idsFicha, ConfiguracaoEmpregadorESocial config,
                                         PessoaFisica pf, VinculoFPEventoEsocial vinculoEvento,
                                         ConfiguracaoRH configuracaoRH) {
        EventosESocialDTO.S1200 eventoS1200 = (EventosESocialDTO.S1200) eSocialService.getEventoS1200(empregador);
        eventoS1200.setClasseWP(ClasseWP.FICHAFINANCEIRAFP_OU_RPA);
        String ano = registroEventoEsocial.getExercicio().getAno().toString();
        if (TipoApuracaoFolha.MENSAL.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            String mes = registroEventoEsocial.getMes().getNumeroMes().toString();
            eventoS1200.setIdESocial(TipoApuracaoFolha.MENSAL.name().concat(ano).concat(mes).concat(vinculoEvento.getCpf()));
        } else {
            eventoS1200.setIdESocial(TipoFolhaDePagamento.SALARIO_13.name().concat(ano).concat(vinculoEvento.getCpf()));
        }

        eventoS1200.setIndApuracao(TipoApuracaoFolha.SALARIO_13.equals(registroEventoEsocial.getTipoApuracaoFolha()) ? 2 : 1);
        if (TipoApuracaoFolha.SALARIO_13.equals(registroEventoEsocial.getTipoApuracaoFolha())) {
            eventoS1200.setPerApur(registroEventoEsocial.getExercicio().getAno());
        } else {
            eventoS1200.setPerApur(registroEventoEsocial.getExercicio().getAno(), registroEventoEsocial.getMes().getNumeroMes());
        }
        adicionarInformacoesBasicas(registroEventoEsocial, eventoS1200, val, vinculoEvento);
        adicionarInformacoesRemuneracao(registroEventoEsocial, eventoS1200, idsFicha, config, val, configuracaoRH);
        return eventoS1200;
    }

    private void adicionarInformacoesBasicas(RegistroEventoEsocial registroEventoEsocial,
                                             EventosESocialDTO.S1200 eventoS1200, ValidacaoException val,
                                             VinculoFPEventoEsocial vinculoEvento) {
        PessoaFisica pessoa = pessoaFisicaFacade.recuperarComDocumentos(vinculoEvento.getIdPessoa());
        eventoS1200.setCpfTrab(StringUtil.retornaApenasNumeros(pessoa.getCpf()));
        if (pessoa.getCarteiraDeTrabalho() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoa + " não possui carteira de trabalho.");
        } else {
            eventoS1200.setNisTrab(pessoa.getCarteiraDeTrabalho().getPisPasep());
        }
        if (registroEventoEsocial.getTipoOperacaoESocial() != null) {
            eventoS1200.setOperacao(TipoOperacaoESocial.valueOf(registroEventoEsocial.getTipoOperacaoESocial().name()));
        }
    }

    private void adicionarInformacoesRemuneracao(RegistroEventoEsocial registroEventoEsocial,
                                                 EventosESocialDTO.S1200 eventoS1200, List<Long> idFichaFinanceiraFP,
                                                 ConfiguracaoEmpregadorESocial config, ValidacaoException ve,
                                                 ConfiguracaoRH configuracaoRH) {
        for (Long idFichaFinanceira : idFichaFinanceiraFP) {
            EventoS1200.DmDev dmDev = eventoS1200.addDmDev();
            if (Strings.isNullOrEmpty(eventoS1200.getIdentificadorWP())) {
                eventoS1200.setIdentificadorWP(idFichaFinanceira.toString());
            } else {
                eventoS1200.setIdentificadorWP(eventoS1200.getIdentificadorWP().concat("/").
                    concat(idFichaFinanceira.toString()));
            }

            dmDev.setIdeDmDev(idFichaFinanceira.toString());
            FichaFinanceiraFP fichaFinanceiraFP = fichaFinanceiraFPFacade.buscarFichaFinanceiraPorId(idFichaFinanceira);
            FichaRPA fichaRPA = fichaRPAFacade.buscarFichaRPAPorId(idFichaFinanceira);

            if (fichaFinanceiraFP != null) {
                eventoS1200.setDescricao(fichaFinanceiraFP.getVinculoFP().toString());
                dmDev.setCodCateg(fichaFinanceiraFP.getVinculoFP().getCategoriaTrabalhador() != null ?
                    fichaFinanceiraFP.getVinculoFP().getCategoriaTrabalhador().getCodigo() : EMPREGADO_GERAL_ADM_DIRETA_OU_INDIRETA_CONTRATADO_CLT);
            } else {
                if (fichaRPA != null && fichaRPA.getPrestadorServicos().getCategoriaTrabalhador() != null) {
                    eventoS1200.setDescricao(fichaRPA.getPrestadorServicos().getPrestador().toString());
                    dmDev.setCodCateg(fichaRPA.getPrestadorServicos().getCategoriaTrabalhador().getCodigo());
                }
            }
            EventoS1200.DmDev.IdeEstabLot ideEstabLot = dmDev.addIdeEstabLot();
            ideEstabLot.setTpInsc(config.getClassificacaoTributaria().getTpInsc());
            ideEstabLot.setNrInsc(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
            ideEstabLot.setCodLotacao(registroEventoEsocial.getEntidade().getId().toString());

            EventoS1200.DmDev.IdeEstabLot.RemunPerApur remunPerApur = ideEstabLot.addRemunPerApur();
            Date dataReferencia = DataUtil.criarDataComMesEAno((TipoApuracaoFolha.SALARIO_13.equals(registroEventoEsocial.getTipoApuracaoFolha()) && fichaFinanceiraFP != null ?
                    fichaFinanceiraFP.getFolhaDePagamento().getMes().getNumeroMes() : registroEventoEsocial.getMes().getNumeroMes()),
                registroEventoEsocial.getExercicio().getAno()).toDate();

            if (fichaFinanceiraFP != null) {
                String matricula = registroESocialFacade.getMatriculaS2200ProcessadoSucesso(fichaFinanceiraFP.getVinculoFP());
                if (matricula == null) {
                    String vinculo = StringUtil.cortarOuCompletarEsquerda(fichaFinanceiraFP.getVinculoFP().getNumero(), 2, "0");
                    remunPerApur.setMatricula(fichaFinanceiraFP.getVinculoFP().getMatriculaFP().getMatricula().concat(vinculo));
                } else {
                    remunPerApur.setMatricula(matricula);
                }
                remunPerApur.setGrauExp(1);
                preencherValoresItemFichaFinanceiraFP(registroEventoEsocial, fichaFinanceiraFP.getItemFichaFinanceiraFP(),
                        remunPerApur, dataReferencia, ve, configuracaoRH);
            } else if (fichaRPA != null) {
                remunPerApur.setMatricula(fichaRPA.getPrestadorServicos().getMatriculaESocial());
                preencherValoresItemFichaRPA(registroEventoEsocial, idFichaFinanceira, remunPerApur, dataReferencia, ve,
                        fichaRPA.getPrestadorServicos(), configuracaoRH);
            }
        }
    }

    private void preencherValoresItemFichaFinanceiraFP(RegistroEventoEsocial registroEventoEsocial,
                                                       List<ItemFichaFinanceiraFP> itensFicha,
                                                       EventoS1200.DmDev.IdeEstabLot.RemunPerApur remunPerApur,
                                                       Date dataReferencia, ValidacaoException ve,
                                                       ConfiguracaoRH configuracaoRH) {
        for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : itensFicha) {
            EventoFPEmpregador eventoFPEmpregador = eventoFPFacade.getEventoFPEmpregador(itemFichaFinanceiraFP.getEventoFP(), dataReferencia, registroEventoEsocial.getEntidade());

            if (eventoFPEmpregador == null) {
                if (!TipoEventoFP.INFORMATIVO.equals(itemFichaFinanceiraFP.getEventoFP().getTipoEventoFP())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O evento " + itemFichaFinanceiraFP.getEventoFP() + " não tem as configurações necessárias para envio da verba ao e-social");
                    ve.lancarException();
                }
            } else {
                if (!TipoEventoFP.INFORMATIVO.equals(itemFichaFinanceiraFP.getEventoFP().getTipoEventoFP()) && Strings.isNullOrEmpty(eventoFPEmpregador.getIdentificacaoTabela())) {
                    ve.adicionarMensagemDeCampoObrigatorio("Evento FP " + eventoFPEmpregador.getEventoFP() + " sem Identificador de Tabela para envio!");
                    continue;
                }
                if (eventoFPEmpregador.getIncidenciaPrevidencia() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Evento FP " + eventoFPEmpregador.getEventoFP() + " sem Incidência Tributária para a Previdência Social.");
                    continue;
                }
                if (eventoFPEmpregador.getIncidenciaTributariaIRRF() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Evento FP " + eventoFPEmpregador.getEventoFP() + " sem Incidência Tributária para IRRF.");
                    continue;
                }
                EventoS1200.DmDev.IdeEstabLot.RemunPerApur.ItensRemun itensRemun = remunPerApur.addItensRemun();
                itensRemun.setCodRubr(itemFichaFinanceiraFP.getEventoFP().getCodigo());
                itensRemun.setIdeTabRubr(eventoFPEmpregador.getIdentificacaoTabela());
                itensRemun.setVrRubr(itemFichaFinanceiraFP.getValor());
                Integer ano = DataUtil.getAno(configuracaoRH.getDataObrigatoriedadeApuracaoIR());
                Integer mes = DataUtil.getMes(configuracaoRH.getDataObrigatoriedadeApuracaoIR());
                Integer mesFolha = TipoApuracaoFolha.SALARIO_13.equals(registroEventoEsocial.getTipoApuracaoFolha()) ?
                    itemFichaFinanceiraFP.getFichaFinanceiraFP().getFolhaDePagamento().getMes().getNumeroMes() : registroEventoEsocial.getMes().getNumeroMes();
                if (registroEventoEsocial.getExercicio().getAno() >= ano && mesFolha >= mes) {
                    itensRemun.setIndApurIR(Util.converterZeroOuUm(itemFichaFinanceiraFP.getEventoFP().getApuracaoIR()));
                }
            }
        }
    }

    private void preencherValoresItemFichaRPA(RegistroEventoEsocial registroEventoEsocial, Long idFichaRPA,
                                              EventoS1200.DmDev.IdeEstabLot.RemunPerApur remunPerApur,
                                              Date dataReferencia, ValidacaoException ve, PrestadorServicos prestador,
                                              ConfiguracaoRH configuracaoRH) {
        FichaRPA fichaRPA = fichaRPAFacade.recuperar(idFichaRPA);
        for (ItemFichaRPA itemFichaRPA : fichaRPA.getItemFichaRPAs()) {
            EventoFPEmpregador eventoFPEmpregador = eventoFPFacade.getEventoFPEmpregador(itemFichaRPA.getEventoFP(), dataReferencia, registroEventoEsocial.getEntidade());

            if (eventoFPEmpregador == null) {

                ve.adicionarMensagemDeOperacaoNaoPermitida("O Evento <b>" + itemFichaRPA.getEventoFP() + " </b> não possuí configuração necessária ao E-social.");
                ve.lancarException();
                break;
            }
            if (Strings.isNullOrEmpty(eventoFPEmpregador.getIdentificacaoTabela())) {
                continue;
            }
            if (itemFichaRPA.getValor().compareTo(BigDecimal.ZERO) == 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(prestador.getPrestador() + " - Prestador de Serviço com Valor zero");
                ve.lancarException();
                break;
            }
            EventoS1200.DmDev.IdeEstabLot.RemunPerApur.ItensRemun itensRemun = remunPerApur.addItensRemun();
            itensRemun.setCodRubr(itemFichaRPA.getEventoFP().getCodigo());
            itensRemun.setIdeTabRubr(eventoFPEmpregador.getIdentificacaoTabela());
            itensRemun.setVrRubr(itemFichaRPA.getValor());
            Integer ano = DataUtil.getAno(configuracaoRH.getDataObrigatoriedadeApuracaoIR());
            Integer mes = DataUtil.getMes(configuracaoRH.getDataObrigatoriedadeApuracaoIR());
            if (registroEventoEsocial.getExercicio().getAno() >= ano && registroEventoEsocial.getMes().getNumeroMes() >= mes) {
                itensRemun.setIndApurIR(Util.converterZeroOuUm(itemFichaRPA.getEventoFP().getApuracaoIR()));
            }
        }
    }
}
