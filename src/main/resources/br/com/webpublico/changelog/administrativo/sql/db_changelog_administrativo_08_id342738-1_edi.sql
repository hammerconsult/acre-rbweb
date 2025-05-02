update contrato cont
set cont.SITUACAOCONTRATO = 'RESCINDIDO'
where id in (select c.id
             from contrato c
                      inner join rescisaocontrato rc on rc.CONTRATO_ID = c.id
             where c.id = cont.id);


update ITEMSOLICITACAOEXTERNO isol set isol.QUANTIDADE = 1, isol.VALORUNITARIO = isol.VALORTOTAL
where TIPOCONTROLE = 'VALOR';
