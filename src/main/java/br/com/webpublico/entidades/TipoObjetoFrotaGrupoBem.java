package br.com.webpublico.entidades;

import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotas;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mga on 16/04/2018.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Tipo Objeto Frota Grupo Patrimonial")
@Table(name = "TIPOOBJETOFROTAGRUPOBEM")
public class TipoObjetoFrotaGrupoBem extends SuperEntidade implements ValidadorVigencia {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Objeto Frota")
    private TipoObjetoFrota tipoObjetoFrota;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Grupo Patrimonial")
    @Tabelavel
    @Pesquisavel
    private GrupoBem grupoPatrimonial;

    @ManyToOne
    @Etiqueta("Parâmetro Frota")
    @Tabelavel
    @Pesquisavel
    private ParametroFrotas parametroFrotas;

    @Tabelavel
    @Etiqueta("Início de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Obrigatorio
    private Date inicioVigencia;

    @Tabelavel
    @Etiqueta("Fim de Vigência")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;

    public TipoObjetoFrotaGrupoBem() {
//        super();
    }

    public ParametroFrotas getParametroFrotas() {
        return parametroFrotas;
    }

    public void setParametroFrotas(ParametroFrotas parametroFrotas) {
        this.parametroFrotas = parametroFrotas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GrupoBem getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(GrupoBem grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public TipoObjetoFrota getTipoObjetoFrota() {
        return tipoObjetoFrota;
    }

    public void setTipoObjetoFrota(TipoObjetoFrota tipoObjetoFrota) {
        this.tipoObjetoFrota = tipoObjetoFrota;
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

    @Override
    public String toString() {
        try {
            return this.tipoObjetoFrota.getDescricao() + " - " + this.grupoPatrimonial.toString() + ". Vigente em: " + DataUtil.getDataFormatada(this.inicioVigencia);
        } catch (NullPointerException ne) {
            return "";
        }
    }
}
