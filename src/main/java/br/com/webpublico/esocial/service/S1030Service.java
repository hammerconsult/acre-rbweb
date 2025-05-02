package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.CBO;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.TipoCargoAcumulavel;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.enums.rh.esocial.TipoContagemEspecialEsocial;
import br.com.webpublico.esocial.comunicacao.eventos.iniciais.EventoS1030;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.FacesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.webpublico.util.StringUtil.*;

@Service(value = "s1030Service")
public class S1030Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1030Service.class);

    @Autowired
    private ESocialService eSocialService;


    public void enviarS1030(ConfiguracaoEmpregadorESocial config, Cargo cargo) {
        ValidacaoException val = new ValidacaoException();
        EventoS1030 s1030 = criarEventoS1030(config, cargo, val);
        logger.debug("Antes de Enviar: " + s1030.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1030(s1030);
    }

    public EventoS1030 criarEventoS1030(ConfiguracaoEmpregadorESocial config, Cargo cargo, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1030 eventoS1030 = (EventosESocialDTO.S1030) eSocialService.getEventoS1030(empregador);
        validarTipoCargo(cargo, val);
        adicionarInformacoesBasicas(eventoS1030, config, cargo, val);
        adicionarDadosCargo(eventoS1030, config, cargo, val);
        adicionarLeiCargo(eventoS1030, config, cargo, val);


        return eventoS1030;
    }

    private void validarTipoCargo(Cargo cargo, ValidacaoException val) {
        if (cargo.getTipoPCS() != null && TipoPCS.FUNCAO_GRATIFICADA.equals(cargo.getTipoPCS())) {
            val.adicionarMensagemDeOperacaoNaoPermitida("Tipo de cargo não aceito para envio: " + cargo + " Tipo: " + cargo.getTipoPCS());
        }
    }

    private void adicionarLeiCargo(EventosESocialDTO.S1030 eventoS1030, ConfiguracaoEmpregadorESocial config, Cargo cargo, ValidacaoException val) {
        if (cargo.getAtoLegal() != null) {
            AtoLegal ato = cargo.getAtoLegal();
            if (ato.getNumero() != null) {
                eventoS1030.setNrLei(ato.getNumero());
            } else {
                val.adicionarMensagemDeCampoObrigatorio("O Número do Ato Legal é obrigatório para o Cargo: " + cargo);
            }
            if (ato.getDataPublicacao() != null) {
                eventoS1030.setDtLei(ato.getDataPublicacao());
            } else {
                val.adicionarMensagemDeCampoObrigatorio("A data de publicação do Ato Legal é obrigatório para o Cargo: " + cargo);
            }
            if (ato.getSituacaoAtoLegal() != null) {
                eventoS1030.setSitCargo(ato.getSituacaoAtoLegal().getCodigo());
            } else {
                String url = FacesUtil.getRequestContextPath() + "/atolegal/editar/";

                val.adicionarMensagemDeCampoObrigatorio("A Situação do Ato Legal <b>" + ato + "</n> é obrigatório para o Cargo:<b> " + cargo +
                    "</b>. <a href='" + FacesUtil.getRequestContextPath() + url + ato.getId() + "' target='_blank'>Clique aqui para editar</a>");;
            }
        } else {
            val.adicionarMensagemDeCampoObrigatorio("Não foi possível encontrar o Ato Legal do Cargo: " + cargo);
        }

    }

    private void adicionarDadosCargo(EventosESocialDTO.S1030 eventoS1030, ConfiguracaoEmpregadorESocial config, Cargo cargo, ValidacaoException val) {
        eventoS1030.setNmCargo(cortaDireita(100, cargo.getDescricao()));
        String codigoCBO = convertVazioEmNull(getCodigoCBO(cargo));
        if (codigoCBO != null) {
            eventoS1030.setCodCBO(codigoCBO);
        } else {
            val.adicionarMensagemDeCampoObrigatorio("Não foi possível encontrar o CBO do cargo: " + cargo);
        }
        if (cargo.getTipoCargoAcumulavelSIPREV() != null) {
            eventoS1030.setAcumCargo(cargo.getTipoCargoAcumulavelSIPREV().getCodigo());
        } else {
            eventoS1030.setAcumCargo(TipoCargoAcumulavel.NAO_ACUMULAVEL.getCodigo());
        }
        if (cargo.getTipoContagemEspecialEsocial() != null) {
            eventoS1030.setContagemEsp(cargo.getTipoContagemEspecialEsocial().getCodigo());
        } else {
            eventoS1030.setContagemEsp(TipoContagemEspecialEsocial.NAO.getCodigo());
        }
        eventoS1030.setDedicExcl(cargo.getDedicacaoExclusivaSIPREV() != null ? cargo.getDedicacaoExclusivaSIPREV() : false);
    }

    private String getCodigoCBO(Cargo cargo) {
        for (CBO cbo : cargo.getCbos()) {
            return cbo.getCodigo().toString();
        }
        return "";
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S1030 eventoS1030, ConfiguracaoEmpregadorESocial config, Cargo cargo, ValidacaoException val) {
        eventoS1030.setIdESocial(cargo.getId().toString());
        if (cargo.getInicioVigencia() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência é obrigatório");
        }
        eventoS1030.setCodCargo(cargo.getCodigoDoCargo());
        eventoS1030.setIniValid(config.getInicioVigencia());//TODO Checar
        eventoS1030.setFimValid(cargo.getFinalVigencia());
    }


}
