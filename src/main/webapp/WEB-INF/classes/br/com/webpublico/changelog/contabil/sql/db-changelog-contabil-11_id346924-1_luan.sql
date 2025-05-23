insert into SubContaFonteRec select hibernate_sequence.nextval, fonteUm.id as idFonte, sub.id as idSubConta, cdd.id as idContaDestinacao
from subconta sub
         inner join SubContaFonteRec subfr on sub.ID = subfr.SUBCONTA_ID
         inner join fontederecursos fonte on fonte.id = subfr.fontederecursos_id and substr(fonte.CODIGO, 0, 1) = '2'
         inner join fontederecursos fonteUm on fonteUm.CODIGO = '1' || substr(fonte.CODIGO, 2, 4) and fonteUm.EXERCICIO_ID = (select id from exercicio where ano = 2025)
         inner join CONTADEDESTINACAO cdd on cdd.FONTEDERECURSOS_ID = fonteUm.id
where fonte.EXERCICIO_ID = (select id from exercicio where ano = 2025)
  and sub.id || '1' || substr(fonte.CODIGO, 2, 4) not in (
    select distinct sub.id || fonte.CODIGO as chave
    from subconta sub
             inner join SubContaFonteRec subfr on sub.ID = subfr.SUBCONTA_ID
             left join fontederecursos fonte on fonte.id = subfr.fontederecursos_id
    where fonte.EXERCICIO_ID = (select id from exercicio where ano = 2025) and substr(fonte.CODIGO, 0, 1) = '1'
);
