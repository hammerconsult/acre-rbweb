package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoDenuncia;
import br.com.webpublico.enums.SituacaoFinalDenuncia;
import br.com.webpublico.enums.TipoOrigemDenuncia;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Fabio
 */
@GrupoDiagrama(nome = "Tributário")
@Entity

@Audited
@Etiqueta(value = "Denúncia")
public class Denuncia implements Serializable, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta(value = "Número")
    private Long numero;
    @Transient
    @Tabelavel
    @Etiqueta(value = "Código")
    private String codigoCompleto;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Exercício")
    @ManyToOne
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Origem")
    private TipoOrigemDenuncia tipoOrigemDenuncia;
    @Tabelavel
    @Etiqueta("Referência da Origem")
    private String referenciaOrigem;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Secretaria")
    @ManyToOne
    private SecretariaFiscalizacao secretariaFiscalizacao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Data")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataDenuncia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoDenuncia situacao;
    @Etiqueta(value = "Denunciante")
    @ManyToOne(cascade = CascadeType.ALL)
    @Tabelavel
    private PessoaDenuncia denunciante;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Tipo de Denúncia")
    @ManyToOne
    private TipoDenuncia tipoDenuncia;
    @ManyToOne(cascade = CascadeType.ALL)
    @Tabelavel
    @Etiqueta(value = "Denunciado")
    private PessoaDenuncia denunciado;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    private String detalhamento;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "denuncia", orphanRemoval = true)
    private List<DenunciaFiscaisDesignados> denunciaFisciasDesignados;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "denuncia", orphanRemoval = true)
    private List<DenunciaOcorrencias> denunciaOcorrencias;
    private String parecerFinal;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataFinal;
    @Enumerated(EnumType.STRING)
    private SituacaoFinalDenuncia situacaoFinalDenuncia;
    @Transient
    private Long criadoEm;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;
    private String latitude;
    private String longitude;



    public Denuncia() {
        denunciaFisciasDesignados = Lists.newArrayList();
        denunciaOcorrencias = Lists.newArrayList();
        criadoEm = System.nanoTime();
        this.setLatitude("-9.9823576");
        this.setLongitude("-67.8271484");
    }

    public Denuncia(Denuncia denuncia) {
        this.id = denuncia.getId();
        this.secretariaFiscalizacao = denuncia.getSecretariaFiscalizacao();
        this.numero = denuncia.getNumero();
        this.exercicio = denuncia.getExercicio();
        this.dataDenuncia = denuncia.getDataDenuncia();
        this.usuarioSistema = denuncia.getUsuarioSistema();
        this.situacao = denuncia.getSituacao();
        this.denunciante = denuncia.getDenunciante();
        this.denunciado = denuncia.getDenunciado();
        this.tipoDenuncia = denuncia.getTipoDenuncia();
        this.detalhamento = denuncia.getDetalhamento();
        this.denunciaFisciasDesignados = denuncia.getDenunciaFisciasDesignados();
        this.denunciaOcorrencias = denuncia.getDenunciaOcorrencias();
        this.parecerFinal = denuncia.getParecerFinal();
        this.dataFinal = denuncia.getDataFinal();
        this.situacaoFinalDenuncia = denuncia.getSituacaoFinalDenuncia();
    }

    public void setCodigoCompleto(String codigoCompleto) {
        this.codigoCompleto = codigoCompleto;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getReferenciaOrigem() {
        return referenciaOrigem;
    }

    public void setReferenciaOrigem(String referenciaOrigem) {
        this.referenciaOrigem = referenciaOrigem;
    }

    public TipoOrigemDenuncia getTipoOrigemDenuncia() {
        return tipoOrigemDenuncia;
    }

    public void setTipoOrigemDenuncia(TipoOrigemDenuncia tipoOrigemDenuncia) {
        this.tipoOrigemDenuncia = tipoOrigemDenuncia;
    }

    public Date getDataDenuncia() {
        return dataDenuncia;
    }

    public void setDataDenuncia(Date dataDenuncia) {
        this.dataDenuncia = dataDenuncia;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public List<DenunciaFiscaisDesignados> getDenunciaFisciasDesignados() {
        return denunciaFisciasDesignados;
    }

    public void setDenunciaFisciasDesignados(List<DenunciaFiscaisDesignados> denunciaFisciasDesignados) {
        this.denunciaFisciasDesignados = denunciaFisciasDesignados;
    }

    public List<DenunciaOcorrencias> getDenunciaOcorrencias() {
        return denunciaOcorrencias;
    }

    public void setDenunciaOcorrencias(List<DenunciaOcorrencias> denunciaOcorrencias) {
        this.denunciaOcorrencias = denunciaOcorrencias;
    }

    public PessoaDenuncia getDenunciante() {
        return denunciante;
    }

    public void setDenunciante(PessoaDenuncia denunciante) {
        this.denunciante = denunciante;
    }

    public PessoaDenuncia getDenunciado() {
        return denunciado;
    }

    public void setDenunciado(PessoaDenuncia denunciado) {
        this.denunciado = denunciado;
    }

    public String getDetalhamento() {
        return detalhamento;
    }

    public void setDetalhamento(String detalhamento) {
        this.detalhamento = detalhamento;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getParecerFinal() {
        return parecerFinal;
    }

    public void setParecerFinal(String parecerFinal) {
        this.parecerFinal = parecerFinal;
    }

    public SituacaoDenuncia getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoDenuncia situacao) {
        this.situacao = situacao;
    }

    public TipoDenuncia getTipoDenuncia() {
        return tipoDenuncia;
    }

    public void setTipoDenuncia(TipoDenuncia tipoDenuncia) {
        this.tipoDenuncia = tipoDenuncia;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public SituacaoFinalDenuncia getSituacaoFinalDenuncia() {
        return situacaoFinalDenuncia;
    }

    public void setSituacaoFinalDenuncia(SituacaoFinalDenuncia situacaoFinalDenuncia) {
        this.situacaoFinalDenuncia = situacaoFinalDenuncia;
    }

    public SecretariaFiscalizacao getSecretariaFiscalizacao() {
        return secretariaFiscalizacao;
    }

    public void setSecretariaFiscalizacao(SecretariaFiscalizacao secretariaFiscalizacao) {
        this.secretariaFiscalizacao = secretariaFiscalizacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getCodigoCompleto() {
        if (codigoCompleto == null || codigoCompleto.isEmpty()) {
            defineCodigoCompleto();
        }
        return codigoCompleto;
    }

    public void defineCodigoCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.cortaOuCompletaEsquerda(String.valueOf(numero), 4, "0"));
        sb.append(".");
        sb.append(tipoOrigemDenuncia.getCodigo());
        sb.append(".");
        sb.append(exercicio.getAno());
        codigoCompleto = sb.toString();

    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (numero != null) {
            sb.append(numero);
        }
        if (exercicio != null) {
            sb.append("/").append(exercicio.getAno());
        }

        if (tipoOrigemDenuncia != null) {
            sb.append(" ").append(tipoOrigemDenuncia.getDescricao());
        }

        if (referenciaOrigem != null) {
            sb.append(" ").append(referenciaOrigem);
        }

        if (dataDenuncia != null) {
            sb.append(" - ").append(Util.dateToString(dataDenuncia));
        }

        if (tipoDenuncia != null) {
            sb.append(" - ").append(tipoDenuncia.getDescricao());
        }

        return sb.toString();
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
