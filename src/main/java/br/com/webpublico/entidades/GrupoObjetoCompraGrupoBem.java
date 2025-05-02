package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ROBSONLUIS-MGA
 * Date: 27/11/13
 * Time: 08:36
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Patrimonio")
@Etiqueta("Associação Grupo de Objeto de Compra e Grupo Patrimonial")
@Table(name = "GRUPOOBJCOMPRAGRUPOBEM")
public class GrupoObjetoCompraGrupoBem extends SuperEntidade implements ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Início de Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;

    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Fim de Vigência")
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Grupo Patrimonial")
    @ManyToOne
    private GrupoBem grupoBem;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Grupo de Objeto de Compra")
    @ManyToOne
    private GrupoObjetoCompra grupoObjetoCompra;

    public GrupoObjetoCompraGrupoBem() {
    }

    public GrupoObjetoCompraGrupoBem(Date inicioVigencia, GrupoBem grupoBem, GrupoObjetoCompra grupoObjetoCompra) {
        super();
        this.inicioVigencia = inicioVigencia;
        this.grupoBem = grupoBem;
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public Date getFimVigencia() {
        return fimVigencia;
    }

    @Override
    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    @Override
    public String toString() {
        return "GrupoObjetoCompraGrupoBem" + id;
    }
}
