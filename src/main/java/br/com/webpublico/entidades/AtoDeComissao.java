/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Licitacao")
@Entity

@Audited
@Etiqueta("Ato de Comissão")
public class AtoDeComissao extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Ato Legal")
    @OneToOne
    private AtoLegal atoLegal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "atoDeComissao", orphanRemoval = true)
    private List<Comissao> comissoes = new ArrayList<>();

    public AtoDeComissao() {
    }

    public AtoDeComissao(AtoLegal atoLegal) {
        setAtoLegal(atoLegal);
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Comissao> getComissoes() {
        return comissoes;
    }

    public void setComissoes(List<Comissao> comissoes) {
        this.comissoes = comissoes;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.AtoDeComissao[ id=" + id + " ]";
    }

    public boolean temComissaoAdicionadaPorCodigo(Comissao comissao) {
        try {
            for (Comissao c : comissoes) {
                if (!c.equals(comissao) && c.getCodigo().equals(comissao.getCodigo())) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void adicionarComissao(Comissao comissao) {
        comissoes = Util.adicionarObjetoEmLista(comissoes, comissao);
    }

    public void removerComissao(Comissao comissao) {
        comissoes.remove(comissao);
    }

    public boolean temComissaoSemMembro() {
        for (Comissao c : comissoes) {
            if (CollectionUtils.isEmpty(c.getMembroComissao())) {
                return true;
            }
        }
        return false;
    }

    public boolean temComissaoAdicionada() {
        return !CollectionUtils.isEmpty(comissoes);
    }
}
