package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author daniel
 */
@Entity

@Audited
@Etiqueta("Tipo de Autônomo")
public class TipoAutonomo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Código")
    @Pesquisavel
    private Long codigo;
    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    @Tabelavel
    @Etiqueta("Gera ISS")
    @Pesquisavel
    private Boolean geraISS;
    @Tabelavel
    @Etiqueta("Edita enquadramento")
    @Pesquisavel
    private Boolean editaEnquadramento;
    @Tabelavel
    @Etiqueta("Valor do ISS (UFM)")
    @Pesquisavel
    private BigDecimal valorUFMRB;
    @Transient
    private Long criadoEm;
    @Tabelavel
    @Etiqueta("Necessita Placa")
    @Pesquisavel
    private Boolean necessitaPlacas;
    @Tabelavel
    @Etiqueta("Qtde Placa")
    @Pesquisavel
    private Integer qtdPlacas;
    @Tabelavel
    @Etiqueta("Quantidade de Cadastros")
    @Pesquisavel
//    private Boolean quantidadeCadastroRBTrans;
    private Integer quantidadeCadastroRBTrans;

    public TipoAutonomo() {
        this.necessitaPlacas = false;
        this.geraISS = false;
        this.editaEnquadramento = false;
        this.criadoEm = System.nanoTime();
    }

    public TipoAutonomo(String desc) {
        this.descricao = desc;
        this.necessitaPlacas = false;
        this.geraISS = false;
        this.editaEnquadramento = false;
        this.criadoEm = System.nanoTime();
    }

    public Boolean getNecessitaPlacas() {
        return necessitaPlacas;
    }

    public void setNecessitaPlacas(Boolean necessitaPlacas) {
        this.necessitaPlacas = necessitaPlacas;
    }

    public Integer getQtdPlacas() {
        return qtdPlacas;
    }

    public void setQtdPlacas(Integer qtdPlacas) {
        this.qtdPlacas = qtdPlacas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getGeraISS() {
        return geraISS;
    }

    public void setGeraISS(Boolean geraISS) {
        this.geraISS = geraISS;
    }

    public Boolean getEditaEnquadramento() {
        return editaEnquadramento;
    }

    public void setEditaEnquadramento(Boolean editaEnquadramento) {
        this.editaEnquadramento = editaEnquadramento;
    }

    public BigDecimal getValorUFMRB() {
        return valorUFMRB;
    }

    public void setValorUFMRB(BigDecimal valorUFMRB) {
        this.valorUFMRB = valorUFMRB;
    }

    public Integer getQuantidadeCadastroRBTrans() {
        return quantidadeCadastroRBTrans == null ? 0 : quantidadeCadastroRBTrans;
    }

    public void setQuantidadeCadastroRBTrans(Integer quantidadeCadastroRBTrans) {
        this.quantidadeCadastroRBTrans = quantidadeCadastroRBTrans;
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
        return descricao;
    }
}
