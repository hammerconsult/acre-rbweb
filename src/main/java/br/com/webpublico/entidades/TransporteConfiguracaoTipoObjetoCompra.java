package br.com.webpublico.entidades;

import br.com.webpublico.enums.SubTipoObjetoCompra;
import br.com.webpublico.enums.TipoConfiguracaoContabil;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.persistence.*;

@GrupoDiagrama(nome = "Contabil")
@Entity
@Table(name = "TRANSPCONFIGTIPOOBJETOCOMPRA")
@Etiqueta("Transporte Configuração Tipo de Objeto de Compra")
public class TransporteConfiguracaoTipoObjetoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Configuração")
    private TipoConfiguracaoContabil tipoConfiguracaoContabil;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Transporte Configuração Contábil")
    private TransporteConfiguracaoContabil transporteConfigContabil;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Objeto de Compra")
    @Tabelavel
    @Pesquisavel
    private TipoObjetoCompra tipoObjetoCompra;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Subtipo Objeto de Compra")
    @Tabelavel
    @Pesquisavel
    private SubTipoObjetoCompra subtipoObjetoCompra;

    @Obrigatorio
    @Etiqueta("Log")
    private String log;

    public TransporteConfiguracaoTipoObjetoCompra() {
    }

    public TransporteConfiguracaoTipoObjetoCompra(TipoConfiguracaoContabil tipoConfiguracaoContabil, TransporteConfiguracaoContabil transporteConfigContabil, TipoObjetoCompra tipoObjetoCompra, SubTipoObjetoCompra subtipoObjetoCompra, String log) {
        this.tipoConfiguracaoContabil = tipoConfiguracaoContabil;
        this.transporteConfigContabil = transporteConfigContabil;
        this.tipoObjetoCompra = tipoObjetoCompra;
        this.subtipoObjetoCompra = subtipoObjetoCompra;
        this.log = log;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoConfiguracaoContabil getTipoConfiguracaoContabil() {
        return tipoConfiguracaoContabil;
    }

    public void setTipoConfiguracaoContabil(TipoConfiguracaoContabil tipoConfiguracaoContabil) {
        this.tipoConfiguracaoContabil = tipoConfiguracaoContabil;
    }

    public TransporteConfiguracaoContabil getTransporteConfigContabil() {
        return transporteConfigContabil;
    }

    public void setTransporteConfigContabil(TransporteConfiguracaoContabil transporteConfigContabil) {
        this.transporteConfigContabil = transporteConfigContabil;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public SubTipoObjetoCompra getSubtipoObjetoCompra() {
        return subtipoObjetoCompra;
    }

    public void setSubtipoObjetoCompra(SubTipoObjetoCompra subtipoObjetoCompra) {
        this.subtipoObjetoCompra = subtipoObjetoCompra;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public String toString() {
        return tipoObjetoCompra.getDescricao() + " - " + subtipoObjetoCompra.getDescricao();
    }
}
