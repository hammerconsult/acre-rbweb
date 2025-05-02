/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Etiqueta("Sindicato")
@Audited
public class Sindicato extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Pessoa Jurídica")
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private PessoaJuridica pessoaJuridica;
    @Pesquisavel
    @Etiqueta("Dia data Base")
    @Obrigatorio
    @Tabelavel
    private Integer diaDataBase;
    @Pesquisavel
    @Etiqueta("Mês data Base")
    @Tabelavel
    @Obrigatorio
    private Integer mesDataBase;
    @Pesquisavel
    @Etiqueta("Código")
    @Tabelavel
    @Obrigatorio
    private Long codigo;
    @OneToMany(mappedBy = "sindicato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSindicato> itensSindicatos;
    @ManyToOne
    private EventoFP eventoFP;

    public Sindicato() {
    }

    public Integer getDiaDataBase() {
        return diaDataBase;
    }

    public void setDiaDataBase(Integer diaDataBase) {
        this.diaDataBase = diaDataBase;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMesDataBase() {
        return mesDataBase;
    }

    public void setMesDataBase(Integer mesDataBase) {
        this.mesDataBase = mesDataBase;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public List<ItemSindicato> getItensSindicatos() {
        return itensSindicatos;
    }

    public void setItensSindicatos(List<ItemSindicato> itensSindicatos) {
        this.itensSindicatos = itensSindicatos;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    @Override
    public String toString() {
        return codigo + " - " +pessoaJuridica + " - " + diaDataBase;
    }
}
