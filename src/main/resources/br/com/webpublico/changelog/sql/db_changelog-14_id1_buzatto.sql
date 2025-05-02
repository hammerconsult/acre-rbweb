update eventocontabil set tipoeventocontabil = 'INSCRICAO_RESTO_PAGAR' where codigo like '53%' or codigo like '54%';

update eventocontabil set tipoeventocontabil = 'PAGAMENTO_RESTO_PAGAR' where codigo like '63%'
    or codigo like '64%'
    or codigo like '65%'
    or codigo like '66%'
    or codigo like '67%';