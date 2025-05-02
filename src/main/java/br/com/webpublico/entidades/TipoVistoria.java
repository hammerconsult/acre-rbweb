package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
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
@GrupoDiagrama(nome = "Alvara")
@Etiqueta("Tipo Vistoria")
public class TipoVistoria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Valor UFMRB")
    private BigDecimal valorUFMRB;
    @Tabelavel
    @Etiqueta("Vigilância")
    private Boolean vigilancia;
    @Tabelavel
    @Etiqueta("Vigência Inicío")
    @Temporal(TemporalType.DATE)
    private Date vigenciaInicio;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Vigência Fim")
    private Date vigenciaFim;
    @Transient
    private Long criadoEm;

    public TipoVistoria() {
        this.criadoEm = System.nanoTime();

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorUFMRB() {
        return valorUFMRB;
    }

    public void setValorUFMRB(BigDecimal valorUFMRB) {
        this.valorUFMRB = valorUFMRB;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public Date getVigenciaFim() {
        return vigenciaFim;
    }

    public void setVigenciaFim(Date vigenciaFim) {
        this.vigenciaFim = vigenciaFim;
    }

    public Date getVigenciaInicio() {
        return vigenciaInicio;
    }

    public void setVigenciaInicio(Date vigenciaInicio) {
        this.vigenciaInicio = vigenciaInicio;
    }

    public Boolean getVigilancia() {
        return vigilancia;
    }

    public void setVigilancia(Boolean vigilancia) {
        this.vigilancia = vigilancia;
    }

    @Override
    public String toString() {
        return this.descricao;// + " (" + this.cnae.getCodigoCnae() + ") - " + valorUFMRB + " UFMRBs";
    }
}
