package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2405;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.math.BigDecimal;
import java.util.*;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

/**
 * Created by William on 04/12/2018.
 */
@Service(value = "s2405Service")
public class S2405Service {

    public static final int COD_IBGE_MUNIC_RIO_BRANCO = 1200401;
    public static final Integer CODIGO_BRASIL_BACEN = 105;
    protected static final Logger logger = LoggerFactory.getLogger(S2405Service.class);
    @Autowired
    private ESocialService eSocialService;
    private EnderecoCorreioFacade enderecoCorreioFacade;
    private DependenteFacade dependenteFacade;
    private DependenteVinculoFPFacade dependenteVinculoFPFacade;
    private AposentadoriaFacade aposentadoriaFacade;
    private PensionistaFacade pensionistaFacade;

    @PostConstruct
    public void init() {
        try {
            enderecoCorreioFacade = (EnderecoCorreioFacade) new InitialContext().lookup("java:module/EnderecoCorreioFacade");
            dependenteFacade = (DependenteFacade) new InitialContext().lookup("java:module/DependenteFacade");
            dependenteVinculoFPFacade = (DependenteVinculoFPFacade) new InitialContext().lookup("java:module/DependenteVinculoFPFacade");
            aposentadoriaFacade = (AposentadoriaFacade) new InitialContext().lookup("java:module/AposentadoriaFacade");
            pensionistaFacade = (PensionistaFacade) new InitialContext().lookup("java:module/PensionistaFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    public ESocialService geteSocialService() {
        return eSocialService;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void enviarS2405(ConfiguracaoEmpregadorESocial config, VinculoFP vinculofp) {
        ValidacaoException val = new ValidacaoException();
        verificarEnventoInicialNaoEnviado(vinculofp, val);
        val.lancarException();
        EventoS2405 s2405 = criarEventoS2405(config, vinculofp, val);
        logger.debug("Antes de Enviar: " + s2405.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2405(s2405);

    }

    private void verificarEnventoInicialNaoEnviado(VinculoFP vinculoFP, ValidacaoException val) {
        List<EventoESocialDTO> eventosPorIdEsocial = eSocialService.getEventosPorIdEsocial(vinculoFP.getId() + "");
        for (EventoESocialDTO eventoESocialDTO : eventosPorIdEsocial) {
            SituacaoESocial situacao = eventoESocialDTO.getSituacao();
            if (TipoArquivoESocial.S2400.equals(eventoESocialDTO.getTipoArquivo()) && SituacaoESocial.PROCESSADO_COM_SUCESSO.equals(situacao)) {
                return;
            }
        }
        val.adicionarMensagemDeOperacaoNaoRealizada("Não foi enviado o evento S-2405. Não foi identificado registro de envio do beneficiário pelo evento S-2400");
    }

    private EventoS2405 criarEventoS2405(ConfiguracaoEmpregadorESocial config, VinculoFP vinculofp, ValidacaoException ve) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2405 eventoS2405 = (EventosESocialDTO.S2405) eSocialService.getEventoS2405(empregador);
        eventoS2405.setIdentificadorWP(vinculofp.getId().toString());
        eventoS2405.setClasseWP(ClasseWP.VINCULOFP);
        eventoS2405.setDescricao(vinculofp.toString());

        eventoS2405.setIdESocial(vinculofp.getId().toString());
        adicionarInformacoesBasicas(eventoS2405, vinculofp, ve);
        adicionarInformacoesEnderecoNacional(eventoS2405, vinculofp, ve);
  //      adicionarInformacoesEnderecoExterior(eventoS2405, vinculofp, ve);
        adicionarDependentes(eventoS2405, vinculofp, ve);
        return eventoS2405;
    }

    private void adicionarDependentes(EventosESocialDTO.S2405 eventoS2405, VinculoFP vinculoFP, ValidacaoException ve) {
        List<Dependente> dependenteList = dependenteFacade.getDependentesDe(vinculoFP.getMatriculaFP().getPessoa(), new Date());
        for (Dependente dependenteWeb : dependenteList) {
            EventoS2405.Dependente depeEsocial = eventoS2405.addDependente();
            PessoaFisica dependenteFP = dependenteWeb.getDependente();
            if (dependenteFP != null && dependenteFP.getCpf() != null) {
                String cpfNumerico = StringUtil.retornaApenasNumeros(dependenteFP.getCpf());
                if (cpfNumerico.length() != 11 || !cpfNumerico.matches("\\d{11}")) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa " + vinculoFP.getMatriculaFP().getPessoa() + " o dependente não possui um CPF válido. O CPF deve ter exatamente 11 dígitos numéricos."
                        + " - Nome: "+ dependenteFP.getNome()+ " CPF: " + dependenteFP.getCpf() );
                } else {
                    depeEsocial.setCpfDep(cpfNumerico);
                }
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa " + vinculoFP.getMatriculaFP().getPessoa() + " o dependente não possui CPF informado.");
            }
            if(dependenteFP.getDataNascimento() ==null ){
                ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa " + vinculoFP.getMatriculaFP().getPessoa() + " é obrigatório a data de nascimento.");
            } else {
                depeEsocial.setDtNascto(dependenteFP.getDataNascimento());
            }
            if(dependenteFP.getNome() == null || !validarNomeDependente(dependenteFP.getNome())){
                ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa " + vinculoFP.getMatriculaFP().getPessoa() + " é obrigatório o Nome do dependente com 2 a 70 caracteres e não pode ser apenas espaços.");
            }else {
                depeEsocial.setNmDep(dependenteFP.getNome());
            }
            if (dependenteFP.getSexo() != null) {
                depeEsocial.setSexoDep(dependenteFP.getSexo().getSigla());
            } else{
                ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa " + vinculoFP.getMatriculaFP().getPessoa() + " não tem informação do Sexo do Dependente " + (dependenteFP.getNome() != null? dependenteFP.getNome():""));
            }
            if (dependenteWeb.getGrauDeParentesco() != null) {
                depeEsocial.setTpDep(dependenteWeb.getGrauDeParentesco().getCodigoEsocial());
            }
            depeEsocial.setDepIRRF(dependenteVinculoFPFacade.hasDependenteIRRF(dependenteWeb) != null);
            depeEsocial.setIncFisMenDep(dependenteFP.getDeficienteFisico() != null ? dependenteFP.getDeficienteFisico() : false);
        }

    }

    public boolean validarNomeDependente(String nome) {
        if (nome == null) {
            return false;
        }
        nome = nome.trim();
        if (nome.length() < 2 || nome.length() > 70) {
            return false;
        }
        if (!nome.matches(".*\\S.*")) {
            return false;
        }
        return true;
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S2405 eventoS2405, VinculoFP vinculoFP, ValidacaoException ve) {
        PessoaFisica pessoaFisica = vinculoFP.getMatriculaFP().getPessoa();
        String urlPessoa = "/pessoa/editar/" + pessoaFisica.getId();

        eventoS2405.setCpfBenef(StringUtil.retornaApenasNumeros(vinculoFP.getMatriculaFP().getPessoa().getCpf()));
        eventoS2405.setNmBenefic(vinculoFP.getMatriculaFP().getPessoa().getNome());
        eventoS2405.setSexo(vinculoFP.getMatriculaFP().getPessoa().getSexo() != null ? vinculoFP.getMatriculaFP().getPessoa().getSexo().getSigla() : null);
        eventoS2405.setEstCiv(vinculoFP.getMatriculaFP().getPessoa().getEstadoCivil() != null ? vinculoFP.getMatriculaFP().getPessoa().getEstadoCivil().getCodigoESocial() : null);
        eventoS2405.setIncFisMen("N");

        if (vinculoFP instanceof Aposentadoria) {
            Aposentadoria aposentadoria = (Aposentadoria) vinculoFP;
            aposentadoria = aposentadoriaFacade.recuperar(aposentadoria.getId());
            List<InvalidezAposentado> invalidezAposentados = aposentadoria.getInvalidezAposentados();

            if (!invalidezAposentados.isEmpty()) {
                for (InvalidezAposentado invalidezAposentado : invalidezAposentados) {
                    if (invalidezAposentado.getIsentoPrevidencia()) {
                        eventoS2405.setIncFisMen("S");
                        break;
                    }
                }
            }
        } if (vinculoFP instanceof Pensionista) {
            Pensionista pensionista = (Pensionista) vinculoFP;
            pensionista = pensionistaFacade.recuperar(pensionista.getId());
            List<InvalidezPensao> invalidezPensoes = pensionista.getListaInvalidez();

            if (!invalidezPensoes.isEmpty()) {
                for (InvalidezPensao invalidezPensao : invalidezPensoes) {
                    if (invalidezPensao.getIsentoPrevidencia()) {
                        eventoS2405.setIncFisMen("S");
                        break;
                    }
                }
            }
        }

        Date dtAlteracao = new Date();
        eventoS2405.setDtAlteracao(dtAlteracao);


        if (vinculoFP.getMatriculaFP().getPessoa().getRacaCor() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + vinculoFP.getMatriculaFP().getPessoa() + " não tem informações cadastradas sobre o Raça/Cor." + urlPessoa);
        } else {
            Date dataLimite = DataUtil.getDateParse("21/04/2024");
            Set<Integer> racaCorPermitidas = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            Integer racaCor = pessoaFisica.getRacaCor().getCodigoEsocial();

            if (!racaCorPermitidas.contains(racaCor) && (dtAlteracao.after(dataLimite))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa " + pessoaFisica + " possui um código de Raça/Cor inválido. Se data de alteração for igual ou posterior a 22/04/2024, " +
                    "não pode ser informado o valor [6]." +
                    "De acordo com as regras do eSocial, a partir de 22/04/2024, os valores permitidos são: " +
                    "1 - Branca, 2 - Preta, 3 - Parda, 4 - Amarela ou 5 - Indígena. " + urlPessoa);
            } else {
                eventoS2405.setRacaCor(vinculoFP.getMatriculaFP().getPessoa().getRacaCor().getCodigoEsocial());
            }
        }
    }

    private void adicionarInformacoesEnderecoNacional(EventosESocialDTO.S2405 eventoS2405, VinculoFP vinculoFP, ValidacaoException ve) {
        EnderecoCorreio enderecoPrincipal = vinculoFP.getMatriculaFP().getPessoa().getEnderecoPrincipal();
        if (enderecoPrincipal == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + vinculoFP.getMatriculaFP().getPessoa() + " não possui endereço.");
            return;
        }
        eventoS2405.setTpLogradBr(enderecoPrincipal.getTipoLogradouro() != null ? enderecoPrincipal.getTipoLogradouro().name() : "R");
        if (enderecoPrincipal.getLogradouro() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + vinculoFP.getMatriculaFP().getPessoa() + " não possui descrição de logradouro.");
        } else {
            eventoS2405.setDscLogradBr(enderecoPrincipal.getLogradouro());
        }
        if (StringUtils.isBlank(enderecoPrincipal.getNumero())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa - " + vinculoFP.getMatriculaFP().getPessoa() + " ' - é necessário informar o número do logradouro.");
        }
        String numeroLogradouro;
        if (!Strings.isNullOrEmpty(enderecoPrincipal.getNumero())) {
            numeroLogradouro = Util.cortarString(enderecoPrincipal.getNumero(), 10);
            eventoS2405.setNrLogradBr(numeroLogradouro);
        }

        String complemento;
        if (!Strings.isNullOrEmpty(enderecoPrincipal.getComplemento())) {
            complemento = Util.cortarString(enderecoPrincipal.getComplemento(), 30);
            eventoS2405.setComplementoBr(complemento);
        } else {
            eventoS2405.setComplementoBr(null);
        }



        eventoS2405.setBairroBr(!Strings.isNullOrEmpty(enderecoPrincipal.getBairro()) ? enderecoPrincipal.getBairro() : null);
        String cep = StringUtil.retornaApenasNumeros(enderecoPrincipal.getCep());
        if (cep != null && cep.matches("\\d{8}")) {
            eventoS2405.setCepBr(cep);
        } else {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa - " + vinculoFP.getMatriculaFP().getPessoa() + " ' O CEP fornecido não está no formato válido. O CEP deve conter exatamente 8 dígitos numéricos.");
        }
        if (enderecoPrincipal.getLocalidade() != null && enderecoPrincipal.getUf() != null) {
            BigDecimal codigoIBGECidade = enderecoCorreioFacade.getCodigoIBGECidade(enderecoPrincipal.getLocalidade(), enderecoPrincipal.getUf());
            if (codigoIBGECidade != null) {
                eventoS2405.setCodMunicBr(Integer.parseInt(codigoIBGECidade.toString()));
            } else {
                eventoS2405.setCodMunicBr(COD_IBGE_MUNIC_RIO_BRANCO);
            }
            eventoS2405.setUfBr(enderecoPrincipal.getUf().trim());

        }
    }

    private static String getLogradouro(EnderecoCorreio enderecoPrincipal) {
        return enderecoPrincipal.getLogradouro().replaceAll("^ *", "");
    }

    private void adicionarInformacoesEnderecoExterior(EventosESocialDTO.S2405 eventoS2405, VinculoFP vinculoFP, ValidacaoException ve) {
        PessoaFisica pessoa = vinculoFP.getMatriculaFP().getPessoa();
        if (pessoa.getNacionalidade() != null && pessoa.getNacionalidade().getPais().getCodigoBacen().equals(CODIGO_BRASIL_BACEN)) {
            return;
        }
        EnderecoCorreio enderecoPrincipal = pessoa.getEnderecoPrincipal();
        if (enderecoPrincipal == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoa + " não possui endereço.");
            return;
        }
        eventoS2405.setNmCidEx(enderecoPrincipal.getLocalidade() != null ? enderecoPrincipal.getLocalidade() : null);
        eventoS2405.setPaisResidEx(pessoa.getNacionalidade() != null ? pessoa.getNacionalidade().getPais().getCodigoBacen() + "" : null);
        eventoS2405.setDscLogradEx(enderecoPrincipal.getLogradouro() != null ? getLogradouro(enderecoPrincipal) : null);
        eventoS2405.setNrLogradEx(enderecoPrincipal.getNumero());
        eventoS2405.setComplementoEx(!Strings.isNullOrEmpty(enderecoPrincipal.getComplemento()) ? enderecoPrincipal.getComplemento() : null);
        eventoS2405.setBairroEx(!Strings.isNullOrEmpty(enderecoPrincipal.getBairro()) ? enderecoPrincipal.getBairro() : null);
        eventoS2405.setCodPostalEx(StringUtil.retornaApenasNumeros(enderecoPrincipal.getCep()));

    }

}
