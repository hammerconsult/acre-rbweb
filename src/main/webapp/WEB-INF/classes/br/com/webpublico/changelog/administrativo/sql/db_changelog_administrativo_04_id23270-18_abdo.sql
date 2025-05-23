select * from bemnotafiscal;
insert into bemnotafiscal (id, bem_id, numeronotafiscal, datanotafiscal, doctofiscalliquidacao_id)
select hibernate_sequence.nextVal, dados.* from(
select distinct ev.bem_id, ebn.notafiscal, ebn.datanotafiscal, ebn.doctofiscalliquidacao_id
   from eventobem ev
  inner join estadobem eb on eb.id = ev.estadoresultante_id
  inner join estadobemnota ebn on eb.id = ebn.estadobem_id) dados;

insert into bemnotafiscalempenho (id, bemnotafiscal_id, numeroempenho, dataempenho, empenho_id)
select hibernate_sequence.nextVal,
       (select bnf.id
          from bemnotafiscal bnf
        where bnf.bem_id = dados.bem_id
          and bnf.numeronotafiscal = dados.notafiscal
          and bnf.datanotafiscal = dados.datanotafiscal
          and bnf.doctofiscalliquidacao_id = dados.doctofiscalliquidacao_id),
        dados.notaempenho, dados.datanotaempenho, dados.empenho_id
   from(select distinct ev.bem_id, ebn.notafiscal, ebn.datanotafiscal, ebn.doctofiscalliquidacao_id,
               ebne.notaempenho, ebne.datanotaempenho, ebne.empenho_id
           from eventobem ev
          inner join estadobem eb on eb.id = ev.estadoresultante_id
          inner join estadobemnota ebn on eb.id = ebn.estadobem_id
          inner join estadobemnotaempenho ebne on ebne.estadobemnota_id = ebn.id) dados;

