package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.rh.TipoCalculo;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroFolhaDePagamentoDTO {
    @Etiqueta("Tipo de Cálculo")
    private TipoCalculo tipoCalculo;
    private Long hierarquiaOrganizacionalId;
    private Long itemEntidadeDPContasId;
    private Long vinculoFPId;
    private Long loteProcessamentoId;
    private List<Long> eventoFPsBloqueadosIds;
    private List<Long> hierarquiasOrganizacionaisIds;

    @JsonIgnore
    @Etiqueta("Calculada em")
    private Date calculadaEm;
    @JsonIgnore
    @Etiqueta("Órgão")
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @JsonIgnore
    @Etiqueta("Entidade")
    private ItemEntidadeDPContas itemEntidadeDPContas;
    @JsonIgnore
    @Etiqueta("Servidor")
    private VinculoFP vinculoFP;
    @JsonIgnore
    @Etiqueta("Lote de Processamento")
    private LoteProcessamento loteProcessamento;
    @JsonIgnore
    @Etiqueta("Verbas Bloqueadas")
    private List<EventoFP> eventosFPsBloqueados;
    @JsonIgnore
    @Etiqueta("Órgãos")
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais;

    public FiltroFolhaDePagamentoDTO() {
        this.tipoCalculo = TipoCalculo.INDIVIDUAL;
    }

    public Date getCalculadaEm() {
        return calculadaEm;
    }

    public void setCalculadaEm(Date calculadaEm) {
        this.calculadaEm = calculadaEm;
    }

    public TipoCalculo getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(TipoCalculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public Long getHierarquiaOrganizacionalId() {
        return hierarquiaOrganizacionalId;
    }

    public void setHierarquiaOrganizacionalId(Long hierarquiaOrganizacionalId) {
        this.hierarquiaOrganizacionalId = hierarquiaOrganizacionalId;
    }

    public Long getItemEntidadeDPContasId() {
        return itemEntidadeDPContasId;
    }

    public void setItemEntidadeDPContasId(Long itemEntidadeDPContasId) {
        this.itemEntidadeDPContasId = itemEntidadeDPContasId;
    }

    public Long getVinculoFPId() {
        return vinculoFPId;
    }

    public void setVinculoFPId(Long contratoFPId) {
        this.vinculoFPId = contratoFPId;
    }

    public Long getLoteProcessamentoId() {
        return loteProcessamentoId;
    }

    public void setLoteProcessamentoId(Long loteProcessamentoId) {
        this.loteProcessamentoId = loteProcessamentoId;
    }

    public List<Long> getEventoFPsBloqueadosIds() {
        return eventoFPsBloqueadosIds;
    }

    public void setEventoFPsBloqueadosIds(List<Long> eventoFPsBloqueadosIds) {
        this.eventoFPsBloqueadosIds = eventoFPsBloqueadosIds;
    }

    public List<Long> getHierarquiasOrganizacionaisIds() {
        return hierarquiasOrganizacionaisIds;
    }

    public void setHierarquiasOrganizacionaisIds(List<Long> hierarquiasOrganizacionaisIds) {
        this.hierarquiasOrganizacionaisIds = hierarquiasOrganizacionaisIds;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public ItemEntidadeDPContas getItemEntidadeDPContas() {
        return itemEntidadeDPContas;
    }

    public void setItemEntidadeDPContas(ItemEntidadeDPContas itemEntidadeDPContas) {
        this.itemEntidadeDPContas = itemEntidadeDPContas;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public LoteProcessamento getLoteProcessamento() {
        return loteProcessamento;
    }

    public void setLoteProcessamento(LoteProcessamento loteProcessamento) {
        this.loteProcessamento = loteProcessamento;
    }

    public List<EventoFP> getEventosFPsBloqueados() {
        return eventosFPsBloqueados;
    }

    public void setEventosFPsBloqueados(List<EventoFP> eventosFPsBloqueados) {
        this.eventosFPsBloqueados = eventosFPsBloqueados;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    @JsonIgnore
    public String getOrgaos() {
        if (hierarquiasOrganizacionais == null || hierarquiasOrganizacionais.isEmpty()) {
            return "";
        }
        return hierarquiasOrganizacionais.stream().map(HierarquiaOrganizacional::toString).collect(Collectors.joining("<br/>"));
    }

    @JsonIgnore
    public String getEventosBloqueados() {
        if (eventosFPsBloqueados == null || eventosFPsBloqueados.isEmpty()) {
            return "";
        }
        return eventosFPsBloqueados.stream().map(EventoFP::toString).collect(Collectors.joining("<br/>"));
    }

    public boolean hasEventosBloqueados() {
        if (eventosFPsBloqueados == null || eventosFPsBloqueados.isEmpty()) {
            return false;
        }
        return true;
    }

    public String toJSON() throws JsonProcessingException {
        if (hierarquiaOrganizacional != null) {
            this.hierarquiaOrganizacionalId = hierarquiaOrganizacional.getId();
        }
        if (itemEntidadeDPContas != null) {
            this.itemEntidadeDPContasId = itemEntidadeDPContas.getId();
        }
        if (vinculoFP != null) {
            this.vinculoFPId = vinculoFP.getId();
        }
        if (loteProcessamento != null) {
            this.loteProcessamentoId = loteProcessamento.getId();
        }
        if (eventosFPsBloqueados != null && !eventosFPsBloqueados.isEmpty()) {
            this.eventoFPsBloqueadosIds = eventosFPsBloqueados.stream().map(EventoFP::getId).collect(Collectors.toList());
        }
        if (hierarquiasOrganizacionais != null && !hierarquiasOrganizacionais.isEmpty()) {
            this.hierarquiasOrganizacionaisIds = hierarquiasOrganizacionais.stream().map(HierarquiaOrganizacional::getId).collect(Collectors.toList());
        }
        return Util.converterObjetoParaJson(this);
    }

    public static FiltroFolhaDePagamentoDTO deJSON(String json) {
        return Util.converterDeJsonParaObjeto(json, FiltroFolhaDePagamentoDTO.class);
    }

    public static FiltroFolhaDePagamentoDTO daEntidade(FiltroFolhaDePagamento filtro) {
        FiltroFolhaDePagamentoDTO dto = deJSON(filtro.getFiltroJSON());
        dto.setCalculadaEm(filtro.getCalculadaEm());
        return dto;
    }
}
