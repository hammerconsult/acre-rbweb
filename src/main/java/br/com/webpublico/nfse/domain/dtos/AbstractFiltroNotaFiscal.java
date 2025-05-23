package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.enums.SituacaoLoteRps;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.nfse.enums.TipoDocumentoNfse;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.WebReportUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractFiltroNotaFiscal {

    private String numero;
    private Date dataInicial;
    private Date dataFinal;
    private Mes mesInicial;
    private Exercicio exercicioInicial;
    private Mes mesFinal;
    private Exercicio exercicioFinal;
    private TipoPessoa tipoContribuinte;
    private Pessoa contribuinte;
    private String cnpjInicial;
    private String cnpjFinal;
    private CadastroEconomico cadastroEconomico;
    private ClassificacaoAtividade classificacaoAtividade;
    private List<ClassificacaoAtividade> classificacoesAtividade;
    private Servico servico;
    private List<Servico> servicos;
    private SituacaoLoteRps situacaoLoteRps;
    private String numeroLote;
    private Exigibilidade exigibilidade;
    private List<Exigibilidade> exigibilidades;
    private Boolean foraPais;
    private SituacaoNota situacao;
    private List<SituacaoNota> situacoes;
    private BigDecimal valorTotalInicial;
    private BigDecimal valorTotalFinal;
    private TipoRelatorioApresentacao tipoRelatorioApresentacao;
    private Boolean somenteTotalizador;
    private String cpfCnpjTomadorInicial;
    private String cpfCnpjTomadorFinal;
    private TipoDocumentoNfse tipoDocumento;
    private TipoIssqn tipoIssqn;
    private List<TipoIssqn> tiposIssqn;
    private SituacaoCadastralCadastroEconomico situacaoCadastral;
    private List<SituacaoCadastralCadastroEconomico> situacoesCadastral;
    private TipoPorte tipoPorte;
    private List<TipoPorte> tiposPorte;

    public AbstractFiltroNotaFiscal() {
        classificacoesAtividade = Lists.newArrayList();
        servicos = Lists.newArrayList();
        situacoes = Lists.newArrayList();
        exigibilidades = Lists.newArrayList();
        tiposIssqn = Lists.newArrayList();
        situacoesCadastral = Lists.newArrayList();
        tiposPorte = Lists.newArrayList();
    }

    public void validarCamposPorEmissao() throws ValidacaoException {
        ValidacaoException operacaoNaoPermitidaException = new ValidacaoException();
        if (dataInicial == null)
            operacaoNaoPermitidaException.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial é obrigatório.");

        if (dataFinal == null)
            operacaoNaoPermitidaException.adicionarMensagemDeCampoObrigatorio("O campo Data Final é obrigatório.");

        if (dataInicial != null && dataFinal != null && dataInicial.after(dataFinal))
            operacaoNaoPermitidaException.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");

        operacaoNaoPermitidaException.lancarException();
    }

    public void validarCamposPorCompetencia() throws ValidacaoException {
        ValidacaoException operacaoNaoPermitidaException = new ValidacaoException();

        if (exercicioInicial == null || mesInicial == null)
            operacaoNaoPermitidaException.adicionarMensagemDeCampoObrigatorio("O campo Competência Inicial é obrigatório.");

        if (exercicioFinal == null || mesFinal == null)
            operacaoNaoPermitidaException.adicionarMensagemDeCampoObrigatorio("O campo Competência Final é obrigatório.");

        operacaoNaoPermitidaException.lancarException();
    }

    public void validarCamposPorEmissaoOrCompetencia(ValidacaoException ve) throws ValidacaoException {
        if ((dataInicial == null || dataFinal == null) &&
            ((exercicioInicial == null || mesInicial == null) || (exercicioFinal == null || mesFinal == null))) {
            ve.adicionarMensagemDeCampoObrigatorio("A emissão ou a competência é obrigatório.");
        }
    }


    public String montarDescricaoFiltros() {
        StringBuilder retorno = new StringBuilder();
        if (exercicioInicial != null && mesInicial != null) {
            retorno.append(" Competência Inicial: ").append(mesInicial.getNumeroMes()).append("/").append(exercicioInicial.getAno()).append("; ");
        }
        if (exercicioFinal != null && mesFinal != null) {
            retorno.append(" Competência Final: ").append(mesFinal.getNumeroMes()).append("/").append(exercicioFinal.getAno()).append("; ");
        }
        if (dataInicial != null) {
            retorno.append(" Data Inicial: ").append(DataUtil.getDataFormatada(dataInicial)).append("; ");
        }
        if (dataFinal != null) {
            retorno.append(" Data Final: ").append(DataUtil.getDataFormatada(dataFinal)).append("; ");
        }
        if (numero != null && !numero.isEmpty()) {
            retorno.append(" Número: ").append(numero.trim()).append("; ");
        }
        if (Strings.isNotEmpty(cnpjInicial)) {
            retorno.append(" CNPJ Inicial: ").append(cnpjInicial).append("; ");
        }
        if (Strings.isNotEmpty(cnpjFinal)) {
            retorno.append(" CNPJ Final: ").append(cnpjFinal).append("; ");
        }
        if (tipoContribuinte != null) {
            retorno.append(" Tipo Tomador: ").append(tipoContribuinte.getDescricao()).append("; ");
        }
        if (contribuinte != null) {
            retorno.append(" Tomador: ").append(contribuinte).append("; ");
        }
        if (cadastroEconomico != null) {
            retorno.append(" Cadastro Econômico: ").append(cadastroEconomico).append("; ");
        }
        if (classificacoesAtividade != null && !classificacoesAtividade.isEmpty()) {
            retorno.append(" Classificação(ões) de Atividade: ").append(StringUtils.join(classificacoesAtividade, ", ")).append("; ");
        }
        if (tiposPorte != null && !tiposPorte.isEmpty()) {
            retorno.append(" Porte(s): ").append(StringUtils.join(tiposPorte, ", ")).append("; ");
        }
        if (servicos != null && !servicos.isEmpty()) {
            retorno.append(" Serviços(s): ");
            String juncao = "";
            for (Servico s : servicos) {
                retorno.append(juncao).append(s.getCodigo());
                juncao = ", ";
            }
            retorno.append("; ");
        }
        if (situacaoLoteRps != null) {
            retorno.append(" Situação Lote: ").append(situacaoLoteRps.getDescricao()).append("; ");
        }
        if (numeroLote != null && !numeroLote.isEmpty()) {
            retorno.append(" Número do Lote: ").append(numeroLote.trim()).append("; ");
        }
        if (exigibilidades != null && !exigibilidades.isEmpty()) {
            retorno.append(" Natureza(s) de Operação(ões): ").append(StringUtils.join(getDescricoesExigibilidades(), ", ")).append("; ");
        }
        if (foraPais != null) {
            retorno.append(" Fora do País: ").append(Util.converterBooleanSimOuNao(foraPais)).append("; ");
        }
        if (situacoes != null && !situacoes.isEmpty()) {
            retorno.append(" Situações: ").append(StringUtils.join(situacoes, ", ")).append("; ");
        }
        if (valorTotalInicial != null) {
            retorno.append(" Valor Total Inicial: ").append(Util.formataValor(valorTotalInicial)).append("; ");
        }
        if (valorTotalFinal != null) {
            retorno.append(" Valor Total Final: ").append(Util.formataValor(valorTotalFinal)).append("; ");
        }
        if (tipoDocumento != null) {
            retorno.append(" Tipo de Documento: ").append(tipoDocumento.getDescricao()).append("; ");
        }
        if (tiposIssqn != null && !tiposIssqn.isEmpty()) {
            retorno.append(" Tipo(s) I.S.S.Q.N: ").append(StringUtils.join(tiposIssqn, ", ")).append("; ");
        }
        if (situacoesCadastral != null && !situacoesCadastral.isEmpty()) {
            retorno.append(" Situação(ões) da(s) Empresa(s): ").append(StringUtils.join(situacoesCadastral, ", ")).append("; ");
        }
        if (tipoRelatorioApresentacao != null) {
            retorno.append(" Tipo de Apresentação: ").append(tipoRelatorioApresentacao.getDescricao()).append("; ");
        }
        return retorno.toString();
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoPessoa getTipoContribuinte() {
        return tipoContribuinte;
    }

    public void setTipoContribuinte(TipoPessoa tipoContribuinte) {
        this.tipoContribuinte = tipoContribuinte;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public String getCnpjInicial() {
        return cnpjInicial;
    }

    public void setCnpjInicial(String cnpjInicial) {
        this.cnpjInicial = cnpjInicial;
    }

    public String getCnpjFinal() {
        return cnpjFinal;
    }

    public void setCnpjFinal(String cnpjFinal) {
        this.cnpjFinal = cnpjFinal;
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

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public ClassificacaoAtividade getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(ClassificacaoAtividade classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public List<ClassificacaoAtividade> getClassificacoesAtividade() {
        return classificacoesAtividade;
    }

    public void setClassificacoesAtividade(List<ClassificacaoAtividade> classificacoesAtividade) {
        this.classificacoesAtividade = classificacoesAtividade;
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

    public SituacaoLoteRps getSituacaoLoteRps() {
        return situacaoLoteRps;
    }

    public void setSituacaoLoteRps(SituacaoLoteRps situacaoLoteRps) {
        this.situacaoLoteRps = situacaoLoteRps;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public Boolean getForaPais() {
        return foraPais;
    }

    public void setForaPais(Boolean foraPais) {
        this.foraPais = foraPais;
    }

    public Exigibilidade getExigibilidade() {
        return exigibilidade;
    }

    public void setExigibilidade(Exigibilidade exigibilidade) {
        this.exigibilidade = exigibilidade;
    }

    public List<Exigibilidade> getExigibilidades() {
        return exigibilidades;
    }

    public void setExigibilidades(List<Exigibilidade> exigibilidades) {
        this.exigibilidades = exigibilidades;
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

    public BigDecimal getValorTotalInicial() {
        return valorTotalInicial;
    }

    public void setValorTotalInicial(BigDecimal valorTotalInicial) {
        this.valorTotalInicial = valorTotalInicial;
    }

    public BigDecimal getValorTotalFinal() {
        return valorTotalFinal;
    }

    public void setValorTotalFinal(BigDecimal valorTotalFinal) {
        this.valorTotalFinal = valorTotalFinal;
    }

    public TipoRelatorioApresentacao getTipoRelatorioApresentacao() {
        return tipoRelatorioApresentacao;
    }

    public void setTipoRelatorioApresentacao(TipoRelatorioApresentacao tipoRelatorioApresentacao) {
        this.tipoRelatorioApresentacao = tipoRelatorioApresentacao;
    }

    public Boolean getSomenteTotalizador() {
        return somenteTotalizador;
    }

    public void setSomenteTotalizador(Boolean somenteTotalizador) {
        this.somenteTotalizador = somenteTotalizador;
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

    public String getCpfCnpjTomadorInicial() {
        return cpfCnpjTomadorInicial;
    }

    public void setCpfCnpjTomadorInicial(String cpfCnpjTomadorInicial) {
        this.cpfCnpjTomadorInicial = cpfCnpjTomadorInicial;
    }

    public String getCpfCnpjTomadorFinal() {
        return cpfCnpjTomadorFinal;
    }

    public void setCpfCnpjTomadorFinal(String cpfCnpjTomadorFinal) {
        this.cpfCnpjTomadorFinal = cpfCnpjTomadorFinal;
    }

    public TipoDocumentoNfse getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoNfse tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void addClassificacao() {
        Util.adicionarObjetoEmLista(classificacoesAtividade, classificacaoAtividade);
        classificacaoAtividade = null;
    }

    public void removeClassificacao(ClassificacaoAtividade classificacaoAtividade) {
        classificacoesAtividade.remove(classificacaoAtividade);
    }

    public void addServico() {
        Util.adicionarObjetoEmLista(servicos, servico);
        servico = null;
    }

    public void removeServico(Servico servico) {
        servicos.remove(servico);
    }

    public List<String> getNamesClassificacoesAtividade() {
        List<String> names = Lists.newArrayList();
        for (ClassificacaoAtividade c : classificacoesAtividade) {
            names.add(c.name());
        }
        return names;
    }

    public List<String> getNamesSituacoes() {
        List<String> names = Lists.newArrayList();
        for (SituacaoNota s : situacoes) {
            names.add(s.name());
        }
        return names;
    }

    public List<String> getDescricoesExigibilidades() {
        List<String> descricoes = Lists.newArrayList();
        for (Exigibilidade e : exigibilidades) {
            descricoes.add(e.getDescricao());
        }
        return descricoes;
    }

    public List<String> getNamesExigibilidades() {
        List<String> names = Lists.newArrayList();
        for (Exigibilidade e : exigibilidades) {
            names.add(e.name());
        }
        return names;
    }

    public List<Long> getIdsServicos() {
        List<Long> ids = Lists.newArrayList();
        for (Servico s : servicos) {
            ids.add(s.getId());
        }
        return ids;
    }

    public void addSituacao() {
        if (situacao == null) return;
        Util.adicionarObjetoEmLista(situacoes, situacao);
        situacao = null;
    }

    public void removeSituacao(SituacaoNota situacao) {
        situacoes.remove(situacao);
    }

    public void addExigibilidade() {
        if (exigibilidade == null)
            return;
        if (exigibilidades == null) {
            exigibilidades = Lists.newArrayList();
        }
        exigibilidades.add(exigibilidade);
        exigibilidade = null;
    }

    public void removerExigibilidade(Exigibilidade exigibilidade) {
        exigibilidades.remove(exigibilidade);
    }

    public boolean hasExigibilidade(Exigibilidade exigibilidade) {
        if (exigibilidades == null)
            return false;
        return exigibilidades.contains(exigibilidade);
    }

    public Calendar getPeriodoInicial() {
        Calendar periodoInicial = Calendar.getInstance();
        periodoInicial.setTime(new Date());
        if (getExercicioInicial() != null && getMesInicial() != null) {
            periodoInicial.set(Calendar.YEAR, getExercicioInicial().getAno());
            periodoInicial.set(Calendar.MONTH, getMesInicial().getNumeroMesIniciandoEmZero());
            periodoInicial.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            periodoInicial.setTime(getDataInicial());
        }
        return periodoInicial;
    }

    public Calendar getPeriodoFinal() {
        Calendar periodoFinal = Calendar.getInstance();
        periodoFinal.setTime(new Date());
        if (getExercicioFinal() != null && getMesFinal() != null) {
            periodoFinal.set(Calendar.YEAR, getExercicioFinal().getAno());
            periodoFinal.set(Calendar.MONTH, getMesFinal().getNumeroMesIniciandoEmZero());
            periodoFinal.set(Calendar.DAY_OF_MONTH, periodoFinal.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else {
            periodoFinal.setTime(getDataFinal());
        }
        return periodoFinal;
    }

    public TipoIssqn getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(TipoIssqn tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public List<TipoIssqn> getTiposIssqn() {
        return tiposIssqn;
    }

    public void setTiposIssqn(List<TipoIssqn> tiposIssqn) {
        this.tiposIssqn = tiposIssqn;
    }

    public void addTipoIssqn() {
        if (tipoIssqn == null) return;
        Util.adicionarObjetoEmLista(tiposIssqn, tipoIssqn);
        tipoIssqn = null;
    }

    public void removeTipoIssqn(TipoIssqn tipoIssqn) {
        tiposIssqn.remove(tipoIssqn);
    }

    public SituacaoCadastralCadastroEconomico getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(SituacaoCadastralCadastroEconomico situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesCadastral() {
        return situacoesCadastral;
    }

    public void setSituacoesCadastral(List<SituacaoCadastralCadastroEconomico> situacoesCadastral) {
        this.situacoesCadastral = situacoesCadastral;
    }

    public void addSituacaoCadastral() {
        if (situacaoCadastral == null) return;
        Util.adicionarObjetoEmLista(situacoesCadastral, situacaoCadastral);
        situacaoCadastral = null;
    }

    public void removeSituacaoCadastral(SituacaoCadastralCadastroEconomico situacaoCadastral) {
        situacoesCadastral.remove(situacaoCadastral);
    }

    public Map<String, Map<String, String>> montarMapFiltrosExcel() {
        Map<String, Map<String, String>> filtros = Maps.newHashMap();

        if (dataInicial != null && dataFinal != null) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Período:", DataUtil.getDataFormatada(dataInicial) + " à " +
                DataUtil.getDataFormatada(dataFinal));
        }

        if (exercicioInicial != null && mesInicial != null &&
            exercicioFinal != null && mesFinal != null) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Competência:", mesInicial.getDescricao() + "/" + exercicioInicial.toString() + " à " +
                mesFinal.getDescricao() + "/" + exercicioFinal.toString());
        }

        if (!com.google.common.base.Strings.isNullOrEmpty(numero)) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Número:", numero);
        }

        if (cadastroEconomico != null) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Razão Social:", cadastroEconomico.getPessoa().getNome());
            WebReportUtil.adicionarMapFiltroExcel(filtros, "CNPJ:", cadastroEconomico.getPessoa().getCpf_Cnpj());
            WebReportUtil.adicionarMapFiltroExcel(filtros, "CMC:", cadastroEconomico.getInscricaoCadastral());
            EnderecoCorreio enderecoPrincipal = cadastroEconomico.getPessoa().getEnderecoPrincipal();
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Endereço:", enderecoPrincipal != null ? enderecoPrincipal.toString() : "Não Informado.");
            Telefone telefonePrincipal = cadastroEconomico.getPessoa().getTelefonePrincipal();
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Telefone:", telefonePrincipal != null ? telefonePrincipal.toString() : "Não Informado.");
            WebReportUtil.adicionarMapFiltroExcel(filtros, "E-mail:", cadastroEconomico.getPessoa().getEmail());
        }

        if (!com.google.common.base.Strings.isNullOrEmpty(cnpjInicial)) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "CNPJ Inicial:", cnpjInicial);
        }

        if (!com.google.common.base.Strings.isNullOrEmpty(cnpjFinal)) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "CNPJ Final:", cnpjFinal);
        }

        if (tipoContribuinte != null) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Tipo de Tomador:", tipoContribuinte.getDescricao());
        }

        if (contribuinte != null) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Tomador:", contribuinte.getNomeCpfCnpj());
        }

        if (!com.google.common.base.Strings.isNullOrEmpty(cpfCnpjTomadorInicial)) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "CPF/CNPJ Inicial:", cpfCnpjTomadorInicial);
        }

        if (!com.google.common.base.Strings.isNullOrEmpty(cpfCnpjTomadorFinal)) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "CPF/CNPJ Final:", cpfCnpjTomadorFinal);
        }

        if (exigibilidades != null && !exigibilidades.isEmpty()) {
            StringBuilder descricoes = new StringBuilder();
            for (String descricao : getDescricoesExigibilidades()) {
                if (!descricao.isEmpty()) {
                    descricoes.append(descricao);
                    descricoes.append(", ");
                }
            }
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Natureza(s) Operação(ões):", descricoes.toString());
        }

        if (foraPais != null) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Fora do País:", foraPais ? "Sim" : "Não");
        }

        if (situacoes != null && !situacoes.isEmpty()) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Situações:",
                StringUtils.join(situacoes.stream().map(SituacaoNota::getDescricao).collect(Collectors.toList()), ", "));
        } else {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Situações:", "Todas.");
        }

        if (servicos != null && !servicos.isEmpty()) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Serviços:", StringUtils.join(servicos, ", "));
        }

        if (tiposIssqn != null && !tiposIssqn.isEmpty()) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Tipos de I.S.S.Q.N:", StringUtils.join(tiposIssqn.stream()
                .map(TipoIssqn::getDescricao).collect(Collectors.toList()), ", "));
        }

        if (situacoesCadastral != null && !situacoesCadastral.isEmpty()) {
            WebReportUtil.adicionarMapFiltroExcel(filtros, "Situação(ões) da(s) Empresa(s):", StringUtils.join(situacoesCadastral.stream()
                .map(SituacaoCadastralCadastroEconomico::getDescricao).collect(Collectors.toList()), ", "));
        }

        return filtros;
    }

    public TipoPorte getTipoPorte() {
        return tipoPorte;
    }

    public void setTipoPorte(TipoPorte tipoPorte) {
        this.tipoPorte = tipoPorte;
    }

    public List<TipoPorte> getTiposPorte() {
        return tiposPorte;
    }

    public void setTiposPorte(List<TipoPorte> tiposPorte) {
        this.tiposPorte = tiposPorte;
    }

    public void addTipoPorte() {
        if (tipoPorte == null) return;
        Util.adicionarObjetoEmLista(tiposPorte, tipoPorte);
        tipoPorte = null;
    }

    public void removeTipoPorte(TipoPorte tipoPorte) {
        tiposPorte.remove(tipoPorte);
    }
}
