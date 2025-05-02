package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.NFSAvulsa;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/08/15
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoNotaFiscal extends AbstractFiltroRelacaoLancamentoDebito {

    private TipoCadastroTributario tipoCadastroTributarioTomador;
    private String cmcInicialTomador;
    private String cmcFinalTomador;
    private TipoCadastroTributario tipoCadastroTributarioPrestador;
    private String cmcInicialPrestador;
    private String cmcFinalPrestador;
    private NFSAvulsa.Situacao situacaoNota;
    private UsuarioSistema usuarioNota;
    private UsuarioSistema usuarioDAM;
    private String numeroNotaFiscalInicial;
    private String numeroNotaFiscalFinal;
    private Date dataEmissaoNotaInicial;
    private Date dataEmissaoNotaFinal;
    private BigDecimal totalNotaInicial;
    private BigDecimal totalNotaFinal;
    private BigDecimal totalIssInicial;
    private BigDecimal totalIssFinal;
    private Pessoa tomador;
    private List<Pessoa> tomadores;
    private Pessoa prestador;
    private List<Pessoa> prestadores;

    public FiltroRelacaoLancamentoNotaFiscal() {
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
    }

    public TipoCadastroTributario getTipoCadastroTributarioTomador() {
        return tipoCadastroTributarioTomador;
    }

    public void setTipoCadastroTributarioTomador(TipoCadastroTributario tipoCadastroTributarioTomador) {
        this.tipoCadastroTributarioTomador = tipoCadastroTributarioTomador;
    }

    public String getCmcInicialTomador() {
        return cmcInicialTomador;
    }

    public void setCmcInicialTomador(String cmcInicialTomador) {
        this.cmcInicialTomador = cmcInicialTomador;
    }

    public String getCmcFinalTomador() {
        return cmcFinalTomador;
    }

    public void setCmcFinalTomador(String cmcFinalTomador) {
        this.cmcFinalTomador = cmcFinalTomador;
    }

    public TipoCadastroTributario getTipoCadastroTributarioPrestador() {
        return tipoCadastroTributarioPrestador;
    }

    public void setTipoCadastroTributarioPrestador(TipoCadastroTributario tipoCadastroTributarioPrestador) {
        this.tipoCadastroTributarioPrestador = tipoCadastroTributarioPrestador;
    }

    public String getCmcInicialPrestador() {
        return cmcInicialPrestador;
    }

    public void setCmcInicialPrestador(String cmcInicialPrestador) {
        this.cmcInicialPrestador = cmcInicialPrestador;
    }

    public String getCmcFinalPrestador() {
        return cmcFinalPrestador;
    }

    public void setCmcFinalPrestador(String cmcFinalPrestador) {
        this.cmcFinalPrestador = cmcFinalPrestador;
    }

    public NFSAvulsa.Situacao getSituacaoNota() {
        return situacaoNota;
    }

    public void setSituacaoNota(NFSAvulsa.Situacao situacaoNota) {
        this.situacaoNota = situacaoNota;
    }

    public UsuarioSistema getUsuarioNota() {
        return usuarioNota;
    }

    public void setUsuarioNota(UsuarioSistema usuarioNota) {
        this.usuarioNota = usuarioNota;
    }

    public UsuarioSistema getUsuarioDAM() {
        return usuarioDAM;
    }

    public void setUsuarioDAM(UsuarioSistema usuarioDAM) {
        this.usuarioDAM = usuarioDAM;
    }

    public String getNumeroNotaFiscalInicial() {
        return numeroNotaFiscalInicial;
    }

    public void setNumeroNotaFiscalInicial(String numeroNotaFiscalInicial) {
        this.numeroNotaFiscalInicial = numeroNotaFiscalInicial;
    }

    public String getNumeroNotaFiscalFinal() {
        return numeroNotaFiscalFinal;
    }

    public void setNumeroNotaFiscalFinal(String numeroNotaFiscalFinal) {
        this.numeroNotaFiscalFinal = numeroNotaFiscalFinal;
    }

    public Date getDataEmissaoNotaInicial() {
        return dataEmissaoNotaInicial;
    }

    public void setDataEmissaoNotaInicial(Date dataEmissaoNotaInicial) {
        this.dataEmissaoNotaInicial = dataEmissaoNotaInicial;
    }

    public Date getDataEmissaoNotaFinal() {
        return dataEmissaoNotaFinal;
    }

    public void setDataEmissaoNotaFinal(Date dataEmissaoNotaFinal) {
        this.dataEmissaoNotaFinal = dataEmissaoNotaFinal;
    }

    public BigDecimal getTotalNotaInicial() {
        return totalNotaInicial;
    }

    public void setTotalNotaInicial(BigDecimal totalNotaInicial) {
        this.totalNotaInicial = totalNotaInicial;
    }

    public BigDecimal getTotalNotaFinal() {
        return totalNotaFinal;
    }

    public void setTotalNotaFinal(BigDecimal totalNotaFinal) {
        this.totalNotaFinal = totalNotaFinal;
    }

    public BigDecimal getTotalIssInicial() {
        return totalIssInicial;
    }

    public void setTotalIssInicial(BigDecimal totalIssInicial) {
        this.totalIssInicial = totalIssInicial;
    }

    public BigDecimal getTotalIssFinal() {
        return totalIssFinal;
    }

    public void setTotalIssFinal(BigDecimal totalIssFinal) {
        this.totalIssFinal = totalIssFinal;
    }

    public Pessoa getTomador() {
        return tomador;
    }

    public void setTomador(Pessoa tomador) {
        this.tomador = tomador;
    }

    public List<Pessoa> getTomadores() {
        return tomadores;
    }

    public void setTomadores(List<Pessoa> tomadores) {
        this.tomadores = tomadores;
    }

    public Pessoa getPrestador() {
        return prestador;
    }

    public void setPrestador(Pessoa prestador) {
        this.prestador = prestador;
    }

    public List<Pessoa> getPrestadores() {
        return prestadores;
    }

    public void setPrestadores(List<Pessoa> prestadores) {
        this.prestadores = prestadores;
    }

    public void addTomador() {
        if (this.getTomador() == null) {
            FacesUtil.addCampoObrigatorio("Por favor selecione um tomador!");
            return;
        }
        if (this.getTomadores() != null && this.getTomadores().contains(this.getTomador())) {
            FacesUtil.addCampoObrigatorio("Tomador já foi adicionado a pesquisa!");
            return;
        }
        if (this.getTomadores() == null) {
            this.setTomadores(Lists.<Pessoa>newArrayList());
        }
        this.getTomadores().add(tomador);
        this.setTomador(null);
    }

    public void delTomador(Pessoa pessoa) {
        tomadores.remove(pessoa);
    }

    public void addPrestador() {
        if (this.getPrestador() == null) {
            FacesUtil.addCampoObrigatorio("Por favor selecione um prestador!");
            return;
        }
        if (this.getPrestadores() != null && this.getPrestadores().contains(this.getPrestador())) {
            FacesUtil.addCampoObrigatorio("Prestador já foi adicionado a pesquisa!");
            return;
        }
        if (this.getPrestadores() == null) {
            this.setPrestadores(Lists.<Pessoa>newArrayList());
        }
        this.getPrestadores().add(prestador);
        this.setPrestador(null);
    }

    public void delPrestador(Pessoa pessoa) {
        prestadores.remove(pessoa);
    }

    public List<SelectItem> situacoesPagamento() {
        List<SelectItem> situacoes = Lists.newArrayList();
        situacoes.add(new SelectItem(null, "      "));
        for (SituacaoParcela situacaoParcela : SituacaoParcela.values()) {
            situacoes.add(new SelectItem(situacaoParcela, situacaoParcela.getDescricao()));
        }
        return situacoes;
    }

    public List<SelectItem> situacoesNota() {
        return Util.getListSelectItem(NFSAvulsa.Situacao.values());
    }

    public List<SelectItem> tiposCadastro() {
        List<SelectItem> tipos = Lists.newArrayList();
        tipos.add(new SelectItem(null, "      "));
        tipos.add(new SelectItem(TipoCadastroTributario.ECONOMICO, TipoCadastroTributario.ECONOMICO.getDescricao()));
        tipos.add(new SelectItem(TipoCadastroTributario.PESSOA, TipoCadastroTributario.PESSOA.getDescricao()));
        return tipos;
    }

    public void processaSelecaoTipoCadastroTomador() {
        cmcInicialTomador = "";
        cmcFinalTomador = "";
        tomador = null;
        tomadores = null;
    }

    public void processaSelecaoTipoCadastroPrestador() {
        cmcInicialPrestador = "";
        cmcFinalPrestador = "";
        prestador = null;
        prestadores = null;
    }
}
