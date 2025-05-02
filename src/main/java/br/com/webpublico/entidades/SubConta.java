/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoConta;
import br.com.webpublico.enums.TipoContaFinanceira;
import br.com.webpublico.enums.TipoRecursoSubConta;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author venon
 */
@Entity
@GrupoDiagrama(nome = "Bancario")
@Audited
@Etiqueta("Conta Financeira")
public class SubConta implements Serializable, Comparator<SubConta> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    private String codigo;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @Etiqueta("Conta Bancária Entidade")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private ContaBancariaEntidade contaBancariaEntidade;
    @OneToMany(mappedBy = "subConta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubContaUniOrg> unidadesOrganizacionais;
    @OneToMany(mappedBy = "subConta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamentos;
    @OneToMany(mappedBy = "subConta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConvenioReceitaSubConta> convenioReceitas;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Conta")
    @Pesquisavel
    @Tabelavel
    private TipoContaFinanceira tipoContaFinanceira;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Recurso")
    @Pesquisavel
    @Tabelavel
    private TipoRecursoSubConta tipoRecursoSubConta;
    @Pesquisavel
    @Tabelavel
    private String observacao;
    @OneToMany(mappedBy = "subConta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubContaFonteRec> subContaFonteRecs;
    @ManyToOne
    @Etiqueta("Conta Vinculada")
    @Pesquisavel
    @Tabelavel
    private SubConta contaVinculada;
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    private SituacaoConta situacao;
    @Etiqueta("Integração Tributária")
    private Boolean integracao;
    @Etiqueta("Conta Única")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Boolean contaUnica;
    @Etiqueta("Finalidade OB")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Boolean finalidadeOB;
    @Etiqueta("Obrigar Acompanhamento da Execução Orçamentária - CO")
    private Boolean obrigarCodigoCO;

    public SubConta() {
        criadoEm = System.nanoTime();
        unidadesOrganizacionais = new ArrayList<SubContaUniOrg>();
        subContaFonteRecs = new ArrayList<>();
        convenioReceitas = new ArrayList<>();
        pagamentos = new ArrayList<Pagamento>();
        integracao = Boolean.FALSE;
        obrigarCodigoCO = Boolean.FALSE;
    }

    public SubConta(String codigo, String descricao, ContaBancariaEntidade contaBancariaEntidade, List<SubContaUniOrg> unidadesOrganizacionais, List<Pagamento> pagamentos, Long criadoEm, TipoContaFinanceira tipoContaFinanceira, TipoRecursoSubConta tipoRecursoSubConta, String observacao, List<SubContaFonteRec> subContaFonteRecs, SubConta contaVinculada, SituacaoConta situacao, Boolean integracao) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.contaBancariaEntidade = contaBancariaEntidade;
        this.unidadesOrganizacionais = unidadesOrganizacionais;
        this.pagamentos = pagamentos;
        this.criadoEm = criadoEm;
        this.tipoContaFinanceira = tipoContaFinanceira;
        this.tipoRecursoSubConta = tipoRecursoSubConta;
        this.observacao = observacao;
        this.subContaFonteRecs = subContaFonteRecs;
        this.contaVinculada = contaVinculada;
        this.situacao = situacao;
        this.integracao = integracao;
    }

    public SituacaoConta getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoConta situacao) {
        this.situacao = situacao;
    }

    public SubConta getContaVinculada() {
        return contaVinculada;
    }

    public void setContaVinculada(SubConta contaVinculada) {
        this.contaVinculada = contaVinculada;
    }

    public List<SubContaFonteRec> getSubContaFonteRecs() {
        return subContaFonteRecs;
    }

    public void setSubContaFonteRecs(List<SubContaFonteRec> subContaFonteRecs) {
        this.subContaFonteRecs = subContaFonteRecs;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public TipoContaFinanceira getTipoContaFinanceira() {
        return tipoContaFinanceira;
    }

    public void setTipoContaFinanceira(TipoContaFinanceira tipoContaFinanceira) {
        this.tipoContaFinanceira = tipoContaFinanceira;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<SubContaUniOrg> getUnidadesOrganizacionais() {
        return unidadesOrganizacionais;
    }

    public void setUnidadesOrganizacionais(List<SubContaUniOrg> unidadesOrganizacionais) {
        this.unidadesOrganizacionais = unidadesOrganizacionais;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoRecursoSubConta getTipoRecursoSubConta() {
        return tipoRecursoSubConta;
    }

    public void setTipoRecursoSubConta(TipoRecursoSubConta tipoRecursoSubConta) {
        this.tipoRecursoSubConta = tipoRecursoSubConta;
    }

    public Boolean getIntegracao() {
        return integracao;
    }

    public void setIntegracao(Boolean integracao) {
        this.integracao = integracao;
    }

    public List<ConvenioReceitaSubConta> getConvenioReceitas() {
        return convenioReceitas;
    }

    public void setConvenioReceitas(List<ConvenioReceitaSubConta> convenioReceitas) {
        this.convenioReceitas = convenioReceitas;
    }

    public Boolean getContaUnica() {
        return contaUnica == null ? Boolean.FALSE : contaUnica;
    }

    public void setContaUnica(Boolean contaUnica) {
        this.contaUnica = contaUnica;
    }

    public Boolean getFinalidadeOB() {
        return finalidadeOB == null ? Boolean.FALSE : finalidadeOB;
    }

    public void setFinalidadeOB(Boolean finalidadeOB) {
        this.finalidadeOB = finalidadeOB;
    }

    public Boolean getObrigarCodigoCO() {
        return obrigarCodigoCO == null ? Boolean.FALSE : obrigarCodigoCO;
    }

    public void setObrigarCodigoCO(Boolean obrigarCodigoCO) {
        this.obrigarCodigoCO = obrigarCodigoCO;
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
        if (codigo != null && descricao != null) {
            return codigo + " - " + descricao;
        }
        return "";

    }

    public String toStringAutoComplete() {
        if (codigo != null && descricao != null) {
            String retorno = codigo + " - " + descricao;
            return retorno.length() > 70 ? retorno.substring(0, 67) + "..." : retorno;
        }
        return "";

    }

    public String toStringAutoCompleteContaFinanceira() {
        String descricaoContaFinanceria = "";
        if (codigo != null) {
            descricaoContaFinanceria += codigo + " - ";
        }
        if (descricao != null) {
            descricaoContaFinanceria += descricao + " - ";

        }
        if (tipoContaFinanceira != null) {
            descricaoContaFinanceria += tipoContaFinanceira.getDescricao() + " - ";
        }
        if (situacao != null) {
            descricaoContaFinanceria += situacao.getDescricao() + "";
        }

        return descricaoContaFinanceria;
    }

    public String getOutroToString() {
        return codigo;
    }

    @Override
    public int compare(SubConta t, SubConta t1) {
        return t.getCodigo().compareTo(t1.getCodigo());
    }

    public Boolean isContaUnica() {
        if (this.getContaUnica() == null) {
            return false;
        } else {
            if (this.getContaUnica()) {
                return true;
            }
        }
        return false;
    }

    public Boolean verificarUnidadeExistente(UnidadeOrganizacional uo, Exercicio exercicio) {
        for (SubContaUniOrg s : this.getUnidadesOrganizacionais()) {
            if (s.getUnidadeOrganizacional().getId().equals(uo.getId()) && s.getExercicio().getId().equals(exercicio.getId())) {
                return false;
            }
        }
        return true;
    }

    @Deprecated
    public Boolean verificarFonteExistente(FonteDeRecursos fr) {
        for (SubContaFonteRec s : this.getSubContaFonteRecs()) {
            if (s.getFonteDeRecursos().getId().equals(fr.getId())) {
                return false;
            }
        }
        return true;
    }

    public boolean hasContaDeDestinacaoAdicionada(ContaDeDestinacao contaDeDestinacao) {
        for (SubContaFonteRec subContaFonteRec : subContaFonteRecs) {
            if (subContaFonteRec.getContaDeDestinacao().equals(contaDeDestinacao)) {
                return true;
            }
        }
        return false;
    }
}
