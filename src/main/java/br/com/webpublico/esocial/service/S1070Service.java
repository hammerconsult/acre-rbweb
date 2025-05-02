package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ProcessoAdministrativoJudicial;
import br.com.webpublico.enums.rh.esocial.IndicativoMateriaProcesso;
import br.com.webpublico.enums.rh.esocial.TipoProcessoAdministrativoJudicial;
import br.com.webpublico.esocial.comunicacao.eventos.iniciais.EventoS1070;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;


@Service(value = "s1070Service")
public class S1070Service {

    protected static final Logger logger = LoggerFactory.getLogger(S1070Service.class);

    @Autowired
    private ESocialService eSocialService;

    @PostConstruct
    public void init() {
    }

    public void enviarS1070(ConfiguracaoEmpregadorESocial configuracao, ProcessoAdministrativoJudicial processo) {
        ValidacaoException val = new ValidacaoException();
        EventoS1070 s1070 = criarEventoS1070(processo, configuracao, val);
        logger.debug("Antes de Enviar: " + s1070.getXml());
        val.lancarException();
        eSocialService.enviarEventoS1070(s1070);
    }

    public EventoS1070 criarEventoS1070(ProcessoAdministrativoJudicial processo, ConfiguracaoEmpregadorESocial configuracao, ValidacaoException val) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(configuracao.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S1070 eventoS1070 = (EventosESocialDTO.S1070) eSocialService.getEventoS1070(empregador);
        eventoS1070.setIdentificadorWP(processo.getId().toString());
        eventoS1070.setClasseWP(ClasseWP.PROCESSOADMINISTRATIVOJUDICIAL);
        eventoS1070.setDescricao(processo.getAutorRequerente().toString());

        eventoS1070.setIdESocial(processo.getId().toString());
        eventoS1070.setTpProc(processo.getTipoProcesso().getCodigo());
        eventoS1070.setNrProc(processo.getNumeroProcesso());
        eventoS1070.setIniValid(configuracao.getInicioVigencia());
        if (processo.getIndicativoAutoria() != null) {
            eventoS1070.setIndAutoria(processo.getIndicativoAutoria().getCodigo());
        }
        eventoS1070.setIndMatProc(processo.getIndicativoMateriaProcesso().getCodigo());
        eventoS1070.setObservacao(processo.getObservacoes());

        criarInformacoesProcessoJudicial(processo, eventoS1070);
        criarInformacoesIndicativoMateriaTributaria(processo, eventoS1070);
        return eventoS1070;
    }

    private void criarInformacoesIndicativoMateriaTributaria(ProcessoAdministrativoJudicial processo, EventosESocialDTO.S1070 eventoS1070) {
        if (IndicativoMateriaProcesso.TRIBUTARIA.equals(processo.getIndicativoMateriaProcesso())) {
            EventoS1070.InfoSusp informacaoSuspensao = eventoS1070.addInfoSusp();
            informacaoSuspensao.setIndSusp(processo.getIndicativoSuspensaoExigib().getCodigo());
            informacaoSuspensao.setCodSusp(Integer.parseInt(processo.getCodigoSuspensao()));
            informacaoSuspensao.setDtDecisao(processo.getDataDecisao());
            informacaoSuspensao.setIndDeposito(processo.getDepositoMontanteIntegral());
        }
    }

    private void criarInformacoesProcessoJudicial(ProcessoAdministrativoJudicial processo, EventosESocialDTO.S1070 eventoS1070) {
        if (TipoProcessoAdministrativoJudicial.JUDICIAL.equals(processo.getTipoProcesso())) {
            eventoS1070.setUfVara(processo.getUfVara().getUf());
            eventoS1070.setCodMunic(processo.getMunicipio().getCodigo());
            eventoS1070.setIdVara(Integer.parseInt(processo.getCodigoVara()));
        }
    }

}
