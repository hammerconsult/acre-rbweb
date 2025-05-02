package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.Estagiario;
import br.com.webpublico.entidades.ExoneracaoRescisao;
import br.com.webpublico.entidades.PrestadorServicos;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2399;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;


/**
 * Created by William on 16/01/2019.
 */
@Service(value = "s2399Service")
public class S2399Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1000Service.class);

    @Autowired
    private ESocialService eSocialService;

    public void enviarS2399(ConfiguracaoEmpregadorESocial config, ExoneracaoRescisao exoneracaoRescisao, PrestadorServicos prestadorServicos) {
        ValidacaoException val = new ValidacaoException();
        EventoS2399 s2399 = criarEventoS2399(config, val, exoneracaoRescisao, prestadorServicos);
        logger.debug("Antes de Enviar: " + s2399.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2399(s2399);
    }

    private EventoS2399 criarEventoS2399(ConfiguracaoEmpregadorESocial config, ValidacaoException val, ExoneracaoRescisao exoneracaoRescisao, PrestadorServicos prestadorServicos) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2399 eventoS2399 = (EventosESocialDTO.S2399) eSocialService.getEventoS2399(empregador);
        eventoS2399.setIdentificadorWP(exoneracaoRescisao.getId().toString());
        eventoS2399.setClasseWP(ClasseWP.EXONERACAORESCISAO);
        eventoS2399.setDescricao(exoneracaoRescisao.getVinculoFP().toString());

        adicionarInformacoesBasicas(eventoS2399, exoneracaoRescisao.getVinculoFP(), prestadorServicos, val);

        return eventoS2399;
    }

    private void adicionarMatricula(EventosESocialDTO.S2399 eventoS2399, PrestadorServicos prestadorServicos) {
        if (prestadorServicos != null) {
            eventoS2399.setMatricula(prestadorServicos.getMatriculaESocial());
        }
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S2399 eventoS2399, VinculoFP vinculoFP, PrestadorServicos prestadorServicos, ValidacaoException ve) {
        eventoS2399.setCpfTrab(StringUtil.retornaApenasNumeros(vinculoFP.getMatriculaFP().getPessoa().getCpf()));
        if (vinculoFP.getMatriculaFP().getPessoa().getCarteiraDeTrabalho() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + vinculoFP.getMatriculaFP().getPessoa() + " não possui carteira de trabalho.");
        } else {
            eventoS2399.setNisTrab(vinculoFP.getMatriculaFP().getPessoa().getCarteiraDeTrabalho().getPisPasep());
        }
        if (vinculoFP.getCategoriaTrabalhador() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi informado a categoria do trabalhador " + vinculoFP + " em seu cadastro na aba 'e-social'");
        } else {
            eventoS2399.setCodCateg(vinculoFP.getCategoriaTrabalhador().getCodigo());
        }
        if (vinculoFP instanceof Estagiario) {
            eventoS2399.setDtTerm(((Estagiario) vinculoFP).getDataPrevistaTermino());
        } else {
            eventoS2399.setDtTerm(vinculoFP.getFinalVigencia());
        }
        adicionarMatricula(eventoS2399, prestadorServicos);
    }
}
