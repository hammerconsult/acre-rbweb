package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.ItemDemonstrativoComponenteDTO;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 02/09/13
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */
public class ItemDemonstrativoComponente {

    private String descricao;
    private String nome;
    private Integer ordem;
    private Integer grupo;
    private Integer coluna;
    private Integer espaco;
    private ItemDemonstrativo itemDemonstrativo;
    private String numeroNota;

    public ItemDemonstrativoComponente() {
    }

    public ItemDemonstrativoComponente(ItemDemonstrativo itemDemonstrativo) {
        setNome(itemDemonstrativo.getNome());
        setDescricao(itemDemonstrativo.getDescricao());
        setOrdem(itemDemonstrativo.getOrdem());
        setColuna(itemDemonstrativo.getColuna());
        setGrupo(itemDemonstrativo.getGrupo());
        setEspaco(itemDemonstrativo.getEspaco());
        setItemDemonstrativo(itemDemonstrativo);
    }

    public static List<ItemDemonstrativoComponenteDTO> itensDemonstrativosComponentesToDto(List<ItemDemonstrativoComponente> itens) {
        List<ItemDemonstrativoComponenteDTO> toReturn = Lists.newArrayList();
        for (ItemDemonstrativoComponente item : itens) {
            toReturn.add(itemDemonstrativoComponenteToDto(item));
        }
        return toReturn;
    }

    private static ItemDemonstrativoComponenteDTO itemDemonstrativoComponenteToDto(ItemDemonstrativoComponente item) {
        ItemDemonstrativoComponenteDTO itemDto = new ItemDemonstrativoComponenteDTO();
        itemDto.setDescricao(item.getDescricao());
        itemDto.setNome(item.getNome());
        itemDto.setOrdem(item.getOrdem());
        itemDto.setGrupo(item.getGrupo());
        itemDto.setColuna(item.getColuna());
        itemDto.setEspaco(item.getEspaco());
        itemDto.setItemDemonstrativo(item.getItemDemonstrativo().toDto());
        itemDto.setDescricaoComEspacos(item.getDescricaoComEspacos());
        itemDto.setNumeroNota(item.getNumeroNota());
        return itemDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoComEspacos() {
        return StringUtil.preencheString("", espaco, ' ') + nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ItemDemonstrativo getItemDemonstrativo() {
        return itemDemonstrativo;
    }

    public void setItemDemonstrativo(ItemDemonstrativo itemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Integer getGrupo() {
        return grupo;
    }

    public void setGrupo(Integer grupo) {
        this.grupo = grupo;
    }

    public Integer getColuna() {
        return coluna;
    }

    public void setColuna(Integer coluna) {
        this.coluna = coluna;
    }

    public Integer getEspaco() {
        return espaco;
    }

    public void setEspaco(Integer espaco) {
        this.espaco = espaco;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }
}
