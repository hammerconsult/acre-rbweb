package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.CBO;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.esocial.comunicacao.eventos.iniciais.EventoS1035;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static br.com.webpublico.util.StringUtil.cortaDireita;
import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;


@Service(value = "s1035Service")
public class S1035Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1035Service.class);

    @Autowired
    private ESocialService eSocialService;


    @PostConstruct
    public void init() {
    }


    public void enviarS1035(ConfiguracaoEmpregadorESocial config, Cargo cargo) {
        ValidacaoException val = new ValidacaoException();
        EventoS1035 s1035 = criarEventoS1035(config, cargo, val);
        logger.debug("Antes de Enviar: " + s1035.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1035(s1035);
    }

    public EventoS1035 criarEventoS1035(ConfiguracaoEmpregadorESocial config, Cargo cargo, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1035 eventoS1035 = (EventosESocialDTO.S1035) eSocialService.getEventoS1035(empregador);
        validarTipoCargo(cargo, val);
        adicionarInformacoesBasicas(eventoS1035, config, cargo, val);
        adicionarDadosCarreira(eventoS1035, cargo, val);
        adicionarLeiCarreira(eventoS1035, cargo, val);


        return eventoS1035;
    }

    private void validarTipoCargo(Cargo cargo, ValidacaoException val) {
        if (cargo.getTipoPCS() != null && TipoPCS.CARGO_EM_COMISSAO.equals(cargo.getTipoPCS())) {
            val.adicionarMensagemDeOperacaoNaoPermitida("Tipo de cargo não aceito para envio: " + cargo + " Tipo: " + cargo.getTipoPCS());
        }
        if (cargo.getTipoPCS() != null && TipoPCS.FUNCAO_GRATIFICADA.equals(cargo.getTipoPCS())) {
            val.adicionarMensagemDeOperacaoNaoPermitida("Tipo de cargo não aceito para envio: " + cargo + " Tipo: " + cargo.getTipoPCS());
        }
    }

    private void adicionarLeiCarreira(EventosESocialDTO.S1035 eventoS1035, Cargo cargo, ValidacaoException val) {
        if (cargo.getAtoLegal() != null) {
            AtoLegal ato = cargo.getAtoLegal();
            if (ato.getNumero() != null) {
                if (ato.getNumero().trim().length() < 3) {
                    eventoS1035.setLeiCarr(StringUtil.cortarOuCompletarEsquerda("0", 3, ato.getNumero()));
                } else {
                    eventoS1035.setLeiCarr(ato.getNumero());
                }
            } else {
                val.adicionarMensagemDeCampoObrigatorio("O Número do Ato Legal é obrigatório para o Cargo: " + cargo);
            }
            if (ato.getDataPublicacao() != null) {
                eventoS1035.setDtLeiCarr(ato.getDataPublicacao());
            } else {
                val.adicionarMensagemDeCampoObrigatorio("A data de publicação do Ato Legal é obrigatório para o Cargo: " + cargo);
            }
        } else {
            val.adicionarMensagemDeCampoObrigatorio("Não foi possível encontrar o Ato Legal do Cargo: " + cargo);
        }

    }

    private void adicionarDadosCarreira(EventosESocialDTO.S1035 eventoS1035, Cargo cargo, ValidacaoException val) {
        if (Strings.isNullOrEmpty(cargo.getDescricaoCarreira())) {
            val.adicionarMensagemDeCampoObrigatorio("Não foi possível encontrar a descrição da carreira do cargo: " + cargo);
        } else {
            eventoS1035.setDscCarreira(cortaDireita(30, cargo.getDescricaoCarreira()));
        }
        if (cargo.getSituacaoCarreira() == null) {
            val.adicionarMensagemDeCampoObrigatorio("Não foi possível encontrar a situação da carreira do cargo: " + cargo);

        } else {
            eventoS1035.setSitCarr(cargo.getSituacaoCarreira().getCodigo());
        }
    }

    private String getCodigoCBO(Cargo cargo) {
        for (CBO cbo : cargo.getCbos()) {
            return cbo.getCodigo().toString();
        }
        return "";
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S1035 eventoS1035, ConfiguracaoEmpregadorESocial config, Cargo cargo, ValidacaoException val) {
        eventoS1035.setIdESocial(cargo.getId().toString());
        if (config.getInicioVigencia() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência é obrigatório");
        }
        eventoS1035.setCodCarreira(cargo.getCodigoCarreira());
        eventoS1035.setIniValid(config.getInicioVigencia());
        eventoS1035.setFimValid(config.getFinalVigencia());
    }


}
