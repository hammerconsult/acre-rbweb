package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.CedenciaContratoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.TipoCedenciaContratoFP;
import br.com.webpublico.enums.rh.esocial.TipoCessao2231;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2231;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.esocial.RegistroESocialFacade;
import br.com.webpublico.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s2231Service")
public class S2231Service {
    protected static final Logger logger = LoggerFactory.getLogger(S2231Service.class);

    private RegistroESocialFacade registroESocialFacade;


    public RegistroESocialFacade getRegistroESocialFacade() {
        if (registroESocialFacade == null) {
            try {
                registroESocialFacade = (RegistroESocialFacade) new InitialContext().lookup("java:module/RegistroESocialFacade");
            } catch (NamingException e) {
                logger.error("Erro ao enviar S2231 para o e-social. {}", e.getMessage());
                logger.debug("Detalhes do erro ao enviar S2231 para o e-social. ", e);
            } catch (Exception ex) {
                logger.error("Erro ao enviar S2231 para o e-social. {}", ex.getMessage());
                logger.debug("Detalhes do erro ao enviar S2231 para o e-social. ", ex);
            }
        }
        return registroESocialFacade;
    }

    @Autowired
    private ESocialService eSocialService;

    public void enviarS2231(ConfiguracaoEmpregadorESocial config, CedenciaContratoFP cedencia, Date dataOperacao, TipoCessao2231 tipoCessao2231) {
        ValidacaoException val = new ValidacaoException();
        EventoS2231 s2231 = criarEventoS2231(cedencia, config, dataOperacao, val, tipoCessao2231);
        logger.debug("Antes de Enviar: " + s2231.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2231(s2231);
    }

    private EventoS2231 criarEventoS2231(CedenciaContratoFP cedencia, ConfiguracaoEmpregadorESocial config, Date dataOperacao, ValidacaoException val, TipoCessao2231 tipoCessao2231) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2231 eventoS2231 = (EventosESocialDTO.S2231) eSocialService.getEventoS2231(empregador);
        eventoS2231.setIdentificadorWP(cedencia.getId().toString());
        eventoS2231.setClasseWP(ClasseWP.CEDENCIACONTRATOFP);
        eventoS2231.setDescricao(cedencia.getContratoFP().toString());

        eventoS2231.setIdESocial(cedencia.getId().toString());
        adicionarInformacoesEmpregador(eventoS2231, cedencia);
        adicionarInformacoesCessao(eventoS2231, cedencia, dataOperacao, val, tipoCessao2231);
        return eventoS2231;
    }

    private void adicionarInformacoesEmpregador(EventoS2231 eventoS2231, CedenciaContratoFP cedencia) {
        eventoS2231.setCpfTrab(StringUtil.retornaApenasNumeros(cedencia.getContratoFP().getMatriculaFP().getPessoa().getCpf()));
        String matricula = getRegistroESocialFacade().getMatriculaS2200ProcessadoSucesso(cedencia.getContratoFP());
        if (matricula == null) {
            String vinculo = StringUtil.cortarOuCompletarEsquerda(cedencia.getContratoFP().getNumero(), 2, "0");
            eventoS2231.setMatricula(cedencia.getContratoFP().getMatriculaFP().getMatricula().concat(vinculo));
        } else {
            eventoS2231.setMatricula(matricula);
        }
    }

    private void adicionarInformacoesCessao(EventoS2231 eventoS2231, CedenciaContratoFP cedencia, Date dataOperacao, ValidacaoException ve, TipoCessao2231 tipoCessao2231) {
        if (TipoCessao2231.INI_CESSAO.equals(tipoCessao2231)) {
            if (cedencia.getDataCessao().after(dataOperacao)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de cessão da cedência \"" + cedencia + "\" deve ser anterior a data atual.");
            } else {
                eventoS2231.setDtIniCessao(cedencia.getDataCessao());
            }
            if (cedencia.getCessionario().getPessoaJuridica().getCnpj() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O cessionário da cedência \"" + cedencia + "\" deve conter CNPJ.");
            } else {
                eventoS2231.setCnpjCess(StringUtil.retornaApenasNumeros(cedencia.getCessionario().getPessoaJuridica().getCnpj()));
            }
            eventoS2231.setRespRemun(TipoCedenciaContratoFP.COM_ONUS.equals(cedencia.getTipoContratoCedenciaFP()));
        } else {
            if (cedencia.getDataRetorno() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de retorno da cedência \"" + cedencia + "\" deve ser informada.");
            } else if (cedencia.getDataRetorno().after(dataOperacao)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de retorno da cedência \"" + cedencia + "\" deve ser anterior a data atual.");
            } else {
                eventoS2231.setDtTermCessao(cedencia.getDataRetorno());
            }
        }
    }
}
