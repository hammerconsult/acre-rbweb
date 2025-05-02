/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades.rh.documentoportalservidor;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Documento para Servidores")
@Table(name = "DOCUMENTOPORTALCONTRIBUINT")
public class DocumentoPortalContribuinte extends SuperEntidade implements Serializable, PossuidorArquivo{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Nome no Portal")
    @Obrigatorio
    private String nomeNoPortal;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ativo")
    private Boolean ativo;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Publicação")
    private Date publicadoEm;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Atualização")
    private Date atualizadoEm;
    @ManyToOne
    private DocumentoPortalContribuinte superior;
    @OneToMany(mappedBy = "superior", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<DocumentoPortalContribuinte> filhos;
    @Obrigatorio
    private Integer ordem;
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @Transient
    private AssistenteDetentorArquivoComposicao assistenteArquivo;

    public DocumentoPortalContribuinte() {
        super();
        this.detentorArquivoComposicao = new DetentorArquivoComposicao();
        this.assistenteArquivo = new AssistenteDetentorArquivoComposicao(this, new Date());
    }

    public String getNomeNoPortal() {
        return nomeNoPortal;
    }

    public void setNomeNoPortal(String nomeNoPortal) {
        this.nomeNoPortal = nomeNoPortal;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentoPortalContribuinte getSuperior() {
        return superior;
    }

    public void setSuperior(DocumentoPortalContribuinte superior) {
        this.superior = superior;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public List<DocumentoPortalContribuinte> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<DocumentoPortalContribuinte> filhos) {
        this.filhos = filhos;
    }

    public Date getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Date atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public Date getPublicadoEm() {
        return publicadoEm;
    }

    public void setPublicadoEm(Date publicadoEm) {
        this.publicadoEm = publicadoEm;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public AssistenteDetentorArquivoComposicao getAssistenteArquivo() {
        return assistenteArquivo;
    }

    public void setAssistenteArquivo(AssistenteDetentorArquivoComposicao assistenteArquivo) {
        this.assistenteArquivo = assistenteArquivo;
    }
}
