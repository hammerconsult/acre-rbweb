package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.enums.TipoEstoque;

import java.math.BigDecimal;

/**
 * Created by Desenvolvimento on 02/02/2017.
 */
public interface AgrupadorLevantamentoEstoque {

    GrupoMaterial getGrupoMaterial();

    TipoEstoque getTipoEstoque();

    BigDecimal getQuantidade();

    BigDecimal getValorTotal();

    Boolean getGrupoExterno();
}
