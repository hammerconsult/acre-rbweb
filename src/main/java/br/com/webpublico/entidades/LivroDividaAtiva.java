/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author munif
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Divida Ativa")
@Etiqueta("Livro de Dívida Ativa")
public class LivroDividaAtiva implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Tabelavel
    @Pesquisavel
    private Long numero;
    @Tabelavel
    @Etiqueta("Total de Páginas")
    private Long totalPaginas;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Cadastro")
    private TipoCadastroTributario tipoCadastroTributario;
    private Long sequencia;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "livroDividaAtiva")
    private List<ItemLivroDividaAtiva> itensLivros;
    @Transient
    @Invisivel
    private Long criadoEm;
    @Transient
    public final Long QUANTIDADE_LINHA_POR_PAGINA = 18l;
    @Enumerated(EnumType.STRING)
    private TipoOrdenacao tipoOrdenacao;
    private String migracaochave;
    @Transient
    private Long idExercicio;

    public LivroDividaAtiva() {
        this.criadoEm = System.nanoTime();
        itensLivros = new ArrayList<>();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemLivroDividaAtiva> getItensLivros() {
        return itensLivros;
    }

    public void setItensLivros(List<ItemLivroDividaAtiva> itensLivros) {
        this.itensLivros = itensLivros;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Long getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(Long totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    public TipoOrdenacao getTipoOrdenacao() {
        return tipoOrdenacao;
    }

    public void setTipoOrdenacao(TipoOrdenacao tipoOrdenacao) {
        this.tipoOrdenacao = tipoOrdenacao;
    }

    public Long getIdExercicio() {
        if(idExercicio == null && exercicio != null){
            return exercicio.getId();
        }
        return idExercicio;
    }

    public void setIdExercicio(Long idExercicio) {
        this.idExercicio = idExercicio;
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

        return "br.com.webpublico.entidades.novas.LivroDividaAtiva[ id=" + id + " ]";
    }

    public enum TipoOrdenacao {

        NUMERICA("Nº do Cadastro"),
        ALFABETICA("Nome do Contribuinte");
        private String descricao;

        private TipoOrdenacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public String getMigracaochave() {
        return migracaochave;
    }

    public void setMigracaochave(String migracaochave) {
        this.migracaochave = migracaochave;
    }

}
