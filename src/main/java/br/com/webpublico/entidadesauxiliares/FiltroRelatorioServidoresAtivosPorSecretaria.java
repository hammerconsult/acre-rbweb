package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.webreportdto.dto.rh.RelatorioServidoresAtivosPorSecretariaDTO;
import br.com.webpublico.webreportdto.dto.rh.TipoFolhaDePagamentoDTO;

/**
 * @author octavio
 */
public class FiltroRelatorioServidoresAtivosPorSecretaria {

    private Mes mesInicial;
    private Mes mesFinal;
    private Integer anoInicial;
    private Integer anoFinal;
    private TipoFolhaDePagamento tipoFolha;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private boolean detalharPorMes;

    public FiltroRelatorioServidoresAtivosPorSecretaria() {
    }

    public static RelatorioServidoresAtivosPorSecretariaDTO servidoresAtivosToDto(FiltroRelatorioServidoresAtivosPorSecretaria parametrosRelatorio) {
        RelatorioServidoresAtivosPorSecretariaDTO parametro = new RelatorioServidoresAtivosPorSecretariaDTO();
        parametro.setAnoFinal(parametrosRelatorio.getAnoFinal());
        parametro.setAnoInicial(parametrosRelatorio.getAnoInicial());
        parametro.setCodigoHierarquia(parametrosRelatorio.getHierarquiaOrganizacional().getCodigoSemZerosFinais() + "%");
        parametro.setDetalharPorMes(parametrosRelatorio.isDetalharPorMes());
        parametro.setMesInicial(parametrosRelatorio.getMesInicial().getNumeroMes());
        parametro.setMesFinal(parametrosRelatorio.getMesFinal().getNumeroMes());
        parametro.setTipoFolha(TipoFolhaDePagamentoDTO.valueOf(parametrosRelatorio.getTipoFolha().name()));
        return parametro;
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public Integer getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(Integer anoInicial) {
        this.anoInicial = anoInicial;
    }

    public Integer getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(Integer anoFinal) {
        this.anoFinal = anoFinal;
    }

    public TipoFolhaDePagamento getTipoFolha() {
        return tipoFolha;
    }

    public void setTipoFolha(TipoFolhaDePagamento tipoFolha) {
        this.tipoFolha = tipoFolha;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public boolean isDetalharPorMes() {
        return detalharPorMes;
    }

    public void setDetalharPorMes(boolean detalharPorMes) {
        this.detalharPorMes = detalharPorMes;
    }
}
