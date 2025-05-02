package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAlteracaoCadastralPessoa;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Alteração Cadastral de Pessoa")
public class AlteracaoCadastralPessoa extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data de Solicitação")
    private Date dataAlteracao;
    @ManyToOne
    @Etiqueta("Usuário Responsável")
    private UsuarioSistema usuarioResponsavel;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Nome/Razão Social")
    private String nomeRazaoSocial;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Nascimento/Abertura")
    private Date dataNascimentoAbertura;
    @Etiqueta("CEP")
    private String cep;
    @Etiqueta("Logradouro")
    private String logradouro;
    @Etiqueta("Bairro")
    private String bairro;
    @Etiqueta("Cidade")
    private String cidade;
    @Etiqueta("UF")
    private String uf;
    @Etiqueta("Complemento")
    private String complemento;
    @Etiqueta("Número")
    private String numero;
    @Etiqueta("E-mail")
    private String email;
    @Etiqueta("Telefone")
    private String telefone;
    @ManyToOne
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoAlteracaoCadastralPessoa situacao;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;
    private String motivoConclusao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataConclusao;
    @ManyToOne
    private UsuarioSistema usuarioConclusao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public Date getDataNascimentoAbertura() {
        return dataNascimentoAbertura;
    }

    public void setDataNascimentoAbertura(Date dataNascimentoAbertura) {
        this.dataNascimentoAbertura = dataNascimentoAbertura;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
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

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public UsuarioSistema getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(UsuarioSistema usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public SituacaoAlteracaoCadastralPessoa getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAlteracaoCadastralPessoa situacao) {
        this.situacao = situacao;
    }

    public String getMotivoConclusao() {
        return motivoConclusao;
    }

    public void setMotivoConclusao(String motivoConclusao) {
        this.motivoConclusao = motivoConclusao;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public UsuarioSistema getUsuarioConclusao() {
        return usuarioConclusao;
    }

    public void setUsuarioConclusao(UsuarioSistema usuarioConclusao) {
        this.usuarioConclusao = usuarioConclusao;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
