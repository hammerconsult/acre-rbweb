package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.webreportdto.dto.rh.AssistenteRelatorioTotalVerbasPorOrgaoDTO;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class AssistenteRelatorioTotalVerbasPorOrgao {
    private Integer mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolha;
    private Date dataAtual;
    private Boolean isDetalhado;
    private Boolean isRelatorioPorOrgao;
    private String eventoFPSelecionado;
    private List<Long> idsGruposMarcados;
    private String codigoHierarquia;
    private Integer versao;

    public AssistenteRelatorioTotalVerbasPorOrgao() {
        isDetalhado = Boolean.FALSE;
        isRelatorioPorOrgao = Boolean.FALSE;
        idsGruposMarcados = Lists.newArrayList();
    }

    public static AssistenteRelatorioTotalVerbasPorOrgaoDTO assistenteToDto(AssistenteRelatorioTotalVerbasPorOrgao assistente) {
        AssistenteRelatorioTotalVerbasPorOrgaoDTO assistenteDto = new AssistenteRelatorioTotalVerbasPorOrgaoDTO();
        assistenteDto.setMes(assistente.getMes());
        assistenteDto.setAno(assistente.getAno());
        assistenteDto.setTipoFolha(assistente.getTipoFolha() != null ? assistente.getTipoFolha().getToDto() : null);
        assistenteDto.setDataAtual(assistente.getDataAtual());
        assistenteDto.setDetalhado(assistente.getDetalhado());
        assistenteDto.setRelatorioPorOrgao(assistente.getRelatorioPorOrgao());
        assistenteDto.setEventoFPSelecionado(assistente.getEventoFPSelecionado());
        assistenteDto.setIdsGruposMarcados(assistente.getIdsGruposMarcados());
        assistenteDto.setCodigoHierarquia(assistente.getCodigoHierarquia());
        assistenteDto.setVersao(assistente.getVersao());
        return assistenteDto;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public TipoFolhaDePagamento getTipoFolha() {
        return tipoFolha;
    }

    public void setTipoFolha(TipoFolhaDePagamento tipoFolha) {
        this.tipoFolha = tipoFolha;
    }

    public Date getDataAtual() {
        return dataAtual;
    }

    public void setDataAtual(Date dataAtual) {
        this.dataAtual = dataAtual;
    }

    public Boolean getDetalhado() {
        return isDetalhado;
    }

    public void setDetalhado(Boolean detalhado) {
        isDetalhado = detalhado;
    }

    public Boolean getRelatorioPorOrgao() {
        return isRelatorioPorOrgao;
    }

    public void setRelatorioPorOrgao(Boolean relatorioPorOrgao) {
        isRelatorioPorOrgao = relatorioPorOrgao;
    }

    public List<Long> getIdsGruposMarcados() {
        return idsGruposMarcados;
    }

    public void setIdsGruposMarcados(List<Long> idsGruposMarcados) {
        this.idsGruposMarcados = idsGruposMarcados;
    }

    public String getEventoFPSelecionado() {
        return eventoFPSelecionado;
    }

    public void setEventoFPSelecionado(String eventoFPSelecionado) {
        this.eventoFPSelecionado = eventoFPSelecionado;
    }

    public String getCodigoHierarquia() {
        return codigoHierarquia;
    }

    public void setCodigoHierarquia(String codigoHierarquia) {
        this.codigoHierarquia = codigoHierarquia;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }
}
