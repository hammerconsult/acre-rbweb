/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusVistoria;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Alvara")
@Entity
@Audited
@Etiqueta("Vistoria")
public class Vistoria extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Sequência")
    private Long sequencia;
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Data")
    private Date data;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Alvará")
    @Obrigatorio
    private Alvara alvara;
    @Transient
    @Tabelavel
    @Etiqueta("Cadastro Econômico")
    private String cadastroEconomico;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Alvará")
    private TipoAlvara tipoAlvara;
    @Invisivel
    @Etiqueta("Nº Protocolo")
    private String numeroProtocolo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vistoria", orphanRemoval = true)
    private List<IrregularidadeDaVistoria> listaIrregularidade;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vistoria", orphanRemoval = true)
    private List<VistoriaCnae> cnaes;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vistoria", orphanRemoval = true)
    private List<ArquivoVistoria> arquivos;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vistoria", orphanRemoval = true)
    private List<ParecerVistoria> pareceres;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Status")
    private StatusVistoria statusVistoria;
    @Invisivel
    private String migracaoChave;

    public Vistoria() {
        listaIrregularidade = new ArrayList<>();
        cnaes = new ArrayList<>();
        arquivos = new ArrayList<>();
        statusVistoria = StatusVistoria.ABERTA;
    }

    public Vistoria(Long id, Long sequencia, Date data, TipoAlvara tipoAlvara, StatusVistoria status, Alvara alvara, CadastroEconomico cadastroEconomico) {
        this.id = id;
        this.sequencia = sequencia;
        this.data = data;
        this.tipoAlvara = tipoAlvara;
        this.statusVistoria = status;
        this.alvara = alvara;
        this.cadastroEconomico = cadastroEconomico.getInscricaoCadastral() + " - " + cadastroEconomico.getPessoa().getNomeCpfCnpj();
    }

    public StatusVistoria getStatusVistoria() {
        return statusVistoria;
    }

    public void setStatusVistoria(StatusVistoria statusVistoria) {
        this.statusVistoria = statusVistoria;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<IrregularidadeDaVistoria> getListaIrregularidade() {
        return listaIrregularidade;
    }

    public void setListaIrregularidade(List<IrregularidadeDaVistoria> listaIrregularidade) {
        this.listaIrregularidade = listaIrregularidade;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public List<VistoriaCnae> getCnaes() {
        return cnaes;
    }

    public void setCnaes(List<VistoriaCnae> cnaes) {
        this.cnaes = cnaes;
    }

    public List<ArquivoVistoria> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ArquivoVistoria> arquivos) {
        this.arquivos = arquivos;
    }

    public List<ParecerVistoria> getPareceres() {
        return pareceres;
    }

    public void setPareceres(List<ParecerVistoria> pareceres) {
        this.pareceres = pareceres;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Alvara getAlvara() {
        return alvara;
    }

    public void setAlvara(Alvara alvara) {
        this.alvara = alvara;
    }

    public String getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(String cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    @Override
    public String toString() {
        return "Vistoria " + getSequencia() + " - " + tipoAlvara.getDescricao();
    }
}
