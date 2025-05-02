/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.envers.Audited;

/**
 *
 * @author gustavo
 */
@Entity

@Audited
public class ConfiguracaoProtocolo extends ConfiguracaoModulo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Tabelavel
    @OneToOne(cascade= CascadeType.ALL)
    @Etiqueta("Informações")
    private Texto informacoes;
    @ManyToOne
    @Etiqueta("Tipo de Documento para Capa do Processo")
    private TipoDoctoOficial tipoDoctoCapaProcesso;
    @ManyToOne
    @Etiqueta("Tipo de Documento para Trâmite do Processo")
    private TipoDoctoOficial tipoDoctoTramiteProcesso;
    @ManyToOne
    @Etiqueta("Tipo de Documento para Capa do Protocolo")
    private TipoDoctoOficial tipoDoctoCapaProtocolo;
    @ManyToOne
    @Etiqueta("Tipo de Documento para Processo Web")
    private TipoDoctoOficial tipoDoctoProcessoWeb;
    @ManyToOne
    @Etiqueta("Tipo de Documento para Protocolo Web")
    private TipoDoctoOficial tipoDoctoProtocoloWeb;
    @ManyToOne(cascade= CascadeType.ALL)
    @Etiqueta("Arquivo do Manual")
    private Arquivo arquivoManual;

    public Texto getInformacoes() {
        return informacoes;
    }

    public void setInformacoes(Texto informacoes) {
        this.informacoes = informacoes;
    }

    public TipoDoctoOficial getTipoDoctoCapaProcesso() {
        return tipoDoctoCapaProcesso;
    }

    public void setTipoDoctoCapaProcesso(TipoDoctoOficial tipoDoctoCapaProcesso) {
        this.tipoDoctoCapaProcesso = tipoDoctoCapaProcesso;
    }

    public TipoDoctoOficial getTipoDoctoTramiteProcesso() {
        return tipoDoctoTramiteProcesso;
    }

    public void setTipoDoctoTramiteProcesso(TipoDoctoOficial tipoDoctoTramiteProcesso) {
        this.tipoDoctoTramiteProcesso = tipoDoctoTramiteProcesso;
    }

    public TipoDoctoOficial getTipoDoctoCapaProtocolo() {
        return tipoDoctoCapaProtocolo;
    }

    public void setTipoDoctoCapaProtocolo(TipoDoctoOficial tipoDoctoCapaProtocolo) {
        this.tipoDoctoCapaProtocolo = tipoDoctoCapaProtocolo;
    }

    public TipoDoctoOficial getTipoDoctoProcessoWeb() {
        return tipoDoctoProcessoWeb;
    }

    public void setTipoDoctoProcessoWeb(TipoDoctoOficial tipoDoctoProcessoWeb) {
        this.tipoDoctoProcessoWeb = tipoDoctoProcessoWeb;
    }

    public TipoDoctoOficial getTipoDoctoProtocoloWeb() {
        return tipoDoctoProtocoloWeb;
    }

    public void setTipoDoctoProtocoloWeb(TipoDoctoOficial tipoDoctoProtocoloWeb) {
        this.tipoDoctoProtocoloWeb = tipoDoctoProtocoloWeb;
    }

    public Arquivo getArquivoManual() {
        return arquivoManual;
    }

    public void setArquivoManual(Arquivo arquivoManual) {
        this.arquivoManual = arquivoManual;
    }

}
