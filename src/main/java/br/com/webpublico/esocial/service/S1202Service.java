package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.EventoFPEmpregador;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.rh.esocial.TipoApuracaoFolha;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.periodicos.EventoS1202;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.PrestadorServicosFacade;
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
import java.util.Date;
import java.util.List;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1202Service")
public class S1202Service {
    protected static final Logger logger = LoggerFactory.getLogger(S1202Service.class);

    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private PessoaFisicaFacade pessoaFisicaFacade;
    private EventoFPFacade eventoFPFacade;

    private RegistroESocialFacade registroESocialFacade;
    private PrestadorServicosFacade prestadorServicosFacade;

    @Autowired
    private ESocialService eSocialService;


    @PostConstruct
    public void init() {
        try {
            configuracaoEmpregadorESocialFacade = (ConfiguracaoEmpregadorESocialFacade) Util.getFacadeViaLookup("java:module/ConfiguracaoEmpregadorESocialFacade");
            fichaFinanceiraFPFacade = (FichaFinanceiraFPFacade) Util.getFacadeViaLookup("java:module/FichaFinanceiraFPFacade");
            pessoaFisicaFacade = (PessoaFisicaFacade) Util.getFacadeViaLookup("java:module/PessoaFisicaFacade");
            eventoFPFacade = (EventoFPFacade) Util.getFacadeViaLookup("java:module/EventoFPFacade");
            registroESocialFacade = (RegistroESocialFacade) Util.getFacadeViaLookup("java:module/RegistroESocialFacade");
        } catch (Exception e) {
            logger.error("Não foi possivel criar a instancia: " + e);
        }
    }

    public void enviarEventoS1202(RegistroEventoEsocial registroS1202, EmpregadorESocial empregador,
                                  ConfiguracaoEmpregadorESocial config, VinculoFP vinculoFP, List<Long> idsFicha,
                                  PrestadorServicos prestadorServicos) {
        ValidacaoException ve = new ValidacaoException();
        EventoS1202 eventoS1202 = criarEventoS1202(registroS1202, empregador, vinculoFP, ve, config, idsFicha, prestadorServicos);
        logger.debug("Antes de Enviar: " + eventoS1202.getXml());
        ve.lancarException();
        eSocialService.enviarEventoS1202(eventoS1202, vinculoFP);
    }

    private EventoS1202 criarEventoS1202(RegistroEventoEsocial registroS1202, EmpregadorESocial empregador,
                                         VinculoFP vinculoFP, ValidacaoException ve, ConfiguracaoEmpregadorESocial config,
                                         List<Long> idsFicha, PrestadorServicos prestadorServicos) {
        EventosESocialDTO.S1202 eventoS1202 = (EventosESocialDTO.S1202) eSocialService.getEventoS1202(empregador);
        eventoS1202.setClasseWP(ClasseWP.FICHAFINANCEIRAFP_OU_RPA);
        String ano = registroS1202.getExercicio().getAno().toString();
        if (TipoApuracaoFolha.MENSAL.equals(registroS1202.getTipoApuracaoFolha())) {
            String mes = registroS1202.getMes().getNumeroMes().toString();
            eventoS1202.setIdESocial(TipoApuracaoFolha.MENSAL.name().concat(ano).concat(mes).concat(vinculoFP.getId().toString()));
        } else {
            eventoS1202.setIdESocial(TipoFolhaDePagamento.SALARIO_13.name().concat(ano).concat(vinculoFP.getId().toString()));
        }

        eventoS1202.setIndApuracao(TipoApuracaoFolha.SALARIO_13.equals(registroS1202.getTipoApuracaoFolha()) ? 2 : 1);
        if (TipoApuracaoFolha.SALARIO_13.equals(registroS1202.getTipoApuracaoFolha())) {
            eventoS1202.setPerApur(registroS1202.getExercicio().getAno());
        } else {
            eventoS1202.setPerApur(registroS1202.getExercicio().getAno(), registroS1202.getMes().getNumeroMes());
        }
        adicionarInformacoesBasicas(eventoS1202, vinculoFP, ve, registroS1202);
        adicionarInformacoesRemuneracao(registroS1202, eventoS1202, idsFicha, prestadorServicos, config, ve);
        return eventoS1202;
    }

    private void adicionarMatricula(VinculoFP vinculoFP, PrestadorServicos prestadorServicos,
                                    EventoS1202.DmDev.IdeEstab.RemunPerApur remunPerApur) {
        if (ModalidadeContratoFP.PRESTADOR_SERVICO.equals(vinculoFP.getContratoFP().getModalidadeContratoFP().getCodigo())) {
            if (prestadorServicos != null) {
                remunPerApur.setMatricula(prestadorServicos.getMatriculaESocial());
            }
        } else {
            String matricula = registroESocialFacade.getMatriculaS2200ProcessadoSucesso(vinculoFP);
            if (matricula == null) {
                String numeroVinculo = StringUtil.cortarOuCompletarEsquerda(vinculoFP.getNumero(), 2, "0");
                remunPerApur.setMatricula(vinculoFP.getMatriculaFP().getMatricula().concat(numeroVinculo));
            } else {
                remunPerApur.setMatricula(matricula);
            }
        }
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S1202 eventoS1202, VinculoFP vinculoFP,
                                             ValidacaoException val, RegistroEventoEsocial registroS1202) {
        PessoaFisica pessoa = vinculoFP.getMatriculaFP().getPessoa();
        pessoa = pessoaFisicaFacade.recuperarComDocumentos(pessoa.getId());
        eventoS1202.setCpfTrab(StringUtil.retornaApenasNumeros(pessoa.getCpf()));
        if (pessoa.getCarteiraDeTrabalho() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoa + " não possui carteira de trabalho.");
        } else {
            eventoS1202.setNisTrab(pessoa.getCarteiraDeTrabalho().getPisPasep());
        }
        if (registroS1202.getTipoOperacaoESocial() != null) {
            eventoS1202.setOperacao(TipoOperacaoESocial.valueOf(registroS1202.getTipoOperacaoESocial().name()));
        }
    }

    private void adicionarInformacoesRemuneracao(RegistroEventoEsocial registroEventoEsocial,
                                                 EventosESocialDTO.S1202 eventoS1202, List<Long> idFichaFinanceiraFP,
                                                 PrestadorServicos prestadorServicos, ConfiguracaoEmpregadorESocial config,
                                                 ValidacaoException val) {
        for (Long idFichaFinanceira : idFichaFinanceiraFP) {
            EventoS1202.DmDev dmDev = eventoS1202.addDmDev();
            dmDev.setIdeDmDev(idFichaFinanceira.toString());
            FichaFinanceiraFP fichaFinanceiraFP = fichaFinanceiraFPFacade.recuperar(idFichaFinanceira);

            if (Strings.isNullOrEmpty(eventoS1202.getIdentificadorWP())) {
                eventoS1202.setIdentificadorWP(idFichaFinanceira.toString());
            } else {
                eventoS1202.setIdentificadorWP(eventoS1202.getIdentificadorWP().concat("/").
                        concat(idFichaFinanceira.toString()));
            }
            eventoS1202.setDescricao(fichaFinanceiraFP.getVinculoFP().toString());

            if (fichaFinanceiraFP.getVinculoFP().getCategoriaTrabalhador() != null) {
                dmDev.setCodCateg(fichaFinanceiraFP.getVinculoFP().getCategoriaTrabalhador().getCodigo());
            } else {
                val.adicionarMensagemDeOperacaoNaoPermitida("Servidor(a) " + fichaFinanceiraFP.getVinculoFP() + " não possuí Categoria do Trabalhador configurado.");
            }

            EventoS1202.DmDev.IdeEstab ideEstab = dmDev.addIdeEstab();
            ideEstab.setTpInsc(config.getClassificacaoTributaria().getTpInsc());
            ideEstab.setNrInsc(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));

            EventoS1202.DmDev.IdeEstab.RemunPerApur remunPerApur = ideEstab.addRemunPerApur();
            adicionarMatricula(fichaFinanceiraFP.getVinculoFP(), prestadorServicos, remunPerApur);

            Date dataReferencia = DataUtil.criarDataComMesEAno((TipoApuracaoFolha.SALARIO_13.equals(registroEventoEsocial.getTipoApuracaoFolha()) ?
                fichaFinanceiraFP.getFolhaDePagamento().getMes().getNumeroMes() : registroEventoEsocial.getMes().getNumeroMes()), registroEventoEsocial.getExercicio().getAno()).toDate();

            boolean temVerbaCadastrada = false;
            for (ItemFichaFinanceiraFP item : fichaFinanceiraFP.getItemFichaFinanceiraFP()) {
                EventoFPEmpregador eventoFPEmpregador = eventoFPFacade.getEventoFPEmpregador(item.getEventoFP(), dataReferencia, registroEventoEsocial.getEntidade());
                if (eventoFPEmpregador != null) {
                    temVerbaCadastrada = true;
                    EventoS1202.DmDev.IdeEstab.RemunPerApur.ItensRemun itensRemun = remunPerApur.addItensRemun();
                    if (Strings.isNullOrEmpty(eventoFPEmpregador.getIdentificacaoTabela())) {
                        val.adicionarMensagemDeOperacaoNaoPermitida("O evento " + eventoFPEmpregador.getEventoFP() + " não possuí Identificação da Tabela informado.");
                    } else {
                        itensRemun.setIdeTabRubr(eventoFPEmpregador.getIdentificacaoTabela());
                    }
                    itensRemun.setCodRubr(item.getEventoFP().getCodigo());

                    itensRemun.setVrRubr(item.getValor());
                    itensRemun.setIndApurIR(0);
                }
            }
            if (!temVerbaCadastrada) {
                val.adicionarMensagemDeOperacaoNaoPermitida("Servidor(a) " + fichaFinanceiraFP.getVinculoFP() + " não possuí Verbas da Folha de Pagamento configuradas.");
            }
        }
    }
}
