package br.com.webpublico.reinf.service;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.contabil.reinf.FiltroReinf;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.AssistenteSincronizacaoReinf;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.reinf.eventos.EventosReinfDTO;
import br.com.webpublico.reinf.eventos.domain.EventoR4099;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "r4099Service")
public class R4099Service {
    protected static final Logger logger = LoggerFactory.getLogger(R4099Service.class);
    @Autowired
    private ReinfService reinfService;
    private EmpregadorESocial empregadorESocial;

    public void enviar(AssistenteSincronizacaoReinf assistente) {
        reinfService.enviarEventoR4099(assistente.getSelecionado().getEventoR4099());
    }

    public EventoR4099 criarEventosR4099(ConfiguracaoEmpregadorESocial config, FiltroReinf reg) {
        empregadorESocial = reinfService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosReinfDTO.R4099 evento = criarEventoR4099(config, reg);
        if (evento != null) {
            logger.info("XML " + evento.getXml());
            return evento;
        }
        return null;
    }

    private EventosReinfDTO.R4099 criarEventoR4099(ConfiguracaoEmpregadorESocial config, FiltroReinf reg) {
        EventosReinfDTO.R4099 evento = (EventosReinfDTO.R4099) reinfService.getEventoR4099(empregadorESocial);
        Integer mes = reg.getMes().getNumeroMes();
        Integer ano = reg.getExercicio().getAno();
        evento.setIdESocial(config.getId().toString().concat(ano.toString().concat(mes.toString())));
        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());
        evento.setPerApur(ano, mes);
        evento.setTpAmb(reinfService.isPerfilDev() ? 2 : 1);
        evento.setProcEmi(1);
        evento.setVerProc("2.1.2");
        evento.setFechRet(reg.getCodigoFechamentoReabertura().shortValue());
        PessoaFisica responsavel = config.getResponsavelReinf();
        if (responsavel != null) {
            evento.setNmResp(responsavel.getNome());
            evento.setCpfResp(responsavel.getCpfSemFormatacao());
            evento.setTelefone(Util.removerCaracteresEspeciais(responsavel.getTelefonePrincipal().getTelefone()).replace(" ",""));
            if (responsavel.getEmail() != null && Util.validarEmail(responsavel.getEmail())) {
                evento.setEmail(responsavel.getEmail());
            }
        }
        return evento;
    }
}
