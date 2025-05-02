package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.domain.OcorrenciaESocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Etiqueta("Registro E-Social")
@Audited
public class RegistroESocial extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * grava o id do objeto que foi enviado para o ESocial
     */
    @Tabelavel
    @Pesquisavel
    private Long identificador;
    @Enumerated(EnumType.STRING)
    private TipoClasseESocial tipoClasse;
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoArquivoESocial tipoArquivoESocial;
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private SituacaoESocial situacao;
    @Tabelavel
    @ManyToOne
    private ConfiguracaoEmpregadorESocial empregador;
    private String observacao;
    private String xml;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    private Date dataRegistro;
    private String idESocial;
    private String recibo;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    private Date dataIntegracao;
    private Integer codigoResposta;
    private String descricaoResposta;
    @Enumerated(EnumType.STRING)
    private TipoOperacaoESocial operacao;
    private Integer codigoOcorrencia;
    private String localizacao;
    private String identificadorWP;
    @Enumerated(EnumType.STRING)
    private ClasseWP classeWP;
    private Integer mesApuracao;
    private Integer anoApuracao;
    private String descricao;


    public RegistroESocial() {
    }

    public RegistroESocial(Long identificador, TipoClasseESocial tipo, TipoArquivoESocial tipoArquivoESocial, SituacaoESocial situacao, ConfiguracaoEmpregadorESocial empregador, String observacao) {
        this.identificador = identificador;
        this.tipoClasse = tipo;
        this.tipoArquivoESocial = tipoArquivoESocial;
        this.situacao = situacao;
        this.empregador = empregador;
        this.observacao = observacao;
        this.dataRegistro = new Date();
    }

    public RegistroESocial(Long identificador, TipoClasseESocial tipo, TipoArquivoESocial tipoArquivoESocial, SituacaoESocial situacao, ConfiguracaoEmpregadorESocial empregador) {
        this.identificador = identificador;
        this.tipoClasse = tipo;
        this.tipoArquivoESocial = tipoArquivoESocial;
        this.situacao = situacao;
        this.empregador = empregador;
        this.dataRegistro = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Long identificador) {
        this.identificador = identificador;
    }

    public TipoArquivoESocial getTipoArquivoESocial() {
        return tipoArquivoESocial;
    }

    public void setTipoArquivoESocial(TipoArquivoESocial tipoArquivoESocial) {
        this.tipoArquivoESocial = tipoArquivoESocial;
    }

    public SituacaoESocial getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoESocial situacao) {
        this.situacao = situacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public ConfiguracaoEmpregadorESocial getEmpregador() {
        return empregador;
    }

    public void setEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        this.empregador = empregador;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public TipoClasseESocial getTipoClasse() {
        return tipoClasse;
    }

    public void setTipoClasse(TipoClasseESocial tipoClasse) {
        this.tipoClasse = tipoClasse;
    }

    public String getIdESocial() {
        return idESocial;
    }

    public void setIdESocial(String idESocial) {
        this.idESocial = idESocial;
    }

    public Date getDataIntegracao() {
        return dataIntegracao;
    }

    public void setDataIntegracao(Date dataIntegracao) {
        this.dataIntegracao = dataIntegracao;
    }

    public String getRecibo() {
        return recibo;
    }

    public void setRecibo(String recibo) {
        this.recibo = recibo;
    }

    public String getIdentificadorWP() {
        return identificadorWP;
    }

    public void setIdentificadorWP(String identificadorWP) {
        this.identificadorWP = identificadorWP;
    }

    public ClasseWP getClasseWP() {
        return classeWP;
    }

    public void setClasseWP(ClasseWP classeWP) {
        this.classeWP = classeWP;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static EventoESocialDTO registroESocialToEventoESocialDTO(RegistroESocial registroESocial) {
        Set<OcorrenciaESocial> ocorrencias = new HashSet<>();
        OcorrenciaESocial ocorrencia = new OcorrenciaESocialDTO();
        ocorrencia.setDescricao(registroESocial.getObservacao());
        ocorrencia.setCodigo(registroESocial.getCodigoOcorrencia());
        ocorrencia.setLocalizacao(registroESocial.getLocalizacao());
        ocorrencias.add(ocorrencia);
        EventoESocialDTO eventoESocialDTO = new EventoESocialDTO();
        eventoESocialDTO.setTipoArquivo(registroESocial.getTipoArquivoESocial());
        eventoESocialDTO.setSituacao(registroESocial.getSituacao());
        eventoESocialDTO.setReciboEntrega(registroESocial.getRecibo());
        eventoESocialDTO.setDataRegistro(registroESocial.getDataRegistro());
        eventoESocialDTO.setCodigoResposta(registroESocial.getCodigoResposta());
        eventoESocialDTO.setDescricaoResposta(registroESocial.getDescricaoResposta());
        eventoESocialDTO.setOperacao(registroESocial.getOperacao());
        eventoESocialDTO.setXML(registroESocial.getXml());
        eventoESocialDTO.setOcorrencias(ocorrencias);
        return eventoESocialDTO;
    }

    public Integer getCodigoResposta() {
        return codigoResposta;
    }

    public void setCodigoResposta(Integer codigoResposta) {
        this.codigoResposta = codigoResposta;
    }

    public String getDescricaoResposta() {
        return descricaoResposta;
    }

    public void setDescricaoResposta(String descricaoResposta) {
        this.descricaoResposta = descricaoResposta;
    }

    public TipoOperacaoESocial getOperacao() {
        return operacao;
    }

    public void setOperacao(TipoOperacaoESocial operacao) {
        this.operacao = operacao;
    }

    public Integer getCodigoOcorrencia() {
        return codigoOcorrencia;
    }

    public void setCodigoOcorrencia(Integer codigoOcorrencia) {
        this.codigoOcorrencia = codigoOcorrencia;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Integer getMesApuracao() {
        return mesApuracao;
    }

    public void setMesApuracao(Integer mesApuracao) {
        this.mesApuracao = mesApuracao;
    }

    public Integer getAnoApuracao() {
        return anoApuracao;
    }

    public void setAnoApuracao(Integer anoApuracao) {
        this.anoApuracao = anoApuracao;
    }


    public static EventoESocialDTO registroESocialToEventoESocialDTOSomenteID(RegistroESocial registroESocial) {
        EventoESocialDTO eventoESocialDTO = new EventoESocialDTO();
        eventoESocialDTO.setId(registroESocial.getIdentificador());
        eventoESocialDTO.setDescricao(registroESocial.getDescricao());
        return eventoESocialDTO;
    }
}
