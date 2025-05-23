package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2400;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AposentadoriaFacade;
import br.com.webpublico.negocios.EnderecoCorreioFacade;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.*;
import java.math.BigDecimal;
import java.util.List;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

/**
 * Created by William on 04/12/2018.
 */
@Service(value = "s2400Service")
public class S2400Service {

    public static final int COD_IBGE_MUNIC_RIO_BRANCO = 1200401;
    protected static final Logger logger = LoggerFactory.getLogger(S2400Service.class);
    @Autowired
    private ESocialService eSocialService;
    private EnderecoCorreioFacade enderecoCorreioFacade;

    private AposentadoriaFacade aposentadoriaFacade;

    @PostConstruct
    public void init() {
        try {
            enderecoCorreioFacade = (EnderecoCorreioFacade) new InitialContext().lookup("java:module/EnderecoCorreioFacade");
            aposentadoriaFacade = (AposentadoriaFacade) new InitialContext().lookup("java:module/AposentadoriaFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void enviarS2400(ConfiguracaoEmpregadorESocial config, VinculoFP vinculoFP) {
        ValidacaoException val = new ValidacaoException();
        EventoS2400 s2400 = criarEventoS2400(config, vinculoFP, val);
        if (StringUtils.isBlank(s2400.getXml())) {
            logger.debug("Antes de Enviar: " + s2400.getXml());
            logger.error("Antes de Enviar: " + s2400.getXml());
        }
        val.lancarException();
        eSocialService.enviarEventoS2400(s2400);
    }

    private EventoS2400 criarEventoS2400(ConfiguracaoEmpregadorESocial config, VinculoFP vinculoFP, ValidacaoException ve) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2400 eventoS2400 = (EventosESocialDTO.S2400) eSocialService.getEventoS2400(empregador);
        eventoS2400.setIdentificadorWP(vinculoFP.getId().toString());
        eventoS2400.setClasseWP(ClasseWP.VINCULOFP);
        eventoS2400.setDescricao(vinculoFP.toString());

        eventoS2400.setIdESocial(vinculoFP.getId().toString());
        adicionarInformacoesBasicas(eventoS2400, vinculoFP, config, ve);
        adicionarInformacoesEndereco(eventoS2400, vinculoFP, ve);
        return eventoS2400;
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S2400 eventoS2400, VinculoFP vinculoFP, ConfiguracaoEmpregadorESocial config, ValidacaoException ve) {
        PessoaFisica pessoaFisica = vinculoFP.getMatriculaFP().getPessoa();
        String urlPessoa = "/pessoa/editar/" + pessoaFisica.getId();

        eventoS2400.setCpfBenef(retornaApenasNumeros(vinculoFP.getMatriculaFP().getPessoa().getCpf()));
        eventoS2400.setNmBenefic(vinculoFP.getMatriculaFP().getPessoa().getNome());
        eventoS2400.setDtNasctoBenefic(vinculoFP.getMatriculaFP().getPessoa().getDataNascimento());
        eventoS2400.setSexo(vinculoFP.getMatriculaFP().getPessoa().getSexo().getSigla());
        if (vinculoFP.getTipoOperacaoESocial() != null) {
            eventoS2400.setOperacao(TipoOperacaoESocial.valueOf(vinculoFP.getTipoOperacaoESocial().name()));
        }

        Date dtInicio = null;

        if (config.getDataInicioObrigatoriedadeS2400() != null) {
            if (config.getDataInicioObrigatoriedadeS2400().compareTo(vinculoFP.getInicioVigencia()) > 0) {
                dtInicio = config.getDataInicioObrigatoriedadeS2400();
            } else {
                dtInicio = vinculoFP.getInicioVigencia();
            }
            eventoS2400.setDtInicio(dtInicio);
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informado a data de obrigatoriedade do evento S-2400 na configuração do empregador.");
        }

        if (vinculoFP.getMatriculaFP().getPessoa().getRacaCor() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + vinculoFP.getMatriculaFP().getPessoa() + " não tem informações cadastradas sobre o Raça/Cor." + urlPessoa);
        } else {
            Date dataLimite = DataUtil.getDateParse("21/04/2024");
            Set<Integer> racaCorPermitidas = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            Integer racaCor = pessoaFisica.getRacaCor().getCodigoEsocial();

            if (!racaCorPermitidas.contains(racaCor) && (dtInicio.after(dataLimite))) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoaFisica + " possui um código de Raça/Cor inválido. Se data de Inicio de Obrigatoriedade S2400 ou se não data de inicio de vigencia do vinculo, " +
                    "for igual ou posterior a 22/04/2024, " +
                    "não pode ser informado o valor [6]." +
                    "De acordo com as regras do eSocial, a partir de 22/04/2024, os valores permitidos são: " +
                    "1 - Branca, 2 - Preta, 3 - Parda, 4 - Amarela ou 5 - Indígena. " + urlPessoa);
            } else {
                eventoS2400.setRacaCor(vinculoFP.getMatriculaFP().getPessoa().getRacaCor().getCodigoEsocial());
            }
        }

        if (vinculoFP instanceof Aposentadoria) {
            Aposentadoria aposentadoria = (Aposentadoria) vinculoFP;

                aposentadoria = aposentadoriaFacade.recuperar(aposentadoria.getId());

                List<InvalidezAposentado> invalidezAposentados = aposentadoria.getInvalidezAposentados();
                boolean incFisMen = false;
                for (InvalidezAposentado invalidezAposentado : invalidezAposentados) {
                    if (invalidezAposentado.getIsentoPrevidencia()) {
                        eventoS2400.setIncFisMen(true);
                        eventoS2400.setDtIncFisMen(invalidezAposentado.getInicioVigencia());
                        incFisMen = true;
                        break;
                    }
                }
                eventoS2400.setIncFisMen(incFisMen);
            }  else {
            eventoS2400.setIncFisMen(false);
        }
    }

    private void adicionarInformacoesEndereco(EventosESocialDTO.S2400 eventoS2400, VinculoFP vinculoFP, ValidacaoException ve) {
        EnderecoCorreio enderecoPrincipal = vinculoFP.getMatriculaFP().getPessoa().getEnderecoPrincipal();
        if (enderecoPrincipal == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa - " + vinculoFP.getMatriculaFP().getPessoa() + " ' não possui endereço.");
            return;
        }

        String logradouro = enderecoPrincipal.getLogradouro();
        if (StringUtils.isBlank(logradouro)) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa - " + vinculoFP.getMatriculaFP().getPessoa() + " ' O endereço principal da está vazio. Por favor, forneça um logradouro válido antes de prosseguir.");
        } else if (!logradouro.matches("[^\\s]{1}[\\S\\s]*")) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa - " + vinculoFP.getMatriculaFP().getPessoa() + " ' O logradouro '" + logradouro + "' não atende aos requisitos. O logradouro deve começar com um caractere que não seja espaço em branco e pode conter até 100 caracteres.");
        } else {
            eventoS2400.setDscLogradBr(enderecoPrincipal.getLogradouro());
        }
        eventoS2400.setTpLogradBr(enderecoPrincipal.getTipoLogradouro() != null ? enderecoPrincipal.getTipoLogradouro().name() : "R");


        if (StringUtils.isBlank(enderecoPrincipal.getNumero())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa - " + vinculoFP.getMatriculaFP().getPessoa() + " ' - é necessário informar o número do logradouro.");
        }
        String numeroLogradouro;
        if (!Strings.isNullOrEmpty(enderecoPrincipal.getNumero())) {
            numeroLogradouro = Util.cortarString(enderecoPrincipal.getNumero(), 10);
            eventoS2400.setNrLogradBr(numeroLogradouro);
        }

        String complemento;
        if (!Strings.isNullOrEmpty(enderecoPrincipal.getComplemento())) {
            complemento = Util.cortarString(enderecoPrincipal.getComplemento(), 30);
            eventoS2400.setComplementoBr(complemento);
        } else {
            eventoS2400.setComplementoBr(null);
        }

        eventoS2400.setBairroBr(!Strings.isNullOrEmpty(enderecoPrincipal.getBairro()) ? enderecoPrincipal.getBairro() : null);
        String cep = StringUtil.retornaApenasNumeros(enderecoPrincipal.getCep());
        if (cep != null && cep.matches("\\d{8}")) {
            eventoS2400.setCepBr(cep);
        } else {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa - " + vinculoFP.getMatriculaFP().getPessoa() + " ' O CEP fornecido não está no formato válido. O CEP deve conter exatamente 8 dígitos numéricos.");
        }
        if (enderecoPrincipal.getLocalidade() != null && enderecoPrincipal.getUf() != null) {
            BigDecimal codigoIBGECidade = enderecoCorreioFacade.getCodigoIBGECidade(enderecoPrincipal.getLocalidade(), enderecoPrincipal.getUf());
            if (codigoIBGECidade != null) {
                eventoS2400.setCodMunicBr(Integer.parseInt(codigoIBGECidade.toString()));
            } else {
                eventoS2400.setCodMunicBr(COD_IBGE_MUNIC_RIO_BRANCO);
            }
            eventoS2400.setUfBr(enderecoPrincipal.getUf().trim());
        }
    }
}
