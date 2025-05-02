package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.contabil.execucao.NotaExplicativaFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class NotaExplicativaComponenteControlador implements Serializable {
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private NotaExplicativaFacade notaExplicativaFacade;
    private String nomeRelatorio;
    private TipoRelatorioItemDemonstrativo tipoRelatorioItemDemonstrativo;
    private Exercicio exercicio;
    private String conteudoNotaExplicativa;
    private String dataReferenciaNota;

    public NotaExplicativaComponenteControlador() {
    }

    public String getNomeRelatorio() {
        return nomeRelatorio;
    }

    public void setNomeRelatorio(String nomeRelatorio) {
        this.nomeRelatorio = nomeRelatorio;
    }

    public TipoRelatorioItemDemonstrativo getTipoRelatorioItemDemonstrativo() {
        return tipoRelatorioItemDemonstrativo;
    }

    public void setTipoRelatorioItemDemonstrativo(TipoRelatorioItemDemonstrativo tipoRelatorioItemDemonstrativo) {
        this.tipoRelatorioItemDemonstrativo = tipoRelatorioItemDemonstrativo;
    }

    public void novo(String nomeRelatorio, TipoRelatorioItemDemonstrativo tipoRelatorioItemDemonstrativo, Exercicio exercicio, String dataReferenciaNota) {
        this.nomeRelatorio = nomeRelatorio;
        this.tipoRelatorioItemDemonstrativo = tipoRelatorioItemDemonstrativo;
        this.dataReferenciaNota = dataReferenciaNota;
        if (exercicio == null) {
            this.exercicio = sistemaFacade.getExercicioCorrente();
        } else {
            this.exercicio = exercicio;
        }
        this.conteudoNotaExplicativa = notaExplicativaFacade.buscarConteudoNotaExplicativa(this.exercicio, this.nomeRelatorio, this.tipoRelatorioItemDemonstrativo, this.dataReferenciaNota);
    }

    public String getConteudoNotaExplicativa() {
        return conteudoNotaExplicativa;
    }

    public void setConteudoNotaExplicativa(String conteudoNotaExplicativa) {
        this.conteudoNotaExplicativa = conteudoNotaExplicativa;
    }

    public String getDataReferenciaNota() {
        return dataReferenciaNota;
    }

    public void setDataReferenciaNota(String dataReferenciaNota) {
        this.dataReferenciaNota = dataReferenciaNota;
    }
}
