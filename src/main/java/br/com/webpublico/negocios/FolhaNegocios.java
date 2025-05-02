/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

//import br.com.webpublico.entidades.ContratoServidor;
//import br.com.webpublico.entidades.ContratoServidorRecursoFP;
//import br.com.webpublico.entidades.EventoFichaFinanceiraFP;

/**
 * @author Munif
 */
@Stateless
public class FolhaNegocios {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
//    public ContratoServidorRecursoFP obtemContratoServidorRecursoFP(FolhaDePagamento folhaDePagamento, ContratoServidor contratoServidor) {
//        Query consultaContratoServidorRecurso = em.createQuery("from ContratoServidorRecursoFP contrato where  :dataFolha>=contrato.inicioVigencia and :dataFolha<=finalVigencia and contrato.contratoServidor=:contratoServidor");
//        consultaContratoServidorRecurso.setParameter("contratoServidor", contratoServidor);
//        Calendar ultimoDiaDoMesDaFolha = Calendar.getInstance();
//        ultimoDiaDoMesDaFolha.set(Calendar.YEAR, folhaDePagamento.getAno());
//        ultimoDiaDoMesDaFolha.set(Calendar.MONTH, folhaDePagamento.getMes()); //Lembrar que no Calendar, o mes de Janeiro é 0
//        ultimoDiaDoMesDaFolha.add(Calendar.DAY_OF_MONTH, -1);
//        consultaContratoServidorRecurso.setParameter("dataFolha", ultimoDiaDoMesDaFolha.getTime());
//        ContratoServidorRecursoFP contratoServidorRecursoFP = (ContratoServidorRecursoFP) consultaContratoServidorRecurso.getSingleResult();
//        return contratoServidorRecursoFP;
//    }
//
//    public void geraEventosFinanceiros(FolhaDePagamento folhaDePagamento) {
//        Query q = em.createQuery("from ContratoServidor contrato where contrato.unidadeOrganizacional=:uo");
//        q.setParameter("uo", folhaDePagamento.getUnidadeOrganizacional());
//        List<ContratoServidor> contratos = q.getResultList();
//
//        for (ContratoServidor contrato : contratos) {
//
//            FichaFinanceiraFP fichaFinanceiraFP = new FichaFinanceiraFP();
//            fichaFinanceiraFP.setContratoServidor(contrato);
//            fichaFinanceiraFP.setFolhaDePagamento(folhaDePagamento);
//            fichaFinanceiraFP.setRecursoFP(obtemContratoServidorRecursoFP(folhaDePagamento, contrato).getRecursoFP());
//            em.persist(fichaFinanceiraFP);
//
//
//            EventoFP evento = contrato.getModalidadeContratoServidor().getVantagemVencimentoBase();
//
//            //PROCESSAR OUTROS EVENTOS
//            EventoFichaFinanceiraFP eventoFichaFinanceiraFP = new EventoFichaFinanceiraFP();
//            eventoFichaFinanceiraFP.setEventoFP(evento);
//            eventoFichaFinanceiraFP.setFichaFinanceiraFP(fichaFinanceiraFP);
//            eventoFichaFinanceiraFP.setValor(contrato.getVencimentoBase()); //VAI MUDAR PARA EVENTO
//
//            em.persist(eventoFichaFinanceiraFP);
//        }
//    }
//
//    public void empenhaFolhaDePagamanto(FolhaDePagamento folhaDePagamento) {
//        switch (folhaDePagamento.getTipoEmpenhoFP()) {
//            case POR_CONTRATO:
//                empenhaFolhaDePagamentoPorContrato(folhaDePagamento);
//                break;
//            case POR_FONTE:
//                empenhaFolhaDePagamentoPorFonte(folhaDePagamento);
//                break;
//            case POR_PESSOA:
//                empenhaFolhaDePagamentoPorPessoa(folhaDePagamento);
//                break;
//        }
//    }
//
//    /**
//     * Neste caso cada evento financeiro gera um empenho
//     * @param folhaDePagamento
//     */
//    private void empenhaFolhaDePagamentoPorContrato(FolhaDePagamento folhaDePagamento) {
//        Query consultaEventosFichaFinanceira = em.createQuery("from EventoFichaFinanceiraFP evento where evento.fichaFinanceiraFP.folhaDePagamento=:folha");
//        consultaEventosFichaFinanceira.setParameter("folha", folhaDePagamento);
//        List<EventoFichaFinanceiraFP> eventos = consultaEventosFichaFinanceira.getResultList();
//        for (EventoFichaFinanceiraFP eventoFichaFinanceiraFP : eventos) {
//            Empenho empenho = new Empenho();
//            EmpenhoFichaFinanceiraFP empenhoFichaFinanceiraFP = new EmpenhoFichaFinanceiraFP();
//            empenhoFichaFinanceiraFP.setEmpenho(empenho);
//            empenhoFichaFinanceiraFP.setEventoFichaFinanceiraFP(eventoFichaFinanceiraFP);
//
//            RecursoFP recursoFP = eventoFichaFinanceiraFP.getFichaFinanceiraFP().getRecursoFP();
//
//            empenho.setContaDeDespesa(recursoFP.getContaDeDespesa());
//            empenho.setContaDestinacaoRecursos(recursoFP.getDestinacaoDeRecursos());
//            empenho.setDocumento("FOLHA " + folhaDePagamento.getMes() + "/" + folhaDePagamento.getAno());
//            empenho.setProvisaoPPADespesa(recursoFP.getProvisaoPPAFonte().getProvisaoPPADespesa());
//            empenho.setPessoa(eventoFichaFinanceiraFP.getFichaFinanceiraFP().getContratoServidor().getServidor());
//            empenho.setRealizadoEm(new Date());
//            empenho.setUnidadeOrganizacional(folhaDePagamento.getUnidadeOrganizacional());
//            empenho.setValor(eventoFichaFinanceiraFP.getValor());
//
//            em.persist(empenhoFichaFinanceiraFP);
//            em.persist(empenho);
//        }
//
//    }
//
//    private void empenhaFolhaDePagamentoPorFonte(FolhaDePagamento folhaDePagamento) {
//
//        Map<ProvisaoPPAFonte, List<EventoFichaFinanceiraFP>> map = new HashMap<ProvisaoPPAFonte, List<EventoFichaFinanceiraFP>>();
//        Query consultaEventosFichaFinanceira = em.createQuery("from EventoFichaFinanceiraFP evento where evento.fichaFinanceiraFP.folhaDePagamento=:folha");
//        consultaEventosFichaFinanceira.setParameter("folha", folhaDePagamento);
//        List<EventoFichaFinanceiraFP> eventos = consultaEventosFichaFinanceira.getResultList();
//        for (EventoFichaFinanceiraFP eventoFichaFinanceiraFP : eventos) {
//            RecursoFP recursoFP = eventoFichaFinanceiraFP.getFichaFinanceiraFP().getRecursoFP();
//            if (map.containsKey(recursoFP.getProvisaoPPAFonte())) {
//                List<EventoFichaFinanceiraFP> lista = map.get(recursoFP.getProvisaoPPAFonte());
//                lista.add(eventoFichaFinanceiraFP);
//            } else {
//                List<EventoFichaFinanceiraFP> lista = new ArrayList<EventoFichaFinanceiraFP>();
//                lista.add(eventoFichaFinanceiraFP);
//                map.put(recursoFP.getProvisaoPPAFonte(), lista);
//            }
//        }
//
//        for (ProvisaoPPAFonte provisaoPPAFonte : map.keySet()) {
//            Empenho empenho = new Empenho();
//            empenho.setDocumento("ProvisaoPPAFonte " + provisaoPPAFonte + " FOLHA " + folhaDePagamento.getMes() + "/" + folhaDePagamento.getAno());
//            empenho.setContaDeDespesa(provisaoPPAFonte.getProvisaoPPADespesa().getContaDeDespesa());
//            empenho.setContaDestinacaoRecursos(provisaoPPAFonte.getDestinacaoDeRecursos());
//            empenho.setProvisaoPPADespesa(provisaoPPAFonte.getProvisaoPPADespesa());
//            empenho.setRealizadoEm(new Date());
//            empenho.setUnidadeOrganizacional(folhaDePagamento.getUnidadeOrganizacional());
//
//            List<EventoFichaFinanceiraFP> lista = map.get(provisaoPPAFonte);
//            BigDecimal valorTotal = BigDecimal.ZERO;
//            for (EventoFichaFinanceiraFP eventoFichaFinanceiraFP : lista) {
//                EmpenhoFichaFinanceiraFP empenhoFichaFinanceiraFP = new EmpenhoFichaFinanceiraFP();
//                empenhoFichaFinanceiraFP.setEmpenho(empenho);
//                empenhoFichaFinanceiraFP.setEventoFichaFinanceiraFP(eventoFichaFinanceiraFP);
//                valorTotal = valorTotal.add(eventoFichaFinanceiraFP.getValor());
//                em.persist(empenhoFichaFinanceiraFP);
//            }
//            empenho.setValor(valorTotal);
//            em.persist(empenho);
//        }
//    }
//
//    private void empenhaFolhaDePagamentoPorPessoa(FolhaDePagamento folhaDePagamento) {
//        Map<PessoaFisica, List<EventoFichaFinanceiraFP>> map = new HashMap<PessoaFisica, List<EventoFichaFinanceiraFP>>();
//        Query consultaEventosFichaFinanceira = em.createQuery("from EventoFichaFinanceiraFP evento where evento.fichaFinanceiraFP.folhaDePagamento=:folha");
//        consultaEventosFichaFinanceira.setParameter("folha", folhaDePagamento);
//        List<EventoFichaFinanceiraFP> eventos = consultaEventosFichaFinanceira.getResultList();
//        for (EventoFichaFinanceiraFP eventoFichaFinanceiraFP : eventos) {
//            PessoaFisica servidor = eventoFichaFinanceiraFP.getFichaFinanceiraFP().getContratoServidor().getServidor();
//            if (map.containsKey(servidor)) {
//                List<EventoFichaFinanceiraFP> lista = map.get(servidor);
//                lista.add(eventoFichaFinanceiraFP);
//            } else {
//                List<EventoFichaFinanceiraFP> lista = new ArrayList<EventoFichaFinanceiraFP>();
//                lista.add(eventoFichaFinanceiraFP);
//                map.put(servidor, lista);
//            }
//        }
//        /*
//        for (PessoaFisica servidor : map.keySet()) {
//        Empenho empenho = new Empenho();
//        empenho.setDocumento("Pessoa Física " + servidor + " FOLHA " + folhaDePagamento.getMes() + "/" + folhaDePagamento.getAno());
//        empenho.setContaDeDespesa(provisaoPPAFonte.getProvisaoPPADespesa().getContaDeDespesa());
//        empenho.setContaDestinacaoRecursos(provisaoPPAFonte.getDestinacaoDeRecursos());
//        empenho.setProvisaoPPADespesa(provisaoPPAFonte.getProvisaoPPADespesa());
//        empenho.setRealizadoEm(new Date());
//        empenho.setUnidadeOrganizacional(folhaDePagamento.getUnidadeOrganizacional());
//
//        List<EventoFichaFinanceiraFP> lista = map.get(provisaoPPAFonte);
//        BigDecimal valorTotal = BigDecimal.ZERO;
//        for (EventoFichaFinanceiraFP eventoFichaFinanceiraFP : lista) {
//        EmpenhoFichaFinanceiraFP empenhoFichaFinanceiraFP = new EmpenhoFichaFinanceiraFP();
//        empenhoFichaFinanceiraFP.setEmpenho(empenho);
//        empenhoFichaFinanceiraFP.setEventoFichaFinanceiraFP(eventoFichaFinanceiraFP);
//        valorTotal = valorTotal.add(eventoFichaFinanceiraFP.getValor());
//        em.persist(empenhoFichaFinanceiraFP);
//        }
//        empenho.setValor(valorTotal);
//        em.persist(empenho);
//        }
//         *
//         */
//
//
//    }
//
//
//    public void liquidarEmpenho(Empenho em){
//
//        LiquidacaoEmpenho liquidacaoEmpenho=null;  //Para cada uma do empenho
//
//        geraFatoContabil(liquidacaoEmpenho);
//
//
//
//    }
//
//    public void geraFatoContabil(LiquidacaoEmpenho liquidacaoEmpenho) {
//
//        FatoContabil fatoContabil;
//
//
//    }
//
//    public void geraFatoContabil(Empenho empenho) {
//        FatoContabil fatoContabil=new FatoContabil();
//
//        fatoContabil.setGeradoEm(new Date());
//        fatoContabil.setTipoFatoContabil(TipoFatoContabil.NORMAL);
//        fatoContabil.setUnidadeOrganizacional(empenho.getUnidadeOrganizacional());
//
//
//        ProvisaoPPADespesa provisaoPPADespesa=empenho.getProvisaoPPADespesa();
//
//        Query q=em.createQuery("from Pro");
//
//
//           fatoContabil.setClp(null);
//
//
//        //fatoContabil.setClp(empenho.);
//
//
//
//
//
//
//    }
//
//
//
}
