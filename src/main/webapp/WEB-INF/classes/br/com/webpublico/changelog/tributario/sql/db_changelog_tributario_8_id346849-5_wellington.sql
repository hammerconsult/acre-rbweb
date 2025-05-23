insert into parametrocobrancaspc (id, debitoexercicio, debitodividaativa,
                                  debitodividaativaprotestada, debitodividaativaajuizada,
                                  pessoafisica, pessoajuridica)
select hibernate_sequence.nextval, 1, 1, 1, 1, 1, 1
from dual
where not exists (select 1 from parametrocobrancaspc)
