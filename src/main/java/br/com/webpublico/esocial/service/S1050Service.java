package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.HorarioDeTrabalho;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.eventos.iniciais.EventoS1050;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1050Service")
public class S1050Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1050Service.class);

    @Autowired
    private ESocialService eSocialService;


    public void enviarS1050(ConfiguracaoEmpregadorESocial config, HorarioDeTrabalho horario) {
        ValidacaoException val = new ValidacaoException();
        EventoS1050 s1050 = criarEventoS1050(config, horario, val);
        logger.debug("Antes de Enviar: " + s1050.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1050(s1050);
    }

    public EventoS1050 criarEventoS1050(ConfiguracaoEmpregadorESocial config, HorarioDeTrabalho horario, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1050 eventoS1050 = (EventosESocialDTO.S1050) eSocialService.getEventoS1050(empregador);

        adicionarInformacoesBasicas(eventoS1050, config, horario, val);
        adicionarDadosHorario(eventoS1050, config, horario, val);


        return eventoS1050;
    }


    private void adicionarDadosHorario(EventosESocialDTO.S1050 eventoS1050, ConfiguracaoEmpregadorESocial config, HorarioDeTrabalho horario, ValidacaoException val) {
        eventoS1050.setHrEntr(horario.getEntrada());
        eventoS1050.setHrSaida(horario.getSaida());
        eventoS1050.setPerHorFlexivel(horario.getPermiteHorarioFlexivel());
        if (horario.getDuracaoJornada() != null) {
            eventoS1050.setDurJornada(horario.getDuracaoJornada());
        } else {
            val.adicionarMensagemDeCampoObrigatorio("A duração da jornada é obrigatória, preencher o campo no cadastro de Horário de Trabalho. " + horario);
        }


        Integer minutos = DataUtil.getMinutos(horario.getIntervalo(), horario.getRetornoIntervalo());
        if (minutos < 0) {
            val.adicionarMensagemDeCampoObrigatorio("A duração do intervalo deve ser maior que zero(0). " + horario);
        }
        if (minutos != 0) {
            EventoS1050.HorarioIntervalo horarioIntervalo = eventoS1050.addHorarioIntervalo();
            horarioIntervalo.setIniInterv(horario.getIntervalo());
            horarioIntervalo.setTermInterv(horario.getRetornoIntervalo());
            horarioIntervalo.setDurInterv(minutos);

            if (horario.getTipoHorarioIntervalo() != null) {
                horarioIntervalo.setTpInterv(horario.getTipoHorarioIntervalo().getCodigo());
            } else {
                val.adicionarMensagemDeCampoObrigatorio("O tipo de intervalo é obrigatório, preencher o campo no cadastro de Horário de Trabalho. " + horario);
            }
        }


    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S1050 eventoS1050, ConfiguracaoEmpregadorESocial config, HorarioDeTrabalho horario, ValidacaoException val) {
        eventoS1050.setIdESocial(horario.getId().toString());
        if (horario.getCodigo() != null) {
            eventoS1050.setCodHorContrat(horario.getCodigo().toString());
        } else {
            val.adicionarMensagemDeCampoObrigatorio("Campo código é obrigatório, preencher o campo código do horário no cadastro de Horário de Trabalho. " + horario);
        }

        if (horario.getInicioVigencia() != null) {
            eventoS1050.setIniValid(config.getDataInicioObrigatoriedadeFase1());
        } else {
            val.adicionarMensagemDeCampoObrigatorio("Início de vigência do ESocial é obrigatório, preencher no cadastro de Horário de Trabalho. " + horario);
        }

        eventoS1050.setFimValid(config.getFinalVigencia());

    }


}
