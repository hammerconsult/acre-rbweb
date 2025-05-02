package br.com.webpublico.esocial.service;


import br.com.webpublico.entidades.rh.esocial.IndicativoContribuicao;
import br.com.webpublico.esocial.comunicacao.eventos.periodicos.EventoS1280;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1280Service")
public class S1280Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1280Service.class);


    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    @PostConstruct
    public void init() {
        try {
            configuracaoEmpregadorESocialFacade = (ConfiguracaoEmpregadorESocialFacade) new InitialContext().lookup("java:module/ConfiguracaoEmpregadorESocialFacade");
        } catch (NamingException e) {
            logger.error("Não foi possivel criar a instancia: " + e.getMessage());
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }


    @Autowired
    private ESocialService eSocialService;

    public void enviarS1280(IndicativoContribuicao indicativoContribuicao) {
        ValidacaoException val = new ValidacaoException();
        EventoS1280 s1280 = criarEventoS1280(indicativoContribuicao);
        logger.debug("Antes de Enviar: " + s1280.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1280(s1280);
    }

    private EventoS1280 criarEventoS1280(IndicativoContribuicao indicativoContribuicao) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(indicativoContribuicao.getEmpregador().getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1280 eventoS1280 = (EventosESocialDTO.S1280) eSocialService.getEventoS1280(empregador);

        String mes = indicativoContribuicao.getMes().getNumeroMes().toString();
        String ano = indicativoContribuicao.getExercicio().getAno().toString();

        eventoS1280.setClasseWP(ClasseWP.INDICATIVOCONTRIBUICAO);
        eventoS1280.setIdentificadorWP(indicativoContribuicao.getId().toString());

        eventoS1280.setIdESocial(indicativoContribuicao.getEmpregador().getId().toString().concat(ano.concat(mes)));

        eventoS1280.setIndApuracao(indicativoContribuicao.getDecimoTerceiro() ? 2 : 1);
        if (indicativoContribuicao.getDecimoTerceiro()) {
            eventoS1280.setPerApur(indicativoContribuicao.getExercicio().getAno());
        } else {
            eventoS1280.setPerApur(indicativoContribuicao.getExercicio().getAno(), indicativoContribuicao.getMes().getNumeroMes());
        }
        adicionarInformacoesBasicas(eventoS1280, indicativoContribuicao);
        return eventoS1280;
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S1280 eventoS1280,
                                             IndicativoContribuicao indicativoContribuicao) {
        eventoS1280.setIndSubstPatr(indicativoContribuicao.getIndicativoSubstituicao().getCodigo());
        eventoS1280.setPercRedContrib(indicativoContribuicao.getPercentualContribuicao());
        if (indicativoContribuicao.getDecimoTerceiro()) {
            eventoS1280.setFator13(indicativoContribuicao.getFatorMes());
        } else {
            eventoS1280.setFatorMes(indicativoContribuicao.getFatorMes());
        }
    }
}
