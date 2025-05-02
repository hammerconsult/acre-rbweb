/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gustavo
 */
@Entity

@Audited
@GrupoDiagrama(nome = "DividaAtiva")
public class Peticao implements Serializable, Comparable<Peticao> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataEmissao;
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    private Cadastro cadastro;
    private Long codigo;
    @Enumerated(EnumType.STRING)
    private SituacaoPeticao situacaoPeticao;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    @OneToMany(mappedBy = "peticao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPeticao> itensPeticao;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private VaraCivel varaCivel;


    public VaraCivel getVaraCivel() {
        return varaCivel;
    }

    public void setVaraCivel(VaraCivel varaCivel) {
        this.varaCivel = varaCivel;
    }

    public Peticao() {
        itensPeticao = new ArrayList<>();
        criadoEm = System.nanoTime();
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<ItemPeticao> getItensPeticao() {
        return itensPeticao;
    }

    public void setItensPeticao(List<ItemPeticao> itensPeticao) {
        this.itensPeticao = itensPeticao;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public SituacaoPeticao getSituacaoPeticao() {
        return situacaoPeticao;
    }

    public void setSituacaoPeticao(SituacaoPeticao situacaoPeticao) {
        this.situacaoPeticao = situacaoPeticao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
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

    @Override
    public String toString() {
        return codigo + "/" + exercicio + " " + pessoa;
    }

    @Override
    public int compareTo(Peticao o) {
        int retorno = this.codigo.compareTo(o.getCodigo());
        if (retorno == 0) {
            retorno = this.getExercicio().compareTo(o.getExercicio());
        }
        return retorno;
    }


    public enum SituacaoPeticao {

        EM_ABERTO("Em Aberto"),
        FINALIZADO("Finalizado"),
        CANCELADO("Cancelado");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private SituacaoPeticao(String descricao) {
            this.descricao = descricao;
        }
    }
}
