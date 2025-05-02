package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.TipoPensao;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2410;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.EnderecoCorreioFacade;
import br.com.webpublico.negocios.RegistroDeObitoFacade;
import br.com.webpublico.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s2410Service")
public class S2410Service {

    public static final int COD_IBGE_MUNIC_RIO_BRANCO = 1200401;
    protected static final Logger logger = LoggerFactory.getLogger(S2410Service.class);
    public static final String OBITO = "01";
    public static final String TERMINO_PRAZO_BENEFICIO = "05";
    @Autowired
    private ESocialService eSocialService;
    private EnderecoCorreioFacade enderecoCorreioFacade;
    private RegistroDeObitoFacade registroDeObitoFacade;

    @PostConstruct
    public void init() {
        try {
            enderecoCorreioFacade = (EnderecoCorreioFacade) new InitialContext().lookup("java:module/EnderecoCorreioFacade");
            registroDeObitoFacade = (RegistroDeObitoFacade) new InitialContext().lookup("java:module/RegistroDeObitoFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    public void enviarS2410(ConfiguracaoEmpregadorESocial config, VinculoFP vinculofp) {
        ValidacaoException val = new ValidacaoException();
        EventoS2410 s2410 = criarEventoS2410(config, vinculofp, val);
        logger.debug("Antes de Enviar: " + s2410.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2410(s2410);
    }

    private EventoS2410 criarEventoS2410(ConfiguracaoEmpregadorESocial config, VinculoFP vinculofp, ValidacaoException ve) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2410 eventoS2410 = (EventosESocialDTO.S2410) eSocialService.getEventoS2410(empregador);
        eventoS2410.setIdentificadorWP(vinculofp.getId().toString());
        eventoS2410.setClasseWP(ClasseWP.VINCULOFP);
        eventoS2410.setDescricao(vinculofp.toString());

        if (vinculofp.getTipoOperacaoESocial() != null) {
            eventoS2410.setOperacao(TipoOperacaoESocial.valueOf(vinculofp.getTipoOperacaoESocial().name()));
        }
        eventoS2410.setIdESocial(vinculofp.getId().toString());

        adicionarBeneficario(eventoS2410, vinculofp, empregador);
        adicionarInfoBenInicio(eventoS2410, vinculofp, empregador);
        adicionarDadosBeneficio(eventoS2410, vinculofp, ve);
        adicionarInfoPenMorte(eventoS2410, vinculofp, empregador);
        adicionarInstPenMorte(eventoS2410, vinculofp, ve);
        adicionarSucessaoBenef(eventoS2410, vinculofp, ve);
        adicionarMudanaCPF(eventoS2410, vinculofp, ve);
        adicionarInfoBenTermino(eventoS2410, vinculofp,empregador, ve);
        return eventoS2410;
    }

    private void adicionarBeneficario(EventosESocialDTO.S2410 eventoS2410, VinculoFP vinculfp, EmpregadorESocial empregador) {
        eventoS2410.setCpfBenef(StringUtil.retornaApenasNumeros(vinculfp.getMatriculaFP().getPessoa().getCpf()));
        eventoS2410.setMatricula(vinculfp.getMatriculaFP().getMatricula());
        eventoS2410.setCnpjOrigem(retornaApenasNumeros(empregador.getInfoCadastro().getNrInsc()));

    }

    private void adicionarInfoBenInicio(EventosESocialDTO.S2410 eventoS2410, VinculoFP vinculofp, EmpregadorESocial empregador) {

        boolean cadIni = vinculofp.getInicioVigencia().before(empregador.getDataInicioFase2());
        eventoS2410.setCadIni(cadIni);
        if (!cadIni) {
            eventoS2410.setIndSitBenef(1);
        }
        eventoS2410.setNrBeneficio(vinculofp.getMatriculaFP().getMatricula() + StringUtil.cortarOuCompletarEsquerda(vinculofp.getNumero(), 2, "0"));
        eventoS2410.setDtIniBeneficio(vinculofp.getInicioVigencia());
        //eventoS2410.setDtPublic();

    }

    private void adicionarDadosBeneficio(EventosESocialDTO.S2410 eventoS2410, VinculoFP vinculofp, ValidacaoException ve) {
        if (vinculofp instanceof Aposentadoria) {
            if (((Aposentadoria) vinculofp).getTipoBeneficioEsocial() != null) {
                eventoS2410.setTpBeneficio(((Aposentadoria) vinculofp).getTipoBeneficioEsocial().getCodigo()); //TODO tabela 25
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Preencha o campo Tipo Benefício Esocial no cadastro de Aposentadoria. Vinculo: " + vinculofp);
            }

        }
        if (vinculofp instanceof Pensionista) {
            eventoS2410.setTpBeneficio(((Pensionista) vinculofp).getTipoFundamentacao().getCodigoEsocial()); //TODO tabela 25
        }
        eventoS2410.setTpPlanRP(0);
        //eventoS2410.setDsc();
        if (vinculofp instanceof Beneficiario) {
            eventoS2410.setIndDecJud(true);
            eventoS2410.setDsc("Beneficiário de Pensão Transitada e Julgada");
        } else {
            eventoS2410.setIndDecJud(false);
        }

    }


    private void adicionarInfoPenMorte(EventosESocialDTO.S2410 eventoS2410, VinculoFP vinculofp, EmpregadorESocial empregador) {
        if (vinculofp instanceof Pensionista) {
            eventoS2410.setTpPenMorte(isVitalicia(((Pensionista) vinculofp).getTipoPensao()) ? 1 : 2);
        }
    }

    private void adicionarInstPenMorte(EventosESocialDTO.S2410 eventoS2410, VinculoFP vinculofp, ValidacaoException ve) {
        if (vinculofp instanceof Pensionista) {
            eventoS2410.setCpfInst(retornaApenasNumeros(((Pensionista) vinculofp).getPensao().getContratoFP().getMatriculaFP().getPessoa().getCpf()));
            RegistroDeObito registroDeObito = registroDeObitoFacade.buscarRegistroDeObitoPorPessoaFisica(((Pensionista) vinculofp).getPensao().getContratoFP().getMatriculaFP().getPessoa());
            if (registroDeObito != null) {
                eventoS2410.setDtInst(registroDeObito.getDataFalecimento());
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não informado a data de óbito do instituidor da pensão por morte. Vinculo: " + vinculofp);
            }
        }

    }

    private void adicionarSucessaoBenef(EventosESocialDTO.S2410 eventoS2410, VinculoFP vinculofp, ValidacaoException ve) {
        //TODO sucessaoBenef
//        eventoS2410.setCnpjOrgaoAnt();
//        eventoS2410.setNrBeneficioAnt();
//        eventoS2410.setDtTransf();
//        eventoS2410.setObservacao();

    }


    private void adicionarMudanaCPF(EventosESocialDTO.S2410 eventoS2410, VinculoFP vinculofp, ValidacaoException ve) {
        //TODO mudanca de cpf
//        eventoS2410.setCpfAnt();
//        eventoS2410.setNrBeneficioAntMud();
//        eventoS2410.setDtAltCPF();
//        eventoS2410.setObservacaoMud();
    }


    private void adicionarInfoBenTermino(EventosESocialDTO.S2410 eventoS2410, VinculoFP vinculofp, EmpregadorESocial empregador, ValidacaoException ve) {
        if (eventoS2410.getCadIni()) {
            if (vinculofp.getFinalVigencia() != null) {
                if (vinculofp.getFinalVigencia().before(empregador.getDataInicioFase2())) {
                    eventoS2410.setDtTermBeneficio(vinculofp.getFinalVigencia());
                    RegistroDeObito registroDeObito = registroDeObitoFacade.buscarRegistroDeObitoPorPessoaFisica(((Pensionista) vinculofp).getPensao().getContratoFP().getMatriculaFP().getPessoa());
                    if (registroDeObito != null) {
                        eventoS2410.setMtvTermino(OBITO);
                    } else {
                        eventoS2410.setMtvTermino(TERMINO_PRAZO_BENEFICIO);
                    }
                }
            }
        } else {
            if (vinculofp.getFinalVigencia() != null) {
                eventoS2410.setDtTermBeneficio(vinculofp.getFinalVigencia());
                RegistroDeObito registroDeObito = registroDeObitoFacade.buscarRegistroDeObitoPorPessoaFisica(((Pensionista) vinculofp).getPensao().getContratoFP().getMatriculaFP().getPessoa());
                if (registroDeObito != null) {
                    eventoS2410.setMtvTermino(OBITO);
                } else {
                    eventoS2410.setMtvTermino(TERMINO_PRAZO_BENEFICIO);
                }
            }
        }
    }

    private boolean isVitalicia(TipoPensao tipoPensao) {
        if (TipoPensao.VITALICIA.equals(tipoPensao) || TipoPensao.VITALICIA_INVALIDEZ.equals(tipoPensao)) {
            return true;
        }
        return false;
    }

}
