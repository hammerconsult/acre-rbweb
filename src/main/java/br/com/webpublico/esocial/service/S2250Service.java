package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.rh.cadastrofuncional.AvisoPrevio;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2250;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

/**
 * Created by William on 28/09/2018.
 */
@Service(value = "s2250Service")
public class S2250Service {
    protected static final Logger logger = LoggerFactory.getLogger(S2250Service.class);

    @Autowired
    private ESocialService eSocialService;

    public void enviarS2250(ConfiguracaoEmpregadorESocial config, AvisoPrevio avisoPrevio) {
        ValidacaoException val = new ValidacaoException();
        ContratoFP contratoFP = avisoPrevio.getContratoFP();
        EventoS2250 s2250 = criarEventoS2250(avisoPrevio, config, contratoFP, val);
        logger.debug("Antes de Enviar: " + s2250.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2250(s2250);
    }

    private EventoS2250 criarEventoS2250(AvisoPrevio avisoPrevio, ConfiguracaoEmpregadorESocial config, ContratoFP contratoFP, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2250 eventoS2250 = (EventosESocialDTO.S2250) eSocialService.getEventoS2250(empregador);
        eventoS2250.setIdESocial(contratoFP.getId().toString());
        eventoS2250.setIdentificadorWP(avisoPrevio.getId().toString());
        eventoS2250.setClasseWP(ClasseWP.AVISOPREVIO);
        eventoS2250.setDescricao(avisoPrevio.getContratoFP().toString());

        adicionarInformacoesTrabalhador(eventoS2250, contratoFP, config, val);
        adicionarInformacoesAvisoPrevio(eventoS2250, avisoPrevio, config, val);
        return eventoS2250;
    }

    private void adicionarInformacoesTrabalhador(EventosESocialDTO.S2250 eventoS2250, ContratoFP contratoFP, ConfiguracaoEmpregadorESocial config, ValidacaoException ve) {
        if (Strings.isNullOrEmpty(contratoFP.getMatriculaFP().getPessoa().getCpf())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + contratoFP.getMatriculaFP().getPessoa() + " não possui CPF cadastrado.");
        } else {
            eventoS2250.setCpfTrab(StringUtil.retornaApenasNumeros(contratoFP.getMatriculaFP().getPessoa().getCpf()));
        }
        if (contratoFP.getMatriculaFP().getPessoa().getCarteiraDeTrabalho() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + contratoFP.getMatriculaFP().getPessoa() + " não possui carteira de trabalho.");
        } else {
            eventoS2250.setNisTrab(contratoFP.getMatriculaFP().getPessoa().getCarteiraDeTrabalho().getPisPasep());
        }
        eventoS2250.setMatricula(contratoFP.getMatriculaFP().getMatricula());
    }

    private void adicionarInformacoesAvisoPrevio(EventosESocialDTO.S2250 eventoS2250, AvisoPrevio avisoPrevio, ConfiguracaoEmpregadorESocial config, ValidacaoException ve) {
        eventoS2250.setDtAvPrv(avisoPrevio.getDataAviso());
        eventoS2250.setDtPrevDeslig(avisoPrevio.getDataDesligamento());
        eventoS2250.setTpAvPrevio(avisoPrevio.getTipoAvisoPrevio().getCodigoESocial());
    }
}
