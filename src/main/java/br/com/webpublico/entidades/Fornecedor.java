package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 25/06/14
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Sistema de Cadastramento Unificado de Fornecedores – SICAFRB")
public class Fornecedor implements Serializable, ValidadorEntidade, PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data Operação")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date alteradoEm;

    @Etiqueta("Fornecedor")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @OneToOne
    private Pessoa pessoa;

    @Etiqueta("Observações")
    private String observacoes;

    @Etiqueta("Código Verificação")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String codigoVerificacao;

    @Etiqueta("Inscrição no SICARB")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Integer inscricaoNoSicaRb;

    @Etiqueta("Certificado")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Integer certificado;

    @Etiqueta("Inscrição Municipal")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String inscricaoMunicipal;

    @Etiqueta("Registro do Ato Constitutivo")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String registroDoAtoConstitutivo;

    @Etiqueta("Registro na Entidade Profissional")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String registroNaEntidadeProfissional;

    @Etiqueta("Capital Registrado")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Monetario
    private BigDecimal capitalRegistrado;

    @Etiqueta("Patrimônio Líquido")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Monetario
    private BigDecimal patrimonioLiquido;

    @Etiqueta("Endereço em Rio Branco")
    @ManyToOne
    private EnderecoCorreio enderecoEmRB;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoFornecedor> documentosFornecedor;

    @ManyToOne
    private Exercicio exercicio;


    @Invisivel
    @Transient
    private Long criadoEm;

    public Fornecedor() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAlteradoEm() {
        return alteradoEm;
    }

    public void setAlteradoEm(Date alteradoEm) {
        this.alteradoEm = alteradoEm;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<DocumentoFornecedor> getDocumentosFornecedor() {
        return documentosFornecedor;
    }

    public void setDocumentosFornecedor(List<DocumentoFornecedor> documentosFornecedor) {
        this.documentosFornecedor = documentosFornecedor;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public Integer getInscricaoNoSicaRb() {   return inscricaoNoSicaRb; }

    public void setInscricaoNoSicaRb(Integer inscricaoNoSicaRb) {  this.inscricaoNoSicaRb = inscricaoNoSicaRb;  }

    public Integer getCertificado() {  return certificado;  }

    public void setCertificado(Integer certificado) { this.certificado = certificado; }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public String getRegistroDoAtoConstitutivo() {
        return registroDoAtoConstitutivo;
    }

    public void setRegistroDoAtoConstitutivo(String registroDoAtoConstitutivo) {
        this.registroDoAtoConstitutivo = registroDoAtoConstitutivo;
    }

    public String getRegistroNaEntidadeProfissional() {
        return registroNaEntidadeProfissional;
    }

    public void setRegistroNaEntidadeProfissional(String registroNaEntidadeProfissional) {
        this.registroNaEntidadeProfissional = registroNaEntidadeProfissional;
    }

    public BigDecimal getCapitalRegistrado() {
        return capitalRegistrado;
    }

    public void setCapitalRegistrado(BigDecimal capitalRegistrado) {
        this.capitalRegistrado = capitalRegistrado;
    }

    public BigDecimal getPatrimonioLiquido() {
        return patrimonioLiquido;
    }

    public void setPatrimonioLiquido(BigDecimal patrimonioLiquido) {
        this.patrimonioLiquido = patrimonioLiquido;
    }

    public EnderecoCorreio getEnderecoEmRB() {
        return enderecoEmRB;
    }

    public void setEnderecoEmRB(EnderecoCorreio enderecoEmRB) {
        this.enderecoEmRB = enderecoEmRB;
    }

    public Exercicio getExercicio() { return exercicio; }

    public void setExercicio(Exercicio exercicio) { this.exercicio = exercicio; }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(alteradoEm) + " - " + pessoa;
    }
}
