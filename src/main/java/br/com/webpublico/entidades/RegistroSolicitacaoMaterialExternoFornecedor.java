package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 04/08/14
 * Time: 09:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "REGISTROSOLMATEXTFORNEC")
@Etiqueta("Registro Solicitação Material Externo Fornecedor")
public class RegistroSolicitacaoMaterialExternoFornecedor implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Etiqueta("Registro Solicitação Externo")
    @ManyToOne
    public RegistroSolicitacaoMaterialExterno registroSolMatExterno;

    @Etiqueta("Fornecedor")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    public Pessoa pessoa;

    @Etiqueta("Itens")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "regSolMatExtFornecedor")
    public List<RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno> itens;

    @Invisivel
    @Transient
    public Long criadoEm;

    public RegistroSolicitacaoMaterialExternoFornecedor() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegistroSolicitacaoMaterialExterno getRegistroSolMatExterno() {
        return registroSolMatExterno;
    }

    public void setRegistroSolMatExterno(RegistroSolicitacaoMaterialExterno registroSolMatExterno) {
        this.registroSolMatExterno = registroSolMatExterno;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno> getItens() {
        return itens;
    }

    public void setItens(List<RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno> itens) {
        this.itens = itens;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }
}
