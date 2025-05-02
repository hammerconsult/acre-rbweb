package br.com.webpublico.esocial.service;


import br.com.webpublico.entidades.Aposentadoria;
import br.com.webpublico.entidades.Pensionista;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.TipoPensao;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2416;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s2416Service")
public class S2416Service {

    protected static final Logger logger = LoggerFactory.getLogger(S2416Service.class);
    @Autowired
    private ESocialService eSocialService;

    public void enviarS2416(ConfiguracaoEmpregadorESocial config, VinculoFP vinculofp) {
        ValidacaoException val = new ValidacaoException();
        EventoS2416 s2416 = criarEventoS2416(config, vinculofp, val);
        logger.debug("Antes de Enviar: " + s2416.getXml());
        val.lancarException();
        eSocialService.enviarEventoS2416(s2416);
    }

    private EventoS2416 criarEventoS2416(ConfiguracaoEmpregadorESocial config, VinculoFP vinculofp, ValidacaoException ve) {
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2416 eventoS2416 = (EventosESocialDTO.S2416) eSocialService.getEventoS2416(empregador);
        eventoS2416.setIdentificadorWP(vinculofp.getId().toString());
        eventoS2416.setClasseWP(ClasseWP.VINCULOFP);
        eventoS2416.setDescricao(vinculofp.toString());

        eventoS2416.setIdESocial(vinculofp.getId().toString());
        adicionarBeneficario(eventoS2416, vinculofp, empregador);
        adicionarInformacaoDataALteracaoBeneficio(eventoS2416, vinculofp, empregador);
        adicionarDadosBeneficio(eventoS2416, vinculofp, ve);
        adicionarInfoPenMorte(eventoS2416, vinculofp);


        return eventoS2416;

    }

    private void adicionarBeneficario(EventosESocialDTO.S2416 eventoS2416, VinculoFP vinculofp, EmpregadorESocial empregador) {
        eventoS2416.setCpfBenef(StringUtil.retornaApenasNumeros(vinculofp.getMatriculaFP().getPessoa().getCpf()));
        eventoS2416.setNrBeneficio(vinculofp.getMatriculaFP().getMatricula() + StringUtil.cortarOuCompletarEsquerda(vinculofp.getNumero(), 2, "0"));
    }

    private void adicionarInformacaoDataALteracaoBeneficio(EventosESocialDTO.S2416 eventoS2416, VinculoFP vinculofp, EmpregadorESocial empregador) {
        eventoS2416.setDtAltBeneficio(vinculofp.getInicioVigencia());
    }

    private void adicionarDadosBeneficio(EventosESocialDTO.S2416 eventoS2416, VinculoFP vinculofp, ValidacaoException ve) {
        if (vinculofp instanceof Aposentadoria) {
            if (((Aposentadoria) vinculofp).getTipoBeneficioEsocial() != null) {
                eventoS2416.setTpBeneficio(((Aposentadoria) vinculofp).getTipoBeneficioEsocial().getCodigo()); //TODO tabela 25
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Preencha o campo Tipo Benefício Esocial no cadastro de Aposentadoria. Vinculo: " + vinculofp);
            }

        }
        if (vinculofp instanceof Pensionista) {
            eventoS2416.setTpBeneficio(((Pensionista) vinculofp).getTipoFundamentacao().getCodigoEsocial()); //TODO tabela 25
        }
        eventoS2416.setTpPlanRP(0);
        eventoS2416.setIndSuspensao(false); //TODO nao temos a funcionalidade de suspensao

    }

    private void adicionarInfoPenMorte(EventosESocialDTO.S2416 eventoS2416, VinculoFP vinculofp) {
        if (vinculofp instanceof Pensionista) {
            eventoS2416.setTpPenMorte(isVitalicia(((Pensionista) vinculofp).getTipoPensao()) ? 1 : 2);
        }
    }

    private boolean isVitalicia(TipoPensao tipoPensao) {
        return TipoPensao.VITALICIA.equals(tipoPensao) || TipoPensao.VITALICIA_INVALIDEZ.equals(tipoPensao);
    }
}
