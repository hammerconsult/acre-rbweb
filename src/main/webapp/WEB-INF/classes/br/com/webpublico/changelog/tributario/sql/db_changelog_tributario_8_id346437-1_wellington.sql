create table corrige_debito_af (
                                   id_valordivida number(19,0),
                                   ano number(19,0),
                                   numero number(19,0)
);


insert into corrige_debito_af (id_valordivida, ano, numero)
select vd.id as id_valordivida,
       ai.ano,
       ai.numero
from calculofiscalizacao cf
         inner join autoinfracaofiscal ai on ai.id = cf.autoinfracaofiscal_id
         inner join exercicio eai on eai.ano = ai.ano
         inner join valordivida vd on vd.calculo_id = cf.id
         inner join exercicio ex on ex.id = vd.exercicio_id
where ex.ano <> ai.ano;

insert into corrige_debito_af (id_valordivida, ano, numero)
select vd.id as id_valordivida,
       ai.ano,
       ai.numero
from calculomultafiscalizacao cf
         inner join autoinfracaofiscal ai on ai.id = cf.autoinfracaofiscal_id
         inner join exercicio eai on eai.ano = ai.ano
         inner join valordivida vd on vd.calculo_id = cf.id
         inner join exercicio ex on ex.id = vd.exercicio_id
where ex.ano <> ai.ano;
