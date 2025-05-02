/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusLancePregao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Rodada Pregão")
public class RodadaPregao extends SuperEntidade implements Comparable<RodadaPregao>{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Item Pregão")
    private ItemPregao itemPregao;

    @Etiqueta("Número")
    private Integer numero;

    @Invisivel
    @OneToMany(mappedBy = "rodadaPregao", cascade = CascadeType.REMOVE)
    private List<LancePregao> listaDeLancePregao;

    @Etiqueta("Motivo")
    private String justificativaExclusao;

    @Etiqueta("Observação")
    private String observacao;

    public RodadaPregao() {
        super();
        listaDeLancePregao = new ArrayList<LancePregao>();
        justificativaExclusao = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemPregao getItemPregao() {
        return itemPregao;
    }

    public void setItemPregao(ItemPregao itemPregao) {
        this.itemPregao = itemPregao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public List<LancePregao> getListaDeLancePregao() {
        return listaDeLancePregao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setListaDeLancePregao(List<LancePregao> listaDeLancePregao) {
        this.listaDeLancePregao = listaDeLancePregao;
    }

    public String getJustificativaExclusao() {
        return justificativaExclusao;
    }

    public void setJustificativaExclusao(String justificativaExclusao) {
        this.justificativaExclusao = justificativaExclusao;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.RodadaPregao[ id=" + id + " ]";
    }

    public List<LancePregao> getLancesAtivo() {
        List<LancePregao> lancesAtivos = new ArrayList<>();
        if (this.getListaDeLancePregao() != null && !this.getListaDeLancePregao().isEmpty()) {
            for (LancePregao lancePregao : this.getListaDeLancePregao()) {
                if (StatusLancePregao.ATIVO.equals(lancePregao.getStatusLancePregao())) {
                    lancesAtivos.add(lancePregao);
                }
            }
        }
        return lancesAtivos;
    }

    public LancePregao getLanceVencedor() {
        for (LancePregao lancePregao : this.getListaDeLancePregao()) {
            if (lancePregao.getStatusLancePregao().isVencedor()) {
                return lancePregao;
            }
        }
        return null;
    }

    public boolean temVencedor() {
        for (LancePregao lancePregao : this.getListaDeLancePregao()) {
            if (StatusLancePregao.VENCEDOR.equals(lancePregao.getStatusLancePregao())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(RodadaPregao o) {
        if (o.getNumero() !=null && getNumero() !=null){
            return getNumero().compareTo(o.getNumero());
        }
        return 0;
    }
}
