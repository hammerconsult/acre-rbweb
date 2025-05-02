package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.Telefone;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.esocial.comunicacao.eventos.iniciais.EventoS1000;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static br.com.webpublico.util.StringUtil.convertVazioEmNull;
import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1000Service")
public class S1000Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1000Service.class);

    @Autowired
    private ESocialService eSocialService;


    private SistemaFacade sistemaFacade;

    private PessoaFisicaFacade pessoaFisicaFacade;


    @PostConstruct
    public void init() {
        try {
            pessoaFisicaFacade = (PessoaFisicaFacade) new InitialContext().lookup("java:module/PessoaFisicaFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }


    public void enviarS1000(ConfiguracaoEmpregadorESocial config) {
        ValidacaoException val = new ValidacaoException();
        EventoS1000 s1000 = criarEventoS1000(config, val);
        logger.debug("Antes de Enviar: " + s1000.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1000(s1000);
    }

    public EventoS1000 criarEventoS1000(ConfiguracaoEmpregadorESocial config, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1000 eventoS1000 = (EventosESocialDTO.S1000) eSocialService.getEventoS1000(empregador);
        eventoS1000.setIdentificadorWP(config.getId().toString());
        eventoS1000.setClasseWP(ClasseWP.CONFIGURACAOEMPREGADORESOCIAL);
        eventoS1000.setDescricao(config.toString());

        //Info Cadastro
        adicionarInfoCadastro(config, eventoS1000, val);
        //dados isenção
        adicionarDadosIsencao(config, eventoS1000, val);
        adicionarValidade(config, eventoS1000, val);


        return eventoS1000;
    }

    private void adicionarValidade(ConfiguracaoEmpregadorESocial config, EventosESocialDTO.S1000 eventoS1000, ValidacaoException val) {
        eventoS1000.setIniValid(config.getInicioVigencia());
        eventoS1000.setFimValid(config.getFinalVigencia());
    }

    private void adicionarDadosIsencao(ConfiguracaoEmpregadorESocial config, EventosESocialDTO.S1000 eventoS1000, ValidacaoException val) {
        if (config.nenhumCampoPrenchidoDadosIsencao()) {
            return;
        }
        if (config.dadosIsencoesObrigatorios()) {
            eventoS1000.setIdeMinLei(convertVazioEmNull(config.getSiglaNomeLeiCertificado()));
            eventoS1000.setNrCertif(convertVazioEmNull(config.getNumeroCertificado()));
            eventoS1000.setDtEmisCertif(config.getDataEmissaoCertificado());
            eventoS1000.setDtVencCertif(config.getDataVencimentoCertificado());

            eventoS1000.setNrProtRenov(convertVazioEmNull(config.getNumeroProtocoloRenovacao()));
            eventoS1000.setDtProtRenov(config.getDataProtocoloRenovacao());
            eventoS1000.setDtDou(config.getDataDou());
            eventoS1000.setPagDou(config.getPaginaDou());
        }

    }

    private void adicionarInfoCadastro(ConfiguracaoEmpregadorESocial config, EventosESocialDTO.S1000 eventoS1000, ValidacaoException val) {

        eventoS1000.setIdESocial(config.getId().toString());
        eventoS1000.setClassTrib(config.getClassificacaoTributaria().getCodigo());
        eventoS1000.setIndCoop(0);
        eventoS1000.setIndConstr(0);
        eventoS1000.setIndDesFolha(config.getClassificacaoTributaria().getTipoDesoneracaoFolhaESocial().getCodigo());
        eventoS1000.setIndOptRegEletron(config.getOptouPontoEletronico().getCodigo());

        if (config.getEnteFederativo() != null && !Strings.isNullOrEmpty(config.getEnteFederativo().getCnpj())) {
            eventoS1000.setCnpjEFR(retornaApenasNumeros(config.getEnteFederativo().getCnpj()));
        } else {
            val.adicionarMensagemDeOperacaoNaoPermitida("A Pessoa vinculada ao Ente Federativo " + config.getEnteFederativo() + " está sem CNPJ.");
        }
    }

    private void validarSofwareHouse(String cnpj, String nomeEmpresa, String nomeContato, String telefone, ValidacaoException val) {
        if (cnpj == null) {
            val.adicionarMensagemDeCampoObrigatorio("CNPJ da Sofware House é obrigatório");
        }
        if (nomeEmpresa == null) {
            val.adicionarMensagemDeCampoObrigatorio("O nome da empresa Sofware House é obrigatório");
        }
        if (nomeContato == null) {
            val.adicionarMensagemDeCampoObrigatorio("O nome do contato da empresa Sofware House é obrigatório");
        }
        if (telefone == null) {
            val.adicionarMensagemDeCampoObrigatorio("O telefone da empresa Sofware House é obrigatório");
        }

    }

    private String getTelefone(PessoaFisica responsavel, TipoTelefone tipo) {
        responsavel = pessoaFisicaFacade.recuperar(responsavel.getId());

        String telefone = "";
        if (responsavel.getTelefonePrincipal() != null) {
            if (tipo != null && tipo.equals(responsavel.getTelefonePrincipal().getTipoFone())) {
                Telefone tel = responsavel.getTelefonePrincipal();
                return getTelefone(tel);
            } else if (tipo == null) {
                return getTelefone(responsavel.getTelefonePrincipal());
            } else {
                return getTelefone(responsavel.getTelefonePrincipal());
            }
        }

        if (!responsavel.getTelefones().isEmpty()) {
            for (Telefone fone : responsavel.getTelefones()) {
                if (tipo != null && tipo.equals(fone.getTipoFone())) {
                    telefone = getTelefone(fone);
                } else if (tipo == null) {
                    telefone = getTelefone(fone);
                }
            }
            if (telefone.isEmpty()) {
                for (Telefone fone : responsavel.getTelefones()) {
                    return getTelefone(fone);
                }
            }
        }
        return telefone;
    }

    public String getTelefone(Telefone telefone) {
        String tel = telefone.getDDD() + telefone.getTelefone();
        return StringUtil.retornaApenasNumeros(tel);
    }

}
