/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCertidaoAtividadeBCE;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Etiqueta(value = "Baixa de Atividade do C.M.C.")
@GrupoDiagrama(nome = "CadastroEconomico")
@Audited
@Entity
public class CertidaoAtividadeBCE extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Data")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCadastro;
    @Etiqueta("Operador")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Etiqueta("Operador")
    @Tabelavel
    @Transient
    private String nome_login;
    @Etiqueta("C.M.C.")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Etiqueta("Nome / Razão Social")
    @Tabelavel
    @Transient
    private String nome_razaoSocial;
    @Etiqueta("Situação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoCertidaoAtividadeBCE situacao;
    @Etiqueta("Motivo")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String motivo;
    @Etiqueta("Número do Protocolo")
    @Pesquisavel
    @Tabelavel
    private String numeroProtocolo;
    @Etiqueta("Ano do Protocolo")
    @Pesquisavel
    @Tabelavel
    private String anoProtocolo;
    @OneToMany(mappedBy = "certidaoAtividadeBce", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertidaoBCEArquivos> certidaoArquivos;
    @ManyToOne
    private DocumentoOficial documentoOficial;

    public List<CertidaoBCEArquivos> getCertidaoArquivos() {
        return certidaoArquivos;
    }

    public void setCertidaoArquivos(List<CertidaoBCEArquivos> certidaoArquivos) {
        this.certidaoArquivos = certidaoArquivos;
    }

    public String getNumeroAnoProtocolo() {
        if (getNumeroProtocolo() != null && getAnoProtocolo() != null) {
            return getNumeroProtocolo() + "/" + getAnoProtocolo();
        }
        return "";
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public CertidaoAtividadeBCE() {
        this.certidaoArquivos = new ArrayList<>();
        this.criadoEm = System.nanoTime();
    }

    public CertidaoAtividadeBCE(Long id, Date dataCadastro, UsuarioSistema usuarioSistema, CadastroEconomico cadastroEconomico, TipoCertidaoAtividadeBCE situacao, String motivo, String numeroProtocolo, String anoProtocolo) {
        this.id = id;
        this.dataCadastro = dataCadastro;
        this.usuarioSistema = usuarioSistema;
        this.nome_login = usuarioSistema.getLogin() + " - " + usuarioSistema.getPessoaFisica().getNome();
        this.cadastroEconomico = cadastroEconomico;
        this.nome_razaoSocial = cadastroEconomico.getPessoa().getNomeCpfCnpj();
        this.situacao = situacao;
        this.motivo = motivo;
        this.criadoEm = System.nanoTime();
        this.numeroProtocolo = numeroProtocolo;
        this.anoProtocolo = anoProtocolo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public UsuarioSistema getOperador() {
        return usuarioSistema;
    }

    public void setOperador(UsuarioSistema operador) {
        this.usuarioSistema = operador;
    }

    public TipoCertidaoAtividadeBCE getSituacao() {
        return situacao;
    }

    public void setSituacao(TipoCertidaoAtividadeBCE situacao) {
        this.situacao = situacao;
    }

    public String getNome_razaoSocial() {
        return nome_razaoSocial;
    }

    public void setNome_razaoSocial(String nome_razaoSocial) {
        this.nome_razaoSocial = nome_razaoSocial;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return cadastroEconomico.toString();
    }
}
