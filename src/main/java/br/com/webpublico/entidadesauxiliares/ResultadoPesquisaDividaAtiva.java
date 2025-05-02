package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;


public class ResultadoPesquisaDividaAtiva {
    private String cadastro;
    private String divida;
    private String nomeRazaoSocial;
    private String cpfCnpj;
    private Integer exercicio;
    private Date dataInscricao;
    private String termo;
    private Integer livro;
    private Integer pagina;
    private Integer linha;
    private BigDecimal valor;
    private Long itemInscricaoId;
    private Boolean ajuizada;

    public ResultadoPesquisaDividaAtiva() {
    }

    public String getCadastro() {
        return cadastro;
    }

    public void setCadastro(String cadastro) {
        this.cadastro = cadastro;
    }

    public String getDivida() {
        return divida;
    }

    public void setDivida(String divida) {
        this.divida = divida;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(BigDecimal exercicio) {
        if (exercicio != null) {
            this.exercicio = exercicio.intValue();
        }
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(Date dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public String getTermo() {
        return termo;
    }

    public void setTermo(BigDecimal termo) {
        if (termo != null) {
            this.termo = termo.toString();
        }
    }

    public Integer getLivro() {
        return livro;
    }

    public void setLivro(BigDecimal livro) {
        if (livro != null) {
            this.livro = livro.intValue();
        }
    }

    public Integer getPagina() {
        return pagina;
    }

    public void setPagina(BigDecimal pagina) {
        if (pagina != null) {
            this.pagina = pagina.intValue();
        }
    }

    public Integer getLinha() {
        return linha;
    }

    public void setLinha(BigDecimal linha) {
        if (linha != null) {
            this.linha = linha.intValue();
        }
    }

    public Long getItemInscricaoId() {
        return itemInscricaoId;
    }

    public void setItemInscricaoId(BigDecimal itemInscricaoId) {
        if (itemInscricaoId != null) {
            this.itemInscricaoId = itemInscricaoId.longValue();
        }
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean getAjuizada() {
        return ajuizada;
    }

    public void setAjuizada(Boolean ajuizada) {
        this.ajuizada = ajuizada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResultadoPesquisaDividaAtiva that = (ResultadoPesquisaDividaAtiva) o;

        return itemInscricaoId.equals(that.itemInscricaoId);

    }

    @Override
    public int hashCode() {
        return itemInscricaoId.hashCode();
    }
}
