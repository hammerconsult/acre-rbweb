package br.com.webpublico.entidades;


import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by William on 28/06/2016.
 */
@Audited
@Entity
@Etiqueta("Edital de Contribuição de Melhoria")
@Table(name = "CONTRIBMELHORIAEDITAL")
public class ContribuicaoMelhoriaEdital extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Integer codigo;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Lei/Decreto/Ato Legal")
    private AtoLegal atoLegal;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data do Edital")
    private Date dataEdital;

    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data da Publicação")
    private Date dataPublicacao;

    @Obrigatorio
    @Etiqueta("Área Total de Abrangência (m²)")
    private BigDecimal area;

    @Etiqueta("Custo por m² (R$)")
    @Obrigatorio
    private BigDecimal custoMetroQuadrado;

    @Obrigatorio
    @Etiqueta("Custo Total (R$)")
    private BigDecimal custoTotal;

    @Obrigatorio
    @Etiqueta("Texto")
    private String texto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getDataEdital() {
        return dataEdital;
    }

    public void setDataEdital(Date dataEdital) {
        this.dataEdital = dataEdital;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getCustoMetroQuadrado() {
        return custoMetroQuadrado;
    }

    public void setCustoMetroQuadrado(BigDecimal custoMetroQuadrado) {
        this.custoMetroQuadrado = custoMetroQuadrado;
    }

    public BigDecimal getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(BigDecimal custoTotal) {
        this.custoTotal = custoTotal;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString() {
        return this.getDescricao() + " - " + DataUtil.getDataFormatada(this.getDataEdital());
    }


}


