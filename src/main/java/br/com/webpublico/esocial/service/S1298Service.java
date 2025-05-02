package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.esocial.comunicacao.eventos.periodicos.EventoS1298;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "s1298Service")
public class S1298Service {
    protected static final Logger logger = LoggerFactory.getLogger(S1298Service.class);


    @Autowired
    private ESocialService eSocialService;

    public void enviarS1298(RegistroEventoEsocial registroEventoEsocial) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(StringUtil.retornaApenasNumeros(registroEventoEsocial.getEntidade().getPessoaJuridica().getCnpj()));

        EventoS1298 eventoS1298 = criarEventoS1298(registroEventoEsocial, empregador);
        logger.debug("Antes de Enviar: " + eventoS1298.getXml());
        eSocialService.enviarEventoS1298(eventoS1298);
    }

    private EventoS1298 criarEventoS1298(RegistroEventoEsocial registro, EmpregadorESocial empregador) {
        EventosESocialDTO.S1298 eventoS1298 = (EventosESocialDTO.S1298) eSocialService.getEventoS1298(empregador);

        eventoS1298.setIndApuracao(TipoFolhaDePagamento.SALARIO_13.equals(registro.getTipoFolhaDePagamento()) ? 2 : 1);
        String mes = registro.getMes().getNumeroMes().toString();
        String ano = registro.getExercicio().getAno().toString();
        eventoS1298.setIdESocial(ano.concat(mes).concat(registro.getId().toString()));

        if (TipoFolhaDePagamento.SALARIO_13.equals(registro.getTipoFolhaDePagamento())) {
            eventoS1298.setPerApur(registro.getExercicio().getAno());
        } else {
            eventoS1298.setPerApur(registro.getExercicio().getAno(), registro.getMes().getNumeroMes());
        }
        return eventoS1298;
    }
}
