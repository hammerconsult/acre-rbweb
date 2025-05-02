package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.rh.esocial.TipoRegimePrevidenciario;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2206;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.CBOFacade;
import br.com.webpublico.negocios.CidadeFacade;
import br.com.webpublico.negocios.EnderecoCorreioFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.negocios.rh.esocial.RegistroESocialFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.math.BigDecimal;
import java.util.Date;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

/**
 * Created by William on 20/09/2018.
 */
@Service(value = "s2206Service")
public class S2206Service {
    protected static final Logger logger = LoggerFactory.getLogger(S2206Service.class);
    public static final int TEMPORARIO = 106;
    public static final int COD_IBGE_MUNIC_RIO_BRANCO = 1200401;
    public static final int EMPREGADO_CODIGO_TRABALHO_INTERMITENTE = 111;
    private static final int TP_REG_TRAB_ESTATUTARIO = 2;
    private static final int TP_PROV_CARGO_COMISSAO = 2;

    @Autowired
    private ESocialService eSocialService;
    @Autowired
    private S2200Service s2200Service;

    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private EnderecoCorreioFacade enderecoCorreioFacade;
    private CidadeFacade cidadeFacade;
    private CBOFacade cboFacade;
    private RegistroESocialFacade registroESocialFacade;

    @PostConstruct
    public void init() {
        try {
            fichaFinanceiraFPFacade = (FichaFinanceiraFPFacade) new InitialContext().lookup("java:module/FichaFinanceiraFPFacade");
            enderecoCorreioFacade = (EnderecoCorreioFacade) new InitialContext().lookup("java:module/EnderecoCorreioFacade");
            cidadeFacade = (CidadeFacade) new InitialContext().lookup("java:module/CidadeFacade");
            cboFacade = (CBOFacade) new InitialContext().lookup("java:module/CBOFacade");
            registroESocialFacade = (RegistroESocialFacade) new InitialContext().lookup("java:module/RegistroESocialFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    public void enviarS2206(ConfiguracaoEmpregadorESocial config, ContratoFP contratoFP, Date dataOperacao, Date dataAlteracaoContrato) {
        ValidacaoException val = new ValidacaoException();
        EventoS2206 s2206 = criarEventoS2206(config, contratoFP, val, dataOperacao, dataAlteracaoContrato);
        logger.debug("Antes de Enviar: " + s2206.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2206(s2206);
    }

    private EventoS2206 criarEventoS2206(ConfiguracaoEmpregadorESocial config, ContratoFP contratoFP, ValidacaoException val, Date dataOperacao, Date dataAlteracaoContrato) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2206 eventoS2206 = (EventosESocialDTO.S2206) eSocialService.getEventoS2206(empregador);
        eventoS2206.setIdentificadorWP(contratoFP.getId().toString());
        eventoS2206.setClasseWP(ClasseWP.VINCULOFP);
        eventoS2206.setDescricao(contratoFP.toString());

        eventoS2206.setIdESocial(contratoFP.getId().toString());
        adicionarInformacoesEmpregador(eventoS2206, config, val);
        adicionarInformacoesTrabalhador(eventoS2206, contratoFP, val, dataAlteracaoContrato);
        if (TipoRegime.CELETISTA.equals(contratoFP.getTipoRegime().getCodigo())) {
            adicionarInformacoesRegimeTrabalhista(eventoS2206, contratoFP, val);
        } else {
            adicionarInformacoesRegimeEstatutario(eventoS2206, contratoFP);
        }
        adicionarInformacoesContratoTrabalhador(eventoS2206, contratoFP, val, dataOperacao);
        if (!ModalidadeContratoFP.CONCURSADOS.equals(contratoFP.getModalidadeContratoFP().getCodigo())) {
            adicionarInformacoesContrato(eventoS2206, contratoFP, val);
            adicionarInformacoesRemuneracao(eventoS2206, contratoFP, val, dataOperacao);
        }
        adicionarInformacoesVinculo(eventoS2206, contratoFP);
        if (contratoFP.getCategoriaTrabalhador() != null && contratoFP.getCategoriaTrabalhador().getCodigo() == TEMPORARIO) {
            adicionarInformacoesEnderecoContratoTemporario(eventoS2206, contratoFP, val);
        }
        return eventoS2206;
    }

    private void adicionarInformacoesEmpregador(EventosESocialDTO.S2206 eventoS2206, ConfiguracaoEmpregadorESocial config, ValidacaoException ve) {
        eventoS2206.setTpInscLocalTrabGeral(1); //cnpj 165
        eventoS2206.setNrInscLocalTrabGeral((StringUtil.retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj().trim())));
    }

    private void adicionarInformacoesContrato(EventosESocialDTO.S2206 eventoS2206, ContratoFP contratoFP, ValidacaoException ve) {
        if (contratoFP.getModalidadeContratoFP() != null && contratoFP.getModalidadeContratoFP().getTipoPrazoContrato() != null) {
            eventoS2206.setTpContr(contratoFP.getModalidadeContratoFP().getTipoPrazoContrato().getCodigoESocial());
        } else {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Servidor(a) " + contratoFP + " não possui a Modalidade de Contrato configurado corretamente com o Prazo do Contrato.");
            ve.lancarException();
        }

        if (contratoFP.getFinalVigencia() != null) {
            eventoS2206.setDtTerm(contratoFP.getFinalVigencia());
        }
    }

    private void adicionarInformacoesTrabalhador(EventosESocialDTO.S2206 eventoS2206, ContratoFP contratoFP, ValidacaoException ve, Date dataAlteracaoContrato) {
        eventoS2206.setCpfTrab(StringUtil.retornaApenasNumeros(contratoFP.getMatriculaFP().getPessoa().getCpf()));
        if (contratoFP.getMatriculaFP().getPessoa().getCarteiraDeTrabalho() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + contratoFP.getMatriculaFP().getPessoa() + " não possui carteira de trabalho.");
        } else {
            eventoS2206.setNisTrab(contratoFP.getMatriculaFP().getPessoa().getCarteiraDeTrabalho().getPisPasep());
        }

        String matricula = registroESocialFacade.getMatriculaS2200ProcessadoSucesso(contratoFP);
        if (matricula == null) {
            String vinculo = StringUtil.cortarOuCompletarEsquerda(contratoFP.getNumero(), 2, "0");
            eventoS2206.setMatricula(contratoFP.getMatriculaFP().getMatricula().concat(vinculo));
        } else {
            eventoS2206.setMatricula(matricula);
        }
        eventoS2206.setDtAlteracao(dataAlteracaoContrato);
        eventoS2206.setTpRegPrev(contratoFP.getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getTipoRegimePrevidenciario().getCodigoTipoRegimeEsocial());
    }

    private void adicionarInformacoesRegimeTrabalhista(EventosESocialDTO.S2206 eventoS2206, ContratoFP contratoFP, ValidacaoException ve) {
        eventoS2206.setTpRegJor(1); // TODO temos duvida nessa informação campo 118
        eventoS2206.setNatAtividade(1);

        Sindicato sindicato = null;
        if (contratoFP.getSindicatoVinculoFPVigente() != null) {
            sindicato = contratoFP.getSindicatoVinculoFPVigente().getSindicato();
        } else {
            sindicato = s2200Service.buscarSindicatoPeloCargo(contratoFP.getCargo());
        }
        if (sindicato == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrato Sindicato para o cargo: " + contratoFP.getCargo() + " do servidor(a) " + contratoFP);
            return;
        }
        if (sindicato != null) {
            if (sindicato.getPessoaJuridica() != null && Strings.isNullOrEmpty(sindicato.getPessoaJuridica().getCnpj())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O sindicato Servidor(a) " + contratoFP + " - sindicato + " + sindicato.getPessoaJuridica() + " não possui CNPJ cadastrado.");
            } else {
                eventoS2206.setCnpjSindCategProf(StringUtil.retornaApenasNumeros(sindicato.getPessoaJuridica().getCnpj()));
            }
        }
    }

    private void adicionarInformacoesContratoTrabalhador(EventosESocialDTO.S2206 eventoS2206, ContratoFP contratoFP, ValidacaoException ve, Date dataOperacao) {
        int tpRegTrab = contratoFP.getTipoRegime().getCodigo().intValue();
        int tpProv = contratoFP.getModalidadeContratoFP().getProvimentoEstatutarioEsocial().getCodigo();

        String descricaoCarreira = contratoFP.getCargo().getDescricaoCarreira();
        String nomeCargo = contratoFP.getCargo().getDescricao();

        if (tpRegTrab == TP_REG_TRAB_ESTATUTARIO && tpProv == TP_PROV_CARGO_COMISSAO)  {

            if (nomeCargo != null && !nomeCargo.isEmpty()) {
                eventoS2206.setNmCargo(nomeCargo);
            }

            if (descricaoCarreira != null && !descricaoCarreira.isEmpty()) {
                eventoS2206.setNmFuncao(descricaoCarreira);
                CBO cboFuncao = null;
                ContratoFPCargo contratoFPCargo = contratoFP.getCargoVigente(dataOperacao);
                if (contratoFPCargo != null && contratoFPCargo.getCbo() != null) {
                    cboFuncao = contratoFPCargo.getCbo();
                } else {
                    cboFuncao = cboFacade.getCBOCargoOrdenadoPorCodigo(contratoFP.getCargo());
                }

                if (cboFuncao != null ) {
                    if(cboFuncao.getCodigo().toString().length() == 6){
                        eventoS2206.setCBOFuncao(cboFuncao.getCodigo().toString());
                    } else{
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O CBO da função de confiança/cargo em comissão deve possuir 6 dígitos.");
                    }
                } else {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O CBO da função de confiança/cargo em comissão deve ser preenchido.");
                }
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O nome da função de confiança/cargo em comissão é obrigatório.");
            }
        } else {
            if (nomeCargo == null || nomeCargo.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo 'nome cargo' é obrigatório para o regime de trabalho especificado.");
            } else {
                eventoS2206.setNmCargo(nomeCargo);
            }
        }

        eventoS2206.setAcumCargo(contratoFP.getCargo().getPermiteAcumulo());
        CBO cbo = null;
        ContratoFPCargo contratoFPCargo = contratoFP.getCargoVigente(dataOperacao);
        if (contratoFPCargo != null && contratoFPCargo.getCbo() != null) {
            cbo = contratoFPCargo.getCbo();
        } else {
            cbo = cboFacade.getCBOCargoOrdenadoPorCodigo(contratoFP.getCargo());
        }
        if (cbo != null) {
            if(cbo.getCodigo().toString().length() == 6){
                eventoS2206.setCBOCargo(cbo.getCodigo().toString());
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O CBO do cargo deve possuir 6 dígitos.");
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O cargo " + contratoFP.getCargo() + " não possui CBO cadastrado.");
        }
        if (contratoFP.getCategoriaTrabalhador() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi informado a categoria do trabalhador " + contratoFP + " em seu cadastro na aba 'e-social'");
        } else {
            if (contratoFP.getCategoriaTrabalhador().getCodigo() != EMPREGADO_CODIGO_TRABALHO_INTERMITENTE) {
                eventoS2206.setHorNoturno(false);
            }
            eventoS2206.setCodCateg(contratoFP.getCategoriaTrabalhador().getCodigo());
        }
    }

    private void adicionarInformacoesRemuneracao(EventosESocialDTO.S2206 eventoS2206, ContratoFP contratoFP, ValidacaoException ve, Date dataOperacao) {
        BigDecimal valorRemuneracao = fichaFinanceiraFPFacade.buscarValorEnquadramentoPCSVigente(contratoFP, dataOperacao);
        if (valorRemuneracao != null) {
            eventoS2206.setVrSalFx(valorRemuneracao);
        } else {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi encontrato valor de remuneração para o contrato na data " +
                DataUtil.getDataFormatada(dataOperacao) + " para " + contratoFP);
        }
        eventoS2206.setUndSalFixo(5);
    }

    private void adicionarInformacoesVinculo(EventosESocialDTO.S2206 eventoS2206, ContratoFP contratoFP) {
        eventoS2206.setDscJorn(contratoFP.getJornadaDeTrabalho().getTipoJornadaTrabalho() != null ? contratoFP.getJornadaDeTrabalho().getTipoJornadaTrabalho().getDescricao() :
            contratoFP.getJornadaDeTrabalho().toString());

        eventoS2206.setTpJornada(contratoFP.getJornadaDeTrabalho().getTipoJornadaTrabalho() != null ?
            contratoFP.getJornadaDeTrabalho().getTipoJornadaTrabalho().getCodigoESocial() : 4);

        eventoS2206.setTmpParc(contratoFP.getJornadaDeTrabalho().getTipoTempoParcial() != null ?
            contratoFP.getJornadaDeTrabalho().getTipoTempoParcial().getCodigoESocial() : 0);

        eventoS2206.setQtdHrsSem(BigDecimal.valueOf(contratoFP.getJornadaDeTrabalho().getHorasSemanal()));

    }

    private void adicionarInformacoesEnderecoContratoTemporario(EventosESocialDTO.S2206 eventoS2206, ContratoFP
        contratoFP, ValidacaoException ve) {
        String urlPessoa = "/pessoa/editar/" + contratoFP.getMatriculaFP().getPessoa().getId();
        String urlContrato = "/contratofp/editar/" + contratoFP.getId();
        EnderecoCorreio enderecoPrincipal = contratoFP.getEnderecoContratoTemporario();
        if (enderecoPrincipal == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O contrato " + contratoFP.getMatriculaFP().getPessoa() + " não possúi endereço na aba Esocial " + urlContrato);
            return;
        }
        Cidade cidade = cidadeFacade.recuperaCidadePorNomeEEstado(enderecoPrincipal.getLocalidade(), enderecoPrincipal.getUf());
        if (cidade == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Cidade do contrato da(o) " + contratoFP.getMatriculaFP().getPessoa() + " não encontrado na aba Esocial " + urlContrato);
            return;
        }
        if (Strings.isNullOrEmpty(enderecoPrincipal.getLogradouro())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + contratoFP.getMatriculaFP().getPessoa() + " está sem descrição do logradouro. " + urlPessoa);
        } else {
            eventoS2206.setDscLogradLocalTrabDom(enderecoPrincipal.getLogradouro().trim());
        }
        if (Strings.isNullOrEmpty(enderecoPrincipal.getNumero())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + contratoFP.getMatriculaFP().getPessoa() + " está sem número. " + urlPessoa);
        } else {
            eventoS2206.setNrLogradLocalTrabDom(Util.cortarString(enderecoPrincipal.getNumero(), 10));
        }
        if (!Strings.isNullOrEmpty(enderecoPrincipal.getComplemento())) {
            if (enderecoPrincipal.getComplemento().length() < 2) {
                eventoS2206.setComplementoLocalTrabDom("Compl: " + enderecoPrincipal.getComplemento());
            } else {
                eventoS2206.setComplementoLocalTrabDom(Util.cortarString(enderecoPrincipal.getComplemento(), 30));
            }

        } else {
            eventoS2206.setComplementoLocalTrabDom(null);
        }
        eventoS2206.setBairroLocalTrabDom(!Strings.isNullOrEmpty(enderecoPrincipal.getBairro()) ? enderecoPrincipal.getBairro() : null);
        if (Strings.isNullOrEmpty(enderecoPrincipal.getCep()) || enderecoPrincipal.getCep().length() != 8) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + contratoFP.getMatriculaFP().getPessoa() + " está sem cep ou o cep esta inválido. " + urlPessoa);
        } else {
            eventoS2206.setCepLocalTrabDom(StringUtil.retornaApenasNumeros(enderecoPrincipal.getCep()));
        }

        if (Strings.isNullOrEmpty(enderecoPrincipal.getUf())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + contratoFP.getMatriculaFP().getPessoa() + " está sem UF. " + urlPessoa);
        } else {
            eventoS2206.setUfLocalTrabDom(enderecoPrincipal.getUf().trim());
        }
        if (enderecoPrincipal.getLocalidade() != null && enderecoPrincipal.getUf() != null) {
            BigDecimal codigoIBGECidade = enderecoCorreioFacade.getCodigoIBGECidade(enderecoPrincipal.getLocalidade(), enderecoPrincipal.getUf());
            if (codigoIBGECidade != null) {
                if (codigoIBGECidade.toString().trim().length() != 7) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada("O código do IBGE da Cidade " + enderecoPrincipal.getLocalidade() + " está incorreto," +
                        " deve possuir 7 (sete) caracteres");
                } else {
                    eventoS2206.setCodMunicLocalTrabDom(Integer.parseInt(codigoIBGECidade.toString()));
                }
            } else {
                eventoS2206.setCodMunicLocalTrabDom(COD_IBGE_MUNIC_RIO_BRANCO);
            }
        }
    }

    private void adicionarInformacoesRegimeEstatutario(EventosESocialDTO.S2206 eventoS2206, ContratoFP contratoFP) {
        if (TipoRegimePrevidenciario.RPPS.equals(contratoFP.getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getTipoRegimePrevidenciario())) {
            eventoS2206.setTpPlanRP(contratoFP.getSegregacaoMassa() != null ? contratoFP.getSegregacaoMassa().getCodigo() : 0);
            eventoS2206.setIndTetoRGPS(contratoFP.getTetoRgps());
            eventoS2206.setIndAbonoPerm(contratoFP.getAbonoPermanencia());
        }
    }
}
