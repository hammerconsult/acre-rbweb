package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2306;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Date;
import java.util.Objects;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

/**
 * Created by William on 21/11/2018.
 */
@Service(value = "s2306Service")
public class S2306Service {

    protected static final Logger logger = LoggerFactory.getLogger(S2306Service.class);
    @Autowired
    private ESocialService eSocialService;

    private CargoFacade cargoFacade;


    private CargoFacade getCargoFacade() {
        try {
            if (cargoFacade == null) {
                cargoFacade = (CargoFacade) new InitialContext().lookup("java:module/CargoFacade");
            }
        } catch (NamingException e) {
            logger.error("Erro ao injetar Cargo Facade : ", e);
        }
        return cargoFacade;
    }

    public void enviarS2306(ConfiguracaoEmpregadorESocial config, PrestadorServicos prestador) {
        ValidacaoException val = new ValidacaoException();
        EventoS2306 s2306 = criarEventoS2306(config, prestador, val);
        logger.debug("Antes de Enviar: " + s2306.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2306(s2306);
    }

    private EventoS2306 criarEventoS2306(ConfiguracaoEmpregadorESocial config, PrestadorServicos prestador, ValidacaoException ve) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2306 eventoS2306 = (EventosESocialDTO.S2306) eSocialService.getEventoS2306(empregador);
        eventoS2306.setIdentificadorWP(prestador.getId().toString());
        eventoS2306.setClasseWP(ClasseWP.PRESTADORSERVICOS);
        eventoS2306.setDescricao(prestador.getPrestador().toString());
        eventoS2306.setUndSalFixo(5);
        eventoS2306.setVrSalFx(prestador.getValorParcelaFixa());

        eventoS2306.setIdESocial(prestador.getId().toString());
        adicionarInformacoesBasicas(eventoS2306, prestador, ve);
        adicionarInformacoesCargoFuncao(eventoS2306, prestador, ve);
        return eventoS2306;
    }

    private void adicionarMatricula(EventosESocialDTO.S2306 eventoS2306, PrestadorServicos prestadorServicos) {
        if (prestadorServicos != null) {
            eventoS2306.setMatricula(prestadorServicos.getMatriculaESocial());
        }
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S2306 eventoS2306, PrestadorServicos prestador, ValidacaoException ve) {
        eventoS2306.setCpfTrab(StringUtil.retornaApenasNumeros(prestador.getPrestador().getCpf_Cnpj()));
        if (prestador.getCategoriaTrabalhador() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi informado a categoria do trabalhador " + prestador + " em seu cadastro na aba 'e-social'");
        } else {
            eventoS2306.setCodCateg(prestador.getCategoriaTrabalhador().getCodigo());
        }
        eventoS2306.setDtAlteracao(new Date()); //TODO verificar data
        adicionarMatricula(eventoS2306, prestador);
    }

    private void adicionarInformacoesComplementares(EventosESocialDTO.S2306 eventoS2306, VinculoFP vinculoFP, ValidacaoException ve) {
        ContratoFP contrato = (ContratoFP) vinculoFP;
        Estagiario estagiario = (Estagiario) contrato;
        if (estagiario.getTipoNaturezaEstagio() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informado a Natureza do Estágio do trabalhador " + vinculoFP +
                " na aba 'Informações relativas ao estagiário (E-social)'");
        } else {
            eventoS2306.setNatEstagio(estagiario.getTipoNaturezaEstagio().getSigla());
        }
        if (estagiario.getTipoNivelEstagio() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informado o Nível do Estágio do trabalhador " + vinculoFP +
                " na aba 'Informações relativas ao estagiário (E-social)'");
        } else {
            eventoS2306.setNivEstagio(estagiario.getTipoNivelEstagio().getCodigo());
        }
        if (estagiario.getDataPrevistaTermino() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informado a Data prevista de término do estágio do trabalhador " + vinculoFP +
                " na aba 'Informações relativas ao estagiário (E-social)'");
        } else {
            eventoS2306.setDtPrevTerm(estagiario.getDataPrevistaTermino());
        }
        if (estagiario.getInstituicaoEnsino() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi informado a Instituição de Ensino do trabalhador " + vinculoFP +
                " na aba 'Informações relativas ao estagiário (E-social)'");
        } else {
            eventoS2306.setCnpjInstEnsino(retornaApenasNumeros(estagiario.getInstituicaoEnsino().getCnpj()));
            eventoS2306.setNmRazaoInstEnsino(estagiario.getInstituicaoEnsino().getRazaoSocial());
        }
    }

    private void adicionarInformacoesCargoFuncao(EventosESocialDTO.S2306 eventoS2306, PrestadorServicos prestadorServicos, ValidacaoException ve) {
        eventoS2306.setNmCargo(prestadorServicos.getCargo().getDescricao());
        if (prestadorServicos.getCargo() != null) {
            Cargo cargo = Objects.requireNonNull(getCargoFacade()).recuperar(prestadorServicos.getCargo().getId());
            if (cargo.getCbos().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O prestador de serviço " + prestadorServicos + " não tem CBO vinculado ao Cargo.");
            } else {
                eventoS2306.setCboCargo(cargo.getCbos().get(0).getCodigo().toString());
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O prestador " + prestadorServicos + " não possuí cargo cadastreado");
        }
    }
}
