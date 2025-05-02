package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;

import java.util.Date;
import java.util.List;

/**
 * @Author peixe on 19/05/2017  09:11.
 */
public interface FolhaCalculavel {

    Long getId();

    Mes getMes();

    Integer getAno();

    Integer getVersao();

    Date getCalculadaEm();

    UnidadeOrganizacional getUnidadeOrganizacional();

    Date getEfetivadaEm();

    TipoFolhaDePagamento getTipoFolhaDePagamento();

    CompetenciaFP getCompetenciaFP();

    DetalhesCalculoRH getDetalhesCalculoRHAtual();

    List<DetalhesCalculoRH> getDetalhesCalculoRHList();

    List<FiltroFolhaDePagamento> getFiltros();

    boolean isGravarMemoriaCalculo();

    void setGravarMemoriaCalculo(boolean gravarMemoriaCalculo);

    boolean isImprimeLogEmArquivo();

    boolean isProcessarCalculoTransient();

    void setProcessarCalculoTransient(boolean gravarMemoriaCalculo);

    LoteProcessamento getLoteProcessamento();

    void setLoteProcessamento(LoteProcessamento lote);

    void setCalculadaEm(Date calculadaEm);

    void setVersao(Integer calculadaEm);

    void setImprimeLogEmArquivo(boolean isImpremeEmLog);

    void setDetalhesCalculoRHAtual(DetalhesCalculoRH detalhes);

    void setFiltros(List<FiltroFolhaDePagamento> filtros);

    boolean isSimulacao();

}
