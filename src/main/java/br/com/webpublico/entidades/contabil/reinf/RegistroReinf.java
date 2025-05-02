package br.com.webpublico.entidades.contabil.reinf;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.domain.OcorrenciaESocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Etiqueta("Registro Efd-reinf")
public class RegistroReinf extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    private Long identificador;
    @Tabelavel
    @Pesquisavel
    private String tipo;
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoArquivoReinf tipoArquivo;
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
    private String idXmlEvento;


    public RegistroReinf() {
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public TipoArquivoReinf getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(TipoArquivoReinf tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public SituacaoESocial getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoESocial situacao) {
        this.situacao = situacao;
    }

    public ConfiguracaoEmpregadorESocial getEmpregador() {
        return empregador;
    }

    public void setEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        this.empregador = empregador;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getIdESocial() {
        return idESocial;
    }

    public void setIdESocial(String idESocial) {
        this.idESocial = idESocial;
    }

    public String getRecibo() {
        return recibo;
    }

    public void setRecibo(String recibo) {
        this.recibo = recibo;
    }

    public Date getDataIntegracao() {
        return dataIntegracao;
    }

    public void setDataIntegracao(Date dataIntegracao) {
        this.dataIntegracao = dataIntegracao;
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

    public String getIdXmlEvento() {
        return idXmlEvento;
    }

    public void setIdXmlEvento(String idXmlEvento) {
        this.idXmlEvento = idXmlEvento;
    }
}
