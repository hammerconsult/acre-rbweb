package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Plano de Contas")
public class PlanoDeContas implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta(value = "Exercício")
    private Exercicio exercicio;
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Etiqueta("Tipo de Conta")
    @Pesquisavel
    private TipoConta tipoConta;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    @Invisivel
    @OneToMany(mappedBy = "planoDeContas", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<Conta> contas;
    @Invisivel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Transient
    private Long criadoEm;

    public PlanoDeContas() {
        dataRegistro = new Date();
        contas = new ArrayList<Conta>();
        criadoEm = System.nanoTime();
    }

    public PlanoDeContas(String descricao, TipoConta tipoConta, List<Conta> contas) {
        this.descricao = descricao;
        this.tipoConta = tipoConta;
        this.contas = contas;
    }

    public PlanoDeContas(String descricao, TipoConta tipoConta, List<Conta> contas, Date dataRegistro) {
        this.descricao = descricao;
        this.tipoConta = tipoConta;
        this.contas = contas;
        this.dataRegistro = dataRegistro;
    }

    public TipoConta getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(TipoConta tipoConta) {
        this.tipoConta = tipoConta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Conta> getContas() {
        Collections.sort(contas);
        return contas;
    }

    public void setContas(List<Conta> contas) {
        this.contas = contas;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
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
        try {
            return this.descricao.toString() + " - " + this.exercicio.getAno();
        } catch (Exception e) {
            if (this.descricao == null) {
                return "";
            } else {
                return this.descricao.toString();
            }
        }

    }
}
