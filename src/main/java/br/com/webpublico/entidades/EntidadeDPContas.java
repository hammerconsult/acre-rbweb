/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@Etiqueta("Configuração de Entidades para Declarações e Prestações de Contas.")
@GrupoDiagrama(nome = "RecursosHumanos")
public class EntidadeDPContas extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Declarações/Prestações de Contas")
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    private DeclaracaoPrestacaoContas declaracaoPrestacaoContas;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Início de vigência")
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Final de vigência")
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @OneToMany(mappedBy = "entidadeDPContas", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Entidades Vinculadas")
    private List<ItemEntidadeDPContas> itensEntidaDPContas = Lists.newArrayList();

    public EntidadeDPContas() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeclaracaoPrestacaoContas getDeclaracaoPrestacaoContas() {
        return declaracaoPrestacaoContas;
    }

    public void setDeclaracaoPrestacaoContas(DeclaracaoPrestacaoContas declaracaoPrestacaoContas) {
        this.declaracaoPrestacaoContas = declaracaoPrestacaoContas;
    }

    public List<ItemEntidadeDPContas> getItensEntidaDPContas() {
        return itensEntidaDPContas;
    }

    public void setItensEntidaDPContas(List<ItemEntidadeDPContas> itensEntidaDPContas) {
        this.itensEntidaDPContas = itensEntidaDPContas;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public String toString() {
        return declaracaoPrestacaoContas.toString();
    }
}
