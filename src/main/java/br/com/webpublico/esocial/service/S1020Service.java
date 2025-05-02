package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.rh.esocial.TipoLotacaoTributariaESocial;
import br.com.webpublico.esocial.comunicacao.eventos.iniciais.EventoS1020;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1020Service")
public class S1020Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1020Service.class);

    @Autowired
    private ESocialService eSocialService;


    public void enviarS1020(ConfiguracaoEmpregadorESocial config, Entidade entidade) {
        ValidacaoException val = new ValidacaoException();
        EventoS1020 s1020 = criarEventoS1020(config, entidade, val);
        logger.debug("Antes de Enviar: " + s1020.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1020(s1020);
    }

    public EventoS1020 criarEventoS1020(ConfiguracaoEmpregadorESocial config, Entidade entidade, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1020 eventoS1020 = (EventosESocialDTO.S1020) eSocialService.getEventoS1020(empregador);
        eventoS1020.setIdentificadorWP(entidade.getId().toString());
        eventoS1020.setClasseWP(ClasseWP.ENTIDADE);
        eventoS1020.setDescricao(entidade.toString());

        adicionarInformacoesBasicas(eventoS1020, config, entidade, val);
        adicionarDadosLotacao(eventoS1020, config, entidade, val);

        return eventoS1020;
    }

    private void adicionarDadosLotacao(EventosESocialDTO.S1020 eventoS1020, ConfiguracaoEmpregadorESocial config, Entidade entidade, ValidacaoException val) {
        if (config.getTipoLotacaoTributaria() != null) {
            eventoS1020.setTpLotacao(config.getTipoLotacaoTributaria().getCodigo());
            if (!TipoLotacaoTributariaESocial.preencherTpIns(config.getTipoLotacaoTributaria())) {
                eventoS1020.setTpInscLotacao(1);
                eventoS1020.setNrInscLotacao(retornaApenasNumeros(entidade.getPessoaJuridica().getCnpj()));
            }

        } else {
            val.adicionarMensagemDeCampoObrigatorio("Selecione a Lotação Tribtutária no cadastro de Empregador");
        }




        if (entidade.getCodigoFpas() != null) {
            eventoS1020.setFpasLotacao(entidade.getCodigoFpas());
        } else {
            val.adicionarMensagemDeCampoObrigatorio("O Código FPAS é obrigatório, preencher código na tela de entidade.");
        }

        if (entidade.getCodigoOutrasEntidades() != null) {
            eventoS1020.setCodTercs(entidade.getCodigoOutrasEntidades());
        } else {
            val.adicionarMensagemDeCampoObrigatorio("O Código de Outras Entidades é obrigatório, preencher código na tela de entidade. ex: 0079");
        }

        eventoS1020.setCodTercsSusp(entidade.getCodigoOutrasEntidadesSuspenso());//TODO implementar processo judicial(aba em entidade)
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S1020 eventoS1020, ConfiguracaoEmpregadorESocial config, Entidade entidade, ValidacaoException val) {
        eventoS1020.setIdESocial(entidade.getId().toString());
        if (config.getInicioVigencia() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência é obrigatório");
        }
        eventoS1020.setCodLotacao(entidade.getId().toString());
        eventoS1020.setIniValid(config.getInicioVigencia());//TODO Checar
        eventoS1020.setFimValid(config.getFinalVigencia());
    }


}
