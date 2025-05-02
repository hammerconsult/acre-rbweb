/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Extinção de Cargos")
@Audited
@Entity
public class ExtincaoCargo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Ato Legal")
    @ManyToOne
    @Obrigatorio
    private AtoLegal atoLegal;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Extinção")
    private Date dataExtincao;
    @OneToOne(mappedBy = "extincaoCargo")
    @Etiqueta("Disponibilidade")
    private Disponibilidade disponibilidade;
    @Etiqueta("Cargos")
    @OneToMany(mappedBy = "extincaoCargo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemExtincaoCargo> itensExtincaoCargo;
    @Transient
    @Invisivel
    private Long criadoEm;

    public ExtincaoCargo() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getDataExtincao() {
        return dataExtincao;
    }

    public void setDataExtincao(Date dataExtincao) {
        this.dataExtincao = dataExtincao;
    }

    public List<ItemExtincaoCargo> getItensExtincaoCargo() {
        if (itensExtincaoCargo != null) {
            Collections.sort(itensExtincaoCargo, new Comparator<ItemExtincaoCargo>() {
                @Override
                public int compare(ItemExtincaoCargo o1, ItemExtincaoCargo o2) {
                    try {
                        Integer c1 = Integer.parseInt(o1.getCargo().getCodigoDoCargo());
                        Integer c2 = Integer.parseInt(o2.getCargo().getCodigoDoCargo());

                        return c1.compareTo(c2);
                    } catch (Exception e) {
                        return 0;
                    }
                }
            });
        }
        return itensExtincaoCargo;
    }

    public void setItensExtincaoCargo(List<ItemExtincaoCargo> itensExtincaoCargo) {
        this.itensExtincaoCargo = itensExtincaoCargo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Disponibilidade getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(Disponibilidade disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "Extinto em: "+new SimpleDateFormat("dd/MM/yyyy").format(dataExtincao) + " - Ato Legal: "+atoLegal;
    }
}
