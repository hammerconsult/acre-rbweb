package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hudson on 02/10/2015.
 */

@Entity
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("RetiradaEdital")
public class RetiradaEdital extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    @Obrigatorio
    @Etiqueta("Fornecedor")
    @Pesquisavel
    @Tabelavel
    private Pessoa fornecedor;
    @OneToOne
    @Obrigatorio
    @Etiqueta("Representante")
    @Pesquisavel
    @Tabelavel
    private RepresentanteLicitacao representante;
    @ManyToOne
    @Etiqueta("Licitação")
    @Pesquisavel
    @Tabelavel
    private Licitacao licitacao;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Retirada")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Date retiradaEm;

    public RetiradaEdital() {
    }

    public RetiradaEdital(Pessoa fornecedor, RepresentanteLicitacao representante, Licitacao licitacao, Date retiradaEm) {
        this.fornecedor = fornecedor;
        this.representante = representante;
        this.licitacao = licitacao;
        this.retiradaEm = retiradaEm;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public RepresentanteLicitacao getRepresentante() {
        return representante;
    }

    public void setRepresentante(RepresentanteLicitacao representante) {
        this.representante = representante;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public Date getRetiradaEm() {
        return retiradaEm;
    }

    public void setRetiradaEm(Date retiradaEm) {
        this.retiradaEm = retiradaEm;
    }

    @Override
    public String toString() {
        return "RetiradaEdital{" +
            ", fornecedor=" + fornecedor +
            ", representante=" + representante +
            ", licitacao=" + licitacao +
            ", retiradaEm=" + retiradaEm +
            '}';
    }
}
