package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoGrupo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 17/10/14
 * Time: 09:40
 * To change this template use File | Settings | File Templates.
 */
public interface EventoBemIncorporavelComContabil {

    Boolean ehEstorno();

    UnidadeOrganizacional getUnidadeOrcamentariaInicial();

    UnidadeOrganizacional getUnidadeOrcamentariaResultante();

    UnidadeOrganizacional getUnidadeAdministrativaInicial();

    UnidadeOrganizacional getUnidadeAdministrativaResultante();

    TipoGrupo getTipoGrupoInicial();

    TipoGrupo getTipoGrupoResultante();

    GrupoBem getGrupoBemInicial();

    GrupoBem getGrupoBemResultante();

    TipoBem getTipoBemResultante();

    BigDecimal getValorDoLancamento();

    Date getDataDoLancamento();

    BigDecimal getValorDepreciacao();

    BigDecimal getValorAmortizacao();

    BigDecimal getValorExaustao();

    BigDecimal getValorReducao();

    BigDecimal getValorOriginal();

    Bem getBem();
}
