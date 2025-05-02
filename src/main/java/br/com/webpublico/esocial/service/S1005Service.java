package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.eventos.iniciais.EventoS1005;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1005Service")
public class S1005Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1005Service.class);

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


    public void enviarS1005(ConfiguracaoEmpregadorESocial config) {
        ValidacaoException val = new ValidacaoException();
        EventoS1005 s1005 = criarEventoS1005(config, val);
        logger.debug("Antes de Enviar: " + s1005.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1005(s1005);
    }

    public EventoS1005 criarEventoS1005(ConfiguracaoEmpregadorESocial config, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1005 eventoS1005 = (EventosESocialDTO.S1005) eSocialService.getEventoS1005(empregador);

        adicionarInfoEstabeleciomento(eventoS1005, config, val);
        adicionarProcessoJudicialAlteracaoRat(eventoS1005, config, val);
        adicionarProcessoJudicialAlteracaoFap(eventoS1005, config, val);
        adicionarInfoTrabalhistas(eventoS1005, config, val);

        return eventoS1005;
    }

    private void adicionarInfoTrabalhistas(EventosESocialDTO.S1005 eventoS1005, ConfiguracaoEmpregadorESocial config, ValidacaoException val) {

        if (config.getMenorAprendizEducativo()) {
            if (config.getProcessoJudPCD() != null) {
                eventoS1005.setNrProcJudApr(config.getProcessoJudPCD().getNumeroProcesso());
            }
            EventoS1005.InfoEntEduc infoEntEduc = eventoS1005.addInfoEntEduc();
            infoEntEduc.setNrInsc(Util.removeMascaras(config.getEntidadeEducativa().getCnpj()));
        }

    }

    private void adicionarProcessoJudicialAlteracaoRat(EventosESocialDTO.S1005 eventoS1005, ConfiguracaoEmpregadorESocial config, ValidacaoException val) {
        if (config.getProcessoJudAlteracaoRat() != null) {
            if (config.getProcessoJudAlteracaoRat().getTipoProcesso() != null) {
                eventoS1005.setTpProcAdmJudRat(config.getProcessoJudAlteracaoRat().getTipoProcesso().getCodigo());
            } else {
                val.adicionarMensagemDeCampoObrigatorio("Não foi informado o Tipo Processo Judicial de Alteração do RAT.");
            }
            if (config.getProcessoJudAlteracaoRat().getNumeroProcesso() != null) {
                eventoS1005.setNrProcAdmJudRat(config.getProcessoJudAlteracaoRat().getNumeroProcesso());
            } else {
                val.adicionarMensagemDeCampoObrigatorio("Não foi informado o Número do Processo Judicial de Alteração do RAT.");
            }
            if (config.getProcessoJudAlteracaoRat().getCodigoSuspensao() != null) {
                eventoS1005.setCodSuspAdmJudRat(Integer.valueOf(config.getProcessoJudAlteracaoRat().getCodigoSuspensao()));
            } else {
                val.adicionarMensagemDeCampoObrigatorio("Não foi informado o Código de Suspensão do Processo Judicial de Alteração do RAT.");
            }
        }
    }

    private void adicionarProcessoJudicialAlteracaoFap(EventosESocialDTO.S1005 eventoS1005, ConfiguracaoEmpregadorESocial config, ValidacaoException val) {
        if (config.getProcessoJudAlteracaoFap() != null) {
            if (config.getProcessoJudAlteracaoFap().getTipoProcesso() != null) {
                eventoS1005.setTpProcAdmJudFap(config.getProcessoJudAlteracaoFap().getTipoProcesso().getCodigo());
            } else {
                val.adicionarMensagemDeCampoObrigatorio("Não foi informado o Tipo Processo Judicial de Alteração do FAP.");
            }
            if (config.getProcessoJudAlteracaoFap().getNumeroProcesso() != null) {
                eventoS1005.setNrProcAdmJudFap(config.getProcessoJudAlteracaoFap().getNumeroProcesso());
            } else {
                val.adicionarMensagemDeCampoObrigatorio("Não foi informado o Número do Processo Judicial de Alteração do FAP.");
            }
            if (config.getProcessoJudAlteracaoFap().getCodigoSuspensao() != null) {
                eventoS1005.setCodSuspAdmJudFap(Integer.valueOf(config.getProcessoJudAlteracaoFap().getCodigoSuspensao()));
            } else {
                val.adicionarMensagemDeCampoObrigatorio("Não foi informado o Código de Suspensão do Processo Judicial de Alteração do FAP.");
            }
        }
    }

    private void adicionarInfoEstabeleciomento(EventosESocialDTO.S1005 eventoS1005, ConfiguracaoEmpregadorESocial config, ValidacaoException val) {

        eventoS1005.setIdentificadorWP(config.getId().toString());
        eventoS1005.setClasseWP(ClasseWP.CONFIGURACAOEMPREGADORESOCIAL);
        eventoS1005.setDescricao(config.toString());

        eventoS1005.setIdESocial(config.getId().toString());

        eventoS1005.setTpInscEstab(1); //CNPJ
        eventoS1005.setNrInscEstab(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        eventoS1005.setIniValid(config.getInicioVigencia());
        eventoS1005.setFimValid(config.getFinalVigencia());
        if (config.getEntidade().getCnae() != null) {
            eventoS1005.setCnaePrep(Integer.valueOf(config.getEntidade().getCnae().getCodigoCnae()));
        } else {
            val.adicionarMensagemDeCampoObrigatorio("Preencher o CNAE na tela de Entidade.");
        }


        if (config.getProcessoJudAlteracaoRat() != null) {
            if (config.getEntidade().getAliquotaRAT() != null) {
                eventoS1005.setAliqRat(config.getEntidade().getAliquotaRAT().intValue());
            } else {
                val.adicionarMensagemDeCampoObrigatorio("Preencher a Aliquota RAT na tela de Entidade.");
            }
        }

        if (config.getProcessoJudAlteracaoFap() != null) {
            if (config.getEntidade().getFap() != null) {
                eventoS1005.setFap(config.getEntidade().getFap());
            } else {
                val.adicionarMensagemDeCampoObrigatorio("Para Pessoa Jurídica, o FAP é obrigatório, preencher na tela de Entidade.");
            }
        }
    }

}
