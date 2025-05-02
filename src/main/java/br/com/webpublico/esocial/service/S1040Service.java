package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.CBO;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.eventos.iniciais.EventoS1040;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.webpublico.util.StringUtil.*;

@Service(value = "s1040Service")
public class S1040Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1040Service.class);

    @Autowired
    private ESocialService eSocialService;


    public void enviarS1040(ConfiguracaoEmpregadorESocial config, Cargo cargo) {
        ValidacaoException val = new ValidacaoException();
        EventoS1040 s1040 = criarEventoS1040(config, cargo, val);
        logger.debug("Antes de Enviar: " + s1040.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1040(s1040);
    }

    public EventoS1040 criarEventoS1040(ConfiguracaoEmpregadorESocial config, Cargo cargo, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1040 eventoS1040 = (EventosESocialDTO.S1040) eSocialService.getEventoS1040(empregador);

        adicionarInformacoesBasicas(eventoS1040, config, cargo, val);
        adicionarDadosCargo(eventoS1040, config, cargo, val);


        return eventoS1040;
    }


    private void adicionarDadosCargo(EventosESocialDTO.S1040 eventoS1040, ConfiguracaoEmpregadorESocial config, Cargo cargo, ValidacaoException val) {
        eventoS1040.setDscFuncao(cortaDireita(100, cargo.getDescricao()));
        String codigoCBO = convertVazioEmNull(getCodigoCBO(cargo));
        if (codigoCBO != null) {
            eventoS1040.setCodCBO(codigoCBO);
        } else {
            val.adicionarMensagemDeCampoObrigatorio("Não foi possível encontrar o CBO do cargo: " + cargo);
        }
    }

    private String getCodigoCBO(Cargo cargo) {
        for (CBO cbo : cargo.getCbos()) {
            return cbo.getCodigo().toString();
        }
        return "";
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S1040 eventoS1040, ConfiguracaoEmpregadorESocial config, Cargo cargo, ValidacaoException val) {
        eventoS1040.setIdESocial(cargo.getId().toString());
        if (cargo.getInicioVigencia() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência é obrigatório");
        }
        eventoS1040.setCodFuncao(cargo.getCodigoDoCargo());
        eventoS1040.setIniValid(config.getInicioVigencia());
        eventoS1040.setFimValid(cargo.getFinalVigencia());
    }


}
