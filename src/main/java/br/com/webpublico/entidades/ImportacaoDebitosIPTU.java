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
 * Created by Filipe
 * On 17, Abril, 2019,
 * At 13:53
 */


@Entity
@Audited
@Etiqueta("Importação de Débitos de IPTU")
public class ImportacaoDebitosIPTU extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Resposavel")
    private UsuarioSistema responsavel;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Registro")
    @Pesquisavel
    @Tabelavel
    private Date dataRegistro;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "importacaoDebitosIPTU", orphanRemoval = true)
    private List<ItemImportacaoDebitosIPTU> itemImportacaoDebitosIPTU;

    public ImportacaoDebitosIPTU() {
        itemImportacaoDebitosIPTU = Lists.newArrayList();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public List<ItemImportacaoDebitosIPTU> getItemImportacaoDebitosIPTU() {
        return itemImportacaoDebitosIPTU;
    }

    public void setItemImportacaoDebitosIPTU(List<ItemImportacaoDebitosIPTU> itemImportacaoDebitosIPTU) {
        this.itemImportacaoDebitosIPTU = itemImportacaoDebitosIPTU;
    }
}
