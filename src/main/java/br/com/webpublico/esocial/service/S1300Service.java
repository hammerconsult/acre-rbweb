package br.com.webpublico.esocial.service;


import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.entidades.rh.esocial.VinculoFPEventoEsocial;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.esocial.comunicacao.eventos.periodicos.EventoS1300;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1300Service")
public class S1300Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1300Service.class);

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

    public void enviarS1300(RegistroEventoEsocial registroEventoEsocial, VinculoFPEventoEsocial vinculoFPEventoEsocial) {
        ValidacaoException val = new ValidacaoException();
        ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(registroEventoEsocial.getEntidade());
        EventoS1300 s1300 = criarEventoS1300(registroEventoEsocial, vinculoFPEventoEsocial, val);
        logger.debug("Antes de Enviar: " + s1300.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1300(s1300);
    }

    private EventoS1300 criarEventoS1300(RegistroEventoEsocial registroEventoEsocial, VinculoFPEventoEsocial vinculoFPEventoEsocial, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(registroEventoEsocial.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1300 eventoS1300 = (EventosESocialDTO.S1300) eSocialService.getEventoS1300(empregador);
        eventoS1300.setIdentificadorWP(vinculoFPEventoEsocial.getVinculoFP().getId().toString());
        eventoS1300.setClasseWP(ClasseWP.VINCULOFP);
        eventoS1300.setIdESocial(vinculoFPEventoEsocial.getVinculoFP().getId().toString());
        eventoS1300.setDescricao(vinculoFPEventoEsocial.getVinculoFP().toString());

        eventoS1300.setIndApuracao(registroEventoEsocial.getTipoFolhaDePagamento().equals(TipoFolhaDePagamento.SALARIO_13) ? 2 : 1);
        if (registroEventoEsocial.getTipoFolhaDePagamento().equals(TipoFolhaDePagamento.SALARIO_13)) {
            eventoS1300.setPerApur(registroEventoEsocial.getExercicio().getAno());
        } else {
            eventoS1300.setPerApur(registroEventoEsocial.getExercicio().getAno(), registroEventoEsocial.getMes().getNumeroMes());
        }
        adicionarInformacoesBasicas(eventoS1300, vinculoFPEventoEsocial.getVinculoFP(), val);
        adicionarInformacoesSindicato(registroEventoEsocial, eventoS1300);
        return eventoS1300;
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S1300 eventoS1300, VinculoFP vinculoFP, ValidacaoException val) {
        PessoaFisica pessoa = vinculoFP.getMatriculaFP().getPessoa();
        pessoa = pessoaFisicaFacade.recuperarComDocumentos(pessoa.getId());
        eventoS1300.setCpfTrab(StringUtil.retornaApenasNumeros(pessoa.getCpf()));
        if (pessoa.getCarteiraDeTrabalho() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoa + " não possui carteira de trabalho.");
        } else {
            eventoS1300.setNisTrab(pessoa.getCarteiraDeTrabalho().getPisPasep());
        }
    }

    private void adicionarInformacoesSindicato(RegistroEventoEsocial registroEventoEsocial, EventosESocialDTO.S1300 eventoS1300) {
        for (VinculoFPEventoEsocial vinculoFPEventoEsocial : registroEventoEsocial.getItemVinculoFP()) {
            EventoS1300.ContribSind contribSind = eventoS1300.addContribSind();
            contribSind.setCnpjSindic(StringUtil.retornaApenasNumeros(vinculoFPEventoEsocial.getVinculoFP().getSindicatoVinculoFPVigente().getSindicato().getPessoaJuridica().getCnpj()));
            contribSind.setTpContribSind(1);
            contribSind.setVlrContribSind(vinculoFPEventoEsocial.getValorEventoFP().doubleValue());
        }


    }
}
