package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoConservacaoBem;
import br.com.webpublico.enums.TipoAquisicaoBem;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 06/05/14
 * Time: 09:47
 * To change this template use File | Settings | File Templates.
 */
public interface CaracterizadorDeBem {

    public Long getId();

    public Date getDataDaOperacao();

    public Date getDataLancamento();

    public BigDecimal getValorDoBem();

    public String getCodigoPatrimonio();

    public String getDescricaoDoBem();

    public UnidadeOrganizacional getUnidadeAdministrativa();

    public UnidadeOrganizacional getUnidadeOrcamentaria();

    public GrupoBem getGrupoBem();

    public void setGrupoBem(GrupoBem gb);

    public TipoAquisicaoBem getTipoAquisicaoBem();

    public String getModelo();

    public String getMarca();

    public String getLocalizacao();

    EstadoConservacaoBem getEstadoConservacaoBem();

    SituacaoConservacaoBem getSituacaoConservacaoBem();

    GrupoObjetoCompra getGrupoObjetoCompra();

    String getObservacao();

    DetentorOrigemRecurso getDetentorOrigemRecurso();

    Pessoa getFornecedor();

    List<BemNotaFiscal> getNotasFiscais();
}
