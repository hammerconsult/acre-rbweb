/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 09/07/15
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "PPA")
@Entity
@Etiqueta("Liberação Cota Orçamentária")
@Audited
public class LiberacaoCotaOrcamentaria extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Data")
    private Date dataLiberacao;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Grupo Orçamentário")
    private GrupoOrcamentario grupoOrcamentario;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Operação")
    private OperacaoFormula operacao;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Valor")
    private BigDecimal valor;

    public LiberacaoCotaOrcamentaria() {
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLiberacao() {
        return dataLiberacao;
    }

    public void setDataLiberacao(Date dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
    }

    public GrupoOrcamentario getGrupoOrcamentario() {
        return grupoOrcamentario;
    }

    public void setGrupoOrcamentario(GrupoOrcamentario grupoOrcamentario) {
        this.grupoOrcamentario = grupoOrcamentario;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public OperacaoFormula getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoFormula operacao) {
        this.operacao = operacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return grupoOrcamentario + " " + Util.formataValor(valor);
    }
}
