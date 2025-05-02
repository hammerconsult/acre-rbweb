/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.pccr.MesesProgressao;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Plano Cargos Salários")
public class PlanoCargosSalarios extends SuperEntidade implements Serializable, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date inicioVigencia;
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Final da Vigência")
    private Date finalVigencia;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @Etiqueta("Tipo PCS")
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private TipoPCS tipoPCS;
    @OneToMany(mappedBy = "planoCargosSalarios", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntidadePCS> entidadesPCS = new ArrayList<>();
    @OneToMany(mappedBy = "planoCargosSalarios", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MesesProgressao> mesesProgressao = new ArrayList<>();
    @OneToMany(mappedBy = "planoCargosSalarios", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MesesPromocao> mesesPromocao = new ArrayList<>();

    public PlanoCargosSalarios() {
    }

    public List<EntidadePCS> getEntidadesPCS() {
        return entidadesPCS;
    }

    public void setEntidadesPCS(List<EntidadePCS> entidadesPCS) {
        this.entidadesPCS = entidadesPCS;
    }

    public PlanoCargosSalarios(Date inicioVigencia, Date finalVigencia, String descricao) {
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return this.getFinalVigencia();
    }

    @Override
    public void setFimVigencia(Date data) {
        this.setFinalVigencia(data);
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public TipoPCS getTipoPCS() {
        return tipoPCS;
    }

    public void setTipoPCS(TipoPCS tipoPCS) {
        this.tipoPCS = tipoPCS;
    }

    public List<MesesProgressao> getMesesProgressao() {
        return mesesProgressao;
    }

    public void setMesesProgressao(List<MesesProgressao> mesesProgressao) {
        this.mesesProgressao = mesesProgressao;
    }

    public List<MesesPromocao> getMesesPromocao() {
        return mesesPromocao;
    }

    public void setMesesPromocao(List<MesesPromocao> mesesPromocao) {
        this.mesesPromocao = mesesPromocao;
    }

    public boolean temConfiguracaoDeMesesParaPromocao() {
        return !CollectionUtils.isEmpty(mesesPromocao);
    }

    public List<MesesProgressao> getMesesProgressaoOrdenadosInicioVigenciaDesc() {
        if (mesesProgressao == null) {
            return null;
        }

        Collections.sort(mesesProgressao, new Comparator<MesesProgressao>() {
            @Override
            public int compare(MesesProgressao o1, MesesProgressao o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
        return mesesProgressao;
    }

    public List<MesesProgressao> getMesesProgressaoOrdenadosInicioVigenciaAsc() {
        if (mesesProgressao == null) {
            return null;
        }

        Collections.sort(mesesProgressao, new Comparator<MesesProgressao>() {
            @Override
            public int compare(MesesProgressao o1, MesesProgressao o2) {
                return o1.getInicioVigencia().compareTo(o2.getInicioVigencia());
            }
        });
        return mesesProgressao;
    }

    public void ordenarMesesProgressaoInicioVigenciaAsc() {
        if (mesesProgressao == null) {
            return;
        }

        Collections.sort(mesesProgressao, new Comparator<MesesProgressao>() {
            @Override
            public int compare(MesesProgressao o1, MesesProgressao o2) {
                return o1.getInicioVigencia().compareTo(o2.getInicioVigencia());
            }
        });
    }


    public List<MesesPromocao> getMesesPromocaoOrdenadosInicioVigenciaDesc() {
        Collections.sort(mesesPromocao, new Comparator<MesesPromocao>() {
            @Override
            public int compare(MesesPromocao o1, MesesPromocao o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
        return mesesPromocao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public void adicionarConfiguracaoMesesPromocao(MesesPromocao mp) {
        mesesPromocao = Util.adicionarObjetoEmLista(mesesPromocao, mp);
    }

    public void removerConfiguracaoMesesPromocao(MesesPromocao mp) {
        if (mesesPromocao.contains(mp)) {
            mesesPromocao.remove(mp);
        }
    }
}
