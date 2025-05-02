package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.TributoTaxaDividasDiversas;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.SituacaoCalculo;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;

import java.util.Date;

public class FiltroRelacaoTaxasDiversas {

    private Date dataLancamentoInicial;
    private Date dataLancamentoFinal;
    private Date dataVencimentoInicial;
    private Date dataVencimentoFinal;
    private TipoCadastroTributario tipoCadastroTributario;
    private String cadastroInicial;
    private String cadastroFinal;
    private String nomeInicial;
    private String nomeFinal;
    private SituacaoCalculo situacaoCalculo;
    private DAM.Situacao situacaoDAM;
    private UsuarioSistema usuarioTaxa;
    private UsuarioSistema usuarioDAM;
    private String numeroTaxaInicial;
    private String numeroTaxaFinal;
    private StringBuilder filtro;
    private TributoTaxaDividasDiversas tributoTaxaDividasDiversas;

    public FiltroRelacaoTaxasDiversas() {
    }

    public void validarInformacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (dataLancamentoInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data Inicial do Lançamento!");
        }
        if (dataLancamentoFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data Final do Lançamento!");
        }
        ve.lancarException();
    }


    public void montarDescricaoFiltro() {
        filtro = new StringBuilder();

        if (this.getDataLancamentoInicial() != null) {
            filtro.append(" Data de Lançamento Inicial: ");
            filtro.append(Util.dateToString(this.getDataLancamentoInicial()));
        }

        if (this.getDataLancamentoFinal() != null) {
            filtro.append(" Data de Lançamento Final: ");
            filtro.append(Util.dateToString(this.getDataLancamentoFinal()));
        }

        if (this.getDataVencimentoInicial() != null) {
            filtro.append(" Data de Vencimento Inicial: ");
            filtro.append(Util.dateToString(this.getDataVencimentoInicial()));
        }

        if (this.getDataVencimentoFinal() != null) {
            filtro.append(" Data de Vencimento Final: ");
            filtro.append(Util.dateToString(this.getDataVencimentoFinal()));
        }

        if (this.getTipoCadastroTributario() != null) {
            filtro.append(" Tipo de Cadastro: ");
            filtro.append(this.getTipoCadastroTributario().getDescricao());

            if (this.getCadastroInicial() != null && !this.getCadastroInicial().trim().isEmpty()) {
                filtro.append(" Cadastro Inicial: ");
                filtro.append(this.getCadastroInicial().trim());
            }
            if (this.getCadastroFinal() != null && !this.getCadastroFinal().trim().isEmpty()) {
                filtro.append(" Cadastro Final: ");
                filtro.append(this.getCadastroFinal().trim());
            }
        }

        if (this.getTributoTaxaDividasDiversas() != null) {
            filtro.append(" Tipo de Taxa Diversas: ");
            filtro.append(this.getTributoTaxaDividasDiversas().getTributo().getDescricao());
        }

        if (this.getNomeInicial() != null && !this.getNomeInicial().trim().isEmpty()) {
            filtro.append(" Nome Requerente Inicial: ");
            filtro.append(this.getNomeInicial().trim());
        }

        if (this.getNomeFinal() != null && !this.getNomeFinal().trim().isEmpty()) {
            filtro.append(" Nome Requerente Final: ");
            filtro.append(this.getNomeFinal().trim());
        }

        if (this.getSituacaoCalculo() != null) {
            filtro.append(" Situação de Emissão da Taxa Diversa: ");
            filtro.append(this.getSituacaoCalculo().getDescricao());
        }

        if (this.getSituacaoDAM() != null) {
            filtro.append(" Situação de Pagamento da Taxa Diversa: ");
            filtro.append(this.getSituacaoDAM().getDescricao());
        }

        if (this.getUsuarioTaxa() != null && this.getUsuarioTaxa().getId() != null) {
            filtro.append(" Usuário de Emissão da Taxa Diversa: ");
            filtro.append(this.getUsuarioTaxa());
        }

        if (this.getUsuarioDAM() != null) {
            filtro.append(" Usuário de Emissão do DAM: ");
            filtro.append(this.getUsuarioDAM());
        }

        if (this.getNumeroTaxaInicial() != null && !this.getNumeroTaxaInicial().trim().isEmpty()) {
            filtro.append(" Número da Taxa Inicial: ");
            filtro.append(this.getNumeroTaxaInicial());
        }

        if (this.getNumeroTaxaFinal() != null && !this.getNumeroTaxaFinal().trim().isEmpty()) {
            filtro.append(" Número da Taxa Final: ");
            filtro.append(this.getNumeroTaxaFinal());
        }
    }

    public String getNumeroTaxaInicial() {
        return numeroTaxaInicial;
    }

    public void setNumeroTaxaInicial(String numeroTaxaInicial) {
        this.numeroTaxaInicial = numeroTaxaInicial;
    }

    public String getNumeroTaxaFinal() {
        return numeroTaxaFinal;
    }

    public void setNumeroTaxaFinal(String numeroTaxaFinal) {
        this.numeroTaxaFinal = numeroTaxaFinal;
    }

    public StringBuilder getFiltro() {
        return filtro;
    }

    public Date getDataLancamentoInicial() {
        return dataLancamentoInicial;
    }

    public void setDataLancamentoInicial(Date dataLancamentoInicial) {
        this.dataLancamentoInicial = dataLancamentoInicial;
    }

    public Date getDataLancamentoFinal() {
        return dataLancamentoFinal;
    }

    public void setDataLancamentoFinal(Date dataLancamentoFinal) {
        this.dataLancamentoFinal = dataLancamentoFinal;
    }

    public Date getDataVencimentoInicial() {
        return dataVencimentoInicial;
    }

    public void setDataVencimentoInicial(Date dataVencimentoInicial) {
        this.dataVencimentoInicial = dataVencimentoInicial;
    }

    public Date getDataVencimentoFinal() {
        return dataVencimentoFinal;
    }

    public void setDataVencimentoFinal(Date dataVencimentoFinal) {
        this.dataVencimentoFinal = dataVencimentoFinal;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public TributoTaxaDividasDiversas getTributoTaxaDividasDiversas() {
        return tributoTaxaDividasDiversas;
    }

    public void setTributoTaxaDividasDiversas(TributoTaxaDividasDiversas tributoTaxaDividasDiversas) {
        this.tributoTaxaDividasDiversas = tributoTaxaDividasDiversas;
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

    public String getNomeInicial() {
        return nomeInicial;
    }

    public void setNomeInicial(String nomeInicial) {
        this.nomeInicial = nomeInicial;
    }

    public String getNomeFinal() {
        return nomeFinal;
    }

    public void setNomeFinal(String nomeFinal) {
        this.nomeFinal = nomeFinal;
    }

    public SituacaoCalculo getSituacaoCalculo() {
        return situacaoCalculo;
    }

    public void setSituacaoCalculo(SituacaoCalculo situacaoCalculo) {
        this.situacaoCalculo = situacaoCalculo;
    }

    public DAM.Situacao getSituacaoDAM() {
        return situacaoDAM;
    }

    public void setSituacaoDAM(DAM.Situacao situacaoDAM) {
        this.situacaoDAM = situacaoDAM;
    }

    public UsuarioSistema getUsuarioTaxa() {
        return usuarioTaxa;
    }

    public void setUsuarioTaxa(UsuarioSistema usuarioTaxa) {
        this.usuarioTaxa = usuarioTaxa;
    }

    public UsuarioSistema getUsuarioDAM() {
        return usuarioDAM;
    }

    public void setUsuarioDAM(UsuarioSistema usuarioDAM) {
        this.usuarioDAM = usuarioDAM;
    }

}
