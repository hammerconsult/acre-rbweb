package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2205;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.EnderecoCorreioFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

/**
 * Created by William on 12/09/2018.
 */
@Service(value = "s2205Service")
public class S2205Service {
    public static final int COD_IBGE_MUNIC_RIO_BRANCO = 1200401;
    protected static final Logger logger = LoggerFactory.getLogger(S2205Service.class);
    @PersistenceContext
    protected transient EntityManager entityManager;
    @Autowired
    private ESocialService eSocialService;

    private EnderecoCorreioFacade enderecoCorreioFacade;


    @PostConstruct
    public void init() {
        try {
            enderecoCorreioFacade = (EnderecoCorreioFacade) new InitialContext().lookup("java:module/EnderecoCorreioFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    public void enviarS2205(ConfiguracaoEmpregadorESocial config, ContratoFP contratoFP) {
        ValidacaoException val = new ValidacaoException();
        EventoS2205 s2205 = criarEventoS2205(config, contratoFP, val);
        logger.debug("Antes de Enviar: " + s2205.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2205(s2205);
    }

    private EventoS2205 criarEventoS2205(ConfiguracaoEmpregadorESocial config, ContratoFP contratoFP, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2205 eventoS2205 = (EventosESocialDTO.S2205) eSocialService.getEventoS2205(empregador);
        eventoS2205.setIdentificadorWP(contratoFP.getId().toString());
        eventoS2205.setClasseWP(ClasseWP.VINCULOFP);
        eventoS2205.setDescricao(contratoFP.toString());

        eventoS2205.setIdESocial(contratoFP.getId().toString());
        Date dtAlteracao = adicionarInformacoesIdentificacaoTrabalhador(eventoS2205, contratoFP);
        adicionarInformacoesDadosTrabalhador(eventoS2205, contratoFP, val, dtAlteracao);
        adicionarInformacoesNascimento(eventoS2205, contratoFP, val);
        adicionarInformacoesEndereco(eventoS2205, contratoFP, val);

        return eventoS2205;
    }

    private Date adicionarInformacoesIdentificacaoTrabalhador(EventosESocialDTO.S2205 eventoS2205, ContratoFP contratoFP) {
        Date dtAlteracao = new Date();
        eventoS2205.setCpfTrab(retornaApenasNumeros(contratoFP.getMatriculaFP().getPessoa().getCpf()));
        eventoS2205.setDtAlteracao(dtAlteracao); //TODO conferir se deve pegar da auditoria
        return dtAlteracao;
    }

    private void adicionarInformacoesDadosTrabalhador(EventosESocialDTO.S2205 eventoS2205, ContratoFP contratoFP, ValidacaoException ve, Date dtAlteracao) {
        PessoaFisica pessoaFisica = contratoFP.getMatriculaFP().getPessoa();
        String urlPessoa = "/pessoa/editar/" + pessoaFisica.getId();

        if (contratoFP.getMatriculaFP().getPessoa().getCarteiraDeTrabalho() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + contratoFP.getMatriculaFP().getPessoa() + " não possui carteira de trabalho.");
        } else {
            eventoS2205.setNisTrab(contratoFP.getMatriculaFP().getPessoa().getCarteiraDeTrabalho().getPisPasep());
        }
        eventoS2205.setNmTrab(contratoFP.getMatriculaFP().getPessoa().getNome());
        if (contratoFP.getMatriculaFP().getPessoa().getSexo() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + contratoFP.getMatriculaFP().getPessoa() + " não tem informações cadastradas sobre o Sexo.");
        } else {
            eventoS2205.setSexo(contratoFP.getMatriculaFP().getPessoa().getSexo().getSigla());
        }
        if (contratoFP.getMatriculaFP().getPessoa().getRacaCor() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + contratoFP.getMatriculaFP().getPessoa() + " não tem informações cadastradas sobre o Raça/Cor.");
        } else {
            Date dataLimite = DataUtil.getDateParse("21/04/2024");
            Set<Integer> racaCorPermitidas = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            Integer racaCor = contratoFP.getMatriculaFP().getPessoa().getRacaCor().getCodigoEsocial();

            if (!racaCorPermitidas.contains(racaCor) && (dtAlteracao.after(dataLimite))) {
                ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoaFisica + " possui um código de Raça/Cor inválido. Se data de alterarção for igual ou posterior a 22/04/2024 " +
                    "não pode ser informado o valor [6]." +
                    "De acordo com as regras do eSocial, a partir de 22/04/2024, os valores permitidos são: " +
                    "1 - Branca, 2 - Preta, 3 - Parda, 4 - Amarela ou 5 - Indígena. " + urlPessoa);
            } else {
                eventoS2205.setRacaCor(contratoFP.getMatriculaFP().getPessoa().getRacaCor().getCodigoEsocial());
            }
        }
        eventoS2205.setGrauInstr(contratoFP.getMatriculaFP().getPessoa().getNivelEscolaridade().getGrauEscolaridadeESocial().getCodigo());
    }

    private void adicionarInformacoesNascimento(EventosESocialDTO.S2205 eventoS2205, ContratoFP contratoFP, ValidacaoException ve) {
        eventoS2205.setUfBr(contratoFP.getMatriculaFP().getPessoa().getNaturalidade().getUf().getUf());
        eventoS2205.setPaisNac(Util.cortarString(contratoFP.getMatriculaFP().getPessoa().getNacionalidade().getPais().getCodigoBacen().toString(), 3));
    }


    private void adicionarInformacoesEndereco(EventosESocialDTO.S2205 eventoS2205, ContratoFP contratoFP, ValidacaoException ve) {
        EnderecoCorreio enderecoPrincipal = contratoFP.getMatriculaFP().getPessoa().getEnderecoPrincipal();
        if (enderecoPrincipal == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + contratoFP.getMatriculaFP().getPessoa() + " não possúi endereço.");
            return;
        }
        eventoS2205.setTpLogradBr(enderecoPrincipal.getTipoLogradouro() != null ? enderecoPrincipal.getTipoLogradouro().name() : "R");
        eventoS2205.setDscLogradBr(enderecoPrincipal.getLogradouro());
        eventoS2205.setNrLogradBr(enderecoPrincipal.getNumero());
        eventoS2205.setComplementoBr(!Strings.isNullOrEmpty(enderecoPrincipal.getComplemento()) ? enderecoPrincipal.getComplemento() : null);
        eventoS2205.setBairroBr(!Strings.isNullOrEmpty(enderecoPrincipal.getBairro()) ? enderecoPrincipal.getBairro() : null);
        eventoS2205.setCepBr(retornaApenasNumeros(enderecoPrincipal.getCep()));
        String codigoIBGECidade = enderecoCorreioFacade.getCodigoIBGECidade(enderecoPrincipal.getLocalidade(), enderecoPrincipal.getUf()).toString();
        if (codigoIBGECidade != null) {
            eventoS2205.setCodMunicBr(Integer.parseInt(codigoIBGECidade));
        } else {
            eventoS2205.setCodMunicBr(COD_IBGE_MUNIC_RIO_BRANCO);
        }
        eventoS2205.setUfBr(enderecoPrincipal.getUf().trim());
    }
}
