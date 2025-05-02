package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.rh.esocial.ClassificacaoTributariaESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.dto.EmpregadorESocialDTO;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

import static br.com.webpublico.util.StringUtil.convertVazioEmNull;
import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;


@Service(value = "empregadorService")
public class EmpregadorService {

    protected static final Logger logger = LoggerFactory.getLogger(EmpregadorService.class);

    @Autowired
    private ESocialService eSocialService;



    private SistemaFacade sistemaFacade;

    private PessoaFisicaFacade pessoaFisicaFacade;

    private ConfiguracaoRHFacade configuracaoRHFacade;


    @PostConstruct
    public void init() {
        try {
            configuracaoRHFacade = (ConfiguracaoRHFacade) new InitialContext().lookup("java:module/ConfiguracaoRHFacade");
            pessoaFisicaFacade = (PessoaFisicaFacade) new InitialContext().lookup("java:module/PessoaFisicaFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }


    public EmpregadorESocialDTO montarEmpregadorESocial(ConfiguracaoEmpregadorESocial config) {
        EmpregadorESocialDTO dto = new EmpregadorESocialDTO();
        dto.setSenhaCertificado(config.getSenhaCertificado());
        dto.setDataInicioOperacao(config.getInicioVigencia());
        dto.setDataInicioFase1(config.getDataInicioObrigatoriedadeFase1());
        dto.setDataInicioFase2(config.getDataInicioObrigatoriedadeFase2());
        dto.setDataInicioFase3(config.getDataInicioObrigatoriedadeFase3());
        dto.setDataInicioFase4(config.getDataInicioObrigatoriedadeFase4());
        adicionarInfoCadastroEmpregador(config, dto);
        adicionarDadosIsencaoEmpregador(config, dto);
        adicionarInfoEnteEmpregador(config, dto);


        if (config.getCertificado() != null) {
            String certificado = new Base64().encodeAsString(config.getCertificado().getArquivosComposicao().get(0).getArquivo().getByteArrayDosDados());
            dto.setCertificado(certificado);
        }
        return dto;
    }

    private void adicionarInfoEnteEmpregador(ConfiguracaoEmpregadorESocial config, EmpregadorESocialDTO dto) {
        if (config.getClassificacaoTributaria() != null && !ClassificacaoTributariaESocial.PESSOA_JURIDICA_GERAL.equals(config.getClassificacaoTributaria())) {
            dto.getInfoEFR().setIdeEFR(config.getEnteFederativoResponsavel());
            dto.getInfoEFR().setCnpjEFR(convertVazioEmNull(getCnpj(config.getEnteFederativo())));
            dto.getInfoEFR().setCodMunic(config.getEntidade().getCodigoMunicipio() != null ? Integer.valueOf(config.getEntidade().getCodigoMunicipio()) : null);
            dto.getInfoEFR().setIndRPPS(config.getPossuiPrevidenciaPropria() != null ? config.getPossuiPrevidenciaPropria() ? "S" : "N" : null);
            dto.getInfoEFR().setSubteto(config.getPoderSubteto() != null ? config.getPoderSubteto().getCodigoESocial() : null);
            dto.getInfoEFR().setVrSubteto(config.getValorSubtetoEnteFederativo() != null ? config.getValorSubtetoEnteFederativo().doubleValue() : null);
            dto.getInfoCadastro().setNrSiafi(convertVazioEmNull(config.getNumeroSiafi()));
        }
    }

    private String getCnpj(PessoaJuridica pj) {
        if (pj != null) {
            return retornaApenasNumeros(pj.getCnpj());
        }
        return null;
    }

    private void adicionarDadosIsencaoEmpregador(ConfiguracaoEmpregadorESocial config, EmpregadorESocialDTO dto) {

        if (config.nenhumCampoPrenchidoDadosIsencao()) {
            return;
        }

        dto.getDadosIsencao().setIdeMinLei(convertVazioEmNull(config.getSiglaNomeLeiCertificado()));
        dto.getDadosIsencao().setNrCertif(convertVazioEmNull(config.getNumeroCertificado()));
        dto.getDadosIsencao().setDtEmisCertif(config.getDataEmissaoCertificado());
        dto.getDadosIsencao().setDtVencCertif(config.getDataVencimentoCertificado());
        dto.getDadosIsencao().setNrProtRenov(convertVazioEmNull(config.getNumeroProtocoloRenovacao()));
        dto.getDadosIsencao().setDtProtRenov(config.getDataProtocoloRenovacao());
        dto.getDadosIsencao().setDtDou(config.getDataDou());
        dto.getDadosIsencao().setPagDou(config.getPaginaDou());
    }

    private void adicionarInfoCadastroEmpregador(ConfiguracaoEmpregadorESocial config, EmpregadorESocialDTO dto) {
        dto.getInfoCadastro().setNrInsc(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        dto.getInfoCadastro().setClassTrib(config.getClassificacaoTributaria().getCodigo());
        dto.getInfoCadastro().setTpInsc(config.getClassificacaoTributaria().getTpInsc());
        dto.getInfoCadastro().setNatJurid(String.valueOf(config.getEntidade().getNaturezaJuridicaEntidade().getCodigo()));
        dto.getInfoCadastro().setTpAmb(config.getTipoAmbienteESocial().getCodigo());
    }

    public List<EventoESocialDTO> getEventosEmpregador(Entidade empregador) {
        String cnpj = getCnpj(empregador.getPessoaJuridica());
        List<EventoESocialDTO> eventosPorEmpregador = eSocialService.getEventosPorEmpregador(eSocialService.getEmpregadorPorCnpj(cnpj));
        return eventosPorEmpregador;
    }

    public List<EventoESocialDTO> getEventosEmpregadorAndTipoArquivo(Entidade empregador, TipoArquivoESocial tipoArquivoESocial, SituacaoESocial situacao, int page, int pageSize) {
        String cnpj = getCnpj(empregador.getPessoaJuridica());
        List<EventoESocialDTO> eventosPorEmpregador = eSocialService.getEventosPorEmpregadorAndTipoArquivo(eSocialService.getEmpregadorPorCnpj(cnpj),
            tipoArquivoESocial, situacao, page, pageSize);
        return eventosPorEmpregador;
    }

    public Integer getQuantidadeEventosPorEmpregadorAndTipoArquivo(Entidade empregador, TipoArquivoESocial tipoArquivoESocial, SituacaoESocial situacao) {
        String cnpj = getCnpj(empregador.getPessoaJuridica());
        return eSocialService.getQuantidadeEventosPorEmpregadorAndTipoArquivo(eSocialService.getEmpregadorPorCnpj(cnpj), tipoArquivoESocial, situacao);
    }

    public List<EventoESocialDTO> getEventosPorEmpregadorAndIdEsocial(Entidade empregador, String idEsocial) {
        String cnpj = getCnpj(empregador.getPessoaJuridica());
        List<EventoESocialDTO> eventosPorEmpregador = eSocialService.getEventosPorEmpregadorAndIdEsocial(eSocialService.getEmpregadorPorCnpj(cnpj), idEsocial);
        return eventosPorEmpregador;
    }
}
