package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class DocumentoFiscalIntegracao implements Serializable {
    private String origem;
    private String descricao;
    private String unidadeAdministrativa;
    private Long idOrigem;
    private DoctoFiscalLiquidacao doctoFiscalLiquidacao;
    private Desdobramento desdobramento;
    private DesdobramentoLiquidacaoEstorno desdobramentoEstorno;
    private BigDecimal valorALiquidar;
    private BigDecimal valorLiquidado;
    private BigDecimal saldo;
    private Boolean selecionado;
    private Boolean integrador;
    private Boolean expandirGrupos;
    private TipoContaDespesa tipoContaDespesa;
    private List<String> mensagens;
    private List<EmpenhoDocumentoFiscal> empenhosDocumentoFiscal;
    private List<DocumentoFiscalIntegracaoGrupo> grupos;
    private List<LiquidacaoDoctoFiscalTransferenciaBens> transferenciasBens;
    private List<EstornoLiquidacaoDoctoFiscalTransferenciaBens> estornoTransferenciasBens;

    public DocumentoFiscalIntegracao() {
        grupos = Lists.newArrayList();
        empenhosDocumentoFiscal = Lists.newArrayList();
        transferenciasBens = Lists.newArrayList();
        estornoTransferenciasBens = Lists.newArrayList();
        mensagens = Lists.newArrayList();
        selecionado = false;
        integrador = false;
        expandirGrupos = false;
        valorALiquidar = BigDecimal.ZERO;
        valorLiquidado = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(String unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public DoctoFiscalLiquidacao getDoctoFiscalLiquidacao() {
        return doctoFiscalLiquidacao;
    }

    public void setDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        this.doctoFiscalLiquidacao = doctoFiscalLiquidacao;
    }

    public Desdobramento getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(Desdobramento desdobramento) {
        this.desdobramento = desdobramento;
    }

    public DesdobramentoLiquidacaoEstorno getDesdobramentoEstorno() {
        return desdobramentoEstorno;
    }

    public void setDesdobramentoEstorno(DesdobramentoLiquidacaoEstorno desdobramentoEstorno) {
        this.desdobramentoEstorno = desdobramentoEstorno;
    }

    public BigDecimal getValorLiquidado() {
        return valorLiquidado;
    }

    public void setValorLiquidado(BigDecimal valorLiquidado) {
        this.valorLiquidado = valorLiquidado;
    }

    public BigDecimal getValorALiquidar() {
        return valorALiquidar;
    }

    public void setValorALiquidar(BigDecimal valorALiquidar) {
        this.valorALiquidar = valorALiquidar;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Boolean getIntegrador() {
        return integrador;
    }

    public void setIntegrador(Boolean integrador) {
        this.integrador = integrador;
    }

    public Boolean getExpandirGrupos() {
        return expandirGrupos;
    }

    public void setExpandirGrupos(Boolean expandirGrupos) {
        this.expandirGrupos = expandirGrupos;
    }

    public List<DocumentoFiscalIntegracaoGrupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<DocumentoFiscalIntegracaoGrupo> grupos) {
        this.grupos = grupos;
    }

    public List<LiquidacaoDoctoFiscalTransferenciaBens> getTransferenciasBens() {
        return transferenciasBens;
    }

    public void setTransferenciasBens(List<LiquidacaoDoctoFiscalTransferenciaBens> transferenciasBens) {
        this.transferenciasBens = transferenciasBens;
    }

    public List<EstornoLiquidacaoDoctoFiscalTransferenciaBens> getEstornoTransferenciasBens() {
        return estornoTransferenciasBens;
    }

    public void setEstornoTransferenciasBens(List<EstornoLiquidacaoDoctoFiscalTransferenciaBens> estornoTransferenciasBens) {
        this.estornoTransferenciasBens = estornoTransferenciasBens;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<String> mensagens) {
        this.mensagens = mensagens;
    }

    public boolean hasInconsistencia() {
        return !Util.isListNullOrEmpty(mensagens);
    }

    public BigDecimal getSaldo() {
        return isLiquidar() ? doctoFiscalLiquidacao.getValor().subtract(getValorLiquidado()) : saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public List<EmpenhoDocumentoFiscal> getEmpenhosDocumentoFiscal() {
        return empenhosDocumentoFiscal;
    }

    public void setEmpenhosDocumentoFiscal(List<EmpenhoDocumentoFiscal> empenhosDocumentoFiscal) {
        this.empenhosDocumentoFiscal = empenhosDocumentoFiscal;
    }

    public boolean hasGrupos() {
        return !Util.isListNullOrEmpty(grupos);
    }

    public boolean hasSaldo() {
        return saldo != null && saldo.compareTo(BigDecimal.ZERO) > 0;
    }

    public List<Empenho> getEmpenhos() {
        List<Empenho> empenhos = Lists.newArrayList();
        if (hasGrupos()) {
            for (DocumentoFiscalIntegracaoGrupo grupo : grupos) {
                if (grupo.getEmpenho() != null) {
                    empenhos.add(grupo.getEmpenho());
                }
            }
        }
        return empenhos;
    }

    public BigDecimal getValorTotalALiquidarGrupoIntegrador() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasGrupos() && getIntegrador()) {
            for (DocumentoFiscalIntegracaoGrupo grupo : getGrupos()) {
                if (grupo.getIntegrador()) {
                    for (DocumentoFiscalIntegracaoGrupoItem item : grupo.getItens()) {
                        valor = valor.add(item.getValorALiquidar() != null ? item.getValorALiquidar() : BigDecimal.ZERO);
                    }
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalGrupoIntegrador() {
        BigDecimal valor = BigDecimal.ZERO;
        if (hasGrupos() && getIntegrador()) {
            for (DocumentoFiscalIntegracaoGrupo grupo : getGrupos()) {
                if (grupo.getIntegrador()) {
                    valor = valor.add(grupo.getValorGrupo());
                }
            }
        }
        return valor;
    }

    public BigDecimal getValorTotalEmpenhoDocumento() {
        BigDecimal valor = BigDecimal.ZERO;
        if (!Util.isListNullOrEmpty(empenhosDocumentoFiscal)) {
            for (EmpenhoDocumentoFiscal emp : empenhosDocumentoFiscal) {
                valor = valor.add(emp.getValor());
            }
        }
        return valor;
    }

    public String getDescricaoProcesso() {
        return tipoContaDespesa.isBemMovel() ? "Aquisição" : "Entrada por Compra";
    }

    public boolean isLiquidar() {
        return desdobramento != null;
    }

    public boolean isBemMovel() {
        return getTipoContaDespesa() != null && getTipoContaDespesa().isBemMovel();
    }

    public boolean isEstoque() {
        return getTipoContaDespesa() != null && getTipoContaDespesa().isEstoque();
    }

    public String getDescricaoColunaItem() {
        return isBemMovel() ? "Bem" : "Material";
    }

    public String getDescricaoGrupoProcesso() {
        return isBemMovel() ? "Grupo Patrimonial" : "Grupo Material";
    }

    public EmpenhoDocumentoFiscal getEmpenhoDocumentoFiscal(Empenho empenho) {
        for (EmpenhoDocumentoFiscal empVO : empenhosDocumentoFiscal) {
            if (empVO.getId().equals(empenho.getId())) {
                return empVO;
            }
        }
        return new EmpenhoDocumentoFiscal(empenho);
    }

    public boolean hasMaisDeUmEmpenhoDocumentoFiscal() {
        return !Util.isListNullOrEmpty(empenhosDocumentoFiscal) && empenhosDocumentoFiscal.size() > 1;
    }

    public void verificarInconsistencias() {
        if (getIntegrador()) {
            setMensagens(Lists.<String>newArrayList());
            String descricao = isLiquidar() ? "liquidar" : "estornar";
            if (valorALiquidar == null || valorALiquidar.compareTo(BigDecimal.ZERO) < 0) {
                mensagens.add("O campo valor a liquidar deve ser informado para o documento " + doctoFiscalLiquidacao.getNumero() + ".");

            } else if (getSelecionado() && !hasSaldo()) {
                mensagens.add("O documento fiscal não possui saldo à " + descricao + ". ");
            }
            if (isLiquidar() && desdobramento.getEmpenho() != null && getValorTotalALiquidarGrupoIntegrador().compareTo(desdobramento.getEmpenho().getSaldo()) > 0) {
                mensagens.add("O valor total dos documentos deve menor ou igual ao saldo do empenho. ");
            }
            if (valorALiquidar.compareTo(getValorTotalGrupoIntegrador()) > 0) {
                mensagens.add("O valor do documento está maior que o valor permitido para o " + getDescricaoGrupoProcesso());
            }
            if (valorALiquidar.compareTo(saldo) > 0) {
                mensagens.add("O valor a " + descricao + " deve ser menor ou igual ao saldo do documento de " + Util.formataValor(saldo) + ".");
            }
            if (valorALiquidar.compareTo(getValorTotalALiquidarGrupoIntegrador()) != 0) {
                mensagens.add("O valor total a " + descricao + " distribuido nos itens, deve ser igual ao total do documento comprobatório.");
            }
            if (hasMaisDeUmEmpenhoDocumentoFiscal() && isLiquidar()) {
                EmpenhoDocumentoFiscal empenhoVO = getEmpenhoDocumentoFiscal(desdobramento.getLiquidacao().getEmpenho());
                if (valorALiquidar.compareTo(empenhoVO.getValor()) > 0) {
                    mensagens.add("O valor a " + descricao + " deve ser menor ou igual ao valor de " + Util.formataValor(empenhoVO.getValor()) + ", referente ao empenho que original o item do documento.");
                }
            }
        }
    }
}
