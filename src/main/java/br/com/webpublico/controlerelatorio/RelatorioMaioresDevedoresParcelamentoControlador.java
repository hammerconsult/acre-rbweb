///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package br.com.webpublico.controlerelatorio;
//
//import br.com.webpublico.controle.SistemaControlador;
//import br.com.webpublico.entidades.Divida;
//import br.com.webpublico.entidadesauxiliares.AssistenteRelatorioMaioresDevedores;
//import br.com.webpublico.entidadesauxiliares.FiltroMaioresDevedores;
//import br.com.webpublico.entidadesauxiliares.MaioresDevedores;
//import br.com.webpublico.enums.Ordenacao;
//import br.com.webpublico.enums.TipoPessoa;
//import br.com.webpublico.negocios.RelatorioMaioresDevedoresFacade;
//import br.com.webpublico.negocios.tributario.singletons.SingletonRelatorioMaioresDevedores;
//import br.com.webpublico.util.FacesUtil;
//import br.com.webpublico.util.Util;
//import com.google.common.collect.Lists;
//import net.sf.jasperreports.engine.JRException;
//
//import javax.faces.model.SelectItem;
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//
////@ViewScoped
////@ManagedBean(name = "relatorioMaioresDevedoresParcelamentoControlador")
////@URLMappings(mappings = {
////        @URLMapping(id = "novoRelatorioMaioresDevedoresParcelamento",
////                pattern = "/tributario/arrecadacao/relatorios/maiores-devedores-de-parcelamento/",
////                viewId = "/faces/tributario/arrecadacao/relatorios/maioresdevedoresparcelamento.xhtml")
////})
//
//public class RelatorioMaioresDevedoresParcelamentoControlador extends AbstractReport implements Serializable {
//
//    //@EJB
//    private RelatorioMaioresDevedoresFacade relatorioMaioresDevedoresFacade;
//    //@EJB
//    private SingletonRelatorioMaioresDevedores singletonRelatorioMaioresDevedores;
//    private FiltroMaioresDevedores filtro;
//    private Divida divida;
//    private Future<AssistenteRelatorioMaioresDevedores> futureDadosRelatorio1;
//    private Future<AssistenteRelatorioMaioresDevedores> futureDadosRelatorio2;
//    private Future<AssistenteRelatorioMaioresDevedores> futureDadosRelatorio3;
//    private Future<AssistenteRelatorioMaioresDevedores> futureDadosRelatorio4;
//    private Future<AssistenteRelatorioMaioresDevedores> futureDadosRelatorio5;
//    private Future<AssistenteRelatorioMaioresDevedores> futureLista;
//    private boolean futureDadosRelatorioConcluida;
//    private List<MaioresDevedores> maioresDevedoresSingleton;
//    private RelatorioMaioresDevedoresControlador.Assistente assistente;
//
//    public SistemaControlador getSistemaControlador() {
//        return ((SistemaControlador) Util.getControladorPeloNome("sistemaControlador"));
//    }
//
//    public RelatorioMaioresDevedoresParcelamentoControlador() {
//        geraNoDialog = Boolean.TRUE;
//    }
//
//    public FiltroMaioresDevedores getFiltro() {
//        return filtro;
//    }
//
//    public void setFiltro(FiltroMaioresDevedores filtro) {
//        this.filtro = filtro;
//    }
//
//    public List<Divida> getDividasSelecionadas() {
//        return dividasSelecionadas;
//    }
//
//    public void setDividasSelecionadas(List<Divida> dividasSelecionadas) {
//        this.dividasSelecionadas = dividasSelecionadas;
//    }
//
//    public Divida getDivida() {
//        return divida;
//    }
//
//    public void setDivida(Divida divida) {
//        this.divida = divida;
//    }
//
//    public void consultaAndamentoQuery() {
//        if (maioresDevedoresSingleton.isEmpty()) {
//            if (futureLista != null && futureLista.isDone()) {
//                System.out.println("Terminou o Consulta...");
//                iniciaAtualizacaoValor();
//                FacesUtil.executaJavaScript("acompanhaAtualizacao()");
//            }
//        }
//    }
//
//    public void consultaAndamentoEmissaoRelatorio() {
//        if (isFutureDadosRelatorioConcluida()) {
//            System.out.println("Terminou o Relatorio...");
//            FacesUtil.executaJavaScript("terminaRelatorio()");
//        }
//    }
//
//    public boolean isFutureDadosRelatorioConcluida() {
//        if (maioresDevedoresSingleton.isEmpty()) {
//            if (futureDadosRelatorio1 == null || futureDadosRelatorio2 == null || futureDadosRelatorio3 == null || futureDadosRelatorio4 == null || futureDadosRelatorio5 == null) {
//                return false;
//            }
//
//            return futureDadosRelatorio1.isDone() && futureDadosRelatorio2.isDone() && futureDadosRelatorio3.isDone() && futureDadosRelatorio4.isDone() && futureDadosRelatorio5.isDone();
//        } else {
//            return true;
//        }
//    }
//
//    public void setFutureDadosRelatorioConcluida(boolean futureDadosRelatorioConcluida) {
//        this.futureDadosRelatorioConcluida = futureDadosRelatorioConcluida;
//    }
//
//    public List<SelectItem> getOrdens() {
//        List<SelectItem> retorno = new ArrayList<>();
//        for (Ordenacao ordem : Ordenacao.values()) {
//            retorno.add(new SelectItem(ordem, ordem.getDescricao()));
//        }
//        return retorno;
//    }
//
//    public List<SelectItem> getTiposPessoa() {
//        List<SelectItem> retorno = new ArrayList<>();
//        retorno.add(new SelectItem(null, "Ambos"));
//        for (TipoPessoa tipo : TipoPessoa.values()) {
//            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
//        }
//        return retorno;
//    }
//
//    public void removeDivida(Divida divida) {
//        if (dividasSelecionadas.contains(divida)) {
//            dividasSelecionadas.remove(divida);
//        }
//    }
//
//    public List<SelectItem> getDividas() {
//        List<SelectItem> toReturn = new ArrayList<>();
//        toReturn.add(new SelectItem(null, " "));
//        List<Divida> dividas = relatorioMaioresDevedoresFacade.getDividaFacade().listaDividasDeParcelamentoOrdenadoPorDescricao();
//        for (Divida t : dividas) {
//            toReturn.add(new SelectItem(t, t.getDescricao()));
//        }
//        return toReturn;
//    }
//
//    public void addDivida() {
//        if (validaDivida()) {
//            dividasSelecionadas.add(divida);
//            divida = new Divida();
//        }
//    }
//
//    private boolean validaDivida() {
//        boolean valida = true;
//        if (divida == null || divida.getId() == null) {
//            FacesUtil.addError("Atenção", "Selecione uma Dívida para adicionar");
//            valida = false;
//        } else if (dividasSelecionadas.contains(divida)) {
//            FacesUtil.addError("Atenção", "Essa Dívida já foi selecionada.");
//            valida = false;
//        }
//        return valida;
//    }
//
//    private boolean validaCampo() {
//        boolean valido = true;
//        if (filtro.getVencimentoInicial() == null) {
//            FacesUtil.addCampoObrigatorio("Informe a data de vencimento inicial!");
//            valido = false;
//        } else {
//            filtro.setVencimentoInicial(Util.getDataHoraMinutoSegundoZerado(filtro.getVencimentoInicial()));
//        }
//        if (filtro.getVencimentoFinal() == null) {
//            FacesUtil.addCampoObrigatorio("Informe a data de vencimento final!");
//            valido = false;
//        } else {
//            filtro.setVencimentoFinal(Util.getDataHoraMinutoSegundoZerado(filtro.getVencimentoFinal()));
//        }
//        if (valido) {
//            if (filtro.getVencimentoInicial().after(filtro.getVencimentoFinal())) {
//                FacesUtil.addError("Atenção!", "A data de vencimento inicial não pode ser maior que a final!");
//                valido = false;
//            }
//        }
//        return valido;
//    }
//
//    public void iniciaConsulta() {
//        trataDividasSelecionadas();
//
//        futureDadosRelatorio1 = null;
//        futureDadosRelatorio2 = null;
//        futureDadosRelatorio3 = null;
//        futureDadosRelatorio4 = null;
//        futureDadosRelatorio5 = null;
//
//        if (!validaCampo()) {
//            FacesUtil.executaJavaScript("aguarde.hide()");
//            return;
//        }
//
//        maioresDevedoresSingleton = singletonRelatorioMaioresDevedores.getRelatorio(filtro);
//        if (maioresDevedoresSingleton.isEmpty()) {
//            futureLista = relatorioMaioresDevedoresFacade.listaMaioresDevedores(filtro);
//            FacesUtil.executaJavaScript("acompanhaConsulta();");
//        }
//    }
//
//    public void iniciaAtualizacaoValor() {
//        boolean executou = false;
//        List<MaioresDevedores> maioresDevedores = Lists.newArrayList();
//        try {
//            maioresDevedores = futureLista.get().getResultado();
//        } catch (InterruptedException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (ExecutionException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//        if (!maioresDevedores.isEmpty()) {
//            executou = true;
//            System.out.println("Buscou os " + maioresDevedores.size() + " devedores");
//
//            List<MaioresDevedores> lista1 = Lists.newArrayList();
//            List<MaioresDevedores> lista2 = Lists.newArrayList();
//            List<MaioresDevedores> lista3 = Lists.newArrayList();
//            List<MaioresDevedores> lista4 = Lists.newArrayList();
//            List<MaioresDevedores> lista5 = Lists.newArrayList();
//
//            int qtdePorThread = filtro.getQtdeDevedores() / 5;
//
//            if (maioresDevedores.size() >= qtdePorThread) {
//                for (int i = 0; i < qtdePorThread; i++) {
//                    lista1.add(maioresDevedores.get(i));
//                }
//                System.out.println("Lista 1 --> " + lista1.size());
//            }
//
//            if (maioresDevedores.size() >= qtdePorThread * 2) {
//                for (int i = qtdePorThread; i < qtdePorThread * 2; i++) {
//                    lista2.add(maioresDevedores.get(i));
//                }
//                System.out.println("Lista 2 --> " + lista2.size());
//            }
//
//            if (maioresDevedores.size() >= qtdePorThread * 3) {
//                for (int i = qtdePorThread * 2; i < qtdePorThread * 3; i++) {
//                    lista3.add(maioresDevedores.get(i));
//                }
//                System.out.println("Lista 3 --> " + lista3.size());
//            }
//
//            if (maioresDevedores.size() >= qtdePorThread * 4) {
//                for (int i = qtdePorThread * 3; i < qtdePorThread * 4; i++) {
//                    lista4.add(maioresDevedores.get(i));
//                }
//                System.out.println("Lista 4 --> " + lista4.size());
//            }
//
//            for (int i = qtdePorThread * 4; i < maioresDevedores.size(); i++) {
//                lista5.add(maioresDevedores.get(i));
//            }
//            System.out.println("Lista 5 --> " + lista5.size());
//            assistente = new RelatorioMaioresDevedoresControlador.Assistente(maioresDevedores.size());
//            if (!lista1.isEmpty()) {
//                futureDadosRelatorio1 = relatorioMaioresDevedoresFacade.atualizarValoresDevidos(filtro, assistente);
//                executou = true;
//            }
//            if (!lista2.isEmpty()) {
//                futureDadosRelatorio2 = relatorioMaioresDevedoresFacade.atualizarValoresDevidos(filtro, assistente);
//                executou = true;
//            }
//            if (!lista3.isEmpty()) {
//                futureDadosRelatorio3 = relatorioMaioresDevedoresFacade.atualizarValoresDevidos(filtro, assistente);
//                executou = true;
//            }
//            if (!lista4.isEmpty()) {
//                futureDadosRelatorio4 = relatorioMaioresDevedoresFacade.atualizarValoresDevidos(filtro, assistente);
//                executou = true;
//            }
//            if (!lista5.isEmpty()) {
//                futureDadosRelatorio5 = relatorioMaioresDevedoresFacade.atualizarValoresDevidos(filtro, assistente);
//                executou = true;
//            }
//        }
//        if (!executou) {
//            FacesUtil.executaJavaScript("aguarde.hide()");
//            FacesUtil.addError("Atenção!", "Não foram encontrados dados com os filtros informados!");
//        }
//    }
//
//    private void trataDividasSelecionadas() {
//        if (divida != null) {
//            if (!dividasSelecionadas.contains(divida)) {
//                dividasSelecionadas.add(divida);
//            }
//        }
//        for (Divida dv : dividasSelecionadas) {
//            filtro.getListaDividas().add(dv);
//        }
//        divida = null;
//    }
//
//    //@URLAction(mappingId = "novoRelatorioMaioresDevedoresParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
//    public void novo() {
//        filtro = new FiltroMaioresDevedores(getSistemaControlador().getUsuarioCorrente());
//        filtro.setParcelamento(true);
//
//        Calendar inicio = Calendar.getInstance();
//        inicio.set(Calendar.YEAR, 2015);
//        inicio.set(Calendar.MONTH, Calendar.JULY);
//        inicio.set(Calendar.DAY_OF_MONTH, 1);
//        filtro.setVencimentoInicial(inicio.getTime());
//
//        Calendar fim = Calendar.getInstance();
//        fim.set(Calendar.YEAR, 2015);
//        fim.set(Calendar.MONTH, Calendar.JULY);
//        fim.set(Calendar.DAY_OF_MONTH, 31);
//        filtro.setVencimentoFinal(fim.getTime());
//
//        maioresDevedoresSingleton = Lists.newArrayList();
//
//        dividasSelecionadas = Lists.newArrayList();
//        divida = null;
//    }
//
//    public void imprimir() throws ExecutionException, InterruptedException, IOException, JRException {
//        String tituloRelatorio = "Relatório de Maiores Devedores de Parcelamento";
//        if (maioresDevedoresSingleton.isEmpty()) {
//            List<MaioresDevedores> maioresDevedores = Lists.newArrayList();
//            maioresDevedores.addAll(futureDadosRelatorio1.get().getResultado());
//            maioresDevedores.addAll(futureDadosRelatorio2.get().getResultado());
//            maioresDevedores.addAll(futureDadosRelatorio3.get().getResultado());
//            maioresDevedores.addAll(futureDadosRelatorio4.get().getResultado());
//            maioresDevedores.addAll(futureDadosRelatorio5.get().getResultado());
//            relatorioMaioresDevedoresFacade.emiteRelatorio(filtro.getFiltro(), maioresDevedores, tituloRelatorio, filtro.getDetalhado());
//        } else {
//            relatorioMaioresDevedoresFacade.emiteRelatorio(filtro.getFiltro(dividasSelecionadas), maioresDevedoresSingleton, tituloRelatorio, filtro.getDetalhado());
//        }
//    }
//
//    public void finalizouRelatorio() throws ExecutionException, InterruptedException {
//        if (maioresDevedoresSingleton.isEmpty()) {
//            List<MaioresDevedores> maioresDevedores = Lists.newArrayList();
//            maioresDevedores.addAll(futureDadosRelatorio1.get().getResultado());
//            maioresDevedores.addAll(futureDadosRelatorio2.get().getResultado());
//            maioresDevedores.addAll(futureDadosRelatorio3.get().getResultado());
//            maioresDevedores.addAll(futureDadosRelatorio4.get().getResultado());
//            maioresDevedores.addAll(futureDadosRelatorio5.get().getResultado());
//            singletonRelatorioMaioresDevedores.addRelatorio(filtro, maioresDevedores);
//        }
//    }
//
//    public List<FiltroMaioresDevedores> ultimosFiltrosUtilizados() {
//        return singletonRelatorioMaioresDevedores.getFiltros(true);
//    }
//
//    public void atribuiValoresParaFiltro(FiltroMaioresDevedores filtroSelecionado) {
//        filtro.setVencimentoInicial(filtroSelecionado.getVencimentoInicial());
//        filtro.setVencimentoFinal(filtroSelecionado.getVencimentoFinal());
//        filtro.setQtdeDevedores(filtroSelecionado.getQtdeDevedores());
//        filtro.setTipoPessoa(filtroSelecionado.getTipoPessoa());
//        filtro.setOrdenacao(filtroSelecionado.getOrdenacao());
//        filtro.setValoresAtualizados(filtroSelecionado.getValoresAtualizados());
//
//        dividasSelecionadas = Lists.newArrayList();
//        for (Divida div : filtroSelecionado.getListaDividas()) {
//            dividasSelecionadas.add(div);
//        }
//    }
//}
