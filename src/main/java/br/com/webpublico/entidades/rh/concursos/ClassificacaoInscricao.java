package br.com.webpublico.entidades.rh.concursos;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.CriterioDesempate;
import br.com.webpublico.enums.rh.concursos.StatusClassificacaoInscricao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;

@Entity
@Audited
public class ClassificacaoInscricao extends SuperEntidade implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classificação")
    private ClassificacaoConcurso classificacaoConcurso;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Candidato")
    private InscricaoConcurso inscricaoConcurso;
    @Etiqueta("Status da Classificação")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private StatusClassificacaoInscricao status;
    @Etiqueta("Posição")
    private Integer posicao;
    @Etiqueta("Observações")
    private String observacoes;
    @Invisivel
    @Etiqueta("Pontuação")
    private Integer pontuacao;
    @Invisivel
    @Etiqueta("Média")
    private BigDecimal media;
    @Etiqueta(value = "Data Convocação")
    @Temporal(TemporalType.DATE)
    @Tabelavel
    private Date convocadoEm;
    @Etiqueta("Apresentou-se?")
    private Boolean apresentouSe;
    @OneToOne
    @Etiqueta("Servidor")
    private ContratoFP contratoFP;
    @Transient
    @Invisivel
    private Integer pontuacaoDaRodada;
    @Invisivel
    @Transient
    private Boolean convocado;

    @Invisivel
    @Transient
    private Long idPessoa;
    @Invisivel
    @Transient
    private Long idMatricula;


    public Boolean getApresentouSe() {
        if (apresentouSe == null) {
            apresentouSe = Boolean.FALSE;
        }
        return apresentouSe;
    }

    public void setApresentouSe(Boolean apresentouSe) {
        this.apresentouSe = apresentouSe;
    }

    public Integer getPontuacaoDaRodada() {
        return pontuacaoDaRodada;
    }

    public void setPontuacaoDaRodada(Integer pontuacaoDaRodada) {
        this.pontuacaoDaRodada = pontuacaoDaRodada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public InscricaoConcurso getInscricaoConcurso() {
        return inscricaoConcurso;
    }

    public void setInscricaoConcurso(InscricaoConcurso inscricaoConcurso) {
        this.inscricaoConcurso = inscricaoConcurso;
    }

    public ClassificacaoConcurso getClassificacaoConcurso() {
        return classificacaoConcurso;
    }

    public void setClassificacaoConcurso(ClassificacaoConcurso classificacaoConcurso) {
        this.classificacaoConcurso = classificacaoConcurso;
    }

    public Integer getPosicao() {
        return posicao;
    }

    public void setPosicao(Integer posicao) {
        this.posicao = posicao;
    }

    public String getPosicaoComIndicadorOrdinal() {
        return posicao + "º";
    }

    @Override
    public int compareTo(Object o) {
        return this.getPosicao().compareTo(((ClassificacaoInscricao) o).getPosicao());
    }

    public Integer getPontuacao() {
        if (pontuacao == null) {
            pontuacao = 0;
        }
        return pontuacao;
    }

    public void setPontuacao(Integer pontuacao) {
        this.pontuacao = pontuacao;
    }

    public BigDecimal getMedia() {
        return media;
    }

    public void setMedia(BigDecimal media) {
        this.media = media;
    }

    public String getMediaAsString() {
        DecimalFormat formato = new DecimalFormat("#0.00");
        formato.setMaximumFractionDigits(2);
        DecimalFormatSymbols padrao = new DecimalFormatSymbols();
        padrao.setDecimalSeparator('.');
        formato.setDecimalFormatSymbols(padrao);
        return formato.format(media);
    }

    public void pontuarPorDesempate(CriterioDesempate desempate) {
        switch (desempate) {
            case IDADE:
                pontuarQuandoIdade();
                break;
            case JURADO:
                pontuarQuandoJurado(1);
                break;
            case MESARIO:
                pontuarQuandoMesario(1);
                break;
            case DOADOR:
                pontuarQuandoDoador(1);
                break;
            case CARGO_PUBLICO:
                pontuarQuandoCargoPublico(1);
                break;
        }
    }

    private void pontuarQuandoDoador(int fator) {
        if (getInscricaoConcurso().getDoador()) {
            adicionarNosPontos(fator);
        }
    }

    private void pontuarQuandoMesario(int fator) {
        if (getInscricaoConcurso().getMesario()) {
            adicionarNosPontos(fator);
        }
    }

    private void pontuarQuandoJurado(int fator) {
        if (getInscricaoConcurso().getJurado()) {
            adicionarNosPontos(fator);
        }
    }

    private void pontuarQuandoCargoPublico(int fator) {
        if (getInscricaoConcurso().getCargoPublico()) {
            adicionarNosPontos(fator);
        }
    }

    private void pontuarQuandoIdade() {
        Integer diasDeVida = DataUtil.diferencaDiasInteira(getInscricaoConcurso().getDataNascimento(), UtilRH.getDataOperacao());
        adicionarNosPontos(diasDeVida);
    }

    public void adicionarNosPontos(Integer valor) {
        if (pontuacaoDaRodada == null) {
            pontuacaoDaRodada = valor;
        }
        pontuacaoDaRodada += valor;
    }

    public StatusClassificacaoInscricao getStatus() {
        return status;
    }

    public void setStatus(StatusClassificacaoInscricao status) {
        this.status = status;
    }

    public void addObservacoes(String obs) {
        if (observacoes == null) {
            observacoes = "";
        }
        observacoes += obs;
        observacoes += "<br/>";
    }

    public Date getConvocadoEm() {
        return convocadoEm;
    }

    public void setConvocadoEm(Date convocadoEm) {
        this.convocadoEm = convocadoEm;
    }

    public Boolean getConvocado() {
        if (convocadoEm != null) {
            convocado = true;
        }
        return convocado;
    }

    public void setConvocado(Boolean convocado) {
        if (convocado) {
            setConvocadoEm(UtilRH.getDataOperacao());
        } else {
            setConvocadoEm(null);
        }
        this.convocado = convocado;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Long getIdMatricula() {
        return idMatricula;
    }

    public void setIdMatricula(Long idMatricula) {
        this.idMatricula = idMatricula;
    }
}
