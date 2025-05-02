/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author claudio
 */
@Entity

@GrupoDiagrama(nome = "Certidao")
@Audited
public class DocumentoOficial implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ModeloDoctoOficial modeloDoctoOficial;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataEmissao;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataSolicitacao;
    private String conteudo;
    private String conteudoAssinatura;
    private Integer numero;
    @ManyToOne
    private Exercicio exercicio;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date validade;
    private String codigoVerificacao;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    private String ipMaquina;
    @ManyToOne
    private Cadastro cadastro;
    @ManyToOne
    private Pessoa pessoa;
    @Enumerated(EnumType.STRING)
    private SituacaoDocumentoOficial situacaoDocumento;
    @Transient
    private Long criadoEm;

    public DocumentoOficial() {
        criadoEm = System.nanoTime();
        this.situacaoDocumento = SituacaoDocumentoOficial.ATIVO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getIpMaquina() {
        return ipMaquina;
    }

    public void setIpMaquina(String ipMaquina) {
        this.ipMaquina = ipMaquina;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public ModeloDoctoOficial getModeloDoctoOficial() {
        return modeloDoctoOficial;
    }

    public void setModeloDoctoOficial(ModeloDoctoOficial modeloDoctoOficial) {
        this.modeloDoctoOficial = modeloDoctoOficial;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getConteudoAssinatura() {
        return conteudoAssinatura;
    }

    public void setConteudoAssinatura(String conteudoAssinatura) {
        this.conteudoAssinatura = conteudoAssinatura;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public SituacaoDocumentoOficial getSituacaoDocumento() {
        if (situacaoDocumento == null) {
            return SituacaoDocumentoOficial.ATIVO;
        }
        return situacaoDocumento;
    }

    public void setSituacaoDocumento(SituacaoDocumentoOficial situacaoDocumento) {
        this.situacaoDocumento = situacaoDocumento;
    }

    public boolean isValido() {
        return validade == null || validade.after(new Date());
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
        if (this.numero != null) {
            return String.valueOf(numero);
        }
        return "";
    }

    public enum SituacaoDocumentoOficial {
        ATIVO("Ativo"),
        CANCELADO("Cancelado");
        private String descricao;

        private SituacaoDocumentoOficial(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
