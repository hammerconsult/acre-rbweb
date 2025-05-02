/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Peixe
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@CorEntidade(value = "#00FF00")
@Etiqueta("Lote de Processamento")
public class LoteProcessamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "loteProcessamento")
    private List<ItensLoteProcessamento> itensLoteProcessamentos;
    private String consulta;
    @Transient
    private List<UnidadeOrganizacional> unidadeOrganizacionals;
    @Etiqueta("Mês")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.ORDINAL)
    private Mes mes;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Ano")
    private Integer ano;
    @Etiqueta("Número do Lote")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Integer numeroLote;
    @Etiqueta("Tipo Folha de Pagamento")
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    @Etiqueta("Quantidade de Meses de Retroação")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Integer qtdeMesesRetroacao;

    public LoteProcessamento() {
        itensLoteProcessamentos = Lists.newLinkedList();
        unidadeOrganizacionals = Lists.newLinkedList();
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(Integer numeroLote) {
        this.numeroLote = numeroLote;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Integer getQtdeMesesRetroacao() {
        return qtdeMesesRetroacao;
    }

    public void setQtdeMesesRetroacao(Integer qtdeMesesRetroacao) {
        this.qtdeMesesRetroacao = qtdeMesesRetroacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;
    }

    public List<ItensLoteProcessamento> getItensLoteProcessamentos() {
        return itensLoteProcessamentos;
    }

    public void setItensLoteProcessamentos(List<ItensLoteProcessamento> itensLoteProcessamentos) {
        this.itensLoteProcessamentos = itensLoteProcessamentos;
    }

    public List<UnidadeOrganizacional> getUnidadeOrganizacionals() {
        return unidadeOrganizacionals;
    }

    public void setUnidadeOrganizacionals(List<UnidadeOrganizacional> unidadeOrganizacionals) {
        this.unidadeOrganizacionals = unidadeOrganizacionals;
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
        if (!(object instanceof LoteProcessamento)) {
            return false;
        }
        LoteProcessamento other = (LoteProcessamento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
