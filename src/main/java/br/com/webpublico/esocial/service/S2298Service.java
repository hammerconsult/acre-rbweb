package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.Reintegracao;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.rh.TipoReintegracao;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2298;
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
 * Created by William on 06/11/2018.
 */
@Service(value = "s2298Service")
public class S2298Service {
    protected static final Logger logger = LoggerFactory.getLogger(S2298Service.class);

    @Autowired
    private ESocialService eSocialService;

    public void enviarS2298(ConfiguracaoEmpregadorESocial config, Reintegracao reintegracao) {
        ValidacaoException val = new ValidacaoException();
        EventoS2298 s2298 = criarEventoS2298(reintegracao, config, val);
        logger.debug("Antes de Enviar: " + s2298.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2298(s2298);
    }

    private EventoS2298 criarEventoS2298(Reintegracao reintegracao, ConfiguracaoEmpregadorESocial config, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2298 eventoS2298 = (EventosESocialDTO.S2298) eSocialService.getEventoS2298(empregador);
        eventoS2298.setIdESocial(reintegracao.getId().toString());
        eventoS2298.setIdentificadorWP(reintegracao.getId().toString());
        eventoS2298.setClasseWP(ClasseWP.REINTEGRACAO);
        eventoS2298.setDescricao(reintegracao.getContratoFP().toString());


        adicionarInformacoesBasicas(eventoS2298, reintegracao, val);
        adicionarInformacoesTipoReintegracao(eventoS2298, reintegracao, val);
        return eventoS2298;
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S2298 eventoS2298, Reintegracao reintegracao, ValidacaoException val) {
        eventoS2298.setCpfTrab(StringUtil.retornaApenasNumeros(reintegracao.getContratoFP().getMatriculaFP().getPessoa().getCpf()));
        if (reintegracao.getContratoFP().getMatriculaFP().getPessoa().getCarteiraDeTrabalho() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + reintegracao.getContratoFP().getMatriculaFP().getPessoa() + " não possui carteira de trabalho.");
        } else {
            eventoS2298.setNisTrab(reintegracao.getContratoFP().getMatriculaFP().getPessoa().getCarteiraDeTrabalho().getPisPasep());
        }
        eventoS2298.setMatricula(reintegracao.getContratoFP().getMatriculaFP().getMatricula());
    }

    private void adicionarInformacoesTipoReintegracao(EventosESocialDTO.S2298 eventoS2298, Reintegracao reintegracao, ValidacaoException val) {
        if (reintegracao.getTipoReintegracao() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A reintegração deve ter um Tipo de Reitegração.");
        } else {
            eventoS2298.setTpReint(reintegracao.getTipoReintegracao().getCodigo());
        }
        if (reintegracao.getTipoReintegracao() != null && TipoReintegracao.REINTEGRACAO_DECISAO_JUDICIAL.equals(reintegracao.getTipoReintegracao())) {
            if (Strings.isNullOrEmpty(reintegracao.getNumeroProcessoJudicial())) {
                val.adicionarMensagemDeOperacaoNaoRealizada("Reintegração do Tipo de Reitegração <b> " + reintegracao.getTipoReintegracao().getDescricao() + " </b> " +
                    "deve ser informado o Número do Processo Judicial.");
            } else {
                eventoS2298.setNrProcJud(reintegracao.getNumeroProcessoJudicial());
            }
        }
        if (reintegracao.getTipoReintegracao() != null && TipoReintegracao.REINTEGRACAO_ANISTIA_LEGAL.equals(reintegracao.getTipoReintegracao())) {
            if (Strings.isNullOrEmpty(reintegracao.getNumeroLeiAnistia())) {
                val.adicionarMensagemDeOperacaoNaoRealizada("Reintegração do Tipo de Reitegração <b> " + reintegracao.getTipoReintegracao().getDescricao() + " </b> " +
                    "deve ser informado o Número da Lei de Anistia.");
            } else {
                eventoS2298.setNrLeiAnistia(reintegracao.getNumeroLeiAnistia());
            }
        }
        eventoS2298.setDtEfetRetorno(reintegracao.getDataReintegracao());
        eventoS2298.setIndPagtoJuizo(reintegracao.getRemuneracaoPagoEmJuizo());
        if (reintegracao.getInicioEfeitosFinanceiros() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A reintegração não possui Data de Início dos Efeitos Financeiros.");
        } else {
            eventoS2298.setDtEfeito(reintegracao.getInicioEfeitosFinanceiros());
        }
    }
}
