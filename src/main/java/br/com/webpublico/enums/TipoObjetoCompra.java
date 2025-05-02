package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoObjetoCompraDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 06/09/13
 * Time: 10:31
 * To change this template use File | Settings | File Templates.
 */
public enum TipoObjetoCompra implements EnumComDescricao {
    CONSUMO("Material de Consumo", "Grupo Material", TipoObjetoCompraDTO.CONSUMO),
    PERMANENTE_MOVEL("Material Permanente", "Grupo Patrimonial", TipoObjetoCompraDTO.PERMANENTE_MOVEL),
    PERMANENTE_IMOVEL("Obras e Instalações", " ", TipoObjetoCompraDTO.PERMANENTE_IMOVEL),
    SERVICO("Serviço", "Grupo Serviço", TipoObjetoCompraDTO.SERVICO);
    private String descricao;
    private String descricaoGrupo;
    private TipoObjetoCompraDTO toDTO;

    TipoObjetoCompra(String descricao, String descricaoGrupo, TipoObjetoCompraDTO toDTO) {
        this.descricao = descricao;
        this.descricaoGrupo = descricaoGrupo;
        this.toDTO = toDTO;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoGrupo() {
        return descricaoGrupo;
    }

    public TipoObjetoCompraDTO getToDTO() {
        return toDTO;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static List<TipoObjetoCompra> getTiposObjetoCompraPermanente() {
        List<TipoObjetoCompra> toReturn = Lists.newArrayList();
        toReturn.add(PERMANENTE_MOVEL);
        toReturn.add(PERMANENTE_IMOVEL);
        return toReturn;
    }

    public static List<TipoObjetoCompra> getTiposObraEServicoEngenharia() {
        List<TipoObjetoCompra> toReturn = Lists.newArrayList();
        toReturn.add(PERMANENTE_IMOVEL);
        toReturn.add(SERVICO);
        return toReturn;
    }

    public static List<TipoObjetoCompra> getTiposObjetoCompraServico() {
        List<TipoObjetoCompra> toReturn = Lists.newArrayList();
        toReturn.add(SERVICO);
        return toReturn;
    }

    public static List<TipoObjetoCompra> getTiposObjetoCompraMaterial() {
        List<TipoObjetoCompra> toReturn = Lists.newArrayList();
        toReturn.add(CONSUMO);
        toReturn.add(PERMANENTE_IMOVEL);
        toReturn.add(PERMANENTE_MOVEL);
        return toReturn;
    }

    public static List<TipoObjetoCompra> getTiposObjetoCompraOrdemCompra() {
        List<TipoObjetoCompra> toReturn = Lists.newArrayList();
        toReturn.add(CONSUMO);
        toReturn.add(PERMANENTE_MOVEL);
        return toReturn;
    }

    public static List<TipoObjetoCompra> getTiposObjetoCompraOrdemCompraAndServico() {
        List<TipoObjetoCompra> toReturn = Lists.newArrayList();
        toReturn.add(SERVICO);
        toReturn.add(CONSUMO);
        toReturn.add(PERMANENTE_MOVEL);
        return toReturn;
    }

    public static List<String> recuperarListaName(List<TipoObjetoCompra> tiposObjetosCompra) {
        List<String> toReturn = Lists.newArrayList();
        for (TipoObjetoCompra tipo : tiposObjetosCompra) {
            toReturn.add(tipo.name());
        }
        return toReturn;
    }

    public boolean isObraInstalacoes() {
        return PERMANENTE_IMOVEL.equals(this);
    }

    public boolean isServico() {
        return SERVICO.equals(this);
    }

    public boolean isMaterialConsumo() {
        return CONSUMO.equals(this);
    }

    public boolean isMaterialPermanente() {
        return PERMANENTE_MOVEL.equals(this);
    }

    public boolean isPermanenteOrConsumo() {
        return isMaterialPermanente() || isMaterialConsumo();
    }

    public TipoControleLicitacao getTipoControleLicitacao() {
        switch (this) {
            case PERMANENTE_IMOVEL:
            case SERVICO:
                return TipoControleLicitacao.VALOR;
            default:
                return TipoControleLicitacao.QUANTIDADE;
        }
    }
}
