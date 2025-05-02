/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author Claudio
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Recursos Humanos")
@Etiqueta("BB Atuarial")
public class BBAtuarial extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Invisivel
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Sequencia")
    @Obrigatorio
    @Tabelavel
    private Long sequencia;
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data de Referência")
    private Date dataReferencia;
    @Etiqueta("Data de Registro")
    @Temporal(TemporalType.DATE)
    @Tabelavel
    private Date dataRegistro;
    @ManyToOne
    @Etiqueta("Usuário")
    @Tabelavel
    private UsuarioSistema usuarioSistema;
    @Transient
    @Invisivel
    private List<String> tiposArquivoBBAtuarial;
    @Transient
    @Invisivel
    private List<Long> idsServidoresAtivos;
    @Transient
    @Invisivel
    private List<Long> idsAposentados;
    @Transient
    @Invisivel
    private List<Long> idsDependentes;
    @Transient
    @Invisivel
    private List<Long> idsPensionistas;

    public BBAtuarial() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public List<String> getTiposArquivoBBAtuarial() {
        return tiposArquivoBBAtuarial;
    }

    public void setTiposArquivoBBAtuarial(List<String> tiposArquivoBBAtuarial) {
        this.tiposArquivoBBAtuarial = tiposArquivoBBAtuarial;
    }

    public List<Long> getIdsServidoresAtivos() {
        return idsServidoresAtivos;
    }

    public void setIdsServidoresAtivos(List<Long> idsServidoresAtivos) {
        this.idsServidoresAtivos = new ArrayList(new HashSet(idsServidoresAtivos));
    }

    public List<Long> getIdsAposentados() {
        return idsAposentados;
    }

    public void setIdsAposentados(List<Long> idsAposentados) {
        this.idsAposentados = new ArrayList(new HashSet(idsAposentados));
    }

    public List<Long> getIdsDependentes() {
        return idsDependentes;
    }

    public void setIdsDependentes(List<Long> idsDependentes) {
        this.idsDependentes = new ArrayList(new HashSet(idsDependentes));
    }

    public List<Long> getIdsPensionistas() {
        return idsPensionistas;
    }

    public void setIdsPensionistas(List<Long> idsPensionistas) {
        this.idsPensionistas = new ArrayList(new HashSet(idsPensionistas));
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(dataReferencia);
    }
}
