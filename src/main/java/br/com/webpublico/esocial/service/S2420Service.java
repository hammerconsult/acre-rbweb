package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2420;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s2420Service")
public class S2420Service {
    protected static final Logger logger = LoggerFactory.getLogger(S2420Service.class);

    @Autowired
    private ESocialService eSocialService;

    public void enviarS2420(ConfiguracaoEmpregadorESocial config, VinculoFP vinculoFP, Date dataOperacao, Boolean isObito) {
        ValidacaoException val = new ValidacaoException();
        EventoS2420 s2420 = criarEventoS2420(vinculoFP, config, val, dataOperacao, isObito);
        logger.debug("Antes de Enviar: " + s2420.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2420(s2420);
    }

    private EventoS2420 criarEventoS2420(VinculoFP vinculoFP, ConfiguracaoEmpregadorESocial config, ValidacaoException val, Date dataOperacao, Boolean isObito) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2420 eventoS2420 = (EventosESocialDTO.S2420) eSocialService.getEventoS2420(empregador);
        eventoS2420.setIdentificadorWP(vinculoFP.getId().toString());
        eventoS2420.setClasseWP(ClasseWP.VINCULOFP);
        eventoS2420.setDescricao(vinculoFP.toString());

        eventoS2420.setIdESocial(vinculoFP.getId().toString());
        adicionarInformacoesIdeBeneficio(eventoS2420, vinculoFP, val);
        adicionarInformacoesTerminoBeneficio(eventoS2420, vinculoFP, val, dataOperacao, isObito);
        return eventoS2420;
    }

    private void adicionarInformacoesIdeBeneficio(EventoS2420 eventoS2420, VinculoFP vinculoFP, ValidacaoException ve) {
        eventoS2420.setCpfBenef(StringUtil.retornaApenasNumeros(vinculoFP.getMatriculaFP().getPessoa().getCpf()));
        eventoS2420.setNrBeneficio(vinculoFP.getMatriculaFP().getMatricula() + StringUtil.cortarOuCompletarEsquerda(vinculoFP.getNumero(), 2, "0"));
    }

    private void adicionarInformacoesTerminoBeneficio(EventoS2420 eventoS2420, VinculoFP vinculoFP, ValidacaoException ve, Date dataOperacao, Boolean isObito) {
        if (vinculoFP.getFinalVigencia().after(dataOperacao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de final de vigência deve ser anterior a data atual. Vínculo: " + vinculoFP);
        } else {
            eventoS2420.setDtTermBeneficio(vinculoFP.getFinalVigencia());
        }
        eventoS2420.setMtvTermino(isObito ? "01" : "05");
//        eventoS2420.setCnpjOrgaoSuc();
//        eventoS2420.setNovoCPF();
    }
}
