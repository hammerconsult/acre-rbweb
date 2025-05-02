package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.Servico;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoRelatorioApresentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;

public class FiltroRelatorioServicosDeclaradosDTO {

    private String numero;
    private Date dataPagamentoInicial;
    private Date dataPagamentoFinal;
    private Date dataInicial;
    private Date dataFinal;
    private String prestador;
    private String tomador;
    private String cpfCnpjInicialPrestador;
    private String cpfCnpjFinalPrestador;
    private String cpfCnpjInicialTomador;
    private String cpfCnpjFinalTomador;
    private Exigibilidade exigibilidade;
    private SituacaoNota situacao;
    private List<SituacaoNota> situacoes;
    private TipoRelatorioApresentacao tipoRelatorioApresentacao;
    private Boolean somenteTotalizador;
    private TipoAgrupamento tipoAgrupamento;
    private Servico servico;
    private List<Servico> servicos;
    private UsuarioSistema usuarioSistema;

    public FiltroRelatorioServicosDeclaradosDTO() {
        situacoes = Lists.newArrayList();
        servicos = Lists.newArrayList();
        tipoRelatorioApresentacao = TipoRelatorioApresentacao.RESUMIDO;
        tipoAgrupamento = TipoAgrupamento.NATUREZA_OPERACAO;
    }

    public void validarOutrosCampos(ValidacaoException operacaoNaoPermitidaException) {
        if (tipoRelatorioApresentacao == null)
            operacaoNaoPermitidaException.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Apresentação é obrigatório.");

        if (tipoAgrupamento == null)
            operacaoNaoPermitidaException.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Agrupamento é obrigatório.");
    }

    public void validarCamposPorPeriodo() throws ValidacaoException {
        ValidacaoException operacaoNaoPermitidaException = new ValidacaoException();
        if (dataInicial == null)
            operacaoNaoPermitidaException.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial é obrigatório.");

        if (dataFinal == null)
            operacaoNaoPermitidaException.adicionarMensagemDeCampoObrigatorio("O campo Data Final é obrigatório.");

        if (dataInicial != null && dataFinal != null && dataInicial.after(dataFinal))
            operacaoNaoPermitidaException.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");

        validarOutrosCampos(operacaoNaoPermitidaException);

        operacaoNaoPermitidaException.lancarException();
    }

    public String montarDescricaoFiltros() {
        String retorno = "";
        if (dataInicial != null) {
            retorno += " Data Inicial: " + DataUtil.getDataFormatada(dataInicial) + "; ";
        }

        if (dataFinal != null) {
            retorno += " Data Final: " + DataUtil.getDataFormatada(dataFinal) + "; ";
        }

        if (dataPagamentoInicial != null) {
            retorno += " Data de Pagamento Inicial: " + DataUtil.getDataFormatada(dataPagamentoInicial) + "; ";
        }
        if (dataPagamentoFinal != null) {
            retorno += " Data de Pagamento Final: " + DataUtil.getDataFormatada(dataPagamentoFinal) + "; ";
        }

        if (numero != null && !numero.isEmpty()) {
            retorno += " Número: " + numero.trim() + "; ";
        }

        if (!Strings.isNullOrEmpty(cpfCnpjInicialPrestador)) {
            retorno += " CPF/CNPJ Inicial (Prestador): " + cpfCnpjInicialPrestador + "; ";
        }

        if (!Strings.isNullOrEmpty(cpfCnpjFinalPrestador)) {
            retorno += " CPF/CNPJ Final (Prestador): " + cpfCnpjFinalPrestador + "; ";
        }

        if (!Strings.isNullOrEmpty(prestador)) {
            retorno += " Prestador: " + prestador + "; ";
        }

        if (!Strings.isNullOrEmpty(cpfCnpjInicialTomador)) {
            retorno += " CPF/CNPJ Inicial (Tomador): " + cpfCnpjInicialTomador + "; ";
        }

        if (!Strings.isNullOrEmpty(cpfCnpjFinalTomador)) {
            retorno += " CPF/CNPJ Final (Tomador): " + cpfCnpjFinalTomador + "; ";
        }

        if (!Strings.isNullOrEmpty(tomador)) {
            retorno += " Tomador: " + tomador + "; ";
        }

        if (exigibilidade != null) {
            retorno += " Natureza de Operação: " + exigibilidade.getDescricao() + "; ";
        }

        if (situacoes != null && !situacoes.isEmpty()) {
            retorno += " Situações: " + StringUtils.join(situacoes, ", ") + "; ";
        }
        if (servicos != null && !servicos.isEmpty()) {
            retorno += " Serviços(s): " + StringUtils.join(servicos, ", ") + "; ";
        }

        return retorno;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataPagamentoInicial() {
        return dataPagamentoInicial;
    }

    public void setDataPagamentoInicial(Date dataPagamentoInicial) {
        this.dataPagamentoInicial = dataPagamentoInicial;
    }

    public Date getDataPagamentoFinal() {
        return dataPagamentoFinal;
    }

    public void setDataPagamentoFinal(Date dataPagamentoFinal) {
        this.dataPagamentoFinal = dataPagamentoFinal;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCpfCnpjInicialPrestador() {
        return cpfCnpjInicialPrestador;
    }

    public void setCpfCnpjInicialPrestador(String cpfCnpjInicialPrestador) {
        this.cpfCnpjInicialPrestador = cpfCnpjInicialPrestador;
    }

    public String getCpfCnpjFinalPrestador() {
        return cpfCnpjFinalPrestador;
    }

    public void setCpfCnpjFinalPrestador(String cpfCnpjFinalPrestador) {
        this.cpfCnpjFinalPrestador = cpfCnpjFinalPrestador;
    }

    public String getCpfCnpjInicialTomador() {
        return cpfCnpjInicialTomador;
    }

    public void setCpfCnpjInicialTomador(String cpfCnpjInicialTomador) {
        this.cpfCnpjInicialTomador = cpfCnpjInicialTomador;
    }

    public String getCpfCnpjFinalTomador() {
        return cpfCnpjFinalTomador;
    }

    public void setCpfCnpjFinalTomador(String cpfCnpjFinalTomador) {
        this.cpfCnpjFinalTomador = cpfCnpjFinalTomador;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Exigibilidade getExigibilidade() {
        return exigibilidade;
    }

    public void setExigibilidade(Exigibilidade exigibilidade) {
        this.exigibilidade = exigibilidade;
    }

    public SituacaoNota getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoNota situacao) {
        this.situacao = situacao;
    }

    public List<SituacaoNota> getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(List<SituacaoNota> situacoes) {
        this.situacoes = situacoes;
    }

    public TipoRelatorioApresentacao getTipoRelatorioApresentacao() {
        return tipoRelatorioApresentacao;
    }

    public void setTipoRelatorioApresentacao(TipoRelatorioApresentacao tipoRelatorioApresentacao) {
        this.tipoRelatorioApresentacao = tipoRelatorioApresentacao;
    }

    public TipoAgrupamento getTipoAgrupamento() {
        return tipoAgrupamento;
    }

    public void setTipoAgrupamento(TipoAgrupamento tipoAgrupamento) {
        this.tipoAgrupamento = tipoAgrupamento;
    }

    public Boolean getSomenteTotalizador() {
        return somenteTotalizador;
    }

    public void setSomenteTotalizador(Boolean somenteTotalizador) {
        this.somenteTotalizador = somenteTotalizador;
    }

    public enum TipoAgrupamento {
        NATUREZA_OPERACAO("Por Natureza da Operação"),
        COMPETENCIA_NATUREZA_OPERACAO("Por Competência e Natureza de Operação"),
        SERVICO_NATUREZA_OPERACAO("Por Serviço e Natureza de Operação");

        private String descricao;

        TipoAgrupamento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public boolean hasSituacao(SituacaoNota situacaoNota) {
        if (situacoes != null && !situacoes.isEmpty()) {
            for (SituacaoNota s : situacoes) {
                if (s.equals(situacaoNota))
                    return true;
            }
        }
        return false;
    }

    public void addSituacao() {
        Util.adicionarObjetoEmLista(situacoes, situacao);
        situacao = null;
    }

    public List<Long> getIdsServicos() {
        List<Long> ids = Lists.newArrayList();
        for (Servico s : servicos) {
            ids.add(s.getId());
        }
        return ids;
    }

    public void addServico() {
        Util.adicionarObjetoEmLista(servicos, servico);
        servico = null;
    }

    public void removeServico(Servico servico) {
        servicos.remove(servico);
    }

    public String getPrestador() {
        return prestador;
    }

    public void setPrestador(String prestador) {
        this.prestador = prestador;
    }

    public String getTomador() {
        return tomador;
    }

    public void setTomador(String tomador) {
        this.tomador = tomador;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
    }
}
