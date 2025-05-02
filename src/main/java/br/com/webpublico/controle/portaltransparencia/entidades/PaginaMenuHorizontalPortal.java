package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.TipoQuadroPaginaInicialPortal;
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

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Table(name = "PAGINAMENUHORIZPORTAL")
public class PaginaMenuHorizontalPortal extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Temporal(value = TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Etiqueta("Fim de Vigência")
    @Temporal(value = TemporalType.DATE)
    private Date fimVigencia;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo")
    private TipoQuadroPaginaInicialPortal tipo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paginaMenuHorizontalPortal", orphanRemoval = true)
    private List<PaginaMenuHorizontalPaginaPrefeitura> paginasPrefeituraPortal;

    public PaginaMenuHorizontalPortal() {
        paginasPrefeituraPortal = Lists.newArrayList();
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

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public TipoQuadroPaginaInicialPortal getTipo() {
        return tipo;
    }

    public void setTipo(TipoQuadroPaginaInicialPortal tipo) {
        this.tipo = tipo;
    }

    public List<PaginaMenuHorizontalPaginaPrefeitura> getPaginasPrefeituraPortal() {
        return paginasPrefeituraPortal;
    }

    public void setPaginasPrefeituraPortal(List<PaginaMenuHorizontalPaginaPrefeitura> paginasPrefeituraPortal) {
        this.paginasPrefeituraPortal = paginasPrefeituraPortal;
    }
}
