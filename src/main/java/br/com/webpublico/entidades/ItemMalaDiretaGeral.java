package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ContribuinteTributario;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * Created by William on 07/06/2016.
 */
@Entity
@Audited
public class ItemMalaDiretaGeral extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Invisivel
    @OneToMany(mappedBy = "itemMalaDiretaGeral", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParcelaMalaDiretaGeral> parcelas;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("DAM")
    @OneToOne
    private DAM dam;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Mala Direta")
    @ManyToOne
    private MalaDiretaGeral malaDiretaGeral;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Cadastro")
    @ManyToOne
    private Cadastro cadastro;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Pessoa")
    @ManyToOne
    private Pessoa pessoa;
    private String texto;
    @Transient
    private ContribuinteTributario contribuinteTributario;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ParcelaMalaDiretaGeral> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ParcelaMalaDiretaGeral> parcelas) {
        this.parcelas = parcelas;
    }

    public DAM getDam() {
        return dam;
    }

    public void setDam(DAM dam) {
        this.dam = dam;
    }

    public MalaDiretaGeral getMalaDiretaGeral() {
        return malaDiretaGeral;
    }

    public void setMalaDiretaGeral(MalaDiretaGeral malaDiretaGeral) {
        this.malaDiretaGeral = malaDiretaGeral;
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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public ContribuinteTributario getContribuinteTributario() {
        return contribuinteTributario;
    }

    public void setContribuinteTributario(ContribuinteTributario contribuinteTributario) {
        this.contribuinteTributario = contribuinteTributario;
    }

    public String getDescricao() {
        return cadastro != null ? cadastro.toString() + " - " + pessoa.toString() : pessoa.toString();
    }

    @Override
    public String toString() {
        return "ItemMalaDiretaGeral{" +
            "id=" + id +
            ", dam=" + dam +
            ", malaDiretaGeral=" + malaDiretaGeral +
            ", cadastro=" + cadastro +
            ", pessoa=" + pessoa +
            '}';
    }

}
