package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.PrestadorServicos;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.saudeservidor.AgenteNocivo;
import br.com.webpublico.entidades.rh.saudeservidor.RegistroAmbiental;
import br.com.webpublico.entidades.rh.saudeservidor.RiscoOcupacional;
import br.com.webpublico.enums.rh.esocial.TipoResponsavelAmbiental;
import br.com.webpublico.enums.rh.esocial.UtilizaEPC;
import br.com.webpublico.enums.rh.esocial.UtilizaEPI;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2240;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.saudeservidor.RiscoOcupacionalFacade;
import br.com.webpublico.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s2240Service")
public class S2240Service {

    protected static final Logger logger = LoggerFactory.getLogger(S2240Service.class);

    private RiscoOcupacionalFacade riscoOcupacionalFacade;

    @Autowired
    private ESocialService eSocialService;

    @PostConstruct
    public void init() {
        try {
            riscoOcupacionalFacade = (RiscoOcupacionalFacade) new InitialContext().lookup("java:module/RiscoOcupacionalFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    public void enviarS2240(ConfiguracaoEmpregadorESocial config, RiscoOcupacional riscoOcupacional) {
        ValidacaoException val = new ValidacaoException();
        EventoS2240 s2240 = criarEventoS2240(riscoOcupacional, config, val);
        logger.debug("Antes de Enviar: " + s2240.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2240(s2240);
    }

    private EventoS2240 criarEventoS2240(RiscoOcupacional riscoOcupacional, ConfiguracaoEmpregadorESocial config, ValidacaoException val) {
        riscoOcupacional = riscoOcupacionalFacade.recuperar(riscoOcupacional.getId());
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2240 eventoS2240 = (EventosESocialDTO.S2240) eSocialService.getEventoS2240(empregador);
        eventoS2240.setClasseWP(ClasseWP.RISCOOCUPACIONAL);
        eventoS2240.setIdentificadorWP(riscoOcupacional.getId().toString());
        eventoS2240.setDescricao(riscoOcupacional.getVinculoFP().toString());

        eventoS2240.setIdESocial(riscoOcupacional.getId().toString());
        adicionarInformacoesTrabalhador(eventoS2240, riscoOcupacional.getVinculoFP(), riscoOcupacional.getPrestadorServico());
        adicionarInformacoesExposicaoRisco(eventoS2240, riscoOcupacional);
//        adicionarInformacoesAmbiente(config, eventoS2240, riscoOcupacional);
        adicionarInformacoesAgenteNocivo(eventoS2240, riscoOcupacional);
        adicionarInformacoesRegistroAmbiental(eventoS2240, riscoOcupacional);
        return eventoS2240;
    }

    private void adicionarMatricula(EventosESocialDTO.S2240 eventoS2240, VinculoFP vinculoFP, String vinculo, PrestadorServicos prestadorServico) {
        if (ModalidadeContratoFP.PRESTADOR_SERVICO.equals(vinculoFP.getContratoFP().getModalidadeContratoFP().getCodigo())) {
            eventoS2240.setMatricula(prestadorServico.getMatriculaESocial());
        } else {
            eventoS2240.setMatricula(vinculoFP.getMatriculaFP().getMatricula().concat(vinculo));
        }
    }

    private void adicionarInformacoesTrabalhador(EventosESocialDTO.S2240 eventoS2240, VinculoFP vinculoFP, PrestadorServicos prestadorServico) {
        String cpf;
        if (vinculoFP != null) {
            String vinculo = StringUtil.cortarOuCompletarEsquerda(vinculoFP.getNumero(), 2, "0");
            adicionarMatricula(eventoS2240, vinculoFP, vinculo, prestadorServico);
            cpf = StringUtil.retornaApenasNumeros(vinculoFP.getMatriculaFP().getPessoa().getCpf());
        } else {
            cpf = StringUtil.retornaApenasNumeros(prestadorServico.getPrestador().getCpf_Cnpj());
            eventoS2240.setCodCateg(prestadorServico.getCategoriaTrabalhador().getCodigo());
        }
        eventoS2240.setCpfTrab(cpf);
    }

    private void adicionarInformacoesExposicaoRisco(EventosESocialDTO.S2240 eventoS2240, RiscoOcupacional riscoOcupacional) {
        eventoS2240.setDtIniCondicao(riscoOcupacional.getInicio());
        eventoS2240.setDscAtivDes(riscoOcupacional.getDescricaoAtividade());
    }

//    private void adicionarInformacoesAmbiente(ConfiguracaoEmpregadorESocial config, EventosESocialDTO.S2240 eventoS2240, RiscoOcupacional riscoOcupacional) {
//        EventoS2240.InfoAmb infoAmb = eventoS2240.addInfoAmb();
//        infoAmb.setLocalAmb(riscoOcupacional.getLocalRiscoOcupacional().getCodigo());
//        infoAmb.setDscSetor(riscoOcupacional.getDescricaoSetor());
//        infoAmb.setTpInsc(config.getClassificacaoTributaria().getTpInsc());
//        infoAmb.setNrInsc(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
//    }

    private void adicionarInformacoesAgenteNocivo(EventosESocialDTO.S2240 eventoS2240, RiscoOcupacional riscoOcupacional) {
        for (AgenteNocivo agenteNocivo : riscoOcupacional.getItemAgenteNovico()) {
            EventoS2240.AgNoc agNoc = eventoS2240.addAgNoc();
            agNoc.setCodAgNoc(agenteNocivo.getCodigoAgenteNocivo().getCodigo());
            agNoc.setDscAgNoc(agenteNocivo.getDescricaoAgenteNocivo());
            agNoc.setTpAval(agenteNocivo.getTipoAvaliacaoAgenteNocivo().getCodigo());
            agNoc.setIntConc(agenteNocivo.getIntensidadeConcentracao());
            agNoc.setLimTol(agenteNocivo.getLimiteTolerancia());
            agNoc.setUnMed(agenteNocivo.getTipoUnidadeMedida().getCodigo());
            agNoc.setTecMedicao(agenteNocivo.getTecnicaMedicao());

            agNoc.setUtilizEPC(agenteNocivo.getUtilizaEPC().getCodigo());
            if (UtilizaEPC.IMPLEMENTA.equals(agenteNocivo.getUtilizaEPC())) {
                agNoc.setEficEpc(agenteNocivo.getEpcEficaz());
            }
            agNoc.setUtilizEPI(agenteNocivo.getUtilizaEPI().getCodigo());
            if (agenteNocivo.getUtilizaEPI().equals(UtilizaEPI.UTILIZADO)) {
                agNoc.setEficEpi(agenteNocivo.getEpiEficaz());
            }
        }
    }

    private void adicionarInformacoesRegistroAmbiental(EventosESocialDTO.S2240 eventoS2240, RiscoOcupacional riscoOcupacional) {
        for (RegistroAmbiental registroAmbiental : riscoOcupacional.getItemRegistroAmbiental()) {
            EventoS2240.RespReg respReg = eventoS2240.addRespReg();
            respReg.setCpfResp(retornaApenasNumeros(registroAmbiental.getResponsavel().getCpf()));
            respReg.setIdeOC(registroAmbiental.getTipoResponsavelAmbiental().getCodigo());
            if (TipoResponsavelAmbiental.OUTROS.equals(registroAmbiental.getTipoResponsavelAmbiental())) {
                respReg.setDscOC(registroAmbiental.getDescricaoClasse());
            }
            respReg.setNrOC(registroAmbiental.getNumeroInscricaoClasse());
            respReg.setUfOC(registroAmbiental.getUf().getUf());
        }
    }
}
