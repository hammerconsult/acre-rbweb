package br.com.webpublico.esocial.service;


import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.saudeservidor.CAT;
import br.com.webpublico.enums.LocalAcidente;
import br.com.webpublico.enums.TipoAcidenteCAT;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2210;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s2210Service")
public class S2210Service {

    protected static final Logger logger = LoggerFactory.getLogger(S2210Service.class);

    public static final int COD_IBGE_MUNIC_RIO_BRANCO = 1200401;


    @Autowired
    private ESocialService eSocialService;

    public void enviarS2210(ConfiguracaoEmpregadorESocial config, CAT cat, ContratoFP contrato) {
        ValidacaoException val = new ValidacaoException();
        if (contrato == null) {
            val.adicionarMensagemDeOperacaoNaoPermitida("Não existe contrato vigente para essa pessoa " + cat.getColaborador());
        }

        EventoS2210 s2210 = criarEventoS2210(config, cat, contrato, val);
        logger.debug("Antes de Enviar: " + s2210.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2210(s2210);
    }

    private EventoS2210 criarEventoS2210(ConfiguracaoEmpregadorESocial config, CAT cat, ContratoFP contrato, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2210 eventoS2210 = (EventosESocialDTO.S2210) eSocialService.getEventoS2210(empregador);
        eventoS2210.setClasseWP(ClasseWP.CAT);
        eventoS2210.setIdentificadorWP(cat.getId().toString());
        eventoS2210.setDescricao(cat.getColaborador().toString());

        eventoS2210.setIdESocial(cat.getId().toString());
        adicionarInformacoesBasicas(eventoS2210, contrato, val);
        adicionarInformacoesAcidentes(eventoS2210, cat, val);
        adicionarLocalAcidente(eventoS2210, cat, val, config);
        adicionarInformacaoParteAtingida(eventoS2210, cat, val);
        adicionarInformacaoAgenteCausador(eventoS2210, cat, val);
        adicionarInformacaoAtendimento(eventoS2210, cat, val);
        adicionarInformacaoEmitenteAtestado(eventoS2210, cat, val);

        return eventoS2210;
    }

    private void adicionarMatricula(EventosESocialDTO.S2210 eventoS2210, ContratoFP contratoFP) {
        eventoS2210.setMatricula(contratoFP.getMatriculaFP().getMatricula());
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S2210 eventoS2210, ContratoFP contratoFP, ValidacaoException val) {
        eventoS2210.setCpfTrab(StringUtil.retornaApenasNumeros(contratoFP.getMatriculaFP().getPessoa().getCpf()));
        adicionarMatricula(eventoS2210, contratoFP);

        if (contratoFP.getCategoriaTrabalhador() == null) {
            val.adicionarMensagemDeOperacaoNaoPermitida("O contrato " + contratoFP + " não possuí categoria cadastrada");
        } else {
            eventoS2210.setCodCateg(contratoFP.getCategoriaTrabalhador().getCodigo());
        }
        val.lancarException();
    }

    private void adicionarInformacoesAcidentes(EventosESocialDTO.S2210 eventoS2210, CAT cat, ValidacaoException val) {
        if (TipoAcidenteCAT.TIPICO.equals(cat.getTipoAcidente())) {
            if (cat.getHorasTrabalhadasAntesAcidente() != null) {
                eventoS2210.setHrsTrabAntesAcid(DataUtil.getDataFormatadaDiaHora(cat.getHorasTrabalhadasAntesAcidente(), "HHmm"));
            } else {
                val.adicionarMensagemDeOperacaoNaoPermitida("Para o Tipo de Acidente " + cat.getTipoAcidente().getDescricao() + "  deve ser informado a Horas trabalhadas antes da ocorrência do acidente");
            }
        }
        eventoS2210.setDtAcid(cat.getOcorridoEm());
        eventoS2210.setTpAcid(cat.getTipoAcidente().getCodigo());
        eventoS2210.setHrAcid(DataUtil.getDataFormatadaDiaHora(cat.getOcorridoEm(), "HHmm"));
        eventoS2210.setTpCat(cat.getTipoCat().getCodigo());
        eventoS2210.setIndCatObito(cat.getHouveObito());
        eventoS2210.setDtObito(cat.getDataObito());
        eventoS2210.setIndComunPolicia(cat.getHouveComunicacaoPolicial());
        eventoS2210.setCodSitGeradora(Integer.valueOf(cat.getAgenteAcidenteTrabalho().getCodigo()));
        eventoS2210.setIniciatCAT(cat.getIniciativaCAT().getCodigo());
        eventoS2210.setObsCAT(cat.getObservacoes());
        val.lancarException();
    }

    private void adicionarLocalAcidente(EventosESocialDTO.S2210 eventoS2210, CAT cat, ValidacaoException val, ConfiguracaoEmpregadorESocial config) {
        eventoS2210.setTpLocal(cat.getLocalAcidente().getCodigoEsocial());
        eventoS2210.setDscLocal(cat.getDescricaoLocalAcidente());
        eventoS2210.setTpLograd(cat.getTipoLogradouro().name());
        eventoS2210.setDscLograd(cat.getDescricaoLogradouro());
        eventoS2210.setNrLograd(cat.getNumeroLogradouro());
        eventoS2210.setComplemento(cat.getComplementoLogradouro());
        eventoS2210.setBairro(cat.getBairroDistritoLogradouro());
        if (LocalAcidente.ESTABELECIMENTO_EMPREGADOR_EXTERIOR.equals(cat.getLocalAcidente())) {
            eventoS2210.setPais(cat.getCodigoPais());
            eventoS2210.setCodPostal(cat.getCodigoEnderecamentoPostal());
        } else {
            eventoS2210.setCep(cat.getCepLogradouro());
            eventoS2210.setUf(cat.getUf().getUf());
            eventoS2210.setCodMunic(Integer.valueOf(cat.getCodigoMunicipio()));
        }
        eventoS2210.setTpInscLocalAcid(cat.getTipoInscricaoAcidenteDoenca().getCodigo());
        eventoS2210.setNrInscLocalAcid(retornaApenasNumeros(config.getEntidadeEducativa().getCnpj()));
    }

    private void adicionarInformacaoParteAtingida(EventosESocialDTO.S2210 eventoS2210, CAT cat, ValidacaoException val) {
        EventoS2210.ParteAtingida parteAtingida = eventoS2210.addParteAtingida();
        parteAtingida.setCodParteAting(Integer.valueOf(cat.getParteCorpo().getCodigo()));
        parteAtingida.setLateralidade(cat.getLateralidadeParteAtingida().getCodigo());
    }

    private void adicionarInformacaoAgenteCausador(EventosESocialDTO.S2210 eventoS2210, CAT cat, ValidacaoException val) {
        if (cat.getAgenteAcidenteTrabalho() != null) {
            EventoS2210.AgenteCausador agenteCausador = eventoS2210.addAgenteCausador();
            agenteCausador.setCodAgntCausador(Integer.parseInt(cat.getAgenteAcidenteTrabalho().getCodigo()));
        } else {
            val.adicionarMensagemDeOperacaoNaoPermitida("Não foi informado o agente causador do acidente.");
        }
        val.lancarException();
    }

    private void adicionarInformacaoAtendimento(EventosESocialDTO.S2210 eventoS2210, CAT cat, ValidacaoException val) {
        eventoS2210.setDtAtendimento(cat.getAtendidoEm());
        eventoS2210.setHrAtendimento(DataUtil.getDataFormatadaDiaHora(cat.getAtendidoEm(), "HHmm"));
        eventoS2210.setIndInternacao(cat.getHouveInternacao());
        eventoS2210.setDurTrat(cat.getDuracao());
        eventoS2210.setIndAfast(cat.getAfastaDuranteTratamento());
        eventoS2210.setDscLesao(Integer.parseInt(cat.getNaturezaLesao().getCodigo()));
        eventoS2210.setDscCompLesao(cat.getNaturezaLesao().getDescricao());
        eventoS2210.setDiagProvavel(cat.getDiagnosticoProvavel());
        eventoS2210.setCodCID(cat.getCid().getCodigoDaCid());
        eventoS2210.setObservacao(cat.getObservacoes());
    }

    private void adicionarInformacaoEmitenteAtestado(EventosESocialDTO.S2210 eventoS2210, CAT cat, ValidacaoException val) {
        eventoS2210.setNmEmit(cat.getMedico().getMedico().getNome());
        eventoS2210.setIdeOC(cat.getOrgaoClasse().getCodigo());
        eventoS2210.setNrOC(cat.getOrgaoClasse().getCodigo().toString());
        eventoS2210.setUfOC(cat.getUfOrgaoClasse().getUf());
    }
}


