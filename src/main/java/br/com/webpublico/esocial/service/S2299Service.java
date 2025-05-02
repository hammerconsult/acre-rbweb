package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.AvisoPrevio;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.EventoFPEmpregador;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoValorPensaoAlimenticia;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2299;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.cadastrofuncional.AvisoPrevioFacade;
import br.com.webpublico.negocios.rh.esocial.RegistroESocialFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.math.BigDecimal;
import java.util.List;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

/**
 * Created by William on 22/10/2018.
 */
@Service(value = "s2299Service")
public class S2299Service {

    protected static final Logger logger = LoggerFactory.getLogger(S2299Service.class);

    @Autowired
    private ESocialService eSocialService;

    private PensaoAlimenticiaFacade pensaoAlimenticiaFacade;
    private AvisoPrevioFacade avisoPrevioFacade;
    private PessoaFisicaFacade pessoaFisicaFacade;
    private SistemaFacade sistemaFacade;
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private RegistroESocialFacade registroESocialFacade;
    private EventoFPFacade eventoFPFacade;

    @PostConstruct
    public void init() {
        try {
            pensaoAlimenticiaFacade = (PensaoAlimenticiaFacade) new InitialContext().lookup("java:module/PensaoAlimenticiaFacade");
            avisoPrevioFacade = (AvisoPrevioFacade) new InitialContext().lookup("java:module/AvisoPrevioFacade");
            sistemaFacade = (SistemaFacade) new InitialContext().lookup("java:module/SistemaFacade");
            folhaDePagamentoFacade = (FolhaDePagamentoFacade) new InitialContext().lookup("java:module/FolhaDePagamentoFacade");
            registroESocialFacade = (RegistroESocialFacade) new InitialContext().lookup("java:module/RegistroESocialFacade");
            pessoaFisicaFacade = (PessoaFisicaFacade) new InitialContext().lookup("java:module/PessoaFisicaFacade");
            eventoFPFacade = (EventoFPFacade) new InitialContext().lookup("java:module/EventoFPFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    public void enviarS2299(ConfiguracaoEmpregadorESocial config, ExoneracaoRescisao exoneracaoRescisao) {
        ValidacaoException val = new ValidacaoException();
        EventoS2299 s2299 = criarEventoS2299(exoneracaoRescisao, config, val);
        logger.debug("Antes de Enviar: " + s2299.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2299(s2299);
    }


    private EventoS2299 criarEventoS2299(ExoneracaoRescisao exoneracaoRescisao, ConfiguracaoEmpregadorESocial config, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2299 eventoS2299 = (EventosESocialDTO.S2299) eSocialService.getEventoS2299(empregador);
        eventoS2299.setIdentificadorWP(exoneracaoRescisao.getId().toString());
        eventoS2299.setClasseWP(ClasseWP.EXONERACAORESCISAO);
        eventoS2299.setDescricao(exoneracaoRescisao.getVinculoFP().toString());

        eventoS2299.setIdESocial(registroESocialFacade.getIDRegistroConcatenadaComExclusao(exoneracaoRescisao.getId(), TipoArquivoESocial.S2299));
        adicionarInformacoesBasicas(eventoS2299, exoneracaoRescisao, val);
        adicionarInformacoesDesligamento(eventoS2299, exoneracaoRescisao, val);
        adicionarInformacoesPensaoAlimenticia(eventoS2299, exoneracaoRescisao);
        adicionarValoresFolhaNormaAndRescisao(eventoS2299, exoneracaoRescisao, config, val);
        return eventoS2299;
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S2299 eventoS2299, ExoneracaoRescisao exoneracaoRescisao, ValidacaoException val) {
        PessoaFisica pessoa = exoneracaoRescisao.getVinculoFP().getMatriculaFP().getPessoa();
        pessoa = pessoaFisicaFacade.recuperarComDocumentos(pessoa.getId());
        eventoS2299.setCpfTrab(StringUtil.retornaApenasNumeros(pessoa.getCpf()));
        if (pessoa.getCarteiraDeTrabalho() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoa + " não possui carteira de trabalho.");
        } else {
            eventoS2299.setNisTrab(pessoa.getCarteiraDeTrabalho().getPisPasep());
        }
        String matricula = registroESocialFacade.getMatriculaS2200ProcessadoSucesso(exoneracaoRescisao.getVinculoFP());
        if (matricula == null) {
            String numeroVinculo = StringUtil.cortarOuCompletarEsquerda(exoneracaoRescisao.getVinculoFP().getNumero(), 2, "0");
            eventoS2299.setMatricula(exoneracaoRescisao.getVinculoFP().getMatriculaFP().getMatricula().concat(numeroVinculo));
        } else {
            eventoS2299.setMatricula(matricula);
        }

        if (exoneracaoRescisao.getTipoOperacaoESocial() != null) {
            eventoS2299.setOperacao(TipoOperacaoESocial.valueOf(exoneracaoRescisao.getTipoOperacaoESocial().name()));
        }
    }

    private void adicionarInformacoesDesligamento(EventosESocialDTO.S2299 eventoS2299, ExoneracaoRescisao exoneracaoRescisao, ValidacaoException val) {
        if (exoneracaoRescisao.getMotivoExoneracaoRescisao().getTipoMotivoDesligamentoESocial() == null) {
            val.adicionarMensagemDeCampoObrigatorio("A Exoneração não tem as informações do Motivo do Desligamento E-social");
        } else {
            eventoS2299.setMtvDeslig(exoneracaoRescisao.getMotivoExoneracaoRescisao().getTipoMotivoDesligamentoESocial().getCodigo());
        }
        eventoS2299.setDtDeslig(exoneracaoRescisao.getDataRescisao());

        List<AvisoPrevio> avisoPrevio = avisoPrevioFacade.getAvisoPrevioPorContratoFP((ContratoFP) exoneracaoRescisao.getVinculoFP());
        if (avisoPrevio == null || avisoPrevio.isEmpty()) {
            eventoS2299.setIndPagtoAPI(false);
        } else {
            eventoS2299.setIndPagtoAPI(true);
        }

        if (avisoPrevio != null && !avisoPrevio.isEmpty()) {
            eventoS2299.setDtProjFimAPI(avisoPrevio.get(0).getDataDesligamento());
        }
    }

    private Boolean podeBuscarValoresPensao(BeneficioPensaoAlimenticia beneficioPensaoAlimenticia, ValorPensaoAlimenticia valorPensaoAlimenticia) {
        if (beneficioPensaoAlimenticia.getFinalVigencia() == null ||
            (sistemaFacade.getDataOperacao().after(beneficioPensaoAlimenticia.getInicioVigencia()) && sistemaFacade.getDataOperacao().before(beneficioPensaoAlimenticia.getFinalVigencia()))) {
            if (valorPensaoAlimenticia.getFinalVigencia() == null ||
                (sistemaFacade.getDataOperacao().after(valorPensaoAlimenticia.getInicioVigencia()) && sistemaFacade.getDataOperacao().before(valorPensaoAlimenticia.getFinalVigencia()))) {
                return true;
            }
        }
        return false;
    }

    private void adicionarInformacoesPensaoAlimenticia(EventosESocialDTO.S2299 eventoS2299, ExoneracaoRescisao exoneracaoRescisao) {
        List<PensaoAlimenticia> pensao = pensaoAlimenticiaFacade.buscarPensoesAlimenticiasPorInstituidor(exoneracaoRescisao.getVinculoFP().getMatriculaFP().getPessoa());
        if (pensao == null || pensao.isEmpty()) {
            eventoS2299.setPensAlim(0);
        } else {
            for (PensaoAlimenticia pensaoAlimenticia : pensao) {
                BigDecimal valor = BigDecimal.ZERO;
                BigDecimal percentual = BigDecimal.ZERO;
                for (BeneficioPensaoAlimenticia beneficioPensaoAlimenticia : pensaoAlimenticia.getBeneficiosPensaoAlimenticia()) {
                    for (ValorPensaoAlimenticia valorPensaoAlimenticia : beneficioPensaoAlimenticia.getValoresPensaoAlimenticia()) {
                        if (podeBuscarValoresPensao(beneficioPensaoAlimenticia, valorPensaoAlimenticia)) {
                            if (beneficioPensaoAlimenticia.getTipoValorPensaoAlimenticia().equals(TipoValorPensaoAlimenticia.VALOR_FIXO)) {
                                valor = valor.add(valorPensaoAlimenticia.getValor());
                                eventoS2299.setVrAlim(valor);
                                eventoS2299.setPensAlim(2);
                            } else {
                                percentual = percentual.add(valorPensaoAlimenticia.getValor());
                                eventoS2299.setPercAliment(percentual);
                                eventoS2299.setPensAlim(1);
                            }
                        }
                    }
                }
                if (valor.compareTo(BigDecimal.ZERO) > 0 && percentual.compareTo(BigDecimal.ZERO) > 0) {
                    eventoS2299.setPensAlim(3);
                }
                if (valor.compareTo(BigDecimal.ZERO) == 0 && percentual.compareTo(BigDecimal.ZERO) == 0) {
                    eventoS2299.setPensAlim(0);
                }
            }
        }
    }

    private void adicionarValoresFolhaNormaAndRescisao(EventosESocialDTO.S2299 eventoS2299,
                                                       ExoneracaoRescisao exoneracaoRescisao,
                                                       ConfiguracaoEmpregadorESocial config, ValidacaoException ve) {

        ContratoFP contratoFP = exoneracaoRescisao.getVinculoFP().getContratoFP();
        Mes mes = Mes.getMesToInt(DataUtil.getMes(exoneracaoRescisao.getDataRescisao()));
        Integer ano = DataUtil.getAno(exoneracaoRescisao.getDataRescisao());

        if (contratoFP.isTipoRegimeCeletista()) {
            List<FichaFinanceiraFP> fichas = folhaDePagamentoFacade.buscarFichasPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidades(mes, ano, exoneracaoRescisao.getVinculoFP(),
                Lists.newArrayList(TipoFolhaDePagamento.RESCISAO.name()));

            if (fichas == null || fichas.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa " + contratoFP.getDescricaoCompleta() + " não possui nenhuma Ficha Financeira encontrada!");
            } else {
                for (FichaFinanceiraFP ficha : fichas) {
                    EventoS2299.DmDev dmDev = eventoS2299.addDmDev();
                    dmDev.setIdeDmDev(ficha.getId().toString());

                    EventoS2299.DmDev.IdeEstabLot ideEstabLot = dmDev.addIdeEstabLot();
                    ideEstabLot.setTpInsc(1); //cnpj
                    ideEstabLot.setNrInsc(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
                    ideEstabLot.setCodLotacao(config.getEntidade().getId().toString());
                    ideEstabLot.setGrauExp(1);

                    for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : ficha.getItemFichaFinanceiraFP()) {
                        EventoFPEmpregador eventoFPEmpregador = eventoFPFacade.getEventoFPEmpregador(itemFichaFinanceiraFP.getEventoFP(), exoneracaoRescisao.getDataRescisao(),
                            config.getEntidade());

                        if (eventoFPEmpregador == null) {
                            if (!TipoEventoFP.INFORMATIVO.equals(itemFichaFinanceiraFP.getEventoFP().getTipoEventoFP())) {
                                ve.adicionarMensagemDeCampoObrigatorio("Evento FP " + itemFichaFinanceiraFP.getEventoFP() + " sem configurações de envio!");
                            }
                            continue;
                        }
                        if (!TipoEventoFP.INFORMATIVO.equals(itemFichaFinanceiraFP.getEventoFP().getTipoEventoFP()) && Strings.isNullOrEmpty(eventoFPEmpregador.getIdentificacaoTabela())) {
                            ve.adicionarMensagemDeCampoObrigatorio("Evento FP " + eventoFPEmpregador.getEventoFP() + " sem Identificador de Tabela para envio!");
                            continue;
                        }
                        EventoS2299.DmDev.IdeEstabLot.DetVerbas detVerbas = ideEstabLot.addDetVerbas();
                        detVerbas.setCodRubr(itemFichaFinanceiraFP.getEventoFP().getCodigo());
                        detVerbas.setIdeTabRubr(eventoFPEmpregador.getIdentificacaoTabela());
                        if (itemFichaFinanceiraFP.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa " + contratoFP.getDescricaoCompleta() + " - O item  " + itemFichaFinanceiraFP + " - Deve  ter o valor da rubrica maior que 0 (zero)");
                            continue;
                        } else {
                            detVerbas.setVrRubr(itemFichaFinanceiraFP.getValor());
                        }
                        detVerbas.setIndApurIR(0);
                    }
                }
            }
        }
    }
}
