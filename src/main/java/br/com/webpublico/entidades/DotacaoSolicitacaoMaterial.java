/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author lucas
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Reserva de Dotação de Solicitação de Compra")
@Table(name = "DOTSOLMAT")
public class DotacaoSolicitacaoMaterial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Realizada Em")
    @Temporal(TemporalType.DATE)
    private Date realizadaEm;

    @Obrigatorio
    @Etiqueta("Solicitação de Compra")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private SolicitacaoMaterial solicitacaoMaterial;

    @Etiqueta("Convênio de Receita")
    @ManyToOne
    private ConvenioReceita convenioReceita;

    @OneToMany(mappedBy = "dotacaoSolicitacaoMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoSolicitacaoMaterialItem> itens;

    public DotacaoSolicitacaoMaterial() {
        itens = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return solicitacaoMaterial;
    }

    public void setSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        this.solicitacaoMaterial = solicitacaoMaterial;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getRealizadaEm() {
        return realizadaEm;
    }

    public void setRealizadaEm(Date realizadaEm) {
        this.realizadaEm = realizadaEm;
    }

    public String getCodigoOrDescricaoParaGeracaoAutomatica() {
        if (id != null) {
            return codigo.toString();
        }
        return "Gerado automaticamente ao salvar.";
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public List<DotacaoSolicitacaoMaterialItem> getItens() {
        return itens;
    }

    public void setItens(List<DotacaoSolicitacaoMaterialItem> itens) {
        this.itens = itens;
    }

    @Override
    public String toString() {
        return "Nº - " + codigo + " da Solicitação de Compra: " + solicitacaoMaterial.toString();
    }
}
