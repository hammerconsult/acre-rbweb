package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created by William on 07/06/2016.
 */
@Entity
@Audited
public class CadastroMalaDiretaIPTU extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Invisivel
    @OneToMany(mappedBy = "cadastroMalaDiretaIPTU", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParcelaMalaDiretaIPTU> parcelaMalaDiretaIPTU;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Mala Direta de IPTU")
    @ManyToOne
    private MalaDiretaIPTU malaDiretaIPTU;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Cadastro Imobiliário")
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroMalaDiretaIPTU() {
        this.parcelaMalaDiretaIPTU = Lists.newArrayList();
    }

    public List<ParcelaMalaDiretaIPTU> getParcelaMalaDiretaIPTU() {
        return parcelaMalaDiretaIPTU;
    }

    public void setParcelaMalaDiretaIPTU(List<ParcelaMalaDiretaIPTU> parcelaMalaDiretaIPTU) {
        this.parcelaMalaDiretaIPTU = parcelaMalaDiretaIPTU;
    }

    public MalaDiretaIPTU getMalaDiretaIPTU() {
        return malaDiretaIPTU;
    }

    public void setMalaDiretaIPTU(MalaDiretaIPTU malaDiretaIPTU) {
        this.malaDiretaIPTU = malaDiretaIPTU;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }
}
