package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.Ordenacao;
import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FiltroMaioresDevedores implements Comparable<FiltroMaioresDevedores> {
    private static final Logger log = LoggerFactory.getLogger(FiltroMaioresDevedores.class);
    private Date dataRelatorio;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private Integer exercicioInicial;
    private Integer exercicioFinal;
    private Integer qtdeDevedores;
    private TipoPessoa tipoPessoa;
    private List<Pessoa> pessoas;
    private Ordenacao ordenacao;
    private List<Divida> listaDividas;
    private Boolean parcelamento;
    private Boolean valoresAtualizados;
    private Boolean detalhado;
    private Boolean cadastroImobiliario;
    private UsuarioSistema usuarioSistema;
    private Boolean tipoDebitoExercicio;
    private Boolean tipoDebitoDividaAtiva;
    private Boolean tipoDebitoDividaAtivaAjuizada;
    private Boolean situacaoCadastro;

    private Boolean tipoDebitoDividaAtivaProtestada;

    public FiltroMaioresDevedores(UsuarioSistema usuarioSistema) {
        listaDividas = Lists.newArrayList();
        pessoas = Lists.newArrayList();
        ordenacao = Ordenacao.DECRESCENTE;
        qtdeDevedores = 20;
        tipoPessoa = null;
        parcelamento = false;
        valoresAtualizados = true;
        detalhado = false;
        dataRelatorio = new Date();
        cadastroImobiliario = false;
        tipoDebitoExercicio = false;
        tipoDebitoDividaAtiva = false;
        tipoDebitoDividaAtivaAjuizada = false;
        tipoDebitoDividaAtivaProtestada = false;
        inscricaoInicial = "100100000000000";
        inscricaoFinal = "100199999999999";
        this.usuarioSistema = usuarioSistema;
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

    public Date getDataRelatorio() {
        return dataRelatorio;
    }

    public void setDataRelatorio(Date dataRelatorio) {
        this.dataRelatorio = dataRelatorio;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataRelatorioSemHora() {
        return Util.getDataHoraMinutoSegundoZerado(dataRelatorio);
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Integer getQtdeDevedores() {
        return qtdeDevedores;
    }

    public void setQtdeDevedores(Integer qtdeDevedores) {
        this.qtdeDevedores = qtdeDevedores;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public String getTipoDebitos() {
        String retorno = "";
        String juncao = "";
        if (getTipoDebitoExercicio()) {
            retorno += "Exercício";
            juncao = ", ";
        }
        if (getTipoDebitoDividaAtiva()) {
            retorno += juncao + "Dívida Ativa";
            juncao = ", ";
        }
        if (getTipoDebitoDividaAtivaAjuizada()) {
            retorno += juncao + "Dívida Ativa Ajuizada";
            juncao = ", ";
        }
        if ("".equals(juncao)) {
            retorno += juncao + "TODOS";
        }
        return retorno;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public Ordenacao getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(Ordenacao ordenacao) {
        this.ordenacao = ordenacao;
    }

    public List<Divida> getListaDividas() {
        return listaDividas;
    }

    public void setListaDividas(List<Divida> listaDividas) {
        this.listaDividas = listaDividas;
    }

    public List<Long> getListaIdDividas() {
        List<Long> retorno = Lists.newArrayList();
        if (listaDividas != null) {
            for (Divida divida : listaDividas) {
                retorno.add(divida.getId());
            }
        }
        return retorno;
    }

    public List<Long> getListaIdPessoas() {
        List<Long> retorno = Lists.newArrayList();
        if (pessoas != null) {
            for (Pessoa pessoa : pessoas) {
                retorno.add(pessoa.getId());
            }
        }
        return retorno;
    }

    public Boolean getParcelamento() {
        return parcelamento;
    }

    public void setParcelamento(Boolean parcelamento) {
        this.parcelamento = parcelamento;
    }

    public Boolean getValoresAtualizados() {
        return valoresAtualizados;
    }

    public void setValoresAtualizados(Boolean valoresAtualizados) {
        this.valoresAtualizados = valoresAtualizados;
    }

    public Boolean getDetalhado() {
        return detalhado;
    }

    public void setDetalhado(Boolean detalhado) {
        this.detalhado = detalhado;
    }

    public Boolean getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(Boolean cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }

    public Boolean getTipoDebitoExercicio() {
        return tipoDebitoExercicio;
    }

    public void setTipoDebitoExercicio(Boolean tipoDebitoExercicio) {
        this.tipoDebitoExercicio = tipoDebitoExercicio;
    }

    public Boolean getTipoDebitoDividaAtiva() {
        return tipoDebitoDividaAtiva;
    }

    public void setTipoDebitoDividaAtiva(Boolean tipoDebitoDividaAtiva) {
        this.tipoDebitoDividaAtiva = tipoDebitoDividaAtiva;
    }

    public Boolean getTipoDebitoDividaAtivaAjuizada() {
        return tipoDebitoDividaAtivaAjuizada;
    }

    public void setTipoDebitoDividaAtivaAjuizada(Boolean tipoDebitoDividaAtivaAjuizada) {
        this.tipoDebitoDividaAtivaAjuizada = tipoDebitoDividaAtivaAjuizada;
    }

    public Boolean getTipoDebitoDividaAtivaProtestada() {
        return tipoDebitoDividaAtivaProtestada;
    }

    public void setTipoDebitoDividaAtivaProtestada(Boolean tipoDebitoDividaAtivaProtestada) {
        this.tipoDebitoDividaAtivaProtestada = tipoDebitoDividaAtivaProtestada;
    }

    public Integer getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Integer exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Integer getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Integer exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Boolean getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(Boolean situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public String getFiltro() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String retorno = "Filtros: ";
        if (getVencimentoInicial() != null) {
            retorno += " Vencimento inicial: " + sdf.format(getVencimentoInicial());
        }
        if (getVencimentoInicial() != null) {
            retorno += " Vencimento final: " + sdf.format(getVencimentoFinal());
        }
        if (getQtdeDevedores() > 0) {
            retorno += " Quantidade de devedores: " + getQtdeDevedores();
        }
        retorno += " Pessoa(s): ";
        if (!pessoas.isEmpty()) {
            for (Pessoa pessoa : pessoas) {
                retorno += pessoa.getCpf_Cnpj() + ", ";
            }
        }
        if (getOrdenacao() != null) {
            retorno += " Ordenação: " + getOrdenacao().getDescricao();
        }
        retorno += " Dívida(s): ";
        if (!listaDividas.isEmpty()) {
            for (Divida dv : listaDividas) {
                retorno += dv.getDescricao() + ", ";
            }
        } else {
            retorno += "TODAS";
        }
        retorno += " Tipo de Débito: ";
        String juncao = ",";
        if (getTipoDebitoExercicio()) {
            retorno += "Exercício";
        }
        if (getTipoDebitoDividaAtiva()) {
            retorno += juncao + " Dívida Ativa";
        }
        if (getTipoDebitoDividaAtivaAjuizada()) {
            retorno += juncao + " Dívida Ativa Ajuizada";
        }
        if (getTipoDebitoDividaAtivaProtestada()) {
            retorno += juncao + " Dívida Ativa Protestada";
        }
        retorno += juncao + " Situação dos Cadastros: ";
        if (getSituacaoCadastro() != null) {
            retorno += getSituacaoCadastro() ? "Ativos" : "Inativos";
        } else {
            retorno += "Ambos";
        }
        if (getDetalhado()) {
            retorno += " (Relatório Detalhado) ";
        } else {
            retorno += " (Relatório Resumido) ";
        }
        return retorno;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiltroMaioresDevedores)) return false;
        FiltroMaioresDevedores that = (FiltroMaioresDevedores) o;
        if (getDataRelatorioSemHora() != null ? !getDataRelatorioSemHora().equals(that.getDataRelatorioSemHora()) : that.getDataRelatorioSemHora() != null)
            return false;
        if (vencimentoInicial != null ? !vencimentoInicial.equals(that.vencimentoInicial) : that.vencimentoInicial != null)
            return false;
        if (vencimentoFinal != null ? !vencimentoFinal.equals(that.vencimentoFinal) : that.vencimentoFinal != null)
            return false;
        if (qtdeDevedores != null ? !qtdeDevedores.equals(that.qtdeDevedores) : that.qtdeDevedores != null)
            return false;
        if (tipoPessoa != that.tipoPessoa) return false;
        if (ordenacao != that.ordenacao) return false;
        if (tipoDebitoExercicio != null ? !tipoDebitoExercicio.equals(that.tipoDebitoExercicio) : that.tipoDebitoExercicio != null)
            return false;
        if (tipoDebitoDividaAtiva != null ? !tipoDebitoDividaAtiva.equals(that.tipoDebitoDividaAtiva) : that.tipoDebitoDividaAtiva != null)
            return false;
        if (tipoDebitoDividaAtivaAjuizada != null ? !tipoDebitoDividaAtivaAjuizada.equals(that.tipoDebitoDividaAtivaAjuizada) : that.tipoDebitoDividaAtivaAjuizada != null)
            return false;
        if (listaDividas != null ? !listaDividas.equals(that.listaDividas) : that.listaDividas != null) return false;
        if (parcelamento != null ? !parcelamento.equals(that.parcelamento) : that.parcelamento != null) return false;
        if (cadastroImobiliario != null ? !cadastroImobiliario.equals(that.cadastroImobiliario) : that.cadastroImobiliario != null)
            return false;
        return !(valoresAtualizados != null ? !valoresAtualizados.equals(that.valoresAtualizados) : that.valoresAtualizados != null);

    }

    @Override
    public int hashCode() {
        int result = getDataRelatorioSemHora() != null ? getDataRelatorioSemHora().hashCode() : 0;
        result = 31 * result + (vencimentoInicial != null ? vencimentoInicial.hashCode() : 0);
        result = 31 * result + (vencimentoFinal != null ? vencimentoFinal.hashCode() : 0);
        result = 31 * result + (qtdeDevedores != null ? qtdeDevedores.hashCode() : 0);
        result = 31 * result + (tipoPessoa != null ? tipoPessoa.hashCode() : 0);
        result = 31 * result + (ordenacao != null ? ordenacao.hashCode() : 0);
        result = 31 * result + (tipoDebitoExercicio != null ? tipoDebitoExercicio.hashCode() : 0);
        result = 31 * result + (tipoDebitoDividaAtiva != null ? tipoDebitoDividaAtiva.hashCode() : 0);
        result = 31 * result + (tipoDebitoDividaAtivaAjuizada != null ? tipoDebitoDividaAtivaAjuizada.hashCode() : 0);
        result = 31 * result + (listaDividas != null ? listaDividas.hashCode() : 0);
        result = 31 * result + (parcelamento != null ? parcelamento.hashCode() : 0);
        result = 31 * result + (cadastroImobiliario != null ? cadastroImobiliario.hashCode() : 0);
        result = 31 * result + (valoresAtualizados != null ? valoresAtualizados.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FiltroMaioresDevedores{" +
            "dataRelatorio=" + dataRelatorio +
            ", vencimentoInicial=" + vencimentoInicial +
            ", vencimentoFinal=" + vencimentoFinal +
            ", qtdeDevedores=" + qtdeDevedores +
            ", tipoPessoa=" + tipoPessoa +
            ", ordenacao=" + ordenacao +
            ", listaDividas=" + listaDividas +
            ", parcelamento=" + parcelamento +
            ", valoresAtualizados=" + valoresAtualizados +
            ", cadastroImobiliario=" + cadastroImobiliario +
            ", detalhado=" + detalhado +
            '}';
    }

    public void copiarValores(FiltroMaioresDevedores filtroSelecionado) {
        getListaDividas().clear();
        setVencimentoInicial(filtroSelecionado.getVencimentoInicial());
        setVencimentoFinal(filtroSelecionado.getVencimentoFinal());
        setQtdeDevedores(filtroSelecionado.getQtdeDevedores());
        setTipoPessoa(filtroSelecionado.getTipoPessoa());
        setOrdenacao(filtroSelecionado.getOrdenacao());
        setValoresAtualizados(filtroSelecionado.getValoresAtualizados());
        setPessoas(filtroSelecionado.getPessoas());
        setTipoDebitoExercicio(filtroSelecionado.getTipoDebitoExercicio());
        setTipoDebitoDividaAtiva(filtroSelecionado.getTipoDebitoDividaAtiva());
        setTipoDebitoDividaAtivaAjuizada(filtroSelecionado.getTipoDebitoDividaAtivaAjuizada());
        for (Divida div : filtroSelecionado.getListaDividas()) {
            getListaDividas().add(div);
        }
    }

    public String filtrarPorDividaAtivaOuAjuizada(Boolean exercicio, Boolean dividaAtiva, Boolean dividaAtivaAjuizada) {
        String retorno = "";
        if (exercicio && !dividaAtiva && !dividaAtivaAjuizada) {
            retorno += " and pvd.DIVIDAATIVA = 0 and pvd.DIVIDAATIVAAJUIZADA = 0 ";
        } else if (exercicio && dividaAtiva && !dividaAtivaAjuizada) {
            retorno += " and pvd.DIVIDAATIVAAJUIZADA = 0 ";
        } else if (exercicio && !dividaAtiva && dividaAtivaAjuizada) {
            retorno += " and pvd.DIVIDAATIVA = 0 ";
        } else if (!exercicio && dividaAtiva && dividaAtivaAjuizada) {
            retorno += " and pvd.DIVIDAATIVA = 1 and pvd.DIVIDAATIVAAJUIZADA in (0, 1) ";
        } else if (!exercicio && dividaAtiva && !dividaAtivaAjuizada) {
            retorno += " and pvd.DIVIDAATIVA = 1 and pvd.DIVIDAATIVAAJUIZADA = 0 ";
        } else if (!exercicio && !dividaAtiva && dividaAtivaAjuizada) {
            retorno += " and pvd.DIVIDAATIVAAJUIZADA = 1 ";
        }
        return retorno;
    }

    @Override
    public int compareTo(FiltroMaioresDevedores o) {
        return o.getDataRelatorio().compareTo(this.dataRelatorio);
    }
}
