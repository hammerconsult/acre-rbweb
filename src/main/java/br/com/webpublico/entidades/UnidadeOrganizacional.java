/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Unidade Organizacional representa todas as possíveis separações das
 * instituições como entidades, órgãos, unidades, locais/departamentos, etc.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "Administrativo")
@Entity
@Audited
@Etiqueta("Unidade Organizacional")
public class UnidadeOrganizacional extends SuperEntidade implements Comparable<UnidadeOrganizacional>, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta(value = "Descrição")
    private String descricao;
    @Tabelavel
    @OneToOne
    @Etiqueta(value = "Endereço")
    private Endereco endereco;
    @Etiqueta(value = "Endereço")
    private String enderecoDesc;
    @Etiqueta(value = "Conta de Retorno")
    private String contaBancaria;
    @Etiqueta(value = "Agência de Retorno")
    private String agenciaBancaria;
    @Etiqueta(value = "CEP")
    private String cep;
    @Etiqueta(value = "Cidade")
    private String cidade;
    @Etiqueta(value = "UF")
    private String uf;
    @Tabelavel
    @OneToOne(cascade = CascadeType.ALL)
    @Etiqueta(value = "Entidade")
    private Entidade entidade;
    private String codigoInep;
    private Boolean escola;
    /**
     * Determina se esta UnidadeOrganizacional poderá receber e gerenciar
     * recursos através de Cotas Orçamentárias.
     */
    private Boolean gestoraDeCotas;
    /*Determina se a Unidade Organizacional estará inativo para a hierarquia organizacional*/
    private Boolean inativo;
    @Etiqueta("Código")
    private Integer codigo;
    @OneToMany(mappedBy = "unidadeOrganizacional", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrevisaoHOContaDestinacao> previsaoHOContaDestinacao;
    private String migracaoChave;
    private Boolean habilitaTipoPeq;
    private Boolean obrigaQualificacaoCadastral;
    @OneToMany(mappedBy = "unidadeOrganizacional", orphanRemoval = true)
    private List<UnidadeGestoraUnidadeOrganizacional> unidadeGestoraUnidadesOrganizacionais;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "unidadeOrganizacional", orphanRemoval = true)
    private List<ResponsavelUnidade> responsaveisUnidades;
    @OneToMany(mappedBy = "subordinada")
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais;
    @Transient
    private Long criadoEm;

    private String email;
    private String contato;
    private String horarioFuncionamento;
    private String observacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    @Etiqueta("Arquivos")
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "unidadeOrganizacional", orphanRemoval = true)
    private List<UnidadeOrganizacionalAnexo> anexos;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "unidadeOrganizacional", orphanRemoval = true)
    private List<UnidadeOrganizacionalAtoNormativo> atosNormativos;

    public UnidadeOrganizacional() {
        codigo = 0;
        previsaoHOContaDestinacao = new ArrayList<>();
        criadoEm = System.nanoTime();
        atosNormativos = Lists.newArrayList();
    }

    public UnidadeOrganizacional(String descricao, Entidade entidade, Boolean gestoraDeCotas, Boolean inativo, Integer codigo, List<PrevisaoHOContaDestinacao> previsaoHOContaDestinacao, String enderecoDesc, String contaBancaria, String agenciaBancaria) {
        this.descricao = descricao;
        this.entidade = entidade;
        this.gestoraDeCotas = gestoraDeCotas;
        this.inativo = inativo;
        this.codigo = codigo;
        this.enderecoDesc = enderecoDesc;
        this.contaBancaria = contaBancaria;
        this.agenciaBancaria = agenciaBancaria;
        this.previsaoHOContaDestinacao = previsaoHOContaDestinacao;
        criadoEm = System.nanoTime();
        atosNormativos = Lists.newArrayList();
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public List<ResponsavelUnidade> getResponsaveisUnidades() {
        return responsaveisUnidades;
    }

    public void setResponsaveisUnidades(List<ResponsavelUnidade> responsaveisUnidades) {
        this.responsaveisUnidades = responsaveisUnidades;
    }

    public List<UnidadeGestoraUnidadeOrganizacional> getUnidadeGestoraUnidadesOrganizacionais() {
        return unidadeGestoraUnidadesOrganizacionais;
    }

    public void setUnidadeGestoraUnidadesOrganizacionais(List<UnidadeGestoraUnidadeOrganizacional> unidadeGestoraUnidadesOrganizacionais) {
        this.unidadeGestoraUnidadesOrganizacionais = unidadeGestoraUnidadesOrganizacionais;
    }

    public Boolean getInativo() {
        return inativo;
    }

    public void setInativo(Boolean inativo) {
        this.inativo = inativo;
    }

    public List<PrevisaoHOContaDestinacao> getPrevisaoHOContaDestinacao() {
        return previsaoHOContaDestinacao;
    }

    public void setPrevisaoHOContaDestinacao(List<PrevisaoHOContaDestinacao> previsaoHOContaDestinacao) {
        this.previsaoHOContaDestinacao = previsaoHOContaDestinacao;
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

    public String getDescricaoReduzida() {
        if (descricao.length() > 40) {
            return descricao.substring(0, 40) + "...";
        }
        return descricao;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Boolean getGestoraDeCotas() {
        return gestoraDeCotas;
    }

    public void setGestoraDeCotas(Boolean gestoraDeCotas) {
        this.gestoraDeCotas = gestoraDeCotas;
    }

    public String getEnderecoDesc() {
        return enderecoDesc;
    }

    public void setEnderecoDesc(String enderecoDesc) {
        this.enderecoDesc = enderecoDesc;
    }

    public String getContaBancaria() {
        return contaBancaria;
    }

    public void setContaBancaria(String contaBancaria) {
        this.contaBancaria = contaBancaria;
    }

    public String getAgenciaBancaria() {
        return agenciaBancaria;
    }

    public void setAgenciaBancaria(String agenciaBancaria) {
        this.agenciaBancaria = agenciaBancaria;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCodigoInep() {
        return codigoInep;
    }

    public void setCodigoInep(String codigoInep) {
        this.codigoInep = codigoInep;
    }

    //    @Override
//    public boolean equals(Object obj) {
//        return IdentidadeDaEntidade.calcularEquals(this, obj);
//    }
    public String getNomeCompleto() {
        return id + "";
    }

    //    @Override
//    public int hashCode() {
//        return IdentidadeDaEntidade.calcularHashCode(this);
//    }
    @Override
    public String toString() {
        return descricao;
    }

    public String getCodigoDescricao() {
        return codigo + " - " + descricao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }


    public Boolean getHabilitaTipoPeq() {
        return habilitaTipoPeq != null ? habilitaTipoPeq : false;
    }

    public void setHabilitaTipoPeq(Boolean habilitaTipoPeq) {
        this.habilitaTipoPeq = habilitaTipoPeq;
    }

    public Boolean getObrigaQualificacaoCadastral() {
        return obrigaQualificacaoCadastral != null ? obrigaQualificacaoCadastral : false;
    }

    public void setObrigaQualificacaoCadastral(Boolean obrigaQualificacaoCadastral) {
        this.obrigaQualificacaoCadastral = obrigaQualificacaoCadastral;
    }

    @Override
    public int compareTo(UnidadeOrganizacional o) {
        try {
            return this.getCodigo().compareTo(o.getCodigo());  //To change body of implemented methods use File | Settings | File Templates.
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnidadeOrganizacional)) return false;

        UnidadeOrganizacional other = (UnidadeOrganizacional) o;

        if (id == null || other.id == null) { //se o ID de um dos dois for null
            return this.criadoEm != null ? criadoEm.equals(other.criadoEm) : other.criadoEm != null; //se o criadoEm desse for diferente de nulo, retorna o equals dele
        } else {                                                                                     //senão, retorna o criadoEm do outro igual a nulo também
            return this.id != null ? this.id.equals(other.id) : other.id != null; //igual o criadoEm, mas com id
        }
    }

    @Override
    public int hashCode() {
        if (id == null) { //se o ID de um dos dois for null
            return this.criadoEm.hashCode(); //se o criadoEm desse for diferente de nulo, retorna o equals dele
        } else {                                                                                     //senão, retorna o criadoEm do outro igual a nulo também
            return this.id.hashCode();
        }
    }

    public void addGestorOrdenadorEntidade(ResponsavelUnidade ru) {
        try {
            ru.setUnidadeOrganizacional(this);
            this.responsaveisUnidades = Util.adicionarObjetoEmLista(this.responsaveisUnidades, ru);
        } catch (NullPointerException npe) {
            this.responsaveisUnidades = new ArrayList<>();
            addGestorOrdenadorEntidade(ru);
        }
    }

    public Boolean getEscola() {
        return escola != null ? escola : false;
    }

    public void setEscola(Boolean escola) {
        this.escola = escola;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(String horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public List<UnidadeOrganizacionalAnexo> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<UnidadeOrganizacionalAnexo> anexos) {
        this.anexos = anexos;
    }

    public List<UnidadeOrganizacionalAtoNormativo> getAtosNormativos() {
        return atosNormativos;
    }

    public void setAtosNormativos(List<UnidadeOrganizacionalAtoNormativo> atosNormativos) {
        this.atosNormativos = atosNormativos;
    }
}
