package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Etiqueta("Romaneio Feirante")
public class RomaneioFeiraFeirante extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Romaneio Feira")
    private RomaneioFeira romaneioFeira;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Feirante")
    private Feirante feirante;

    @Etiqueta("Produtos")
    @OneToMany(mappedBy = "romaneioFeiraFeirante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RomaneioFeiraFeiranteProduto> produtos;

    public RomaneioFeiraFeirante() {
        produtos = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Feirante getFeirante() {
        return feirante;
    }

    public void setFeirante(Feirante feirante) {
        this.feirante = feirante;
    }

    public RomaneioFeira getRomaneioFeira() {
        return romaneioFeira;
    }

    public void setRomaneioFeira(RomaneioFeira romaneioFeira) {
        this.romaneioFeira = romaneioFeira;
    }

    public List<RomaneioFeiraFeiranteProduto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<RomaneioFeiraFeiranteProduto> produtos) {
        this.produtos = produtos;
    }
}
