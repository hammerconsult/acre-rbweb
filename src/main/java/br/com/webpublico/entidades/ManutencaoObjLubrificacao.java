/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Lubrificação")
public class ManutencaoObjLubrificacao extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ManutencaoObjetoFrota manutencaoObjetoFrota;
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;
    @Etiqueta("Lubrificante")
    private String lubrificante;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public ManutencaoObjetoFrota getManutencaoObjetoFrota() {
        return manutencaoObjetoFrota;
    }

    public void setManutencaoObjetoFrota(ManutencaoObjetoFrota manutencaoObjetoFrota) {
        this.manutencaoObjetoFrota = manutencaoObjetoFrota;
    }

    public String getLubrificante() {
        return lubrificante;
    }

    public void setLubrificante(String lubrificante) {
        this.lubrificante = lubrificante;
    }

    @Override
    public String toString() {
        String descricao = "";
        if (lubrificante != null) {
            descricao = lubrificante + " - ";
        }
        if (quantidade != null) {
            descricao += "Qtde: " + quantidade + "Lts";
        }
        return descricao;
    }
}
