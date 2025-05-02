package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.pccr.ModoGeracaoProgressao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 18/09/13
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Progressão Automática")
public class ProgressaoAuto extends SuperEntidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Geração")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;
    @Etiqueta("Progressões Automáticas")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "progressaoAuto", orphanRemoval = true)
    private List<ItemProgressaoAuto> itemProgressaoAutos;
    @Etiqueta("Modo de Geração")
    @Enumerated(EnumType.STRING)
    private ModoGeracaoProgressao modoGeracaoProgressao;
    @Etiqueta("Unidades da Progressão Automática")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "progressaoAuto", orphanRemoval = true)
    private List<UnidadeProgressaoAuto> unidades;

    public ProgressaoAuto() {
        itemProgressaoAutos = new ArrayList<>();
        dataCadastro = new Date();
        unidades = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public List<ItemProgressaoAuto> getItemProgressaoAutos() {
        return itemProgressaoAutos;
    }

    public void setItemProgressaoAutos(List<ItemProgressaoAuto> itemProgressaoAutos) {
        this.itemProgressaoAutos = itemProgressaoAutos;
    }

    public ModoGeracaoProgressao getModoGeracaoProgressao() {
        return modoGeracaoProgressao;
    }

    public void setModoGeracaoProgressao(ModoGeracaoProgressao modoGeracaoProgressao) {
        this.modoGeracaoProgressao = modoGeracaoProgressao;
    }

    public List<UnidadeProgressaoAuto> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeProgressaoAuto> unidades) {
        this.unidades = unidades;
    }

    @Override
    public String toString() {
        return Util.dateHourToString(dataCadastro);
    }
}
