package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroESocial;
import br.com.webpublico.enums.rh.esocial.ModalidadeContratacaoAprendiz;
import br.com.webpublico.enums.rh.esocial.ProvimentoEstatutarioEsocial;
import br.com.webpublico.enums.rh.esocial.TipoPrazoContrato;
import br.com.webpublico.enums.rh.esocial.TipoRegimePrevidenciario;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2200;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.esocial.RegistroESocialFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

/**
 * Created by William on 22/08/2018.
 */
@Service(value = "s2200Service")
public class S2200Service {
    public static final int COD_IBGE_MUNIC_RIO_BRANCO = 1200401;
    public static final int BRASILEIRO = 10;
    protected static final Logger logger = LoggerFactory.getLogger(S2200Service.class);
    private static final String S_3000 = " S3000:";
    public static final int EMPREGADO_CODIGO_TRABALHO_INTERMITENTE = 111;
    public static final int TEMPORARIO = 106;
    public static final int APRENDIZ = 103;
    @PersistenceContext
    protected transient EntityManager entityManager;
    @Autowired
    private ESocialService eSocialService;

    private PessoaFisicaFacade pessoaFisicaFacade;

    private EnderecoCorreioFacade enderecoCorreioFacade;
    private CidadeFacade cidadeFacade;

    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;

    private RegistroESocialFacade registroESocialFacade;

    private CBOFacade cboFacade;

    private SindicatoFacade sindicatoFacade;


    @PostConstruct
    public void init() {
        try {
            enderecoCorreioFacade = (EnderecoCorreioFacade) new InitialContext().lookup("java:module/EnderecoCorreioFacade");
            cidadeFacade = (CidadeFacade) new InitialContext().lookup("java:module/CidadeFacade");
            fichaFinanceiraFPFacade = (FichaFinanceiraFPFacade) new InitialContext().lookup("java:module/FichaFinanceiraFPFacade");
            registroESocialFacade = (RegistroESocialFacade) new InitialContext().lookup("java:module/RegistroESocialFacade");
            pessoaFisicaFacade = (PessoaFisicaFacade) new InitialContext().lookup("java:module/PessoaFisicaFacade");
            cboFacade = (CBOFacade) new InitialContext().lookup("java:module/CBOFacade");
            sindicatoFacade = (SindicatoFacade) new InitialContext().lookup("java:module/SindicatoFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    public void enviarS2200(ConfiguracaoEmpregadorESocial config, ContratoFP contratoFP, Date dataOperacao, AssistenteBarraProgresso assistenteBarraProgresso) {
        ValidacaoException val = new ValidacaoException();
        EventoS2200 s2200 = criarEventoS2200(config, contratoFP, val, dataOperacao);
        logger.debug("Antes de Enviar: " + s2200.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2200(s2200, assistenteBarraProgresso, contratoFP);
    }

    private String montarId(ContratoFP contratoFP) {
        List<RegistroESocial> registroESocials2200 = registroESocialFacade.buscarRegistroEsocialPorTipoAndIdentificador(TipoArquivoESocial.S2200, contratoFP.getId().toString(), null);
        String idEsocial = contratoFP.getId().toString();
        if (registroESocials2200 == null) {
            return idEsocial;
        }
        boolean s3000MaiorData = true;
        List<RegistroESocial> s3000 = registroESocialFacade.buscarRegistroEsocialPorTipoAndIdentificador(TipoArquivoESocial.S3000, contratoFP.getId().toString(), 1);
        if (s3000 != null) {
            for (RegistroESocial s2200 : registroESocials2200) {
                if ((s3000.get(0).getDataIntegracao().compareTo(s2200.getDataIntegracao()) <= 0)) {
                    s3000MaiorData = false;
                    break;
                }
            }
            if (s3000MaiorData) {
                idEsocial = S_3000 + s3000.get(0).getId() + contratoFP.getId().toString();
            }
        }
        return idEsocial;
    }

    private EventoS2200 criarEventoS2200(ConfiguracaoEmpregadorESocial config, ContratoFP contratoFP, ValidacaoException val, Date dataOperacao) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2200 eventoS2200 = (EventosESocialDTO.S2200) eSocialService.getEventoS2200(empregador);
        eventoS2200.setIdentificadorWP(contratoFP.getId().toString());
        eventoS2200.setClasseWP(ClasseWP.VINCULOFP);
        eventoS2200.setDescricao(contratoFP.toString());

        eventoS2200.setIdESocial(montarId(contratoFP));
        adicionarInformacoesBasicas(eventoS2200, contratoFP, val);
        adicionarInformacoesNascimento(eventoS2200, contratoFP, val);
        if (contratoFP.getMatriculaFP().getPessoa().getNacionalidade() != null && contratoFP.getMatriculaFP().getPessoa().getNacionalidade().getCodigoRaiz() == BRASILEIRO) {
            adicionarInformacoesEndereco(eventoS2200, contratoFP, val);
        }
        if (TipoRegime.CELETISTA.equals(contratoFP.getTipoRegime().getCodigo())) {
            adicionarInformacoesRegimeTrabalhistaCeletista(eventoS2200, contratoFP, val);
        } else {
            adicionarInformacoesRegimeEstatutario(eventoS2200, contratoFP, val, config);
        }
        if (!ModalidadeContratoFP.CONCURSADOS.equals(contratoFP.getModalidadeContratoFP().getCodigo())) {
            adicionarInformacoesContrato(eventoS2200, contratoFP, val);
            adicionarInformacoesRemuneracao(eventoS2200, contratoFP, dataOperacao);
        }

        adicionarInformacoesVinculo(eventoS2200, contratoFP, config, val);
        adicionarInformacoesCargo(eventoS2200, contratoFP, val, dataOperacao);
        adicionarInformacoesContratoTrabalhador(eventoS2200, contratoFP, val);
        adicionarInformacoesLocalTrabalho(eventoS2200, contratoFP, config, val);
        if (contratoFP.getCategoriaTrabalhador().getCodigo() == TEMPORARIO) {
            adicionarInformacoesEnderecoContratoTemporario(eventoS2200, contratoFP, val);
            adicionarInformacoesIdentificacaoEstabalecimentoTemporario(eventoS2200, contratoFP);
            adicionarInformacoesGeraisTrabalhadorTemporario(eventoS2200, contratoFP);
        }
        if (contratoFP.getCategoriaTrabalhador().getCodigo() == APRENDIZ) {
            adicionarInformacoesAprendiz(eventoS2200, contratoFP, val);
        }
        return eventoS2200;
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S2200 eventoS2200, ContratoFP contratoFP, ValidacaoException val) {
        PessoaFisica pessoaFisica = contratoFP.getMatriculaFP().getPessoa();
        pessoaFisica = pessoaFisicaFacade.recuperarComDocumentos(pessoaFisica.getId());
        eventoS2200.setCpfTrab(StringUtil.retornaApenasNumeros(pessoaFisica.getCpf()));
        String urlPessoa = "/pessoa/editar/" + pessoaFisica.getId();
        if (pessoaFisica.getCarteiraDeTrabalho() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoaFisica + " não possui carteira de trabalho. " + urlPessoa);
        } else {
            eventoS2200.setNisTrab(pessoaFisica.getCarteiraDeTrabalho().getPisPasep());
        }
        eventoS2200.setNmTrab(pessoaFisica.getNome());
        eventoS2200.setDtNascto(pessoaFisica.getDataNascimento());
        if (pessoaFisica.getSexo() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoaFisica + " não tem informações cadastradas sobre o Sexo." + urlPessoa);
        } else {
            eventoS2200.setSexo(pessoaFisica.getSexo().getSigla());
        }
        if (pessoaFisica.getRacaCor() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoaFisica + " não tem informações cadastradas sobre o Raça/Cor." + urlPessoa);
        }else {
            Date dataLimite = DataUtil.getDateParse("21/04/2024");
            Set<Integer> racaCorPermitidas = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            Integer racaCor = pessoaFisica.getRacaCor().getCodigoEsocial();

            if (!racaCorPermitidas.contains(racaCor) && (contratoFP.getInicioVigencia().after(dataLimite))) {
                val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoaFisica + " possui um código de Raça/Cor inválido. Se data de Início de Vigência do contrato  " +
                    "for igual ou posterior a 22/04/2024, " +
                    "não pode ser informado o valor [6]." +
                    "De acordo com as regras do eSocial, a partir de 22/04/2024, os valores permitidos são: " +
                    "1 - Branca, 2 - Preta, 3 - Parda, 4 - Amarela ou 5 - Indígena. " + urlPessoa);
            } else {
                eventoS2200.setRacaCor(racaCor);
            }
        }

        eventoS2200.setEstCiv(pessoaFisica.getEstadoCivil().getCodigoESocial());

        if (pessoaFisica.getNivelEscolaridade() == null || pessoaFisica.getNivelEscolaridade().getGrauEscolaridadeESocial() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoaFisica + " não possui nível de escolaridade cadastrado. " + urlPessoa);
        } else {
            eventoS2200.setGrauInstr(pessoaFisica.getNivelEscolaridade().getGrauEscolaridadeESocial().getCodigo());
        }

        if (contratoFP.getTipoOperacaoESocial() != null) {
            eventoS2200.setOperacao(TipoOperacaoESocial.valueOf(contratoFP.getTipoOperacaoESocial().name()));
        }

    }

    private void adicionarInformacoesNascimento(EventosESocialDTO.S2200 eventoS2200, ContratoFP
        contratoFP, ValidacaoException val) {
        String urlPessoa = "/pessoa/editar/" + contratoFP.getMatriculaFP().getPessoa().getId();
        if (contratoFP.getMatriculaFP().getPessoa().getNacionalidade() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + contratoFP.getMatriculaFP().getPessoa() + " não tem a Nacionalidade informada. " + urlPessoa);
        } else {
            if (contratoFP.getMatriculaFP().getPessoa().getNacionalidade().getCodigoRaiz() == 10) {
                if (contratoFP.getMatriculaFP().getPessoa().getNaturalidade() == null) {
                    val.adicionarMensagemDeOperacaoNaoRealizada("Servidor " + contratoFP + " sem cidade de Naturalidade cadastrada. " + urlPessoa);
                } else {
                    BigDecimal codigoIBGECidade = enderecoCorreioFacade.getCodigoIBGECidade(contratoFP.getMatriculaFP().getPessoa().getNaturalidade().getNome(),
                        contratoFP.getMatriculaFP().getPessoa().getNaturalidade().getUf().getUf());
                    if (codigoIBGECidade != null) {
                        if (codigoIBGECidade.toString().trim().length() != 7) {
                            val.adicionarMensagemDeOperacaoNaoRealizada("O código do IBGE da Cidade " + contratoFP.getMatriculaFP().getPessoa().getNaturalidade().getNome() + " está incorreto," +
                                " deve possuir 7 (sete) caracteres");
                        } else {
                            eventoS2200.setCodMunicNascto(codigoIBGECidade.intValue());
                        }
                    } else {
                        eventoS2200.setCodMunicNascto(COD_IBGE_MUNIC_RIO_BRANCO);
                    }
                }

                if (contratoFP.getMatriculaFP().getPessoa().getNaturalidade() == null || contratoFP.getMatriculaFP().getPessoa().getNaturalidade().getUf() == null) {
                    val.adicionarMensagemDeOperacaoNaoPermitida("Servidor(a)" + contratoFP + " não foi informado a Naturalidade. " + urlPessoa);
                } else {
                    eventoS2200.setUfNascto(contratoFP.getMatriculaFP().getPessoa().getNaturalidade().getUf().getUf());
                }
            }
            if (contratoFP.getMatriculaFP().getPessoa().getNacionalidade() != null && contratoFP.getMatriculaFP().getPessoa().getNacionalidade().getPais() != null &&
                contratoFP.getMatriculaFP().getPessoa().getNacionalidade().getPais().getCodigoBacen() != null) {
                eventoS2200.setPaisNascto(Util.cortarString(contratoFP.getMatriculaFP().getPessoa().getNacionalidade().getPais().getCodigoBacen().toString(), 3));
                eventoS2200.setPaisNac(Util.cortarString(contratoFP.getMatriculaFP().getPessoa().getNacionalidade().getPais().getCodigoBacen().toString(), 3));
            } else {
                val.adicionarMensagemDeOperacaoNaoPermitida("Servidor(a)" + contratoFP + " não tem na sua Nacionalidade e Pais o código bacen informado.");
            }

        }
    }

    private void adicionarInformacoesEndereco(EventosESocialDTO.S2200 eventoS2200, ContratoFP
        contratoFP, ValidacaoException ve) {
        EnderecoCorreio enderecoPrincipal = contratoFP.getMatriculaFP().getPessoa().getEnderecoPrincipal();
        String urlPessoa = "/pessoa/editar/" + contratoFP.getMatriculaFP().getPessoa().getId();
        if (enderecoPrincipal == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + contratoFP.getMatriculaFP().getPessoa() + " não possúi endereço. " + urlPessoa);
            return;
        }
        eventoS2200.setTpLogradBr(enderecoPrincipal.getTipoLogradouro() != null ? enderecoPrincipal.getTipoLogradouro().name() : "R");
        if (Strings.isNullOrEmpty(enderecoPrincipal.getLogradouro())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + contratoFP.getMatriculaFP().getPessoa() + " está sem descrição do logradouro. " + urlPessoa);
        } else {
            eventoS2200.setDscLogradBr(enderecoPrincipal.getLogradouro().trim());
        }
        if (Strings.isNullOrEmpty(enderecoPrincipal.getNumero())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + contratoFP.getMatriculaFP().getPessoa() + " está sem número. " + urlPessoa);
        } else {
            eventoS2200.setNrLogradBr(Util.cortarString(enderecoPrincipal.getNumero(), 10));
        }
        if (!Strings.isNullOrEmpty(enderecoPrincipal.getComplemento())) {
            if (enderecoPrincipal.getComplemento().length() < 2) {
                eventoS2200.setComplementoBr("Compl: " + enderecoPrincipal.getComplemento());
            } else {
                eventoS2200.setComplementoBr(Util.cortarString(enderecoPrincipal.getComplemento(), 30));
            }

        } else {
            eventoS2200.setComplementoBr(null);
        }
        eventoS2200.setBairroBr(!Strings.isNullOrEmpty(enderecoPrincipal.getBairro()) ? enderecoPrincipal.getBairro() : null);
        if (Strings.isNullOrEmpty(enderecoPrincipal.getCep()) || enderecoPrincipal.getCep().length() != 8) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + contratoFP.getMatriculaFP().getPessoa() + " está sem cep ou o cep esta inválido. " + urlPessoa);
        } else {

            eventoS2200.setCepBr(StringUtil.retornaApenasNumeros(enderecoPrincipal.getCep()));
        }

        if (Strings.isNullOrEmpty(enderecoPrincipal.getUf())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + contratoFP.getMatriculaFP().getPessoa() + " está sem UF. " + urlPessoa);
        } else {
            eventoS2200.setUfBr(enderecoPrincipal.getUf().trim());
        }
        if (enderecoPrincipal.getLocalidade() != null && enderecoPrincipal.getUf() != null) {
            BigDecimal codigoIBGECidade = enderecoCorreioFacade.getCodigoIBGECidade(enderecoPrincipal.getLocalidade(), enderecoPrincipal.getUf());
            if (codigoIBGECidade != null) {
                if (codigoIBGECidade.toString().trim().length() != 7) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada("O código do IBGE da Cidade " + enderecoPrincipal.getLocalidade() + " está incorreto," +
                        " deve possuir 7 (sete) caracteres");
                } else {
                    eventoS2200.setCodMunicBr(Integer.parseInt(codigoIBGECidade.toString()));
                }
            } else {
                eventoS2200.setCodMunicBr(COD_IBGE_MUNIC_RIO_BRANCO);
            }
        }
    }

    private void adicionarInformacoesVinculo(EventosESocialDTO.S2200 eventoS2200, ContratoFP
        contratoFP, ConfiguracaoEmpregadorESocial configuracao, ValidacaoException ve) {
        String matricula = registroESocialFacade.getMatriculaS2200ProcessadoSucesso(contratoFP);
        if (matricula == null) {
            String vinculo = StringUtil.cortarOuCompletarEsquerda(contratoFP.getNumero(), 2, "0");
            eventoS2200.setMatricula(contratoFP.getMatriculaFP().getMatricula().concat(vinculo));
        } else {
            eventoS2200.setMatricula(matricula);
        }
        eventoS2200.setTpRegTrab(contratoFP.getTipoRegime().getCodigo().intValue());
        eventoS2200.setTpRegPrev(contratoFP.getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getTipoRegimePrevidenciario().getCodigoTipoRegimeEsocial());
//        eventoS2200.setNrRecInfPrelim(""); // TODO VERIFICAR SE É NECESSÁRIO, NO MANUAL NÃO É OBRIGATÓRIO 111

        eventoS2200.setQtdHrsSem(new BigDecimal(contratoFP.getJornadaDeTrabalho().getHorasSemanal()));
        eventoS2200.setTpJornada(contratoFP.getJornadaDeTrabalho().getTipoJornadaTrabalho() != null ?
            contratoFP.getJornadaDeTrabalho().getTipoJornadaTrabalho().getCodigoESocial() : 4);
        eventoS2200.setTmpParc(contratoFP.getJornadaDeTrabalho().getTipoTempoParcial() != null ?
            contratoFP.getJornadaDeTrabalho().getTipoTempoParcial().getCodigoESocial() : 0);
        eventoS2200.setDscJorn(contratoFP.getJornadaDeTrabalho().toString());
        eventoS2200.setCadIni(definirCadIni(contratoFP, configuracao));
    }

    private void adicionarInformacoesCargo(EventosESocialDTO.S2200 eventoS2200, ContratoFP contratoFP, ValidacaoException ve, Date dataOperacao) {
        if (contratoFP.getCargo() != null) {
            eventoS2200.setNmCargo(contratoFP.getCargo().getDescricao());
            CBO cbo = null;
            ContratoFPCargo contratoFPCargo = contratoFP.getCargoVigente(dataOperacao);
            if (contratoFPCargo != null && contratoFPCargo.getCbo() != null) {
                cbo = contratoFPCargo.getCbo();
            } else {
                cbo = cboFacade.getCBOCargoOrdenadoPorCodigo(contratoFP.getCargo());
            }
            if (cbo == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O cargo " + contratoFP.getCargo() + " não possui CBO cadastrado.");
            } else {
                eventoS2200.setCBOCargo(cbo.getCodigo().toString());
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Servidor " + contratoFP.getCargo() + " não possui cargo cadastrado.");
        }
        eventoS2200.setAcumCargo(false);
    }


    private void adicionarInformacoesRegimeTrabalhistaCeletista(EventosESocialDTO.S2200 eventoS2200, ContratoFP
        contratoFP, ValidacaoException ve) {
        Date dataEmpregador = contratoFP.getInicioVigencia();
        eventoS2200.setDtAdm(dataEmpregador);
        eventoS2200.setTpAdmissao(1);
        eventoS2200.setIndAdmissao(1);
        eventoS2200.setNatAtividade(1);
        eventoS2200.setTpRegJor(1); // TODO temos duvida nessa informação campo 118

        Sindicato sindicato = null;
        if (contratoFP.getSindicatoVinculoFPVigente() != null && contratoFP.getSindicatoVinculoFPVigente().getSindicato() != null) {
            sindicato = contratoFP.getSindicatoVinculoFPVigente().getSindicato();
        } else {
            sindicato = sindicatoFacade.buscarSindicatoPeloCargo(contratoFP.getCargo());
        }

        if (sindicato == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Sindicato para o cargo: " + contratoFP.getCargo() + " do servidor(a) " + contratoFP);
            return;
        }

        if (sindicato != null) {
            if (sindicato.getPessoaJuridica() != null && Strings.isNullOrEmpty(sindicato.getPessoaJuridica().getCnpj())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O sindicato Servidor(a) " + contratoFP + " - sindicato + " + sindicato.getPessoaJuridica() + " não possui CNPJ cadastrado.");
            } else {
                eventoS2200.setCnpjSindCategProf(StringUtil.retornaApenasNumeros(sindicato.getPessoaJuridica().getCnpj()));
            }
        }
    }

    private void adicionarInformacoesRegimeEstatutario(EventosESocialDTO.S2200 eventoS2200, ContratoFP
        contratoFP, ValidacaoException ve, ConfiguracaoEmpregadorESocial configuracao) {
        Date dataEmpregador = contratoFP.getDataAdmissao();

        NaturezaJuridicaEntidade naturezaJuridicaEntidade = configuracao.getEntidade().getNaturezaJuridicaEntidade();

        if (ProvimentoEstatutarioEsocial.provimentoAdministracaoPublica().contains(contratoFP.getModalidadeContratoFP().getProvimentoEstatutarioEsocial())
            && naturezaJuridicaEntidade != null && !naturezaJuridicaEntidade.isNaturezaAdministracao()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O tipo de provimento relacionado a Modalidade de Contrato do servidor " + contratoFP + " só são permitidos se a natureza jurídica do declarante for Administração Pública (grupo [1]).");
        } else {
            eventoS2200.setTpProv(contratoFP.getModalidadeContratoFP().getProvimentoEstatutarioEsocial().getCodigo());
        }
        eventoS2200.setDtExercicio(contratoFP.getInicioVigencia());

        if (TipoRegimePrevidenciario.RPPS.equals(contratoFP.getPrevidenciaVinculoFPVigente().getTipoPrevidenciaFP().getTipoRegimePrevidenciario())) {
            eventoS2200.setTpPlanRP(contratoFP.getSegregacaoMassa() != null ? contratoFP.getSegregacaoMassa().getCodigo() : 0);
            eventoS2200.setIndTetoRGPS(contratoFP.getTetoRgps());
            eventoS2200.setIndAbonoPerm(contratoFP.getAbonoPermanencia());
        }

        if (contratoFP.getAbonoPermanencia() && !definirCadIni(contratoFP, configuracao)) {
            eventoS2200.setDtIniAbono(contratoFP.getInicioAbono());
        }
    }

    private boolean definirCadIni(ContratoFP contratoFP, ConfiguracaoEmpregadorESocial configuracao) {
        if (contratoFP.getCadastroInicialEsocial() != null) {
            if (VinculoFP.TipoCadastroInicialVinculoFP.SIM.equals(contratoFP.getCadastroInicialEsocial())) {
                return true;
            } else {
                return false;
            }
        } else {
            Date dataEmpregador = contratoFP.getDataAdmissao();

            if (configuracao.getDataInicioObrigatoriedadeFase2().compareTo(dataEmpregador) <= 0) {
                return false;
            } else {
                return true;
            }
        }
    }

    private void adicionarInformacoesContratoTrabalhador(EventosESocialDTO.S2200 eventoS2200, ContratoFP
        contratoFP, ValidacaoException ve) {
        if (contratoFP.getCategoriaTrabalhador() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi informado a categoria do trabalhador " + contratoFP + " em seu cadastro na aba 'e-social'");
        } else {
            if (contratoFP.getCategoriaTrabalhador().getCodigo() != EMPREGADO_CODIGO_TRABALHO_INTERMITENTE) {
                eventoS2200.setHorNoturno(false);
            }
            eventoS2200.setCodCateg(contratoFP.getCategoriaTrabalhador().getCodigo());
        }
    }

    private void adicionarInformacoesRemuneracao(EventosESocialDTO.S2200 eventoS2200, ContratoFP contratoFP, Date
        dataOperacao) {
        BigDecimal valorRemuneracao = fichaFinanceiraFPFacade.buscarValorBaseSalarial(contratoFP, dataOperacao);
        if (valorRemuneracao != null) {
            eventoS2200.setVrSalFx(valorRemuneracao);
        } else {
            eventoS2200.setVrSalFx(BigDecimal.ONE); //TODO REMOVER, SOMENTE PARA TESTE E VERIFICAR O PQ NAO TEM REMUNERAÇÃO
//            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi encontrado valor de remuneração para o contrato " + contratoFP);
        }
        eventoS2200.setUndSalFixo(5);
    }

    private void adicionarInformacoesContrato(EventosESocialDTO.S2200 eventoS2200, ContratoFP
        contratoFP, ValidacaoException ve) {
        if (contratoFP.getFinalVigencia() != null) {
            eventoS2200.setDtTerm(contratoFP.getFinalVigencia());
        }

        if (contratoFP.getModalidadeContratoFP() != null && contratoFP.getModalidadeContratoFP().getTipoPrazoContrato() != null) {
            eventoS2200.setTpContr(contratoFP.getModalidadeContratoFP().getTipoPrazoContrato().getCodigoESocial());
        } else {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Servidor(a) " + contratoFP + " não possui a Modalidade de Contrato configurado corretamente com o Prazo do Contrato.");
            ve.lancarException();
        }

        if (contratoFP.getCategoriaTrabalhador().getCodigo() != TEMPORARIO) {
            if (contratoFP.getModalidadeContratoFP().getTipoPrazoContrato() != null && TipoPrazoContrato.PRAZO_DETERMINADO.equals(contratoFP.getModalidadeContratoFP().getTipoPrazoContrato())) {
                eventoS2200.setClauAssec(false);
            }
        }
    }

    private void adicionarInformacoesLocalTrabalho(EventosESocialDTO.S2200 eventoS2200, ContratoFP
        contratoFP, ConfiguracaoEmpregadorESocial config, ValidacaoException ve) {
        eventoS2200.setTpInscLocalTrabGeral(1); //cnpj 165
        eventoS2200.setNrInscLocalTrabGeral((StringUtil.retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj().trim()))); //cnpj 165
    }

    private void adicionarInformacoesIdentificacaoEstabalecimentoTemporario(EventosESocialDTO.S2200 eventoS2200, ContratoFP
        contratoFP) {
        eventoS2200.setNrInscTomadorServ(StringUtil.retornaApenasNumeros(
            contratoFP.getUnidadeOrganizacional().getEntidade().getPessoaJuridica().getCnpj()));

        if (contratoFP.getTrabalhadorSubstituido() != null) {
            EventoS2200.IdeTrabSubstituido trabSubstituido = eventoS2200.addIdeTrabSubstituido();
            trabSubstituido.setCpfTrabSubst(StringUtil.retornaApenasNumeros(contratoFP.getTrabalhadorSubstituido().getCpf()));
        }
    }

    private void adicionarInformacoesGeraisTrabalhadorTemporario(EventosESocialDTO.S2200 eventoS2200, ContratoFP
        contratoFP) {
        if (!Strings.isNullOrEmpty(contratoFP.getJustificativaTrabalhadorTemp())) {
            eventoS2200.setJustContr(contratoFP.getJustificativaTrabalhadorTemp());
        }
        if (!Strings.isNullOrEmpty(contratoFP.getObjetoContratacao())) {
            eventoS2200.setObjDet(contratoFP.getObjetoContratacao());
        }
    }

    private void adicionarInformacoesAprendiz(EventosESocialDTO.S2200 eventoS2200, ContratoFP contratoFP, ValidacaoException ve) {
        String urlContrato = "/contratofp/editar/" + contratoFP.getId();
        if (contratoFP.getModalidadeContrAprendiz() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O contrato " + contratoFP.getMatriculaFP().getPessoa() + " não possúi Modalidade de contratação do aprendiz na aba Esocial " + urlContrato);
        } else {
            eventoS2200.setIndAprend(contratoFP.getModalidadeContrAprendiz().getCodigo());
        }
        if (ModalidadeContratacaoAprendiz.CONTRATACAO_INDIRETA.equals(contratoFP.getModalidadeContrAprendiz())) {
            if (contratoFP.getEstabelecContratAprendiz() == null) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("O contrato " + contratoFP.getMatriculaFP().getPessoa() + " não possúi Estabelecimento para o qual a contratação de aprendiz foi efetivada na aba Esocial " + urlContrato);
            } else {
                Integer tpInscricao = contratoFP.getEstabelecContratAprendiz() instanceof PessoaJuridica ? 1 : 2;
                eventoS2200.setTpInscAprend(tpInscricao);
                if (contratoFP.getEstabelecContratAprendiz().getCpf_Cnpj() == null) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada("O contrato " + contratoFP.getMatriculaFP().getPessoa() + " possúi o Estabelecimento para o qual a contratação de aprendiz foi efetivada sem CPF/CNPJ na aba Esocial " + urlContrato);
                } else {
                    eventoS2200.setNrInscAprend(StringUtil.retornaApenasNumeros(contratoFP.getEstabelecContratAprendiz().getCpf_Cnpj()));
                }
            }
            if (contratoFP.getEstabelecAtividadpraticas() != null && !Strings.isNullOrEmpty(contratoFP.getEstabelecAtividadpraticas().getCnpj())) {
                eventoS2200.setCnpjPratAprend(StringUtil.retornaApenasNumeros(contratoFP.getEstabelecAtividadpraticas().getCnpj()));
            }
        } else {
            eventoS2200.setCnpjEntQualAprend(StringUtil.retornaApenasNumeros(contratoFP.getEntidadeQualificadora().getCnpj()));
        }
    }

    private void adicionarInformacoesEnderecoContratoTemporario(EventosESocialDTO.S2200 eventoS2200, ContratoFP
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
            eventoS2200.setDscLogradLocalTempDom(enderecoPrincipal.getLogradouro().trim());
        }
        if (Strings.isNullOrEmpty(enderecoPrincipal.getNumero())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + contratoFP.getMatriculaFP().getPessoa() + " está sem número. " + urlPessoa);
        } else {
            eventoS2200.setNrLogradLocalTempDom(Util.cortarString(enderecoPrincipal.getNumero(), 10));
        }
        if (!Strings.isNullOrEmpty(enderecoPrincipal.getComplemento())) {
            if (enderecoPrincipal.getComplemento().length() < 2) {
                eventoS2200.setComplementoLocalTempDom("Compl: " + enderecoPrincipal.getComplemento());
            } else {
                eventoS2200.setComplementoLocalTempDom(Util.cortarString(enderecoPrincipal.getComplemento(), 30));
            }

        } else {
            eventoS2200.setComplementoLocalTempDom(null);
        }
        eventoS2200.setBairroLocalTempDom(!Strings.isNullOrEmpty(enderecoPrincipal.getBairro()) ? enderecoPrincipal.getBairro() : null);
        if (Strings.isNullOrEmpty(enderecoPrincipal.getCep()) || enderecoPrincipal.getCep().length() != 8) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + contratoFP.getMatriculaFP().getPessoa() + " está sem cep ou o cep esta inválido. " + urlPessoa);
        } else {

            eventoS2200.setCepLocalTempDom(StringUtil.retornaApenasNumeros(enderecoPrincipal.getCep()));
        }

        if (Strings.isNullOrEmpty(enderecoPrincipal.getUf())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + contratoFP.getMatriculaFP().getPessoa() + " está sem UF. " + urlPessoa);
        } else {
            eventoS2200.setUFLocalTempDom(enderecoPrincipal.getUf().trim());
        }
        if (enderecoPrincipal.getLocalidade() != null && enderecoPrincipal.getUf() != null) {
            BigDecimal codigoIBGECidade = enderecoCorreioFacade.getCodigoIBGECidade(enderecoPrincipal.getLocalidade(), enderecoPrincipal.getUf());
            if (codigoIBGECidade != null) {
                if (codigoIBGECidade.toString().trim().length() != 7) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada("O código do IBGE da Cidade " + enderecoPrincipal.getLocalidade() + " está incorreto," +
                        " deve possuir 7 (sete) caracteres");
                } else {
                    eventoS2200.setCodMunicLocalTempDom(Integer.parseInt(codigoIBGECidade.toString()));
                }
            } else {
                eventoS2200.setCodMunicLocalTempDom(COD_IBGE_MUNIC_RIO_BRANCO);
            }
        }
    }


    @Transactional
    public List<RegistroESocial> buscarRegistrosPorEmpregadorAndSituacao(Long idEmpregador, TipoArquivoESocial
        tipoArquivoESocial, SituacaoESocial situacao) {
        Query query = entityManager.createNativeQuery("select ev.* from RegistroESocial ev " +
            " where ev.EMPREGADOR_ID= :empregador " +
            "       and ev.SITUACAO = :situacao" +
            "       and ev.tipoArquivoESocial = :tipoArquivo ", RegistroESocial.class);
        query.setParameter("empregador", idEmpregador);
        query.setParameter("situacao", situacao.name());
        query.setParameter("tipoArquivo", tipoArquivoESocial.name());
        return query.getResultList();
    }

    @Transactional
    public Sindicato buscarSindicatoPeloCargo(Cargo cargo) {
        Query query = entityManager.createNativeQuery("select  sindicato.* from ItemCargoSindicato item " +
            " inner join sindicato sindicato on item.sindicato_id = sindicato.id " +
            " where sysdate between item.DATAINICIO and coalesce(item.datafim, sysdate)" +
            " and item.CARGO_ID = :cargo", Sindicato.class);
        query.setParameter("cargo", cargo.getId());
        if (!query.getResultList().isEmpty()) {
            return (Sindicato) query.getResultList().get(0);
        }
        return null;
    }
}
