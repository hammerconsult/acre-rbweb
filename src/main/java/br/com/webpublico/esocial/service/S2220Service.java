package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.ASO;
import br.com.webpublico.entidades.ExameComplementar;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.PrestadorServicos;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2220;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s2220Service")
public class S2220Service {

    protected static final Logger logger = LoggerFactory.getLogger(S2220Service.class);

    @Autowired
    private ESocialService eSocialService;


    public void enviarS2220(ConfiguracaoEmpregadorESocial config, ASO aso, PrestadorServicos prestadorServicos) {
        ValidacaoException val = new ValidacaoException();
        EventoS2220 s2220 = criarEventoS2220(config, aso, val, prestadorServicos);
        logger.error("Antes de Enviar: " + s2220.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2220(s2220);
    }

    private EventoS2220 criarEventoS2220(ConfiguracaoEmpregadorESocial config, ASO aso, ValidacaoException val, PrestadorServicos prestadorServicos) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2220 eventoS2220 = (EventosESocialDTO.S2220) eSocialService.getEventoS2220(empregador);
        eventoS2220.setClasseWP(ClasseWP.ASO);
        eventoS2220.setIdentificadorWP(aso.getId().toString());
        eventoS2220.setDescricao(aso.toString());

        eventoS2220.setIdESocial(aso.getId().toString());
        adicionarInformacoesBasicas(eventoS2220, aso, prestadorServicos);
        adicionarInformacoeAtestado(eventoS2220, aso);
        adicionarInformacoesASO(eventoS2220, aso, val);
        adicionarInformacoesMedico(eventoS2220, aso, val);
        return eventoS2220;
    }

    private void adicionarMatricula(EventosESocialDTO.S2220 eventoS2220, ASO atestado, PrestadorServicos prestadorServicos) {
        if (ModalidadeContratoFP.PRESTADOR_SERVICO.equals(atestado.getContratoFP().getModalidadeContratoFP().getCodigo())) {
            eventoS2220.setMatricula(prestadorServicos.getMatriculaESocial());
        } else {
            eventoS2220.setMatricula(atestado.getContratoFP().getMatriculaFP().getMatricula());
        }
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S2220 eventoS2220, ASO atestado, PrestadorServicos prestadorServicos) {

        System.out.println("cpf " + StringUtil.retornaApenasNumeros(atestado.getContratoFP().getMatriculaFP().getPessoa().getCpf()));
        eventoS2220.setCpfTrab(StringUtil.retornaApenasNumeros(atestado.getContratoFP().getMatriculaFP().getPessoa().getCpf()));
        eventoS2220.setCodCateg(atestado.getContratoFP().getCategoriaTrabalhador().getCodigo());
        eventoS2220.setTpExameOcup(atestado.getTipoExame().getCodigo());
        adicionarMatricula(eventoS2220, atestado, prestadorServicos);
    }

    private void adicionarInformacoeAtestado(EventosESocialDTO.S2220 eventoS2220, ASO aso) {
        eventoS2220.setDtAso(aso.getDataEmissaoASO());
        eventoS2220.setResAso(aso.getSituacao().getCodigo());
    }

    private void adicionarInformacoesASO(EventosESocialDTO.S2220 eventoS2220, ASO aso, ValidacaoException ve) {
        for (ExameComplementar complementar : aso.getExameComplementares()) {
            EventoS2220.Exame exame = eventoS2220.addExame();
            if(complementar.getDataExame().after(aso.getDataEmissaoASO())){
                ve.adicionarMensagemDeOperacaoNaoPermitida( aso.getContratoFP() +" problema na Data do exame realizado - Deve ser uma data válida, igual ou anterior à data do ASO informada.");
            }
            exame.setDtExm(complementar.getDataExame());
            exame.setProcRealizado(Integer.valueOf(complementar.getProcedimentoDiagnostico().getCodigo()));
            if (!Strings.isNullOrEmpty(complementar.getObservacaoProcesso())) {
                exame.setObsProc(complementar.getObservacaoProcesso());
            }
            if (complementar.getTipoOrdemExame() != null) {
                exame.setOrdExame(complementar.getTipoOrdemExame().getCodigo());
            }
            if (complementar.getTipoIndicacaoResultado() != null) {
                exame.setIndResult(complementar.getTipoIndicacaoResultado().getCodigo());
            }
        }
    }

    private void adicionarInformacoesMedico(EventosESocialDTO.S2220 eventoS2220, ASO aso, ValidacaoException ve) {
        eventoS2220.setNmMed(aso.getMedico().getMedico().getNome());
        eventoS2220.setNrCRMMed(aso.getMedico().getRegistroCRM());
        if (aso.getMedico().getUfCRM() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O médico " + aso.getMedico() + " não tem informado a UF de expedição do CRM.");
        } else {
            eventoS2220.setUfCRMMed(aso.getMedico().getUfCRM().getUf());
        }
    }
}
