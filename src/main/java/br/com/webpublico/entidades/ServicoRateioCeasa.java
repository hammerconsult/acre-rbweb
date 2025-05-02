package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 27/12/13
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ServicoRateioCeasa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    private Integer codigo;
    private String descricao;
    private BigDecimal valor;
    @ManyToOne
    private ParametroRendas parametroCeasa;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ServicoRateioCeasa() {
        this.criadoEm = System.currentTimeMillis();
    }

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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ParametroRendas getParametroCeasa() {
        return parametroCeasa;
    }

    public void setParametroCeasa(ParametroRendas parametroCeasa) {
        this.parametroCeasa = parametroCeasa;
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
        return descricao;
    }
}
