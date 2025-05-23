package br.com.webpublico.reinf.service;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidadesauxiliares.AssistenteSincronizacaoReinf;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.reinf.eventos.EventosReinfDTO;
import br.com.webpublico.reinf.eventos.domain.EventoR1000;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "r1000Service")
public class R1000Service {

    protected static final Logger logger = LoggerFactory.getLogger(R1000Service.class);

    @Autowired
    private ReinfService reinfService;


    private PessoaFacade pessoaFacade;

    private EmpregadorESocial empregadorESocial;


    @PostConstruct
    public void init() {
        try {
            pessoaFacade = (PessoaFacade) new InitialContext().lookup("java:module/PessoaFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }


    public void enviarR1000(AssistenteSincronizacaoReinf assistente, EventoR1000 r1000) {
        if (assistente.getSelecionado().getUtilizarVersao2_1()) {
            reinfService.enviarEventoR1000V2(r1000);
        } else {
            reinfService.enviarEventoR1000(r1000);
        }
    }

    public List<EventoR1000> criarEventosR1000(AssistenteSincronizacaoReinf assistente) {
        List<EventoR1000> retorno = Lists.newArrayList();
        ValidacaoException val = new ValidacaoException();
        this.empregadorESocial = null;
        EventoR1000 r1000 = criarEventoR1000(assistente, val);
        if (r1000 != null) {
            logger.info("XML " + r1000.getXml());
            //val.lancarException();
            // reinfService.enviarEventoR1000(r1000);
            retorno.add(r1000);
        }
        return retorno;
    }

    public EventoR1000 criarEventoR1000(AssistenteSincronizacaoReinf assistente, ValidacaoException val) {
        ConfiguracaoEmpregadorESocial config = assistente.getConfiguracaoEmpregadorESocial();
        if (empregadorESocial == null) {
            empregadorESocial = reinfService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        }

        if (assistente.getSelecionado().getUtilizarVersao2_1()) {
            EventosReinfDTO.R1000V2 evento = criarEventoR1000V2(assistente, val, config);
            return evento;
        } else {
            EventosReinfDTO.R1000 evento = criarEventoR1000V1(assistente, val, config);
            return evento;
        }
    }

    private EventosReinfDTO.R1000V2 criarEventoR1000V2(AssistenteSincronizacaoReinf assistente, ValidacaoException val, ConfiguracaoEmpregadorESocial config) {
        EventosReinfDTO.R1000V2 evento = (EventosReinfDTO.R1000V2) reinfService.getEventoR1000V2(empregadorESocial);

        evento.setIdESocial(config.getId().toString());
        evento.setClassTrib(config.getClassificacaoTributaria().getCodigo());

        /*para excluir da base do REINF (Homologação) */
       /* evento.setVerProc("RemoverContribuinte");
        evento.setClassTrib("00");*/

        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());

        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }

        evento.setIndEscrituracao(0);
        evento.setIndDesoneracao(0);
        evento.setIndAcordoIsenMulta(0);
        evento.setIndSitPJ(0);

        if (config.getEnteFederativoResponsavel()) {
            evento.setIdeEFR("S");
        } else {
            if (config.getEnteFederativo() != null && !Strings.isNullOrEmpty(config.getEnteFederativo().getCnpj())) {
                evento.setIdeEFR("N");
                evento.setCnpjEFR(retornaApenasNumeros(config.getEnteFederativo().getCnpj()));
            }
        }

        adicionarRepresentante(config.getResponsavelReinf(), val, evento);
        if (assistente.getSelecionado().getExclusaoR1000()) {
            evento.setIniValid(config.getDataInicioReinf(), true);
            evento.setFimValid(config.getDataFimReinf(), true);
            evento.setOperacao(TipoOperacaoESocial.EXCLUSAO);
        } else {
            evento.setIniValid(config.getDataInicioReinf(), false);
        }
        return evento;
    }

    private void adicionarRepresentante(Pessoa representante, ValidacaoException val, EventosReinfDTO.R1000V2 evento) {

        evento.setNmCtt(representante.getNome());
        evento.setCpfCtt(((PessoaFisica) representante).getCpfSemFormatacao());

        List<Telefone> telefones = pessoaFacade.telefonePorPessoa(representante);
        Telefone residencial = null;
        Telefone celular = null;

        for (Telefone telefone : telefones) {
            if (TipoTelefone.RESIDENCIAL.equals(telefone.getTipoFone()) || TipoTelefone.FIXO.equals(telefone.getTipoFone())) {
                residencial = telefone;
            }
            if (TipoTelefone.CELULAR.equals(telefone.getTipoFone())) {
                celular = telefone;
            }
        }
        if (residencial != null) {
            evento.setFoneFixo(StringUtil.retornaApenasNumeros(residencial.getTelefone()));
        }
        if (celular != null) {
            evento.setFoneCel(StringUtil.retornaApenasNumeros(celular.getTelefone()));
        }
        evento.setEmail(representante.getEmail());

    }

    private EventosReinfDTO.R1000 criarEventoR1000V1(AssistenteSincronizacaoReinf assistente, ValidacaoException val, ConfiguracaoEmpregadorESocial config) {
        EventosReinfDTO.R1000 evento = (EventosReinfDTO.R1000) reinfService.getEventoR1000(empregadorESocial);

        evento.setIdESocial(config.getId().toString());
        evento.setClassTrib(config.getClassificacaoTributaria().getCodigo());

        evento.setTpInsc(1);
        evento.setNrInsc(empregadorESocial.getNrInscID());

        if (reinfService.isPerfilDev()) {
            evento.setTpAmb(2);
        }

        evento.setIndEscrituracao(0);
        evento.setIndDesoneracao(0);
        evento.setIndAcordoIsenMulta(0);
        evento.setIndSitPJ(0);

        if (config.getEnteFederativoResponsavel()) {
            evento.setIdeEFR("S");
        }
        if (config.getEnteFederativo() != null && !Strings.isNullOrEmpty(config.getEnteFederativo().getCnpj())) {
            evento.setIdeEFR("N");
            evento.setCnpjEFR(retornaApenasNumeros(config.getEnteFederativo().getCnpj()));
        }

        adicionarRepresentante(config.getResponsavelReinf(), val, evento);
        if (assistente.getSelecionado().getExclusaoR1000()) {
            evento.setIniValid(config.getDataInicioReinf(), true);
            evento.setFimValid(config.getDataFimReinf(), true);
            evento.setOperacao(TipoOperacaoESocial.EXCLUSAO);
        } else {
            evento.setIniValid(config.getDataInicioReinf(), false);
            evento.setFimValid(config.getDataFimReinf(), false);
        }
        return evento;
    }

    private void adicionarRepresentante(Pessoa representante, ValidacaoException val, EventosReinfDTO.R1000 evento) {

        evento.setNmCtt(representante.getNome());
        evento.setCpfCtt(((PessoaFisica) representante).getCpfSemFormatacao());

        List<Telefone> telefones = pessoaFacade.telefonePorPessoa(representante);
        Telefone residencial = null;
        Telefone celular = null;

        for (Telefone telefone : telefones) {
            if (TipoTelefone.RESIDENCIAL.equals(telefone.getTipoFone()) || TipoTelefone.FIXO.equals(telefone.getTipoFone())) {
                residencial = telefone;
            }
            if (TipoTelefone.CELULAR.equals(telefone.getTipoFone())) {
                celular = telefone;
            }
        }
        if (residencial != null) {
            evento.setFoneFixo(StringUtil.retornaApenasNumeros(residencial.getTelefone()));
        }
        if (celular != null) {
            evento.setFoneCel(StringUtil.retornaApenasNumeros(celular.getTelefone()));
        }
        evento.setEmail(representante.getEmail());

    }
}
