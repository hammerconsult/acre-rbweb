/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Terminal-2
 */
@Entity
@Etiqueta("Cálculo de IPTU")
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@GrupoDiagrama(nome = "Tributario")
public class CalculoIPTU extends Calculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @ManyToOne
    private Construcao construcao;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Cadastro Imobiliário")
    private CadastroImobiliario cadastroImobiliario;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "calculoIPTU")
    private List<ItemCalculoIPTU> itensCalculo;
    @ManyToOne
    private ProcessoCalculoIPTU processoCalculoIPTU;
    @OneToMany(mappedBy = "calculoIptu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OcorrenciaCalculoIPTU> ocorrenciaCalculoIPTUs;
    @OneToMany(mappedBy = "calculoIPTU", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemoriaCaluloIPTU> memorias;
    @OneToMany(mappedBy = "calculo", cascade = CascadeType.ALL)
    private List<CarneIPTU> carnes;
    @ManyToOne
    private IsencaoCadastroImobiliario isencaoCadastroImobiliario;
    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Etiqueta("Exercício")
    private Integer exercicioTabelavel;
    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data do Cálculo")
    private Date dataCalculoTabelavel;
    @Transient
    @Tabelavel
    @Etiqueta("Processo")
    private String descricaoProcessoTabelavel;
    @Transient
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Valor Calculado")
    @Monetario
    private BigDecimal valorCalculadoTabelavel;
    @Transient
    @Tabelavel
    @Etiqueta("Protocolo")
    private String descricaoProtocoloTabelavel;

    public CalculoIPTU() {
        itensCalculo = Lists.newArrayList();
        ocorrenciaCalculoIPTUs = Lists.newArrayList();
        memorias = Lists.newArrayList();
        carnes = Lists.newArrayList();
        super.setTipoCalculo(TipoCalculo.IPTU);
    }

    public CalculoIPTU(Long id, CadastroImobiliario cadastroImobiliario, Exercicio exercicio, Date dataCaculo, String descricaoProcesso, BigDecimal valorEfetivo, String numeroProtocolo, String anoProtocolo) {
        setId(id);
        this.cadastroImobiliario = cadastroImobiliario;
        this.exercicioTabelavel = exercicio.getAno();
        this.dataCalculoTabelavel = dataCaculo;
        this.descricaoProcessoTabelavel = descricaoProcesso;
        this.valorCalculadoTabelavel = valorEfetivo;
        this.descricaoProtocoloTabelavel = numeroProtocolo != null ? numeroProtocolo + "/" + anoProtocolo : "";
    }

    public CalculoIPTU(CalculoIPTU calculoIPTU, Long id) {
        setId(id);
        this.cadastroImobiliario = calculoIPTU.getCadastroImobiliario();
        itensCalculo = Lists.newArrayList();
        ocorrenciaCalculoIPTUs = Lists.newArrayList();
        memorias = Lists.newArrayList();
        carnes = Lists.newArrayList();
        super.setTipoCalculo(TipoCalculo.IPTU);
    }

    public List<OcorrenciaCalculoIPTU> getOcorrenciaCalculoIPTUs() {
        return ocorrenciaCalculoIPTUs;
    }

    public void setOcorrenciaCalculoIPTUs(List<OcorrenciaCalculoIPTU> ocorrenciaCalculoIPTUs) {
        this.ocorrenciaCalculoIPTUs = ocorrenciaCalculoIPTUs;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
        setCadastro(cadastroImobiliario);
    }

    public Construcao getConstrucao() {
        return construcao;
    }

    public void setConstrucao(Construcao construcao) {
        this.construcao = construcao;
    }

    public ProcessoCalculoIPTU getProcessoCalculoIPTU() {
        return processoCalculoIPTU;
    }

    public void setProcessoCalculoIPTU(ProcessoCalculoIPTU processoCalculoIPTU) {
        super.setProcessoCalculo(processoCalculoIPTU);
        this.processoCalculoIPTU = processoCalculoIPTU;
    }

    public List<ItemCalculoIPTU> getItensCalculo() {
        if (itensCalculo != null) {
            Collections.sort(itensCalculo);
        }
        return itensCalculo;
    }

    public void setItensCalculo(List<ItemCalculoIPTU> valorTributos) {
        this.itensCalculo = valorTributos;
    }

    public List<MemoriaCaluloIPTU> getMemorias() {
        return memorias;
    }

    public List<CarneIPTU> getCarnes() {
        return carnes;
    }

    public void setCarnes(List<CarneIPTU> carnes) {
        this.carnes = carnes;
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = new BigDecimal(0);
        for (ItemCalculoIPTU c : this.itensCalculo) {
            total = total.add(c.getValorReal());
        }
        return total;
    }

    public void setMemorias(List<MemoriaCaluloIPTU> memorias) {
        this.memorias = memorias;
    }

    public IsencaoCadastroImobiliario getIsencaoCadastroImobiliario() {
        return isencaoCadastroImobiliario;
    }

    public void setIsencaoCadastroImobiliario(IsencaoCadastroImobiliario isencaoCadastroImobiliario) {
        this.isencaoCadastroImobiliario = isencaoCadastroImobiliario;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        String retorno = "";
        if (cadastroImobiliario != null) {
            retorno += cadastroImobiliario.getCodigo();
        }
        if (construcao != null) {
            retorno += " - " + construcao.getDescricao();
        }
        return retorno;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoIPTU;
    }

    public void defineReferencia() {
        StringBuilder sb = new StringBuilder();
        sb.append(processoCalculoIPTU.getDescricao());
        super.setReferencia(sb.toString());
    }

    public Integer getExercicioTabelavel() {
        return exercicioTabelavel;
    }

    public void setExercicioTabelavel(Integer exercicioTabelavel) {
        this.exercicioTabelavel = exercicioTabelavel;
    }

    public Date getDataCalculoTabelavel() {
        return dataCalculoTabelavel;
    }

    public void setDataCalculoTabelavel(Date dataCalculoTabelavel) {
        this.dataCalculoTabelavel = dataCalculoTabelavel;
    }

    public BigDecimal getValorCalculadoTabelavel() {
        return valorCalculadoTabelavel;
    }

    public void setValorCalculadoTabelavel(BigDecimal valorCalculadoTabelavel) {
        this.valorCalculadoTabelavel = valorCalculadoTabelavel;
    }

    public String getDescricaoProcessoTabelavel() {
        return descricaoProcessoTabelavel;
    }

    public void setDescricaoProcessoTabelavel(String descricaoProcessoTabelavel) {
        this.descricaoProcessoTabelavel = descricaoProcessoTabelavel;
    }

    public String getDescricaoProtocoloTabelavel() {
        return descricaoProtocoloTabelavel;
    }

    public void setDescricaoProtocoloTabelavel(String descricaoProtocoloTabelavel) {
        this.descricaoProtocoloTabelavel = descricaoProtocoloTabelavel;
    }
}
