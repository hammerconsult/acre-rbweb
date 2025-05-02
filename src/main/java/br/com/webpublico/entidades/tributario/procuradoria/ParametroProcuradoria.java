package br.com.webpublico.entidades.tributario.procuradoria;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 19/08/15
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Procuradoria")
@Etiqueta("Parâmetro Procuradoria")
public class ParametroProcuradoria extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data Inicial")
    @Temporal(TemporalType.DATE)
    private Date dataInicial;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data Final")
    @Temporal(TemporalType.DATE)
    private Date dataFinal;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parametroProcuradoria")
    private List<DocumentoProcuradoria> documentosNecessarios;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parametroProcuradoria")
    private List<LinksUteis> links;

    public ParametroProcuradoria() {
        this.links = Lists.newArrayList();
        this.documentosNecessarios = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DocumentoProcuradoria> getDocumentosNecessarios() {
        return documentosNecessarios;
    }

    public void setDocumentosNecessarios(List<DocumentoProcuradoria> documentosNecessarios) {
        this.documentosNecessarios = documentosNecessarios;
    }

    public List<LinksUteis> getLinks() {
        return links;
    }

    public void setLinks(List<LinksUteis> links) {
        this.links = links;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }
}
