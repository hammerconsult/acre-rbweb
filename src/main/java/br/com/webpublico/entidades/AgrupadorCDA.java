package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.List;

@Entity

@Audited
@Etiqueta("Agrupador de Dívida Ativa")
@GrupoDiagrama(nome = "Divida Ativa")
public class AgrupadorCDA extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Length(max = 255)
    private String titulo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "agrupadorCDA", fetch = FetchType.EAGER)
    private List<AgrupadorCDADivida> dividas;

    @ManyToOne
    private ParametrosDividaAtiva parametrosDividaAtiva;

    public AgrupadorCDA() {
        dividas = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<AgrupadorCDADivida> getDividas() {
        return dividas;
    }

    public ParametrosDividaAtiva getParametrosDividaAtiva() {
        return parametrosDividaAtiva;
    }

    public void setParametrosDividaAtiva(ParametrosDividaAtiva parametrosDividaAtiva) {
        this.parametrosDividaAtiva = parametrosDividaAtiva;
    }

    public void setDividas(List<AgrupadorCDADivida> dividas) {
        this.dividas = dividas;
    }

    @Override
    public String toString() {
        String retorno = "";
        for (AgrupadorCDADivida divida : getDividas()) {
            retorno = retorno.length() > 2 ? ", " + divida.getDivida().getDescricao() : divida.getDivida().getDescricao();
        }
        return retorno;
    }
}
