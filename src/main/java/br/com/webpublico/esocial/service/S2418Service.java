package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.rh.esocial.ReativacaoBeneficio;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2418;
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

@Service(value = "s2418Service")
public class S2418Service {
    protected static final Logger logger = LoggerFactory.getLogger(S2418Service.class);

    @Autowired
    private ESocialService eSocialService;

    public void enviarS2418(ConfiguracaoEmpregadorESocial config, ReativacaoBeneficio reativacao, Date dataOperacao) {
        ValidacaoException val = new ValidacaoException();
        EventoS2418 s2418 = criarEventoS2418(reativacao, config, val, dataOperacao);
        logger.debug("Antes de Enviar: " + s2418.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2418(s2418);
    }

    private EventoS2418 criarEventoS2418(ReativacaoBeneficio reativacao, ConfiguracaoEmpregadorESocial config, ValidacaoException val, Date dataOperacao) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2418 eventoS2418 = (EventosESocialDTO.S2418) eSocialService.getEventoS2418(empregador);
        eventoS2418.setIdentificadorWP(reativacao.getId().toString());
        eventoS2418.setClasseWP(ClasseWP.REATIVACAOBENEFICIO);
        eventoS2418.setDescricao(reativacao.getVinculoFP().toString());

        eventoS2418.setIdESocial(reativacao.getId().toString());
        adicionarInformacoesBeneficio(eventoS2418, reativacao, val);
        adicionarInformacoesReativacao(eventoS2418, reativacao, val, dataOperacao);
        return eventoS2418;
    }

    private void adicionarInformacoesBeneficio(EventoS2418 eventoS2418, ReativacaoBeneficio reativacao, ValidacaoException ve) {
        eventoS2418.setCpfBenef(StringUtil.retornaApenasNumeros(reativacao.getVinculoFP().getMatriculaFP().getPessoa().getCpf()));
        if (reativacao.getNrBeneficio() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Deve ser informado número do benefício do vínculo: " + reativacao.getVinculoFP());
        } else {
            eventoS2418.setNrBeneficio(reativacao.getNrBeneficio());
        }
    }

    private void adicionarInformacoesReativacao(EventoS2418 eventoS2418, ReativacaoBeneficio reativacao, ValidacaoException ve, Date dataOperacao) {
        if (reativacao.getDtEfetReativ() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Deve ser informada a data da efetiva reativação do benefício do vínculo: " + reativacao.getVinculoFP());
        } else if (reativacao.getDtEfetReativ().before(reativacao.getDtCessaoBeneficio())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data da efetiva reativação do benefício deve ser posterior à data de cessação do benefício do vínculo: " + reativacao.getVinculoFP());
        } else if (reativacao.getDtEfetReativ().after(dataOperacao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data da efetiva reativação do benefício deve ser igual ou anterior à data atual. Vínculo: " + reativacao.getVinculoFP());
        } else {
            eventoS2418.setDtEfetReativ(reativacao.getDtEfetReativ());
        }

        if (reativacao.getDtEfeito() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Deve ser informada a data de início dos efeitos financeiros da reativação do benefício do vínculo: " + reativacao.getVinculoFP());
        } else if (reativacao.getDtEfeito().after(reativacao.getDtEfetReativ())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de início dos efeitos financeiros da reativação do benefício, deve ser data igual ou anterior à data da efetiva reativação do benefício do vínculo: " + reativacao.getVinculoFP());
        } else if (reativacao.getDtEfeito().before(reativacao.getDtCessaoBeneficio())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de início dos efeitos financeiros da reativação do benefício deve ser posterior à data de cessação do benefício do vínculo: " + reativacao.getVinculoFP());
        } else {
            eventoS2418.setDtEfeito(reativacao.getDtEfeito());
        }
    }
}
