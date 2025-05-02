/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoLogradouro;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author terminal4
 */
@GrupoDiagrama(nome = "Endereçamento")
@Entity

@Audited
public class Bairro implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Etiqueta("Código")
    @Tabelavel
    @Obrigatorio
    private Long codigo;
    @Pesquisavel
    @Column(length = 70)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Etiqueta("Ativo")
    @Tabelavel
    private Boolean ativo = Boolean.TRUE;
    @OneToMany(mappedBy = "bairro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogradouroBairro> logradouros;

    public Bairro() {
        logradouros = new ArrayList<>();
    }

    public Bairro(String descricao) {
        this.descricao = descricao;
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

    public List<LogradouroBairro> getLogradouros() {
        return logradouros;
    }

    public void setLogradouros(List<LogradouroBairro> logradouros) {
        this.logradouros = logradouros;
    }

    public List<LogradouroBairro> getLogradourosAtivos() {
        return getLogradourosPorSituacao(SituacaoLogradouro.ATIVO);
    }

    public List<LogradouroBairro> getLogradourosInativo() {
        return getLogradourosPorSituacao(SituacaoLogradouro.INATIVO);
    }

    public List<LogradouroBairro> getLogradourosPorSituacao(SituacaoLogradouro sit) {
        List<LogradouroBairro> retorno = Lists.newArrayList();
        for (LogradouroBairro logradouro : logradouros) {
            if (logradouro.getLogradouro() != null && logradouro.getLogradouro().getSituacao() != null && logradouro.getLogradouro().getSituacao().equals(sit)) {
                retorno.add(logradouro);
            }
        }
        return retorno;
    }

    public LogradouroBairro getLogradouroPorCodigo(Long codigo) {
        for (LogradouroBairro logradouro : logradouros) {
            if (logradouro.getLogradouro().getCodigo().equals(codigo)) {
                return logradouro;
            }
        }
        return null;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bairro)) {
            return false;
        }
        Bairro other = (Bairro) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        if (codigo != null) {
            sb.append(codigo).append(" - ");
        }
        if (descricao != null) {
            sb.append(descricao);
        }

        return sb.toString().trim();
    }
}
