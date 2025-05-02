package br.com.webpublico.esocial.service;


import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocialS1299;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.rh.esocial.TipoApuracaoFolha;
import br.com.webpublico.esocial.comunicacao.eventos.periodicos.EventoS1299;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1299Service")
public class S1299Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1299Service.class);

    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    private PessoaFisicaFacade pessoaFisicaFacade;

    @Autowired
    private ESocialService eSocialService;

    @PostConstruct
    public void init() {
        try {
            configuracaoEmpregadorESocialFacade = (ConfiguracaoEmpregadorESocialFacade) new InitialContext().lookup("java:module/ConfiguracaoEmpregadorESocialFacade");
            pessoaFisicaFacade = (PessoaFisicaFacade) new InitialContext().lookup("java:module/PessoaFisicaFacade");
        } catch (NamingException e) {
            logger.error("Não foi possivel criar a instancia: " + e.getMessage());
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    public void enviarS1299(RegistroEventoEsocialS1299 evento) {
        ValidacaoException val = new ValidacaoException();
        ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(evento.getEntidade());
        EventoS1299 s1299 = criarEventoS1299(evento, val);
        logger.debug("Antes de Enviar: " + s1299.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1299(s1299);
    }

    private EventoS1299 criarEventoS1299(RegistroEventoEsocialS1299 evento, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(evento.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1299 eventoS1299 = (EventosESocialDTO.S1299) eSocialService.getEventoS1299(empregador);
        String ano = evento.getExercicio().toString();
        String mes = evento.getMes().getNumeroMes().toString();
        eventoS1299.setIdESocial(ano.concat(mes).concat(evento.getEntidade().getId().toString()));
        eventoS1299.setIndApuracao(evento.getTipoApuracaoFolha().equals(TipoApuracaoFolha.SALARIO_13) ? 2 : 1);
        if (evento.getTipoApuracaoFolha().equals(TipoApuracaoFolha.SALARIO_13)) {
            eventoS1299.setPerApur(evento.getExercicio().getAno());
        } else {
            eventoS1299.setPerApur(evento.getExercicio().getAno(), evento.getMes().getNumeroMes());
        }
        adicionarInformacoesFechamento(eventoS1299, evento);
        return eventoS1299;
    }


    private void adicionarInformacoesFechamento(EventosESocialDTO.S1299 eventoS1299, RegistroEventoEsocialS1299 registroEventoEsocialS1299) {
        eventoS1299.setEvtRemun(registroEventoEsocialS1299.getRemuneracaoTrabalhador());
        eventoS1299.setEvtPgtos(registroEventoEsocialS1299.getRendimentoTrabalho());
        eventoS1299.setEvtComProd(registroEventoEsocialS1299.getComercializacaoProducao());
        eventoS1299.setEvtContratAvNP(registroEventoEsocialS1299.getTrabalhadorAvulsoNaoPortuario());
        eventoS1299.setEvtInfoComplPer(registroEventoEsocialS1299.getDesoneracaoFolha());
    }
}
