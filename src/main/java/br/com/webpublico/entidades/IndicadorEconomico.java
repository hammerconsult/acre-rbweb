/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.PeriodicidadeIndicador;
import br.com.webpublico.enums.TipoIndicador;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author java
 */
@GrupoDiagrama(nome = "Indicador Econômico")
@Entity

@Audited
@Etiqueta("Indicador Econômico")
public class IndicadorEconomico extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Nome")
    private String nome;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Sigla")
    private String sigla;
    @OneToMany(mappedBy = "indicadorEconomico",cascade= CascadeType.ALL, orphanRemoval=true)
    private List<ValorIndicadorEconomico> listaDeValorIndicador;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Indicador")
    private TipoIndicador tipoIndicador;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Periodicidade")
    private PeriodicidadeIndicador periodicidadeIndicador;

    public IndicadorEconomico() {
        this.criadoEm = System.nanoTime();
    }

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

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public List<ValorIndicadorEconomico> getListaDeValorIndicador() {
        return listaDeValorIndicador;
    }

    public void setListaDeValorIndicador(List<ValorIndicadorEconomico> listaDeValorIndicador) {
        this.listaDeValorIndicador = listaDeValorIndicador;
    }

    public TipoIndicador getTipoIndicador() {
        return tipoIndicador;
    }

    public void setTipoIndicador(TipoIndicador tipoIndicador) {
        this.tipoIndicador = tipoIndicador;
    }

    public PeriodicidadeIndicador getPeriodicidadeIndicador() {
        return periodicidadeIndicador;
    }

    public void setPeriodicidadeIndicador(PeriodicidadeIndicador periodicidadeIndicador) {
        this.periodicidadeIndicador = periodicidadeIndicador;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void inicializaListaValorIndicador(){
        listaDeValorIndicador = new ArrayList<>();
    }

    @Override
    public String toString() {
        return nome + " (" + sigla + ")";
    }
}
