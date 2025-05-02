update CONTATRIBUTORECEITA conta set conta.INICIOVIGENCIA =
(select to_date('01/01/' || ex.ano, 'dd/MM/yyyy') from CONTATRIBUTORECEITA ctc
inner join EnquadramentoTributoExerc enq on enq.id = ctc.ENQUADRAMENTO_ID
inner join Exercicio ex on ex.id = enq.EXERCICIOVIGENTE_ID
where conta.id = ctc.id)
