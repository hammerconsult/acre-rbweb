package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.Afastamento;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.PrestadorServicos;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.rh.esocial.TipoAfastamentoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2230;
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
 * Created by William on 04/10/2018.
 */
@Service(value = "s2230Service")
public class S2230Service {
    protected static final Logger logger = LoggerFactory.getLogger(S2230Service.class);

    @Autowired
    private ESocialService eSocialService;

    public void enviarS2230(ConfiguracaoEmpregadorESocial config, Afastamento afastamento, PrestadorServicos prestadorServicos) {
        ValidacaoException val = new ValidacaoException();
        EventoS2230 s2230 = criarEventoS2230(afastamento, config, prestadorServicos, val);
        logger.debug("Antes de Enviar: " + s2230.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2230(s2230);
    }

    private EventoS2230 criarEventoS2230(Afastamento afastamento, ConfiguracaoEmpregadorESocial config, PrestadorServicos prestadorServicos, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2230 eventoS2230 = (EventosESocialDTO.S2230) eSocialService.getEventoS2230(empregador);
        eventoS2230.setIdentificadorWP(afastamento.getId().toString());
        eventoS2230.setClasseWP(ClasseWP.AFASTAMENTO);
        eventoS2230.setDescricao(afastamento.getContratoFP().toString());

        eventoS2230.setIdESocial(afastamento.getId().toString());
        adicionarInformacoesBasicas(eventoS2230, afastamento, prestadorServicos);
        adicionarInformacoesEmpregador(eventoS2230, afastamento, val);
        adicionarInformacoesAdastamento(eventoS2230, afastamento, val);
        return eventoS2230;
    }

    private void adicionarMatricula(EventoS2230 eventoS2230, Afastamento afastamento, String vinculo, PrestadorServicos prestadorServicos) {
        if(ModalidadeContratoFP.PRESTADOR_SERVICO.equals(afastamento.getContratoFP().getModalidadeContratoFP().getCodigo())) {
            if (prestadorServicos != null) {
                eventoS2230.setMatricula(prestadorServicos.getMatriculaESocial());
            }
        } else {
            eventoS2230.setMatricula(afastamento.getContratoFP().getMatriculaFP().getMatricula().concat(vinculo));
        }
    }

    private void adicionarInformacoesBasicas(EventoS2230 eventoS2230, Afastamento afastamento, PrestadorServicos prestadorServicos) {
        String vinculo = StringUtil.cortarOuCompletarEsquerda(afastamento.getContratoFP().getNumero(), 2, "0");
        adicionarMatricula(eventoS2230, afastamento, vinculo, prestadorServicos);
    }

    private void adicionarInformacoesEmpregador(EventoS2230 eventoS2230, Afastamento afastamento, ValidacaoException ve) {
        eventoS2230.setCpfTrab(StringUtil.retornaApenasNumeros(afastamento.getContratoFP().getMatriculaFP().getPessoa().getCpf()));
        if (afastamento.getContratoFP().getMatriculaFP().getPessoa().getCarteiraDeTrabalho() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + afastamento.getContratoFP().getMatriculaFP().getPessoa() + " não possui carteira de trabalho.");
        } else {
            eventoS2230.setNisTrab(afastamento.getContratoFP().getMatriculaFP().getPessoa().getCarteiraDeTrabalho().getPisPasep());
        }
    }

    private void adicionarInformacoesAdastamento(EventoS2230 eventoS2230, Afastamento afastamento, ValidacaoException ve) {
        eventoS2230.setDtIniAfast(afastamento.getInicio());
        if (afastamento.getTipoAfastamento() != null && afastamento.getTipoAfastamento().getTipoAfastamentoESocial() == null) { //validação para funcionar férias e afastamento
            ve.adicionarMensagemDeOperacaoNaoPermitida("Deve ser informado no Tipo de Afastamento o Tipo de Afastamento do E-social");
        } else {
            eventoS2230.setCodMotAfast(afastamento.getTipoAfastamentoESocial() != null ? afastamento.getTipoAfastamentoESocial().getCodigo() :
                afastamento.getTipoAfastamento().getTipoAfastamentoESocial().getCodigo());
        }
        if (afastamento.getTipoAfastamento() != null) {
            if (TipoAfastamentoESocial.ACIDENTE_DOENCA_TRABALHO.equals(afastamento.getTipoAfastamento().getTipoAfastamentoESocial()) ||
                TipoAfastamentoESocial.ACIDENTE_DOENCA_NAO_RELACIONADA_TRABALHO.equals(afastamento.getTipoAfastamento().getTipoAfastamentoESocial())) {
                if (afastamento.getTipoAfastamento().getTipoAcidenteTransito() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Para o Tipo Afastamento E-Social <b>" + afastamento.getTipoAfastamento().getTipoAfastamentoESocial().getDescricao() +
                        "</b> é necessário informar o Tipo Acidente de Trânsito");
                } else {
                    eventoS2230.setTpAcidTransito(afastamento.getTipoAfastamento().getTipoAcidenteTransito().getCodigo());
                }
            }
        }

        if (afastamento.getTipoAfastamento() != null && TipoAfastamentoESocial.ACIDENTE_DOENCA_TRABALHO.equals(afastamento.getTipoAfastamento().getTipoAfastamentoESocial())) {
            EventoS2230.InfoAtestado infoAtestado = eventoS2230.addInfoAtestado();
            infoAtestado.setCodCID(afastamento.getCid().getCodigoDaCid());
            infoAtestado.setQtdDiasAfast(afastamento.getTotalDias());
            infoAtestado.setNmEmit(afastamento.getMedico().getMedico().getNome());
            infoAtestado.setIdeOC(1); // Conselho Regional de Medicina (CRM)
            infoAtestado.setNrOc(afastamento.getMedico().getRegistroCRM());
        }
    }
}
