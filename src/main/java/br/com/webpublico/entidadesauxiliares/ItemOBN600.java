package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoDocPagto;
import br.com.webpublico.enums.TipoOperacaoPagto;
import br.com.webpublico.interfaces.IGuiaArquivoOBN600;
import br.com.webpublico.util.StringUtil;

import java.math.BigDecimal;

/**
 * Created by Edi on 01/04/2016.
 */
public class ItemOBN600 {

    /*
    * Variáveis linha tipo 4 e 5
    */
    private Integer sequenciaInternaGuia;
    private TipoDocPagto tipoDocPagto;
    private IGuiaArquivoOBN600 guia;
    /*
    * Variáveis linha tipo 3
    */
    private Integer sequenciaInternoMovimentosOB;

    /*
    Variáveis Header
     */
    private Integer sequenciaInterna;
    private String horaMinutoDoArquivo;
    private String dataDoArquivo;


    /*
    Variáveis linha tipo 1
     */
    private String codigoDaAgenciaDevolucaPorErro;
    private String codigoDaUnidade;
    private String descricaoUnidade;
    private String codigoDoOrgao;
    private String descricaoOrgao;
    private String codigoOrgaoUnidadeUGEmitente;
    private String codigoDaContaDevolucaPorErro;
    private ContaBancariaEntidade contaBancariaEntidade;
    private String cidadeUnidade;
    private String ruaUnidade;
    private String cepUnidade;
    private String ufUnidade;

    /*
    Variáveis Gerais
     */
    private Boolean contaUnica;
    private UnidadeOrganizacional unidadeOrganizacional;
    private Bordero ordemBancaria;
    private String dataDaOrdemBancaria;
    private BancoObn bancoObn;
    private ContratoObn contratoObn;

    /*
    Variáveis linha tipo 2
     */
    private String numeroDoMovimento;
    private TipoOperacaoPagto tipoOperacaoPagto;
    private BigDecimal valor;
    private Banco bancoFornecedor;
    private Agencia agenciaFornecedor;
    private ContaCorrenteBancaria contaFornecedor;
    private String codigoContaCorrenteFornecedor;
    private Pessoa fornecedor;
    private EnderecoCorreio endereco;
    private String uf;
    private String historico;
    private FinalidadePagamento finalidadePagamento;
    private String codigoBorderoRelacaoPtos;

    public Integer getSequenciaInternaGuia() {
        return sequenciaInternaGuia;
    }

    public void setSequenciaInternaGuia(Integer sequenciaInternaGuia) {
        this.sequenciaInternaGuia = sequenciaInternaGuia;
    }

    public TipoDocPagto getTipoDocPagto() {
        return tipoDocPagto;
    }

    public void setTipoDocPagto(TipoDocPagto tipoDocPagto) {
        this.tipoDocPagto = tipoDocPagto;
    }

    public IGuiaArquivoOBN600 getGuia() {
        return guia;
    }

    public void setGuia(IGuiaArquivoOBN600 guia) {
        this.guia = guia;
    }

    public Integer getSequenciaInternoMovimentosOB() {
        return sequenciaInternoMovimentosOB;
    }

    public void setSequenciaInternoMovimentosOB(Integer sequenciaInternoMovimentosOB) {
        this.sequenciaInternoMovimentosOB = sequenciaInternoMovimentosOB;
    }

    public Integer getSequenciaInterna() {
        return sequenciaInterna;
    }

    public void setSequenciaInterna(Integer sequenciaInterna) {
        this.sequenciaInterna = sequenciaInterna;
    }

    public String getHoraMinutoDoArquivo() {
        return horaMinutoDoArquivo;
    }

    public void setHoraMinutoDoArquivo(String horaMinutoDoArquivo) {
        this.horaMinutoDoArquivo = horaMinutoDoArquivo;
    }

    public String getDataDoArquivo() {
        return dataDoArquivo;
    }

    public void setDataDoArquivo(String dataDoArquivo) {
        this.dataDoArquivo = dataDoArquivo;
    }

    public String getCodigoDaAgenciaDevolucaPorErro() {
        return codigoDaAgenciaDevolucaPorErro;
    }

    public void setCodigoDaAgenciaDevolucaPorErro(String codigoDaAgenciaDevolucaPorErro) {
        this.codigoDaAgenciaDevolucaPorErro = codigoDaAgenciaDevolucaPorErro;
    }

    public String getCodigoDaUnidade() {
        return codigoDaUnidade;
    }

    public void setCodigoDaUnidade(String codigoDaUnidade) {
        this.codigoDaUnidade = codigoDaUnidade;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public String getCodigoDoOrgao() {
        return codigoDoOrgao;
    }

    public void setCodigoDoOrgao(String codigoDoOrgao) {
        this.codigoDoOrgao = codigoDoOrgao;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getCodigoOrgaoUnidadeUGEmitente() {
        return codigoOrgaoUnidadeUGEmitente;
    }

    public void setCodigoOrgaoUnidadeUGEmitente(String codigoOrgaoUnidadeUGEmitente) {
        this.codigoOrgaoUnidadeUGEmitente = codigoOrgaoUnidadeUGEmitente;
    }

    public String getCodigoDaContaDevolucaPorErro() {
        return codigoDaContaDevolucaPorErro;
    }

    public void setCodigoDaContaDevolucaPorErro(String codigoDaContaDevolucaPorErro) {
        this.codigoDaContaDevolucaPorErro = codigoDaContaDevolucaPorErro;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public String getCidadeUnidade() {
        return cidadeUnidade;
    }

    public void setCidadeUnidade(String cidadeUnidade) {
        this.cidadeUnidade = cidadeUnidade;
    }

    public String getRuaUnidade() {
        return ruaUnidade;
    }

    public void setRuaUnidade(String ruaUnidade) {
        this.ruaUnidade = ruaUnidade;
    }

    public String getCepUnidade() {
        return cepUnidade;
    }

    public void setCepUnidade(String cepUnidade) {
        this.cepUnidade = cepUnidade;
    }

    public String getUfUnidade() {
        return ufUnidade;
    }

    public void setUfUnidade(String ufUnidade) {
        this.ufUnidade = ufUnidade;
    }

    public Boolean getContaUnica() {
        return contaUnica;
    }

    public void setContaUnica(Boolean contaUnica) {
        this.contaUnica = contaUnica;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Bordero getOrdemBancaria() {
        return ordemBancaria;
    }

    public void setOrdemBancaria(Bordero ordemBancaria) {
        this.ordemBancaria = ordemBancaria;
    }

    public String getDataDaOrdemBancaria() {
        return dataDaOrdemBancaria;
    }

    public void setDataDaOrdemBancaria(String dataDaOrdemBancaria) {
        this.dataDaOrdemBancaria = dataDaOrdemBancaria;
    }

    public BancoObn getBancoObn() {
        return bancoObn;
    }

    public void setBancoObn(BancoObn bancoObn) {
        this.bancoObn = bancoObn;
    }

    public String getNumeroDoMovimento() {
        return numeroDoMovimento;
    }

    public void setNumeroDoMovimento(String numeroDoMovimento) {
        this.numeroDoMovimento = numeroDoMovimento;
    }

    public TipoOperacaoPagto getTipoOperacaoPagto() {
        return tipoOperacaoPagto;
    }

    public void setTipoOperacaoPagto(TipoOperacaoPagto tipoOperacaoPagto) {
        this.tipoOperacaoPagto = tipoOperacaoPagto;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Banco getBancoFornecedor() {
        return bancoFornecedor;
    }

    public void setBancoFornecedor(Banco bancoFornecedor) {
        this.bancoFornecedor = bancoFornecedor;
    }

    public Agencia getAgenciaFornecedor() {
        return agenciaFornecedor;
    }

    public void setAgenciaFornecedor(Agencia agenciaFornecedor) {
        this.agenciaFornecedor = agenciaFornecedor;
    }

    public ContaCorrenteBancaria getContaFornecedor() {
        return contaFornecedor;
    }

    public void setContaFornecedor(ContaCorrenteBancaria contaFornecedor) {
        this.contaFornecedor = contaFornecedor;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public EnderecoCorreio getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoCorreio endereco) {
        this.endereco = endereco;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public FinalidadePagamento getFinalidadePagamento() {
        return finalidadePagamento;
    }

    public void setFinalidadePagamento(FinalidadePagamento finalidadePagamento) {
        this.finalidadePagamento = finalidadePagamento;
    }

    public String getCodigoBorderoRelacaoPtos() {
        return codigoBorderoRelacaoPtos;
    }

    public String getCodigoContaCorrenteFornecedor() {
        return codigoContaCorrenteFornecedor;
    }

    public void setCodigoContaCorrenteFornecedor(String codigoContaCorrenteFornecedor) {
        this.codigoContaCorrenteFornecedor = codigoContaCorrenteFornecedor;
    }

    public void setCodigoBorderoRelacaoPtos(String codigoBorderoRelacaoPtos) {
        this.codigoBorderoRelacaoPtos = codigoBorderoRelacaoPtos;
    }

// Não remover, comentado pois a regra mudou para o gerador do arquivo
//    private boolean isFornecedorPF(Pessoa fornecedor) {
//        return fornecedor instanceof PessoaFisica;
//    }
//
//    private boolean isFornecedorPJ(Pessoa fornecedor) {
//        return fornecedor instanceof PessoaJuridica;
//    }
//
//    public String getContaFavorecido(ContaCorrenteBancaria contaFornecedor, Pessoa fornecedor) {
//        if (contaFornecedor != null) {
//            if (this.bancoObn.isArquivoCaixaEconomica()) {
//                String codigoOperacaoCaixa = "";
//                if (isFornecedorPF(fornecedor)) {
//                    if (ModalidadeConta.CONTA_CORRENTE.equals(contaFornecedor.getModalidadeConta())
//                        || ModalidadeConta.CONTA_SALARIO.equals(contaFornecedor.getModalidadeConta())) {
//                        codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.CONTA_CORRENTE_PESSOA_FISICA.getCodigo();
//                    }
//                } else {
//                    if (ModalidadeConta.CONTA_CORRENTE.equals(contaFornecedor.getModalidadeConta())) {
//                        codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.CONTA_CORRENTE_PESSOA_JURIDICA.getCodigo();
//                    }
//                    if (ModalidadeConta.CONTA_POUPANCA_PESSOA_JURIDICA.equals(contaFornecedor.getModalidadeConta())) {
//                        codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.POUPANCA_PESSOA_JURIDICA.getCodigo();
//                    }
//                    if (ModalidadeConta.ENTIDADES_PUBLICAS.equals(contaFornecedor.getModalidadeConta())) {
//                        codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.ENTIDADES_PUBLICAS.getCodigo();
//                    }
//                }
//                if (ModalidadeConta.CONTA_POUPANCA.equals(contaFornecedor.getModalidadeConta())) {
//                    codigoOperacaoCaixa = TipoOperacaoCaixaEconomica.POUPANCA.getCodigo();
//                }
//                System.out.println("Aqui...: " + codigoOperacaoCaixa + contaFornecedor.getNumeroContaComDigito());
//                return codigoOperacaoCaixa + contaFornecedor.getNumeroContaComDigito();
//            } else {
//                return contaFornecedor.getNumeroContaComDigito();
//            }
//        }
////        else {
////            if (contaBancariaEntidade != null) {
////                if (this.bancoObn.isArquivoCaixaEconomica()
////                    && contaBancariaEntidade.getTipoOperacaoCaixaEconomica() != null) {
////                    return contaBancariaEntidade.getTipoOperacaoCaixaEconomica().getCodigo().substring(1, 3) + contaBancariaEntidade.getNumeroContaComDigitoVerificadorArquivoOBN600();
////                } else {
////                    return contaBancariaEntidade.getNumeroContaComDigitoVerificadorArquivoOBN600();
////                }
////            }
////        }
//        return "";
//    }

    public String getCodigoOBdaFinalidadePagamento(FinalidadePagamento finalidadePagamento) {
        if (finalidadePagamento != null) {
            if (finalidadePagamento.getCodigoOB() != null) {
                return finalidadePagamento.getCodigoOB();
            }
        }
        return "";
    }

    public String limpaCpfCnpj(String valor) {
        try {
            return StringUtil.retornaApenasNumeros(valor);
        } catch (Exception e) {
            return "";
        }
    }

    public String getCep(EnderecoCorreio endereco) {
        String retorno = "";
        if (endereco != null) {
            if (endereco.getCep() != null) {
                retorno = endereco.getCep().replace(".", "").replace("-", "").trim();
            }
        }
        return retorno;
    }


    public ContratoObn getContratoObn() {
        return contratoObn;
    }

    public void setContratoObn(ContratoObn contratoObn) {
        this.contratoObn = contratoObn;
    }
}
