package br.com.webpublico.entidades;

import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 16/05/14
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta(value = "Configuração de Tipo de Conta de Despesa/Classe Credor")
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Table(name = "CONFTIPOCONTACLASSECREDOR")
public class ConfiguracaoTipoContaDespesaClasseCredor extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Conta de Despesa")
    @Obrigatorio
    private TipoContaDespesa tipoContaDespesa;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    @Etiqueta(value = "Subtipo de Despesa")
    @Pesquisavel
    private SubTipoDespesa subTipoDespesa;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Inicio de Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Fim de Vigência")
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configuracao")
    @Tabelavel
    @Etiqueta("Classes de Pessoa")
    private List<TipoContaDespesaClasseCredor> listaDeClasseCredor;


    public ConfiguracaoTipoContaDespesaClasseCredor() {
        super();
        listaDeClasseCredor = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public List<TipoContaDespesaClasseCredor> getListaDeClasseCredor() {
        return listaDeClasseCredor;
    }

    public void setListaDeClasseCredor(List<TipoContaDespesaClasseCredor> listaDeClasseCredor) {
        this.listaDeClasseCredor = listaDeClasseCredor;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public SubTipoDespesa getSubTipoDespesa() {
        return subTipoDespesa;
    }

    public void setSubTipoDespesa(SubTipoDespesa subTipoDespesa) {
        this.subTipoDespesa = subTipoDespesa;
    }

    @Override
    public String toString() {
        return tipoContaDespesa.getCodigo() + " - " + tipoContaDespesa.getDescricao();
    }
}
