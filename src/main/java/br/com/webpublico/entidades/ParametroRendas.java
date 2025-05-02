package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import br.com.webpublico.util.anotacoes.UFM;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author daniel
 */
@Entity

@Audited
@Etiqueta("Parâmetros de Rendas Patrimoniais / CEASA")
public class ParametroRendas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Índice Econômico")
    private IndiceEconomico indiceEconomico;
    @OneToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Etiqueta("Vigência Meses")
    private Long quantidadeMesesVigencia;
    @Tabelavel
    @Etiqueta("Dia Vencimento")
    @Pesquisavel
    private Long diaVencimentoParcelas;
    @Transient
    private Long criadoEm;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Lotação")
    private LotacaoVistoriadora lotacaoVistoriadora;
    @Etiqueta("Quantidade de Parcelas")
    private Integer quantidadeParcelas;
    @UFM
    @Etiqueta("Área Total (m2)")
    @Tabelavel
    @Pesquisavel
    private BigDecimal areaTotal;
    @OneToMany(mappedBy = "parametroCeasa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicoRateioCeasa> listaServicoRateioCeasa;
    @Tabelavel
    @Etiqueta("Data de Início")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataInicioContrato;
    @Tabelavel
    @Etiqueta("Data de Término")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataFimContrato;


    public List<ServicoRateioCeasa> getListaServicoRateioCeasa() {
        return listaServicoRateioCeasa;
    }

    public void setListaServicoRateioCeasa(List<ServicoRateioCeasa> listaServicoRateioCeasa) {
        this.listaServicoRateioCeasa = listaServicoRateioCeasa;
    }

    public ParametroRendas() {
        this.criadoEm = System.nanoTime();
        this.listaServicoRateioCeasa = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getDiaVencimentoParcelas() {
        return diaVencimentoParcelas;
    }

    public void setDiaVencimentoParcelas(Long diaVencimentoParcelas) {
        this.diaVencimentoParcelas = diaVencimentoParcelas;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public IndiceEconomico getIndiceEconomico() {
        return indiceEconomico;
    }

    public void setIndiceEconomico(IndiceEconomico indiceEconomico) {
        this.indiceEconomico = indiceEconomico;
    }

    public Long getQuantidadeMesesVigencia() {
        return quantidadeMesesVigencia;
    }

    public void setQuantidadeMesesVigencia(Long quantidadeMesesVigencia) {
        this.quantidadeMesesVigencia = quantidadeMesesVigencia;
    }

    public LotacaoVistoriadora getLotacaoVistoriadora() {
        return lotacaoVistoriadora;
    }

    public void setLotacaoVistoriadora(LotacaoVistoriadora lotacaoVistoriadora) {
        this.lotacaoVistoriadora = lotacaoVistoriadora;
    }

    public Integer getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(Integer quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public BigDecimal getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(BigDecimal areaTotal) {
        this.areaTotal = areaTotal;
    }

    public Date getDataInicioContrato() {
        return dataInicioContrato;
    }

    public void setDataInicioContrato(Date dataInicioContrato) {
        this.dataInicioContrato = dataInicioContrato;
    }

    public Date getDataFimContrato() {
        return dataFimContrato;
    }

    public void setDataFimContrato(Date dataFimContrato) {
        this.dataFimContrato = dataFimContrato;
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
        if (this.exercicio != null || this.exercicio.getAno() != null) {
            return this.exercicio.getAno().toString();
        } else {
            return "Parâmetro sem Exercício Definido";
        }
    }
}
