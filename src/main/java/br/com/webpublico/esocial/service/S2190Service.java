package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.PrestadorServicos;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2190;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

/**
 * Created by William on 02/08/2018.
 */
@Service(value = "s2190Service")
public class S2190Service {

    protected static final Logger logger = LoggerFactory.getLogger(S2190Service.class);


    @Autowired
    private ESocialService eSocialService;


    public void enviarS2190(ConfiguracaoEmpregadorESocial config, ContratoFP contratoFP, PrestadorServicos prestadorServicos) {
        ValidacaoException val = new ValidacaoException();
        EventoS2190 s2190 = criarEventoS2190(config, contratoFP, val, prestadorServicos);
        logger.debug("Antes de Enviar: " + s2190.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2190(s2190);
    }

    private EventoS2190 criarEventoS2190(ConfiguracaoEmpregadorESocial config, ContratoFP contratoFP, ValidacaoException val, PrestadorServicos prestadorServicos) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2190 eventoS2190 = (EventosESocialDTO.S2190) eSocialService.getEventoS2190(empregador);
        eventoS2190.setIdentificadorWP(contratoFP.getId().toString());
        eventoS2190.setClasseWP(ClasseWP.VINCULOFP);
        eventoS2190.setDescricao(contratoFP.toString());

        adicionarInformacoesBasicas(eventoS2190, contratoFP, val, prestadorServicos);
        adicionarInformacoesContratoTrabalhador(eventoS2190, contratoFP, val);
        return eventoS2190;
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S2190 eventoS2190, ContratoFP contratoFP, ValidacaoException val, PrestadorServicos prestadorServicos) {
        eventoS2190.setIdESocial(contratoFP.getId().toString());
        eventoS2190.setCpfTrab(Util.removerCaracteresEspeciais(contratoFP.getMatriculaFP().getPessoa().getCpf()));
        eventoS2190.setDtAdm(contratoFP.getDataAdmissao());
        eventoS2190.setDtNascto(contratoFP.getDataNascimento());
        String vinculo = StringUtil.cortarOuCompletarEsquerda(contratoFP.getNumero(), 2, "0");
        eventoS2190.setMatricula(contratoFP.getMatriculaFP().getMatricula().concat(vinculo));
    }

    private void adicionarInformacoesContratoTrabalhador(EventosESocialDTO.S2190 eventoS2190, ContratoFP
        contratoFP, ValidacaoException ve) {
        if (contratoFP.getCategoriaTrabalhador() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi informado a categoria do trabalhador " + contratoFP + " em seu cadastro na aba 'e-social'");
        } else {
            eventoS2190.setCodCateg(contratoFP.getCategoriaTrabalhador().getCodigo());
        }
    }
}
