package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoHolerite;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class HoleriteBBFacade extends AbstractFacade<HoleriteBB> {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HoleriteBBFacade() {
        super(HoleriteBB.class);
    }

    public String addMensagem(String msg, HoleriteBB selecionado) {
        selecionado.setMensagens(msg);
        return selecionado.getMensagens();
    }

    @Asynchronous
    @TransactionTimeout(value = 3, unit = TimeUnit.HOURS)
    public void gerarArquivo(HoleriteBB selecionado, Date dataReferencia, ContaBancariaEntidade contaBancariaEntidade, HierarquiaOrganizacional hierarquiaOrganizacional) {
        try {
            addMensagem("<b> <font color='blue'>...Iniciando...</font> </b>", selecionado);
            inicializar(selecionado);
            String conteudo = "";
            List<HoleriteBBAuxiliar> holeriteBBAuxiliars = buscarContratoFp(selecionado, hierarquiaOrganizacional);
            selecionado.setQuantidadeContratos(holeriteBBAuxiliars.size());
            selecionado.setTotal(selecionado.getQuantidadeContratos());
            conteudo = gerarHeader(selecionado, dataReferencia, contaBancariaEntidade);

            String conteudoDEtalhes = "";

            addMensagem("<b> <font color='black'>...Gerando Arquivo...</font> </b>", selecionado);
            for (HoleriteBBAuxiliar contratoFP : holeriteBBAuxiliars) {
                conteudoDEtalhes = gerarDetalhes(contratoFP, selecionado);
                int numeroDeLinhas = StringUtil.contadorLinhas(conteudoDEtalhes);
                conteudo += gerarDestinatario(contratoFP, numeroDeLinhas);
                conteudo += conteudoDEtalhes;
                selecionado.setProcessados(selecionado.getProcessados() + 1);
            }

            conteudo += gerarTrailer(holeriteBBAuxiliars);
            selecionado.setConteudoArquivo(conteudo);
            addMensagem("<b> <font color='blue'>...Arquivo Pronto para Download...</font> </b>", selecionado);
            salvar(selecionado);
            selecionado.setCalculando(false);
        } catch (Exception eng) {
            selecionado.setMensagemExcecao("Erro ao gerar o arquivo: " + eng.getMessage());
            selecionado.setCalculando(false);
        }
    }

    public void inicializar(HoleriteBB selecionado) {
        selecionado.setProcessados(0);
        selecionado.setTotal(0);
        selecionado.setCalculando(true);
        selecionado.setTempo(System.currentTimeMillis());

    }

    private String gerarTrailer(List<HoleriteBBAuxiliar> contratoFPs) {
        HoleriteBBTrailer trailer = new HoleriteBBTrailer();
        trailer.setNumeroIdentificadorDestinatario("");
        trailer.setTipoRegistro("");
        trailer.setAgenciaEConta("");
        trailer.setQuantidadeDestinatarios(String.valueOf(contratoFPs.size()));
        trailer.setEspacosBrancos(" ");
        trailer.montaTrailer(trailer);
        return trailer.getTrailer();
    }

    private String gerarDetalhes(HoleriteBBAuxiliar holeriteBBAuxiliar, HoleriteBB selecionado) {
        String mensagem = "";
        VinculoFP contratoFP = holeriteBBAuxiliar.getVinculoFP();

        String retorno = "";
        Integer sequencial = 1;
        FichaFinanceiraFP ff = getFichaFinanceiraFPDaFolhaDePagamentoPorMesAndAnoAndTipo(selecionado, holeriteBBAuxiliar);

        if (ff != null && isEventoFP(ff)) {

            HoleriteBBDetalhes orgao = montarHoleriteBBDetalhes(contratoFP, sequencial++, StringUtil.removeCaracteresEspeciaisSemEspaco(contratoFP.getUnidadeOrganizacional().getDescricao().toUpperCase()));
            retorno += orgao.getDetalhes();

            HoleriteBBDetalhes cnpj = montarHoleriteBBDetalhes(contratoFP, sequencial++, "CNPJ:04034583000122");
            retorno += cnpj.getDetalhes();
            long time = System.currentTimeMillis();

            String lotacaoFuncional = holeriteBBAuxiliar.getLotacaoFuncional().getUnidadeOrganizacional().getDescricao();
            if (holeriteBBAuxiliar.getVinculoFP().getUnidadeOrganizacional() != null) {
                lotacaoFuncional = StringUtil.removeCaracteresEspeciaisSemEspaco(lotacaoFuncional);
            }
            HoleriteBBDetalhes localdetrabalho = montarHoleriteBBDetalhes(contratoFP, sequencial++, lotacaoFuncional);
            retorno += localdetrabalho.getDetalhes();

            String textoCpfConta = "CPF:" + contratoFP.getMatriculaFP().getPessoa().getCpf();
            if (contratoFP.getContaCorrente() != null && contratoFP.getContaCorrente().getAgencia() != null) {
                textoCpfConta += " CONTA:" + contratoFP.getContaCorrente().getAgencia().getNumeroAgencia() + "-" + contratoFP.getContaCorrente().getAgencia().getDigitoVerificador()
                    + "/" + contratoFP.getContaCorrente().getNumeroConta() + "-" + contratoFP.getContaCorrente().getDigitoVerificador();
            }
            HoleriteBBDetalhes referencia = montarHoleriteBBDetalhes(contratoFP, sequencial++, "REF.: " + Mes.getMesToInt(Integer.parseInt(selecionado.getMes())).name() + "/" + selecionado.getAno());
            retorno += referencia.getDetalhes();

            String numeroMatricula = contratoFP.getMatriculaFP().getMatricula();
            String numeroContrato = contratoFP.getNumero();
            String nome = StringUtil.removeCaracteresEspeciaisSemEspaco(contratoFP.getMatriculaFP().getPessoa().getNome());
            HoleriteBBDetalhes matricula = montarHoleriteBBDetalhes(contratoFP, sequencial++, "  " + numeroMatricula + "-" + numeroContrato + " " + nome);
            retorno += matricula.getDetalhes();

            HoleriteBBDetalhes cpfConta = montarHoleriteBBDetalhes(contratoFP, sequencial++, textoCpfConta);
            retorno += cpfConta.getDetalhes();


            String descricaoCargo = "";
            if (contratoFP instanceof Aposentadoria) {
                descricaoCargo = "APOSENTADO(A)";
            } else if (contratoFP instanceof Pensionista) {
                descricaoCargo = "PENSIONISTA";
            } else if (contratoFP instanceof Beneficiario) {
                descricaoCargo = "BENEFICIÁRIO";
            } else {
                descricaoCargo = ((ContratoFP) contratoFP).getCargo() != null ? StringUtil.removeCaracteresEspeciaisSemEspaco(((ContratoFP) contratoFP).getCargo().getDescricao()) : "";
            }

            HoleriteBBDetalhes cargo = montarHoleriteBBDetalhes(contratoFP, sequencial++, StringUtil.cortaOuCompletaDireita("CARGO: " + descricaoCargo, 48, " "));
            retorno += cargo.getDetalhes();

            HoleriteBBDetalhes referenciaSalarial = montarHoleriteBBDetalhes(contratoFP, sequencial++, StringUtil.cortaOuCompletaDireita("REFERENCIA SALARIAL: " + holeriteBBAuxiliar.getProgressaoPCS().getDescricao(), 48, " "));
            retorno += referenciaSalarial.getDetalhes();

            HoleriteBBDetalhes header = montarHoleriteBBDetalhes(contratoFP, sequencial++, StringUtil.centralizaTexto("DEMONSTRATIVO DE PAGAMENTO", 48, " "));
            retorno += header.getDetalhes();

            HoleriteBBDetalhes subHeader = montarHoleriteBBDetalhes(contratoFP, sequencial++, "VERBA DESCRICAO                   T       VALOR");
            retorno += subHeader.getDetalhes();

            HoleriteBBDetalhes pontilhado = montarHoleriteBBDetalhes(contratoFP, sequencial++, StringUtil.cortaOuCompletaDireita("", 48, "-"));
            retorno += pontilhado.getDetalhes();
            time = System.currentTimeMillis();


            HoleriteBBDetalhes itemHolerite = new HoleriteBBDetalhes();
            time = System.currentTimeMillis();


            Collections.sort(ff.getItemFichaFinanceiraFP(), new Comparator<ItemFichaFinanceiraFP>() {
                @Override
                public int compare(ItemFichaFinanceiraFP o1, ItemFichaFinanceiraFP o2) {
                    return o1.getEventoFP().getCodigoInt().compareTo(o2.getEventoFP().getCodigoInt());
                }
            });
            for (ItemFichaFinanceiraFP item : ff.getItemFichaFinanceiraFP()) {
                itemHolerite = montarHoleriteBBDetalhes(contratoFP, sequencial++, montarItemFichaFinanceira(item));
                retorno += itemHolerite.getDetalhes();
            }


            HoleriteBBDetalhes pontilhadoFinal = montarHoleriteBBDetalhes(contratoFP, sequencial++, StringUtil.cortaOuCompletaDireita("", 48, "-"));
            retorno += pontilhadoFinal.getDetalhes();

            List<String> linhaMensagem = new ArrayList<>();
            mensagem = StringUtil.removeAcentuacao(mensagem);
            for (int i = 0; i <= mensagem.length(); i += 48) {
                try {
                    linhaMensagem.add(mensagem.substring(i, i + 48));
                } catch (StringIndexOutOfBoundsException stx) {
                    linhaMensagem.add(mensagem.substring(i, mensagem.length()));
                }
            }

            for (String linha : linhaMensagem) {
                HoleriteBBDetalhes mensagemDetalhe = montarHoleriteBBDetalhes(contratoFP, sequencial++, linha);
                retorno += mensagemDetalhe.getDetalhes();
            }
        }

        return retorno;
    }

    private FichaFinanceiraFP getFichaFinanceiraFPDaFolhaDePagamentoPorMesAndAnoAndTipo(HoleriteBB selecionado, HoleriteBBAuxiliar holeriteBBAuxiliar) {
        Mes mes = Mes.getMesToInt(Integer.valueOf(selecionado.getMes()));
        Integer ano = Integer.valueOf(selecionado.getAno());

        for (FichaFinanceiraFP fichaFinanceiraFP : holeriteBBAuxiliar.getFichaFinanceiraFPs()) {
            if (fichaFinanceiraFP.getFolhaDePagamento().getMes().equals(mes) && fichaFinanceiraFP.getFolhaDePagamento().getAno().equals(ano)) {
                return fichaFinanceiraFP;
            }
        }
        return null;
    }

    private boolean isEventoFP(FichaFinanceiraFP ff) {
        for (ItemFichaFinanceiraFP itemFichaFinanceiraFP : ff.getItemFichaFinanceiraFP()) {
            if (itemFichaFinanceiraFP.getEventoFP() == null) {
                return false;
            }
        }
        return true;
    }

    private String montarItemFichaFinanceira(ItemFichaFinanceiraFP item) {
        return StringUtil.cortaOuCompletaEsquerda(item.getEventoFP().getCodigo(), 4, "0") + " " +
            StringUtil.cortaOuCompletaDireita(StringUtil.removeCaracteresEspeciaisSemEspaco(item.getEventoFP().getDescricao()), 28, " ") + " "
            + calcularTipoEventoFP(item) + " " + StringUtil.cortaOuCompletaEsquerda(String.valueOf(item.getValor().setScale(2)).replace(".", ","), 12, " ");
    }

    private HoleriteBBDetalhes montarHoleriteBBDetalhes(VinculoFP contratoFP, Integer sequencial, String texto) {
        HoleriteBBDetalhes detalhe = new HoleriteBBDetalhes();
        detalhe.setNumeroIdentificadorDestinatario(contratoFP.getMatriculaFP().getMatricula() + contratoFP.getNumero());
        detalhe.setNumeroSequencialLinha(String.valueOf(sequencial));
        detalhe.setTipoRegistro("2");
        detalhe.setTextoDocumento(texto);
        detalhe.setIndicadorSaltoLinha("0");
        detalhe.setEspacosBrancos("");
        detalhe.montaDetalhes(detalhe);
        return detalhe;
    }

    private String gerarDestinatario(HoleriteBBAuxiliar holeriteBBAuxiliar, int numeroDeLinhas) {
        for (FichaFinanceiraFP fichaFinanceiraFP : holeriteBBAuxiliar.getFichaFinanceiraFPs()) {
            if (isEventoFP(fichaFinanceiraFP)) {
                HoleriteBBDestinatario destinatario = new HoleriteBBDestinatario();
                destinatario.setNumeroIdentificadorDestinatario(holeriteBBAuxiliar.getVinculoFP().getMatriculaFP().getMatricula() + holeriteBBAuxiliar.getVinculoFP().getNumero());
                destinatario.setNumeroSequencialLinha("");
                destinatario.setTipoRegistro("1");
                if (holeriteBBAuxiliar.getVinculoFP().getContaCorrente() != null && holeriteBBAuxiliar.getVinculoFP().getContaCorrente().getAgencia() != null) {
                    destinatario.setNumeroAgencia(holeriteBBAuxiliar.getVinculoFP().getContaCorrente().getAgencia().getNumeroAgencia());
                    destinatario.setNumeroConta(holeriteBBAuxiliar.getVinculoFP().getContaCorrente().getNumeroConta().substring(1));
                } else {
                    destinatario.setNumeroAgencia("");
                    destinatario.setNumeroConta("");
                }
                destinatario.setQuantidadeLinhas(String.valueOf(numeroDeLinhas));
                destinatario.setNome(StringUtil.removeCaracteresEspeciaisSemEspaco(holeriteBBAuxiliar.getVinculoFP().getMatriculaFP().getPessoa().getNome()));

                String cpf = "";
                if (holeriteBBAuxiliar.getVinculoFP().getMatriculaFP().getPessoa().getCpf() != null) {
                    cpf = holeriteBBAuxiliar.getVinculoFP().getMatriculaFP().getPessoa().getCpf();

                }
                destinatario.setCpf(StringUtil.removeCaracteresEspeciaisSemEspaco(cpf));

                destinatario.setEspacosBrancos("");
                destinatario.montaDestinatario(destinatario);

                return destinatario.getDestinatario();
            }
        }
        return "";
    }

    private String gerarHeader(HoleriteBB selecionado, Date dataReferencia, ContaBancariaEntidade contaBancariaEntidade) throws ExcecaoNegocioGenerica {
        try {
            addMensagem("<b> <font color='black'>...Montando Holerite...</font> </b>", selecionado);
            ConfiguracaoHolerite configuracaoHolerite = (ConfiguracaoHolerite) configuracaoRHFacade.buscarConfiguracaoHoleriteVigente(dataReferencia);
            HoleriteBBHeader header = new HoleriteBBHeader();
            header.setNumeroIdentificadorDestinatario("");
            header.setNumeroSequencialLinha("");
            header.setTipoRegistro("");
            header.setNumeroAgencia(contaBancariaEntidade.getAgencia().getNumeroAgencia());
            header.setNumeroConta(contaBancariaEntidade.getNumeroConta());
            header.setNomeArquivo("EDO001");
            header.setNumeroDoContrato(configuracaoHolerite.getNumeroContrato());
            header.setNumeroRemessa(StringUtil.cortaOuCompletaEsquerda(String.valueOf(selecionado.getNumeroRemessa()), 2, "0"));
            header.setCodigoProduto(configuracaoHolerite.getCodigoProduto());
            header.setCodigoModalidade(configuracaoHolerite.getCodigoModalidade());
            header.setAnoFolhaPagamento(selecionado.getAno());
            header.setMesFolhaPagamento(selecionado.getMes());
            header.setRemessaExtra(StringUtil.cortaOuCompletaEsquerda(String.valueOf(selecionado.getVersao()), 2, "0"));
            header.setDataReferencia(StringUtil.removeCaracteresEspeciaisSemEspaco(DataUtil.getDataFormatada(selecionado.getDataGeracao())));
            header.setEspacosBrancos(" ");
            header.montaHeader(header);

            return header.getHeader();
        } catch (ExcecaoNegocioGenerica eng) {
            throw new ExcecaoNegocioGenerica(eng.getMessage());
        }
    }

    private String calcularTipoEventoFP(ItemFichaFinanceiraFP item) {
        if (item.getEventoFP().getTipoEventoFP().equals(TipoEventoFP.VANTAGEM)) {
            return "C";
        }
        if (item.getEventoFP().getTipoEventoFP().equals(TipoEventoFP.DESCONTO)) {
            return "D";
        }
        if (item.getEventoFP().getTipoEventoFP().equals(TipoEventoFP.INFORMATIVO)) {
            return "N";
        }
        return "";
    }

    private List<HoleriteBBAuxiliar> buscarContratoFp(HoleriteBB selecionado, HierarquiaOrganizacional hierarquiaOrganizacional) {
        addMensagem("<b> <font color='black'>...Recuperando Contratos...</font> </b>", selecionado);
        return vinculoFPFacade.buscarVinculosVigentesComFichaFinanceiraFP(Integer.parseInt(selecionado.getAno()),
            Integer.parseInt(selecionado.getMes()),
            selecionado.getTipoFolhaDePagamento(),
            hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getSubordinada() : null);
    }

    public Long recuperarVersao(String ano, String mes) {
        Query q = this.em.createQuery("from HoleriteBB h " +
            " where h.ano = :ano " +
            " and h.mes = :mes " +
            " and h.dataGeracao = (select max(ho.dataGeracao) from HoleriteBB ho where ho.id = h.id)");
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setMaxResults(1);
        List<HoleriteBB> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0).getVersao() + 1L;
        }
        return 1L;
    }

    public Long recuperarNumeroRemessa() {
        Long num;
        String hql = " select max(cast(h.numeroRemessa as integer)) from HoleriteBB h";
        Query query = em.createQuery(hql);
        query.setMaxResults(1);
        if (query.getResultList().get(0) != null) {
            num = Long.valueOf((Integer) query.getResultList().get(0)) + 1l;
        } else {
            return 1L;
        }
        return num;
    }

    public AposentadoriaFacade getAposentadoriaFacade() {
        return aposentadoriaFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public FichaFinanceiraFPFacade getFichaFinanceiraFPFacade() {
        return fichaFinanceiraFPFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public LotacaoFuncionalFacade getLotacaoFuncionalFacade() {
        return lotacaoFuncionalFacade;
    }

    public VinculoFPFacade getVinculoFPFacade() {
        return vinculoFPFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
