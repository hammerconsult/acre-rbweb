package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Agrupador Grupo Objeto de Compra")
public class AgrupadorGOC extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Obrigatorio
    @Etiqueta("Imagem do Agrupador")
    //não está removendo em cascade
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Length(maximo = 255)
    @Etiqueta("Descrição")
    private String descricao;

    @OneToMany(mappedBy = "agrupadorGOC", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<AgrupadorGOCGrupo> grupos;

    public AgrupadorGOC() {
        grupos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public List<AgrupadorGOCGrupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<AgrupadorGOCGrupo> grupos) {
        this.grupos = grupos;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoResumida(){
        return descricao.length() > 25 ? descricao.substring(0, 25) + "..." : descricao;
    }
}
