package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 19/09/13
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "PPA")
@Entity
@Etiqueta("Produto PPA")
@Audited
public class ProdutoPPA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Ação Principal")
    private AcaoPrincipal acaoPrincipal;
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Etiqueta("Código")
    private String codigo;
    private BigDecimal totalFisico;
    private BigDecimal totalFinanceiro;
    @Etiqueta("Provisões")
    @OneToMany(mappedBy = "produtoPPA", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProvisaoPPA> provisoes;
    @Invisivel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToOne
    private ProdutoPPA origem;
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;
    //criado para migração
    @Invisivel
    @ManyToOne
    private Exercicio exercicio;
    @Transient
    private Long criadoEm;

    public ProdutoPPA() {
        somenteLeitura = false;
        criadoEm = System.nanoTime();
    }

    public ProdutoPPA(AcaoPrincipal acaoPrincipal, String descricao, String codigo, BigDecimal totalFisico, BigDecimal totalFinanceiro, List<ProvisaoPPA> provisoes, Date dataRegistro, ProdutoPPA origem, Boolean somenteLeitura, Exercicio exercicio) {
        this.acaoPrincipal = acaoPrincipal;
        this.descricao = descricao;
        this.codigo = codigo;
        this.totalFisico = totalFisico;
        this.totalFinanceiro = totalFinanceiro;
        this.provisoes = provisoes;
        this.dataRegistro = dataRegistro;
        this.origem = origem;
        this.somenteLeitura = somenteLeitura;
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AcaoPrincipal getAcaoPrincipal() {
        return acaoPrincipal;
    }

    public void setAcaoPrincipal(AcaoPrincipal acaoPrincipal) {
        this.acaoPrincipal = acaoPrincipal;
    }

    public void setOrigem(ProdutoPPA origem) {
        this.origem = origem;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ProdutoPPA getOrigem() {
        return origem;
    }

    public List<ProvisaoPPA> getProvisoes() {
        if(provisoes != null){
            Collections.sort(provisoes);
        }
        return provisoes;
    }

    public void setProvisoes(List<ProvisaoPPA> provisoes) {
        this.provisoes = provisoes;
    }

    public BigDecimal getTotalFinanceiro() {
        return totalFinanceiro;
    }

    public void setTotalFinanceiro(BigDecimal totalFinanceiro) {
        this.totalFinanceiro = totalFinanceiro;
    }

    public BigDecimal getTotalFisico() {
        return totalFisico;
    }

    public void setTotalFisico(BigDecimal totalFisico) {
        this.totalFisico = totalFisico;
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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
        return codigo + " - " + descricao;
    }

    public BigDecimal getSomaMetaFinanceiraPrevista() {
        BigDecimal soma = BigDecimal.ZERO;
        if (provisoes != null) {
            for (ProvisaoPPA prov : provisoes) {
                soma = soma.add(prov.getMetaFinanceiraCapital().add(prov.getMetaFinanceiraCorrente()));
            }
        }
        return soma;
    }

    public BigDecimal getSomaMetaFisicaPrevista() {
        BigDecimal soma = BigDecimal.ZERO;
        if (provisoes != null) {
            for (ProvisaoPPA prov : provisoes) {
                soma = soma.add(prov.getMetaFisica());
            }
        }
        return soma;
    }

    public BigDecimal getSomaMetaFinanceiraExecutada() {
        BigDecimal soma = BigDecimal.ZERO;
        if (provisoes != null) {
            for (ProvisaoPPA prov : provisoes) {
                soma = soma.add(prov.getMetaFinanceiraCapital().add(prov.getMetaFinancCapitalExecutada()));
            }
        }
        return soma;
    }

    public BigDecimal getSomaMetaFisicaExecutada() {
        BigDecimal soma = BigDecimal.ZERO;
        if (provisoes != null) {
            for (ProvisaoPPA prov : provisoes) {
                soma = soma.add(prov.getMetaFisicaExecutada());
            }
        }
        return soma;
    }


    public Boolean validaSomaFinanceiraProvisoes(ProdutoPPA produtoPPA) {
        BigDecimal soma = BigDecimal.ZERO;
        if (produtoPPA != null) {
            for (ProvisaoPPA prov : produtoPPA.getProvisoes()) {
                soma = soma.add(prov.getMetaFinanceiraCapital().add(prov.getMetaFinanceiraCorrente()));
            }
            if (soma.compareTo(produtoPPA.getTotalFinanceiro()) != 0) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public Boolean validaSomaFisicaProvisoes(ProdutoPPA produtoPPA) {
        BigDecimal soma = BigDecimal.ZERO;
        if (produtoPPA != null) {
            for (ProvisaoPPA prov : produtoPPA.getProvisoes()) {
                soma = soma.add(prov.getMetaFisica());
            }
            if (produtoPPA.getTotalFisico().compareTo(BigDecimal.ZERO) != 0) {
                if (soma.compareTo(produtoPPA.getTotalFisico()) < 0) {
                    return true;
                } else if (soma.compareTo(produtoPPA.getTotalFisico()) == 1) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        return false;
    }
}
