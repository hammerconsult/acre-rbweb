package br.com.webpublico.reinf.service;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.contabil.reinf.FiltroReinf;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.AssistenteSincronizacaoReinf;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConfiguracaoContabilFacade;
import br.com.webpublico.reinf.eventos.EventosReinfDTO;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.reinf.eventos.domain.EventoR2099;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "r2099Service")
public class R2099Service {
    protected static final Logger logger = LoggerFactory.getLogger(R2099Service.class);
    @Autowired
    private ReinfService reinfService;
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private EmpregadorESocial empregadorESocial;

    @PostConstruct
    public void init() {
        try {
            configuracaoContabilFacade = (ConfiguracaoContabilFacade) new InitialContext().lookup("java:module/ConfiguracaoContabilFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    public void enviar(AssistenteSincronizacaoReinf assistente) {
        if(assistente.getSelecionado().getUtilizarVersao2_1()){
            reinfService.enviarEventoR2099V2(assistente.getSelecionado().getEventoR2099());
        }else {
            reinfService.enviarEventoR2099(assistente.getSelecionado().getEventoR2099());
        }
    }

    public EventoR2099 criarEventosR2099(AssistenteSincronizacaoReinf assistente, ConfiguracaoEmpregadorESocial config, FiltroReinf reg) {
        ValidacaoException val = new ValidacaoException();
        this.empregadorESocial = null;
        EventoR2099 r2099 = criarEventoR2099(assistente, config, reg, val);
        if (r2099 != null) {
            logger.info("XML " + r2099.getXml());
            //val.lancarException();
            // reinfService.enviarEventoR1000(r1000);
            return r2099;
        }
        return null;
    }

    private EventoR2099 criarEventoR2099(AssistenteSincronizacaoReinf assistente, ConfiguracaoEmpregadorESocial config, FiltroReinf reg, ValidacaoException val) {
        if (empregadorESocial == null) {
            empregadorESocial = reinfService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        }
        if(assistente.getSelecionado().getUtilizarVersao2_1()){
            EventosReinfDTO.R2099V2 evento = criarEventoR2099V2(config, reg, val);
            return evento;
        }else {
            EventosReinfDTO.R2099 evento = criarEventoR2099V1(config, reg, val);
            return evento;
        }
    }

    private EventosReinfDTO.R2099 criarEventoR2099V1(ConfiguracaoEmpregadorESocial config, FiltroReinf reg, ValidacaoException val){
        EventosReinfDTO.R2099 evento = (EventosReinfDTO.R2099) reinfService.getEventoR2099(empregadorESocial);
        Integer mes = reg.getMes().getNumeroMes();
        Integer ano = reg.getExercicio().getAno();
        evento.setIdESocial(config.getId().toString().concat(ano.toString().concat(mes.toString())));
        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());
        evento.setPerApur(ano, mes);
        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }
        PessoaFisica responsavel = config.getResponsavelReinf();
        if (responsavel != null) {
            evento.setNmResp(responsavel.getNome());
            evento.setCpfResp(responsavel.getCpfSemFormatacao());
        }
        Integer quantidadeR2010 = reinfService.getQuantidadeEventosPorEmpregadorAndTipoArquivo(empregadorESocial, TipoArquivoReinf.R2010, SituacaoESocial.PROCESSADO_COM_SUCESSO);
        evento.setEvtServTm(quantidadeR2010 > 0);
        Integer quantidadeR2020 = reinfService.getQuantidadeEventosPorEmpregadorAndTipoArquivo(empregadorESocial, TipoArquivoReinf.R2020, SituacaoESocial.PROCESSADO_COM_SUCESSO);
        evento.setEvtServPr(quantidadeR2020 > 0);
        evento.setEvtAquis(false);
        evento.setEvtAssDespRec(false);
        evento.setEvtAssDespRep(false);
        evento.setEvtCprb(false);
        evento.setEvtComProd(false);
        return evento;
    }

    private EventosReinfDTO.R2099V2 criarEventoR2099V2(ConfiguracaoEmpregadorESocial config, FiltroReinf reg, ValidacaoException val){
        EventosReinfDTO.R2099V2 evento = (EventosReinfDTO.R2099V2) reinfService.getEventoR2099V2(empregadorESocial);
        Integer mes = reg.getMes().getNumeroMes();
        Integer ano = reg.getExercicio().getAno();
        evento.setIdESocial(config.getId().toString().concat(ano.toString().concat(mes.toString())));
        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());
        evento.setPerApur(ano, mes);
        evento.setTpAmb(reinfService.isPerfilDev() ? 2 : 1);
        evento.setProcEmi(1);
        evento.setVerProc("2.1.2");
        PessoaFisica responsavel = config.getResponsavelReinf();
        if (responsavel != null) {
            evento.setNmResp(responsavel.getNome());
            evento.setCpfResp(responsavel.getCpfSemFormatacao());
            evento.setTelefone(Util.removerCaracteresEspeciais(responsavel.getTelefonePrincipal().getTelefone()).replace(" ",""));
            if(responsavel.getEmail() != null && Util.validarEmail(responsavel.getEmail())) {
                evento.setEmail(responsavel.getEmail());
            }
        }
        Integer quantidadeR2010 = reinfService.getQuantidadeEventosPorEmpregadorAndTipoArquivo(empregadorESocial, TipoArquivoReinf.R2010, SituacaoESocial.PROCESSADO_COM_SUCESSO);
        evento.setEvtServTm(quantidadeR2010 > 0);
        Integer quantidadeR2020 = reinfService.getQuantidadeEventosPorEmpregadorAndTipoArquivo(empregadorESocial, TipoArquivoReinf.R2020, SituacaoESocial.PROCESSADO_COM_SUCESSO);
        evento.setEvtServPr(quantidadeR2020 > 0);
        evento.setEvtAquis(false);
        evento.setEvtAssDespRec(false);
        evento.setEvtAssDespRep(false);
        evento.setEvtCprb(false);
        evento.setEvtComProd(false);
        return evento;
    }
}
