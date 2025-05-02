package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.tributario.FiltroRelacaoLancamentoIptuDTO;
import br.com.webpublico.webreportdto.dto.tributario.SituacaoParcelaWebreportDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoCobrancaTributarioDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoRelatorioDTO;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 25/06/15
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoIptu extends AbstractFiltroRelacaoLancamentoDebito {

    private TipoImovel tipoImovel;
    private String setorInicial;
    private String setorFinal;
    private String quadraInicial;
    private String quadraFinal;
    private Bairro bairro;
    private Logradouro logradouro;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private List<Exercicio> exerciciosSelecionados;
    private List<Setor> setoresSelecionados;
    private Boolean ativo;
    private SituacaoParcela[] situacoesHabitese;

    public FiltroRelacaoLancamentoIptu() {
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setSituacoesHabitese(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
        setAtivo(Boolean.TRUE);
        setEmitirHabitese(Boolean.FALSE);
    }

    public FiltroRelacaoLancamentoIptuDTO toDto() {
        FiltroRelacaoLancamentoIptuDTO retorno = new FiltroRelacaoLancamentoIptuDTO();
        //AbstractFiltroRelacaoLancamentoDebito
        retorno.setWhere(this.getWhere());
        retorno.setFiltro(this.getFiltro());
        retorno.setIdsDivida(this.inDividas());
        retorno.setDividas(this.filtroDividas());
        retorno.setIdTributo(this.getTributo() != null ? this.getTributo().getId() : null);
        retorno.setDescricaoTributo(this.getTributo() != null ? this.getTributo().getDescricao() : null);
        retorno.setSomenteTotalizador(this.getSomenteTotalizador());
        retorno.setTotalizadoresPorBairro(this.getTotalizadoresPorBairro());
        retorno.setTotalizadoresPorSecretaria(this.getTotalizadoresPorSecretaria());
        retorno.setExercicioDividaInicial(this.getExercicioDividaInicial());
        retorno.setExercicioDividaFinal(this.getExercicioDividaFinal());
        retorno.setExercicioInicial(this.getExercicioInicial() != null ? this.getExercicioInicial().getAno() : null);
        retorno.setExercicioFinal(this.getExercicioFinal() != null ? this.getExercicioFinal().getAno() : null);
        retorno.setDataLancamentoInicial(this.getDataLancamentoInicial());
        retorno.setDataLancamentoFinal(this.getDataLancamentoFinal());
        retorno.setDataVencimentoInicial(this.getDataVencimentoInicial());
        retorno.setDataVencimentoFinal(this.getDataVencimentoFinal());
        retorno.setDataPagamentoInicial(this.getDataPagamentoInicial());
        retorno.setDataPagamentoFinal(this.getDataPagamentoFinal());
        retorno.setDataMovimentoInicial(this.getDataMovimentoInicial());
        retorno.setDataMovimentoFinal(this.getDataMovimentoFinal());
        retorno.setTotalLancadoInicial(this.getTotalLancadoInicial());
        retorno.setTotalLancadoFinal(this.getTotalLancadoFinal());
        retorno.setTipoCobrancaTributario(this.getTipoCobrancaTributario() != null ? TipoCobrancaTributarioDTO.valueOf(this.getTipoCobrancaTributario().name()) : null);
        retorno.setTipoRelatorio(TipoRelatorioDTO.valueOf(this.getTipoRelatorio().name()));
        retorno.setEmitirHabitese(this.isEmitirHabitese());
        retorno.setSituacoes(montarSituacoesParcela(this.getSituacoes()));
        retorno.setIdsContribuintes(this.getContribuintes() != null ? Util.montarIdsList(this.getContribuintes()) : null);
        retorno.setContribuintes(montarNomeContribuintes(this.getContribuintes()));
        //FiltroRelacaoLancamentoIptu
        retorno.setInscricaoInicial(this.getInscricaoInicial());
        retorno.setInscricaoFinal(this.getInscricaoFinal());
        retorno.setTipoImovel(this.getTipoImovel() != null ? this.getTipoImovel().getToDto() : null);
        retorno.setSetorInicial(this.getSetorInicial());
        retorno.setSetorFinal(this.getSetorFinal());
        retorno.setQuadraInicial(this.getQuadraInicial());
        retorno.setQuadraFinal(this.getQuadraFinal());
        retorno.setIdBairro(this.getBairro() != null ? this.getBairro().getId() : null);
        retorno.setDescricaoBairro(this.getBairro() != null ? this.getBairro().getDescricao() : null);
        retorno.setIdLogradouro(this.getLogradouro() != null ? this.getLogradouro().getId() : null);
        retorno.setDescricaoLogradouro(this.getLogradouro() != null ? this.getLogradouro().getNome() : null);
        retorno.setIdsExercicios(montarIdsExercicios(this.getExerciciosSelecionados()));
        retorno.setIdsSetores(montarIdsSetores(this.getSetoresSelecionados()));
        retorno.setAtivo(this.getAtivo());
        retorno.setSituacoesHabitese(this.getSituacoesHabitese() != null ? montarSituacoesParcela(this.getSituacoesHabitese()) : null);
        return retorno;
    }

    private List<Number> montarIdsSetores(List<Setor> setoresSelecionados) {
        List<Number> idsSetores = Lists.newArrayList();
        if(setoresSelecionados != null) {
            for (Setor s : setoresSelecionados) {
                idsSetores.add(s.getId());
            }
        }
        return idsSetores;
    }

    private List<Number> montarIdsExercicios(List<Exercicio> exerciciosSelecionados) {
        List<Number> idsExercicios = Lists.newArrayList();
        if(exerciciosSelecionados != null) {
            for (Exercicio ex : exerciciosSelecionados) {
                idsExercicios.add(ex.getId());
            }
        }
        return idsExercicios;
    }

    private String montarNomeContribuintes(List<Pessoa> contribuintes) {
        List<String> nomes = Lists.newArrayList();
        String nomesContribuintes = "";
        if(contribuintes != null) {
            for (Pessoa contribuinte : contribuintes) {
                nomes.add(contribuinte.getNomeCpfCnpj());
            }
            nomesContribuintes = Joiner.on(", ").join(nomes);
        }

        return nomesContribuintes;
    }

    private SituacaoParcelaWebreportDTO[] montarSituacoesParcela(SituacaoParcela[] situacoesParcelas) {
        List<SituacaoParcelaWebreportDTO> retorno = Lists.newArrayList();
        for (SituacaoParcela situacao : situacoesParcelas) {
            retorno.add(SituacaoParcelaWebreportDTO.valueOf(situacao.name()));
        }
        SituacaoParcelaWebreportDTO[] situacoes = new SituacaoParcelaWebreportDTO[retorno.size()];
        return retorno.toArray(situacoes);
    }

    public TipoImovel getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(TipoImovel tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public String getSetorInicial() {
        return setorInicial;
    }

    public void setSetorInicial(String setorInicial) {
        this.setorInicial = setorInicial;
    }

    public String getSetorFinal() {
        return setorFinal;
    }

    public void setSetorFinal(String setorFinal) {
        this.setorFinal = setorFinal;
    }

    public String getQuadraInicial() {
        return quadraInicial;
    }

    public void setQuadraInicial(String quadraInicial) {
        this.quadraInicial = quadraInicial;
    }

    public String getQuadraFinal() {
        return quadraFinal;
    }

    public void setQuadraFinal(String quadraFinal) {
        this.quadraFinal = quadraFinal;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public List<Exercicio> getExerciciosSelecionados() {
        return exerciciosSelecionados;
    }

    public void setExerciciosSelecionados(List<Exercicio> exerciciosSelecionados) {
        this.exerciciosSelecionados = exerciciosSelecionados;
    }

    public List<Setor> getSetoresSelecionados() {
        return setoresSelecionados;
    }

    public void setSetoresSelecionados(List<Setor> setoresSelecionados) {
        this.setoresSelecionados = setoresSelecionados;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public SituacaoParcela[] getSituacoesHabitese() {
        return situacoesHabitese;
    }

    public void setSituacoesHabitese(SituacaoParcela[] situacoesHabitese) {
        this.situacoesHabitese = situacoesHabitese;
    }
}
