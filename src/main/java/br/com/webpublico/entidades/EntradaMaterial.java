/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEntradaMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Entrada de Materiais")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class EntradaMaterial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Entrada")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private Date dataEntrada;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Conclusão")
    private Date dataConclusao;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Estorno")
    private Date dataEstorno;

    @ManyToOne
    @Etiqueta("Responsável")
    @Obrigatorio
    private PessoaFisica responsavel;

    @ManyToOne
    @Etiqueta("Tipo de Ingresso")
    @Obrigatorio
    private TipoIngresso tipoIngresso;

    @OneToMany(mappedBy = "entradaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEntradaMaterial> itens;

    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Histórico")
    private String historico;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoEntradaMaterial situacao;

    public EntradaMaterial() {
        super();
        itens = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public List<ItemEntradaMaterial> getItens() {
        return itens;
    }

    public void setItens(List<ItemEntradaMaterial> listaEntradaMaterial) {
        this.itens = listaEntradaMaterial;
    }

    public boolean ehEntradaCompra() {
        return this instanceof EntradaCompraMaterial;
    }

    public boolean ehEntradaTransferencia() {
        return this instanceof EntradaTransferenciaMaterial;
    }

    public boolean ehEntradaIncorporacao() {
        return this instanceof EntradaIncorporacaoMaterial;
    }

    public TipoIngresso getTipoIngresso() {
        return tipoIngresso;
    }

    public void setTipoIngresso(TipoIngresso tipoIngresso) {
        this.tipoIngresso = tipoIngresso;
    }

    public SituacaoEntradaMaterial getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEntradaMaterial situacao) {
        this.situacao = situacao;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public boolean entradaDevolvidaTotalmente() {
        for (ItemEntradaMaterial iem : this.getItens()) {
            if (!iem.itemDevolvidoTotalmente()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        try {
            return "Nrº " + numero + " - " + DataUtil.getDataFormatada(dataEntrada) + " - " + historico;
        } catch (NullPointerException e) {
            return "";
        }
    }

    public BigDecimal getValorTotalEntrada() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            if (!getItens().isEmpty()) {
                for (ItemEntradaMaterial item : getItens()) {
                    total = total.add(item.getValorTotal());
                }
            }
            return total;
        } catch (Exception ex) {
            return total;
        }
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }
}
