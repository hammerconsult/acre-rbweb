package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoAtoLegal;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
@Table(name = "PAGINAMENUHPAGINAPREF")
@Etiqueta("Página Menu Horizontal Página Prefeitura")
public class PaginaMenuHorizontalPaginaPrefeitura extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Nome")
    private String nome;
    @Etiqueta("Chave")
    private String chave;
    @Etiqueta("Ícone")
    private String icone;
    @Etiqueta("Descrição")
    private String descricao;
    @Etiqueta("Tipo Ato Legal")
    @Enumerated(EnumType.STRING)
    private TipoAtoLegal tipoAtoLegal;
    @ManyToOne
    private PaginaMenuHorizontalPortal paginaMenuHorizontalPortal;
    @ManyToOne
    private PaginaPrefeituraPortal paginaPrefeituraPortal;
    @Obrigatorio
    private Integer ordem;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paginaMenuHorizontal", orphanRemoval = true)
    private List<SubPaginaMenuHorizontal> subPaginas;

    public PaginaMenuHorizontalPaginaPrefeitura() {
        super();
        this.subPaginas = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoAtoLegal getTipoAtoLegal() {
        return tipoAtoLegal;
    }

    public void setTipoAtoLegal(TipoAtoLegal tipoAtoLegal) {
        this.tipoAtoLegal = tipoAtoLegal;
    }

    public PaginaMenuHorizontalPortal getPaginaMenuHorizontalPortal() {
        return paginaMenuHorizontalPortal;
    }

    public void setPaginaMenuHorizontalPortal(PaginaMenuHorizontalPortal paginaMenuHorizontalPortal) {
        this.paginaMenuHorizontalPortal = paginaMenuHorizontalPortal;
    }

    public PaginaPrefeituraPortal getPaginaPrefeituraPortal() {
        return paginaPrefeituraPortal;
    }

    public void setPaginaPrefeituraPortal(PaginaPrefeituraPortal paginaPrefeituraPortal) {
        this.paginaPrefeituraPortal = paginaPrefeituraPortal;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public List<SubPaginaMenuHorizontal> getSubPaginas() {
        return subPaginas;
    }

    public void setSubPaginas(List<SubPaginaMenuHorizontal> subPaginas) {
        this.subPaginas = subPaginas;
    }

    @Override
    public String toString() {
        return nome;
    }
}
