package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.tributario.consultadebitos.dtos.DamDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Tributario")
@Entity

@Audited
public class DAM implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(DAM.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número do DAM Completo")
    @Pesquisavel
    @Tabelavel
    private String numeroDAM;
    private String codigoBarras;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date emissao;
    private BigDecimal valorOriginal;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal honorarios;
    private BigDecimal correcaoMonetaria;
    private BigDecimal desconto;
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @Enumerated(EnumType.STRING)
    private Tipo tipo;
    @OneToMany(mappedBy = "DAM", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ItemDAM> itens;
    @Etiqueta("Número do DAM")
    @Pesquisavel
    private Long numero;
    @ManyToOne
    private Exercicio exercicio;
    private Integer sequencia;
    private String qrCodePIX;

    @ManyToOne
    private PIX pix;

    @Transient
    private Long criadoEm;

    public DAM() {
        juros = BigDecimal.ZERO;
        multa = BigDecimal.ZERO;
        correcaoMonetaria = BigDecimal.ZERO;
        desconto = BigDecimal.ZERO;
        honorarios = BigDecimal.ZERO;
        valorOriginal = BigDecimal.ZERO;
        vencimento = new Date();
        criadoEm = System.nanoTime();
        itens = Lists.newArrayList();
        situacao = Situacao.ABERTO;
        tipo = Tipo.UNICO;
        sequencia = 1;
    }

    public DAM(Long idDam, DAM.Situacao situacao) {
        this.id = idDam;
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public List<ItemDAM> getItens() {
        return itens;
    }

    public void setItens(List<ItemDAM> itens) {
        this.itens = itens;
    }

    public String getNumeroDAM() {
        return numeroDAM;
    }

    public void setNumeroDAM(String numeroDAM) {
        this.numeroDAM = numeroDAM;
    }

    public String getNumeroCompleto() {
        try {
            String[] split = numeroDAM.split("/");
            return split[0] + split[1];
        } catch (Exception e) {
            return numeroDAM;
        }
    }

    public BigDecimal getValorOriginal() {
        if (valorOriginal != null) {
            return valorOriginal.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public void setValorOriginal(BigDecimal valor) {
        this.valorOriginal = valor;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getJuros() {
        if (juros != null) {
            return juros.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        if (multa != null) {
            return multa.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecaoMonetaria() {
        if (correcaoMonetaria != null) {
            return correcaoMonetaria.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public void setCorrecaoMonetaria(BigDecimal correcaoMonetaria) {
        this.correcaoMonetaria = correcaoMonetaria;
    }

    public BigDecimal getDesconto() {
        if (desconto != null) {
            return desconto.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getValorTotal() {
        return (getValorOriginal().add(getHonorarios().add(getJuros().add(getMulta().add(getCorrecaoMonetaria()))))).subtract(getDesconto());
    }

    public BigDecimal getTotalAcrescimos() {
        return getHonorarios().add(getTotalAcrescimosSemHonorarios());
    }

    public BigDecimal getTotalAcrescimosSemHonorarios() {
        return (getJuros().add(getMulta().add(getCorrecaoMonetaria())));
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
        try {
            numeroDAM = numero + "/" + exercicio.getAno();
        } catch (Exception e) {
            logger.warn("Exercicio ou Numero do DAM nulo.");
            numeroDAM = numero + "";
        }
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getHonorarios() {
        if (honorarios != null) {
            return honorarios.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return numeroDAM;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public String getQrCodePIX() {
        return qrCodePIX;
    }

    public void setQrCodePIX(String qrCodePIX) {
        this.qrCodePIX = qrCodePIX;
    }

    public PIX getPix() {
        return pix;
    }

    public void setPix(PIX pix) {
        this.pix = pix;
    }

    public boolean isVencido() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dataVencimento = null;
        Date dataAtual = null;
        try {
            dataAtual = sdf.parse(sdf.format(new Date()));
            dataVencimento = sdf.parse(sdf.format(vencimento));
        } catch (Exception e) {
            logger.error("Erro ao verificar se o DAM está vencido: {}", e);
            return true;
        }
        return dataVencimento.before(dataAtual);
    }

    public DamDTO toDamDTO() {
        DamDTO dto = new DamDTO();
        dto.setId(id);
        dto.setNumeroDAM(numeroDAM);
        dto.setCodigoBarras(codigoBarras);
        dto.setVencimento(vencimento);
        dto.setEmissao(emissao);
        dto.setValorOriginal(valorOriginal);
        dto.setJuros(juros);
        dto.setMulta(multa);
        dto.setHonorarios(honorarios);
        dto.setCorrecaoMonetaria(correcaoMonetaria);
        dto.setDesconto(desconto);
        dto.setSituacao(situacao.name());
        dto.setTipo(tipo.name());
        dto.setNumero(numero);
        dto.setExercicio(exercicio.getAno());
        return dto;
    }

    public enum Situacao {

        ABERTO("Em Aberto"), CANCELADO("Cancelado"), SUBISTITUIDO("Subistituído"), PAGO("Pago");
        private String descricao;

        private Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum Tipo {

        COMPOSTO("Composto"), UNICO("Único"), SUBVENCAO("Subvenção");
        private String descricao;

        private Tipo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
