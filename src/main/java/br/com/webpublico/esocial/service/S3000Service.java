package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroExclusaoS3000;
import br.com.webpublico.enums.rh.esocial.TipoExclusaoEventoFolha;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS3000;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by William on 29/11/2018.
 */
@Service(value = "s3000Service")
public class S3000Service {
    protected static final Logger logger = LoggerFactory.getLogger(S3000Service.class);

    @Autowired
    private ESocialService eSocialService;

    @Autowired
    private EmpregadorService empregadorService;

    public void enviarS3000(ConfiguracaoEmpregadorESocial config, RegistroExclusaoS3000 exclusaoS3000) {
        ValidacaoException val = new ValidacaoException();
        try {
            EventoS3000 s3000 = criarEventoS3000(config, exclusaoS3000);
            logger.debug("Antes de Enviar: " + s3000.getXml());
            if (hasEnvioIndApuracao(exclusaoS3000)) {
                eSocialService.enviarEventoS3000Simples(exclusaoS3000.getIdXML());
            } else {
                eSocialService.enviarEventoS3000(s3000);
            }
            val.lancarException();
        } catch (Exception e) {
            val.adicionarMensagemDeOperacaoNaoPermitida(e.getMessage());
            val.lancarException();
        }
    }

    private EventoS3000 criarEventoS3000(ConfiguracaoEmpregadorESocial config, RegistroExclusaoS3000 registroExclusaoS3000) {
        try {
            if (!Strings.isNullOrEmpty(registroExclusaoS3000.getIdXML())) {
                EventoS3000 eventoS3000 = eSocialService.getEventoS3000(registroExclusaoS3000.getIdXML());
                logger.debug("evento recuperado s3000 " + eventoS3000);
                if (eventoS3000 != null) {
                    eventoS3000.setNrRecEvt(registroExclusaoS3000.getRecibo());
                    if (registroExclusaoS3000.getVinculoFP() != null) {
                        eventoS3000.setCpfTrab(StringUtil.retornaApenasNumeros(registroExclusaoS3000.getVinculoFP().getMatriculaFP().getPessoa().getCpf()));
                    }
                    if (registroExclusaoS3000.getPrestadorServicos() != null) {
                        eventoS3000.setCpfTrab(StringUtil.retornaApenasNumeros(registroExclusaoS3000.getPrestadorServicos().getPrestador().getCpf_Cnpj()));
                    }

                    if (TipoArquivoESocial.getEventoFolhaPagamento().contains(registroExclusaoS3000.getTipoArquivoESocial())) {
                        if (registroExclusaoS3000.getTipoExclusaoEventoFolha() == null || TipoExclusaoEventoFolha.MENSAL.equals(registroExclusaoS3000.getTipoExclusaoEventoFolha())) {
                            int mes = DataUtil.getMes(registroExclusaoS3000.getCompetencia());
                            String mesString = StringUtil.cortarOuCompletarEsquerda(String.valueOf(mes), 2, "0");
                            Integer ano = DataUtil.getAno(registroExclusaoS3000.getCompetencia());
                            eventoS3000.setIndApuracao(1);
                            eventoS3000.setPerApur(ano.toString().concat("-").concat(mesString));
                        } else {
                            eventoS3000.setIndApuracao(2);
                            eventoS3000.setPerApur(registroExclusaoS3000.getExercicio().getAno().toString());
                        }
                        if (!hasEnvioIndApuracao(registroExclusaoS3000) && !TipoArquivoESocial.S1200.equals(registroExclusaoS3000.getTipoArquivoESocial()) && !TipoArquivoESocial.S1202.equals(registroExclusaoS3000.getTipoArquivoESocial())) {
                            eventoS3000.setIndApuracao(null);
                        }
                        return eventoS3000;
                    } else {
                        return eventoS3000;
                    }
                }
            }
            logger.debug("não encontrou S3000");

            String idEsocial = "";
            if (registroExclusaoS3000.getVinculoFP() != null) {
                idEsocial = registroExclusaoS3000.getVinculoFP().getId().toString();
            }
            if (registroExclusaoS3000.getPrestadorServicos() != null) {
                idEsocial = registroExclusaoS3000.getPrestadorServicos().getId().toString();
            }
            List<EventoESocialDTO> eventoESocial = empregadorService.getEventosPorEmpregadorAndIdEsocial(config.getEntidade(), idEsocial);
            logger.debug("eventoESocial S3000 " + eventoESocial);
            EventosESocialDTO.S3000 eventoS3000 = (EventosESocialDTO.S3000) eSocialService.getEventoS3000(eventoESocial.get(0).getIdXMLEvento());
            eventoS3000.setIdentificadorWP(idEsocial);
            eventoS3000.setIdESocial(idEsocial);
            return eventoS3000;
        } catch (Exception ex) {
            logger.error("Erro para enviar arquivo " + ex.getMessage());
        }
        return null;
    }


    private boolean hasEnvioIndApuracao(RegistroExclusaoS3000 registroExclusaoS3000) {
        return TipoArquivoESocial.S1207.equals(registroExclusaoS3000.getTipoArquivoESocial()) ||
            TipoArquivoESocial.S1280.equals(registroExclusaoS3000.getTipoArquivoESocial()) ||
            TipoArquivoESocial.S1300.equals(registroExclusaoS3000.getTipoArquivoESocial());
    }
}
