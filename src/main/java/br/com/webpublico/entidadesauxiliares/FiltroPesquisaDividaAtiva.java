package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.ItemInscricaoDividaAtiva;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.TipoCadastroTributario;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

public class FiltroPesquisaDividaAtiva implements Serializable {
    private String cadastroInicial, cadastroFinal;
    private TipoCadastroTributario tipoCadastro;
    private List<Divida> dividas;
    private Long exericioInicial, exercicioFinal;
    private Date datatInicio, dataFim;
    private Long termoInicial, termoFinal;
    private Long livro;
    private Pessoa pessoa;
    private ItemInscricaoDividaAtiva.Situacao situacao;
    private Integer maxResults;
    private Integer totalRegistros;
    private Boolean ajuizada;

    public FiltroPesquisaDividaAtiva() {
        totalRegistros = 0;
        dividas = Lists.newArrayList();
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public List<Divida> getDividas() {
        return dividas;
    }

    public void setDividas(List<Divida> dividas) {
        this.dividas = dividas;
    }

    public Long getExericioInicial() {
        return exericioInicial;
    }

    public void setExericioInicial(Long exericioInicial) {
        this.exericioInicial = exericioInicial;
    }

    public Long getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Long exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Date getDatatInicio() {
        return datatInicio;
    }

    public void setDatatInicio(Date datatInicio) {
        this.datatInicio = datatInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Long getTermoInicial() {
        return termoInicial;
    }

    public void setTermoInicial(Long termoInicial) {
        this.termoInicial = termoInicial;
    }

    public Long getTermoFinal() {
        return termoFinal;
    }

    public void setTermoFinal(Long termoFinal) {
        this.termoFinal = termoFinal;
    }

    public Long getLivro() {
        return livro;
    }

    public void setLivro(Long livro) {
        this.livro = livro;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ItemInscricaoDividaAtiva.Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(ItemInscricaoDividaAtiva.Situacao situacao) {
        this.situacao = situacao;
    }

    public Integer getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public Boolean getAjuizada() {
        return ajuizada != null ? ajuizada : false;
    }

    public void setAjuizada(Boolean ajuizada) {
        this.ajuizada = ajuizada;
    }

    public Integer getTotalPaginas() {
        try {
            BigDecimal totalPaginas = BigDecimal.valueOf(getTotalRegistros()).divide(new BigDecimal(getMaxResults()), RoundingMode.UP);
            return totalPaginas.intValue();
        } catch (Exception e) {
            return 0;
        }
    }
}
