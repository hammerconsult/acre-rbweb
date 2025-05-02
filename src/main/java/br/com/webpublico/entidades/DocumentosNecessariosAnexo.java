package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 19/02/2016.
 */
@Entity
@Audited
@Etiqueta("Upload de Documentos Obrigatórios")
public class DocumentosNecessariosAnexo extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data Inicial")
    @Temporal(TemporalType.DATE)
    private Date dataInicio;
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Final")
    private Date dataFim;
    @OneToMany(mappedBy = "documentosNecessariosAnexo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDocumentoAnexo> itens;


    public Long getId() {
        return id;
    }

    public DocumentosNecessariosAnexo() {
        this.itens = Lists.newArrayList();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public List<ItemDocumentoAnexo> getItens() {
        return itens;
    }

    public void setItens(List<ItemDocumentoAnexo> itens) {
        this.itens = itens;
    }
}
